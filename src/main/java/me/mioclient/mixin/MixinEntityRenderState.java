package me.mioclient.mixin;

import me.mioclient.ducks.DuckChamsState;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/* Implements DuckChamsState on the vanilla render state. Zero means no tint. */
@Mixin({EntityRenderState.class})
public abstract class MixinEntityRenderState implements DuckChamsState {
    @Unique
    private int mio$chamsTint;

    @Override
    public int mio$getChamsTint() {
        return this.mio$chamsTint;
    }

    @Override
    public void mio$setChamsTint(int tint) {
        this.mio$chamsTint = tint;
    }
}
