package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;

import java.util.function.Consumer;

public class CodingameGameEngineListener implements GameEngineListener {

    private final MultiplayerGameManager gameManager;

    CodingameGameEngineListener(MultiplayerGameManager gameManager) {
        this.gameManager = gameManager;
    }

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

    private Consumer<Integer> tooltipHandler(String plus2) {
        return pi -> {
            Player player = (Player) gameManager.getPlayer(pi);
            gameManager.addTooltip(player, (player.getNicknameToken()) + " played a " + plus2);
        };
    }
}
