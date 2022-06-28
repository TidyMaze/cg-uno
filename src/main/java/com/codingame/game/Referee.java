package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.codingame.game.graphics.Display;
import com.codingame.game.models.Deck;
import com.codingame.game.models.State;
import com.codingame.game.models.actions.Action;
import com.codingame.game.models.cards.Card;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;

import static com.codingame.game.io.Serializers.parseAction;

public class Referee extends AbstractReferee {

    @Inject
    private MultiplayerGameManager<Player> gm;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    private State state;

    Display graphics;

    @Override
    public void init() {
        this.graphics = new Display(graphicEntityModule);

        gm.setMaxTurns(200);

        Deck deck = Deck.buildDeck();
        deck.shuffle(gm);

        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            hands.add(deck.draw(7));
        }

        state = new State(deck, new ArrayList<>(), hands);
        state.drawToDiscardPile();
        System.out.println(state.toString());
    }

    @Override
    public void gameTurn(int turn) {
        System.out.printf("Turn %d%n", turn);

        Player player = gm.getPlayer(state.nextPlayer);

        List<Action> validActions = GameEngine.getValidActions(state, player.getIndex());

        if (validActions.isEmpty()) {
            Card drawn = state.draw(gm, 1).get(0);
            state.hands.get(player.getIndex()).add(drawn);
            System.out.printf("Player %d had no valid action, drew %s%n", player.getIndex(), drawn);

            validActions = GameEngine.getValidActions(state, player.getIndex());
        }

        if (validActions.isEmpty()) {
            Card drawnCard = state.hands.get(player.getIndex()).get(state.hands.get(player.getIndex()).size() - 1);
            System.out.printf("Player %d still have no valid action, skip turn%n", player.getIndex(), drawnCard);
            state.nextPlayer = GameEngine.nextPlayerIndex(state.rotation, player.getIndex(), false, gm.getPlayerCount());
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

                Action action = parseAction(line);
                System.out.println("Player " + player.getIndex() + " played " + action);

                System.out.println("Valid actions: " + validActions);

                boolean isValid = validActions.contains(action);
                if (isValid) {
                    GameEngine.playAction(state, action, gm,
                            tooltipHandler("+2"),
                            tooltipHandler("Skip"),
                            tooltipHandler("Reverse"),
                            tooltipHandler("+4"),
                            gm.getPlayerCount());
                    gm.addToGameSummary(String.format("%s played %s", player.getNicknameToken(), action));

                    if (hand.size() == 0) {
                        gm.addTooltip(player, "Player " + player.getIndex() + " played the last card and won!");
                        player.setScore(computeScore(state.hands, player.getIndex()));
                        gm.endGame();
                    }
                } else {
                    player.deactivate("Invalid action " + action);
                    player.setScore(-1);
                }
            } catch (TimeoutException e) {
                player.deactivate(String.format("$%d timeout!", player.getIndex()));
                player.setScore(-1);
                gm.endGame();
            } catch (IllegalArgumentException e) {
                player.deactivate("Invalid action " + e.getMessage());
                player.setScore(-1);
            } catch (NotEnoughCardsException e) {
                gm.endGame();
            }
        }

        graphics.drawState(state);

        System.out.printf("End of turn %d%n", turn);

        // Check if there is a win / lose situation and call gameManager.endGame(); when game is finished
    }

    private Consumer<Integer> tooltipHandler(String plus2) {
        return pi -> gm.addTooltip(gm.getPlayer(pi), (gm.getPlayer(pi).getNicknameToken()) + " played a " + plus2);
    }

    private int computeScore(List<List<Card>> hands, int playerIndex) {
        int score = 0;
        for (int iHand = 0; iHand < hands.size(); iHand++) {
            if (iHand != playerIndex) {
                List<Card> hand = hands.get(iHand);
                for (Card card : hand) {
                    score += card.getScore();
                }

            }
        }
        return score;
    }

}

