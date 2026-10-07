package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.event.events.network.ChatEvent;
import me.mioclient.event.events.network.Vec3dEvent;
import me.mioclient.module.render.Ambience;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.phys.Vec3;

@Mixin(ClientPacketListener.class)
public class MixinClientNetworkHandler {
    private static Ambience ambience;

    private static Ambience getAmbience() {
        if (ambience == null && ClientServices.moduleInjector != null) {
            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}
        }
        return ambience;
    }

    @Unique
    private ChatEvent event;

    @Inject(method = "handleSetTime", at = @At("HEAD"), cancellable = true)
    private void onWorldTimeUpdateHook(ClientboundSetTimePacket packet, CallbackInfo callbackInfo) {
        Ambience ambience_lcl = getAmbience();
        if (ambience_lcl == null) return;
        if (ambience_lcl.isToggled() && ambience_lcl.worldTime.getValue().booleanValue() && Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.setTimeFromServer(ambience_lcl.getWorldTime());
            callbackInfo.cancel();
        }
    }

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void sendChatMessageHook(String message, CallbackInfo callbackInfo) {
        if (message.startsWith(ClientCommandRegistry.getPrefix())) {
            ClientCommandRegistry.dispatch(message.substring(ClientCommandRegistry.getPrefix().length()));
            callbackInfo.cancel();
        }
    }

    @ModifyVariable(method = "sendChat", at = @At(value = "INVOKE", target = "Ljava/time/Instant;now()Ljava/time/Instant;", shift = At.Shift.BEFORE), argsOnly = true)
    private String dabigbulletz(String message) {
        this.event = new ChatEvent(message);
        ClientAccessor.eventBus.fireEvent(this.event);
        return this.event.getMessage();
    }

    @Inject(method = "sendChat", at = @At(value = "INVOKE", target = "Ljava/time/Instant;now()Ljava/time/Instant;", shift = At.Shift.AFTER), cancellable = true)
    private void dabigbulletz(String message, CallbackInfo callbackInfo) {
        if (this.event == null || !this.event.isCancelled()) {
            return;
        }
        callbackInfo.cancel();
    }

    @Inject(method = "handleMoveVehicle", at = @At("HEAD"), cancellable = true)
    private void onVehicleMove(ClientboundMoveVehiclePacket packet, CallbackInfo callbackInfo) {
        Vec3dEvent vec3dEvent = new Vec3dEvent(new net.minecraft.world.phys.Vec3(packet.position().x, packet.position().y, packet.position().z));
        ClientAccessor.eventBus.fireEvent(vec3dEvent);
        if (vec3dEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
