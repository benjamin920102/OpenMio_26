package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.sugar.Local;
import me.mioclient.event.events.network.Vec3dEvent;
import me.mioclient.event.events.network.ChatEvent;
import me.mioclient.module.render.Ambience;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({ClientPlayNetworkHandler.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinClientNetworkHandler.class */
public class MixinClientNetworkHandler {
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }

    @Shadow
    private ClientWorld field_3699;

    @Unique
    private ChatEvent event;

    @Inject(method = {"sendChatMessage"}, at = {@At("HEAD")}, cancellable = true)
    private void sendChatMessageHook(String str, CallbackInfo callbackInfo) {
        if (str.startsWith(ClientCommandRegistry.getPrefix())) {
            ClientCommandRegistry.dispatch(str.substring(ClientCommandRegistry.getPrefix().length()));
            callbackInfo.cancel();
        }
    }

    @ModifyVariable(method = {"sendChatMessage"}, at = @At(value = "INVOKE", target = "Ljava/time/Instant;now()Ljava/time/Instant;", shift = At.Shift.BEFORE), argsOnly = true)
    private String dabigbulletz(String str) {
        this.event = new ChatEvent(str);
        ClientAccessor.eventBus.fireEvent(this.event);
        return this.event.getMessage();
    }

    @Inject(method = {"sendChatMessage"}, at = {@At(value = "INVOKE", target = "Ljava/time/Instant;now()Ljava/time/Instant;", shift = At.Shift.AFTER)}, cancellable = true)
    private void dabigbulletz(String str, CallbackInfo callbackInfo) {
        if (this.event == null || !this.event.isCancelled()) {
            return;
        }
        callbackInfo.cancel();
    }

    @Inject(method = {"onWorldTimeUpdate"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER)}, cancellable = true)
    private void onWorldTimeUpdateHook(WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket, CallbackInfo callbackInfo) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return;

        if (ambience_lcl.isToggled() && ambience_lcl.worldTime.getValue().booleanValue()) {
            this.field_3699.setTime(ambience_lcl.getWorldTime());
            this.field_3699.setTimeOfDay(ambience_lcl.getWorldTime());
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"onVehicleMove"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updatePositionAndAngles(DDDFF)V")}, cancellable = true)
    private void onVehicleMove(VehicleMoveS2CPacket vehicleMoveS2CPacket, CallbackInfo callbackInfo, @Local Entity entity) {
        Vec3dEvent vec3dEvent = new Vec3dEvent(new Vec3d(vehicleMoveS2CPacket.getX(), vehicleMoveS2CPacket.getY(), vehicleMoveS2CPacket.getZ()));
        ClientAccessor.eventBus.fireEvent(vec3dEvent);
        if (vec3dEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
