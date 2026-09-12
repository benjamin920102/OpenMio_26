package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.player.SpeedMineMode;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.network.PacketSender;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;

import java.util.Iterator;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.misc.AntiAim;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import me.mioclient.mode.player.SpeedMineMode;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/AutoExp.class */
public class AutoExp extends Module {
    public Setting<SpeedMineMode> mode = add(new EnumSetting<>("Mode", SpeedMineMode.SILENT));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 2, 0, 5));
    public Setting<Integer> frequency = add(new NumberSetting<>("Frequency", 1, 1, 4));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false));
    public Setting<Boolean> air = add(new BooleanSetting("Air", true));
    public Setting<Boolean> stop = add(new BooleanSetting("Stop", true).setVisible());
    public Setting<Integer> stopAt = add(new NumberSetting<>("StopAt", 100, 80, 100, v -> this.stop.isShown()).setSuffix("%"));
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", false).requiresSettings(this.stop));
    public Setting<Boolean> tools = add(new BooleanSetting("Tools", false).requiresSettings(this.stop));
    public static AntiAim antiAim = (AntiAim) ClientServices.moduleInjector.getModule(AntiAim.class);
    public final Stopwatch stopwatch;
    public int num;
    public boolean flag;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/AutoExp$SpeedMineMode.class */
    public enum SpeedMineMode implements NamedValue {
        NORMAL("Normal"),
        SILENT("Silent");

        public final String name;

        SpeedMineMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public AutoExp() {
        super("AutoExp", "Mends your armor for you.", Category.COMBAT, new String[0]);
        this.stopwatch = new Stopwatch();
        this.flag = false;
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame()) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            this.stopwatch.reset();
        }
        if (antiAim.isToggled() && !this.flag) {
            antiAim.resetActionTicks();
        }
        this.num = minecraftClient.player.getInventory().selectedSlot;
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (isNotInGame() || minecraftClient.player.getInventory().selectedSlot == this.num || this.mode.getValue() != SpeedMineMode.NORMAL) {
            return;
        }
        InventoryUtils.selectSlot(this.num);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (this.flag) {
            return "Stopped";
        }
        return String.valueOf(minecraftClient.player.getInventory().main.stream().filter(itemStack -> {
            return itemStack.getItem() == Items.EXPERIENCE_BOTTLE;
        }).mapToInt((v0) -> {
            return v0.getCount();
        }).sum());
    }

    @Listen(get219= 250)
    public void onMotion(MotionEvent motionEvent) {
        if (!shouldPause() && motionEvent.getPhase() == KeyPearlMode.Pre) {
            float[] floatArray2484 = RotationManager.getRotationsTo(BoxMath.getBottomCenter(ClientServices.mainhandHelper.getPredictedBox(minecraftClient.player, 1)));
            if (!this.flag && this.rotate.getValue().booleanValue()) {
                ClientServices.RotationManager.requestRotations(floatArray2484, 4, true);
            }
            boolean isHolding = minecraftClient.player.isHolding(Items.EXPERIENCE_BOTTLE);
            int i = InventoryUtils.findInHotbar(Items.EXPERIENCE_BOTTLE);
            if (this.stop.getValue().booleanValue()) {
                boolean z = true;
                Iterator it = minecraftClient.player.getInventory().armor.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (isBelowMendThreshold((ItemStack) it.next())) {
                        z = false;
                        break;
                    }
                }
                if ((isBelowMendThreshold(minecraftClient.player.getMainHandStack()) || isBelowMendThreshold(minecraftClient.player.getOffHandStack())) && this.tools.getValue().booleanValue()) {
                    z = false;
                }
                if (z || i == -1) {
                    this.flag = true;
                    if (this.autoDisable.getValue().booleanValue()) {
                        disable();
                        return;
                    }
                    return;
                }
            }
            if (i != -1 || isHolding) {
                if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue() * 50)) {
                    Hand hand = minecraftClient.player.getOffHandStack().isOf(Items.EXPERIENCE_BOTTLE) ? Hand.OFF_HAND : Hand.MAIN_HAND;
                    this.flag = false;
                    int i2 = minecraftClient.player.getInventory().selectedSlot;
                    if (minecraftClient.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE && hand == Hand.MAIN_HAND) {
                        InventoryUtils.selectSlot(i);
                    }
                    for (int i3 = 0; i3 < this.frequency.getValue().intValue(); i3++) {
                        if (this.rotate.getValue().booleanValue()) {
                            PacketSender.sendInteractItem(hand, floatArray2484[0], floatArray2484[1]);
                        } else {
                            PacketSender.sendInteractItem(hand);
                        }
                    }
                    if (this.mode.getValue() == SpeedMineMode.SILENT && minecraftClient.player.getInventory().selectedSlot != i2) {
                        InventoryUtils.selectSlot(i2);
                    }
                    this.stopwatch.reset();
                }
            }
        }
    }

    public boolean isBelowMendThreshold(ItemStack itemStack) {
        return itemStack.isDamageable() && !itemStack.isEmpty() && ExplosionDamageCalculator.getClampedDurabilityPercent(itemStack) < this.stopAt.getValue().intValue();
    }

    public boolean shouldPause() {
        if (!this.air.getValue().booleanValue()) {
            if (minecraftClient.crosshairTarget.getType() == HitResult.Type.MISS) {
                return true;
            }
            BlockHitResult blockHitResult = (minecraftClient.crosshairTarget) instanceof BlockHitResult ? (BlockHitResult) (minecraftClient.crosshairTarget) : null;
            if (blockHitResult instanceof BlockHitResult) {
                BlockHitResult blockHitResult2 = blockHitResult;
                if (minecraftClient.world.getBlockState(blockHitResult2.getBlockPos()).isReplaceable()) {
                    return true;
                }
            }
        }
        if (Offhand.is929()) {
            return true;
        }
        return minecraftClient.player.isFallFlying();
    }
}
