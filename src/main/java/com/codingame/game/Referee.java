package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

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

    Display graphics;

    GameEngine gameEngine;

    GameEngineListener listener = new GameEngineListener() {
        @Override
        public void onDrawTwo(int playerIndex) {
            tooltipHandler("+2").accept(playerIndex);
        }

        @Override
        public void onSkip(int playerIndex) {
            tooltipHandler("Skip").accept(playerIndex);
        }

        @Override
        public void onReverse(int playerIndex) {
            tooltipHandler("Reverse").accept(playerIndex);
        }

        @Override
        public void onWildDrawFour(int playerIndex) {
            tooltipHandler("+4").accept(playerIndex);
        }
    };

    private Random random;

    @Override
    public void init() {
        this.graphics = new Display(graphicEntityModule);
        this.random = gm.getRandom();

        gm.setMaxTurns(200);

        Deck deck = Deck.buildDeck();
        deck.shuffle(gm.getRandom());

        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < gm.getPlayerCount(); i++) {
            hands.add(deck.draw(7));
        }

        state = new State(deck, new ArrayList<>(), hands);
        state.drawToDiscardPile();
        System.out.println(state.toString());

        gameEngine = new GameEngine(gm.getPlayerCount(), gm.getRandom(), listener);
    }

    @Override
    public void gameTurn(int turn) {
        int playerCount = gm.getPlayerCount();
        Player player = gm.getPlayer(state.nextPlayer);
        int playerIndex = player.getIndex();

        List<Action> validActions = drawCardOrRedraw(random, player);

        if (validActions.isEmpty()) {
            skipTurnStillCannotPlayAfterRedraw(playerCount, playerIndex);
            gameTurn(turn);
        } else {
            System.out.printf("Turn %d%n", turn);
            doOnePlayerTurn(playerCount, player, validActions);
            graphics.drawState(state);
            System.out.printf("End of turn %d%n", turn);
        }


    }

    private void doOnePlayerTurn(int playerCount, Player player, List<Action> validActions) {
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

    private void skipTurnStillCannotPlayAfterRedraw(int playerCount, int playerIndex) {
        Card drawnCard = state.hands.get(playerIndex).get(state.hands.get(playerIndex).size() - 1);
        System.out.printf("Player %d still have no valid action, skip turn%n", playerIndex, drawnCard);
        state.nextPlayer = GameEngine.nextPlayerIndex(state.rotation, playerIndex, false, playerCount);
    }

    private List<Action> drawCardOrRedraw(Random random, Player player) {
        List<Action> validActions = GameEngine.getValidActions(state, player.getIndex());

        if (validActions.isEmpty()) {
            Card drawn = state.draw(1, random).get(0);
            state.hands.get(player.getIndex()).add(drawn);
            System.out.printf("Player %d had no valid action, drew %s%n", player.getIndex(), drawn);
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

        Action action = parseAction(line);
        return action;
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
        gm.addToGameSummary(String.format("%s played %s", player.getNicknameToken(), action));
    }

    private Consumer<Integer> tooltipHandler(String plus2) {
        return pi -> gm.addTooltip(gm.getPlayer(pi), (gm.getPlayer(pi).getNicknameToken()) + " played a " + plus2);
    }

}

