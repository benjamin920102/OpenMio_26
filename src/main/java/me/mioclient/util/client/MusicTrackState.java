package me.mioclient.util.client;

import me.mioclient.loader.ModInitializerBridge;

public class MusicTrackState {
    public static int num;
    public static String string;
    public static String string2;
    public static long num2;
    public static long num3;

    public static String getString2782() {
        return string + " - " + string2;
    }

    static {
        ModInitializerBridge.setup();
    }
}
