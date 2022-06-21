package com.codingame.game;

import java.util.List;
import java.util.StringJoiner;

public class State {
    Deck deck;
    List<Card> discardPile;

    List<List<Card>> hands;

    public State(Deck deck, List<Card> discardPile, List<List<Card>> hands) {
        this.deck = deck;
        this.discardPile = discardPile;
        this.hands = hands;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", State.class.getSimpleName() + "[", "]")
                .add("deck=" + deck)
                .add("discardPile=" + discardPile)
                .add("hands=" + hands)
                .toString();
    }

    void drawToDiscardPile() {
        discardPile.addAll(deck.draw(1));
    }
}
