package com.codingame.game;

import java.util.Objects;

public class DrawTwoCard implements Card {
    private Color color;

    public DrawTwoCard(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("DRAW_TWO %s", color.name());
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrawTwoCard that = (DrawTwoCard) o;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
