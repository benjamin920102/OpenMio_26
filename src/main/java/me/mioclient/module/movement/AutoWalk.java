package me.mioclient.module.movement;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.event.EventBus;

import me.mioclient.render.RenderConstants;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.network.chat.Component;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

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
            ChatUtils.sendPrefixedMessage(Component.literal("You are stuck!"), ChatUtils.signatureOfModule(this), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
            ClientServices.killstreakSoundManager.findSoundPlayer(this.sound.getValue()).play(this.volume.getValue().floatValue());
        }
        this.flag2 = true;
    }

    @Listen(get219= RenderConstants.num4)
    public void onInputTick(InputTickEvent tickEvent_2) {
        { net.minecraft.client.player.ClientInput _ci = tickEvent_2.getInput(); net.minecraft.world.entity.player.Input _p = _ci.keyPresses; _ci.keyPresses = new net.minecraft.world.entity.player.Input(true, _p.backward(), _p.left(), _p.right(), _p.jump(), _p.shift(), _p.sprint()); }
        if (this.autoJump.getValue().booleanValue()) {
            { net.minecraft.client.player.ClientInput _ci = tickEvent_2.getInput(); net.minecraft.world.entity.player.Input _p = _ci.keyPresses; _ci.keyPresses = new net.minecraft.world.entity.player.Input(_p.forward(), _p.backward(), _p.left(), _p.right(), true, _p.shift(), _p.sprint()); }
        }
    }

    @Listen(get219= RenderConstants.num4)
    public static void onInputTickRelease(InputTickEvent tickEvent_2) {
        if (flag) {
            { net.minecraft.client.player.ClientInput _ci = tickEvent_2.getInput(); net.minecraft.world.entity.player.Input _p = _ci.keyPresses; _ci.keyPresses = new net.minecraft.world.entity.player.Input(false, false, _p.left(), _p.right(), false, _p.shift(), _p.sprint()); }
            flag = false;
        }
    }

    static {
        eventBus.registerStatic(AutoWalk.class);
    }
}