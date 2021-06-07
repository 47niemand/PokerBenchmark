package pp.muza.poker;

import pp.muza.cards.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class GameStatisticCollector implements GameStatistic {

    private static final Collection<Card> EMPTY_LIST = new ArrayList<>();

    private final AtomicLong reportedCount = new AtomicLong();
    private final Map<Integer, Map<PokerCalc.Result, AtomicLong>> playerWinResultStats = new HashMap<>();
    private final Map<PokerCalc.Result, AtomicLong> resultStats = new EnumMap<>(PokerCalc.Result.class);
    private final Map<Integer, AtomicLong> playerWins = new HashMap<>();
    private final Map<Integer, Collection<Card>> playerHand = new HashMap<>();
    private Collection<Card> river = EMPTY_LIST;
    private boolean initialized = false;

    @Override
    public synchronized void initialize(final int players) {
        for (Map<PokerCalc.Result, AtomicLong> i : playerWinResultStats.values()) {
            i.clear();
        }
        playerWinResultStats.clear();
        resultStats.clear();
        playerWins.clear();

        for (int i = 0; i < players; i++) {
            EnumMap<PokerCalc.Result, AtomicLong> playerWinResultItem = new EnumMap<>(PokerCalc.Result.class);
            playerWinResultStats.put(i, playerWinResultItem);
            for (PokerCalc.Result r : PokerCalc.Result.values()) {
                playerWinResultItem.put(r, new AtomicLong());
            }
            playerWins.put(i, new AtomicLong());
            playerHand.put(i, EMPTY_LIST);
            river = EMPTY_LIST;
        }
        for (PokerCalc.Result r : PokerCalc.Result.values()) {
            resultStats.put(r, new AtomicLong());
        }
        reportedCount.set(0L);
        initialized = true;
    }

    private Collection<Card> intersection(final Collection<Card> list1, final Collection<Card> list2) {
        List<Card> list = new ArrayList<>();
        for (Card t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    @Override
    public void reportStatistics(final List<PokerCalc> results) {
        if (!initialized) {
            throw new IllegalStateException();
        }
        if (results != null && results.size() > 0) {

            final int winnerScore = results.stream().map(PokerCalc::getScore).max(Integer::compareTo).orElse(-1);
            List<PokerCalc> winners = results.stream().filter(pokerCalc -> pokerCalc.getScore() == winnerScore).collect(Collectors.toList());
            synchronized (this) {
                for (PokerCalc result : results) {
                    Collection<Card> hand = playerHand.get(result.getPlayer());
                    if (hand == EMPTY_LIST) {
                        playerHand.put(result.getPlayer(),
                                result.getHand().stream().limit(2).collect(Collectors.toList()));
                    } else if (hand.size() > 0) {
                        playerHand.put(result.getPlayer(), intersection(hand,
                                result.getHand().stream().limit(2).collect(Collectors.toList())));
                    }
                }
                updateRiver(results.get(0).getHand().stream().skip(2).collect(Collectors.toList()));

                reportedCount.incrementAndGet();
                for (PokerCalc winner : winners) {
                    playerWinResultStats.get(winner.getPlayer()).get(winner.getResult()).incrementAndGet();
                    resultStats.get(winner.getResult()).incrementAndGet();
                    playerWins.get(winner.getPlayer()).incrementAndGet();
                }
            }
        }
    }

    private void updateRiver(final List<Card> collect) {
        if (river == EMPTY_LIST) {
            river = collect;
        } else if (river.size() > 0) {
            river = intersection(river, collect);
        }
    }

    @Override
    public Map<Integer, Collection<Card>> getPlayersHand() {
        return playerHand;
    }

    @Override
    public Collection<Card> getRiver() {
        return river;
    }

    @Override
    public Long getReportedCount() {
        return reportedCount.get();
    }

    @Override
    public Map<Integer, Map<PokerCalc.Result, Long>> getPlayerResultWinStats() {
        Map<Integer, Map<PokerCalc.Result, Long>> result = new HashMap<>();
        for (Integer player : playerWinResultStats.keySet()) {
            Map<PokerCalc.Result, Long> stat = playerWinResultStats.get(player).entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue().get()));
            result.put(player, stat);
        }
        return result;
    }

    @Override
    public Map<PokerCalc.Result, Long> getResultStats() {
        return resultStats.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().get()));
    }

    @Override
    public Map<Integer, Long> getPlayerWinStats() {
        return playerWins.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().get()));
    }
}
