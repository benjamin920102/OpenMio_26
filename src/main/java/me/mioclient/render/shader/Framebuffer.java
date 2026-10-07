package me.mioclient.render.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Framebuffer {
    public static final int num = 2;
    public static final int num2 = 8;
    public static final Map<Integer, Framebuffer> map = new HashMap<>();
    public static final List<Framebuffer> list = new ArrayList<>();
    public final int num3;
    public int num4;
    public int num5;
    public boolean flag;
    public int textureWidth = -1;
    public int textureHeight = -1;
    public int fbo = -1;

    public Framebuffer(int i) {
        int clamp = Math.clamp(i, 2, num2);
        if ((clamp & (clamp - 1)) != 0) {
            throw new IllegalArgumentException(String.valueOf(clamp));
        }
        this.num3 = clamp;
    }

    public static boolean hasActiveBuffers() {
        return !list.isEmpty();
    }

    public static Framebuffer getOrCreatePooled(int i) {
        return map.computeIfAbsent(Integer.valueOf(i), num3 -> new Framebuffer(i));
    }

    public static void renderTempAndBlit(int i, java.lang.Runnable runnable) {
        runnable.run();
    }

    public static void renderTempAndSwap(int i, Object framebuffer, java.lang.Runnable runnable) {
        runnable.run();
    }

    public void resize(int i, int i2, boolean z) {
        this.textureWidth = i;
        this.textureHeight = i2;
    }

    public void setClearColor(float r, float g, float b, float a) {
    }

    public void clear(boolean bl) {
    }

    public void beginWrite(boolean z) {
        if (this.flag) {
            return;
        }
        list.add(this);
        this.flag = true;
    }

    public void endWrite() {
        if (this.flag) {
            this.flag = false;
            list.remove(this);
        }
    }

    public void delete() {
        endWrite();
        this.fbo = -1;
        this.textureWidth = -1;
        this.textureHeight = -1;
    }

    public int getColorAttachment() {
        return -1;
    }
}
