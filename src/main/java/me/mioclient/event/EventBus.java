package me.mioclient.event;


/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Helper_19.class */
public interface EventBus {
    void registerLookup(String str, LambdaEventListener.Inner inner);

    <T> T fireEvent(T t);

    <T extends Cancellable> T fireCancellableEvent(T t);

    void register(Object obj);

    void registerStatic(Class<?> cls);

    void addListener(EventListener baritoneHelper_5);

    void unregister(Object obj);

    void unregisterStatic(Class<?> cls);

    void removeListener(EventListener baritoneHelper_5);
}
