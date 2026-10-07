package me.mioclient.mode.render;

import me.mioclient.api.NamedValue;

import me.mioclient.module.render.chams.ChamsColorApplier;

import java.awt.Color;
import me.mioclient.module.render.Chams;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;

public enum ChamsMode implements NamedValue, ChamsColorApplier {
    BOTH("Both"),
    FILL("Fill") { // from class: me.mioclient.mode.render.ChamsMode.Inner_3
        @Override // me.mioclient.module.render.chams.ChamsColorApplier
        public Color[] getChamsColors(Chams chams) {
            return new Color[]{color, chams.fill.getValue()};
        }
    },
    LINE("Line") { // from class: me.mioclient.mode.render.ChamsMode.Inner_2
        @Override // me.mioclient.module.render.chams.ChamsColorApplier
        public Color[] getChamsColors(Chams chams) {
            return new Color[]{chams.outline.getValue(), color};
        }
    },
    PLAIN("Plain") { // from class: me.mioclient.mode.render.ChamsMode.Inner
        @Override // me.mioclient.module.render.chams.ChamsColorApplier
        public void applyChamsRender(Chams chams, Entity entity, PoseStack matrixStack) {
        }
    },
    OFF("Off");

    public final String name;

    ChamsMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    @Override // me.mioclient.module.render.chams.ChamsColorApplier
    public boolean is594() {
        return this != OFF;
    }
}
