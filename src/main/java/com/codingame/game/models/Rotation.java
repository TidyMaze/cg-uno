package com.codingame.game.models;

public enum Rotation {
    CLOCKWISE(1),
    COUNTER_CLOCKWISE(-1);


    final int offset;

    Rotation(int offset) {
        this.offset = offset;
    }
}
