package com.codingame.game;

import java.util.Optional;

interface Card {
    static Card parse(String line) {
        String[] split = line.split(" ");
        String type = split[0];
        String color = split[1];
        Color colorParsed = Color.parse(color);

        switch (type) {
            case "WILD":
                return new WildCard();
            case "WILD_DRAW_FOUR":
                return new WildDrawFourCard();
            case "DRAW_TWO":
                return new DrawTwoCard(colorParsed);
            case "REVERSE":
                return new ReverseCard(colorParsed);
            case "SKIP":
                return new SkipCard(colorParsed);
            default:
                return new NumberCard(colorParsed, NumberCard.Value.parse(type));
        }
    }

    String toString();
}

enum Color {
    RED,
    BLUE,
    GREEN,
    YELLOW;

    public static Color parse(String color) {
        switch (color) {
            case "RED":
                return RED;
            case "BLUE":
                return BLUE;
            case "GREEN":
                return GREEN;
            case "YELLOW":
                return YELLOW;
            default:
                throw new IllegalArgumentException("Unknown color: " + color);
        }
    }
}