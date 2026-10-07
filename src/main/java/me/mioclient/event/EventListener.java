package me.mioclient.event;

public interface EventListener {
    void accept(Object obj);

    Class<?> getEventType();

    int getPriority();

    boolean isStaticListener();
}
