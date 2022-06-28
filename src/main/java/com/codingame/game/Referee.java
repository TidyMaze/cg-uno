package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;

import static com.codingame.game.GraphicsConstants.CARD_HEIGHT;
import static com.codingame.game.GraphicsConstants.CARD_WIDTH;
import static com.codingame.gameengine.module.entities.TextBasedEntity.TextAlign.CENTER;

public class Referee extends AbstractReferee {
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    private State state;

    @Override
    public void init() {
        gameManager.setMaxTurns(200);

        Deck deck = Deck.buildDeck();
        deck.shuffle(gameManager);

        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < gameManager.getPlayerCount(); i++) {
            hands.add(deck.draw(7));
        }

        state = new State(deck, new ArrayList<>(), hands);
        state.drawToDiscardPile();
        System.out.println(state.toString());
    }

    @Override
    public void gameTurn(int turn) {
        System.out.println(String.format("Turn %d", turn));

        Player player = gameManager.getPlayer(state.nextPlayer);

        List<Action> validActions = GameEngine.getValidActions(state, player.getIndex());

        if (validActions.isEmpty()) {
            Card drawn = state.draw(gameManager, 1).get(0);
            state.hands.get(player.getIndex()).add(drawn);
            System.out.println(String.format("Player %d had no valid action, drew %s", player.getIndex(), drawn));

            validActions = GameEngine.getValidActions(state, player.getIndex());
        }

        if (validActions.isEmpty()) {
            Card drawnCard = state.hands.get(player.getIndex()).get(state.hands.get(player.getIndex()).size() - 1);
            System.out.println(String.format("Player %d still have no valid action, skip turn", player.getIndex(), drawnCard));
            int currentNextPlayerIndex = GameEngine.nextPlayerIndex(state.rotation, player.getIndex(), false, gameManager.getPlayerCount());
            state.nextPlayer = currentNextPlayerIndex;
        } else {
            // Input line containing the hand of the player and last card in the discard pile
            List<Card> hand = state.hands.get(player.getIndex());
            Optional<Card> lastDiscardedCard = state.discardPile.isEmpty() ? Optional.empty() : Optional.of(state.discardPile.get(state.discardPile.size() - 1));

            player.sendInputLine(String.format("%d", hand.size()));
            for (Card card : hand) {
                player.sendInputLine(card.toString());
            }

            player.sendInputLine(String.format("%d", validActions.size()));
            for (Action action : validActions) {
                player.sendInputLine(action.toString());
            }

            player.sendInputLine(lastDiscardedCard.map(Card::toString).orElse("NO_DISCARDED_CARD"));
            player.execute();
            try {
                List<String> outputs = player.getOutputs();
                if (outputs.size() != 1) {
                    player.deactivate("Too many output lines!");
                    player.setScore(-1);
                }

                String line = outputs.get(0);
                if (line.isEmpty()) {
                    player.deactivate("Empty line");
                    player.setScore(-1);
                }

                Action action = Action.parse(line);
                System.out.println("Player " + player.getIndex() + " played " + action);

                System.out.println("Valid actions: " + validActions);

                boolean isValid = validActions.contains(action);
                if (isValid) {
                    GameEngine.playAction(state, action, gameManager);
                    gameManager.addToGameSummary(String.format("%s played %s", player.getNicknameToken(), action));

                    if (hand.size() == 0) {
                        gameManager.addTooltip(player, "Player " + player.getIndex() + " played the last card and won!");
                        player.setScore(computeScore(state.hands, player.getIndex()));
                        gameManager.endGame();
                    }
                } else {
                    player.deactivate("Invalid action " + action);
                    player.setScore(-1);
                }
            } catch (TimeoutException e) {
                player.deactivate(String.format("$%d timeout!", player.getIndex()));
                player.setScore(-1);
                gameManager.endGame();
            } catch (IllegalArgumentException e) {
                player.deactivate("Invalid action " + e.getMessage());
                player.setScore(-1);
            } catch (NotEnoughCardsException e) {
                gameManager.endGame();
            }
        }

        drawState();

        System.out.println(String.format("End of turn %d", turn));

        // Check if there is a win / lose situation and call gameManager.endGame(); when game is finished
    }

    private int computeScore(List<List<Card>> hands, int playerIndex) {
        int score = 0;
        for (int iHand = 0; iHand < hands.size(); iHand++) {
            if (iHand == playerIndex) {
                // skip the hand of winner
            } else {
                List<Card> hand = hands.get(iHand);
                for (Card card : hand) {
                    score += card.getScore();
                }

            }
        }
        return score;
    }

    void drawState() {
        System.out.println("Drawing state");
        World world = graphicEntityModule.getWorld();
        int width = world.getWidth();
        int height = world.getHeight();

        graphicEntityModule.createRectangle()
                .setHeight(height)
                .setWidth(width)
                // poker green background
                .setFillColor(0x3b9152)
                .setVisible(true);

        // calibration
        drawCard(0, 0, new NumberCard(Color.BLUE, NumberCard.Value.ONE));
        drawCard(width, height, new ReverseCard(Color.RED));

        // draw the discard pile
        int discardPileX = width / 2;
        int discardPileY = height / 2;

        if (!state.discardPile.isEmpty()) {
            drawCard(discardPileX, discardPileY, state.discardPile.get(state.discardPile.size() - 1));
        }

        // draw the deck pile
        int deckPileX = width / 2 - CARD_WIDTH - 20;
        int deckPileY = height / 2;

        if (!state.deck.isEmpty()) {
            drawDeck(deckPileX, deckPileY, state.deck.size());
        }

        for (int iHand = 0; iHand < state.hands.size(); iHand++) {
            List<Card> hand = state.hands.get(iHand);
            drawHand(hand, iHand);
        }

        graphicEntityModule.commitWorldState(1);
    }

    private void drawHand(List<Card> hand, int playerIndex) {
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
                return new Coordinate(graphicEntityModule.getWorld().getWidth() / 2, borderOffset);
            case 1:
                // RIGHT
                return new Coordinate(graphicEntityModule.getWorld().getWidth() - borderOffset, graphicEntityModule.getWorld().getHeight() / 2);
            case 2:
                // DOWN
                return new Coordinate(graphicEntityModule.getWorld().getWidth() / 2, graphicEntityModule.getWorld().getHeight() - borderOffset);
            case 3:
                // LEFT
                return new Coordinate(borderOffset, graphicEntityModule.getWorld().getHeight() / 2);
            default:
                throw new IllegalArgumentException("Invalid player index");
        }
    }

    private Group drawCard(int x, int y, Card card) {
        Optional<Integer> displayColor = card.getCardColor().map(color -> color.getDisplayColor());

        Group g = graphicEntityModule.createGroup();

        RoundedRectangle c = graphicEntityModule.createRoundedRectangle()
                .setFillColor(displayColor.orElse(0x000000))
                .setLineColor(0xFFFFFF)
                .setLineWidth(5)
                .setHeight(CARD_HEIGHT)
                .setWidth(CARD_WIDTH)
                .setX(x - CARD_WIDTH / 2)
                .setY(y - CARD_HEIGHT / 2)
                .setVisible(true);

        Text t = graphicEntityModule.createText(card.getDisplayText())
                .setTextAlign(CENTER)
                .setX(x - 20)
                .setY(y - 30)
                // white
                .setFontSize(50)
                .setFillColor(0xFFFFFF)
                .setStrokeColor(0x000000)
                .setStrokeThickness(5)
                .setZIndex(10)
                .setVisible(true);

        g.add(c);
        g.add(t);
        return g;
    }

    private void drawDeck(int x, int y, int count) {
        graphicEntityModule.createRoundedRectangle()
                .setFillColor(0x010101)
                .setLineColor(0xFFFFFF)
                .setLineWidth(5)
                .setHeight(CARD_HEIGHT)
                .setWidth(CARD_WIDTH)
                .setX(x - CARD_WIDTH / 2)
                .setY(y - CARD_HEIGHT / 2)
                .setVisible(true);

        graphicEntityModule.createCircle()
                .setFillColor(0xFF0000)
                .setRadius(CARD_WIDTH / 2 - 10)
                .setX(x)
                .setY(y)
                .setVisible(true);

        graphicEntityModule.createText(String.format("%d", count))
                .setTextAlign(CENTER)
                .setX(x - 25)
                .setY(y - 30)
                // white
                .setFontSize(50)
                .setFillColor(0xFFFFFF)
                .setStrokeColor(0x000000)
                .setStrokeThickness(5)
                .setVisible(true);
    }
}

class Coordinate {
    int x;
    int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class GraphicsConstants {
    static int CARD_WIDTH = 100;
    static int CARD_HEIGHT = (int) (CARD_WIDTH * 1.58);
}