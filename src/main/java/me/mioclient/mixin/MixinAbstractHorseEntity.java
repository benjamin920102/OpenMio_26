package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.EntityControl;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({AbstractHorseEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinAbstractHorseEntity.class */
public class MixinAbstractHorseEntity {
    private static EntityControl entitycontrol;

    private static EntityControl getEntitycontrol() {

        if (entitycontrol == null && ClientServices.moduleInjector != null) {

            try { entitycontrol = (EntityControl) ClientServices.moduleInjector.getModule(EntityControl.class); } catch (Exception e) {}

        }

        return entitycontrol;

    }

    @Inject(method = {"isSaddled"}, at = {@At("HEAD")}, cancellable = true)
    private void isSaddledHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        EntityControl entitycontrol_lcl = getEntitycontrol();

        if (entitycontrol_lcl == null) return;

        if (entitycontrol_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }
}
