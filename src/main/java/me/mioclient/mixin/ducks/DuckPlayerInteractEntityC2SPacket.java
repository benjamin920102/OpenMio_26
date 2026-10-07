package me.mioclient.mixin.ducks;

import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ServerboundInteractPacket.class})

public interface DuckPlayerInteractEntityC2SPacket {
    @Accessor("entityId")
    int getEntityId();
}
