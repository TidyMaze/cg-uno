package com.codingame.game;

import java.util.ArrayList;
import java.util.List;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;

    @Override
    public void init() {
        gameManager.setMaxTurns(10);

        Deck deck = Deck.buildDeck();
        deck.shuffle(gameManager);

        List<List<Card>> hands = new ArrayList<>();
        for (int i = 0; i < gameManager.getPlayerCount(); i++) {
            hands.add(deck.draw(7));
        }

        State state = new State(deck, new ArrayList<>(), hands);
        state.drawToDiscardPile();
        System.out.println(state.toString());
    }

    @Override
    public void gameTurn(int turn) {
        System.out.println(String.format("Turn %d", turn));

        boolean isFirstTurn = turn == 1;

        int playerCount = gameManager.getPlayerCount();
        Player player = gameManager.getPlayer(turn % playerCount);

        int lastTurn = 42;

        player.sendInputLine(String.format("Opponent played %d", lastTurn));
        player.execute();
        try {
            List<String> outputs = player.getOutputs();
            if (outputs.size() != 1) {
                player.deactivate("Too many output lines!");
            }

            String line = outputs.get(0);
            if (line.isEmpty()) {
                player.deactivate("Empty line");
            }


        } catch (TimeoutException e) {
            player.deactivate(String.format("$%d timeout!", player.getIndex()));
            player.setScore(-1);
            gameManager.endGame();
        }

        // Check if there is a win / lose situation and call gameManager.endGame(); when game is finished
    }
}