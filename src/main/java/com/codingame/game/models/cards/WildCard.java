package com.codingame.game.models.cards;

import com.codingame.game.models.Color;

import java.util.Optional;

public class WildCard implements Card {
    @Override
    public String toString() {
        return "WILD";
    }

    @Override
    public Optional<Color> getCardColor() {
        return Optional.empty();
    }

    @Override
    public int getScore() {
        return 50;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WildCard;
    }
}
