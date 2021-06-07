package pp.muza;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.table.TableRow;
import pp.muza.cards.Card;
import pp.muza.poker.GameStatistic;
import pp.muza.poker.PokerCalc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StatisticPrettyFormatter implements StatisticFormatter {

    private static final double ONE_HANDRED_PERCENT = 100.0;

    private static void printResultStatistics(final GameStatistic gameStats) {
        Table.Builder tableBuilder = new Table.Builder();

        List<Object> header = Stream.of(PokerCalc.Result.values())
                .sorted(Comparator.comparingInt(PokerCalc.Result::getScore))
                .filter(result -> result != PokerCalc.Result.UNKNOWN)
                .map(result -> "% " + result.name())
                .collect(Collectors.toList());
        tableBuilder.addRow(new TableRow<>(header));
        tableBuilder.withAlignment(Table.ALIGN_RIGHT);

        final long n = gameStats.getResultStats().values().stream().reduce(0L, Long::sum);
        List<Object> row = Stream.of(PokerCalc.Result.values())
                .sorted(Comparator.comparingInt(PokerCalc.Result::getScore))
                .filter(result -> result != PokerCalc.Result.UNKNOWN)
                .map(result -> String.format("%.2f", ONE_HANDRED_PERCENT * gameStats.getResultStats().get(result) / n))
                .collect(Collectors.toList());

        tableBuilder.addRow(new TableRow<>(row));
        System.out.println(tableBuilder.build());
    }

    private static void printPlayerStatistics(GameStatistic gameStats) {
        Table.Builder tableBuilder = new Table.Builder();

        List<Object> header =
                Stream.concat(Stream.of("Player", "Hand", "% Wins"),
                        Stream.of(PokerCalc.Result.values())
                                .sorted(Comparator.comparingInt(PokerCalc.Result::getScore))
                                .filter(result -> result != PokerCalc.Result.UNKNOWN)
                                .map(result -> "% " + result.name())
                ).collect(Collectors.toList());
        tableBuilder.addRow(new TableRow<>(header));

        List<Integer> align = header.stream().map(x -> Table.ALIGN_RIGHT).collect(Collectors.toList());
        align.set(0, Table.ALIGN_LEFT);
        align.set(1, Table.ALIGN_LEFT);
        tableBuilder.withAlignments(align);

        final long n = gameStats.getResultStats().values().stream().reduce(0L, Long::sum);
        for (Integer player : gameStats.getPlayerWinStats().keySet()) {
            List<Object> row = new ArrayList<>();
            row.add(String.format("Player%d", player));
            row.add(gameStats.getPlayersHand().get(player).stream().map(Card::toString).collect(Collectors.joining(" ")));
            row.add(String.format("%.2f", ONE_HANDRED_PERCENT * gameStats.getPlayerWinStats().get(player) / n));
            final long n1 = Math.max(Math.min(1, n), gameStats.getPlayerResultWinStats().get(player).values().stream().reduce(0L, Long::sum));
            row.addAll(Stream.of(PokerCalc.Result.values())
                    .sorted(Comparator.comparingInt(PokerCalc.Result::getScore))
                    .filter(result -> result != PokerCalc.Result.UNKNOWN)
                    .map(result -> String.format("%.2f", ONE_HANDRED_PERCENT * gameStats.getPlayerResultWinStats().get(player).get(result) / n1))
                    .collect(Collectors.toList())
            );
            tableBuilder.addRow(new TableRow<>(row));
        }
        if (gameStats.getRiver().size() > 0) {
            System.out.println("River setup " + gameStats.getRiver().toString());
        }
        System.out.println(tableBuilder.build());
    }

    @Override
    public void printStatistics(final GameStatistic gameStats) {
        System.out.println("Results statistics:");
        printResultStatistics(gameStats);
        System.out.println("Players statistics:");
        printPlayerStatistics(gameStats);
    }
}
