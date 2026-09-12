package me.mioclient.event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/RuntimeException.class */
public class ListenerLookupException extends java.lang.RuntimeException {
    public ListenerLookupException(Class<?> cls) {
        super("No registered lambda listener for '" + cls.getName() + "'.");
    }
}
