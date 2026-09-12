package me.mioclient.mixin;

import me.mioclient.ducks.DuckCapabilityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/* compiled from: 0.java */
@Mixin(targets = {"com.mojang.blaze3d.platform.GlStateManager$CapabilityTracker"})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinCapabilityTracker.class */
public abstract class MixinCapabilityTracker implements DuckCapabilityTracker {

    @Shadow
    private boolean field_5051;

    @Shadow
    public abstract void method_4470(boolean z);

    @Override // me.mioclient.ducks.DuckCapabilityTracker
    public boolean getState() {
        return this.field_5051;
    }

    @Override // me.mioclient.ducks.DuckCapabilityTracker
    public void set(boolean z) {
        method_4470(z);
    }
}
