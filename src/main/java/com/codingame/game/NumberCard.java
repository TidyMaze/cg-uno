package com.codingame.game;

import java.util.Objects;
import java.util.Optional;

public class NumberCard implements Card {
    private final Color color;
    private final Value value;

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


    public enum Value {
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

        public int getIntValue() {
            switch (this) {
                case ZERO:
                    return 0;
                case ONE:
                    return 1;
                case TWO:
                    return 2;
                case THREE:
                    return 3;
                case FOUR:
                    return 4;
                case FIVE:
                    return 5;
                case SIX:
                    return 6;
                case SEVEN:
                    return 7;
                case EIGHT:
                    return 8;
                case NINE:
                    return 9;
                default:
                    throw new IllegalArgumentException("Unknown value: " + this);
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

    @Override
    public int getScore() {
        return this.value.getIntValue();
    }


}
