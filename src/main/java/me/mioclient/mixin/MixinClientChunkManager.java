package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Consumer;
import me.mioclient.event.events.network.LoadChunkFromPacketEvent;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ClientChunkManager.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinClientChunkManager.class */
public class MixinClientChunkManager {
    @Inject(method = {"loadChunkFromPacket"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;resetChunkColor(Lnet/minecraft/util/math/ChunkPos;)V", shift = At.Shift.BEFORE)})
    private void loadChunkFromPacketHook(int i, int i2, PacketByteBuf packetByteBuf, NbtCompound nbtCompound, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> callbackInfoReturnable, @Local WorldChunk worldChunk) {
        ClientAccessor.eventBus.fireEvent(new LoadChunkFromPacketEvent(worldChunk));
    }
}
