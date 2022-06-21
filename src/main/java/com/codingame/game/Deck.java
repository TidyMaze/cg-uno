package com.codingame.game;

import com.codingame.game.NumberCard.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class Deck {

    private List<Card> cards;

    private Deck() {
        this.cards = new ArrayList<>();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Deck.class.getSimpleName() + "[", "]")
                .add("cards=" + cards)
                .toString();
    }

    private void addCard(Card card) {
        this.cards.add(card);
    }

    static Deck buildDeck() {
        Deck d = new Deck();

        // add all cards from 1 to 9 for each color, twice
        for (Color c : Color.values()) {
            for (Value v : Value.values()) {
                d.addCard(new NumberCard(c, v));
                d.addCard(new NumberCard(c, v));
            }
        }

        // add 0 from each color
        for (Color c : Color.values()) {
            d.addCard(new NumberCard(c, Value.ZERO));
        }

        // add Skip for each color, twice
        for (Color c : Color.values()) {
            d.addCard(new SkipCard(c));
            d.addCard(new SkipCard(c));
        }

        // add Reverse for each color, twice
        for (Color c : Color.values()) {
            d.addCard(new ReverseCard(c));
            d.addCard(new ReverseCard(c));
        }

        // add Draw 2 for each color, twice
        for (Color c : Color.values()) {
            d.addCard(new DrawTwoCard(c));
            d.addCard(new DrawTwoCard(c));
        }

        // add Wild, four times
        for (int i = 0; i < 4; i++) {
            d.addCard(new WildCard());
        }

        // add Wild Draw 4, four times
        for (int i = 0; i < 4; i++) {
            d.addCard(new WildDrawFourCard());
        }


        return d;
    }
}
