package me.mioclient.api;

public interface Toggleable {
    boolean isToggled();

    default void setToggled(boolean z) {
        if (isToggled() != z) {
            toggle();
        }
    }

    default void toggle() {
        if (isToggled()) {
            disable();
        } else {
            enable();
        }
    }

    void enable();

    void disable();
}
