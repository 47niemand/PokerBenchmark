package pp.muza.poker;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pp.muza.cards.Card;
import pp.muza.cards.CardStack;

// Texas hold 'em calculator
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TexasHoldEmCalc implements PokerCalc {

    private final CardStack combination;
    private final Result result;
    private final int score;
    private final int player;
    private final CardStack hand;

    /**
     * Calculates the result for a given poker hand
     *
     * @param hand - it's a list which contains two hole cards for index 0 to 1,
     *             rest is community cards
     * @param tag  player's ID
     * @return PokerCalc
     */
    public static PokerCalc calculateResult(final List<? extends Card> hand, final int tag) {
        CardStack sorted = new CardStack(hand);
        sorted.sort(Card::compareScoreByValueDesc);

        int[] valCount = new int[Card.Value.MAX_SCORE + 1];
        int[] suitCount = new int[Card.Suit.COUNT];
        for (Card c : sorted) {
            valCount[c.getValue().getScore()]++;
            suitCount[c.getSuit().ordinal()]++;
        }

        // Detect groups (scan high to low for correct priority)
        int quadsVal = 0, tripsVal = 0, pairVal1 = 0, pairVal2 = 0;
        for (int v = Card.Value.MAX_SCORE; v >= 2; v--) {
            switch (valCount[v]) {
                case 4:
                    quadsVal = v;
                    break;
                case 3:
                    if (tripsVal == 0)
                        tripsVal = v;
                    else if (pairVal1 == 0)
                        pairVal1 = v;
                    break;
                case 2:
                    if (pairVal1 == 0)
                        pairVal1 = v;
                    else if (pairVal2 == 0)
                        pairVal2 = v;
                    break;
            }
        }

        int straightHigh = findStraightHigh(valCount);

        // Detect flush
        int flushSuit = -1;
        for (int s = 0; s < Card.Suit.COUNT; s++) {
            if (suitCount[s] >= 5) {
                flushSuit = s;
                break;
            }
        }

        // Detect straight flush
        int sfHigh = 0;
        if (flushSuit >= 0) {
            int[] flushValCount = new int[Card.Value.MAX_SCORE + 1];
            for (Card c : sorted) {
                if (c.getSuit().ordinal() == flushSuit) {
                    flushValCount[c.getValue().getScore()]++;
                }
            }
            sfHigh = findStraightHigh(flushValCount);
        }

        // Determine best result (checked in descending rank order)
        Result res;
        CardStack combination = new CardStack(5);
        int[] scoreCards = new int[5]; // up to 5 significant cards for scoring

        if (sfHigh > 0) {
            res = Result.STRAIGHTFLUSH;
            buildStraightCombo(sorted, combination, sfHigh, flushSuit);
            scoreCards[0] = sfHigh;
        } else if (quadsVal > 0) {
            res = Result.QUADS;
            buildGroupCombo(sorted, combination, quadsVal, 4);
            scoreCards[0] = quadsVal;
            // kicker: best card not in the quads
            scoreCards[1] = findKickers(sorted, combination, 1)[0];
        } else if (tripsVal > 0 && pairVal1 > 0) {
            res = Result.FULLHOUSE;
            buildFullHouseCombo(sorted, combination, tripsVal, pairVal1);
            scoreCards[0] = tripsVal;
            scoreCards[1] = pairVal1;
        } else if (flushSuit >= 0) {
            res = Result.FLUSH;
            buildFlushCombo(sorted, combination, flushSuit);
            // all 5 flush cards matter for comparison
            for (int i = 0; i < 5; i++) {
                scoreCards[i] = combination.get(i).getValue().getScore();
            }
        } else if (straightHigh > 0) {
            res = Result.STRAIGHT;
            buildStraightCombo(sorted, combination, straightHigh, -1);
            scoreCards[0] = straightHigh;
        } else if (tripsVal > 0) {
            res = Result.THREEOFAKIND;
            buildGroupCombo(sorted, combination, tripsVal, 3);
            scoreCards[0] = tripsVal;
            int[] kickers = findKickers(sorted, combination, 2);
            scoreCards[1] = kickers[0];
            scoreCards[2] = kickers[1];
        } else if (pairVal1 > 0 && pairVal2 > 0) {
            res = Result.TWOPAIRS;
            buildGroupCombo(sorted, combination, pairVal1, 2);
            buildGroupCombo(sorted, combination, pairVal2, 2);
            scoreCards[0] = pairVal1;
            scoreCards[1] = pairVal2;
            scoreCards[2] = findKickers(sorted, combination, 1)[0];
        } else if (pairVal1 > 0) {
            res = Result.ONEPAIR;
            buildGroupCombo(sorted, combination, pairVal1, 2);
            scoreCards[0] = pairVal1;
            int[] kickers = findKickers(sorted, combination, 3);
            scoreCards[1] = kickers[0];
            scoreCards[2] = kickers[1];
            scoreCards[3] = kickers[2];
        } else {
            res = Result.HIGHCARD;
            for (int i = 0; i < 5 && i < sorted.size(); i++) {
                combination.add(sorted.get(i));
                scoreCards[i] = sorted.get(i).getValue().getScore();
            }
        }

        final int BASE = Card.Value.MAX_SCORE + 1; // 15
        int score = res.getScore();
        for (int i = 0; i < 5; i++) {
            score = score * BASE + scoreCards[i];
        }

        return new TexasHoldEmCalc(combination, res, score, tag, new CardStack(hand));
    }

    /**
     * Find the best N kickers from sorted hand that are NOT in the combination.
     */
    private static int[] findKickers(CardStack sorted, CardStack combination, int count) {
        int[] kickers = new int[count];
        int found = 0;
        for (Card c : sorted) {
            if (found >= count) break;
            if (!combination.contains(c)) {
                kickers[found++] = c.getValue().getScore();
            }
        }
        return kickers;
    }

    private static int findStraightHigh(int[] valCount) {
        int consecutive = 0;
        for (int v = Card.Value.MAX_SCORE; v >= 2; v--) {
            if (valCount[v] > 0) {
                if (++consecutive >= 5)
                    return v + 4;
            } else {
                consecutive = 0;
            }
        }
        // Wheel: A-2-3-4-5
        if (valCount[Card.Value.MAX_SCORE] > 0
                && valCount[2] > 0 && valCount[3] > 0 && valCount[4] > 0 && valCount[5] > 0) {
            return 5;
        }
        return 0;
    }

    private static void buildGroupCombo(CardStack sorted, CardStack combo, int value, int count) {
        int added = 0;
        for (Card c : sorted) {
            if (c.getValue().getScore() == value && added < count) {
                combo.add(c);
                added++;
            }
        }
    }

    private static void buildFullHouseCombo(CardStack sorted, CardStack combo, int tripsVal, int pairVal) {
        int tripsAdded = 0, pairAdded = 0;
        for (Card c : sorted) {
            int v = c.getValue().getScore();
            if (v == tripsVal && tripsAdded < 3) {
                combo.add(c);
                tripsAdded++;
            } else if (v == pairVal && pairAdded < 2) {
                combo.add(c);
                pairAdded++;
            }
        }
    }

    private static void buildFlushCombo(CardStack sorted, CardStack combo, int flushSuit) {
        for (Card c : sorted) {
            if (c.getSuit().ordinal() == flushSuit && combo.size() < 5) {
                combo.add(c);
            }
        }
    }

    private static void buildStraightCombo(CardStack sorted, CardStack combo, int highVal, int flushSuit) {
        boolean isWheel = (highVal == 5);
        int lowVal = highVal - 4;
        boolean[] added = new boolean[Card.Value.MAX_SCORE + 1];
        for (Card c : sorted) {
            int v = c.getValue().getScore();
            boolean inRange = isWheel
                    ? ((v >= 2 && v <= 5) || v == Card.Value.MAX_SCORE)
                    : (v >= lowVal && v <= highVal);
            if (inRange && !added[v] && (flushSuit < 0 || c.getSuit().ordinal() == flushSuit)) {
                combo.add(c);
                added[v] = true;
            }
        }
        // For wheel, move the ace (which sorted first) to the end
        if (isWheel && combo.size() == 5
                && combo.getFirst().getValue().getScore() == Card.Value.MAX_SCORE) {
            combo.add(combo.remove(0));
        }
    }

    @Override
    public String toString() {
        return "PokerCalc{" +
                "player=" + getPlayer() +
                ",result=" + getResult() +
                ", hand=\"" + getHand() + "\"" +
                ", combination=\"" + getCombination() + "\"" +
                ", score=" + getScore() +
                "}";
    }
}
