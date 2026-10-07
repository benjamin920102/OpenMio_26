package me.mioclient.api;

import me.mioclient.event.ReflectiveEventBus;

import me.mioclient.event.EventBus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import me.mioclient.loader.Globals;

public interface ClientAccessor {
    public static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
    public static final Minecraft minecraftClient = Minecraft.getInstance();
    public static final ReflectiveEventBus eventBus = new Inner();
    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    
    class Inner extends ReflectiveEventBus {
        public Inner() {
            registerLookup("me.mioclient", (method, cls) -> {
                Object[] objArr = new Object[2];
                objArr[0] = cls;
                try {
                    objArr[1] = (MethodHandles.Lookup) MethodHandles.class.getDeclaredMethod("lookup", new Class[0]).invoke(null, new Object[0]);
                    return (MethodHandles.Lookup) method.invoke(null, objArr);
                } catch (Exception unused) {
                    throw new java.lang.RuntimeException(unused);
                }
            });
        }
    }

    default boolean isNotInGame() {
        return minecraftClient.player == null || minecraftClient.level == null;
    }

    static boolean isLocalPlayer(Object obj) {
        return minecraftClient.player == obj;
    }

    static boolean isGlobalsLoaded() {
        try {
            Class.forName("me.mioclient.loader.Globals", false, ClientAccessor.class.getClassLoader());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
