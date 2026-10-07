package me.mioclient.gui;

public interface ClosableElement {
    public static final ClosableElement presetHelper_2 = () -> {
        return false;
    };

    boolean isClosed();

    default boolean isShown() {
        return !isClosed();
    }
}
