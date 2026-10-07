package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.module.render.Shader;
import me.mioclient.render.GizmoRenderer;
import me.mioclient.render.RenderUtil;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 world-render driver (verified via JDK25 javap on the 26.2 merged jar).
   LevelRenderer.submitEntities does not exist in 26.2; the per-frame entry point
   is LevelRenderer.render(...), which carries the DeltaTracker (partial ticks)
   and the live CameraRenderState. This hook fires the client's world-render
   event at HEAD unconditionally (no entity needs to be in view), then flushes
   the gizmo records so vanilla draws them in the same frame. World-space
   drawing goes through GizmoRenderer (vanilla gizmos); no custom submit
   pipeline or GL state is involved. */
@Mixin({LevelRenderer.class})

public abstract class MixinLevelRenderer {
    /* The outline target only composites when the flag is set. Vanilla sets it
       for its own glowing entities; while the Shader module runs it is forced so
       custom outline colors always have a target to land in. Phases still drain
       on execute, so nothing accumulates. */
    @Inject(method = {"submitFeatures(Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;Z)V"}, at = {@At("HEAD")})
    private void mio$forceEntityOutlines(net.minecraft.client.renderer.state.level.LevelRenderState state, net.minecraft.client.renderer.SubmitNodeCollector collector, boolean bl, CallbackInfo ci) {
        try {
            if (ClientServices.moduleInjector == null || state == null) {
                return;
            }
            Shader shader;
            try {
                shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class);
            } catch (Exception e) {
                return;
            }
            if (shader != null && shader.isToggled()) {
                state.shouldShowEntityOutlines = true;
            }
        } catch (Exception ignored) {
        }
    }

    @Inject(method = {"render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"}, at = {@At("HEAD")})
    private void mio$fireWorldRender(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean bl, CameraRenderState cameraState, Matrix4fc matrix, GpuBufferSlice slice, Vector4f vector, boolean bl2, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || cameraState == null) {
            return;
        }
        float partialTick;
        try {
            partialTick = deltaTracker.getGameTimeDeltaTicks();
        } catch (Exception e) {
            partialTick = 1.0f;
        }
        RenderUtil.setPartialTick(partialTick);
        RenderUtil.setMatrixStack(new PoseStack());
        GizmoRenderer.beginWorldPhase();
        try {
            MatrixStackEvent.Inner_3 event = MatrixStackEvent.Inner_3.getInner_3133(RenderUtil.getMatrixStack(), partialTick);
            event.cameraState = cameraState;
            ClientAccessor.eventBus.fireEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                GizmoRenderer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            GizmoRenderer.endWorldPhase();
        }
    }
}
