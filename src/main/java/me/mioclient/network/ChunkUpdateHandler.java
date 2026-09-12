package me.mioclient.network;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.LoadChunkFromPacketEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChunkUpdateHandler.class */
public class ChunkUpdateHandler implements ClientAccessor {
    public final int num;
    public final BiConsumer<ChunkPos, WorldChunk> biConsumer;
    public final BiConsumer<ChunkPos, WorldChunk> biConsumer2;
    public final BiConsumer<BlockPos, BlockState> biConsumer3;
    public ExecutorService executorService;

    public ChunkUpdateHandler(int i, BiConsumer<ChunkPos, WorldChunk> biConsumer, BiConsumer<ChunkPos, WorldChunk> biConsumer2, BiConsumer<BlockPos, BlockState> biConsumer3) {
        this.num = i;
        this.biConsumer = biConsumer;
        this.biConsumer2 = biConsumer2;
        this.biConsumer3 = biConsumer3;
    }

    public void do1640() {
        this.executorService = Executors.newFixedThreadPool(Math.max(this.num, 1));
        eventBus.register(this);
    }

    public void stopListening() {
        eventBus.unregister(this);
        this.executorService.shutdownNow();
        this.executorService = null;
    }

    public void restartExecutor() {
        this.executorService.shutdownNow();
        this.executorService = Executors.newFixedThreadPool(Math.max(this.num, 1));
    }

    public void processAllChunks() {
        if (this.biConsumer != null) {
            for (WorldChunk worldChunk : WorldUtils.getLoadedChunks()) {
                submitTask(() -> {
                    this.biConsumer.accept(worldChunk.getPos(), worldChunk);
                });
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (minecraftClient.world == null) {
            return;
        }
        if (this.biConsumer3 != null) {
            BlockUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof BlockUpdateS2CPacket ? (BlockUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
            if (packet904 instanceof BlockUpdateS2CPacket) {
                BlockUpdateS2CPacket blockUpdateS2CPacket = packet904;
                submitTask(() -> {
                    this.biConsumer3.accept(blockUpdateS2CPacket.getPos(), blockUpdateS2CPacket.getState());
                });
                return;
            }
        }
        if (this.biConsumer3 != null) {
            ExplosionS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof ExplosionS2CPacket ? (ExplosionS2CPacket) (channelRead0Event.getPacket()) : null;
            if (packet9042 instanceof ExplosionS2CPacket) {
                for (BlockPos blockPos : packet9042.getAffectedBlocks()) {
                    submitTask(() -> {
                        this.biConsumer3.accept(blockPos, Blocks.AIR.getDefaultState());
                    });
                }
                return;
            }
        }
        if (this.biConsumer3 != null) {
            ChunkDeltaUpdateS2CPacket packet9043 = (channelRead0Event.getPacket()) instanceof ChunkDeltaUpdateS2CPacket ? (ChunkDeltaUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
            if (packet9043 instanceof ChunkDeltaUpdateS2CPacket) {
                packet9043.visitUpdates((blockPos2, blockState) -> {
                    BlockPos immutable = blockPos2.toImmutable();
                    submitTask(() -> {
                        this.biConsumer3.accept(immutable, blockState);
                    });
                });
                return;
            }
        }
        if (this.biConsumer2 != null) {
            UnloadChunkS2CPacket packet9044 = (channelRead0Event.getPacket()) instanceof UnloadChunkS2CPacket ? (UnloadChunkS2CPacket) (channelRead0Event.getPacket()) : null;
            if (packet9044 instanceof UnloadChunkS2CPacket) {
                UnloadChunkS2CPacket unloadChunkS2CPacket = packet9044;
                ChunkPos chunkPos = new ChunkPos(unloadChunkS2CPacket.pos().x, unloadChunkS2CPacket.pos().z);
                WorldChunk chunk = minecraftClient.world.getChunk(chunkPos.x, chunkPos.z);
                submitTask(() -> {
                    this.biConsumer2.accept(chunkPos, chunk);
                });
            }
        }
    }

    @Listen
    public void onLoadChunkFromPacket(LoadChunkFromPacketEvent loadChunkFromPacketEvent) {
        if (this.biConsumer == null) {
            return;
        }
        submitTask(() -> {
            this.biConsumer.accept(loadChunkFromPacketEvent.getChunk().getPos(), loadChunkFromPacketEvent.getChunk());
        });
    }

    public ExecutorService getThreadPool() {
        return this.executorService;
    }

    public void submitTask(java.lang.Runnable runnable) {
        if (this.num == 0) {
            minecraftClient.executeSync(runnable);
        } else {
            this.executorService.execute(runnable);
        }
    }
}
