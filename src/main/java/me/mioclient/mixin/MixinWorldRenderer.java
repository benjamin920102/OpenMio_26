package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.FullscreenQuad;
import me.mioclient.render.shader.ShaderPassRegistry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.FullscreenQuad;
import me.mioclient.render.RenderUtil;
import me.mioclient.render.shader.ShaderPassRegistry;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import me.mioclient.ducks.DuckWorldRendererFramebuffer;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.event.events.render.DrawBlockOutlineEvent;
import me.mioclient.module.player.Freecam;
import me.mioclient.module.render.Ambience;
import me.mioclient.module.render.Chams;
import me.mioclient.module.render.NoRender;
import me.mioclient.module.render.SkyColor;
import me.mioclient.module.render.ViewClip;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.mioclient.ducks.DuckClientWorldEffects;

/* compiled from: 0.java */
@Mixin({WorldRenderer.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinWorldRenderer.class */
public abstract class MixinWorldRenderer implements ClientAccessor, DuckWorldRendererFramebuffer {
    private static ViewClip viewclip;

    private static ViewClip getViewclip() {

        if (viewclip == null && ClientServices.moduleInjector != null) {

            try { viewclip = (ViewClip) ClientServices.moduleInjector.getModule(ViewClip.class); } catch (Exception e) {}

        }

        return viewclip;

    }
    private static Ambience ambience;

    private static Ambience getAmbience() {

        if (ambience == null && ClientServices.moduleInjector != null) {

            try { ambience = (Ambience) ClientServices.moduleInjector.getModule(Ambience.class); } catch (Exception e) {}

        }

        return ambience;

    }
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static Freecam freecam;

    private static Freecam getFreecam() {

        if (freecam == null && ClientServices.moduleInjector != null) {

            try { freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class); } catch (Exception e) {}

        }

        return freecam;

    }
    private static SkyColor skycolor;

    private static SkyColor getSkycolor() {

        if (skycolor == null && ClientServices.moduleInjector != null) {

            try { skycolor = (SkyColor) ClientServices.moduleInjector.getModule(SkyColor.class); } catch (Exception e) {}

        }

        return skycolor;

    }
    private static Chams chams;

    private static Chams getChams() {

        if (chams == null && ClientServices.moduleInjector != null) {

            try { chams = (Chams) ClientServices.moduleInjector.getModule(Chams.class); } catch (Exception e) {}

        }

        return chams;

    }

    @Unique
    private MatrixStack mio$stack;

    @Shadow
    private Framebuffer field_4101;

    @Shadow
    @Nullable
    private ClientWorld field_4085;

    @Shadow
    protected abstract void method_22977(Entity entity, double d, double d2, double d3, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider);

    @Shadow
    private void method_22712(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double d2, double d3, BlockPos blockPos, BlockState blockState) {
    }

    @Inject(method = {"renderWeather"}, at = {@At("HEAD")}, cancellable = true)
    private void renderWeatherHook(LightmapTextureManager lightmapTextureManager, float f, double d, double d2, double d3, CallbackInfo callbackInfo) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return;

        if (ambience_lcl.isToggled() && ambience_lcl.worldWeather.getValue().booleanValue() && ambience_lcl.weather.getValue() == Ambience.AmbiencePredicateMode.CLEAR) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"render"}, at = {@At("RETURN")})
    private void render(RenderTickCounter renderTickCounter, boolean z, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo callbackInfo, @Local MatrixStack matrixStack) {
        matrixStack.push();
        RenderUtil.applyCameraRotation(matrixStack);
        RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStackEvent.Inner_3 inner_3133 = MatrixStackEvent.Inner_3.getInner_3133(matrixStack, RenderUtil.getPartialTick());
        RenderUtil.INSTANCE.runWithMSAA(() -> {
            eventBus.fireEvent(inner_3133);
        });
        eventBus.fireEvent(MatrixStackEvent.Inner_2.getInner_23016(matrixStack, RenderUtil.getPartialTick()));
        matrixStack.pop();
    }

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getEntityVertexConsumers()Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;", shift = At.Shift.BEFORE)})
    private void render_pre(RenderTickCounter renderTickCounter, boolean z, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo callbackInfo, @Local MatrixStack matrixStack) {
        eventBus.fireEvent(MatrixStackEvent.Inner.getInner933(matrixStack, RenderUtil.getPartialTick()));
    }

    @Redirect(method = {"render"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
    private void render_drawBlockOutline(WorldRenderer worldRenderer, MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double d2, double d3, BlockPos blockPos, BlockState blockState) {
        DrawBlockOutlineEvent drawBlockOutlineEvent = new DrawBlockOutlineEvent(matrixStack, vertexConsumer, blockPos, blockState);
        eventBus.fireEvent(drawBlockOutlineEvent);
        if (drawBlockOutlineEvent.isCancelled()) {
            return;
        }
        method_22712(drawBlockOutlineEvent.getMatrixStack(), drawBlockOutlineEvent.getVertexConsumer(), entity, d, d2, d3, drawBlockOutlineEvent.getBlockPos(), drawBlockOutlineEvent.getBlockState());
    }

    @Inject(method = {"hasBlindnessOrDarkness"}, at = {@At("HEAD")}, cancellable = true)
    private void invokeBlindnessHook(Camera camera, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.blindness.getValue().booleanValue()) {
            callbackInfoReturnable.setReturnValue(null);
        }
    }

    @Redirect(method = {"render"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"))
    private boolean renderHook(ClientPlayerEntity clientPlayerEntity) {
        Freecam freecam_lcl = getFreecam();
        ViewClip viewclip_lcl = getViewclip();

        if (freecam_lcl == null) return false;

        if (viewclip_lcl == null) return false;

        if (freecam_lcl.isToggled()) {
            return true;
        }
        if (viewclip_lcl.isToggled() && minecraftClient.gameRenderer.getCamera().isThirdPerson()) {
            return true;
        }
        return clientPlayerEntity.isSpectator();
    }

    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    private void initHook(MinecraftClient minecraftClient, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilderStorage, CallbackInfo callbackInfo) {
        FullscreenQuad.init();
        ShaderPassRegistry.init();
    }

    @Inject(method = {"render"}, at = {@At("HEAD")})
    private void onRenderHead(RenderTickCounter renderTickCounter, boolean z, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo callbackInfo) {
        RenderUtil.setPartialTick(renderTickCounter.getTickDelta(false));
        this.mio$stack = new MatrixStack();
        RenderUtil.setMatrixStack(this.mio$stack);
        ShaderPassRegistry.beginOutlinePass();
    }

    @Inject(method = {"render"}, at = {@At("TAIL")})
    private void onRenderTail(RenderTickCounter renderTickCounter, boolean z, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo callbackInfo) {
        ShaderPassRegistry.do866();
    }

    @Inject(method = {"renderEntity"}, at = {@At("HEAD")}, cancellable = true)
    private void renderEntity(Entity entity, double d, double d2, double d3, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();
        Chams chams_lcl = getChams();

        if (norender_lcl == null) return;

        if (chams_lcl == null) return;

        if ((norender_lcl.entities2.getValue().booleanValue() && norender_lcl.isEntityHidden(entity)) || chams_lcl.shouldRenderChams(entity)) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"onResized"}, at = {@At("HEAD")})
    private void onResized(int i, int i2, CallbackInfo callbackInfo) {
        ShaderPassRegistry.resizeFramebuffers(i, i2);
    }

    @Inject(method = {"renderWorldBorder"}, at = {@At("HEAD")}, cancellable = true)
    private void renderWorldBorderHook(Camera camera, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return;

        if (norender_lcl.isToggled() && norender_lcl.worldBorder.getValue().booleanValue()) {
            callbackInfo.cancel();
        }
    }

    @Override // me.mioclient.ducks.DuckWorldRendererFramebuffer
    public Framebuffer getFramebuffer() {
        return this.field_4101;
    }

    @Override // me.mioclient.ducks.DuckWorldRendererFramebuffer
    public void setFramebuffer(Framebuffer framebuffer) {
        this.field_4101 = framebuffer;
    }

    @ModifyVariable(method = {"getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I"}, at = @At("STORE"), ordinal = 0)
    private static int getLightmapCoordinates(int i) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return i;

        return (ambience_lcl.isToggled() && ambience_lcl.brightness.getValue() == Ambience.MixinEntityRendererMode.SKY) ? Math.max(ambience_lcl.lightLevel.getValue().intValue(), i) : Math.max(0, i);
    }

    @Redirect(method = {"render"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"))
    private boolean renderHook(Camera camera) {
        Freecam freecam_lcl = getFreecam();

        if (freecam_lcl == null) return false;

        if (freecam_lcl.isToggled()) {
            return true;
        }
        return camera.isThirdPerson();
    }

    @ModifyExpressionValue(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/DimensionEffects;isDarkened()Z")})
    private boolean isDarkened(boolean z) {
        SkyColor skycolor_lcl = getSkycolor();

        if (skycolor_lcl == null) return z;

        return (skycolor_lcl.isToggled() && skycolor_lcl.isDimensionEnabled()) ? ((me.mioclient.ducks.DuckClientWorldEffects)(Object) this.field_4085).mio$getOriginalEffects().isDarkened() : z;
    }

    @Inject(method = {"renderWeather"}, at = {@At("HEAD")})
    private void setWeatherAlpha(LightmapTextureManager lightmapTextureManager, float f, double d, double d2, double d3, CallbackInfo callbackInfo) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.25f);
    }

    @Inject(method = {"renderWeather"}, at = {@At("TAIL")})
    private void restoreWeatherAlpha(LightmapTextureManager lightmapTextureManager, float f, double d, double d2, double d3, CallbackInfo callbackInfo) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @ModifyExpressionValue(method = {"renderWeather"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;hasPrecipitation()Z")})
    private boolean renderWeatherHook(boolean z) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return z;

        return z || ambience_lcl.isWeatherForced();
    }

    @ModifyExpressionValue(method = {"renderWeather"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTopY(Lnet/minecraft/world/Heightmap$Type;II)I")})
    private int renderWeatherHook(int i) {
        Ambience ambience_lcl = getAmbience();

        if (ambience_lcl == null) return i;

        if (!ambience_lcl.isWeatherForced()) {
            return i;
        }
        int i2 = 5;
        if (MinecraftClient.isFancyGraphicsOrBetter()) {
            i2 = 10;
        }
        return (((int) Math.floor(minecraftClient.gameRenderer.getCamera().getPos().y)) - i2) - 1;
    }
}
