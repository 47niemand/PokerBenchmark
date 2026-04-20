package pp.muza.poker;

import org.junit.Test;
import pp.muza.cards.CardStack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TexasHoldEmCalcTest {

    private PokerCalc calc(String cards) {
        return TexasHoldEmCalc.calculateResult(CardStack.parseCards(cards), 0);
    }

    private int score(String holeCards, String community) {
        return calc(holeCards + " " + community).getScore();
    }

    private void assertHand(String cards, PokerCalc.Result expectedResult, String expectedCombination) {
        PokerCalc result = calc(cards);
        assertEquals(expectedResult, result.getResult());
        assertEquals(expectedCombination, result.getCombination().toString());
    }

    private void assertBeats(String message, int strongerScore, int weakerScore) {
        assertTrue(message, strongerScore > weakerScore);
    }

    private void assertSplit(String message, int score1, int score2) {
        assertEquals(message, score1, score2);
    }

    @Test
    public void getResultStraight() {
        assertHand("2S 3C 5H 6H 7H 8S 9D", PokerCalc.Result.STRAIGHT, "9D 8S 7H 6H 5H");
        assertHand("2H 3H 4H 5H 6H 7H TH", PokerCalc.Result.STRAIGHTFLUSH, "7H 6H 5H 4H 3H");
        // straight flush where highest hand card (AS) has a different suit
        assertHand("AS 2H 3H 4H 5H 6H 7H", PokerCalc.Result.STRAIGHTFLUSH, "7H 6H 5H 4H 3H");
    }

    @Test
    public void getResult() {
        assertHand("3H 2D QS KS 10D 9D 7H", PokerCalc.Result.HIGHCARD, "KS QS 10D 9D 7H");
        // This hand contains A-5-4-3-2 (wheel straight), which beats a pair of 2s
        assertHand("AD 5C 10S 2H 4S 3H 2C", PokerCalc.Result.STRAIGHT, "5C 4S 3H 2H AD");
        // Pair of 2s (no wheel: missing the 5)
        assertHand("AD 6C 10S 2H 4S 3H 2C", PokerCalc.Result.ONEPAIR, "2H 2C");
        assertHand("2H 2C 2D AS", PokerCalc.Result.THREEOFAKIND, "2H 2D 2C");
        assertHand("2H 2C 2D 2S 1C", PokerCalc.Result.QUADS, "2H 2S 2D 2C");
        assertHand("2H 2C 3D 3H", PokerCalc.Result.TWOPAIRS, "3H 3D 2H 2C");
        assertHand("2H 2C 3D 3H 3C", PokerCalc.Result.FULLHOUSE, "3H 3D 3C 2H 2C");
        assertHand("3H 3C 3D 2H 2C", PokerCalc.Result.FULLHOUSE, "3H 3D 3C 2H 2C");
        assertHand("3H 3C 3D 2H 2C 3S AC", PokerCalc.Result.QUADS, "3H 3S 3D 3C");
        assertHand("2H 2S 3H 3S 4H 4D 10D", PokerCalc.Result.TWOPAIRS, "4H 4D 3H 3S");
        assertHand("2H 2S 3H 3S 4H 4D 4S QC", PokerCalc.Result.FULLHOUSE, "4H 4S 4D 3H 3S");
        assertHand("2H 2S 2H 3S KD AC 3D", PokerCalc.Result.FULLHOUSE, "3S 3D 2H 2H 2S");
        assertHand("2H 4H 5H 7H 8H 2S 10C", PokerCalc.Result.FLUSH, "8H 7H 5H 4H 2H");
        assertHand("2C 4H 5H 7H 8H 2H 10C", PokerCalc.Result.FLUSH, "8H 7H 5H 4H 2H");
        assertHand("2C 4D 5H 7H 8H 2H 10H", PokerCalc.Result.FLUSH, "10H 8H 7H 5H 2H");
    }

    @Test
    public void getResultWheel() {
        assertHand("AH 2D 3C 4S 5H 9D KC", PokerCalc.Result.STRAIGHT, "5H 4S 3C 2D AH");
        assertHand("AH 2H 3H 4H 5H 9D KC", PokerCalc.Result.STRAIGHTFLUSH, "5H 4H 3H 2H AH");
        assertHand("AH 2D 3C 4S 5H 6D 7C", PokerCalc.Result.STRAIGHT, "7C 6D 5H 4S 3C");
    }

    @Test
    public void getResultStraightFlushOverStraight() {
        assertHand("9S 8S 7H 6H 5H 4H 3H", PokerCalc.Result.STRAIGHTFLUSH, "7H 6H 5H 4H 3H");
    }

    @Test
    public void getResultStraightWithDuplicateValues() {
        assertHand("AH KS KD QH JH 10S 5C", PokerCalc.Result.STRAIGHT, "AH KS QH JH 10S");
    }

    @Test
    public void getResultFullHouseScoring() {
        int scoreJJJKK = score("JH JS", "JD KH KS 3C 2D");
        int scoreQQQ22 = score("QH QS", "QD 2H 2S 3C 4D");
        assertBeats("QQQ-22 should beat JJJ-KK", scoreQQQ22, scoreJJJKK);
    }

    @Test
    public void getScore() {
        int score1 = score("4H 3C", "5H 7H 8H 2D 10H");
        int score2 = score("4H 3D", "5H 7H 8H 2C 10H");
        assertSplit("same flush cards = equal score", score1, score2);

        int betterFlush = score("4H 3C", "5H 7H 8H 2D 10H");
        int weakerFlush = score("2H 3C", "5H 7H 8H 4D 10H");
        assertBeats("4H-flush > 2H-flush in 5th card", betterFlush, weakerFlush);
    }

    @Test
    public void winnerHandRankHierarchy() {
        String community = "7S 8D 9C JH 2D";

        int highCard = score("3H 4C", community);
        int onePair = score("2H 3C", community);
        int twoPair = score("7H 8C", community);
        int trips = score("7H 7C", community);
        int straight = score("10H 6C", community);
        int flush = score("3S 4S", "7S 8S 9S JH 2D");
        int fullHouse = score("7H 7C", "7S 8D 8C JH 2D");
        int quads = score("7H 7C", "7S 7D 8C JH 2D");
        int straightFl = score("5S 6S", "7S 8S 9S JH 2D");

        assertBeats("pair > high card", onePair, highCard);
        assertBeats("two pair > pair", twoPair, onePair);
        assertBeats("trips > two pair", trips, twoPair);
        assertBeats("straight > trips", straight, trips);
        assertBeats("flush > straight", flush, straight);
        assertBeats("full house > flush", fullHouse, flush);
        assertBeats("quads > full house", quads, fullHouse);
        assertBeats("straight flush > quads", straightFl, quads);
    }

    @Test
    public void winnerHigherPairBeatsLowerPair() {
        String community = "2D 5H 8C JH QS";
        int pairOfThrees = score("3H 3C", community);
        int pairOfNines = score("9H 9C", community);
        int pairOfAces = score("AH AC", community);

        assertBeats("99 > 33", pairOfNines, pairOfThrees);
        assertBeats("AA > 99", pairOfAces, pairOfNines);
    }

    @Test
    public void winnerTwoPairHigherTopPairWins() {
        String community = "4D 7H 10C KS 2D";
        int pair47 = score("4H 7C", community);
        int pair710 = score("7S 10D", community);
        int pairK10 = score("KH 10H", community);

        assertBeats("10-7 > 7-4", pair710, pair47);
        assertBeats("K-10 > 10-7", pairK10, pair710);
    }

    @Test
    public void winnerTripsHigherValueWins() {
        String community = "2S 5D 8H JC QS";
        int trip5s = score("5H 5C", community);
        int tripJs = score("JH JD", community);

        assertBeats("JJJ > 555", tripJs, trip5s);
    }

    @Test
    public void winnerStraightHigherTopWins() {
        String community = "6H 7D 8S 9C KH";
        int straight9 = score("5H 3C", community);
        int straight10 = score("10H 2C", community);

        assertBeats("10-high straight > 9-high straight", straight10, straight9);
    }

    @Test
    public void winnerWheelLosesToHigherStraight() {
        String community = "2D 3H 4S 5C KH";
        int wheel = score("AH 9C", community);
        int sixHigh = score("6H 9C", community);

        assertBeats("6-high straight > wheel", sixHigh, wheel);
    }

    @Test
    public void winnerFlushHigherCardsWin() {
        String community = "2H 5H 7H JC QD";
        int lowFlush = score("3H 4H", community);
        int highFlush = score("9H KH", community);

        assertBeats("K-high flush > 7-high flush", highFlush, lowFlush);
    }

    @Test
    public void winnerFullHouseTripsValueDetermines() {
        String community = "4H 4D 9C KS QH";
        int fh444KK = score("4C KH", community);
        int fhKKK44 = score("KC KD", community);

        assertBeats("KKK-44 > 444-KK", fhKKK44, fh444KK);
    }

    @Test
    public void winnerFullHouseSameTrips_HigherPairWins() {
        String community = "QH QD QC 3S 7H";
        int fhQ3 = score("3H 2C", community);
        int fhQ7 = score("7C 2C", community);

        assertBeats("QQQ-77 > QQQ-33", fhQ7, fhQ3);
    }

    @Test
    public void winnerQuadsHigherValueWins() {
        int quads3 = score("3H 3C", "3D 3S 8H JC KD");
        int quadsK = score("KH KC", "KD KS 8H JC 2D");

        assertBeats("KKKK > 3333", quadsK, quads3);
    }

    @Test
    public void winnerStraightFlushHigherTopWins() {
        int sf5 = score("AH 2H", "3H 4H 5H JC KD");
        int sf9 = score("5S 6S", "7S 8S 9S JC KD");

        assertBeats("9-high SF > 5-high SF (wheel)", sf9, sf5);
    }

    @Test
    public void winnerStraightFlushBeatsQuads() {
        int wheelSF = score("AH 2H", "3H 4H 5H 9C KD");
        int quadAces = score("AS AC", "AD AH 5S 9C KD");

        assertBeats("wheel SF > quad aces", wheelSF, quadAces);
    }

    @Test
    public void winnerSameResultKickerDecides() {
        String community = "KH 8D 5S 3C 2H";
        int pairKLowKicker = score("KD 4C", community);
        int pairKHighKicker = score("KC AH", community);

        assertBeats("pair Ks with A kicker > pair Ks with 4 kicker", pairKHighKicker, pairKLowKicker);
    }

    @Test
    public void winnerBothPlayersShareCommunityFlush() {
        String community = "2H 5H 7H JH QD";
        int playerLow = score("3H KC", community);
        int playerHigh = score("AH KC", community);

        assertBeats("A-high flush > J-high flush", playerHigh, playerLow);
    }

    @Test
    public void winnerBothPlayersShareCommunityStraight() {
        String community = "5H 6D 7C 8S 9H";
        int straight9 = score("2H 3C", community);
        int straight10 = score("10H 2C", community);

        assertBeats("10-high straight > 9-high straight", straight10, straight9);
    }

    @Test
    public void winnerThreePairsOnBoard_BestTwoPairWin() {
        String community = "4H 4D 9C 9S KH";
        int player1 = score("KD 2C", community);
        int player2 = score("2H 3C", community);

        assertBeats("KK-99 > 99-44", player1, player2);
    }

    @Test
    public void winnerTwoTripsOnBoard_BestFullHouseWins() {
        String community = "5H 5D 5C QS QH";
        int fh555QQ = score("2H 3C", community);
        int fhQQQ55 = score("QD 2C", community);

        assertBeats("QQQ-55 > 555-QQ", fhQQQ55, fh555QQ);
    }

    @Test
    public void winnerHighCardAceBeatsKing() {
        String community = "2D 5H 7C 9S JH";
        int kingHigh = score("KH 3C", community);
        int aceHigh = score("AH 3C", community);

        assertBeats("A-high > K-high", aceHigh, kingHigh);
    }

    @Test
    public void winnerRoyalFlushBeatsLowerStraightFlush() {
        int royalFlush = score("AH KH", "QH JH 10H 3C 5D");
        int sf9 = score("9H 8H", "7H 6H 5H 3C KD");

        assertBeats("royal flush > 9-high straight flush", royalFlush, sf9);
    }

    @Test
    public void splitStraightOnBoard() {
        String community = "6H 7D 8S 9C 10H";
        int player1 = score("2H 3C", community);
        int player2 = score("2D 3S", community);

        assertSplit("same board straight = split", player1, player2);
    }

    @Test
    public void splitFlushOnBoard() {
        String community = "AH KH QH JH 9H";
        int player1 = score("2D 3C", community);
        int player2 = score("2C 3S", community);

        assertSplit("same board flush = split", player1, player2);
    }

    @Test
    public void splitStraightFlushOnBoard() {
        String community = "AH KH QH JH 10H";
        int player1 = score("2D 3C", community);
        int player2 = score("3D 2C", community);

        assertSplit("royal flush on board = split", player1, player2);
    }

    @Test
    public void splitPairOnBoard_SameKickers() {
        String community = "AH AD KS QH JC";
        int player1 = score("2H 3C", community);
        int player2 = score("2D 3S", community);

        assertSplit("same board pair+kickers = split", player1, player2);
    }

    @Test
    public void splitTwoPairOnBoard() {
        String community = "AH AD KS KH QC";
        int player1 = score("2H 3C", community);
        int player2 = score("3D 2S", community);

        assertSplit("same board two pair = split", player1, player2);
    }

    @Test
    public void splitTripsOnBoard() {
        String community = "AH AD AS KH QC";
        int player1 = score("2H 3C", community);
        int player2 = score("2D 3S", community);

        assertSplit("same board trips = split", player1, player2);
    }

    @Test
    public void splitFullHouseOnBoard() {
        String community = "AH AD AS KH KD";
        int player1 = score("2H 3C", community);
        int player2 = score("3D 2S", community);

        assertSplit("same board full house = split", player1, player2);
    }

    @Test
    public void splitQuadsOnBoard() {
        String community = "AH AD AS AC KH";
        int player1 = score("2H 3C", community);
        int player2 = score("2D 3S", community);

        assertSplit("same board quads = split", player1, player2);
    }

    @Test
    public void splitQuadsOnBoard2() {
        String community = "AH AD AS AC KH";
        int player1 = score("4H 2C", community);
        int player2 = score("2D 3S", community);

        assertSplit("same board quads = split", player1, player2);
    }

    @Test
    public void splitBothPlayersHaveSamePocketPair() {
        String community = "2D 5H 8C JH QS";
        int player1 = score("KH KD", community);
        int player2 = score("KC KS", community);

        assertSplit("same pocket pair value = split", player1, player2);
    }

    @Test
    public void splitBothMakeFlushWithSameTopCards() {
        String community = "AH KH QH JH 2D";
        int player1 = score("3H 4C", community);
        int player2 = score("3H 4S", community);

        assertSplit("same flush top-5 = split", player1, player2);
    }

    @Test
    public void splitCounterfeitedPair() {
        String community = "AH AD KS KH QC";
        int player1 = score("3H 3C", community);
        int player2 = score("4D 2C", community);

        assertSplit("counterfeited pairs = split (both play board)", player1, player2);
    }

    @Test
    public void splitWheelStraight() {
        String community = "2H 3D 4S 5C KH";
        int player1 = score("AH 9C", community);
        int player2 = score("AD 9S", community);

        assertSplit("same wheel straight = split", player1, player2);
    }

    @Test
    public void splitBothMakeSameTrips() {
        String community = "JH JD 3S 5C 9H";
        int player1 = score("JC 2H", community);
        int player2 = score("JS 2D", community);

        assertSplit("same trips from board = split", player1, player2);
    }

    @Test
    public void splitHighCardBothPlayBoard() {
        String community = "AH KD QS JC 9H";
        int player1 = score("2H 3C", community);
        int player2 = score("2D 3S", community);

        assertSplit("both play board high cards = split", player1, player2);
    }

    @Test
    public void ruleHighCard_SecondCardBreaksTie() {
        String community = "2D 5H 7C 9S JH";
        int playerQ = score("QH 3C", community);
        int playerK = score("KH 3C", community);

        assertBeats("K second card > Q second card", playerK, playerQ);
    }

    @Test
    public void ruleHighCard_FifthCardBreaksTie() {
        String community = "AH KD QS JC 3H";
        int player4 = score("4H 2C", community);
        int player8 = score("8H 2C", community);

        assertBeats("A-K-Q-J-8 > A-K-Q-J-4", player8, player4);
    }

    @Test
    public void ruleOnePair_KickerFromBoard() {
        String community = "AH 3D KS QH JC";
        int player1 = score("AC 4H", community);
        int player2 = score("AD 5H", community);

        assertSplit("same pair + same top 3 kickers from board = split", player1, player2);
    }

    @Test
    public void ruleOnePair_ThirdKickerDecides() {
        String community = "AH 3D KS QH 2C";
        int player5 = score("AC 5H", community);
        int player9 = score("AD 9H", community);

        assertBeats("AA-K-Q-9 > AA-K-Q-5", player9, player5);
    }

    @Test
    public void ruleTwoPair_SameHighPair_LowPairDecides() {
        String community = "AH AD 5S 3C 9H";
        int pair33 = score("3H 3D", community);
        int pair55 = score("5H 5D", community);

        assertBeats("AA-55 > AA-33", pair55, pair33);
    }

    @Test
    public void ruleTwoPair_SameTwoPair_KickerDecides() {
        String community = "AH AD KS KH 2C";
        int kicker5 = score("5H 3C", community);
        int kicker9 = score("9H 3C", community);

        assertBeats("AA-KK-9 > AA-KK-5", kicker9, kicker5);
    }

    @Test
    public void ruleTwoPair_SameTwoPair_SameKicker_Split() {
        String community = "AH AD KS KH QC";
        int player1 = score("5H 3C", community);
        int player2 = score("7H 4C", community);

        assertSplit("AA-KK-Q = AA-KK-Q = split", player1, player2);
    }

    @Test
    public void ruleTrips_KickerDecides() {
        String community = "JH JD JC 5S 3H";
        int kickerK = score("KH 2C", community);
        int kickerA = score("AH 2C", community);

        assertBeats("JJJ-A-5 > JJJ-K-5", kickerA, kickerK);
    }

    @Test
    public void ruleTrips_SecondKickerDecides() {
        String community = "JH JD JC AS 3H";
        int kicker5 = score("5H 2C", community);
        int kicker9 = score("9H 2C", community);

        assertBeats("JJJ-A-9 > JJJ-A-5", kicker9, kicker5);
    }

    @Test
    public void ruleStraight_SameHigh_AlwaysSplit() {
        String community = "5H 6D 7S 8C KH";
        int player1 = score("9H 2C", community);
        int player2 = score("9D 3C", community);

        assertSplit("same straight = split regardless of suits", player1, player2);
    }

    @Test
    public void ruleFlush_SecondCardDecides() {
        String community = "AH 3H 5H 7H 9D";
        int flush8 = score("8H 2C", community);
        int flushK = score("KH 2C", community);

        assertBeats("A-K flush > A-8 flush", flushK, flush8);
    }

    @Test
    public void ruleFlush_FifthCardDecides() {
        String community = "AH KH QH 7H 2D";
        int flush3 = score("3H 2C", community);
        int flush5 = score("5H 2C", community);

        assertBeats("A-K-Q-7-5 flush > A-K-Q-7-3 flush", flush5, flush3);
    }

    @Test
    public void ruleFlush_SameTopFive_Split() {
        String community = "AH KH QH JH 4H";
        int player1 = score("2H 3C", community);
        int player2 = score("3H 2C", community);

        assertSplit("same top-5 flush = split", player1, player2);
    }

    @Test
    public void ruleFullHouse_SameTrips_PairDecides() {
        String community = "QH QD QC 4S 7H";
        int fhQ4 = score("4H 2C", community);
        int fhQ7 = score("7C 2C", community);

        assertBeats("QQQ-77 > QQQ-44", fhQ7, fhQ4);
    }

    @Test
    public void ruleFullHouse_NoKickerBeyondPair() {
        String community = "QH QD QC 7S 7H";
        int player1 = score("2H 3C", community);
        int player2 = score("AH KC", community);

        assertSplit("full house has no kicker beyond trips+pair", player1, player2);
    }

    @Test
    public void ruleQuads_KickerDecides() {
        String community = "9H 9D 9S 9C 3H";
        int kickerK = score("KH 2C", community);
        int kickerA = score("AH 2C", community);

        assertBeats("9999-A > 9999-K", kickerA, kickerK);
    }

    @Test
    public void ruleQuads_BoardKickerBeatsHoleKicker() {
        String community = "9H 9D 9S 9C AH";
        int player1 = score("KH 2C", community);
        int player2 = score("QH 3C", community);

        assertSplit("quads with board kicker A = split", player1, player2);
    }

    @Test
    public void ruleStraightFlush_SameHigh_Split() {
        String community = "5H 6H 7H 8H 2D";
        int player1 = score("9H 2C", community);
        int player2 = score("9H 3C", community);

        assertSplit("same straight flush = split", player1, player2);
    }

    @Test
    public void ruleBoardPlays_HighCard() {
        String community = "AH KD QS JC 9H";
        int player1 = score("8H 7C", community);
        int player2 = score("6H 5C", community);

        assertSplit("board plays high card = split", player1, player2);
    }

    @Test
    public void ruleBoardPlays_StraightOverrulesHolePair() {
        String community = "6H 7D 8S 9C 10H";
        int player1 = score("2H 2C", community);
        int player2 = score("AH KC", community);

        assertSplit("board straight plays = split", player1, player2);
    }

    @Test
    public void ruleRankBeatsValue_LowFlushBeatsHighStraight() {
        int straightAce = score("AH KD", "QS JC 10H 4D 2C");
        int lowFlush = score("2S 4S", "5S 7S 3S QH KD");

        assertBeats("any flush > any straight", lowFlush, straightAce);
    }

    @Test
    public void ruleRankBeatsValue_LowFullHouseBeatsHighFlush() {
        int aceFlush = score("AH KH", "QH JH 9H 4D 2C");
        int lowFH = score("2H 2C", "2D 3S 3H QC KD");

        assertBeats("any full house > any flush", lowFH, aceFlush);
    }

    @Test
    public void ruleThreePairs_BestTwoSelected() {
        String community = "3H 3D 7S 7H KH";
        int playerK = score("KC 2D", community);
        int playerA = score("AH AD", community);

        assertBeats("AA-77 > KK-77", playerA, playerK);
    }

    @Test
    public void ruleNoKickerForStraight() {
        String community = "5H 6D 7S 8C 2H";
        int player1 = score("9H 3C", community);
        int player2 = score("9D AH", community);

        assertSplit("straight has no kicker", player1, player2);
    }

    @Test
    public void ruleNoKickerForFullHouse() {
        String community = "KH KD KC QS QH";
        int player1 = score("2H 3C", community);
        int player2 = score("AH AC", community);

        assertBeats("KKK-AA > KKK-QQ", player2, player1);
    }
}
