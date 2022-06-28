package com.codingame.game.io;

import com.codingame.game.models.Color;
import com.codingame.game.models.actions.Action;
import com.codingame.game.models.actions.SimpleAction;
import com.codingame.game.models.actions.WildAction;
import com.codingame.game.models.actions.WildDrawFourAction;
import com.codingame.game.models.cards.*;
import com.codingame.game.models.cards.NumberCard.Value;

import static com.codingame.game.models.Color.*;
import static com.codingame.game.models.cards.NumberCard.Value.*;

public class Serializers {
    public static Card parseCard(String line) {
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
            Color colorParsed = parseColor(color);

            switch (type) {
                case "DRAW_TWO":
                    return new DrawTwoCard(colorParsed);
                case "REVERSE":
                    return new ReverseCard(colorParsed);
                case "SKIP":
                    return new SkipCard(colorParsed);
                default:
                    return new NumberCard(colorParsed, Serializers.parseNumberValue(type));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid card: " + line, e);
        }
    }

    public static Color parseColor(String color) {
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

    static Value parseNumberValue(String value) {
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

    public static Action parseAction(String line) {
        try {
            String[] split = line.split(" ");
            String type = split[0];

            switch (type) {
                case "WILD":
                    return new WildAction(parseColor(split[1]));
                case "WILD_DRAW_FOUR":
                    return new WildDrawFourAction(parseColor(split[1]));
                default:
                    return new SimpleAction(parseCard(line));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid card: " + line, e);
        }
    }

    public static String serializeCard(Card card) {
        return card.toString();
    }

    public static String serializeAction(Action action) {
        return action.toString();
    }
}
