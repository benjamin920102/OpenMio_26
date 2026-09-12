package me.mioclient.module.misc;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.module.Module;
import me.mioclient.module.exploit.RocketExtender;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.Fireworks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/MiddleClick.class */
public class MiddleClick extends Module {
    public static final Fireworks fireworks2 = (Fireworks) ClientServices.moduleInjector.getModule(Fireworks.class);
    public static final RocketExtender rocketExtender = (RocketExtender) ClientServices.moduleInjector.getModule(RocketExtender.class);
    public static ElytraFly elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class);
    public Setting<Boolean> friends = add(new BooleanSetting("Friends", true));
    public Setting<Boolean> fireworks = add(new BooleanSetting("Fireworks", true).setVisible());
    public Setting<Boolean> alternative = add(new BooleanSetting("Alternative", false).requiresSettings(this.fireworks));
    public boolean flag;

    public MiddleClick() {
        super("MiddleClick", "Middle click actions.", Category.MISC, new String[0]);
        setDrawn(false);
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (minecraftClient.currentScreen != null) {
            return;
        }
        if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().getHandle(), 2) != 1 || this.flag) {
            if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().getHandle(), 2) == 0) {
                this.flag = false;
                return;
            }
            return;
        }
        this.flag = true;
        EntityHitResult entityHitResult = (EntityHitResult)(minecraftClient.crosshairTarget);
        if ((minecraftClient.player.isFallFlying() || fireworks2.shouldFastLaunch() || elytrafly.shouldSwapElytra()) && this.fireworks.getValue().booleanValue()) {
            useFireworkRocket();
            return;
        }
        if (entityHitResult instanceof EntityHitResult) {
            PlayerEntity entity = (entityHitResult.getEntity()) instanceof PlayerEntity ? (PlayerEntity) (entityHitResult.getEntity()) : null;
            if (entity instanceof PlayerEntity) {
                PlayerEntity playerEntity = entity;
                if (this.friends.getValue().booleanValue()) {
                    if (ClientServices.socialManager.isFriend(playerEntity.getGameProfile().getName())) {
                        ClientServices.socialManager.removePlayer(playerEntity.getGameProfile().getName());
                    } else {
                        ClientServices.socialManager.addFriend(playerEntity.getGameProfile().getName());
                    }
                }
            }
        }
    }

    public void useFireworkRocket() {
        Hand hand450 = InventoryUtils.getHandWith(Items.FIREWORK_ROCKET);
        int i = minecraftClient.player.getInventory().selectedSlot;
        int i2 = InventoryUtils.findSlot(Items.FIREWORK_ROCKET);
        int i3 = InventoryUtils.findInHotbar(Items.FIREWORK_ROCKET);
        if (hand450 != null) {
            minecraftClient.interactionManager.interactItem(minecraftClient.player, hand450);
            return;
        }
        if (i2 != -1) {
            boolean z = i3 == -1 || this.alternative.getValue().booleanValue();
            selectHotbarSlot(i3, i2, z);
            minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.MAIN_HAND);
            rocketExtender.vec3d = minecraftClient.player.getPos();
            selectHotbarSlot(i, i2, z);
        }
    }

    public void selectHotbarSlot(int i, int i2, boolean z) {
        if (z) {
            InventoryUtils.swapToHotbar(i2);
        } else {
            InventoryUtils.selectSlot(i);
        }
    }
}
