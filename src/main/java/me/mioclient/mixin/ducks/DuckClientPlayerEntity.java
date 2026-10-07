package me.mioclient.mixin.ducks;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): only yRotLast/xRotLast/lastOnGround survive on LocalPlayer. */
@Mixin({LocalPlayer.class})
public interface DuckClientPlayerEntity {
    @Accessor("yRotLast")
    float lastYaw();

    @Accessor("yRotLast")
    void lastYaw(float f);

    @Accessor("xRotLast")
    float lastPitch();

    @Accessor("xRotLast")
    void lastPitch(float f);

    @Accessor("lastOnGround")
    boolean lastOnGround();

    @Accessor("lastOnGround")
    void lastOnGround(boolean z);
}
