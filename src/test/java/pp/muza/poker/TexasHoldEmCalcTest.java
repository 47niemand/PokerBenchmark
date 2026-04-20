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
        {
            // straight flush where highest hand card (AS) has a different suit — regression for flush-suit bug
            CardStack cards = CardStack.parseCards("AS 2H 3H 4H 5H 6H 7H");
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
            assertEquals("KS QS 10D 9D 7H", result.getCombination().toString());
        }
        {
            // This hand contains A-5-4-3-2 (wheel straight), which beats a pair of 2s
            CardStack cards = CardStack.parseCards("AD 5C 10S 2H 4S 3H 2C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHT, result.getResult());
            assertEquals("5C 4S 3H 2H AD", result.getCombination().toString());
        }
        {
            // Pair of 2s (no wheel: missing the 5)
            CardStack cards = CardStack.parseCards("AD 6C 10S 2H 4S 3H 2C");
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
        {
            CardStack cards = CardStack.parseCards("2C 4H 5H 7H 8H 2H 10C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.FLUSH, result.getResult());
            assertEquals("8H 7H 5H 4H 2H", result.getCombination().toString());
        }
        {
            CardStack cards = CardStack.parseCards("2C 4D 5H 7H 8H 2H 10H");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.FLUSH, result.getResult());
            assertEquals("10H 8H 7H 5H 2H", result.getCombination().toString());
        }
    }

    @Test
    public void getResultWheel() {
        // Ace-low straight (wheel): A-2-3-4-5
        {
            CardStack cards = CardStack.parseCards("AH 2D 3C 4S 5H 9D KC");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHT, result.getResult());
            assertEquals("5H 4S 3C 2D AH", result.getCombination().toString());
        }
        // Wheel straight flush
        {
            CardStack cards = CardStack.parseCards("AH 2H 3H 4H 5H 9D KC");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHTFLUSH, result.getResult());
            assertEquals("5H 4H 3H 2H AH", result.getCombination().toString());
        }
        // Higher straight beats wheel
        {
            CardStack cards = CardStack.parseCards("AH 2D 3C 4S 5H 6D 7C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHT, result.getResult());
            assertEquals("7C 6D 5H 4S 3C", result.getCombination().toString());
        }
    }

    @Test
    public void getResultStraightFlushOverStraight() {
        // Lower straight flush beats higher non-flush straight
        {
            CardStack cards = CardStack.parseCards("9S 8S 7H 6H 5H 4H 3H");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHTFLUSH, result.getResult());
            assertEquals("7H 6H 5H 4H 3H", result.getCombination().toString());
        }
    }

    @Test
    public void getResultStraightWithDuplicateValues() {
        // Straight with duplicate values should not include both
        {
            CardStack cards = CardStack.parseCards("AH KS KD QH JH 10S 5C");
            PokerCalc result = TexasHoldEmCalc.calculateResult(cards, 0);
            System.out.println(result.toString());
            assertEquals(TexasHoldEmCalc.Result.STRAIGHT, result.getResult());
            assertEquals("AH KS QH JH 10S", result.getCombination().toString());
        }
    }

    @Test
    public void getResultFullHouseScoring() {
        // Full house where pair value > trips value: JJJ-KK should lose to QQQ-22
        {
            int scoreJJJKK = TexasHoldEmCalc.calculateResult(
                    CardStack.parseCards("JH JS JD KH KS 3C 2D"), 0).getScore();
            int scoreQQQ22 = TexasHoldEmCalc.calculateResult(
                    CardStack.parseCards("QH QS QD 2H 2S 3C 4D"), 1).getScore();
            System.out.println("JJJ-KK: " + scoreJJJKK + ", QQQ-22: " + scoreQQQ22);
            assertTrue("QQQ-22 should beat JJJ-KK", scoreQQQ22 > scoreJJJKK);
        }
    }

    @Test
    public void getScore() {
        {
            // Same flush: 10H 8H 7H 5H 4H — both contribute 4H to the flush
            int score1 = TexasHoldEmCalc.calculateResult(CardStack.parseCards("4H 3C 5H 7H 8H 2D 10H"), 0).getScore();
            int score2 = TexasHoldEmCalc.calculateResult(CardStack.parseCards("4H 3D 5H 7H 8H 2C 10H"), 1).getScore();

            System.out.println("score1: " + score1);
            System.out.println("score2: " + score2);
            assertEquals("same flush cards = equal score", score1, score2);
        }
        {
            // Better flush: 10H 8H 7H 5H 4H vs 10H 8H 7H 5H 2H (4 > 2 in 5th card)
            int score1 = TexasHoldEmCalc.calculateResult(CardStack.parseCards("2H 3C 5H 7H 8H 4D 10H"), 0).getScore();
            int score2 = TexasHoldEmCalc.calculateResult(CardStack.parseCards("4H 3C 5H 7H 8H 2D 10H"), 1).getScore();

            System.out.println("score1: " + score1);
            System.out.println("score2: " + score2);
            assertTrue("4H-flush > 2H-flush in 5th card", score2 > score1);
        }
    }

    // --- Winner determination tests based on score comparison ---

    private int score(String holeCards, String community) {
        CardStack cards = CardStack.parseCards(holeCards + " " + community);
        return TexasHoldEmCalc.calculateResult(cards, 0).getScore();
    }

    @Test
    public void winnerHandRankHierarchy() {
        // Each hand rank must beat all lower ranks
        String community = "7S 8D 9C JH 2D";

        int highCard   = score("3H 4C", community);   // high card J
        int onePair    = score("2H 3C", community);   // pair of 2s
        int twoPair    = score("7H 8C", community);   // 7s and 8s
        int trips      = score("7H 7C", community);   // trip 7s
        int straight   = score("10H 6C", community);  // 6-7-8-9-10
        int flush      = score("3S 4S", "7S 8S 9S JH 2D"); // flush in spades
        int fullHouse  = score("7H 7C", "7S 8D 8C JH 2D"); // 7s full of 8s
        int quads      = score("7H 7C", "7S 7D 8C JH 2D"); // quad 7s
        int straightFl = score("5S 6S", "7S 8S 9S JH 2D"); // 5-9 straight flush

        assertTrue("pair > high card", onePair > highCard);
        assertTrue("two pair > pair", twoPair > onePair);
        assertTrue("trips > two pair", trips > twoPair);
        assertTrue("straight > trips", straight > trips);
        assertTrue("flush > straight", flush > straight);
        assertTrue("full house > flush", fullHouse > flush);
        assertTrue("quads > full house", quads > fullHouse);
        assertTrue("straight flush > quads", straightFl > quads);
    }

    @Test
    public void winnerHigherPairBeatsLowerPair() {
        // Same community, different hole pairs
        String community = "2D 5H 8C JH QS";
        int pairOfThrees = score("3H 3C", community);
        int pairOfNines  = score("9H 9C", community);
        int pairOfAces   = score("AH AC", community);

        assertTrue("99 > 33", pairOfNines > pairOfThrees);
        assertTrue("AA > 99", pairOfAces > pairOfNines);
    }

    @Test
    public void winnerTwoPairHigherTopPairWins() {
        // Both have two pair on same community
        String community = "4D 7H 10C KS 2D";
        int pair47  = score("4H 7C", community); // 7s and 4s
        int pair710 = score("7S 10D", community); // 10s and 7s
        int pairK10 = score("KH 10H", community); // Ks and 10s

        assertTrue("10-7 > 7-4", pair710 > pair47);
        assertTrue("K-10 > 10-7", pairK10 > pair710);
    }

    @Test
    public void winnerTripsHigherValueWins() {
        String community = "2S 5D 8H JC QS";
        int trip5s = score("5H 5C", community); // trip 5s
        int tripJs = score("JH JD", community); // trip Js

        assertTrue("JJJ > 555", tripJs > trip5s);
    }

    @Test
    public void winnerStraightHigherTopWins() {
        String community = "6H 7D 8S 9C KH";
        int straight9  = score("5H 3C", community); // 5-6-7-8-9
        int straight10 = score("10H 2C", community); // 6-7-8-9-10

        assertTrue("10-high straight > 9-high straight", straight10 > straight9);
    }

    @Test
    public void winnerWheelLosesToHigherStraight() {
        // Wheel (A-5) is the lowest straight
        String community = "2D 3H 4S 5C KH";
        int wheel      = score("AH 9C", community); // A-2-3-4-5
        int sixHigh    = score("6H 9C", community); // 2-3-4-5-6

        assertTrue("6-high straight > wheel", sixHigh > wheel);
    }

    @Test
    public void winnerFlushHigherCardsWin() {
        String community = "2H 5H 7H JC QD";
        int lowFlush  = score("3H 4H", community); // flush: 7 5 4 3 2
        int highFlush = score("9H KH", community); // flush: K 9 7 5 2

        assertTrue("K-high flush > 7-high flush", highFlush > lowFlush);
    }

    @Test
    public void winnerFullHouseTripsValueDetermines() {
        // Full house: trips rank is primary, pair rank is secondary
        String community = "4H 4D 9C KS QH";
        int fh_444KK = score("4C KH", community); // 444-KK
        int fh_KKK44 = score("KC KD", community); // KKK-44

        assertTrue("KKK-44 > 444-KK", fh_KKK44 > fh_444KK);
    }

    @Test
    public void winnerFullHouseSameTrips_HigherPairWins() {
        String community = "QH QD QC 3S 7H";
        int fhQ3 = score("3H 2C", community); // QQQ-33
        int fhQ7 = score("7C 2C", community); // QQQ-77

        assertTrue("QQQ-77 > QQQ-33", fhQ7 > fhQ3);
    }

    @Test
    public void winnerQuadsHigherValueWins() {
        int quads3 = score("3H 3C", "3D 3S 8H JC KD"); // quad 3s
        int quadsK = score("KH KC", "KD KS 8H JC 2D"); // quad Ks

        assertTrue("KKKK > 3333", quadsK > quads3);
    }

    @Test
    public void winnerStraightFlushHigherTopWins() {
        int sf5 = score("AH 2H", "3H 4H 5H JC KD");  // wheel SF (5-high)
        int sf9 = score("5S 6S", "7S 8S 9S JC KD");   // 9-high SF

        assertTrue("9-high SF > 5-high SF (wheel)", sf9 > sf5);
    }

    @Test
    public void winnerStraightFlushBeatsQuads() {
        // Even a wheel straight flush beats quad aces
        int wheelSF  = score("AH 2H", "3H 4H 5H 9C KD");
        int quadAces = score("AS AC", "AD AH 5S 9C KD");

        // Straight flush has rank 9, quads has rank 8
        assertTrue("wheel SF > quad aces", wheelSF > quadAces);
    }

    @Test
    public void winnerSameResultKickerDecides() {
        // Both players make a pair on the board, hole cards differ
        String community = "KH 8D 5S 3C 2H";
        int pairK_lowKicker  = score("KD 4C", community); // pair Ks, hole: K+4
        int pairK_highKicker = score("KC AH", community); // pair Ks, hole: K+A

        assertTrue("pair Ks with A kicker > pair Ks with 4 kicker", pairK_highKicker > pairK_lowKicker);
    }

    @Test
    public void winnerBothPlayersShareCommunityFlush() {
        // Community has 4 hearts; each player contributes one heart
        String community = "2H 5H 7H JH QD";
        int playerLow  = score("3H KC", community); // flush: J 7 5 3 2
        int playerHigh = score("AH KC", community); // flush: A J 7 5 2

        assertTrue("A-high flush > J-high flush", playerHigh > playerLow);
    }

    @Test
    public void winnerBothPlayersShareCommunityStraight() {
        // Community: 5-6-7-8-9; player hole cards that extend the straight wins
        String community = "5H 6D 7C 8S 9H";
        int straight9  = score("2H 3C", community); // best: 5-9 straight
        int straight10 = score("10H 2C", community); // best: 6-10 straight

        assertTrue("10-high straight > 9-high straight", straight10 > straight9);
    }

    @Test
    public void winnerThreePairsOnBoard_BestTwoPairWin() {
        // 3 pairs possible, best two-pair is used
        String community = "4H 4D 9C 9S KH";
        int player1 = score("KD 2C", community); // KK + 99
        int player2 = score("2H 3C", community); // 99 + 44

        assertTrue("KK-99 > 99-44", player1 > player2);
    }

    @Test
    public void winnerTwoTripsOnBoard_BestFullHouseWins() {
        // Two sets of trips possible; best full house
        String community = "5H 5D 5C QS QH";
        int fh_555QQ = score("2H 3C", community); // 555-QQ
        int fh_QQQ55 = score("QD 2C", community); // QQQ-55

        assertTrue("QQQ-55 > 555-QQ", fh_QQQ55 > fh_555QQ);
    }

    @Test
    public void winnerHighCardAceBeatsKing() {
        String community = "2D 5H 7C 9S JH";
        int kingHigh = score("KH 3C", community);
        int aceHigh  = score("AH 3C", community);

        assertTrue("A-high > K-high", aceHigh > kingHigh);
    }

    @Test
    public void winnerRoyalFlushBeatsLowerStraightFlush() {
        int royalFlush = score("AH KH", "QH JH 10H 3C 5D"); // royal flush
        int sf9        = score("9H 8H", "7H 6H 5H 3C KD");   // 9-high SF

        assertTrue("royal flush > 9-high straight flush", royalFlush > sf9);
    }

    // --- Split pot (tie) tests: players have equal best 5-card hands ---

    @Test
    public void splitStraightOnBoard() {
        // Community makes the best straight; both players' hole cards are irrelevant (same value sum)
        String community = "6H 7D 8S 9C 10H";
        int player1 = score("2H 3C", community); // best hand: 6-7-8-9-10
        int player2 = score("2D 3S", community); // best hand: 6-7-8-9-10

        assertEquals("same board straight = split", player1, player2);
    }

    @Test
    public void splitFlushOnBoard() {
        // Community provides the best 5-card flush; hole cards can't improve it
        String community = "AH KH QH JH 9H";
        int player1 = score("2D 3C", community); // no hearts, board flush is best
        int player2 = score("2C 3S", community); // no hearts, board flush is best

        assertEquals("same board flush = split", player1, player2);
    }

    @Test
    public void splitStraightFlushOnBoard() {
        // Royal flush on board — everyone ties
        String community = "AH KH QH JH 10H";
        int player1 = score("2D 3C", community);
        int player2 = score("3D 2C", community);

        assertEquals("royal flush on board = split", player1, player2);
    }

    @Test
    public void splitPairOnBoard_SameKickers() {
        // Board pair with high kickers; both players have low useless cards
        String community = "AH AD KS QH JC";
        int player1 = score("2H 3C", community); // pair AA, board kickers K Q J
        int player2 = score("2D 3S", community); // pair AA, board kickers K Q J

        assertEquals("same board pair+kickers = split", player1, player2);
    }

    @Test
    public void splitTwoPairOnBoard() {
        // Board has two pair + high kicker, hole cards irrelevant
        String community = "AH AD KS KH QC";
        int player1 = score("2H 3C", community); // AA-KK with Q kicker
        int player2 = score("3D 2S", community); // AA-KK with Q kicker

        assertEquals("same board two pair = split", player1, player2);
    }

    @Test
    public void splitTripsOnBoard() {
        // Board trips with high kickers
        String community = "AH AD AS KH QC";
        int player1 = score("2H 3C", community); // AAA + K Q
        int player2 = score("2D 3S", community); // AAA + K Q

        assertEquals("same board trips = split", player1, player2);
    }

    @Test
    public void splitFullHouseOnBoard() {
        // Board makes full house
        String community = "AH AD AS KH KD";
        int player1 = score("2H 3C", community); // AAA-KK
        int player2 = score("3D 2S", community); // AAA-KK

        assertEquals("same board full house = split", player1, player2);
    }

    @Test
    public void splitQuadsOnBoard() {
        // Board has quads + high kicker
        String community = "AH AD AS AC KH";
        int player1 = score("2H 3C", community); // AAAA + K
        int player2 = score("2D 3S", community); // AAAA + K

        assertEquals("same board quads = split", player1, player2);
    }

   @Test
    public void splitQuadsOnBoard2() {
        // Board has quads + high kicker
        String community = "AH AD AS AC KH";
        int player1 = score("4H 2C", community); // AAAA + K
        int player2 = score("2D 3S", community); // AAAA + K

        assertEquals("same board quads = split", player1, player2);
    }    

    @Test
    public void splitBothPlayersHaveSamePocketPair() {
        // Both players have the same value pocket pair (different suits)
        String community = "2D 5H 8C JH QS";
        int player1 = score("KH KD", community); // pair Ks
        int player2 = score("KC KS", community); // pair Ks

        assertEquals("same pocket pair value = split", player1, player2);
    }

    @Test
    public void splitBothMakeFlushWithSameTopCards() {
        // Both players contribute one heart but same top-5 flush cards
        String community = "AH KH QH JH 2D";
        int player1 = score("3H 4C", community); // flush: A K Q J 3
        int player2 = score("3H 4S", community); // flush: A K Q J 3

        assertEquals("same flush top-5 = split", player1, player2);
    }

    @Test
    public void splitCounterfeitedPair() {
        // Player's pair is counterfeited by higher board pairs
        // Board: AA KK Q — both players' lower pairs are irrelevant
        String community = "AH AD KS KH QC";
        int player1 = score("3H 3C", community); // AA-KK (33 counterfeited)
        int player2 = score("4D 2C", community); // AA-KK (44 counterfeited, same kicker sum)

        assertEquals("counterfeited pairs = split (both play board)", player1, player2);
    }

    @Test
    public void splitWheelStraight() {
        // Both players make the wheel with same hole card values
        String community = "2H 3D 4S 5C KH";
        int player1 = score("AH 9C", community); // A-2-3-4-5
        int player2 = score("AD 9S", community); // A-2-3-4-5

        assertEquals("same wheel straight = split", player1, player2);
    }

    @Test
    public void splitBothMakeSameTrips() {
        // Both players pair with the board to make same trips
        String community = "JH JD 3S 5C 9H";
        int player1 = score("JC 2H", community); // JJJ
        int player2 = score("JS 2D", community); // JJJ

        assertEquals("same trips from board = split", player1, player2);
    }

    @Test
    public void splitHighCardBothPlayBoard() {
        // Board has the 5 highest cards, players' hole cards don't matter
        String community = "AH KD QS JC 9H";
        int player1 = score("2H 3C", community); // plays A K Q J 9
        int player2 = score("2D 3S", community); // plays A K Q J 9

        assertEquals("both play board high cards = split", player1, player2);
    }

    // ===== Texas Hold'em rule verification tests =====

    // --- Rule: High Card is determined by comparing cards top-down ---
    @Test
    public void ruleHighCard_SecondCardBreaksTie() {
        String community = "2D 5H 7C 9S JH";
        // Both have J-high, but second cards differ
        int playerQ = score("QH 3C", community); // J Q 9 7 5
        int playerK = score("KH 3C", community); // J K 9 7 5

        assertTrue("K second card > Q second card", playerK > playerQ);
    }

    @Test
    public void ruleHighCard_FifthCardBreaksTie() {
        String community = "AH KD QS JC 3H";
        // Both play A K Q J + 5th card from hole
        int player4 = score("4H 2C", community); // A K Q J 4
        int player8 = score("8H 2C", community); // A K Q J 8

        assertTrue("A-K-Q-J-8 > A-K-Q-J-4", player8 > player4);
    }

    // --- Rule: One Pair kickers are the next 3 highest cards ---
    @Test
    public void ruleOnePair_KickerFromBoard() {
        // Both have pair of Aces. Kickers come from board (K,Q,J).
        // Different hole cards don't matter since board kickers are higher
        String community = "AH 3D KS QH JC";
        int player1 = score("AC 4H", community); // AA with K Q J kickers
        int player2 = score("AD 5H", community); // AA with K Q J kickers

        assertEquals("same pair + same top 3 kickers from board = split", player1, player2);
    }

    @Test
    public void ruleOnePair_ThirdKickerDecides() {
        // Same pair of Aces, same first 2 kickers (K,Q), 3rd kicker differs
        String community = "AH 3D KS QH 2C";
        int player5 = score("AC 5H", community); // AA + K Q 5
        int player9 = score("AD 9H", community); // AA + K Q 9

        assertTrue("AA-K-Q-9 > AA-K-Q-5", player9 > player5);
    }

    // --- Rule: Two Pair - compare high pair, then low pair, then kicker ---
    @Test
    public void ruleTwoPair_SameHighPair_LowPairDecides() {
        String community = "AH AD 5S 3C 9H";
        // Both have pair of Aces + another pair
        int pair33 = score("3H 3D", community); // AA-33 + 9 kicker
        int pair55 = score("5H 5D", community); // AA-55 + 9 kicker

        assertTrue("AA-55 > AA-33", pair55 > pair33);
    }

    @Test
    public void ruleTwoPair_SameTwoPair_KickerDecides() {
        // Same two pair (AA-KK), 5th card decides
        String community = "AH AD KS KH 2C";
        int kicker5 = score("5H 3C", community); // AA-KK with 5 kicker
        int kicker9 = score("9H 3C", community); // AA-KK with 9 kicker

        assertTrue("AA-KK-9 > AA-KK-5", kicker9 > kicker5);
    }

    @Test
    public void ruleTwoPair_SameTwoPair_SameKicker_Split() {
        // Same two pair AA-KK, kicker is Q from board
        String community = "AH AD KS KH QC";
        int player1 = score("5H 3C", community); // AA-KK-Q
        int player2 = score("7H 4C", community); // AA-KK-Q

        assertEquals("AA-KK-Q = AA-KK-Q = split", player1, player2);
    }

    // --- Rule: Three of a Kind - trips rank then 2 kickers ---
    @Test
    public void ruleTrips_KickerDecides() {
        String community = "JH JD JC 5S 3H";
        int kickerK = score("KH 2C", community); // JJJ + K 5
        int kickerA = score("AH 2C", community); // JJJ + A 5

        assertTrue("JJJ-A-5 > JJJ-K-5", kickerA > kickerK);
    }

    @Test
    public void ruleTrips_SecondKickerDecides() {
        String community = "JH JD JC AS 3H";
        // Both have JJJ+A, but second kicker differs
        int kicker5 = score("5H 2C", community); // JJJ + A 5
        int kicker9 = score("9H 2C", community); // JJJ + A 9

        assertTrue("JJJ-A-9 > JJJ-A-5", kicker9 > kicker5);
    }

    // --- Rule: Straight - only high card matters ---
    @Test
    public void ruleStraight_SameHigh_AlwaysSplit() {
        // Both make 9-high straight; different hole cards that also form the straight
        String community = "5H 6D 7S 8C KH";
        int player1 = score("9H 2C", community); // 5-6-7-8-9
        int player2 = score("9D 3C", community); // 5-6-7-8-9

        assertEquals("same straight = split regardless of suits", player1, player2);
    }

    // --- Rule: Flush - compare all 5 cards high to low ---
    @Test
    public void ruleFlush_SecondCardDecides() {
        String community = "AH 3H 5H 7H 9D";
        // Flush: A-x-7-5-3 where x differs
        int flush8 = score("8H 2C", community); // AH 8H 7H 5H 3H
        int flushK = score("KH 2C", community); // AH KH 7H 5H 3H

        assertTrue("A-K flush > A-8 flush", flushK > flush8);
    }

    @Test
    public void ruleFlush_FifthCardDecides() {
        String community = "AH KH QH 7H 2D";
        // Flush: A-K-Q-7-x where x is hole heart
        int flush3 = score("3H 2C", community); // AH KH QH 7H 3H
        int flush5 = score("5H 2C", community); // AH KH QH 7H 5H

        assertTrue("A-K-Q-7-5 flush > A-K-Q-7-3 flush", flush5 > flush3);
    }

    @Test
    public void ruleFlush_SameTopFive_Split() {
        // 6+ cards of same suit, both players contribute but same top 5
        String community = "AH KH QH JH 4H";
        int player1 = score("2H 3C", community); // flush: A K Q J 4 (2H doesn't make top 5)
        int player2 = score("3H 2C", community); // flush: A K Q J 4 (3H doesn't make top 5)

        assertEquals("same top-5 flush = split", player1, player2);
    }

    // --- Rule: Full House - trips value first, then pair value ---
    @Test
    public void ruleFullHouse_SameTrips_PairDecides() {
        String community = "QH QD QC 4S 7H";
        int fhQ4 = score("4H 2C", community); // QQQ-44
        int fhQ7 = score("7C 2C", community); // QQQ-77

        assertTrue("QQQ-77 > QQQ-44", fhQ7 > fhQ4);
    }

    @Test
    public void ruleFullHouse_NoKickerBeyondPair() {
        // Full house is fully determined by trips+pair; no 6th card matters
        String community = "QH QD QC 7S 7H";
        int player1 = score("2H 3C", community); // QQQ-77
        int player2 = score("AH KC", community); // QQQ-77 (A and K are irrelevant)

        assertEquals("full house has no kicker beyond trips+pair", player1, player2);
    }

    // --- Rule: Quads - quad value then kicker (best 5th card) ---
    @Test
    public void ruleQuads_KickerDecides() {
        String community = "9H 9D 9S 9C 3H";
        int kickerK = score("KH 2C", community); // 9999-K
        int kickerA = score("AH 2C", community); // 9999-A

        assertTrue("9999-A > 9999-K", kickerA > kickerK);
    }

    @Test
    public void ruleQuads_BoardKickerBeatsHoleKicker() {
        // Board kicker (A) is higher than any hole card
        String community = "9H 9D 9S 9C AH";
        int player1 = score("KH 2C", community); // 9999-A (board A is kicker)
        int player2 = score("QH 3C", community); // 9999-A (board A is kicker)

        assertEquals("quads with board kicker A = split", player1, player2);
    }

    // --- Rule: Straight Flush - only high card matters ---
    @Test
    public void ruleStraightFlush_SameHigh_Split() {
        String community = "5H 6H 7H 8H 2D";
        int player1 = score("9H 2C", community); // 5H-6H-7H-8H-9H
        int player2 = score("9H 3C", community); // 5H-6H-7H-8H-9H

        assertEquals("same straight flush = split", player1, player2);
    }

    // --- Rule: Board plays (all 5 community cards are the best hand) ---
    @Test
    public void ruleBoardPlays_HighCard() {
        String community = "AH KD QS JC 9H";
        // No player can improve on A-K-Q-J-9
        int player1 = score("8H 7C", community);
        int player2 = score("6H 5C", community);

        assertEquals("board plays high card = split", player1, player2);
    }

    @Test
    public void ruleBoardPlays_StraightOverrulesHolePair() {
        // Board makes a straight that beats any pair from hole cards
        String community = "6H 7D 8S 9C 10H";
        int player1 = score("2H 2C", community); // straight 6-10 (pair of 2 irrelevant)
        int player2 = score("AH KC", community); // straight 6-10

        // Both play the board straight; but player2's A can make J-high straight? No, no J.
        // Both best: 6-7-8-9-10
        assertEquals("board straight plays = split", player1, player2);
    }

    // --- Rule: Higher hand rank always wins, regardless of card values ---
    @Test
    public void ruleRankBeatsValue_LowFlushBeatsHighStraight() {
        // Lowest possible flush beats highest possible straight
        int straightAce = score("AH KD", "QS JC 10H 4D 2C"); // A-high straight
        int lowFlush    = score("2S 4S", "5S 7S 3S QH KD");   // 7-5-4-3-2 flush

        assertTrue("any flush > any straight", lowFlush > straightAce);
    }

    @Test
    public void ruleRankBeatsValue_LowFullHouseBeatsHighFlush() {
        int aceFlush    = score("AH KH", "QH JH 9H 4D 2C"); // A-K-Q-J-9 flush
        int lowFH       = score("2H 2C", "2D 3S 3H QC KD");  // 222-33

        assertTrue("any full house > any flush", lowFH > aceFlush);
    }

    // --- Rule: With multiple pairs available, best TWO pairs are used ---
    @Test
    public void ruleThreePairs_BestTwoSelected() {
        // Board gives 3 possible pairs, best two chosen
        String community = "3H 3D 7S 7H KH";
        int playerK = score("KC 2D", community); // KK-77 (best two pair)
        int playerA = score("AH AD", community); // AA-KK? No wait...

        // playerA has: AH AD 3H 3D 7S 7H KH → pairs: AA, 77, 33 → best two: AA-77 + K kicker
        // playerK has: KC 2D 3H 3D 7S 7H KH → pairs: KK, 77, 33 → best two: KK-77 + 3 kicker
        // Actually... let me recalculate. playerK: vals = K,K,7,7,3,3,2 → two pair KK-77 (kicker 3)
        // playerA: vals = A,A,7,7,3,3,K → two pair AA-77 (kicker K)
        assertTrue("AA-77 > KK-77", playerA > playerK);
    }

    // --- Rule: Kicker doesn't exist for straight/flush/full house/straight flush ---
    @Test
    public void ruleNoKickerForStraight() {
        // Both make same straight, one player has an Ace in hole (irrelevant)
        String community = "5H 6D 7S 8C 2H";
        int player1 = score("9H 3C", community); // straight 5-9
        int player2 = score("9D AH", community); // straight 5-9 (Ace is irrelevant)

        assertEquals("straight has no kicker", player1, player2);
    }

    @Test
    public void ruleNoKickerForFullHouse() {
        // Both make same full house, different side cards
        String community = "KH KD KC QS QH";
        int player1 = score("2H 3C", community); // KKK-QQ
        int player2 = score("AH AC", community); // KKK-QQ (AA pair doesn't help, QQ > AA? No, KKK already uses board K's and QQ uses board Q's)
        // Actually player2: AH AC KH KD KC QS QH → KKK + AA (AA > QQ!) so FULLHOUSE KKK-AA
        // Let me reconsider... trips K + pair A → KKK-AA is better than KKK-QQ

        assertTrue("KKK-AA > KKK-QQ", player2 > player1);
    }
}