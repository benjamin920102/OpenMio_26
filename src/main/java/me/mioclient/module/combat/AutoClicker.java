package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.mixin.ducks.DuckMinecraftClient;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.exploit.MultiTask;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/AutoClicker.class */
public class AutoClicker extends Module {
    public static final MultiTask multitask = (MultiTask) ClientServices.moduleInjector.getModule(MultiTask.class);
    public Setting<AutoClickerMode> mode = add(new EnumSetting<>("Mode", AutoClickerMode.LEFT));
    public Setting<Integer> cps = add(new NumberSetting<>("CPS", 8, 1, 20));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 1.0f, 1.0f, 3.0f).setSuffix("x").setHoleSnapConfig("Off", HoleSnapMode.MIN));
    public Setting<Boolean> friendProtect = add(new BooleanSetting("FriendProtect", true));
    public final Stopwatch stopwatch;
    public float val;
    public final List<Long> list;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/AutoClicker$AutoClickerMode.class */
    public enum AutoClickerMode implements NamedValue {
        LEFT("Left"),
        RIGHT("Right");

        public final String name;

        AutoClickerMode(String str) {
            this.name = str;
        }

        public void executeClickAction() {
            if (this == LEFT) {
                ((DuckMinecraftClient)(Object) ClientAccessor.minecraftClient).attack();
            } else {
                ((DuckMinecraftClient)(Object) ClientAccessor.minecraftClient).interact();
            }
        }

        public KeyBinding getKeyBinding2026() {
            return this == LEFT ? ClientAccessor.minecraftClient.options.attackKey : ClientAccessor.minecraftClient.options.useKey;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public AutoClicker() {
        super("AutoClicker", "Spams attack as you hold down the attack button.", Category.COMBAT, new String[0]);
        this.stopwatch = new Stopwatch();
        this.val = Float.intBitsToFloat(1065353216);
        this.list = new ArrayList();
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return String.valueOf(this.list.size());
    }

    @Listen
    public void onRenderWorldPre(MatrixStackEvent.Inner inner) {
        this.list.removeIf(l -> {
            return System.currentTimeMillis() - l.longValue() >= 1000;
        });
        long intValue = 1000 / this.cps.getValue().intValue();
        if (!this.delay.isAtMinimum()) {
            intValue = (long) (((float) intValue) * this.val);
        }
        if (this.stopwatch.hasElapsedMs(intValue)) {
            performClick();
            this.list.add(Long.valueOf(System.currentTimeMillis()));
            this.stopwatch.reset();
            this.val = MathUtils.randomFloat(Float.intBitsToFloat(1065353216), this.delay.getValue().floatValue());
        }
    }

    public boolean is1073(Entity entity) {
        return isToggled() && this.friendProtect.getValue().booleanValue() && (entity instanceof PlayerEntity) && ClientServices.socialManager.isFriendEntity((PlayerEntity) entity);
    }

    public void performClick() {
        if (!multitask.isToggled() || (!minecraftClient.player.isUsingItem() && minecraftClient.currentScreen == null)) {
            KeyBinding.onKeyPressed(this.mode.getValue().getKeyBinding2026().boundKey);
        } else {
            this.mode.getValue().executeClickAction();
        }
    }
}
