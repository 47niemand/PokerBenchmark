package pp.muza.poker;

import pp.muza.cards.Card;
import pp.muza.cards.CardStack;

import java.util.ArrayList;
import java.util.List;

public final class TexasHoldEmGame {
    public static final int RIVER_MAX_SIZE = 5;
    public static final int HAND_MAX_SIZE = 2;
    private final List<CardStack> hands;
    private final CardStack river;
    private final CardStack stack;
    private final int playersCount;

    public TexasHoldEmGame(final int playersCount) {
        this.playersCount = playersCount;
        river = new CardStack(RIVER_MAX_SIZE);
        stack = CardStack.makeDefaultStack();
        hands = new ArrayList<>();
        for (int i = 0; i < playersCount; i++) {
            hands.add(new CardStack(HAND_MAX_SIZE));
        }
    }

    @Override
    public String toString() {
        return "PokerGame{" +
                "river=" + river +
                ", hands=" + hands +
                ", playersCount=" + playersCount +
                "}";
    }

    public void setupRiver(final CardStack riverSetup) {
        for (Card c : riverSetup) {
            if (river.isFull()) {
                break;
            }
            Card t = stack.takeFirst(c);
            if (t != null) {
                river.add(t);
            }
        }
    }

    public void setupPlayers(final CardStack playersSetup) {
        for (int playerIndex = 0; playerIndex < hands.size(); playerIndex++) {
            setupPlayer(playerIndex, playersSetup);
        }
    }

    public void setupPlayer(final int playerIndex, final CardStack playerSetup) {
        CardStack playerHand = hands.get(playerIndex);
        for (Card c : playerSetup) {
            if (playerHand.isFull()) {
                break;
            }
            Card t = stack.takeFirst(c);
            if (t != null) {
                playerHand.add(t);
            }
        }
    }

    void fillRiver() {
        while ((river.size() < river.getMaxSize())) {
            river.add(stack.pop());
        }
    }

    void fillHands() {
        for (CardStack h : hands) {
            while ((h.size() < h.getMaxSize())) {
                h.add(stack.pop());
            }
        }
    }

    public void reset() {
        stack.addAll(river);
        river.clear();
        for (CardStack h : hands) {
            stack.addAll(h);
            h.clear();
        }
        stack.toss();
    }

    public List<PokerCalc> run() {
        List<PokerCalc> calcList = new ArrayList<>();
        fillHands();
        fillRiver();
        for (int i = 0; i < hands.size(); i++) {
            CardStack s = new CardStack(HAND_MAX_SIZE + RIVER_MAX_SIZE);
            s.addAll(hands.get(i));
            s.addAll(river);
            PokerCalc result = TexasHoldEmCalc.getResult(s, i);
            calcList.add(result);
        }
        return calcList;
    }
}
