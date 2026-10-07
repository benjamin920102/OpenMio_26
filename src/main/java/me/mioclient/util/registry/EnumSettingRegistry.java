package me.mioclient.util.registry;

import me.mioclient.api.Experimental;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.fabricmc.loader.api.FabricLoader;

public class EnumSettingRegistry {
    public static boolean flag;

    public static void unregisterExperimentalSettings(Module module) {
        for (Field field : module.getClass().getDeclaredFields()) {
            if (isExperimentalField(field)) {
                field.setAccessible(true);
                try {
                    module.unregister((Setting<?>) field.get(module));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isExperimentalClass(Class<?> cls) {
        return isExperimentalDisabled() && hasExperimentalAnnotation(cls);
    }

    public static boolean isExperimentalEnumValue(Enum<?> r3) {
        try {
            if (isExperimentalDisabled()) {
                if (hasExperimentalAnnotation(r3.getDeclaringClass().getDeclaredField(r3.name()))) {
                    return true;
                }
            }
            return false;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static boolean isExperimentalField(Field field) {
        return isExperimentalDisabled() && hasExperimentalAnnotation(field);
    }

    public static boolean isExperimentalDisabled() {
        return !flag;
    }

    public static boolean hasExperimentalAnnotation(AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(Experimental.class);
    }

    static {
        flag = false;
        String[] launchArguments = FabricLoader.getInstance().getLaunchArguments(true);
        int length = launchArguments.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if ("--mio-allow-experimental".equals(launchArguments[i])) {
                flag = true;
                break;
            }
            i++;
        }
        if (System.getProperties().get("mio.allowExperimental") != null && "true".equalsIgnoreCase((String) System.getProperties().get("mio.allowExperimental"))) {
            flag = true;
        }
    }
}
