package pp.muza.poker;

import org.junit.Test;
import pp.muza.cards.CardStack;

import static org.junit.Assert.*;

public class TexasHoldEmCalcTest {

    @Test
    public void getResultStraight() {
        {
            CardStack cards = CardStack.parseCards("2S 3C 5H 6H 7H 8S 9D");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHT, result.getResult());
            assertEquals("9D 8S 7H 6H 5H", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 3H 4H 5H 6H 7H TH");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHTFLUSH, result.getResult());
            assertEquals("7H 6H 5H 4H 3H", result.getCombination().toString());
        }
    }

    @Test
    public void getResult() {
        {
            CardStack cards = CardStack.parseCards("3H 2D QS KS 10D 9D 7H");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.HIGHCARD, result.getResult());
            assertEquals("KS", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("AD 5C 10S 2H 4S 3H 2C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.ONEPAIR, result.getResult());
            assertEquals("2H 2C", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 2C 2D AS");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.THREEOFAKIND, result.getResult());
            assertEquals("2H 2D 2C", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 2C 2D 2S 1C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.QUADS, result.getResult());
            assertEquals("2H 2S 2D 2C", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 2C 3D 3H");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.TWOPAIRS, result.getResult());
            assertEquals("3H 3D 2H 2C", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 2C 3D 3H 3C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.FULLHOUSE, result.getResult());
            assertEquals("3H 3D 3C 2H 2C", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("3H 3C 3D 2H 2C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.FULLHOUSE, result.getResult());
            assertEquals("3H 3D 3C 2H 2C", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("3H 3C 3D 2H 2C 3S AC");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.QUADS, result.getResult());
            assertEquals("3H 3S 3D 3C", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 2S 3H 3S 4H 4D 10D");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.TWOPAIRS, result.getResult());
            assertEquals("4H 4D 3H 3S", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 2S 3H 3S 4H 4D 4S QC");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.FULLHOUSE, result.getResult());
            assertEquals("4H 4S 4D 3H 3S", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 2S 2H 3S KD AC 3D");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.FULLHOUSE, result.getResult());
            assertEquals("3S 3D 2H 2H 2S", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2H 4H 5H 7H 8H 2S 10C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.FLUSH, result.getResult());
            assertEquals("8H 7H 5H 4H 2H", result.getCombination().toString());
        }
    }
}