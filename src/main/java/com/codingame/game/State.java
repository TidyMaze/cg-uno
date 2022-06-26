package com.codingame.game;

import com.codingame.gameengine.core.MultiplayerGameManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class State {
    Deck deck;
    List<Card> discardPile;

    List<List<Card>> hands;

    Optional<Action> lastAction = Optional.empty();

    Rotation rotation = Rotation.CLOCKWISE;
    public int nextPlayer;

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
                .add("lastAction=" + lastAction)
                .add("rotation=" + rotation)
                .add("nextPlayer=" + nextPlayer)
                .toString();
    }

    void drawToDiscardPile() {
        discardPile.addAll(deck.draw(1));
    }

    public List<Card> draw(MultiplayerGameManager gameManager, int count) {
        if ((deck.size() + Math.max(0, discardPile.size() - 1)) < count) {
            System.out.println("Not enough cards in deck and discard pile");
            throw new NotEnoughCardsException();
        }

        if (deck.size() < count) {
            System.out.println(String.format("Deck is too small (%d < %d), drawing from discard pile (%d)", deck.size(), count, discardPile.size()));

            List<Card> allButLastDiscarded = new ArrayList<>(discardPile.subList(0, discardPile.size() - 1));
            deck.addAll(allButLastDiscarded);
            discardPile = new ArrayList<>(discardPile.subList(discardPile.size() - 1, discardPile.size()));
            deck.shuffle(gameManager);

            System.out.println(String.format(String.format("Deck is now %d, discard pile is now %d", deck.size(), discardPile.size())));
            System.out.println(String.format(String.format("Players hands are now %d and %d", hands.get(0).size(), hands.get(1).size())));
        }
        return deck.draw(count);
    }

    public void setNextPlayer(int playerId) {
        this.nextPlayer = playerId;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }
}

enum Rotation {
    CLOCKWISE(1),
    COUNTER_CLOCKWISE(-1);


    final int offset;

    Rotation(int offset) {
        this.offset = offset;
    }
}