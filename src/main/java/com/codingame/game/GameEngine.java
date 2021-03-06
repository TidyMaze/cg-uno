package com.codingame.game;

import com.codingame.game.models.Color;
import com.codingame.game.models.Rotation;
import com.codingame.game.models.State;
import com.codingame.game.models.actions.Action;
import com.codingame.game.models.actions.SimpleAction;
import com.codingame.game.models.actions.WildAction;
import com.codingame.game.models.actions.WildDrawFourAction;
import com.codingame.game.models.cards.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class GameEngine {

    int playerCount;
    Random random;

    GameEngineListener listener;

    public GameEngine(int playerCount, Random random, GameEngineListener listener) {
        this.playerCount = playerCount;
        this.random = random;
        this.listener = listener;
    }

    static List<Action> getValidActions(State state, int currentPlayer) {
        ArrayList<Action> actions = new ArrayList<Action>();

        List<Card> hand = state.hands.get(currentPlayer);

        for (Card c : hand) {
            if (canPlayCard(state, c)) {
                if (c instanceof WildCard) {
                    for (Color color : Color.values()) {
                        actions.add(new WildAction(color));
                    }
                } else if (c instanceof WildDrawFourCard) {
                    for (Color color : Color.values()) {
                        actions.add(new WildDrawFourAction(color));
                    }
                } else {
                    actions.add(new SimpleAction(c));
                }
            }
        }

        return actions;
    }

    private static boolean canPlayCard(State state, Card c) {
        Card lastPlayed = state.discardPile.get(state.discardPile.size() - 1);

        Optional<Color> cardColor = c.getCardColor();

        return isSameColor(lastPlayed, c) || isSameSymbol(c, lastPlayed) || c instanceof WildCard || c instanceof WildDrawFourCard || (state.lastAction.isPresent() && state.lastAction.get() instanceof WildAction && cardColor.isPresent() && cardColor.get() == ((WildAction) state.lastAction.get()).color) || (state.lastAction.isPresent() && state.lastAction.get() instanceof WildDrawFourAction && cardColor.isPresent() && cardColor.get() == ((WildDrawFourAction) state.lastAction.get()).color);
    }

    private static boolean isSameColor(Card c1, Card c2) {
        Optional<Color> color1 = c1.getCardColor();
        Optional<Color> color2 = c2.getCardColor();
        return color1.isPresent() && color2.isPresent() && color1.get() == color2.get();
    }

    private static boolean isSameSymbol(Card c1, Card c2) {
        if (c1 instanceof NumberCard && c2 instanceof NumberCard) {
            NumberCard nc1 = (NumberCard) c1;
            NumberCard nc2 = (NumberCard) c2;
            return nc1.getValue() == nc2.getValue();
        } else if (c1 instanceof SkipCard && c2 instanceof SkipCard) {
            return true;
        } else if (c1 instanceof ReverseCard && c2 instanceof ReverseCard) {
            return true;
        } else if (c1 instanceof DrawTwoCard && c2 instanceof DrawTwoCard) {
            return true;
        } else if (c1 instanceof WildCard && c2 instanceof WildCard) {
            return true;
        } else if (c1 instanceof WildDrawFourCard && c2 instanceof WildDrawFourCard) {
            return true;
        }
        // other
        return false;
    }

    public void playAction(State state, Action action) {

        int playerIndex = state.nextPlayer;

        Card card;
        if (action instanceof SimpleAction) {
            card = ((SimpleAction) action).card;
        } else if (action instanceof WildAction) {
            card = new WildCard();
        } else if (action instanceof WildDrawFourAction) {
            card = new WildDrawFourCard();
        } else {
            throw new IllegalArgumentException("Unknown action type: " + action.getClass());
        }

        boolean found = state.hands.get(playerIndex).remove(card);
        assert found;
        state.discardPile.add(card);

        boolean skipNextPlayer = false;

        int currentNextPlayerIndex = nextPlayerIndex(state.rotation, playerIndex, false);

        if (action instanceof SimpleAction && ((SimpleAction) action).card instanceof DrawTwoCard) {
            state.hands.get(currentNextPlayerIndex).addAll(state.draw(2, random));
            skipNextPlayer = true;
            listener.onDrawTwo(playerIndex);
        } else if (action instanceof SimpleAction && ((SimpleAction) action).card instanceof SkipCard) {
            skipNextPlayer = true;
            listener.onSkip(playerIndex);
        } else if (action instanceof SimpleAction && ((SimpleAction) action).card instanceof ReverseCard) {
            state.setRotation(state.rotation.equals(Rotation.CLOCKWISE) ? Rotation.COUNTER_CLOCKWISE : Rotation.CLOCKWISE);
            listener.onReverse(playerIndex);
        } else if (action instanceof WildAction) {
            // nothing
        } else if (action instanceof WildDrawFourAction) {
            state.hands.get(currentNextPlayerIndex).addAll(state.draw(4, random));
            skipNextPlayer = true;
            listener.onWildDrawFour(playerIndex);
        }

        state.lastAction = Optional.of(action);
        state.setNextPlayer(nextPlayerIndex(state.rotation, playerIndex, skipNextPlayer));
    }

    int nextPlayerIndex(Rotation rotation, int playerIndex, boolean firstSkipped) {
        int res = (playerIndex + (rotation.equals(Rotation.CLOCKWISE) ? 1 : -1) * (firstSkipped ? 2 : 1)) % playerCount;
        if (res < 0) {
            res += playerCount;
        }
        return res;
    }

    static int computeScore(List<List<Card>> hands, int playerIndex) {
        int score = 0;
        for (int iHand = 0; iHand < hands.size(); iHand++) {
            if (iHand != playerIndex) {
                List<Card> hand = hands.get(iHand);
                for (Card card : hand) {
                    score += card.getScore();
                }

            }
        }
        return score;
    }

    static boolean playerWon(List<Card> hand) {
        return hand.size() == 0;
    }

    public Card drawOne(State state) {
        return state.draw(1, random).get(0);
    }
}
