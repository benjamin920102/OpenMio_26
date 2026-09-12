package me.mioclient.mode.client;

import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Mode_5.class */
public enum CursorType {
    STANDARD(() -> {
        return Long.valueOf(GLFW.glfwCreateStandardCursor(221185));
    }),
    POINTER(() -> {
        return Long.valueOf(GLFW.glfwCreateStandardCursor(221188));
    }),
    INPUT(() -> {
        return Long.valueOf(GLFW.glfwCreateStandardCursor(221186));
    });

    public final Supplier<Long> supplier;
    public long num = -1;
    public static CursorType current = STANDARD;

    CursorType(Supplier supplier) {
        this.supplier = supplier;
    }

    public long getCursorHandle() {
        if (this.num == -1) {
            this.num = this.supplier.get().longValue();
        }
        return this.num;
    }

    public void applyCursor() {
        if (this == current) {
            return;
        }
        GLFW.glfwSetCursor(MinecraftClient.getInstance().getWindow().getHandle(), getCursorHandle());
        current = this;
    }
}
