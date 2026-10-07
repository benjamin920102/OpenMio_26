
package nick;

import me.mioclient.loader.ClientBootstrap;

import java.lang.invoke.MethodHandles;
import nick.CommandRegistry;
import nick.IntegerLookupTable;
import nick.ModuleRegistry;
import nick.StringLookupTable;

public final class MioModInitializer
implements net.fabricmc.api.ModInitializer {
    public void onInitialize() {
        try {
            MethodHandles.lookup().ensureInitialized(Class.forName("me.mioclient.loader.ClientBootstrap"));
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }

    public static int getInt(long key) {
        return IntegerLookupTable.getInt(key);
    }

    public static String getString(int key) {
        return StringLookupTable.getString(key);
    }

    public static Object getConstant(int key) {
        return switch (key) {
            case 8242560 -> 45;
            case 6040919 -> 90;
            case 6786675 -> 360;
            case 5150641 -> Math.PI;
            case 5148802 -> 0.5;
            case 7797789 -> 0.25;
            case 5314762 -> Float.valueOf((float)Math.PI / 180);
            case 4647205 -> Float.valueOf(57.29578f);
            default -> throw new RuntimeException(String.valueOf(key));
        };
    }

    public static void setup() {
        try {
            Class<?> k = Class.forName("me.mioclient.util.client.MusicTrackState");
            k.getDeclaredField("num").set(null, 5);
            k.getDeclaredField("string").set(null, "");
            k.getDeclaredField("string2").set(null, "");
            k.getDeclaredField("num2").set(null, 0L);
            k.getDeclaredField("num3").set(null, 0L);
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }

    public static void modules(Object manager) {
        ModuleRegistry.initialize(manager);
        ModuleRegistry.extra(manager);
    }

    public static void commands(Object manager) {
        CommandRegistry.initialize(manager);
    }

}

