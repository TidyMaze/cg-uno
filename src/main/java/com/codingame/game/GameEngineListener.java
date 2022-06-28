package com.codingame.game;

interface GameEngineListener {

    public void onDrawTwo(int playerIndex);

    public void onSkip(int playerIndex);

    public void onReverse(int playerIndex);

    public void onWildDrawFour(int playerIndex);
}
