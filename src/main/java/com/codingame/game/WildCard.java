package com.codingame.game;

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
    public String getDisplayText() {
        return "WILD";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WildCard;
    }
}
