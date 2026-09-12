package me.mioclient.event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Cancellable.class */
public interface Cancellable {
    void setCancelled(boolean z);

    default void cancel() {
        setCancelled(true);
    }

    boolean isCancelled();
}
