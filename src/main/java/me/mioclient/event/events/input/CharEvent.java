package me.mioclient.event.events.input;

import me.mioclient.event.Event;

public final class CharEvent extends Event {
    public final int num;

    public CharEvent(int i) {
        this.num = i;
    }

    public String getString() {
        return Character.toString(this.num);
    }

    public char getChar() {
        return getString().charAt(0);
    }
}
