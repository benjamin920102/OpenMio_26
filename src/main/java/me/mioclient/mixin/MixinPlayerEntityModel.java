package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/* compiled from: 0.java */
@Mixin({PlayerEntityModel.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinPlayerEntityModel.class */
public class MixinPlayerEntityModel {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @ModifyExpressionValue(method = {"setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z")})
    private boolean setAnglesHook(boolean z) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return z;

        if (norender_lcl.getArmorOpacity() == 0.0f) {
            return true;
        }
        return z;
    }
}
