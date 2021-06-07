package pp.muza;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import pp.muza.cards.CardStack;
import pp.muza.poker.GameStatistic;
import pp.muza.poker.GameStatisticCollector;
import pp.muza.poker.PokerCalc;
import pp.muza.poker.TexasHoldEmGame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class TestRunner {
    public static final int MAX_ITERATIONS = 100000;
    public static final int DEFAULT_PLAYERS = 4;
    public static final int DEFAULT_THREADS = 2;
    static final String OPT_HAND = "hand";
    static final String OPT_RIVER = "river";
    static final String OPT_PLAYERS = "players";
    static final String OPT_THREADS = "threads";
    static final String OPT_ITERATIONS = "iterations";
    static final String OPT_SIMPLE = "simple";
    static final String OPT_DEBUG = "debug";
    static final String OPT_VERBOSE = "verbose";
    static final String OPT_GO = "go";
    static final String OPT_NO_STATS = "no_stats";
    private static final GameStatistic gameStatistic = new GameStatisticCollector();
    private static AtomicLong iterationCounter = new AtomicLong();
    private static int players = DEFAULT_PLAYERS;
    private static int threads = DEFAULT_THREADS;
    private static boolean debug = false;
    private static boolean verbose = false;
    private static boolean noStats = false;
    private static int maxIteration = MAX_ITERATIONS;
    private static CardStack handSetup;
    private static CardStack riverSetup;
    private static StatisticFormatter statisticFormatter;

    private TestRunner() {

    }

    public static AtomicLong getIterationCounter() {
        return iterationCounter;
    }

    public static void main(final String[] args) {

        Options options = new Options();
        options
                .addOption(Option.builder("d").longOpt(OPT_DEBUG).hasArg(false).desc("use debug output").build())
                .addOption(Option.builder("g").longOpt(OPT_GO).required().hasArg(false).desc("run").build())
                .addOption(Option.builder("h").longOpt(OPT_HAND).hasArgs().desc("define hand cards").type(String.class).build())
                .addOption(Option.builder("i").longOpt(OPT_ITERATIONS).hasArg().desc("define number of iterations").type(Integer.class).build())
                .addOption(Option.builder("n").longOpt(OPT_NO_STATS).hasArg(false).desc("don't collect statistics").build())
                .addOption(Option.builder("p").longOpt(OPT_PLAYERS).hasArg().desc("define number of players").type(Integer.class).build())
                .addOption(Option.builder("r").longOpt(OPT_RIVER).hasArgs().desc("define river cards").type(String.class).build())
                .addOption(Option.builder("s").longOpt(OPT_SIMPLE).hasArg(false).desc("use simple formatter").build())
                .addOption(Option.builder("t").longOpt(OPT_THREADS).hasArg().desc("define number of CPU threads").type(Integer.class).build())
                .addOption(Option.builder("v").longOpt(OPT_VERBOSE).hasArg(false).desc("use verbose output").build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(OPT_DEBUG)) {
                debug = true;
            }
            if (cmd.hasOption(OPT_HAND)) {
                handSetup = CardStack.parseCards(String.join(" ", cmd.getOptionValues("hand")));
            }
            if (cmd.hasOption(OPT_ITERATIONS)) {
                maxIteration = Integer.parseInt(cmd.getOptionValue("iterations"));
            }
            if (cmd.hasOption(OPT_NO_STATS)) {
                noStats = true;
            }
            if (cmd.hasOption(OPT_PLAYERS)) {
                players = Integer.parseInt(cmd.getOptionValue("players"));
                if (players <= 0) {
                    throw new IllegalArgumentException("Players value should be greater then 0");
                }
            }
            if (cmd.hasOption(OPT_RIVER)) {
                riverSetup = CardStack.parseCards(String.join(" ", cmd.getOptionValues("river")));
            }
            if (cmd.hasOption(OPT_SIMPLE)) {
                statisticFormatter = new StatisticSimpleFormatter();
            } else {
                statisticFormatter = new StatisticPrettyFormatter();
            }
            if (cmd.hasOption(OPT_THREADS)) {
                threads = Integer.parseInt(cmd.getOptionValue("threads"));
            }
            if (cmd.hasOption(OPT_VERBOSE)) {
                verbose = true;
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            helpFormatter.printHelp("Poker Benchmark", options);
            System.exit(1);
        }
        try {
            run();
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            if (debug) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    public static void run() throws Exception {

        System.out.println(getConfig());
        gameStatistic.initialize(players);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        StdErrorCalc errorCalc = new StdErrorCalc(players);

        int step = maxIteration / threads;
        int tail = maxIteration % threads;

        long startTime = System.currentTimeMillis();
        List<Future<Integer>> future = new ArrayList<>();
        for (int i = 1; i <= threads; i++) {
            future.add(executor.submit(new RunGame(i == 1 ? step + tail : step)));
        }
        executor.shutdown();

        int futureIdx = 0;
        long prevReportsCount = 0;

        while (future.size() > 0) {
            if (futureIdx >= future.size()) {
                futureIdx = 0;
            }
            try {
                prevReportsCount = gameStatistic.getReportedCount();
                future.get(futureIdx).get(1, TimeUnit.SECONDS);
                future.remove(futureIdx);
            } catch (TimeoutException e) {
                if (debug) {
                    calcError(errorCalc, prevReportsCount);
                }
            }
            futureIdx++;
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;

        System.out.println(String.format("%d iterations performed in %d milliseconds", gameStatistic.getReportedCount(), timeElapsed)
                + (debug ? String.format(", ips=%s", (long) (timeElapsed != 0 ? (double) gameStatistic.getReportedCount() / (timeElapsed / 1000.0) : 0))
                + (debug && errorCalc.isReady() ? String.format(", error=%s", errorCalc.getErrorSum()) : "") : ""));
        statisticFormatter.printStatistics(gameStatistic);
    }

    private static void calcError(final StdErrorCalc errorCalc, final long prevReportsCount) {
        final Map<Integer, Long> stat;
        final long reportsCount;
        synchronized (gameStatistic) {
            stat = gameStatistic.getPlayerWinStats();
            reportsCount = gameStatistic.getReportedCount();
        }
        double[] measures = new double[players];
        for (Map.Entry<Integer, Long> ee : stat.entrySet()) {
            measures[ee.getKey()] = reportsCount != 0 ? (double) ee.getValue() / reportsCount : 0;

        }
        double s = errorCalc.report(measures);
        if (errorCalc.isReady()) {
            System.out.println(String.format("running %.0f%%, ips=%s, error=%s", 100.0 * reportsCount / maxIteration, reportsCount - prevReportsCount, s));
        }
    }

    public static String getConfig() {
        return "TestRunner{" +
                "players=" + players +
                ", threads=" + threads +
                ", noStats=" + noStats +
                ", maxIteration=" + maxIteration +
                ", handSetup=" + handSetup +
                ", riverSetup=" + riverSetup +
                '}';
    }

    public static String formatGameResults(List<PokerCalc> calcList) {
        return calcList.stream()
                .filter(pokerCalc -> pokerCalc.getScore() == calcList.stream()
                        .map(PokerCalc::getScore)
                        .max(Integer::compareTo)
                        .orElse(0))
                .sorted(Comparator.comparingInt(PokerCalc::getPlayer))
                .collect(Collectors.toList()).toString();
    }

    public static final class RunGame implements Callable<Integer> {

        private final int iterations;

        RunGame(final int iterations) {
            this.iterations = iterations;
        }

        @Override
        public final Integer call() {
            long i = 0;
            try {
                if (debug) {
                    System.out.println("Starting thread" + Thread.currentThread().getId() + " for " + iterations + " iterations");
                }
                TexasHoldEmGame game = new TexasHoldEmGame(players);
                for (; i < iterations; i++) {
                    game.reset();
                    if (riverSetup != null) {
                        game.setupRiver(riverSetup);
                    }
                    if (handSetup != null) {
                        game.setupPlayers(handSetup);
                    }
                    List<PokerCalc> results = game.run();
                    if (verbose) {
                        String r = formatGameResults(results);
                        System.out.println("ITERATION " + (getIterationCounter().incrementAndGet()) + " " + r);
                    }

                    if (!noStats) {
                        gameStatistic.reportStatistics(results);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (debug) {
                    System.out.println("Thread" + Thread.currentThread().getId() + " finished at " + i);
                }
            }
            return 0;
        }
    }
}
