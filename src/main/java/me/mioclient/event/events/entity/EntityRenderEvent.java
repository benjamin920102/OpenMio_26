package me.mioclient.event.events.entity;

import me.mioclient.event.Event;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EntityEvent_2.class */
public class EntityRenderEvent extends Event {
    public Entity entity;
    public MatrixStack matrixStack;
    public VertexConsumerProvider vertexConsumerProvider;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/EntityEvent_2$Inner.class */
    public static class Inner extends EntityRenderEvent {
        public static final Inner inner = new Inner();

        public static Inner of(Entity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
            inner.entity = entity;
            inner.matrixStack = matrixStack;
            inner.vertexConsumerProvider = vertexConsumerProvider;
            inner.resetCancelled();
            return inner;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/EntityEvent_2$Inner_2.class */
    public static class Inner_2 extends EntityRenderEvent {
        public static final Inner_2 inner_2 = new Inner_2();

        public static Inner_2 getInner_21412(Entity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
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

    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public VertexConsumerProvider getVertexConsumerProvider() {
        return this.vertexConsumerProvider;
    }
}
