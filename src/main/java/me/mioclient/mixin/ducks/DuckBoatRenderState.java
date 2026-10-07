package me.mioclient.mixin.ducks;

import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.spongepowered.asm.mixin.Mixin;

/* 26.2 port (verified): BoatRenderState fields (yRot, hurtDir, hurtTime,
   damageTime, bubbleAngle, isUnderWater, rowingTimeLeft/Right) are all public.
   No accessor needed — this duck is a typed marker so callers can depend on the
   26.2 state class without reflection. */
@Mixin({BoatRenderState.class})
public interface DuckBoatRenderState {
}
