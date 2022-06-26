package com.codingame.game;

import com.codingame.gameengine.core.MultiplayerGameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameEngine {
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

        Optional<Color> cardColor = getCardColor(c);

        return isSameColor(lastPlayed, c) ||
                isSameSymbol(c, lastPlayed) ||
                c instanceof WildCard ||
                c instanceof WildDrawFourCard ||
                (state.lastAction.isPresent() && state.lastAction.get() instanceof WildAction && cardColor.isPresent() && cardColor.get() == ((WildAction) state.lastAction.get()).color) ||
                (state.lastAction.isPresent() && state.lastAction.get() instanceof WildDrawFourAction && cardColor.isPresent() && cardColor.get() == ((WildDrawFourAction) state.lastAction.get()).color);
    }

    private static boolean isSameColor(Card c1, Card c2) {
        Optional<Color> color1 = getCardColor(c1);
        Optional<Color> color2 = getCardColor(c2);
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

    private static Optional<Color> getCardColor(Card c) {
        if (c instanceof NumberCard) {
            return Optional.of(((NumberCard) c).getColor());
        } else if (c instanceof SkipCard) {
            return Optional.of(((SkipCard) c).getColor());
        } else if (c instanceof ReverseCard) {
            return Optional.of(((ReverseCard) c).getColor());
        } else if (c instanceof DrawTwoCard) {
            return Optional.of(((DrawTwoCard) c).getColor());
        } else {
            return Optional.empty();
        }
    }

    public static void playAction(State state, int playerIndex, Action action, MultiplayerGameManager gameManager) {

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

        int currentNextPlayerIndex = nextPlayerIndex(state.rotation, playerIndex, false, gameManager.getPlayerCount());

        if (action instanceof SimpleAction && ((SimpleAction) action).card instanceof DrawTwoCard) {
            int nextPlayer = currentNextPlayerIndex;
            state.hands.get(nextPlayer).addAll(state.draw(gameManager, 2));
            skipNextPlayer = true;
        } else if (action instanceof SimpleAction && ((SimpleAction) action).card instanceof SkipCard) {
            skipNextPlayer = true;
        } else if (action instanceof SimpleAction && ((SimpleAction) action).card instanceof ReverseCard) {
            state.setRotation(state.rotation.equals(Rotation.CLOCKWISE) ? Rotation.COUNTER_CLOCKWISE : Rotation.CLOCKWISE);
        } else if (action instanceof WildAction) {
            // nothing
        } else if (action instanceof WildDrawFourAction) {
            int nextPlayer = currentNextPlayerIndex;
            state.hands.get(nextPlayer).addAll(state.draw(gameManager, 4));
            skipNextPlayer = true;
        }

        state.lastAction = Optional.of(action);
        state.setNextPlayer(nextPlayerIndex(state.rotation, playerIndex, skipNextPlayer, gameManager.getPlayerCount()));
    }

    static int nextPlayerIndex(Rotation rotation, int playerIndex, boolean firstSkipped, int playerCount) {
        int res = (playerIndex + (rotation.equals(Rotation.CLOCKWISE) ? 1 : -1) * (firstSkipped ? 2 : 1)) % playerCount;
        if (res < 0) {
            res += playerCount;
        }
        return res;
    }
}
