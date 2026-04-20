package pp.muza;

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

public final class BenchmarkRunner {

    private final GameStatistic gameStatistic = new GameStatisticCollector();
    private final AtomicLong iterationCounter = new AtomicLong();

    public void run(final BenchmarkConfig config) throws Exception {
        System.out.println(config.toString());
        gameStatistic.initialize(config.getPlayers());

        StdErrorCalc errorCalc = new StdErrorCalc(config.getPlayers());
        int step = config.getMaxIteration() / config.getThreads();
        int tail = config.getMaxIteration() % config.getThreads();

        long startTime = System.currentTimeMillis();
        List<Future<Integer>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(config.getThreads());
        for (int i = 1; i <= config.getThreads(); i++) {
            futures.add(executor.submit(new RunGame(config, i == 1 ? step + tail : step)));
        }
        executor.shutdown();

        int futureIdx = 0;
        long prevReportsCount = 0;

        while (!futures.isEmpty()) {
            if (futureIdx >= futures.size()) {
                futureIdx = 0;
            }
            try {
                prevReportsCount = gameStatistic.getReportedCount();
                futures.get(futureIdx).get(1, TimeUnit.SECONDS);
                futures.remove(futureIdx);
            } catch (TimeoutException e) {
                if (config.isDebug()) {
                    calcError(config, errorCalc, prevReportsCount);
                }
            }
            futureIdx++;
        }

        long timeElapsed = System.currentTimeMillis() - startTime;
        long count = gameStatistic.getReportedCount();
        StringBuilder summary = new StringBuilder(
                String.format("%d iterations performed in %d milliseconds", count, timeElapsed));
        if (config.isDebug()) {
            long ips = timeElapsed != 0 ? (long) ((double) count / (timeElapsed / 1000.0)) : 0;
            summary.append(String.format(", ips=%d", ips));
            if (errorCalc.isReady()) {
                summary.append(String.format(", error=%s", errorCalc.getErrorSum()));
            }
        }
        System.out.println(summary);
        config.getStatisticFormatter().printStatistics(gameStatistic);
    }

    private void calcError(final BenchmarkConfig config, final StdErrorCalc errorCalc, final long prevReportsCount) {
        final Map<Integer, Long> stat;
        final long reportsCount;
        synchronized (gameStatistic) {
            stat = gameStatistic.getPlayerWinStats();
            reportsCount = gameStatistic.getReportedCount();
        }
        double[] measures = new double[config.getPlayers()];
        for (Map.Entry<Integer, Long> entry : stat.entrySet()) {
            measures[entry.getKey()] = reportsCount != 0 ? (double) entry.getValue() / reportsCount : 0;
        }
        double s = errorCalc.report(measures);
        if (errorCalc.isReady()) {
            System.out.println(String.format("running %.0f%%, ips=%s, error=%s",
                    100.0 * reportsCount / config.getMaxIteration(), reportsCount - prevReportsCount, s));
        }
    }

    static String formatGameResults(final List<PokerCalc> calcList) {
        int maxScore = calcList.stream().map(PokerCalc::getScore).max(Integer::compareTo).orElse(0);
        return calcList.stream()
                .filter(p -> p.getScore() == maxScore)
                .sorted(Comparator.comparingInt(PokerCalc::getPlayer))
                .collect(Collectors.toList())
                .toString();
    }

    private final class RunGame implements Callable<Integer> {

        private final BenchmarkConfig config;
        private final int iterations;

        RunGame(final BenchmarkConfig config, final int iterations) {
            this.config = config;
            this.iterations = iterations;
        }

        @Override
        public Integer call() {
            long i = 0;
            try {
                if (config.isDebug()) {
                    System.out.println("Starting thread " + Thread.currentThread().getId() + " for " + iterations + " iterations");
                }
                TexasHoldEmGame game = new TexasHoldEmGame(config.getPlayers());
                for (; i < iterations; i++) {
                    game.reset();
                    if (config.getRiverSetup() != null) {
                        game.setupRiver(config.getRiverSetup());
                    }
                    if (config.getHandSetup() != null) {
                        game.setupPlayers(config.getHandSetup());
                    }
                    List<PokerCalc> results = game.run();
                    if (config.isVerbose()) {
                        System.out.println("ITERATION " + iterationCounter.incrementAndGet() + " " + formatGameResults(results));
                    }
                    if (!config.isNoStats()) {
                        gameStatistic.reportStatistics(results);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (config.isDebug()) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " finished at " + i);
                }
            }
            return 0;
        }
    }
}
