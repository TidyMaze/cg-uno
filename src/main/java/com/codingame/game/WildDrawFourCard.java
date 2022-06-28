package com.codingame.game;

import java.util.Optional;

public class WildDrawFourCard implements Card {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof WildDrawFourCard;
    }

    @Override
    public String toString() {
        return "WILD_DRAW_FOUR";
    }

    @Override
    public Optional<Color> getCardColor() {
        return Optional.empty();
    }

    @Override
    public int getScore() {
        return 50;
    }
}
