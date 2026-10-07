package me.mioclient.mixin.ducks;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified against 26.2 merged jar): BoatRenderer holds
   `private final EntityModel<BoatRenderState> model` plus a water-patch model.
   Old DuckBoatEntityRenderer (texturesAndModels map) has no 26.2 equivalent. */
@Mixin({BoatRenderer.class})
public interface DuckBoatRenderer {
    @Accessor("model")
    EntityModel<BoatRenderState> mio$getModel();
}
