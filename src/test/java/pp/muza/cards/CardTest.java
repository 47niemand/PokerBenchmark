package pp.muza.cards;

import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {

    // --- valueOf / parsing ---

    @Test
    public void valueOf_uppercase_parsesCorrectly() {
        Card card = Card.valueOf("AC");
        assertEquals(Card.Value.ACE, card.getValue());
        assertEquals(Card.Suit.CLUBS, card.getSuit());
    }

    @Test
    public void valueOf_lowercase_parsesCorrectly() {
        Card card = Card.valueOf("kh");
        assertEquals(Card.Value.KING, card.getValue());
        assertEquals(Card.Suit.HEARTS, card.getSuit());
    }

    @Test
    public void valueOf_tenWithAltLabel_parsesCorrectly() {
        Card byFull = Card.valueOf("10S");
        Card byAlt  = Card.valueOf("TS");
        assertEquals(byFull, byAlt);
    }

    @Test
    public void valueOf_aceWithAltLabel_parsesCorrectly() {
        Card ace = Card.valueOf("1D");
        assertEquals(Card.Value.ACE, ace.getValue());
        assertEquals(Card.Suit.DIAMONDS, ace.getSuit());
    }

    @Test
    public void valueOf_invalidInput_throwsIllegalArgument() {
        try {
            Card.valueOf("ZZ");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("ZZ"));
        }
    }

    @Test
    public void valueOf_emptySuit_throwsIllegalArgument() {
        try {
            Card.valueOf("A");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("A"));
        }
    }

    @Test
    public void valueOf_invalidSuit_messageContainsInput() {
        // "AX" has no recognized suit character — TAIL_REGEXP does not split it,
        // so it fails the format check (no cause attached)
        try {
            Card.valueOf("AX");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("AX"));
        }
    }

    @Test
    public void valueOf_invalidValue_messageContainsInput() {
        try {
            Card.valueOf("ZC");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("ZC"));
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            assertTrue(e.getCause().getMessage().contains("Z"));
        }
    }

    // --- toString ---

    @Test
    public void toString_producesExpectedFormat() {
        assertEquals("AH", Card.valueOf("AH").toString());
        assertEquals("10D", Card.valueOf("10D").toString());
        assertEquals("2C", Card.valueOf("2C").toString());
    }

    // --- equals / hashCode / identity ---

    @Test
    public void equals_sameValueSuit_isEqual() {
        Card a = Card.of(Card.Value.ACE, Card.Suit.SPADES);
        Card b = Card.of(Card.Value.ACE, Card.Suit.SPADES);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertSame(a, b);
    }

    @Test
    public void equals_differentValue_notEqual() {
        Card a = Card.of(Card.Value.ACE, Card.Suit.SPADES);
        Card b = Card.of(Card.Value.KING, Card.Suit.SPADES);
        assertNotEquals(a, b);
    }

    @Test
    public void equals_differentSuit_notEqual() {
        Card a = Card.of(Card.Value.ACE, Card.Suit.SPADES);
        Card b = Card.of(Card.Value.ACE, Card.Suit.HEARTS);
        assertNotEquals(a, b);
    }

    // --- compareScoreByValueDesc ---

    @Test
    public void compareScoreByValueDesc_higherCard_returnsNegative() {
        Card ace  = Card.of(Card.Value.ACE,  Card.Suit.SPADES);
        Card king = Card.of(Card.Value.KING, Card.Suit.SPADES);
        assertTrue(ace.compareScoreByValueDesc(king) < 0);
    }

    @Test
    public void compareScoreByValueDesc_lowerCard_returnsPositive() {
        Card two = Card.of(Card.Value.TWO, Card.Suit.SPADES);
        Card ace = Card.of(Card.Value.ACE, Card.Suit.SPADES);
        assertTrue(two.compareScoreByValueDesc(ace) > 0);
    }

    @Test
    public void compareScoreByValueDesc_sameCard_returnsZero() {
        Card a = Card.of(Card.Value.QUEEN, Card.Suit.HEARTS);
        Card b = Card.of(Card.Value.QUEEN, Card.Suit.HEARTS);
        assertEquals(0, a.compareScoreByValueDesc(b));
    }

    // --- Value enum ---

    @Test
    public void value_score_isCorrect() {
        assertEquals(14, Card.Value.ACE.getScore());
        assertEquals(2,  Card.Value.TWO.getScore());
        assertEquals(10, Card.Value.TEN.getScore());
    }

    @Test
    public void value_prev_wrapsAround() {
        assertEquals(Card.Value.KING, Card.Value.ACE.prev());
        assertEquals(Card.Value.ACE,  Card.Value.TWO.prev());
    }

    @Test
    public void value_maxScore_isAce() {
        assertEquals(14, Card.Value.MAX_SCORE);
    }

    // --- Suit enum ---

    @Test
    public void suit_count_isFour() {
        assertEquals(4, Card.Suit.COUNT);
    }
}
