package com.codingame.game;

public class WildCard implements Card {
    @Override
    public String toString() {
        return "WILD";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WildCard;
    }
}
