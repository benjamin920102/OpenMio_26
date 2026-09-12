package me.mioclient.module.client;

import me.mioclient.event.events.client.ClientTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.module.KeybindModule;

import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.event.EventBus;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import java.awt.Color;
import me.mioclient.event.events.client.ClientTickEvent;
import me.mioclient.module.KeybindModule;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.movement.Jesus;
import me.mioclient.module.movement.SafeWalk;
import me.mioclient.module.movement.Step;
import me.mioclient.module.player.AutoTool;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/client/Baritone.class */
public class Baritone extends KeybindModule {
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public static final Step step = (Step) ClientServices.moduleInjector.getModule(Step.class);
    public static final AutoTool autoTool = (AutoTool) ClientServices.moduleInjector.getModule(AutoTool.class);
    public static SafeWalk safeWalk = (SafeWalk) ClientServices.moduleInjector.getModule(SafeWalk.class);
    public static Jesus jesus = (Jesus) ClientServices.moduleInjector.getModule(Jesus.class);
    public Setting<Boolean> setting;
    public Setting<Boolean> setting2;
    public Setting<Boolean> setting3;
    public Setting<Boolean> setting4;
    public Setting<Boolean> setting5;
    public Setting<Color> setting6;
    public Setting<Color> setting7;
    public boolean flag;
    public boolean flag2;

    public Baritone() {
        super("Baritone", "Manages baritone settings.", Category.CLIENT, new String[0]);
        BaritoneAPI.getSettings().chatControl.value = false;
        eventBus.register(this);
    }

    @Listen
    public void onClientTick(ClientTickEvent fontsEvent) {
        if (this.setting == null || this.setting2 == null || this.setting3 == null || this.setting4 == null || this.setting5 == null || this.setting6 == null || this.setting7 == null) {
            return;
        }
        syncAntiCheatCompat();
        BaritoneAPI.getSettings().disconnectOnArrival.value = this.setting2.getValue();
        BaritoneAPI.getSettings().freeLook.value = this.setting.getValue();
        BaritoneAPI.getSettings().blockFreeLook.value = this.setting.getValue();
        BaritoneAPI.getSettings().elytraFreeLook.value = this.setting.getValue();
        BaritoneAPI.getSettings().censorCoordinates.value = this.setting4.getValue();
        BaritoneAPI.getSettings().censorRanCommands.value = this.setting4.getValue();
        if (this.setting5.getValue().booleanValue()) {
            Color color816 = ColorUtils.withAlpha(this.setting6.getValue(), 255);
            Color color8162 = ColorUtils.withAlpha(this.setting7.getValue(), 255);
            BaritoneAPI.getSettings().colorBestPathSoFar.value = color816;
            BaritoneAPI.getSettings().colorGoalBox.value = color8162;
            BaritoneAPI.getSettings().colorInvertedGoalBox.value = color8162;
            BaritoneAPI.getSettings().colorCurrentPath.value = color816;
            BaritoneAPI.getSettings().colorMostRecentConsidered.value = color816;
            BaritoneAPI.getSettings().colorBlocksToBreak.value = color816;
            BaritoneAPI.getSettings().colorBlocksToPlace.value = color816;
            BaritoneAPI.getSettings().colorBlocksToWalkInto.value = color816;
            BaritoneAPI.getSettings().colorNextPath.value = color816;
            BaritoneAPI.getSettings().pathRenderLineWidthPixels.value = Float.valueOf(Float.intBitsToFloat(1069547520));
            BaritoneAPI.getSettings().goalRenderLineWidthPixels.value = Float.valueOf(Float.intBitsToFloat(1069547520));
        }
        if (this.setting3.getValue().booleanValue()) {
            BaritoneAPI.getSettings().assumeStep.value = Boolean.valueOf(step.isToggled());
            BaritoneAPI.getSettings().assumeExternalAutoTool.value = Boolean.valueOf(autoTool.isToggled());
            BaritoneAPI.getSettings().assumeSafeWalk.value = Boolean.valueOf(safeWalk.isToggled());
            BaritoneAPI.getSettings().assumeWalkOnWater.value = Boolean.valueOf(jesus.isToggled() && jesus.canJesus());
        }
    }

    public void syncAntiCheatCompat() {
        Settings.Setting setting = BaritoneAPI.getSettings().antiCheatCompatibility;
        boolean isYawSpoofActive = antiCheat.getYawSpoofHandler().isYawSpoofActive();
        if (isYawSpoofActive != this.flag2) {
            if (isYawSpoofActive) {
                this.flag = ((Boolean) setting.value).booleanValue();
            } else {
                setting.value = Boolean.valueOf(this.flag);
            }
        } else if (isYawSpoofActive) {
            setting.value = false;
        }
        this.flag2 = isYawSpoofActive;
    }
}
