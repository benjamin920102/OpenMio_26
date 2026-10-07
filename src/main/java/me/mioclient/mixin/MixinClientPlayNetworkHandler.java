package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.event.events.network.ChunkDeltaUpdateEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.event.events.network.UpdateSelectedSlotEvent;
import me.mioclient.module.movement.Velocity;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
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

    @Unique
    private static boolean mio$redispatchingExplosion;

    @Inject(method = "handleExplosion", at = @At("HEAD"), cancellable = true)
    private void onExplosionVelocity(ClientboundExplodePacket packet, CallbackInfo callbackInfo) {
        if (mio$redispatchingExplosion) {
            return;
        }
        ExplosionVelocityEvent explosionVelocityEvent = new ExplosionVelocityEvent(packet);
        ClientAccessor.eventBus.fireEvent(explosionVelocityEvent);
        if (explosionVelocityEvent.isCancelled()) {
            callbackInfo.cancel();
            return;
        }
        // 26.2 record is immutable: apply modified knockback by redispatching a rebuilt packet.
        Vec3 originalKnockback = packet.playerKnockback().orElse(Vec3.ZERO);
        Vec3 newKnockback = explosionVelocityEvent.getKnockback();
        if (!newKnockback.equals(originalKnockback)) {
            mio$redispatchingExplosion = true;
            try {
                ((ClientPacketListener)(Object) this).handleExplosion(new ClientboundExplodePacket(packet.center(), packet.radius(), packet.blockCount(), Optional.of(newKnockback), packet.explosionParticle(), packet.explosionSound(), packet.blockParticles()));
            } finally {
                mio$redispatchingExplosion = false;
            }
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handleSetEntityMotion", at = @At("HEAD"), cancellable = true)
    private void onEntityVelocityUpdate(ClientboundSetEntityMotionPacket packet, CallbackInfo callbackInfo) {
        Velocity velocity_lcl = getVelocity();
        if (velocity_lcl == null) return;
        if (velocity_lcl.isPacketVelocity() && packet.id() == Minecraft.getInstance().player.getId()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handleAddEntity", at = @At("HEAD"), cancellable = true)
    private void onEntitySpawn(ClientboundAddEntityPacket packet, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();
        if (norender_lcl == null) return;
        if (norender_lcl.isToggled() && packet != null && packet.getType() != null && norender_lcl.isEntityTypeHidden(packet.getType()) && norender_lcl.removal.getValue() == NoRender.NoRenderMode.FULL) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handleSetHeldSlot", at = @At("HEAD"), cancellable = true)
    private void onUpdateSelectedSlot(ClientboundSetHeldSlotPacket packet, CallbackInfo callbackInfo) {
        UpdateSelectedSlotEvent updateSelectedSlotEvent = new UpdateSelectedSlotEvent(packet.slot());
        ClientAccessor.eventBus.fireEvent(updateSelectedSlotEvent);
        if (updateSelectedSlotEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handleBlockUpdate", at = @At("TAIL"))
    private void onBlockUpdateHook(ClientboundBlockUpdatePacket packet, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new ChunkDeltaUpdateEvent());
    }

    @Inject(method = "handleChunkBlocksUpdate", at = @At("TAIL"))
    private void onChunkDeltaUpdateHook(ClientboundSectionBlocksUpdatePacket packet, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new ChunkDeltaUpdateEvent());
    }
}
