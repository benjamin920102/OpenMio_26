package me.mioclient.event;

public interface Cancellable {
    void setCancelled(boolean z);

    default void cancel() {
        setCancelled(true);
    }

    boolean isCancelled();
}
