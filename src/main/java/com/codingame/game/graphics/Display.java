package com.codingame.game.graphics;

import com.codingame.game.models.Color;
import com.codingame.game.models.Coordinate;
import com.codingame.game.models.State;
import com.codingame.game.models.cards.*;
import com.codingame.gameengine.module.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.codingame.gameengine.module.entities.TextBasedEntity.TextAlign.CENTER;

public class Display {
    private final int worldWidth;
    private final int worldHeight;
    GraphicEntityModule graphicEntityModule;

    public static final int RED_CARD_COLOR = 0xd60000;
    public static final int BLUE_CARD_COLOR = 0x0052b1;
    public static final int GREEN_CARD_COLOR = 0x008e00;
    public static final int YELLOW_CARD_COLOR = 0xead100;

    static int CARD_WIDTH = 100;
    static int CARD_HEIGHT = (int) (CARD_WIDTH * 1.58);
    public static final int BLACK = 0x000000;
    public static final int WHITE = 0xFFFFFF;
    public static final int CARD_BACK_COLOR = 0x010101;
    public static final int RED = 0xFF0000;
    public static final int BACKGROUND_COLOR = 0x3b9152;

    public Display(GraphicEntityModule graphicEntityModule) {
        this.graphicEntityModule = graphicEntityModule;
        this.worldWidth = graphicEntityModule.getWorld().getWidth();
        this.worldHeight = graphicEntityModule.getWorld().getHeight();
    }

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


    public List<Entity> drawState(State state) {
        System.out.println("Drawing state");

        // calibration
//        drawCard(0, 0, new NumberCard(Color.BLUE, NumberCard.Value.ONE));
//        drawCard(worldWidth, worldHeight, new ReverseCard(Color.RED));

        List<Entity> entities = new ArrayList<>();

        int discardPileX = worldWidth / 2;
        int discardPileY = worldHeight / 2;
        if (!state.discardPile.isEmpty()) {
            entities.add(drawCard(discardPileX, discardPileY, state.discardPile.get(state.discardPile.size() - 1)));
        }

        int deckPileX = worldWidth / 2 - CARD_WIDTH - 20;
        int deckPileY = worldHeight / 2;

        if (!state.deck.isEmpty()) {
            entities.add(drawDeck(deckPileX, deckPileY, state.deck.size()));
        }

        for (int iHand = 0; iHand < state.hands.size(); iHand++) {
            List<Card> hand = state.hands.get(iHand);
            entities.add(drawHand(hand, iHand));
        }

        graphicEntityModule.commitWorldState(1);
        return entities;
    }

    public void drawBackground() {
        graphicEntityModule.createRectangle()
                .setHeight(worldHeight)
                .setWidth(worldWidth)
                .setFillColor(BACKGROUND_COLOR)
                .setVisible(true);
    }

    private Group drawHand(List<Card> hand, int playerIndex) {
        Coordinate center = getCenterOfHand(playerIndex);

        Group g = graphicEntityModule.createGroup();

        int handRectangleStartX = -((hand.size() - 1) * CARD_WIDTH) / 2;

        for (int iCard = 0; iCard < hand.size(); iCard++) {
            Group c = drawCard(handRectangleStartX + iCard * CARD_WIDTH, 0, hand.get(iCard));
            g.add(c);
        }

        g.setX(center.x);
        g.setY(center.y);

        double rotation = getRotation(playerIndex);
        g.setRotation(rotation);

        return g;
    }

    private double getRotation(int playerIndex) {
        switch (playerIndex) {
            case 0:
                return degreesToRadians(0);
            case 1:
                return degreesToRadians(-90);
            case 2:
                return degreesToRadians(0);
            case 3:
                return degreesToRadians(90);
            default:
                throw new IllegalArgumentException("Invalid player index");
        }
    }

    double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    private Coordinate getCenterOfHand(int playerIndex) {
        int borderOffset = 150;
        switch (playerIndex) {
            case 0:
                // UP
                return new Coordinate(worldWidth / 2, borderOffset);
            case 1:
                // RIGHT
                return new Coordinate(worldWidth - borderOffset, worldHeight / 2);
            case 2:
                // DOWN
                return new Coordinate(worldWidth / 2, worldHeight - borderOffset);
            case 3:
                // LEFT
                return new Coordinate(borderOffset, worldHeight / 2);
            default:
                throw new IllegalArgumentException("Invalid player index");
        }
    }

    private Group drawCard(int x, int y, Card card) {
        Optional<Integer> displayColor = card.getCardColor().map(color -> Display.getDisplayColor(color));

        Group g = graphicEntityModule.createGroup();

        RoundedRectangle c = graphicEntityModule.createRoundedRectangle()
                .setFillColor(displayColor.orElse(BLACK))
                .setLineColor(WHITE)
                .setLineWidth(5)
                .setHeight(CARD_HEIGHT)
                .setWidth(CARD_WIDTH)
                .setX(x - CARD_WIDTH / 2)
                .setY(y - CARD_HEIGHT / 2)
                .setVisible(true);

        Text t = graphicEntityModule.createText(Display.getCardDisplayText(card))
                .setTextAlign(CENTER)
                .setX(x - 20)
                .setY(y - 30)
                // white
                .setFontSize(50)
                .setFillColor(WHITE)
                .setStrokeColor(BLACK)
                .setStrokeThickness(5)
                .setZIndex(10)
                .setVisible(true);

        g.add(c);
        g.add(t);
        return g;
    }

    private Group drawDeck(int x, int y, int count) {

        Group g = graphicEntityModule.createGroup();

        g.add(graphicEntityModule.createRoundedRectangle()
                .setFillColor(CARD_BACK_COLOR)
                .setLineColor(WHITE)
                .setLineWidth(5)
                .setHeight(CARD_HEIGHT)
                .setWidth(CARD_WIDTH)
                .setX(x - CARD_WIDTH / 2)
                .setY(y - CARD_HEIGHT / 2)
                .setVisible(true));

        g.add(graphicEntityModule.createCircle()
                .setFillColor(RED)
                .setRadius(CARD_WIDTH / 2 - 10)
                .setX(x)
                .setY(y)
                .setZIndex(10)
                .setVisible(true));

        g.add(graphicEntityModule.createText(String.format("%d", count))
                .setTextAlign(CENTER)
                .setX(x - 25)
                .setY(y - 30)
                // white
                .setFontSize(50)
                .setFillColor(WHITE)
                .setStrokeColor(BLACK)
                .setStrokeThickness(5)
                .setZIndex(20)
                .setVisible(true));

        return g;
    }
}
