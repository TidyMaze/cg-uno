package com.codingame.game;

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

    public Color getColor() {
        return color;
    }
}
