package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.network.PacketSender;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import me.mioclient.module.movement.ElytraFly;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import me.mioclient.setting.BooleanSetting;

public class KeyPearl extends Module {
    /* Resolved lazily — static init ran before module registry was ready, leaving field null. */
    private static ElytraFly elytrafly;

    public static ElytraFly getElytrafly() {
        if (elytrafly == null && ClientServices.moduleInjector != null) {
            try {
                elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class);
            } catch (Exception e) {
            }
        }
        return elytrafly;
    }
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
        ElytraFly elytrafly_lcl = getElytrafly();
        boolean z = elytrafly_lcl != null && elytrafly_lcl.isToggled() && this.disableOnElytra.getValue().booleanValue() && !minecraftClient.player.onGround() && PlayerUtils.hasElytraEquipped(minecraftClient.player);
        if (minecraftClient.player.getVehicle() != null && this.disableOnRiding.getValue().booleanValue()) {
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
        int i3 = minecraftClient.player.getInventory().getSelectedSlot();
        boolean z = (minecraftClient.hitResult instanceof EntityHitResult) && this.noEntityTrace.getValue().booleanValue();
        ElytraFly elytrafly_lcl = getElytrafly();
        if (i2 == -1 || z || minecraftClient.player.isFallFlying() || (elytrafly_lcl != null && elytrafly_lcl.shouldSwapElytra()) || minecraftClient.player.getVehicle() != null) {
            disable();
            return;
        }
        BlockHitResult blockHitResult = (minecraftClient.hitResult) instanceof BlockHitResult ? (BlockHitResult) (minecraftClient.hitResult) : null;
        if (blockHitResult instanceof BlockHitResult) {
            BlockHitResult blockHitResult2 = blockHitResult;
            if (!minecraftClient.level.getBlockState(blockHitResult2.getBlockPos()).canBeReplaced() && blockHitResult2.getType() != HitResult.Type.MISS && !this.solid.getValue().booleanValue()) {
                disable();
                return;
            }
        }
        if (Offhand.is929()) {
            return;
        }
        boolean z2 = i == -1;
        equipSlot(z2 ? i2 : i, z2);
        minecraftClient.player.connection.send(new ServerboundMovePlayerPacket.PosRot(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), minecraftClient.player.getYRot(), minecraftClient.player.getXRot(), minecraftClient.player.onGround(), minecraftClient.player.horizontalCollision));
        PacketSender.sendInteractItem(InteractionHand.MAIN_HAND);
        PacketSender.sendHandSwing(InteractionHand.MAIN_HAND);
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