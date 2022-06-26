package com.codingame.game;

import java.util.Objects;
import java.util.Optional;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberCard that = (NumberCard) o;
        return color == that.color && value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, value);
    }


    enum Value {
        ZERO(0),
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9);

        public final int intValue;

        Value(int intValue) {
            this.intValue = intValue;
        }

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

    @Override
    public Optional<Color> getCardColor() {
        return Optional.of(color);
    }
}
