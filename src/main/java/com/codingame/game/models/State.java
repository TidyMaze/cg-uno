package com.codingame.game.models;

import com.codingame.game.NotEnoughCardsException;
import com.codingame.game.models.actions.Action;
import com.codingame.game.models.cards.Card;

import java.util.*;

public class State {
    public Deck deck;
    public List<Card> discardPile;

    public List<List<Card>> hands;

    public Optional<Action> lastAction = Optional.empty();

    public Rotation rotation = Rotation.CLOCKWISE;
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

    public void drawToDiscardPile() {
        discardPile.addAll(deck.draw(1));
    }

    public List<Card> draw(int count, Random random) {
        if ((deck.size() + Math.max(0, discardPile.size() - 1)) < count) {
            System.out.println("Not enough cards in deck and discard pile");
            throw new NotEnoughCardsException();
        }

        if (deck.size() < count) {
            System.out.println(String.format("Deck is too small (%d < %d), drawing from discard pile (%d)", deck.size(), count, discardPile.size()));

            List<Card> allButLastDiscarded = new ArrayList<>(discardPile.subList(0, discardPile.size() - 1));
            deck.addAll(allButLastDiscarded);
            discardPile = new ArrayList<>(discardPile.subList(discardPile.size() - 1, discardPile.size()));
            deck.shuffle(random);

            System.out.println(String.format(String.format("Deck is now %d, discard pile is now %d", deck.size(), discardPile.size())));
            System.out.println(String.format(String.format("Players hands are now %d and %d", hands.get(0).size(), hands.get(1).size())));
        }
        return deck.draw(count);
    }

    public void setNextPlayer(int playerId) {
        if (playerId < 0) {
            throw new IllegalArgumentException("Invalid player id " + playerId);
        }
        System.out.println("Setting next player to " + playerId);
        this.nextPlayer = playerId;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }
}

