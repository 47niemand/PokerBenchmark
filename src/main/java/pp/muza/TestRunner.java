package pp.muza;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import pp.muza.cards.CardStack;

public final class TestRunner {

    public static final int MAX_ITERATIONS = 100000;
    public static final int DEFAULT_PLAYERS = 4;
    public static final int DEFAULT_THREADS = 2;

    static final String OPT_HAND       = "hand";
    static final String OPT_RIVER      = "river";
    static final String OPT_PLAYERS    = "players";
    static final String OPT_THREADS    = "threads";
    static final String OPT_ITERATIONS = "iterations";
    static final String OPT_SIMPLE     = "simple";
    static final String OPT_DEBUG      = "debug";
    static final String OPT_VERBOSE    = "verbose";
    static final String OPT_GO         = "go";
    static final String OPT_NO_STATS   = "no_stats";

    private TestRunner() {}

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

        HelpFormatter helpFormatter = new HelpFormatter();
        BenchmarkConfig config = null;
        try {
            config = parseConfig(new DefaultParser().parse(options, args));
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            helpFormatter.printHelp("Poker Benchmark", options);
            System.exit(1);
        }
        try {
            new BenchmarkRunner().run(config);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            if (config.isDebug()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    static BenchmarkConfig parseConfig(final CommandLine cmd) {
        BenchmarkConfig.Builder builder = BenchmarkConfig.builder();
        if (cmd.hasOption(OPT_DEBUG)) {
            builder.debug(true);
        }
        if (cmd.hasOption(OPT_HAND)) {
            builder.handSetup(CardStack.parseCards(String.join(" ", cmd.getOptionValues(OPT_HAND))));
        }
        if (cmd.hasOption(OPT_ITERATIONS)) {
            builder.maxIteration(Integer.parseInt(cmd.getOptionValue(OPT_ITERATIONS)));
        }
        if (cmd.hasOption(OPT_NO_STATS)) {
            builder.noStats(true);
        }
        if (cmd.hasOption(OPT_PLAYERS)) {
            int players = Integer.parseInt(cmd.getOptionValue(OPT_PLAYERS));
            if (players <= 0) {
                throw new IllegalArgumentException("Players value should be greater than 0");
            }
            builder.players(players);
        }
        if (cmd.hasOption(OPT_RIVER)) {
            builder.riverSetup(CardStack.parseCards(String.join(" ", cmd.getOptionValues(OPT_RIVER))));
        }
        if (cmd.hasOption(OPT_SIMPLE)) {
            builder.statisticFormatter(new StatisticSimpleFormatter());
        }
        if (cmd.hasOption(OPT_THREADS)) {
            builder.threads(Integer.parseInt(cmd.getOptionValue(OPT_THREADS)));
        }
        if (cmd.hasOption(OPT_VERBOSE)) {
            builder.verbose(true);
        }
        return builder.build();
    }
}
