package me.mioclient.mode.client;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import com.mojang.blaze3d.systems.RenderSystem;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SearchMode.class */
public enum SearchMode implements NamedValue {
    NORMAL("Normal"),
    PROTANOPIA("Protanopia") { // from class: me.mioclient.mode.client.SearchMode.Inner
        @Override // me.mioclient.mode.client.SearchMode
        public float[] getShaderColor() {
            return new float[]{0.3f, 0.2f, 1.0f, 1.0f};
        }

        @Override // me.mioclient.mode.client.SearchMode
        public void runWithShaderColor(java.lang.Runnable runnable) {
            float[] floatArray1028 = getShaderColor();
            RenderSystem.setShaderColor(floatArray1028[0], floatArray1028[1], floatArray1028[2], floatArray1028[3]);
            runnable.run();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    },
    DEUTERANOPIA("Deuteranopia") { // from class: me.mioclient.mode.client.SearchMode.Inner_3
        @Override // me.mioclient.mode.client.SearchMode
        public float[] getShaderColor() {
            return new float[]{0.3f, 0.0f, 1.0f, 1.0f};
        }

        @Override // me.mioclient.mode.client.SearchMode
        public void runWithShaderColor(java.lang.Runnable runnable) {
            float[] floatArray1028 = getShaderColor();
            RenderSystem.setShaderColor(floatArray1028[0], floatArray1028[1], floatArray1028[2], floatArray1028[3]);
            runnable.run();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    },
    TRITANOPIA("Tritanopia") { // from class: me.mioclient.mode.client.SearchMode.Inner_2
        @Override // me.mioclient.mode.client.SearchMode
        public float[] getShaderColor() {
            return new float[]{1.0f, 0.7f, 0.0f, 1.0f};
        }

        @Override // me.mioclient.mode.client.SearchMode
        public void runWithShaderColor(java.lang.Runnable runnable) {
            float[] floatArray1028 = getShaderColor();
            RenderSystem.setShaderColor(floatArray1028[0], floatArray1028[1], floatArray1028[2], floatArray1028[3]);
            runnable.run();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    };

    public final String name;

    SearchMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public void runWithShaderColor(java.lang.Runnable runnable) {
        runnable.run();
    }

    public float[] getShaderColor() {
        return new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    }
}
