package com.codingame.game;

import java.util.Objects;
import java.util.StringJoiner;

interface Action {
    static Action parse(String line) {
        try {
            String[] split = line.split(" ");
            String type = split[0];

            switch (type) {
                case "WILD":
                    return new WildAction(Color.parse(split[1]));
                case "WILD_DRAW_FOUR":
                    return new WildDrawFourAction(Color.parse(split[1]));
                default:
                    return new SimpleAction(Card.parse(line));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid card: " + line, e);
        }
    }
}

class WildAction implements Action {
    Color color;

    @Override
    public String toString() {
        return String.format("WILD %s", color.name());
    }

    public WildAction(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildAction that = (WildAction) o;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}

class WildDrawFourAction implements Action {
    Color color;

    @Override
    public String toString() {
        return String.format("WILD_DRAW_FOUR %s", color.name());
    }

    public WildDrawFourAction(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildDrawFourAction that = (WildDrawFourAction) o;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}

class SimpleAction implements Action {
    Card card;

    @Override
    public String toString() {
        return card.toString();
    }

    public SimpleAction(Card card) {
        this.card = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleAction that = (SimpleAction) o;
        return card.equals(that.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card);
    }
}