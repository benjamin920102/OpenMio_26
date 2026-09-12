package me.mioclient.module.player;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.MathConstants;

import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import me.mioclient.module.render.FreeLook;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/RotationLock.class */
public class RotationLock extends Module {
    public static FreeLook freelook = (FreeLook) ClientServices.moduleInjector.getModule(FreeLook.class);
    public Setting<Boolean> yaw = add(new BooleanSetting("Yaw", true).setVisible());
    public Setting<Boolean> custom = add(new BooleanSetting("Custom", false).requiresSettings(this.yaw));
    public Setting<Float> value2 = add(new NumberSetting<>("Value", 0.0f, -180.0f, 180.0f).requiresSettings(this.yaw, this.custom));
    public Setting<Boolean> pitch = add(new BooleanSetting("Pitch", false).setVisible());
    public Setting<Float> value = add(new NumberSetting<>("Value", 0.0f, -90.0f, 90.0f).requiresSettings(this.pitch));

    public RotationLock() {
        super("RotationLock", "Locks your rotation.", Category.PLAYER, new String[0]);
        this.value2.setConfigName("YawValue");
        this.value.setConfigName("PitchValue");
    }

    @Listen(get219= RenderConstants.num)
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Pre && freelook.isToggled()) {
            if (this.yaw.getValue().booleanValue()) {
                motionEvent.setYaw(getYaw());
            }
            if (this.pitch.getValue().booleanValue()) {
                motionEvent.setPitch(getPitch());
            }
        }
    }

    @Listen(get219= RenderConstants.num)
    public void onRotation(RotationEvent event_3) {
        if (freelook.isToggled()) {
            if (this.yaw.getValue().booleanValue()) {
                event_3.setYaw(getYaw());
                event_3.cancel();
            }
            if (this.pitch.getValue().booleanValue()) {
                event_3.setPitch(getPitch());
                event_3.cancel();
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.yaw.getValue().booleanValue()) {
            minecraftClient.player.setYaw(getYaw());
        }
        if (this.pitch.getValue().booleanValue()) {
            minecraftClient.player.setPitch(getPitch());
        }
    }

    public float getYaw() {
        return !this.custom.getValue().booleanValue() ? Math.round((minecraftClient.player.getYaw() + Float.intBitsToFloat(1065353216)) / MathConstants.num) * MathConstants.num : this.value2.getValue().floatValue();
    }

    public float getPitch() {
        return this.value.getValue().floatValue();
    }
}
