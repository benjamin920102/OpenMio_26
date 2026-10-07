package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import net.minecraft.world.entity.MoverType;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.player.TickPreEvent;
import me.mioclient.module.movement.EntityControl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractBoat.class})

public abstract class MixinBoat extends Entity implements ClientAccessor {
    private static EntityControl entitycontrol;

    private static EntityControl getEntitycontrol() {

        if (entitycontrol == null && ClientServices.moduleInjector != null) {

            try { entitycontrol = (EntityControl) ClientServices.moduleInjector.getModule(EntityControl.class); } catch (Exception e) {}

        }

        return entitycontrol;

    }

    @Unique
    private TickPreEvent mio$event;

    public MixinBoat(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    /* 26.2 port: clampPassengerYaw -> clampRotation (bytecode-verified). */
    @Inject(method = {"clampRotation"}, at = {@At("HEAD")}, cancellable = true)
    public void clampPassengerYawHook(Entity entity, CallbackInfo callbackInfo) {
        EntityControl entitycontrol_lcl = getEntitycontrol();

        if (entitycontrol_lcl == null) return;

        if (entitycontrol_lcl.isBoatOverride(this)) {
            callbackInfo.cancel();
        }
    }

    /* 26.2 port: updatePaddles() removed — paddle state now set inline in tick
       via setPaddleState. Cancelling the setter freezes paddles under EntityControl
       override, same effect as before. */
    @Inject(method = {"setPaddleState"}, at = {@At("HEAD")}, cancellable = true)
    private void updatePaddles(boolean bl, boolean bl2, CallbackInfo callbackInfo) {
        EntityControl entitycontrol_lcl = getEntitycontrol();

        if (entitycontrol_lcl == null) return;

        if (entitycontrol_lcl.isBoatOverride(this)) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", shift = At.Shift.BEFORE)})
    private void tickPre(CallbackInfo callbackInfo) {
        if (TickPreEvent.isPlayerVehicle(this)) {
            this.mio$event = new TickPreEvent(getDeltaMovement());
            eventBus.fireEvent(this.mio$event);
            setDeltaMovement(new Vec3(this.mio$event.getX(), this.mio$event.getY(), this.mio$event.getZ()));
        }
    }
}
