package me.mioclient.render.chams;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;

public interface EntityRenderCallback<T extends Entity> {
    void do721(T t, float f, PoseStack matrixStack);
}
