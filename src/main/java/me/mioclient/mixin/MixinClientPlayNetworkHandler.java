package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.event.events.network.ChunkDeltaUpdateEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.event.events.network.UpdateSelectedSlotEvent;
import me.mioclient.mixin.ducks.DuckExplosionS2CPacket;
import me.mioclient.module.movement.Velocity;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({ClientPlayNetworkHandler.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinClientPlayNetworkHandler.class */
public class MixinClientPlayNetworkHandler {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static Velocity velocity;

    private static Velocity getVelocity() {

        if (velocity == null && ClientServices.moduleInjector != null) {

            try { velocity = (Velocity) ClientServices.moduleInjector.getModule(Velocity.class); } catch (Exception e) {}

        }

        return velocity;

    }

    @Inject(method = {"onExplosion"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER)}, cancellable = true)
    private void onExplosionVelocity(ExplosionS2CPacket explosionS2CPacket, CallbackInfo callbackInfo) {
        ExplosionVelocityEvent explosionVelocityEvent = new ExplosionVelocityEvent(explosionS2CPacket);
        ClientAccessor.eventBus.fireEvent(explosionVelocityEvent);
        if (explosionVelocityEvent.isCancelled()) {
            callbackInfo.cancel();
            return;
        }
        DuckExplosionS2CPacket duckExplosionS2CPacket = (DuckExplosionS2CPacket) explosionS2CPacket;
        duckExplosionS2CPacket.setX(explosionVelocityEvent.getPlayerVelocityX());
        duckExplosionS2CPacket.setY(explosionVelocityEvent.getPlayerVelocityY());
        duckExplosionS2CPacket.setZ(explosionVelocityEvent.getPlayerVelocityZ());
    }

    @Inject(method = {"onEntityVelocityUpdate"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER)}, cancellable = true)
    private void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket, CallbackInfo callbackInfo) {
        Velocity velocity_lcl = getVelocity();

        if (velocity_lcl == null) return;

        if (velocity_lcl.isPacketVelocity() && entityVelocityUpdateS2CPacket.getEntityId() == MinecraftClient.getInstance().player.getId()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"onEntitySpawn"}, at = {@At("HEAD")}, cancellable = true)
    private void onEntitySpawn(EntitySpawnS2CPacket entitySpawnS2CPacket, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && entitySpawnS2CPacket != null && entitySpawnS2CPacket.getEntityType() != null && norender_lcl.isEntityTypeHidden(entitySpawnS2CPacket.getEntityType()) && norender_lcl.removal.getValue() == NoRender.NoRenderMode.FULL) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"onUpdateSelectedSlot"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER)}, cancellable = true)
    private void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket updateSelectedSlotS2CPacket, CallbackInfo callbackInfo) {
        UpdateSelectedSlotEvent updateSelectedSlotEvent = new UpdateSelectedSlotEvent(updateSelectedSlotS2CPacket.getSlot());
        ClientAccessor.eventBus.fireEvent(updateSelectedSlotEvent);
        if (updateSelectedSlotEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"onBlockUpdate"}, at = {@At("TAIL")})
    private void onBlockUpdateHook(BlockUpdateS2CPacket blockUpdateS2CPacket, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new ChunkDeltaUpdateEvent());
    }

    @Inject(method = {"onChunkDeltaUpdate"}, at = {@At("TAIL")})
    private void onChunkDeltaUpdateHook(ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new ChunkDeltaUpdateEvent());
    }
}
