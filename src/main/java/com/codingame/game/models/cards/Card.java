package com.codingame.game.models.cards;

import com.codingame.game.models.Color;

import java.util.Optional;

public interface Card {


    String toString();

    Optional<Color> getCardColor();

    int getScore();
}

