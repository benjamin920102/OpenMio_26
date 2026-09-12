package me.mioclient.gui.minigame;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/GameMode.class */
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
