package pp.muza.cards;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CardStackTest {

    // --- constructors ---

    @Test
    public void defaultConstructor_maxSizeIsStandardDeckSize() {
        CardStack stack = new CardStack();
        assertEquals(CardStack.STANDARD_DECK_SIZE, stack.getMaxSize());
        assertTrue(stack.isEmpty());
    }

    @Test
    public void intConstructor_setsMaxSize() {
        CardStack stack = new CardStack(10);
        assertEquals(10, stack.getMaxSize());
    }

    @Test
    public void collectionConstructor_fromCardStack_preservesMaxSize() {
        CardStack source = new CardStack(5);
        source.add(Card.of(Card.Value.ACE, Card.Suit.SPADES));
        CardStack copy = new CardStack(source);
        assertEquals(5, copy.getMaxSize());
        assertEquals(1, copy.size());
    }

    @Test
    public void collectionConstructor_fromPlainList_usesListSize() {
        List<Card> list = Arrays.asList(
                Card.of(Card.Value.ACE, Card.Suit.SPADES),
                Card.of(Card.Value.KING, Card.Suit.HEARTS)
        );
        CardStack stack = new CardStack(list);
        assertEquals(2, stack.getMaxSize());
        assertEquals(2, stack.size());
    }

    // --- makeDefaultStack ---

    @Test
    public void makeDefaultStack_hasFiftyTwoCards() {
        CardStack deck = CardStack.makeDefaultStack();
        assertEquals(52, deck.size());
        assertEquals(52, deck.getMaxSize());
    }

    @Test
    public void makeDefaultStack_isFull() {
        assertTrue(CardStack.makeDefaultStack().isFull());
    }

    @Test
    public void makeDefaultStack_containsAllCards() {
        CardStack deck = CardStack.makeDefaultStack();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Value value : Card.Value.values()) {
                assertTrue(deck.contains(Card.of(value, suit)));
            }
        }
    }

    // --- parseCards ---

    @Test
    public void parseCards_spaceDelimited_parsesCorrectly() {
        CardStack stack = CardStack.parseCards("AC KD QH JS");
        assertEquals(4, stack.size());
        assertEquals(Card.of(Card.Value.ACE,   Card.Suit.CLUBS),    stack.get(0));
        assertEquals(Card.of(Card.Value.KING,  Card.Suit.DIAMONDS), stack.get(1));
        assertEquals(Card.of(Card.Value.QUEEN, Card.Suit.HEARTS),   stack.get(2));
        assertEquals(Card.of(Card.Value.JACK,  Card.Suit.SPADES),   stack.get(3));
    }

    @Test
    public void parseCards_commaDelimited_parsesCorrectly() {
        CardStack stack = CardStack.parseCards("2C,3D,4H");
        assertEquals(3, stack.size());
    }

    @Test
    public void parseCards_lowercase_parsesCorrectly() {
        CardStack stack = CardStack.parseCards("ac kd");
        assertEquals(2, stack.size());
        assertEquals(Card.of(Card.Value.ACE,  Card.Suit.CLUBS),    stack.get(0));
        assertEquals(Card.of(Card.Value.KING, Card.Suit.DIAMONDS), stack.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseCards_invalidCard_throwsIllegalArgument() {
        CardStack.parseCards("ZZ");
    }

    // --- add / capacity enforcement ---

    @Test(expected = UnsupportedOperationException.class)
    public void add_exceedsMaxSize_throws() {
        CardStack stack = new CardStack(1);
        stack.add(Card.of(Card.Value.ACE,  Card.Suit.SPADES));
        stack.add(Card.of(Card.Value.KING, Card.Suit.HEARTS));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAll_exceedsMaxSize_throws() {
        CardStack stack = new CardStack(1);
        stack.addAll(Arrays.asList(
                Card.of(Card.Value.ACE,  Card.Suit.SPADES),
                Card.of(Card.Value.KING, Card.Suit.HEARTS)
        ));
    }

    // --- pop ---

    @Test
    public void pop_returnsAndRemovesLastCard() {
        CardStack stack = CardStack.parseCards("AC KD");
        Card popped = stack.pop();
        assertEquals(Card.of(Card.Value.KING, Card.Suit.DIAMONDS), popped);
        assertEquals(1, stack.size());
    }

    @Test(expected = IllegalStateException.class)
    public void pop_emptyStack_throws() {
        new CardStack(5).pop();
    }

    // --- takeFirst ---

    @Test
    public void takeFirst_cardPresent_removesAndReturnsCard() {
        CardStack stack = CardStack.parseCards("AC KD QH");
        Card taken = stack.takeFirst(Card.of(Card.Value.KING, Card.Suit.DIAMONDS));
        assertEquals(Card.of(Card.Value.KING, Card.Suit.DIAMONDS), taken);
        assertEquals(2, stack.size());
        assertFalse(stack.contains(Card.of(Card.Value.KING, Card.Suit.DIAMONDS)));
    }

    @Test
    public void takeFirst_cardAbsent_returnsNull() {
        CardStack stack = CardStack.parseCards("AC KD");
        assertNull(stack.takeFirst(Card.of(Card.Value.TWO, Card.Suit.CLUBS)));
    }

    // --- exchange ---

    @Test
    public void exchange_swapsPositions() {
        CardStack stack = CardStack.parseCards("AC KD");
        stack.exchange(0, 1);
        assertEquals(Card.of(Card.Value.KING, Card.Suit.DIAMONDS), stack.get(0));
        assertEquals(Card.of(Card.Value.ACE,  Card.Suit.CLUBS),    stack.get(1));
    }

    // --- toss ---

    @Test
    public void toss_preservesSizeAndContents() {
        CardStack deck = CardStack.makeDefaultStack();
        CardStack copy = new CardStack(deck);
        assertTrue("Copy should contain all cards from deck", copy.containsAll(deck));
        deck.toss();
        assertEquals(52, deck.size());
        assertTrue("Copy should still contain all cards from deck", copy.containsAll(deck));

        for (Card card : copy) {
            System.out.println("Checking " + card);
            assertTrue( "Deck should still contain " + card, deck.contains(card));
        }
   
    }

    @Test
    public void toss_preservesSizeAndContents2() {
        CardStack deck = CardStack.makeDefaultStack();
        CardStack copy = new CardStack(deck);
        assertTrue("Deck should contain all cards from copy", deck.containsAll(copy));
        deck.toss();
        assertTrue("Deck should still contain all cards from copy", deck.containsAll(copy));
        assertEquals(52, deck.size());
        assertTrue("Copy should still contain all cards from deck", copy.containsAll(deck));
    }

    // --- isFull ---

    @Test
    public void isFull_whenFull_returnsTrue() {
        CardStack stack = new CardStack(1);
        stack.add(Card.of(Card.Value.ACE, Card.Suit.SPADES));
        assertTrue(stack.isFull());
    }

    @Test
    public void isFull_whenNotFull_returnsFalse() {
        CardStack stack = new CardStack(2);
        stack.add(Card.of(Card.Value.ACE, Card.Suit.SPADES));
        assertFalse(stack.isFull());
    }

    // --- peekLast ---

    @Test
    public void peekLast_nonEmpty_returnsLastCard() {
        CardStack stack = CardStack.parseCards("AC KD");
        assertEquals(Card.of(Card.Value.KING, Card.Suit.DIAMONDS), stack.peekLast());
        assertEquals(2, stack.size()); // non-destructive
    }

    @Test
    public void peekLast_empty_returnsNull() {
        assertNull(new CardStack(5).peekLast());
    }

    // --- getFirst ---

    @Test
    public void getFirst_returnsFirstCard() {
        CardStack stack = CardStack.parseCards("AC KD QH");
        assertEquals(Card.of(Card.Value.ACE, Card.Suit.CLUBS), stack.getFirst());
    }

    // --- toString ---

    @Test
    public void toString_spaceDelimitedCardLabels() {
        CardStack stack = CardStack.parseCards("AC KD");
        assertEquals("AC KD", stack.toString());
    }
}
