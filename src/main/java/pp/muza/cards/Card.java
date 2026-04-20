package pp.muza.cards;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Card {

    private static final Card[] CARD_CACHE;

    static {
        Value[] values = Value.values();
        Suit[] suits = Suit.values();
        CARD_CACHE = new Card[values.length * Suit.COUNT];
        for (Value value : values) {
            for (Suit suit : suits) {
                CARD_CACHE[value.ordinal() * Suit.COUNT + suit.ordinal()] = new Card(value, suit);
            }
        }
    }

    private final Value value;
    private final Suit suit;

    private Card(final Value value, final Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public static Card of(final Value value, final Suit suit) {
        return CARD_CACHE[value.ordinal() * Suit.COUNT + suit.ordinal()];
    }

    public static Card valueOf(final String s) throws IllegalArgumentException {
        String[] t = s.split(Suit.TAIL_REGEXP);
        if (t.length != 2) {
            throw new IllegalArgumentException("Invalid card: '" + s + "'");
        }
        try {
            String suitToken = t[1].trim();
            if (suitToken.isEmpty()) {
                throw new IllegalArgumentException("Missing suit");
            }
            return of(Value.valueOfLabel(t[0].trim()), Suit.valueOfLabel(suitToken.charAt(0)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid card '" + s + "': " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s%c", value.label, suit.label);
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
            throw new IllegalArgumentException("Invalid suit: '" + label + "'");
        }
    }

    public enum Value {
        TWO(2, "2", null),
        THREE(3, "3", null),
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
        @Getter
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
            throw new IllegalArgumentException("Invalid value: '" + l + "'");
        }

        public Value prev() {
            return VALUES[(this.ordinal() - 1 + VALUES.length) % VALUES.length];
        }
    }
}
