package pp.muza;

import pp.muza.poker.GameStatistic;
import pp.muza.poker.PokerCalc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StatisticSimpleFormatter implements StatisticFormatter {

    static final double MULTIPLER = 1.0d;
    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> {
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(8);
        return df;
    });

    private static String roundOff(final double d) {
        return DECIMAL_FORMAT.get().format(d);
    }

    private static String formatResultStatistic(final Map<PokerCalc.Result, Long> stats, final long n) {
        final long n1 = Math.max(Math.min(1, n), stats.values().stream().reduce(0L, Long::sum));
        return Stream.of(PokerCalc.Result.values())
                .sorted(Comparator.comparingInt(PokerCalc.Result::getScore))
                .filter(result -> result != PokerCalc.Result.UNKNOWN)
                .map(result -> String.format("%s=%s", result.toString(), roundOff(MULTIPLER * stats.get(result) / n1)))
                .collect(Collectors.toList())
                .toString();
    }

    @Override
    public void printStatistics(final GameStatistic gameStats) {
        System.out.println("Reports: " + gameStats.getReportedCount());

        final long n = gameStats.getResultStats().values().stream().reduce(0L, Long::sum);
        System.out.println("River setup: " + gameStats.getRiver().toString());
        System.out.println("Player setup: " + gameStats.getPlayersHand().toString());
        System.out.println("ResultStats: " + formatResultStatistic(gameStats.getResultStats(), n));
        String res = gameStats.getPlayerWinStats().keySet().stream()
                .map(player ->
                        (String.format("Player%d=%s %s\n"
                                , player
                                , roundOff(MULTIPLER * gameStats.getPlayerWinStats().get(player) / n)
                                , formatResultStatistic(gameStats.getPlayerResultWinStats().get(player), n)
                        )))
                .collect(Collectors.joining());
        System.out.println(res);
    }
}
