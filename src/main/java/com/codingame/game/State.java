package com.codingame.game;

import com.codingame.gameengine.core.MultiplayerGameManager;

import java.util.ArrayList;
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

    public List<Card> draw(MultiplayerGameManager gameManager, int count) {
        if (deck.size() < count) {
            System.out.println(String.format("Deck is too small (%d < %d), drawing from discard pile (%d)", deck.size(), count, discardPile.size()));

            List<Card> allButLastDiscarded = new ArrayList<>(discardPile.subList(0, discardPile.size() - 1));
            deck.addAll(allButLastDiscarded);
            discardPile = new ArrayList<>(discardPile.subList(discardPile.size() - 1, discardPile.size()));
            deck.shuffle(gameManager);

            System.out.println(String.format(String.format("Deck is now %d, discard pile is now %d", deck.size(), discardPile.size())));
        }
        return deck.draw(count);
    }
}
