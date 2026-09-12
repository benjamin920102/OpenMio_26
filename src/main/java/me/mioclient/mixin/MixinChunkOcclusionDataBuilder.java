package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.player.Freecam;
import me.mioclient.module.render.Xray;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({ChunkOcclusionDataBuilder.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinChunkOcclusionDataBuilder.class */
public class MixinChunkOcclusionDataBuilder {
    private static Freecam freecam;

    private static Freecam getFreecam() {

        if (freecam == null && ClientServices.moduleInjector != null) {

            try { freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class); } catch (Exception e) {}

        }

        return freecam;

    }

    @Inject(method = {"markClosed"}, at = {@At("HEAD")}, cancellable = true)
    private void markClosedHook(BlockPos blockPos, CallbackInfo callbackInfo) {
        Freecam freecam_lcl = getFreecam();

        if (freecam_lcl == null) return;

        if (Xray.getInstance().isToggled() || freecam_lcl.isToggled()) {
            callbackInfo.cancel();
        }
    }
}
