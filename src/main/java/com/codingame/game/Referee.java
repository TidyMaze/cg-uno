package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.codingame.game.graphics.Display;
import com.codingame.game.io.Serializers;
import com.codingame.game.models.Deck;
import com.codingame.game.models.State;
import com.codingame.game.models.actions.Action;
import com.codingame.game.models.cards.Card;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;

import static com.codingame.game.GameEngine.playerWon;
import static com.codingame.game.io.Serializers.*;

public class Referee extends AbstractReferee {

    @Inject
    private MultiplayerGameManager<Player> gm;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    private State state;

    Display display;

    GameEngine gameEngine;

    @Override
    public void init() {
        this.display = new Display(graphicEntityModule);
        gm.setMaxTurns(200);

        int playerCount = gm.getPlayerCount();

        this.state = initializeState(playerCount);
        System.out.println(state);

        GameEngineListener listener = new CodingameGameEngineListener(gm);
        gameEngine = new GameEngine(playerCount, gm.getRandom(), listener);


    }

    private State initializeState(int playerCount) {
        Deck deck = Deck.buildDeck();
        deck.shuffle(gm.getRandom());


        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            hands.add(deck.draw(7));
        }

        State state = new State(deck, new ArrayList<>(), hands);
        state.drawToDiscardPile();

        return state;
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gm.getPlayer(state.nextPlayer);
        int playerIndex = player.getIndex();

        List<Action> validActions = drawCardOrRedraw(player);

        if (validActions.isEmpty()) {
            skipTurnStillCannotPlayAfterRedraw(playerIndex);
            gameTurn(turn);
        } else {
            System.out.printf("Turn %d%n", turn);
            doOnePlayerTurn(player, validActions);
            display.drawState(state);
            System.out.printf("End of turn %d%n", turn);
        }


    }

    private void doOnePlayerTurn(Player player, List<Action> validActions) {
        List<Card> hand = state.hands.get(player.getIndex());
        sendInputLines(player, validActions, hand);
        player.execute();
        try {
            Action action = readPlayerAction(player);
            System.out.println("Player " + player.getIndex() + " played " + action);
            System.out.println("Valid actions: " + validActions);

            if (isValid(validActions, action)) {
                gameEngine.playAction(state, action);
                onActionPlayed(player, action);

                if (playerWon(hand)) {
                    onVictory(player);
                }
            } else {
                disqualifyPlayer(player, "Invalid action " + action);
            }
        } catch (TimeoutException e) {
            disqualifyPlayer(player, String.format("$%d timeout!", player.getIndex()));
        } catch (IllegalArgumentException e) {
            disqualifyPlayer(player, "Invalid action " + e.getMessage());
        } catch (NotEnoughCardsException e) {
            gm.endGame();
        }
    }

    private void skipTurnStillCannotPlayAfterRedraw(int playerIndex) {
        Card drawnCard = state.hands.get(playerIndex).get(state.hands.get(playerIndex).size() - 1);
        state.nextPlayer = gameEngine.nextPlayerIndex(state.rotation, playerIndex, false);
        gm.addToGameSummary(String.format("Player %d still have no valid action, skip turn, next player is %d", playerIndex, state.nextPlayer));
    }

    private List<Action> drawCardOrRedraw(Player player) {
        List<Action> validActions = GameEngine.getValidActions(state, player.getIndex());

        if (validActions.isEmpty()) {
            Card drawn = gameEngine.drawOne(state);
            state.hands.get(player.getIndex()).add(drawn);
            gm.addToGameSummary(String.format("Player %d had no valid action, drew %s%n", player.getIndex(), drawn));
            validActions = GameEngine.getValidActions(state, player.getIndex());
        }
        return validActions;
    }

    private static boolean isValid(List<Action> validActions, Action action) {
        return validActions.contains(action);
    }

    private static Action readPlayerAction(Player player) throws TimeoutException {
        List<String> outputs = player.getOutputs();
        if (outputs.size() != 1) {
            disqualifyPlayer(player, "Too many output lines!");
        }

        String line = outputs.get(0);
        if (line.isEmpty()) {
            disqualifyPlayer(player, "Empty line");
        }

        return parseAction(line);
    }

    private void sendInputLines(Player player, List<Action> validActions, List<Card> hand) {
        Optional<Card> lastDiscardedCard = state.discardPile.isEmpty() ? Optional.empty() : Optional.of(state.discardPile.get(state.discardPile.size() - 1));

        player.sendInputLine(String.format("%d", hand.size()));
        for (Card card : hand) {
            player.sendInputLine(serializeCard(card));
        }

        player.sendInputLine(String.format("%d", validActions.size()));
        for (Action action : validActions) {
            player.sendInputLine(serializeAction(action));
        }

        player.sendInputLine(lastDiscardedCard.map(Serializers::serializeCard).orElse("NO_DISCARDED_CARD"));
    }

    private static void disqualifyPlayer(Player player, String action) {
        player.deactivate(action);
        player.setScore(-1);
    }

    private void onVictory(Player player) {
        showVictoryTooltip(player);
        player.setScore(GameEngine.computeScore(state.hands, player.getIndex()));
        gm.endGame();
    }

    private void showVictoryTooltip(Player player) {
        gm.addTooltip(player, "Player " + player.getIndex() + " played the last card and won!");
    }

    private void onActionPlayed(Player player, Action action) {
        gm.addToGameSummary(String.format("%s played %s, next player is %s", player.getNicknameToken(), action, state.nextPlayer));
    }


}

