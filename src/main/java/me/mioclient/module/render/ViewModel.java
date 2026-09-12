package me.mioclient.module.render;

import me.mioclient.module.render.viewmodel.ViewModelEditorScreen;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.util.Hand;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/ViewModel.class */
public class ViewModel extends Module {
    public static final String string = "Â°";
    public Setting<Boolean> adjust = add(new BooleanSetting("Adjust", false));
    public Setting<Boolean> shadow = add(new BooleanSetting("Shadow", true));
    public Setting<Boolean> noSway = add(new BooleanSetting("NoSway", false));
    public Setting<Boolean> instantSwap = add(new BooleanSetting("InstantSwap", false));
    public Setting<Boolean> eating = add(new BooleanSetting("Eating", true));
    public Setting<Boolean> mainHand2 = add(new BooleanSetting("MainHand", true).setDependent());
    public Setting<Boolean> arm = add(new BooleanSetting("Arm", false).requiresSettings(this.mainHand2));
    public Setting<Float> mainX = add(new NumberSetting<>("MainX", 0.0f, -1.0f, 1.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainY = add(new NumberSetting<>("MainY", 0.0f, -1.0f, 1.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainZ = add(new NumberSetting<>("MainZ", 0.0f, -1.0f, 1.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainScaleX = add(new NumberSetting<>("MainScaleX", 1.0f, 0.0f, 2.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainScaleY = add(new NumberSetting<>("MainScaleY", 1.0f, 0.0f, 2.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainScaleZ = add(new NumberSetting<>("MainScaleZ", 1.0f, 0.0f, 2.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainRotateX = add(new NumberSetting<>("MainRotateX", 0.0f, 0.0f, 360.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainRotateY = add(new NumberSetting<>("MainRotateY", 0.0f, 0.0f, 360.0f).requiresSettings(this.mainHand2));
    public Setting<Float> mainRotateZ = add(new NumberSetting<>("MainRotateZ", 0.0f, 0.0f, 360.0f).requiresSettings(this.mainHand2));
    public Setting<Boolean> offHand = add(new BooleanSetting("OffHand", true).setDependent());
    public Setting<Float> offX = add(new NumberSetting<>("OffX", 0.0f, -1.0f, 1.0f).requiresSettings(this.offHand));
    public Setting<Float> offY = add(new NumberSetting<>("OffY", 0.0f, -1.0f, 1.0f).requiresSettings(this.offHand));
    public Setting<Float> offZ = add(new NumberSetting<>("OffZ", 0.0f, -1.0f, 1.0f).requiresSettings(this.offHand));
    public Setting<Float> offScaleX = add(new NumberSetting<>("OffScaleX", 1.0f, 0.0f, 2.0f).requiresSettings(this.offHand));
    public Setting<Float> offScaleY = add(new NumberSetting<>("OffScaleY", 1.0f, 0.0f, 2.0f).requiresSettings(this.offHand));
    public Setting<Float> offScaleZ = add(new NumberSetting<>("OffScaleZ", 1.0f, 0.0f, 2.0f).requiresSettings(this.offHand));
    public Setting<Float> offRotateX = add(new NumberSetting<>("OffRotateX", 0.0f, 0.0f, 360.0f).requiresSettings(this.offHand));
    public Setting<Float> offRotateY = add(new NumberSetting<>("OffRotateY", 0.0f, 0.0f, 360.0f).requiresSettings(this.offHand));
    public Setting<Float> offRotateZ = add(new NumberSetting<>("OffRotateZ", 0.0f, 0.0f, 360.0f).requiresSettings(this.offHand));
    public Setting<Boolean> misc = add(new BooleanSetting("Misc", false).setDependent());
    public Setting<Float> eatMultiplier = add(new NumberSetting<>("EatMultiplier", 1.0f, 0.0f, 1.0f).requiresSettings(this.misc));
    public Setting<Boolean> noTridentAnim = add(new BooleanSetting("NoTridentAnim", false).requiresSettings(this.misc));
    public Setting<Boolean> swingProgress = add(new BooleanSetting("SwingProgress", false).setVisible().requiresSettings(this.misc));
    public Setting<Boolean> static_ = add(new BooleanSetting("Static", true).requiresSettings(this.misc, this.swingProgress));
    public Setting<Boolean> mainHand = add(new BooleanSetting("MainHand", false).requiresSettings(this.misc, this.swingProgress));
    public Setting<Float> swingProgressAmount2 = add(new NumberSetting<>("SwingProgressAmount", 1.0f, 0.0f, 1.0f).requiresSettings(this.misc, this.swingProgress, this.mainHand));
    public Setting<Boolean> offHand2 = add(new BooleanSetting("OffHand", false).requiresSettings(this.misc, this.swingProgress));
    public Setting<Float> swingProgressAmount = add(new NumberSetting<>("SwingProgressAmount", 1.0f, 0.0f, 1.0f).requiresSettings(this.misc, this.swingProgress, this.offHand2));
    public Setting<Boolean> viewModelFov = add(new BooleanSetting("ViewModelFov", false).setVisible().requiresSettings(this.misc));
    public Setting<Integer> fovAmount = add(new NumberSetting<>("FovAmount", 90, 70, 180).requiresSettings(this.misc, this.viewModelFov));

    public ViewModel() {
        super("ViewModel", "Transforms your 1st person view model.", Category.RENDER, new String[0]);
        this.mainHand.setConfigName("SwingMainHand");
        this.offHand2.setConfigName("SwingOffHand");
        this.swingProgressAmount.setConfigName("SwingProgressAmountOffHand");
        this.adjust.setChangeCallback(() -> {
            if (this.adjust.getValue().booleanValue()) {
                minecraftClient.setScreen(new ViewModelEditorScreen(this));
                this.adjust.setValue(false);
            }
        });
    }

    public float get3142(Hand hand, float f) {
        if (!isToggled() || !this.swingProgress.getValue().booleanValue()) {
            return f;
        }
        if (hand == Hand.MAIN_HAND && this.mainHand.getValue().booleanValue()) {
            return Math.max(this.static_.getValue().booleanValue() ? 0.0f : f, this.swingProgressAmount2.getValue().floatValue());
        }
        if (hand == Hand.OFF_HAND && this.offHand2.getValue().booleanValue()) {
            return Math.max(this.static_.getValue().booleanValue() ? 0.0f : f, this.swingProgressAmount.getValue().floatValue());
        }
        return f;
    }
}
