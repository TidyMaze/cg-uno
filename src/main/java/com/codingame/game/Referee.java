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
        World world = graphicEntityModule.getWorld();
        int width = world.getWidth();
        int height = world.getHeight();

        graphicEntityModule.createRectangle()
                .setHeight(height)
                .setWidth(width)
                // poker green background
                .setFillColor(0x35654d)
                .setVisible(true);

        for (int iHand = 0; iHand < state.hands.size(); iHand++) {
            List<Card> hand = state.hands.get(iHand);
            drawHand(hand, iHand);
        }
    }

    private void drawHand(List<Card> hand, int playerIndex) {
        Coordinate center = getCenterOfHand(playerIndex);

        int handRectangleWidth = hand.size() * CARD_WIDTH;
        int handRectangleHeight = CARD_HEIGHT;

        int handRectangleStartX = center.x - (handRectangleWidth - 1) / 2;
        int handRectangleStartY = center.y - (handRectangleHeight - 1) / 2;

        Group g = graphicEntityModule.createGroup();

        for (int iCard = 0; iCard < hand.size(); iCard++) {
            drawCard(handRectangleStartX + iCard * CARD_WIDTH, handRectangleStartY, hand.get(iCard));
        }
    }

    private Coordinate getCenterOfHand(int playerIndex) {
        int borderOffset = 100;
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

    private void drawCard(int x, int y, Card card) {
        Optional<Integer> displayColor = card.getCardColor().map(color -> color.getDisplayColor());

        graphicEntityModule.createRoundedRectangle()
                .setFillColor(displayColor.orElse(0x000000))
                .setLineColor(0xFFFFFF)
                .setLineWidth(5)
                .setHeight(CARD_HEIGHT)
                .setWidth(CARD_WIDTH)
                .setX(x)
                .setY(y)
                .setVisible(true);

        graphicEntityModule.createText(card.getDisplayText())
                .setX(x)
                .setY(y)
                .setTextAlign(CENTER)
                // white
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