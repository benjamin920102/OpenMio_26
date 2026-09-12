package me.mioclient.gui;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ClosableElement.class */
public interface ClosableElement {
    public static final ClosableElement presetHelper_2 = () -> {
        return false;
    };

    boolean isClosed();

    default boolean isShown() {
        return !isClosed();
    }
}
