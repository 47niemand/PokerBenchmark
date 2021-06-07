package pp.muza.poker;

import pp.muza.cards.CardStack;

public interface PokerCalc {

    CardStack getCombination();

    Result getResult();

    int getScore();

    int getPlayer();

    CardStack getHand();

    enum Result {
        UNKNOWN(0),
        HIGHCARD(1),
        ONEPAIR(2),
        TWOPAIRS(3),
        THREEOFAKIND(4),
        STRAIGHT(5),
        FLUSH(6),
        FULLHOUSE(7),
        QUADS(8),
        STRAIGHTFLUSH(9);

        private final int score;

        Result(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }
}
