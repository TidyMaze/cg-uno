package com.codingame.game;

import java.util.Objects;
import java.util.Optional;

public class SkipCard implements Card {
    private Color color;

    public SkipCard(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("SKIP %s", color.name());
    }

    @Override
    public Optional<Color> getCardColor() {
        return Optional.of(color);
    }

    @Override
    public int getScore() {
        return 20;
    }

    @Override
    public String getDisplayText() {
        return "SKIP";
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkipCard skipCard = (SkipCard) o;
        return color == skipCard.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
