package me.mioclient.module.render.esp;

import me.mioclient.api.NamedValue;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;

public enum ESPRenderMode implements NamedValue {
    BOX("AABB"),
    TEXT("Component"),
    BOTH("Both");

    public final String name;

    ESPRenderMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}