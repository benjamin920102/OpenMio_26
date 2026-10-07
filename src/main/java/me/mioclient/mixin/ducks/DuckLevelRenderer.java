package me.mioclient.mixin.ducks;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): LevelRenderer keeps `private final RenderTarget
   entityOutlineTarget` plus `private final LevelTargetBundle targets`.
   Public `entityOutlineTarget()` returns the bundle handle (may be null);
   this duck exposes the raw field + bundle for outline passes. */
@Mixin({LevelRenderer.class})
public interface DuckLevelRenderer {
    @Accessor("entityOutlineTarget")
    RenderTarget mio$getEntityOutlineTargetField();

    @Accessor("targets")
    LevelTargetBundle mio$getTargets();
}
