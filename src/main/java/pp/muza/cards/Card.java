package pp.muza.cards;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Card {
    private final Value value;
    private final Suit suit;

    public Card(final Value value, final Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public static Card valueOf(final String s) throws IllegalArgumentException {
        String[] t = s.split(Suit.TAIL_REGEXP);
        if (t.length != 2) {
            throw new IllegalArgumentException(String.format("'%s' is not valid card", s));
        }
        return new Card(Value.valueOfLabel(t[0].trim()), Suit.valueOfLabel(t[1].trim().charAt(0)));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        return value == card.value && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, suit);
    }

    @Override
    public String toString() {
        return String.format("%s%c", value.label, suit.label);
    }

    public Value getValue() {
        return value;
    }

    public Suit getSuit() {
        return suit;
    }

    int scoreValue() {
        return value.score * Suit.COUNT + suit.ordinal();
    }

    int scoreSuit() {
        return suit.ordinal() * (Value.MAX_SCORE + 1) + value.score;
    }

    public int compareScoreByValueDesc(final Card withCard) {
        return Integer.compare(withCard.scoreValue(), scoreValue());
    }

    public int compareScoreBySuitDesc(final Card withCard) {
        return Integer.compare(withCard.scoreSuit(), scoreSuit());
    }

    public enum Suit {
        CLUBS('C'),
        DIAMONDS('D'),
        SPADES('S'),
        HEARTS('H');

        public static final int COUNT = values().length;
        static final String TAIL_REGEXP =
                String.format(
                        "(?=[%s])",
                        Stream.of(values())
                                .map(x -> String.valueOf(x.label) + Character.toLowerCase(x.label))
                                .collect(Collectors.joining()));
        static final String HEAD_REGEXP =
                String.format(
                        "(?<=[%s])",
                        Stream.of(values())
                                .map(x -> String.valueOf(x.label) + Character.toLowerCase(x.label))
                                .collect(Collectors.joining()));

        private final char label;

        Suit(final char label) {
            this.label = label;
        }

        static Suit valueOfLabel(final char label) throws IllegalArgumentException {
            for (Suit v : Suit.values()) {
                if (v.label == Character.toUpperCase(label)) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Invalid card suit");
        }
    }

    public enum Value {
        TWO(2, "2", null),
        TREE(3, "3", null),
        FOUR(4, "4", null),
        FIVE(5, "5", null),
        SIX(6, "6", null),
        SEVEN(7, "7", null),
        EIGHT(8, "8", null),
        NINE(9, "9", null),
        TEN(10, "10", "T"),
        JACK(11, "J", null),
        QUEEN(12, "Q", null),
        KING(13, "K", null),
        ACE(14, "A", "1");

        public static final int MAX_SCORE =
                Stream.of(Value.values()).map(x -> x.score).max(Integer::compareTo).orElse(0);
        static final Value[] VALUES = values();
        private final int score;
        private final String label;
        private final String altLabel;
        Value(final int score, final String label, final String altLabel) {
            this.score = score;
            this.label = label;
            this.altLabel = altLabel;
        }

        static Value valueOfLabel(final String label) throws IllegalArgumentException {
            String l = label.trim();
            for (Value v : Value.values()) {
                if (v.label.equalsIgnoreCase(l)
                        || ((v.altLabel != null) && v.altLabel.equalsIgnoreCase(l))) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Invalid card value");
        }

        public int getScore() {
            return score;
        }

        public Value prev() {
            return VALUES[(this.ordinal() - 1 + VALUES.length) % VALUES.length];
        }
    }
}
