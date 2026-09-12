package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.ESP;
import me.mioclient.module.render.NoRender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({BlockEntityRenderDispatcher.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinBlockEntityRenderDispatcher.class */
public class MixinBlockEntityRenderDispatcher {
    private static ESP esp;

    private static ESP getEsp() {

        if (esp == null && ClientServices.moduleInjector != null) {

            try { esp = (ESP) ClientServices.moduleInjector.getModule(ESP.class); } catch (Exception e) {}

        }

        return esp;

    }
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Inject(method = {"render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"}, at = {@At("HEAD")}, cancellable = true)
    private <E extends BlockEntity> void renderHook(E e, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, CallbackInfo callbackInfo) {
        NoRender norender_lcl = getNorender();
        ESP esp_lcl = getEsp();

        if (norender_lcl == null) return;

        if (esp_lcl == null) return;

        if (!(norender_lcl.ignoreESP.getValue().booleanValue() && esp_lcl.is1918()) && norender_lcl.isToggled() && norender_lcl.tileEntities.getValue().booleanValue() && e.getPos().getSquaredDistance(MinecraftClient.getInstance().gameRenderer.getCamera().getBlockPos()) > norender_lcl.tileDistance.getValue().intValue() * norender_lcl.tileDistance.getValue().intValue()) {
            callbackInfo.cancel();
        }
    }
}
