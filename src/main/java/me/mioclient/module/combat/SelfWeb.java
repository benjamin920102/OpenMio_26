package me.mioclient.module.combat;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.world.BlockPlacer;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import me.mioclient.manager.RotationManager;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

public class SelfWeb extends Module {
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true));
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", true));
    public Setting<Boolean> onlyHole = add(new BooleanSetting("OnlyHole", false));
    public Setting<Boolean> smart = add(new BooleanSetting("Smart", false).setVisible());
    public Setting<Float> enemyRange = add(new NumberSetting<>("EnemyRange", 3.0f, 1.0f, 6.0f, v -> this.smart.isShown()).setSuffix("m"));
    public Setting<Boolean> aggressive = add(new BooleanSetting("Aggressive", false).requiresSettings(this.smart));

    public SelfWeb() {
        super("SelfWeb", "Places a cobweb at your feet.", Category.COMBAT, new String[0]);
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        Entity entity1864 = getNearestEnemyPlayer(this.enemyRange);
        if (!this.smart.getValue().booleanValue()) {
            placeSelfWeb(holeSnapEvent);
        } else if (entity1864 != null) {
            placeSelfWeb(holeSnapEvent);
        }
    }

    public void placeSelfWeb(RotationTickEvent holeSnapEvent) {
        if (minecraftClient.player.getVehicle() != null) {
            return;
        }
        if (ClientServices.holeSnapOverlayHelper.isInHole() || !this.onlyHole.getValue().booleanValue()) {
            BlockPos ofFloored = BlockPos.containing(minecraftClient.player.position());
            BlockState blockState = minecraftClient.level.getBlockState(ofFloored);
            if (blockState.canBeReplaced() && blockState.getFluidState().isEmpty() && minecraftClient.player.onGround()) {
                int i = InventoryUtils.findInHotbar(Items.COBWEB);
                int i2 = minecraftClient.player.getInventory().getSelectedSlot();
                if (i == -1) {
                    ChatUtils.sendPrefixedMessage(Component.literal(getName()).append(" is out of blocks!"), ChatUtils.createSignature(-2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
                    disable();
                    return;
                }
                Direction direction3028 = BlockPlacer.findSupportSide(ofFloored);
                if (direction3028 == null) {
                    return;
                }
                InventoryUtils.selectSlot(i);
                AABB boundingBox = minecraftClient.player.getBoundingBox();
                minecraftClient.player.setBoundingBox(new AABB(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d));
                BlockPlacer.placeBlockPacket(ofFloored, direction3028, false, InteractionHand.MAIN_HAND);
                minecraftClient.player.setBoundingBox(boundingBox);
                InventoryUtils.selectSlot(i2);
                if (this.rotate.getValue().booleanValue()) {
                    ClientServices.RotationManager.requestRotations(new float[]{holeSnapEvent.getYaw(), MathConstants.num2}, 5);
                }
                if (this.autoDisable.getValue().booleanValue()) {
                    disable();
                }
            }
        }
    }

    public Entity getNearestEnemyPlayer(Setting<Float> setting) {
        LocalPlayer clientPlayerEntity = minecraftClient.player;
        Objects.requireNonNull(clientPlayerEntity);
        return minecraftClient.level.players().stream().filter(entity -> {
            if (entity != clientPlayerEntity && clientPlayerEntity.distanceTo(entity) <= setting.getValue() && isAbovePlayer(entity) && (entity instanceof Player)) {
                if (!ClientServices.socialManager.isFriend(entity.getName().getString()) && entity.isAlive()) {
                    return true;
                }
            }
            return false;
        }).min(Comparator.comparing(clientPlayerEntity::distanceToSqr)).orElse(null);
    }

    public boolean isAbovePlayer(Entity entity) {
        return this.aggressive.getValue().booleanValue() ? entity.getY() >= minecraftClient.player.getY() : entity.getY() > minecraftClient.player.getY();
    }
}