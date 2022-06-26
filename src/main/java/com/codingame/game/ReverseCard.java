package com.codingame.game;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class ReverseCard implements Card {
    private Color color;

    public ReverseCard(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("REVERSE %s", color.name());
    }

    @Override
    public Optional<Color> getCardColor() {
        return Optional.of(color);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReverseCard that = (ReverseCard) o;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
