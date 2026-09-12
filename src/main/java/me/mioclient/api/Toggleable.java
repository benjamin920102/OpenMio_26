package me.mioclient.api;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Toggleable.class */
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
