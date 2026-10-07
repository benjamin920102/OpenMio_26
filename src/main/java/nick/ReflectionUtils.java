
package nick;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

// 26.2 / Java 25: sun.misc.Unsafe accessors (getBoolean/putBoolean @ offset 12,
// getDeclaredMethods0) are deprecated for removal. Replaced with plain
// reflection + setAccessible, mirroring ModuleFieldInjector's approach
// (no Unsafe, no --add-opens needed for our own classpath).
public class ReflectionUtils {
    public static <T extends AccessibleObject> boolean patched(T obj) {
        try {
            return obj.canAccess(null);
        } catch (Exception e) {
            return true;
        }
    }

    public static <T extends AccessibleObject> void patch(T obj) {
        try {
            obj.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    public static Method getMethod(Class<?> klass, String name, Class<?>... parameters) {
        if (klass == null) {
            throw new RuntimeException("no method found");
        }
        for (Method method : klass.getDeclaredMethods()) {
            if (!method.getName().equals(name) || !Arrays.equals(method.getParameterTypes(), parameters)) continue;
            ReflectionUtils.patch(method);
            return method;
        }
        return ReflectionUtils.getMethod(klass.getSuperclass(), name, parameters);
    }

    public static Field getField(Class<?> klass, String name) {
        if (klass == null) {
            throw new RuntimeException("no field found");
        }
        for (Field field : klass.getDeclaredFields()) {
            if (!field.getName().equals(name)) continue;
            ReflectionUtils.patch(field);
            return field;
        }
        return ReflectionUtils.getField(klass.getSuperclass(), name);
    }

    public static <T> Constructor<T> getConstructors(Class<?> klass, Class<?>... parameters) {
        for (Constructor<?> constructor : klass.getDeclaredConstructors()) {
            if (!Arrays.equals(constructor.getParameterTypes(), parameters)) continue;
            ReflectionUtils.patch(constructor);
            @SuppressWarnings("unchecked")
            Constructor<T> typed = (Constructor<T>) constructor;
            return typed;
        }
        throw new RuntimeException("no constructor found");
    }

    @SuppressWarnings("unchecked")
    public static <T extends AccessibleObject> T[] getMembers(Class<?> klass, Mask mask) {
        switch (mask) {
            case METHODS:
                return (T[]) klass.getDeclaredMethods();
            case FIELDS:
                return (T[]) klass.getDeclaredFields();
            case CONSTRUCTORS:
                return (T[]) klass.getDeclaredConstructors();
            default:
                throw new RuntimeException();
        }
    }

    public enum Mask {
        METHODS,
        FIELDS,
        CONSTRUCTORS
    }
}
