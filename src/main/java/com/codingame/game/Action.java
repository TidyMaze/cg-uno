package com.codingame.game;

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
}