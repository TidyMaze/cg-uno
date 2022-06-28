package com.codingame.game.models.actions;

import com.codingame.game.models.Color;

import java.util.Objects;

public class WildAction implements Action {
    public Color color;

    @Override
    public String toString() {
        return String.format("WILD %s", color.name());
    }

    public WildAction(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildAction that = (WildAction) o;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
