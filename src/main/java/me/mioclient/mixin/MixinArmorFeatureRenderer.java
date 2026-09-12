package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.mesh.CustomRenderLayers;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import me.mioclient.render.mesh.CustomRenderLayers;
import me.mioclient.module.render.Chams;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin({ArmorFeatureRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinArmorFeatureRenderer.class */
public class MixinArmorFeatureRenderer {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static Chams chams;

    private static Chams getChams() {

        if (chams == null && ClientServices.moduleInjector != null) {

            try { chams = (Chams) ClientServices.moduleInjector.getModule(Chams.class); } catch (Exception e) {}

        }

        return chams;

    }

    @Unique
    private LivingEntity mio$lastEntity;

    @Inject(method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V"}, at = {@At("HEAD")}, cancellable = true)
    private <T extends LivingEntity> void renderHook(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T t, float f, float f2, float f3, float f4, float f5, float f6, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        this.mio$lastEntity = t;
        if (norender_lcl.getArmorOpacity() == 0.0f) {
            callbackInfo.cancel();
        }
    }

    @ModifyArgs(method = {"renderArmorParts"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    private void renderArmorParts(Args args) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        args.set(4, Integer.valueOf(ColorUtils.withAlphaPercent(new Color(((Integer) args.get(4)).intValue()), norender_lcl.getArmorOpacity() * norender_lcl.getGlobalOpacity()).hashCode()));
    }

    @Redirect(method = {"renderArmorParts"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private RenderLayer renerArmorParts(Identifier identifier) {
        NoRender norender_lcl = getNorender();
        Chams chams_lcl = getChams();

        if (norender_lcl == null) return null;

        if (chams_lcl == null) return null;

        float f = norender_lcl.getArmorOpacity() * norender_lcl.getGlobalOpacity();
        if (chams_lcl.shouldRenderChams(this.mio$lastEntity)) {
            f *= chams_lcl.opacity.getValue().intValue();
        }
        return f == 1.0f ? RenderLayer.getArmorCutoutNoCull(identifier) : CustomRenderLayers.getRenderLayer2031(identifier);
    }
}
