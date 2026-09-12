package me.mioclient.module.movement;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.event.EventBus;

import me.mioclient.render.RenderConstants;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.module.Module;
import net.minecraft.text.Text;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/AutoWalk.class */
public class AutoWalk extends Module {
    public Setting<Boolean> autoJump = add(new BooleanSetting("AutoJump", false));
    public Setting<Boolean> stuckNotify = add(new BooleanSetting("StuckNotify", false).setVisible());
    public Setting<SoundIdentifier> sound = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "warning")).requiresSettings(this.stuckNotify));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 1.0f, 0.0f, 1.0f).requiresSettings(this.stuckNotify));
    public static boolean flag;
    public boolean flag2;

    public AutoWalk() {
        super("AutoWalk", "Lets your forward key rest.", Category.MOVEMENT, new String[0]);
        this.flag2 = true;
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        flag = true;
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (ClientServices.feetPlaceHelper.getEffectiveSpeed() != 0.0d) {
            this.flag2 = false;
            return;
        }
        if (!this.flag2 && this.stuckNotify.getValue().booleanValue()) {
            ChatUtils.sendPrefixedMessage(Text.literal("You are stuck!"), ChatUtils.signatureOfModule(this), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
            ClientServices.killstreakSoundManager.findSoundPlayer(this.sound.getValue()).play(this.volume.getValue().floatValue());
        }
        this.flag2 = true;
    }

    @Listen(get219= RenderConstants.num4)
    public void onInputTick(InputTickEvent tickEvent_2) {
        tickEvent_2.getInput().movementForward = tickEvent_2.hasSpeed() ? tickEvent_2.getSpeed() : Float.intBitsToFloat(1065353216);
        tickEvent_2.getInput().pressingForward = true;
        if (this.autoJump.getValue().booleanValue()) {
            tickEvent_2.getInput().jumping = true;
        }
    }

    @Listen(get219= RenderConstants.num4)
    public static void onInputTickRelease(InputTickEvent tickEvent_2) {
        if (flag) {
            tickEvent_2.getInput().movementForward = 0.0f;
            tickEvent_2.getInput().pressingForward = false;
            tickEvent_2.getInput().jumping = false;
            flag = false;
        }
    }

    static {
        eventBus.registerStatic(AutoWalk.class);
    }
}
