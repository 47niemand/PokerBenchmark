package pp.muza.poker;

import pp.muza.cards.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public final class GameStatisticCollector implements GameStatistic {

    private static final Collection<Card> EMPTY_LIST = Collections.emptyList();

    private final AtomicLong reportedCount = new AtomicLong();
    private final Map<Integer, Map<PokerCalc.Result, AtomicLong>> playerWinResultStats = new HashMap<>();
    private final Map<PokerCalc.Result, AtomicLong> resultStats = new EnumMap<>(PokerCalc.Result.class);
    private final Map<Integer, AtomicLong> playerWins = new HashMap<>();
    private final Map<Integer, Collection<Card>> playerHand = new HashMap<>();
    private Collection<Card> river = EMPTY_LIST;
    private boolean initialized = false;

    @Override
    public synchronized void initialize(final int players) {
        playerWinResultStats.clear();
        resultStats.clear();
        playerWins.clear();
        playerHand.clear();
        river = EMPTY_LIST;
        reportedCount.set(0L);

        for (PokerCalc.Result r : PokerCalc.Result.values()) {
            resultStats.put(r, new AtomicLong());
        }
        for (int i = 0; i < players; i++) {
            Map<PokerCalc.Result, AtomicLong> perResult = new EnumMap<>(PokerCalc.Result.class);
            for (PokerCalc.Result r : PokerCalc.Result.values()) {
                perResult.put(r, new AtomicLong());
            }
            playerWinResultStats.put(i, perResult);
            playerWins.put(i, new AtomicLong());
            playerHand.put(i, EMPTY_LIST);
        }
        initialized = true;
    }

    private static List<Card> intersection(Collection<Card> a, Collection<Card> b) {
        Set<Card> setB = new HashSet<>(b);
        List<Card> result = new ArrayList<>();
        for (Card card : a) {
            if (setB.contains(card))
                result.add(card);
        }
        return result;
    }

    @Override
    public void reportStatistics(final List<PokerCalc> results) {
        if (!initialized || results == null || results.isEmpty())
            return;

        // Compute max score outside synchronized block
        int maxScore = Integer.MIN_VALUE;
        for (PokerCalc calc : results) {
            if (calc.getScore() > maxScore)
                maxScore = calc.getScore();
        }

        // Extract river cards outside synchronized block (subList is O(1))
        List<Card> firstHand = results.get(0).getHand();
        List<Card> newRiver = firstHand.subList(2, firstHand.size());

        final int finalMaxScore = maxScore;
        synchronized (this) {
            for (PokerCalc result : results) {
                int player = result.getPlayer();
                List<Card> holeCards = result.getHand().subList(0, 2);

                // Update tracked hole cards (intersection across all seen games)
                Collection<Card> current = playerHand.get(player);
                playerHand.put(player, current == EMPTY_LIST
                        ? new ArrayList<>(holeCards)
                        : current.isEmpty() ? current : intersection(current, holeCards));

                // Update win statistics
                if (result.getScore() == finalMaxScore) {
                    playerWinResultStats.get(player).get(result.getResult()).incrementAndGet();
                    resultStats.get(result.getResult()).incrementAndGet();
                    playerWins.get(player).incrementAndGet();
                }
            }

            // Update tracked river cards
            river = river == EMPTY_LIST ? newRiver : river.isEmpty() ? river : intersection(river, newRiver);
            reportedCount.incrementAndGet();
        }
    }

    @Override
    public synchronized Map<Integer, Collection<Card>> getPlayersHand() {
        return new HashMap<>(playerHand);
    }

    @Override
    public synchronized Collection<Card> getRiver() {
        return new ArrayList<>(river);
    }

    @Override
    public Long getReportedCount() {
        return reportedCount.get();
    }

    @Override
    public synchronized Map<Integer, Map<PokerCalc.Result, Long>> getPlayerResultWinStats() {
        Map<Integer, Map<PokerCalc.Result, Long>> result = new HashMap<>();
        playerWinResultStats.forEach((player, stats) -> {
            Map<PokerCalc.Result, Long> snap = new EnumMap<>(PokerCalc.Result.class);
            stats.forEach((r, c) -> snap.put(r, c.get()));
            result.put(player, snap);
        });
        return result;
    }

    @Override
    public synchronized Map<PokerCalc.Result, Long> getResultStats() {
        Map<PokerCalc.Result, Long> result = new EnumMap<>(PokerCalc.Result.class);
        resultStats.forEach((r, c) -> result.put(r, c.get()));
        return result;
    }

    @Override
    public synchronized Map<Integer, Long> getPlayerWinStats() {
        Map<Integer, Long> result = new HashMap<>();
        playerWins.forEach((p, c) -> result.put(p, c.get()));
        return result;
    }
}
