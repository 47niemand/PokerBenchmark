package pp.muza.cards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class CardStack extends ArrayList<Card> {

    public static final int STANDARD_DECK_SIZE = 52;
    private static final Random RANDOM = new Random();
    private final int maxSize;

    public CardStack() {
        this(STANDARD_DECK_SIZE);
    }

    public CardStack(Collection<? extends Card> source) {
        this(source instanceof CardStack ? ((CardStack) source).getMaxSize() : source.size());
        super.addAll(source);
    }

    public CardStack(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public static CardStack parseCards(final String str) throws IllegalArgumentException {
        List<Card> t = new ArrayList<>();
        String[] s = str.split(Card.Suit.HEAD_REGEXP);
        int i = 0;
        while (i < s.length) {
            String s1 = s[i].replaceAll("[,;: ]", "");
            if (s1.length() > 0) {
                t.add(Card.valueOf(s1));
            }
            i++;
        }
        return new CardStack(t);
    }

    public static CardStack makeDefaultStack() {
        final CardStack cardStack = new CardStack(STANDARD_DECK_SIZE);
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Value value : Card.Value.values()) {
                cardStack.add(new Card(value, suit));
            }
        }
        assert cardStack.size() == cardStack.maxSize;
        return cardStack;
    }

    @Override
    public boolean add(final Card card) {
        checkMaxSize(1);
        return super.add(card);
    }

    @Override
    public void add(final int index, final Card element) {
        checkMaxSize(1);
        super.add(index, element);
    }

    @Override
    public boolean addAll(final Collection<? extends Card> c) {
        checkMaxSize(c.size());
        return super.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Card> c) {
        checkMaxSize(c.size());
        return super.addAll(index, c);
    }

    private void checkMaxSize(int increment) {
        if (increment + size() > maxSize) {
            throw new UnsupportedOperationException("Maximum Size " + maxSize + " reached");
        }
    }

    @Override
    public String toString() {
        return this.stream().map(Card::toString).collect(Collectors.joining(" "));
    }

    public Card pop() {
        if (size() == 0) {
            throw new IllegalStateException("There are no cards left on the Stack");
        }
        return remove(size() - 1);
    }

    public Card takeFirst(final Card card) {
        int i = indexOf(card);
        if (i >= 0) {
            return remove(i);
        }
        return null;
    }

    public void toss() {
        for (int i = size() - 1; i > 0; i--) {
            exchange(i, RANDOM.nextInt(i + 1));
        }
    }

    public void exchange(final int index1, final int index2) {
        Card tmp = get(index1);
        set(index1, get(index2));
        set(index2, tmp);
    }

    public boolean isFull() {
        return size() >= maxSize;
    }

    public Card peekLast() {
        if (size() > 0) {
            return get(size() - 1);
        } else {
            return null;
        }
    }

    public Card getFirst() {
        return get(0);
    }

}
