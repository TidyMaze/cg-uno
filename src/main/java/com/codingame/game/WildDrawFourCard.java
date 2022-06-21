package com.codingame.game;

public class WildDrawFourCard implements Card {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof WildDrawFourCard;
    }

    @Override
    public String toString() {
        return "WILD_DRAW_FOUR";
    }
}
