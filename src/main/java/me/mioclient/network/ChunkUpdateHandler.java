package me.mioclient.network;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.LoadChunkFromPacketEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkUpdateHandler implements ClientAccessor {
    public final int num;
    public final BiConsumer<ChunkPos, LevelChunk> biConsumer;
    public final BiConsumer<ChunkPos, LevelChunk> biConsumer2;
    public final BiConsumer<BlockPos, BlockState> biConsumer3;
    public ExecutorService executorService;

    public ChunkUpdateHandler(int i, BiConsumer<ChunkPos, LevelChunk> biConsumer, BiConsumer<ChunkPos, LevelChunk> biConsumer2, BiConsumer<BlockPos, BlockState> biConsumer3) {
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
            for (LevelChunk worldChunk : WorldUtils.getLoadedChunks()) {
                submitTask(() -> {
                    this.biConsumer.accept(worldChunk.getPos(), worldChunk);
                });
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (minecraftClient.level == null) {
            return;
        }
        if (this.biConsumer3 != null) {
            ClientboundBlockUpdatePacket packet904 = (channelRead0Event.getPacket()) instanceof ClientboundBlockUpdatePacket ? (ClientboundBlockUpdatePacket) (channelRead0Event.getPacket()) : null;
            if (packet904 instanceof ClientboundBlockUpdatePacket) {
                ClientboundBlockUpdatePacket blockUpdateS2CPacket = packet904;
                submitTask(() -> {
                    this.biConsumer3.accept(blockUpdateS2CPacket.getPos(), blockUpdateS2CPacket.getBlockState());
                });
                return;
            }
        }
        if (this.biConsumer3 != null) {
            ClientboundSectionBlocksUpdatePacket packet9043 = (channelRead0Event.getPacket()) instanceof ClientboundSectionBlocksUpdatePacket ? (ClientboundSectionBlocksUpdatePacket) (channelRead0Event.getPacket()) : null;
            if (packet9043 instanceof ClientboundSectionBlocksUpdatePacket) {
                packet9043.runUpdates((blockPos2, blockState) -> {
                    BlockPos immutable = blockPos2.immutable();
                    submitTask(() -> {
                        this.biConsumer3.accept(immutable, blockState);
                    });
                });
                return;
            }
        }
        if (this.biConsumer2 != null) {
            ClientboundForgetLevelChunkPacket packet9044 = (channelRead0Event.getPacket()) instanceof ClientboundForgetLevelChunkPacket ? (ClientboundForgetLevelChunkPacket) (channelRead0Event.getPacket()) : null;
            if (packet9044 instanceof ClientboundForgetLevelChunkPacket) {
                ClientboundForgetLevelChunkPacket unloadChunkS2CPacket = packet9044;
                ChunkPos chunkPos = unloadChunkS2CPacket.pos();
                LevelChunk chunk = minecraftClient.level.getChunk(chunkPos.x(), chunkPos.z());
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
            minecraftClient.execute(runnable);
        } else {
            this.executorService.execute(runnable);
        }
    }
}
