package com.codingame.game;

import java.util.Optional;

interface Card {
    static Card parse(String line) {
        try {
            String[] split = line.split(" ");
            String type = split[0];

            switch (type) {
                case "WILD":
                    return new WildCard();
                case "WILD_DRAW_FOUR":
                    return new WildDrawFourCard();
            }

            String color = split[1];
            Color colorParsed = Color.parse(color);

            switch (type) {
                case "DRAW_TWO":
                    return new DrawTwoCard(colorParsed);
                case "REVERSE":
                    return new ReverseCard(colorParsed);
                case "SKIP":
                    return new SkipCard(colorParsed);
                default:
                    return new NumberCard(colorParsed, NumberCard.Value.parse(type));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid card: " + line, e);
        }
    }

    String toString();

    Optional<Color> getCardColor();

    int getScore();
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