package com.codingame.game;

public class SkipCard implements Card {
    private Color color;

    public SkipCard(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("SKIP %s", color.name());
    }

    public Color getColor() {
        return color;
    }
}
