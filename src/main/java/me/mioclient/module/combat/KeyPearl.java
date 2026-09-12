package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import me.mioclient.module.movement.ElytraFly;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/KeyPearl.class */
public class KeyPearl extends Module {
    public static final ElytraFly elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class);
    public Setting<Boolean> noEntityTrace = add(new BooleanSetting("NoEntityTrace", true));
    public Setting<Boolean> solid = add(new BooleanSetting("Solid", true));
    public Setting<Boolean> disableOnElytra = add(new BooleanSetting("DisableOnElytra", false));
    public Setting<Boolean> disableOnRiding = add(new BooleanSetting("DisableOnRiding", false));

    public KeyPearl() {
        super("KeyPearl", "Throws an ender pearl.", Category.COMBAT, new String[0]);
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame()) {
            disable();
            return;
        }
        boolean z = elytrafly.isToggled() && this.disableOnElytra.getValue().booleanValue() && !minecraftClient.player.isOnGround() && PlayerUtils.hasElytraEquipped(minecraftClient.player);
        if (minecraftClient.player.hasVehicle() && this.disableOnRiding.getValue().booleanValue()) {
            z = true;
        }
        if (z) {
            disable();
        }
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() != KeyPearlMode.Pre) {
            return;
        }
        int i = InventoryUtils.findInHotbar(Items.ENDER_PEARL);
        int i2 = InventoryUtils.findSlot(Items.ENDER_PEARL);
        int i3 = minecraftClient.player.getInventory().selectedSlot;
        boolean z = (minecraftClient.crosshairTarget instanceof EntityHitResult) && this.noEntityTrace.getValue().booleanValue();
        if (i2 == -1 || z || minecraftClient.player.isFallFlying() || elytrafly.shouldSwapElytra() || minecraftClient.player.hasVehicle()) {
            disable();
            return;
        }
        BlockHitResult blockHitResult = (minecraftClient.crosshairTarget) instanceof BlockHitResult ? (BlockHitResult) (minecraftClient.crosshairTarget) : null;
        if (blockHitResult instanceof BlockHitResult) {
            BlockHitResult blockHitResult2 = blockHitResult;
            if (!minecraftClient.world.getBlockState(blockHitResult2.getBlockPos()).isReplaceable() && blockHitResult2.getType() != HitResult.Type.MISS && !this.solid.getValue().booleanValue()) {
                disable();
                return;
            }
        }
        if (Offhand.is929()) {
            return;
        }
        boolean z2 = i == -1;
        equipSlot(z2 ? i2 : i, z2);
        minecraftClient.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), minecraftClient.player.getYaw(), minecraftClient.player.getPitch(), minecraftClient.player.isOnGround()));
        PacketSender.sendInteractItem(Hand.MAIN_HAND);
        PacketSender.sendHandSwing(Hand.MAIN_HAND);
        equipSlot(z2 ? i2 : i3, z2);
        ClientServices.moduleRegistry.getDelayModules().stream().filter(delay -> {
            return delay.setting11.getValue().booleanValue();
        }).forEach((v0) -> {
            v0.disable();
        });
        disable();
    }

    public void equipSlot(int i, boolean z) {
        if (z) {
            InventoryUtils.swapToHotbar(i);
        } else {
            InventoryUtils.selectSlot(i);
        }
    }
}
