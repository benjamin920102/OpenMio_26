package me.mioclient.mixin;

import me.mioclient.event.events.entity.EntityRenderEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.ShaderPassRegistry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.ShaderPassRegistry;

import me.mioclient.event.events.entity.EntityRenderEvent;
import me.mioclient.module.render.Chams;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.world.WorldView;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({EntityRenderDispatcher.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinEntityRenderDispatcher.class */
public class MixinEntityRenderDispatcher {
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

    @Inject(method = {"renderShadow"}, at = {@At("HEAD")}, cancellable = true)
    private static void onRenderShadow(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity entity, float f, float f2, WorldView worldView, float f3, CallbackInfo callbackInfo) {
        Chams chams_lcl = getChams();

        if (chams_lcl == null) return;

        if (chams_lcl.shouldShineEntity(entity)) {
            callbackInfo.cancel();
        }
        if (ShaderPassRegistry.flag) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"render"}, at = {@At("HEAD")}, cancellable = true)
    public <E extends Entity> void onRenderPre(E e, double d, double d2, double d3, float f, float f2, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo) {
        EntityRenderEvent.Inner inner994 = EntityRenderEvent.Inner.of(e, matrixStack, vertexConsumerProvider);
        ClientAccessor.eventBus.fireEvent(inner994);
        if (inner994.isCancelled() || e == null) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"render"}, at = {@At("RETURN")})
    public <E extends Entity> void onRenderPost(E e, double d, double d2, double d3, float f, float f2, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(EntityRenderEvent.Inner_2.getInner_21412(e, matrixStack, vertexConsumerProvider));
    }

    @Inject(method = {"renderFire"}, at = {@At("HEAD")}, cancellable = true)
    private void renderFireHook(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity entity, Quaternionf quaternionf, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();
        Chams chams_lcl = getChams();

        if (norender_lcl == null) return;

        if (chams_lcl == null) return;

        if (chams_lcl.shouldShineEntity(entity)) {
            callbackInfo.cancel();
        }
        if (norender_lcl.isToggled() && norender_lcl.fire.getValue().booleanValue()) {
            if (norender_lcl.others.getValue().booleanValue() || (entity instanceof ClientPlayerEntity) || (entity instanceof EndCrystalEntity)) {
                callbackInfo.cancel();
            }
        }
    }

    @Inject(method = {"renderHitbox"}, at = {@At("HEAD")}, cancellable = true)
    private static void renderHitboxHook(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, float f, float f2, float f3, float f4, CallbackInfo callbackInfo) {
        if (Chams.flag) {
            callbackInfo.cancel();
        }
    }
}
