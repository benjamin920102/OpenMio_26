package me.mioclient.mixin.ducks;

import net.minecraft.client.model.object.crystal.EndCrystalModel;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): EndCrystalRenderer holds
   `private final EndCrystalModel model`. Old DuckEndCrystalEntityRenderer
   (core/bottom/frame ModelParts) has no equivalent — chams now uses the model. */
@Mixin({EndCrystalRenderer.class})
public interface DuckEndCrystalRenderer {
    @Accessor("model")
    EndCrystalModel mio$getModel();
}
