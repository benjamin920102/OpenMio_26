package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.ShaderPassRegistry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.ShaderPassRegistry;

import me.mioclient.event.events.render.RenderLabelEvent;
import me.mioclient.module.render.Ambience;
import me.mioclient.module.render.NameTags;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({EntityRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinEntityRenderer.class */
public abstract class MixinEntityRenderer<T extends Entity> {
    private static NameTags nametags;

    private static NameTags getNametags() {

        if (nametags == null && ClientServices.moduleInjector != null) {

            try { nametags = (NameTags) ClientServices.moduleInjector.getModule(NameTags.class); } catch (Exception e) {}

        }

        return nametags;

    }
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }

    @Inject(method = {"renderLabelIfPresent"}, at = {@At("HEAD")}, cancellable = true)
    private void onRenderLabel(T t, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, CallbackInfo callbackInfo) {
        if (ShaderPassRegistry.flag) {
            callbackInfo.cancel();
            return;
        }
        RenderLabelEvent renderLabelEvent = new RenderLabelEvent(matrixStack, t);
        ClientAccessor.eventBus.fireEvent(renderLabelEvent);
        if (renderLabelEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"hasLabel"}, at = {@At("HEAD")}, cancellable = true)
    private void hasLabelHook(T t, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NameTags nametags_lcl = getNametags();

        if (nametags_lcl == null) return;

        if ((t instanceof PlayerEntity) && nametags_lcl.isToggled()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(Boolean.valueOf(t.hasCustomName()));
        }
    }

    @Inject(method = {"getSkyLight"}, at = {@At("RETURN")}, cancellable = true)
    private void getSkyLightHook(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return;

        if (ambience_lcl.isToggled() && ambience_lcl.brightness.getValue() == Ambience.MixinEntityRendererMode.SKY) {
            callbackInfoReturnable.setReturnValue(Integer.valueOf(Math.max(ambience_lcl.lightLevel.getValue().intValue(), callbackInfoReturnable.getReturnValueI())));
        }
    }
}
