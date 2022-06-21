package com.codingame.game;

import java.util.StringJoiner;

public class NumberCard implements Card {
    private Color color;
    private Value value;

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }


    enum Value {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE
    }

    public NumberCard(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s %s", value.name(), color.name());
    }
}
