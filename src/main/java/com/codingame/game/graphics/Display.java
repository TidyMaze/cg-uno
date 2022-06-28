package com.codingame.game.graphics;

import com.codingame.game.models.Color;
import com.codingame.game.models.cards.*;

public class Display {
    public static final int RED_CARD_COLOR = 0xd60000;
    public static final int BLUE_CARD_COLOR = 0x0052b1;
    public static final int GREEN_CARD_COLOR = 0x008e00;
    public static final int YELLOW_CARD_COLOR = 0xead100;

    public static int getDisplayColor(Color c) {
        switch (c) {
            case RED:
                return RED_CARD_COLOR;
            case BLUE:
                return BLUE_CARD_COLOR;
            case GREEN:
                return GREEN_CARD_COLOR;
            case YELLOW:
                return YELLOW_CARD_COLOR;
            default:
                throw new IllegalArgumentException("Unknown color: " + c);
        }
    }

    public static String getCardDisplayText(Card card) {
        if (card instanceof NumberCard) {
            return ((NumberCard) card).getValue().getIntValue() + "";
        } else if (card instanceof WildCard) {
            return "\uD83C\uDFA8";
        } else if (card instanceof WildDrawFourCard) {
            return "+4";
        } else if (card instanceof DrawTwoCard) {
            return "+2";
        } else if (card instanceof ReverseCard) {
            return "\uD83D\uDD04";
        } else if (card instanceof SkipCard) {
            return "∅";
        } else {
            throw new IllegalArgumentException("Unknown card: " + card);
        }
    }
}
