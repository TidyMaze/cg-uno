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
        NINE;

        static Value parse(String value) {
            switch (value) {
                case "ZERO":
                    return ZERO;
                case "ONE":
                    return ONE;
                case "TWO":
                    return TWO;
                case "THREE":
                    return THREE;
                case "FOUR":
                    return FOUR;
                case "FIVE":
                    return FIVE;
                case "SIX":
                    return SIX;
                case "SEVEN":
                    return SEVEN;
                case "EIGHT":
                    return EIGHT;
                case "NINE":
                    return NINE;
                default:
                    throw new IllegalArgumentException("Unknown value: " + value);
            }
        }
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
