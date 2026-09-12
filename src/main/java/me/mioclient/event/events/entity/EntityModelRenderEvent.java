package me.mioclient.event.events.entity;

import me.mioclient.event.Event;

import java.awt.Color;
import net.minecraft.entity.Entity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EntityEvent.class */
public class EntityModelRenderEvent extends Event {
    public static final EntityModelRenderEvent entityEvent = new EntityModelRenderEvent();
    public Entity entity;
    public int num;
    public float val;
    public float val2;
    public float val3;
    public float val4;

    public static EntityModelRenderEvent create(Entity entity, int i, float f, float f2, float f3, float f4) {
        entityEvent.num = i;
        entityEvent.entity = entity;
        entityEvent.val = f;
        entityEvent.val2 = f2;
        entityEvent.val3 = f3;
        entityEvent.val4 = f4;
        entityEvent.resetCancelled();
        return entityEvent;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public float getRed() {
        return this.val;
    }

    public void setRed(float f) {
        this.val = f;
    }

    public float getGreen() {
        return this.val2;
    }

    public void setGreen(float f) {
        this.val2 = f;
    }

    public float getBlue() {
        return this.val3;
    }

    public void setBlue(float f) {
        this.val3 = f;
    }

    public float getAlpha() {
        return this.val4;
    }

    public void setAlpha(float f) {
        this.val4 = f;
    }

    public int getLight() {
        return this.num;
    }

    public void setLight(int i) {
        this.num = i;
    }

    public void setColor(Color color) {
        setRed(color.getRed() / 255.0f);
        setGreen(color.getGreen() / 255.0f);
        setBlue(color.getBlue() / 255.0f);
        setAlpha(color.getAlpha() / 255.0f);
    }
}
