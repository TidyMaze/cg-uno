package com.codingame.game;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameEngine {
    static List<Card> getValidActions(State state, int currentPlayer) {
        ArrayList actions = new ArrayList<Card>();

        List<Card> hand = state.hands.get(currentPlayer);

        for (Card c : hand) {
            if (canPlayCard(state, c)) {
                actions.add(c);
            }
        }

        return actions;
    }

    private static boolean canPlayCard(State state, Card c) {
        Card lastPlayed = state.discardPile.get(state.discardPile.size() - 1);

        return isSameColor(lastPlayed, c) || isSameSymbol(c, lastPlayed);
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
}
