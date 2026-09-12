package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.event.EventBus;

import me.mioclient.event.events.player.TickPreEvent;
import me.mioclient.module.movement.EntityControl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({BoatEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinBoat.class */
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

    public MixinBoat(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = {"clampPassengerYaw"}, at = {@At("HEAD")}, cancellable = true)
    public void clampPassengerYawHook(Entity entity, CallbackInfo callbackInfo) {
        EntityControl entitycontrol_lcl = getEntitycontrol();

        if (entitycontrol_lcl == null) return;

        if (entitycontrol_lcl.isBoatOverride(this)) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"updatePaddles"}, at = {@At("HEAD")}, cancellable = true)
    private void updatePaddles(CallbackInfo callbackInfo) {
        EntityControl entitycontrol_lcl = getEntitycontrol();

        if (entitycontrol_lcl == null) return;

        if (entitycontrol_lcl.isBoatOverride(this)) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"tick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE)})
    private void tickPre(CallbackInfo callbackInfo) {
        if (TickPreEvent.isPlayerVehicle(this)) {
            this.mio$event = new TickPreEvent(getVelocity());
            eventBus.fireEvent(this.mio$event);
            Vec3dUtils.setVectorComponents(getVelocity(), this.mio$event.getX(), this.mio$event.getY(), this.mio$event.getZ());
        }
    }
}
