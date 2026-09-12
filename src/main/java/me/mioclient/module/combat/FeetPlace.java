package me.mioclient.module.combat;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ChunkDeltaUpdateEvent;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Delay;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import me.mioclient.module.Module;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/FeetPlace.class */
public class FeetPlace extends Delay {
    public Setting<Boolean> sequential = add(new BooleanSetting("Sequential", false));
    public Setting<Boolean> center = add(new BooleanSetting("Center", true));
    public Setting<Boolean> flatten = add(new BooleanSetting("Flatten", true));
    public Setting<Boolean> onlyObby = add(new BooleanSetting("OnlyObby", false));
    public final Stopwatch stopwatch;
    public boolean flag;

    public FeetPlace() {
        super("FeetPlace", "Surrounds your feet with obby.", Category.COMBAT, "surround");
        this.stopwatch = new Stopwatch();
        setPlacementEnabled(true);
    }

    @Override // me.mioclient.module.Delay, me.mioclient.module.Module
    public void onEnable() {
        if (!isNotInGame()) {
            centerPlayerInBlock();
        }
        super.onEnable();
        this.stopwatch.reset();
    }

    @Override // me.mioclient.module.Delay
    public List<BlockPos> getPlacePositions() {
        List<BlockPos> list877 = collectSurroundPositions(new ArrayList(), getRoundedBlockPos(), 0);
        list877.sort(getPlacementComparator());
        return list877;
    }

    @Override // me.mioclient.module.Delay
    public int getPriority() {
        return 999;
    }

    @Override // me.mioclient.module.Delay
    public int getSlotIndex() {
        return this.onlyObby.getValue().booleanValue() ? InventoryUtils.findInHotbar(Items.OBSIDIAN) : super.getSlotIndex();
    }

    @Override // me.mioclient.module.Delay
    public void do1323() {
        if (minecraftClient.player.hasVehicle()) {
            return;
        }
        this.flag = !ClientServices.holeSnapRenderHelper.getTeleportStopwatch().hasElapsedMs(100L);
        if (this.flag) {
            this.val = minecraftClient.player.getY();
        }
        try {
            super.do1323();
        } catch (Throwable th) {
        }
    }

    @Override // me.mioclient.module.Delay
    public void sortPositionsBySupport(List<BlockPos> list) {
    }

    @Override // me.mioclient.module.Delay
    public boolean isPlayerFellTooFar() {
        if (this.flag) {
            return false;
        }
        return super.isPlayerFellTooFar();
    }

    @Listen
    public void onChunkDeltaUpdate(ChunkDeltaUpdateEvent chunkDeltaUpdateEvent) {
        if (this.sequential.getValue().booleanValue()) {
            do1323();
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        PlayerPositionLookS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof PlayerPositionLookS2CPacket ? (PlayerPositionLookS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket playerPositionLookS2CPacket = packet904;
            if (minecraftClient.player.getPos().squaredDistanceTo(new Vec3d(playerPositionLookS2CPacket.getX(), playerPositionLookS2CPacket.getY(), playerPositionLookS2CPacket.getZ())) > Double.longBitsToDouble(4621256167635550208L)) {
                this.val = playerPositionLookS2CPacket.getY();
                ClientServices.holeSnapRenderHelper.getTeleportStopwatch().reset();
            }
        }
    }

    public List<BlockPos> collectSurroundPositions(List<Integer> list, BlockPos blockPos, int i) {
        int i2 = getSlotIndex();
        BlockState defaultState = Blocks.OBSIDIAN.getDefaultState();
        if (i2 != -1) {
            BlockItem item = (minecraftClient.player.getInventory().getStack(i2).getItem()) instanceof BlockItem ? (BlockItem) (minecraftClient.player.getInventory().getStack(i2).getItem()) : null;
            if (item instanceof BlockItem) {
                defaultState = item.getBlock().getDefaultState();
            }
        }
        ArrayList arrayList = new ArrayList();
        list.add(Integer.valueOf(blockPos.hashCode()));
        Iterator<Vec3d> it = getNeighborOffsets(i).iterator();
        while (it.hasNext()) {
            Position position = (Vec3d) it.next();
            BlockPos add = blockPos.add(BlockPos.ofFloored(position));
            if (((Vec3d) position).y == Double.longBitsToDouble(-4616189618054758400L)) {
                if (!ClientServices.holeSnapOverlayHelper.isHoleAt(add) || minecraftClient.player.isOnGround()) {
                    if (this.flatten.getValue().booleanValue() && !minecraftClient.player.isInSwimmingPose() && !add.equals(ClientServices.stashFinderHelper.getUnconfirmedBreakPos())) {
                    }
                }
            }
            VoxelShape collisionShape = defaultState.getCollisionShape(minecraftClient.world, BlockPos.ORIGIN);
            Box box = collisionShape.isEmpty() ? new Box(add) : BoxMath.getBoxAroundBlock(collisionShape.getBoundingBox(), add);
            do1328(add);
            if (!minecraftClient.world.getBlockState(add).isReplaceable()) {
                if (!BoxMath.hasSignificantIntersection(minecraftClient.player.getBoundingBox(), new Box(add))) {
                }
            }
            if (!minecraftClient.world.getEntitiesByClass(LivingEntity.class, box, livingEntity -> {
                return livingEntity == minecraftClient.player || BoxMath.hasSignificantIntersection(box, livingEntity.getBoundingBox());
            }).isEmpty()) {
                if (!list.contains(Integer.valueOf(add.hashCode())) && ((Vec3d) position).y == i) {
                    arrayList.addAll(collectSurroundPositions(list, add, i));
                }
            }
            if (minecraftClient.world.getBlockState(add).isReplaceable()) {
                if (BlockPlacer.findSupportSide(add, this.setting4.getValue().booleanValue()) == null) {
                    BlockPos blockPos3032 = BlockPlacer.findNearbyPlaceablePos(add, 2, true, this.setting4.getValue().booleanValue());
                    if (blockPos3032 != null) {
                        arrayList.add(blockPos3032);
                    }
                }
                arrayList.add(add);
            }
        }
        return arrayList;
    }

    public List<Vec3d> getNeighborOffsets(int i) {
        return Arrays.asList(new Vec3d(Double.longBitsToDouble(-4616189618054758400L), i, 0.0d), new Vec3d(Double.longBitsToDouble(4607182418800017408L), i, 0.0d), new Vec3d(0.0d, i, Double.longBitsToDouble(4607182418800017408L)), new Vec3d(0.0d, i, Double.longBitsToDouble(-4616189618054758400L)), new Vec3d(0.0d, i - 1, 0.0d));
    }

    public Comparator<BlockPos> getPlacementComparator() {
        return ClientServices.feetPlaceHelper.getInstantSpeed() <= Double.longBitsToDouble(4621819117588971520L) ? Comparator.comparing(blockPos -> {
            return Float.valueOf(getPlacementScore(minecraftClient.player.getYaw(), blockPos));
        }) : Comparator.comparing(blockPos2 -> {
            return Float.valueOf(getAngularPlacementScore(MovementUtils.getMovementYaw(), blockPos2));
        });
    }

    public float getPlacementScore(float f, BlockPos blockPos) {
        if (blockPos.getY() != PlayerUtils.getRoundedBlockPos().getY() || !isOnPartialBlock(minecraftClient.player)) {
            return getAngularPlacementScore(f, blockPos);
        }
        return (float) PlayerUtils.getRoundedBlockPos().getSquaredDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public float getAngularPlacementScore(float f, BlockPos blockPos) {
        return MathHelper.angleBetween(f, RotationManager.getRotationsTo(blockPos.toCenterPos())[0]) + (blockPos.getY() * Float.intBitsToFloat(981668463));
    }

    public void centerPlayerInBlock() {
        if (isNotInGame() || !this.center.getValue().booleanValue() || !minecraftClient.player.isOnGround() || ClientServices.holeSnapOverlayHelper.isInHole() || PlayerUtils.getSurroundPositions(minecraftClient.player).size() <= 4) {
            return;
        }
        Vec3d vec3d = new Vec3d(Math.floor(minecraftClient.player.getX()) + Double.longBitsToDouble(4602678819172646912L), minecraftClient.player.getY(), Math.floor(minecraftClient.player.getZ()) + Double.longBitsToDouble(4602678819172646912L));
        minecraftClient.player.setVelocity((vec3d.x - minecraftClient.player.getX()) * Double.longBitsToDouble(4602678819172646912L), minecraftClient.player.getVelocity().getY(), (vec3d.z - minecraftClient.player.getZ()) * Double.longBitsToDouble(4602678819172646912L));
        minecraftClient.player.setPosition(vec3d);
    }

    public boolean isOnPartialBlock(LivingEntity livingEntity) {
        Iterator<BlockPos> it = PlayerUtils.getFeetPositions(livingEntity).iterator();
        while (it.hasNext()) {
            VoxelShape collisionShape = minecraftClient.world.getBlockState(it.next()).getCollisionShape(minecraftClient.world, BlockPos.ORIGIN);
            if (!collisionShape.isEmpty() && !BoxMath.isFullBlockBox(collisionShape.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }
}
