package me.mioclient.event.events.render;

import me.mioclient.event.Event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;

public class RenderLabelEvent extends Event {
    public final PoseStack matrixStack;
    public final Entity entity;

    public RenderLabelEvent(PoseStack matrixStack, Entity entity) {
        this.matrixStack = matrixStack;
        this.entity = entity;
    }

    public PoseStack getMatrixStack() {
        return this.matrixStack;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
