package com.codingame.game.models.cards;

import com.codingame.game.models.Color;

import java.util.Objects;
import java.util.Optional;

public class SkipCard implements Card {
    private final Color color;

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
