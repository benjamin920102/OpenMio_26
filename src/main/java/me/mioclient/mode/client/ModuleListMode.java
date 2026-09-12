package me.mioclient.mode.client;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.math.Rectangle;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.NamedValue;
import me.mioclient.util.math.Rectangle;

import java.util.function.Supplier;
import net.minecraft.util.math.MathHelper;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ModuleListMode.class */
public enum ModuleListMode implements ClientAccessor, NamedValue {
    TOP_LEFT("top_left", () -> {
        return Rectangle.fromPositionAndSize(0.0f, 0.0f, 10.0f, 10.0f);
    }),
    TOP_RIGHT("top_right", () -> {
        return Rectangle.fromPositionAndSize(minecraftClient.getWindow().getScaledWidth() - 10, 0.0f, 10.0f, 10.0f);
    }),
    BOTTOM_LEFT("bottom_left", () -> {
        return Rectangle.fromPositionAndSize(0.0f, minecraftClient.getWindow().getScaledHeight() - 10, 10.0f, 10.0f);
    }),
    BOTTOM_RIGHT("bottom_right", () -> {
        return Rectangle.fromPositionAndSize(minecraftClient.getWindow().getScaledWidth() - 10, minecraftClient.getWindow().getScaledHeight() - 10, 10.0f, 10.0f);
    }),
    TOP_CENTER("top_center", () -> {
        return Rectangle.fromPositionAndSize((minecraftClient.getWindow().getScaledWidth() / 2.0f) - 10.0f, 0.0f, 10.0f, 10.0f);
    }),
    NONE("none", () -> {
        return Rectangle.fromPositionAndSize(0.0f, 0.0f, minecraftClient.getWindow().getScaledWidth(), minecraftClient.getWindow().getScaledHeight());
    });

    public final String name;
    public final Supplier<Rectangle> supplier;

    ModuleListMode(String str, Supplier supplier) {
        this.name = str;
        this.supplier = supplier;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public boolean containsPoint(double d, double d2) {
        return this.supplier.get().containsPoint(MathHelper.clamp(d, 0.0d, minecraftClient.getWindow().getScaledWidth()), MathHelper.clamp(d2, 0.0d, minecraftClient.getWindow().getScaledHeight()));
    }

    public Rectangle getData_32936() {
        return this.supplier.get();
    }
}
