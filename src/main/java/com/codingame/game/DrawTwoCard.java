package com.codingame.game;

public class DrawTwoCard implements Card {
    private Color color;

    public DrawTwoCard(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("DRAW TWO %s", color.name());
    }

    public Color getColor() {
        return color;
    }
}
