package me.mioclient.event;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import me.mioclient.event.Listen;

public class LambdaEventListener implements EventListener {
    public static boolean isJava8;
    public static Constructor<MethodHandles.Lookup> lookupConstructor;
    public static Method privateLookupInMethod;
    public final Class<?> eventType;
    public final boolean isStatic;
    public final int priority;
    public Consumer<Object> handler;
    /** Owner class simple name, retained for diagnostic logging. */
    private final String ownerName;
    /** Listener method name, retained for diagnostic logging. */
    private final String methodName;

    
    public interface Inner {
        MethodHandles.Lookup create(Method privateLookupInMethod, Class<?> cls);
    }

    public LambdaEventListener(Inner inner, Class<?> cls, Object obj, Method method2) {
        MethodHandles.Lookup create;
        MethodHandle findVirtual;
        MethodType methodType;
        this.eventType = method2.getParameters()[0].getType();
        this.isStatic = Modifier.isStatic(method2.getModifiers());
        this.priority = ((Listen) method2.getAnnotation(Listen.class)).get219();
        this.ownerName = cls == null ? "null" : cls.getSimpleName();
        this.methodName = method2.getName();
        try {
            String name = method2.getName();
            if (isJava8) {
                boolean isAccessible = lookupConstructor.isAccessible();
                lookupConstructor.setAccessible(true);
                create = lookupConstructor.newInstance(cls);
                lookupConstructor.setAccessible(isAccessible);
            } else {
                create = inner.create(privateLookupInMethod, cls);
            }
            MethodType methodType2 = MethodType.methodType((Class<?>) Void.TYPE, method2.getParameters()[0].getType());
            if (this.isStatic) {
                findVirtual = create.findStatic(cls, name, methodType2);
                methodType = MethodType.methodType(Consumer.class);
            } else {
                findVirtual = create.findVirtual(cls, name, methodType2);
                methodType = MethodType.methodType((Class<?>) Consumer.class, cls);
            }
            MethodHandle target = LambdaMetafactory.metafactory(create, "accept", methodType, MethodType.methodType((Class<?>) Void.TYPE, (Class<?>) Object.class), findVirtual, methodType2).getTarget();
            if (this.isStatic) {
                this.handler = (Consumer) target.invoke();
            } else {
                this.handler = (Consumer) target.invoke(obj);
            }
        } catch (Throwable th) {
            // Linkage failure leaves handler null, so every later accept() would NPE inside the
            // event bus. Name the listener so the trace points at the real cause.
            System.err.println("[mio] failed to link listener "
                    + this.ownerName + "#" + this.methodName);
            th.printStackTrace();
        }
    }

    @Override // me.mioclient.event.EventListener
    public void accept(Object obj) {
        if (this.handler == null) {
            return;
        }
        this.handler.accept(obj);
    }

    /** Owner class simple name, retained for diagnostics. */
    public String getOwnerName() {
        return this.ownerName;
    }

    /** Listener method name, retained for diagnostics. */
    public String getMethodName() {
        return this.methodName;
    }

    @Override // me.mioclient.event.EventListener
    public Class<?> getEventType() {
        return this.eventType;
    }

    @Override // me.mioclient.event.EventListener
    public int getPriority() {
        return this.priority;
    }

    @Override // me.mioclient.event.EventListener
    public boolean isStaticListener() {
        return this.isStatic;
    }

    static {
        try {
            isJava8 = System.getProperty("java.version").startsWith("1.8");
            if (isJava8) {
                lookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
            } else {
                privateLookupInMethod = MethodHandles.class.getDeclaredMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
