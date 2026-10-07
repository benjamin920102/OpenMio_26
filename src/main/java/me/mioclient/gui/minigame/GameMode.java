package me.mioclient.gui.minigame;

import me.mioclient.api.NamedValue;

public enum GameMode implements NamedValue {
    TETRIS("Tetris"),
    SNAKE("Snake");

    public final String name;

    GameMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
