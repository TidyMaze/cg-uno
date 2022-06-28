package com.codingame.game.models.actions;

import com.codingame.game.models.cards.Card;

import java.util.Objects;

public class SimpleAction implements Action {
    public Card card;

    @Override
    public String toString() {
        return card.toString();
    }

    public SimpleAction(Card card) {
        this.card = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleAction that = (SimpleAction) o;
        return card.equals(that.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card);
    }
}
