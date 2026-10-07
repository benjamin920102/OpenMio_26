package me.mioclient.util.client;

import me.mioclient.api.ClientAccessor;

import net.fabricmc.loader.api.FabricLoader;

public class GuiScaleOverride implements ClientAccessor {
    public static double val;
    public static boolean flag = FabricLoader.getInstance().isModLoaded("advanced-ui-scale");

    public static void injectModuleFields() {
        applyScale(Float.intBitsToFloat(1065353216));
    }

    public static void applyScale(float f) {
        if (isScaleModLoaded()) {
            return;
        }
        val = minecraftClient.getWindow().getGuiScale();
        minecraftClient.getWindow().setGuiScale(Math.max(1, (int) (getBaseScaleFactor() * f)));
    }

    public static void do604() {
        if (isScaleModLoaded()) {
            return;
        }
        if (val == Double.longBitsToDouble(-4616189618054758400L)) {
            throw new UnsupportedOperationException();
        }
        minecraftClient.getWindow().setGuiScale((int) val);
        val = Double.longBitsToDouble(-4616189618054758400L);
    }

    public static float getBaseScaleFactor() {
        return minecraftClient.getWindow().calculateScale((Integer) minecraftClient.options.guiScale().get(), (Boolean) minecraftClient.options.forceUnicodeFont().get());
    }

    public static double getScaleMultiplier(float f) {
        return isScaleModLoaded() ? Double.longBitsToDouble(4607182418800017408L) : (getBaseScaleFactor() / minecraftClient.getWindow().getGuiScale()) * f;
    }

    public static boolean isScaleModLoaded() {
        return flag;
    }
}
