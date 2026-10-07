package me.mioclient.event;

public class ListenerLookupException extends java.lang.RuntimeException {
    public ListenerLookupException(Class<?> cls) {
        super("No registered lambda listener for '" + cls.getName() + "'.");
    }
}
