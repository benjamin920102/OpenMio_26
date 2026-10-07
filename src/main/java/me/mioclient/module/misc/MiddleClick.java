package me.mioclient.module.misc;

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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import org.lwjgl.glfw.GLFW;
import me.mioclient.setting.BooleanSetting;

public class MiddleClick extends Module {
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving refs null (NPE). */
    private static Fireworks fireworks2;
    private static RocketExtender rocketExtender;
    private static ElytraFly elytrafly;

    public static Fireworks getFireworks2() {
        if (fireworks2 == null && ClientServices.moduleInjector != null) {
            try {
                fireworks2 = (Fireworks) ClientServices.moduleInjector.getModule(Fireworks.class);
            } catch (Exception e) {
            }
        }
        return fireworks2;
    }

    public static RocketExtender getRocketExtender() {
        if (rocketExtender == null && ClientServices.moduleInjector != null) {
            try {
                rocketExtender = (RocketExtender) ClientServices.moduleInjector.getModule(RocketExtender.class);
            } catch (Exception e) {
            }
        }
        return rocketExtender;
    }

    public static ElytraFly getElytrafly() {
        if (elytrafly == null && ClientServices.moduleInjector != null) {
            try {
                elytrafly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class);
            } catch (Exception e) {
            }
        }
        return elytrafly;
    }
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
        if (minecraftClient.gui.screen() != null) {
            return;
        }
        if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().handle(), 2) != 1 || this.flag) {
            if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().handle(), 2) == 0) {
                this.flag = false;
                return;
            }
            return;
        }
        this.flag = true;
        EntityHitResult entityHitResult = (EntityHitResult)(minecraftClient.hitResult);
        Fireworks fireworks2_lcl = getFireworks2();
        ElytraFly elytrafly_lcl = getElytrafly();
        boolean fastLaunch = fireworks2_lcl != null && fireworks2_lcl.shouldFastLaunch();
        boolean swapElytra = elytrafly_lcl != null && elytrafly_lcl.shouldSwapElytra();
        if ((minecraftClient.player.isFallFlying() || fastLaunch || swapElytra) && this.fireworks.getValue().booleanValue()) {
            useFireworkRocket();
            return;
        }
        if (entityHitResult instanceof EntityHitResult) {
            Player entity = (entityHitResult.getEntity()) instanceof Player ? (Player) (entityHitResult.getEntity()) : null;
            if (entity instanceof Player) {
                Player playerEntity = entity;
                if (this.friends.getValue().booleanValue()) {
                    if (ClientServices.socialManager.isFriend(playerEntity.getGameProfile().name())) {
                        ClientServices.socialManager.removePlayer(playerEntity.getGameProfile().name());
                    } else {
                        ClientServices.socialManager.addFriend(playerEntity.getGameProfile().name());
                    }
                }
            }
        }
    }

    public void useFireworkRocket() {
        InteractionHand hand450 = InventoryUtils.getHandWith(Items.FIREWORK_ROCKET);
        int i = minecraftClient.player.getInventory().getSelectedSlot();
        int i2 = InventoryUtils.findSlot(Items.FIREWORK_ROCKET);
        int i3 = InventoryUtils.findInHotbar(Items.FIREWORK_ROCKET);
        if (hand450 != null) {
            minecraftClient.gameMode.useItem(minecraftClient.player, hand450);
            return;
        }
        if (i2 != -1) {
            boolean z = i3 == -1 || this.alternative.getValue().booleanValue();
            selectHotbarSlot(i3, i2, z);
            minecraftClient.gameMode.useItem(minecraftClient.player, InteractionHand.MAIN_HAND);
            RocketExtender rocketExtender_lcl = getRocketExtender();
            if (rocketExtender_lcl != null) {
                rocketExtender_lcl.vec3d = minecraftClient.player.position();
            }
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