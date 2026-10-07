package me.mioclient.loader;

import nick.MioModInitializer;

public class ModInitializerBridge {
    public static void do1352(Object obj) {
        MioModInitializer.modules(obj);
    }

    public static void do1353(Object obj) {
        MioModInitializer.commands(obj);
    }

    public static int getIntConstant(long j) {
        return MioModInitializer.getInt(j);
    }

    public static String getStringConstant(int i) {
        return MioModInitializer.getString(i);
    }

    public static Object getObject1356(int i) {
        return MioModInitializer.getConstant(i);
    }

    public static void setup() {
        MioModInitializer.setup();
    }
}
