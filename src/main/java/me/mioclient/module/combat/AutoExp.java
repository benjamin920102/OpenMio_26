package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.player.SpeedMineMode;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.network.PacketSender;

import java.util.Iterator;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import me.mioclient.module.misc.AntiAim;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

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
    /* Resolved lazily — static init ran before module registry was ready, leaving field null. */
    private static AntiAim antiAim;

    public static AntiAim getAntiAim() {
        if (antiAim == null && ClientServices.moduleInjector != null) {
            try {
                antiAim = (AntiAim) ClientServices.moduleInjector.getModule(AntiAim.class);
            } catch (Exception e) {
            }
        }
        return antiAim;
    }
    public final Stopwatch stopwatch;
    public int num;
    public boolean flag;

    
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
        AntiAim antiAim_lcl = getAntiAim();
        if (antiAim_lcl != null && antiAim_lcl.isToggled() && !this.flag) {
            antiAim_lcl.resetActionTicks();
        }
        this.num = minecraftClient.player.getInventory().getSelectedSlot();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (isNotInGame() || minecraftClient.player.getInventory().getSelectedSlot() == this.num || this.mode.getValue() != SpeedMineMode.NORMAL) {
            return;
        }
        InventoryUtils.selectSlot(this.num);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (this.flag) {
            return "Stopped";
        }
        return String.valueOf(minecraftClient.player.getInventory().getNonEquipmentItems().stream().filter(itemStack -> {
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
                for (EquipmentSlot equipmentSlot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
                    if (isBelowMendThreshold(minecraftClient.player.getItemBySlot(equipmentSlot))) {
                        z = false;
                        break;
                    }
                }
                if ((isBelowMendThreshold(minecraftClient.player.getMainHandItem()) || isBelowMendThreshold(minecraftClient.player.getOffhandItem())) && this.tools.getValue().booleanValue()) {
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
                    InteractionHand hand = minecraftClient.player.getOffhandItem().getItem() == Items.EXPERIENCE_BOTTLE ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                    this.flag = false;
                    int i2 = minecraftClient.player.getInventory().getSelectedSlot();
                    if (minecraftClient.player.getMainHandItem().getItem() != Items.EXPERIENCE_BOTTLE && hand == InteractionHand.MAIN_HAND) {
                        InventoryUtils.selectSlot(i);
                    }
                    for (int i3 = 0; i3 < this.frequency.getValue().intValue(); i3++) {
                        if (this.rotate.getValue().booleanValue()) {
                            PacketSender.sendInteractItem(hand, floatArray2484[0], floatArray2484[1]);
                        } else {
                            PacketSender.sendInteractItem(hand);
                        }
                    }
                    if (this.mode.getValue() == SpeedMineMode.SILENT && minecraftClient.player.getInventory().getSelectedSlot() != i2) {
                        InventoryUtils.selectSlot(i2);
                    }
                    this.stopwatch.reset();
                }
            }
        }
    }

    public boolean isBelowMendThreshold(ItemStack itemStack) {
        return itemStack.isDamageableItem() && !itemStack.isEmpty() && ExplosionDamageCalculator.getClampedDurabilityPercent(itemStack) < this.stopAt.getValue().intValue();
    }

    public boolean shouldPause() {
        if (!this.air.getValue().booleanValue()) {
            if (minecraftClient.hitResult.getType() == HitResult.Type.MISS) {
                return true;
            }
            BlockHitResult blockHitResult = (minecraftClient.hitResult) instanceof BlockHitResult ? (BlockHitResult) (minecraftClient.hitResult) : null;
            if (blockHitResult instanceof BlockHitResult) {
                BlockHitResult blockHitResult2 = blockHitResult;
                if (minecraftClient.level.getBlockState(blockHitResult2.getBlockPos()).canBeReplaced()) {
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