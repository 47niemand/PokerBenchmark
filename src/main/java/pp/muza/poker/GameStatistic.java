package pp.muza.poker;

import pp.muza.cards.Card;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface GameStatistic {

    Long getReportedCount();

    Map<Integer, Collection<Card>> getPlayersHand();

    Collection<Card> getRiver();

    Map<Integer, Map<PokerCalc.Result, Long>> getPlayerResultWinStats();

    Map<PokerCalc.Result, Long> getResultStats();

    Map<Integer, Long> getPlayerWinStats();

    void reportStatistics(List<PokerCalc> results);

    void initialize(int players);

}
