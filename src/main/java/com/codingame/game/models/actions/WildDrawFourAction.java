package com.codingame.game.models.actions;

import com.codingame.game.models.Color;

import java.util.Objects;

public class WildDrawFourAction implements Action {
    public Color color;

    @Override
    public String toString() {
        return String.format("WILD_DRAW_FOUR %s", color.name());
    }

    public WildDrawFourAction(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildDrawFourAction that = (WildDrawFourAction) o;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
