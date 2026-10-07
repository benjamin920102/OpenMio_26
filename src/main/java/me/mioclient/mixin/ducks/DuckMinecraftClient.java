package me.mioclient.mixin.ducks;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/* 26.2 port (verified): Yarn doAttack/doItemUse are MojMap startAttack/startUseItem.
   itemUseCooldown is gone (FastPlace rework); disconnecting flag is gone. */
@Mixin({Minecraft.class})
public interface DuckMinecraftClient {
    @Invoker("startAttack")
    boolean attack();

    @Invoker("startUseItem")
    void interact();
}
