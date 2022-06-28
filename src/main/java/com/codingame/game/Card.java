package com.codingame.game;

import java.util.Optional;

public interface Card {


    String toString();

    Optional<Color> getCardColor();

    int getScore();
}

