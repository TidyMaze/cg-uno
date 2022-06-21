package com.codingame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        boolean isFirstTurn = turn == 1;

        int playerCount = gameManager.getPlayerCount();
        Player player = gameManager.getPlayer(turn % playerCount);

        int lastTurn = 42;

        // Input line containing the hand of the player and last card in the discard pile
        List<Card> hand = state.hands.get(player.getIndex());
        Optional<Card> lastDiscardedCard = state.discardPile.isEmpty() ? Optional.empty() : Optional.of(state.discardPile.get(state.discardPile.size() - 1));

        player.sendInputLine(String.format("%d", hand.size()));
        for (Card card : hand) {
            player.sendInputLine(card.toString());
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

            Card card = Card.parse(line);
            System.out.println("Player " + player.getIndex() + " played " + card);
        } catch (TimeoutException e) {
            player.deactivate(String.format("$%d timeout!", player.getIndex()));
            player.setScore(-1);
            gameManager.endGame();
        }

        // Check if there is a win / lose situation and call gameManager.endGame(); when game is finished
    }
}