package me.mioclient.event.events.entity;

import me.mioclient.event.Event;

import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;

public class EntityRenderEvent extends Event {
    public Entity entity;
    public PoseStack matrixStack;
    public SubmitNodeCollector vertexConsumerProvider;

    
    public static class Inner extends EntityRenderEvent {
        public static final Inner inner = new Inner();

        public static Inner of(Entity entity, PoseStack matrixStack, SubmitNodeCollector vertexConsumerProvider) {
            inner.entity = entity;
            inner.matrixStack = matrixStack;
            inner.vertexConsumerProvider = vertexConsumerProvider;
            inner.resetCancelled();
            return inner;
        }
    }

    
    public static class Inner_2 extends EntityRenderEvent {
        public static final Inner_2 inner_2 = new Inner_2();

        public static Inner_2 getInner_21412(Entity entity, PoseStack matrixStack, SubmitNodeCollector vertexConsumerProvider) {
            inner_2.entity = entity;
            inner_2.matrixStack = matrixStack;
            inner_2.vertexConsumerProvider = vertexConsumerProvider;
            inner_2.resetCancelled();
            return inner_2;
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public PoseStack getMatrixStack() {
        return this.matrixStack;
    }

    public SubmitNodeCollector getVertexConsumerProvider() {
        return this.vertexConsumerProvider;
    }
}
