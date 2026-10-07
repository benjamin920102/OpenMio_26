package me.mioclient.ducks;

/* Plain duck interface (deliberately outside the mixin-owned package, following
   DuckLivingEntity): chams tint carried on the vanilla render state.
   Implemented by MixinEntityRenderState. */
public interface DuckChamsState {
    int mio$getChamsTint();

    void mio$setChamsTint(int tint);
}
