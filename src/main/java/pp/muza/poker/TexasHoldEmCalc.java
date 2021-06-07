package pp.muza.poker;

import pp.muza.cards.Card;
import pp.muza.cards.CardStack;

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

    public static PokerCalc getResult(final CardStack sourceStack, final int tag) {
        Result res = Result.UNKNOWN;
        CardStack stack = new CardStack(sourceStack);
        CardStack combination = new CardStack();

        stack.sort(Card::compareScoreByValueDesc);

        for (int straightLen = 1, straightIdx = 0, combLen = 0, combIdx = 0, idx = 0; idx < stack.size(); idx++) {
            Card current = stack.get(idx);

            if (current.getValue() == stack.get(combIdx).getValue()) {
                combLen++;
            } else {
                combIdx = idx;
                combLen = 1;
            }

            if (current.getValue() == stack.get(straightIdx).getValue().prev()) {
                straightLen++;
                straightIdx = idx;
            } else if (current.getValue() != stack.get(straightIdx).getValue()) {
                straightIdx = idx;
                straightLen = 1;
            }

            if ((straightLen == 5) && (res.getScore() < TexasHoldEmCalc.Result.STRAIGHT.getScore())) {
                res = TexasHoldEmCalc.Result.STRAIGHT;
                combination.clear();
                for (int j = idx; j >= 0; j--) {
                    if (combination.peekLast() != stack.get(j)) {
                        combination.add(0, stack.get(j));
                    }
                    if (combination.size() >= 5) {
                        break;
                    }
                }
                boolean checkFlush = combination.size() > 1;
                for (int j = 1; checkFlush && j < combination.size(); j++) {
                    checkFlush = combination.get(j).getSuit() == stack.get(0).getSuit();
                }
                if (checkFlush) {
                    res = TexasHoldEmCalc.Result.STRAIGHTFLUSH;
                }
            } else if ((combLen == 1) && (res == TexasHoldEmCalc.Result.UNKNOWN)) {
                res = TexasHoldEmCalc.Result.HIGHCARD;
                combination.clear();
                combination.add(current);
            } else if (combLen == 2) {
                if (res == TexasHoldEmCalc.Result.HIGHCARD) {
                    // A A
                    res = TexasHoldEmCalc.Result.ONEPAIR;
                    combination.clear();
                    combination.addAll(stack.subList(idx - 1, idx + 1));
                } else if (res == TexasHoldEmCalc.Result.ONEPAIR) {
                    // AA BB
                    res = TexasHoldEmCalc.Result.TWOPAIRS;
                    combination.addAll(stack.subList(idx - 1, idx + 1));
                } else if (res == TexasHoldEmCalc.Result.THREEOFAKIND) {
                    //AAA BB
                    res = TexasHoldEmCalc.Result.FULLHOUSE;
                    combination.addAll(stack.subList(idx - 1, idx + 1));
                }
            } else if (combLen == 3) {
                if (res == TexasHoldEmCalc.Result.ONEPAIR) {
                    // AA A
                    res = TexasHoldEmCalc.Result.THREEOFAKIND;
                    combination.clear();
                    combination.addAll(stack.subList(idx - 2, idx + 1));
                } else if (res == TexasHoldEmCalc.Result.TWOPAIRS) {
                    // AA BB B
                    res = TexasHoldEmCalc.Result.FULLHOUSE;
                    combination.add(current);
                }
            } else if (combLen == 4 && res.getScore() < TexasHoldEmCalc.Result.QUADS.getScore()) {
                res = TexasHoldEmCalc.Result.QUADS;
                combination.clear();
                combination.addAll(stack.subList(idx - 3, idx + 1));
            }
        }

        stack.sort(Card::compareScoreBySuitDesc);

        for (int combLen = 0, combIdx = 0, idx = 0; idx < stack.size(); idx++) {
            Card current = stack.get(idx);

            if (current.getSuit() == stack.get(combIdx).getSuit()) {
                combLen++;
            } else {
                combIdx = idx;
                combLen = 1;
            }

            if ((combLen >= 5) && (res.getScore() < TexasHoldEmCalc.Result.FLUSH.getScore())) {
                res = TexasHoldEmCalc.Result.FLUSH;
                combination.clear();
                combination.addAll(stack.subList(idx - 4, idx + 1));
                break;
            }
        }

        final int scoreMultiples = (Card.Value.MAX_SCORE + 1);
        int kicker = sourceStack.stream().sequential().limit(2).map(x -> x.getValue().getScore()).reduce(0, Integer::sum);
        int score = res.getScore() * scoreMultiples * scoreMultiples + combination.getFirst().getValue().getScore() * scoreMultiples + kicker;

        return new TexasHoldEmCalc(tag, combination, res, score, new CardStack(sourceStack));
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
