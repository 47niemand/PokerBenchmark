package pp.muza.poker;

import pp.muza.cards.Card;
import pp.muza.cards.CardStack;

import java.util.ArrayList;

// Texas hold 'em calculator
public final class TexasHoldEmCalc implements PokerCalc {

    private final CardStack combination;
    private final Result result;
    private final int score;
    private final int player;
    private final CardStack hand;

    private TexasHoldEmCalc(final int player, final CardStack combination, final Result result, final int score, final CardStack hand) {
        this.player = player;
        this.combination = combination;
        this.result = result;
        this.score = score;
        this.hand = hand;
    }

    /**
     * Calculates the result for a given poker hand
     *
     * @param hand - it's a list which contains two hole cards for index 0 to 1, rest is community cards
     * @param tag  player's ID
     * @return PokerCalc
     */
    public static PokerCalc calculateResult(final ArrayList<? extends Card> hand, final int tag) {
        Result res = Result.UNKNOWN;
        CardStack handCpy = new CardStack(hand);
        CardStack combination = new CardStack();

        handCpy.sort(Card::compareScoreByValueDesc);

        for (int straightLen = 1, straightIdx = 0, combLen = 0, combIdx = 0, idx = 0; idx < handCpy.size(); idx++) {
            Card current = handCpy.get(idx);

            if (current.getValue() == handCpy.get(combIdx).getValue()) {
                combLen++;
            } else {
                combIdx = idx;
                combLen = 1;
            }

            if (current.getValue() == handCpy.get(straightIdx).getValue().prev()) {
                straightLen++;
                straightIdx = idx;
            } else if (current.getValue() != handCpy.get(straightIdx).getValue()) {
                straightIdx = idx;
                straightLen = 1;
            }

            if ((straightLen == 5) && (res.getScore() < PokerCalc.Result.STRAIGHT.getScore())) {
                res = PokerCalc.Result.STRAIGHT;
                combination.clear();
                for (int j = idx; j >= 0; j--) {
                    if (combination.peekLast() != handCpy.get(j)) {
                        combination.add(0, handCpy.get(j));
                    }
                    if (combination.size() >= 5) {
                        break;
                    }
                }
                boolean checkFlush = combination.size() > 1;
                for (int j = 1; checkFlush && j < combination.size(); j++) {
                    checkFlush = combination.get(j).getSuit() == handCpy.get(0).getSuit();
                }
                if (checkFlush) {
                    res = PokerCalc.Result.STRAIGHTFLUSH;
                }
            } else if ((combLen == 1) && (res == PokerCalc.Result.UNKNOWN)) {
                res = PokerCalc.Result.HIGHCARD;
                combination.clear();
                combination.add(current);
            } else if (combLen == 2) {
                if (res == PokerCalc.Result.HIGHCARD) {
                    // A A
                    res = PokerCalc.Result.ONEPAIR;
                    combination.clear();
                    combination.addAll(handCpy.subList(idx - 1, idx + 1));
                } else if (res == PokerCalc.Result.ONEPAIR) {
                    // AA BB
                    res = PokerCalc.Result.TWOPAIRS;
                    combination.addAll(handCpy.subList(idx - 1, idx + 1));
                } else if (res == PokerCalc.Result.THREEOFAKIND) {
                    //AAA BB
                    res = PokerCalc.Result.FULLHOUSE;
                    combination.addAll(handCpy.subList(idx - 1, idx + 1));
                }
            } else if (combLen == 3) {
                if (res == PokerCalc.Result.ONEPAIR) {
                    // AA A
                    res = PokerCalc.Result.THREEOFAKIND;
                    combination.clear();
                    combination.addAll(handCpy.subList(idx - 2, idx + 1));
                } else if (res == PokerCalc.Result.TWOPAIRS) {
                    // AA BB B
                    res = PokerCalc.Result.FULLHOUSE;
                    combination.add(current);
                }
            } else if (combLen == 4 && res.getScore() < PokerCalc.Result.QUADS.getScore()) {
                res = PokerCalc.Result.QUADS;
                combination.clear();
                combination.addAll(handCpy.subList(idx - 3, idx + 1));
            }
        }

        handCpy.sort(Card::compareScoreBySuitDesc);

        for (int combLen = 0, combIdx = 0, idx = 0; idx < handCpy.size(); idx++) {
            Card current = handCpy.get(idx);

            if (current.getSuit() == handCpy.get(combIdx).getSuit()) {
                combLen++;
            } else {
                combIdx = idx;
                combLen = 1;
            }

            if ((combLen >= 5) && (res.getScore() < PokerCalc.Result.FLUSH.getScore())) {
                res = PokerCalc.Result.FLUSH;
                combination.clear();
                combination.addAll(handCpy.subList(idx - 4, idx + 1));
                break;
            }
        }

        final int scoreMultiples = (Card.Value.MAX_SCORE + 1);
        int kicker = hand.stream().sequential().limit(2).map(x -> x.getValue().getScore()).reduce(0, Integer::sum);
        int score = res.getScore() * scoreMultiples * scoreMultiples + combination.getFirst().getValue().getScore() * scoreMultiples + kicker;

        return new TexasHoldEmCalc(tag, combination, res, score, new CardStack(hand));
    }

    @Override
    public CardStack getCombination() {
        return combination;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int getPlayer() {
        return player;
    }

    @Override
    public CardStack getHand() {
        return hand;
    }

    @Override
    public String toString() {
        return "PokerCalc{" +
                "player=" + getPlayer() +
                ",result=" + getResult() +
                (getHand() != null ? ", hand=\"" + getHand() + "\"" : "") +
                ", combination=\"" + getCombination() + "\"" +
                ", score=" + getScore() +
                "}";
    }

}
