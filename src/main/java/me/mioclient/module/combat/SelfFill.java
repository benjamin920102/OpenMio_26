package me.mioclient.module.combat;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.concurrent.LockedTaskRunner;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.concurrent.LockedTaskRunner;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import me.mioclient.mixin.ducks.DuckAbstractBlock;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/SelfFill.class */
public class SelfFill extends Module {
    public Setting<ScaffoldMode_2> mode = add(new EnumSetting<>("Mode", ScaffoldMode_2.WHITELIST));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true));
    public Setting<Boolean> persistent = add(new BooleanSetting("Persistent", false).setVisible());
    public Setting<Boolean> jumpDisable = add(new BooleanSetting("JumpDisable", true).requiresSettings(this.persistent));
    public Setting<Set<Block>> whitelist = add(new EntityListObjectSetting<>("Whitelist", Registries.BLOCK));
    public Setting<Boolean> attack = add(new BooleanSetting("Attack", true));
    public Setting<Boolean> noLagBack = add(new BooleanSetting("NoLagBack", false));
    public Entity entity;
    public boolean flag;
    public double val;
    public final List<Double> list;

    public SelfFill() {
        super("SelfFill", "Places solid blocks inside you blocking your lower hitbox.", Category.COMBAT, "burrow");
        this.list = List.of(Double.valueOf(Double.longBitsToDouble(4600877379321698714L)), Double.valueOf(Double.longBitsToDouble(4604930618986332160L)), Double.valueOf(Double.longBitsToDouble(4607227454796291113L)), Double.valueOf(Double.longBitsToDouble(4607857958744122982L)));
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame()) {
            disable();
        } else {
            this.val = minecraftClient.player.getY();
            this.flag = false;
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (minecraftClient.player.getY() > this.val && this.persistent.getValue().booleanValue() && this.jumpDisable.getValue().booleanValue()) {
            toggle();
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        PlayerPositionLookS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof PlayerPositionLookS2CPacket ? (PlayerPositionLookS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket playerPositionLookS2CPacket = packet904;
            if (minecraftClient.player.getPos().squaredDistanceTo(new Vec3d(playerPositionLookS2CPacket.getX(), playerPositionLookS2CPacket.getY(), playerPositionLookS2CPacket.getZ())) > Double.longBitsToDouble(4621256167635550208L)) {
                this.val = playerPositionLookS2CPacket.getY();
            }
        }
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Post || minecraftClient.player.hasVehicle()) {
            return;
        }
        int i = 0;
        int i2 = -8;
        while (true) {
            if (i2 >= 8) {
                break;
            }
            if (i2 != 0 && i2 != 1) {
                BlockPos add = BlockPos.ofFloored(minecraftClient.player.getPos()).add(0, i2, 0);
                if (minecraftClient.world.getBlockState(add).getCollisionShape(minecraftClient.world, add).isEmpty()) {
                    i = i2;
                    break;
                }
            }
            i2++;
        }
        BlockPos blockPos1333 = PlayerUtils.getRoundedBlockPos();
        if (minecraftClient.world.getEntitiesByClass(PlayerEntity.class, new Box(blockPos1333), playerEntity -> {
            return playerEntity != minecraftClient.player;
        }).isEmpty()) {
            BlockState blockState = minecraftClient.world.getBlockState(blockPos1333);
            Box expand = minecraftClient.player.getBoundingBox().expand(-BoxMath.val);
            if (((BlockPos.stream(expand.withMaxY(expand.minY)).count() != 1) && MovementUtils.is2181()) || !blockState.isReplaceable() || blockState.isLiquid() || !minecraftClient.player.isOnGround() || i == 0) {
                return;
            }
            if (this.attack.getValue().booleanValue()) {
                this.entity = BlockPlacer.findAndAttackCrystal(blockPos1333, 0);
            }
            if (this.entity != null) {
                return;
            }
            int i3 = InventoryUtils.findInHotbar(itemStack -> {
                BlockItem item = (itemStack.getItem()) instanceof BlockItem ? (BlockItem) (itemStack.getItem()) : null;
                if (item instanceof BlockItem) {
                    BlockItem blockItem = item;
                    if (this.mode.getValue().isAllowedBySetting(blockItem.getBlock(), this.whitelist)) {
                        return true;
                    }
                }
                return false;
            });
            int i4 = minecraftClient.player.getInventory().selectedSlot;
            if (i3 == -1) {
                ChatUtils.sendPrefixedMessage(Text.literal(getName()).append(" is out of blocks!"), ChatUtils.createSignature(-2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
                disable();
                return;
            }
            Direction direction3028 = BlockPlacer.findSupportSide(blockPos1333);
            if (direction3028 == null) {
                return;
            }
            float[] fArr = {motionEvent.getYaw(), MathConstants.num2};
            if (this.entity != null && this.rotate.getValue().booleanValue()) {
                fArr = RotationManager.getRotationsTo(this.entity);
            }
            int i5 = i;
            LockedTaskRunner.executeLocked(() -> {
                placeSelfFillBlocks(blockPos1333, direction3028, i4, i3, i5);
            });
            if (this.rotate.getValue().booleanValue()) {
                ClientServices.RotationManager.requestRotations(fArr, 101);
            }
            if (!this.flag || this.persistent.getValue().booleanValue()) {
                return;
            }
            this.flag = false;
            disable();
        }
    }

    public void placeSelfFillBlocks(BlockPos blockPos, Direction direction, int i, int i2, int i3) {
        boolean z = true;
        int i4 = 1;
        while (true) {
            if (i4 >= 3) {
                break;
            }
            if (!minecraftClient.world.getBlockState(blockPos.up(i4)).isReplaceable()) {
                z = false;
                break;
            }
            i4++;
        }
        if (z) {
            minecraftClient.player.setSneaking(false);
            minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            InventoryUtils.selectSlot(i2);
            boolean isCollidable = ((me.mioclient.mixin.ducks.DuckAbstractBlock) ((BlockItem) minecraftClient.player.getMainHandStack().getItem()).getBlock()).isCollidable();
            if (isCollidable) {
                Iterator<Double> it = this.list.iterator();
                while (it.hasNext()) {
                    minecraftClient.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(minecraftClient.player.getX(), minecraftClient.player.getY() + it.next().doubleValue(), minecraftClient.player.getZ(), minecraftClient.player.getYaw(), MathConstants.num2, false));
                }
            }
            Box boundingBox = minecraftClient.player.getBoundingBox();
            minecraftClient.player.setBoundingBox(new Box(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d));
            if (this.noLagBack.getValue().booleanValue()) {
                BlockPlacer.placeBlock(blockPos, (Vec3d) null, direction, false, Hand.MAIN_HAND);
            } else {
                BlockPlacer.placeBlockPacket(blockPos, direction, false, Hand.MAIN_HAND);
            }
            minecraftClient.player.setBoundingBox(boundingBox);
            if (isCollidable) {
                sendPositionAdjustment(i3);
            }
            InventoryUtils.selectSlot(i);
            minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            this.flag = true;
        }
    }

    public void sendPositionAdjustment(double d) {
        if (!this.noLagBack.getValue().booleanValue()) {
            minecraftClient.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(minecraftClient.player.getX(), minecraftClient.player.getY() + d, minecraftClient.player.getZ(), minecraftClient.player.getYaw(), MathConstants.num2, false));
        } else {
            minecraftClient.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(minecraftClient.player.getX(), minecraftClient.player.getY() + Double.longBitsToDouble(4607182418800017408L), minecraftClient.player.getZ(), minecraftClient.player.getYaw(), MathConstants.num2, true));
            ClientServices.tooltipsHelper.schedule(() -> {
                minecraftClient.player.setPosition(minecraftClient.player.getX(), minecraftClient.player.getY() + Double.longBitsToDouble(4607182418800017408L), minecraftClient.player.getZ());
            }, 1);
        }
    }
}
