package me.mioclient.manager;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.interaction.SlotChangeEvent;
import me.mioclient.event.events.world.BlockCollisionShapeEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockStateSnapshot;
import me.mioclient.util.world.TimedBlockEntry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockStateSnapshot;
import me.mioclient.util.world.TimedBlockEntry;

import me.mioclient.event.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.mixin.ducks.DuckAbstractBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import me.mioclient.event.events.interaction.SlotChangeEvent;
import me.mioclient.event.events.world.BlockCollisionShapeEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.render.RenderConstants;
import me.mioclient.manager.RotationManager;
import me.mioclient.render.RenderConstants;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WorldStateTracker.class */
public class WorldStateTracker implements ClientAccessor {
    public ItemStack itemStack;
    public ItemStack itemStack2;
    public int num;
    public final List<BlockEntity> list = Collections.synchronizedList(new ArrayList());
    public final Map<BlockPos, TimedBlockEntry> map = Collections.synchronizedMap(new HashMap());
    public final Set<BlockPos> set = Collections.synchronizedSet(new HashSet());
    public final Set<BlockPos> set2 = Collections.synchronizedSet(new HashSet());
    public final Set<BlockStateSnapshot> set3 = Collections.synchronizedSet(new HashSet());

    @Deprecated
    public BlockPos unconfirmedBreak = null;
    public final Stopwatch stopwatch = new Stopwatch();

    public WorldStateTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        this.map.clear();
        this.set.clear();
        this.list.clear();
        this.set3.clear();
    }

    @Listen(get219= RenderConstants.num)
    public void onTickPost(TickPostEvent tickPostEvent) {
        syncHandStacks();
    }

    @Listen(get219= RenderConstants.num5)
    public void onTick(TickEvent tickEvent) {
        this.set2.clear();
        if (this.stopwatch.hasElapsedMs(150L)) {
            this.unconfirmedBreak = null;
        }
        this.set.removeIf(blockPos -> {
            return !blockPos.isWithinDistance(minecraftClient.player.getBlockPos(), Double.longBitsToDouble(4638707616191610880L));
        });
        synchronized (this.map) {
            ArrayList arrayList = new ArrayList();
            for (Map.Entry<BlockPos, TimedBlockEntry> entry : this.map.entrySet()) {
                if (System.currentTimeMillis() > entry.getValue().getTimestamp() + 250) {
                    arrayList.add(entry.getKey());
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                this.map.remove((BlockPos) it.next());
            }
        }
        synchronized (getLoadedBlockEntities()) {
            this.list.clear();
            this.list.addAll(WorldUtils.getLoadedBlockEntities());
            this.list.removeIf((v0) -> {
                return Objects.isNull(v0);
            });
            this.list.sort(Comparator.comparing(blockEntity -> {
                return Double.valueOf(minecraftClient.player.getPos().squaredDistanceTo(blockEntity.getPos().toCenterPos()));
            }));
        }
        synchronized (this.set3) {
            for (BlockStateSnapshot blockStateSearchHelper4 : this.set3) {
                if (blockStateSearchHelper4.isInvalid()) {
                    minecraftClient.world.setBlockState(blockStateSearchHelper4.getBlockPos(), blockStateSearchHelper4.getBlockState());
                }
            }
        }
        this.set3.removeIf((v0) -> {
            return v0.isInvalid();
        });
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        PlayerInteractBlockC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractBlockC2SPacket ? (PlayerInteractBlockC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerInteractBlockC2SPacket) {
            PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket = packet904;
            BlockItem item = (getHandItemStack(playerInteractBlockC2SPacket.getHand()).getItem()) instanceof BlockItem ? (BlockItem) (getHandItemStack(playerInteractBlockC2SPacket.getHand()).getItem()) : null;
            if (item instanceof BlockItem) {
                BlockItem blockItem = item;
                BlockHitResult blockHitResult = playerInteractBlockC2SPacket.getBlockHitResult();
                BlockPos offset = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
                this.map.put(offset, TimedBlockEntry.getBlockData2589(blockItem.getBlock()));
                this.set2.add(offset);
                syncHandStacks();
            }
        }
    }

    @Listen
    public void onSlotChange(SlotChangeEvent speedMineEvent) {
        syncHandStacks();
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        BlockUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof BlockUpdateS2CPacket ? (BlockUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof BlockUpdateS2CPacket) {
            BlockUpdateS2CPacket blockUpdateS2CPacket = packet904;
            this.set3.removeIf(blockStateSearchHelper4 -> {
                return blockStateSearchHelper4.getBlockPos().equals(blockUpdateS2CPacket.getPos());
            });
            if (blockUpdateS2CPacket.getState().isAir()) {
                this.set.remove(blockUpdateS2CPacket.getPos());
                this.map.remove(blockUpdateS2CPacket.getPos());
            } else {
                if (this.map.remove(blockUpdateS2CPacket.getPos()) != null) {
                    this.set.add(blockUpdateS2CPacket.getPos());
                }
            }
        }
        ChunkDeltaUpdateS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof ChunkDeltaUpdateS2CPacket ? (ChunkDeltaUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof ChunkDeltaUpdateS2CPacket) {
            packet9042.visitUpdates((blockPos, blockState) -> {
                this.set3.removeIf(blockStateLlamaRenderHelper -> {
                    return blockStateLlamaRenderHelper.getBlockPos().equals(blockPos);
                });
            });
        }
        GameJoinS2CPacket packet9043 = (channelRead0Event.getPacket()) instanceof GameJoinS2CPacket ? (GameJoinS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9043 instanceof GameJoinS2CPacket) {
            this.num = packet9043.viewDistance();
        }
        ChunkLoadDistanceS2CPacket packet9044 = (channelRead0Event.getPacket()) instanceof ChunkLoadDistanceS2CPacket ? (ChunkLoadDistanceS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9044 instanceof ChunkLoadDistanceS2CPacket) {
            this.num = packet9044.getDistance();
        }
    }

    @Listen
    public void onBlockCollisionShape(BlockCollisionShapeEvent voxelShapeEvent) {
        if (voxelShapeEvent.getBlockPos() == null || minecraftClient.player == null) {
            return;
        }
        if (this.map.containsKey(voxelShapeEvent.getBlockPos())) {
            TimedBlockEntry blockData = this.map.get(voxelShapeEvent.getBlockPos());
            if (blockData == null || blockData.getBlockState() == null) {
                return;
            }
            Box box = new Box(voxelShapeEvent.getBlockPos());
            if (minecraftClient.player.getBoundingBox().intersects(box) || !((DuckAbstractBlock) blockData.getBlockState()).isCollidable()) {
                return;
            }
            if (!minecraftClient.world.getEntitiesByClass(Entity.class, box, entity -> {
                return ((entity instanceof ExperienceBottleEntity) || (entity instanceof ItemEntity) || (entity instanceof ExperienceOrbEntity) || (entity instanceof ArrowEntity) || (entity instanceof EnderPearlEntity)) ? false : true;
            }).isEmpty()) {
                return;
            }
            voxelShapeEvent.setShape(blockData.getBlockState().getDefaultState().getOutlineShape(minecraftClient.world, voxelShapeEvent.getBlockPos()));
        }
        if (RotationManager.is724() && ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(300L) && voxelShapeEvent.getBlockPos().equals(this.unconfirmedBreak)) {
            voxelShapeEvent.setShape(VoxelShapes.empty());
        }
    }

    public void breakBlockWithSnapshot(BlockPos blockPos) {
        this.set3.add(BlockStateSnapshot.getBlockStateSearchHelper42779(blockPos));
        minecraftClient.interactionManager.breakBlock(blockPos);
    }

    public void setUnconfirmedBreak(BlockPos blockPos) {
        this.unconfirmedBreak = blockPos;
        this.stopwatch.reset();
    }

    public BlockPos getUnconfirmedBreakPos() {
        return this.unconfirmedBreak;
    }

    public Map<BlockPos, TimedBlockEntry> getTimedBlockMap() {
        return this.map;
    }

    public ItemStack getHandItemStack(Hand hand) {
        return hand == Hand.MAIN_HAND ? this.itemStack : this.itemStack2;
    }

    public void syncHandStacks() {
        this.itemStack = minecraftClient.player.getMainHandStack().copy();
        this.itemStack2 = minecraftClient.player.getOffHandStack().copy();
    }

    public List<BlockEntity> getLoadedBlockEntities() {
        return this.list;
    }

    public boolean isBlockConfirmed(BlockPos blockPos) {
        return this.set.contains(blockPos) || this.map.containsKey(blockPos);
    }

    public boolean isBlockSolid(BlockPos blockPos) {
        return !minecraftClient.world.getBlockState(blockPos).isReplaceable() || this.set2.contains(blockPos);
    }

    public int getViewDistance() {
        return this.num;
    }
}
