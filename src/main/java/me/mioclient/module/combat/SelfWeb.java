package me.mioclient.module.combat;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import me.mioclient.manager.RotationManager;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/SelfWeb.class */
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
        if (minecraftClient.player.hasVehicle()) {
            return;
        }
        if (ClientServices.holeSnapOverlayHelper.isInHole() || !this.onlyHole.getValue().booleanValue()) {
            BlockPos ofFloored = BlockPos.ofFloored(minecraftClient.player.getPos());
            BlockState blockState = minecraftClient.world.getBlockState(ofFloored);
            if (blockState.isReplaceable() && !blockState.isLiquid() && minecraftClient.player.isOnGround()) {
                int i = InventoryUtils.findInHotbar(Items.COBWEB);
                int i2 = minecraftClient.player.getInventory().selectedSlot;
                if (i == -1) {
                    ChatUtils.sendPrefixedMessage(Text.literal(getName()).append(" is out of blocks!"), ChatUtils.createSignature(-2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
                    disable();
                    return;
                }
                Direction direction3028 = BlockPlacer.findSupportSide(ofFloored);
                if (direction3028 == null) {
                    return;
                }
                InventoryUtils.selectSlot(i);
                Box boundingBox = minecraftClient.player.getBoundingBox();
                minecraftClient.player.setBoundingBox(new Box(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d));
                BlockPlacer.placeBlockPacket(ofFloored, direction3028, false, Hand.MAIN_HAND);
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
        Stream<Entity> filter = StreamSupport.stream(minecraftClient.world.getEntities().spliterator(), false).filter(entity -> {
            if (entity != minecraftClient.player && minecraftClient.player.distanceTo(entity) <= ((Float) setting.getValue()).floatValue() && isAbovePlayer(entity) && (entity instanceof PlayerEntity)) {
                if (!ClientServices.socialManager.isFriend(entity.getName().getString()) && entity.isAlive()) {
                    return true;
                }
            }
            return false;
        });
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        Objects.requireNonNull(clientPlayerEntity);
        return (Entity) filter.min(Comparator.comparing(clientPlayerEntity::squaredDistanceTo)).orElse(null);
    }

    public boolean isAbovePlayer(Entity entity) {
        return this.aggressive.getValue().booleanValue() ? entity.getY() >= minecraftClient.player.getY() : entity.getY() > minecraftClient.player.getY();
    }
}
