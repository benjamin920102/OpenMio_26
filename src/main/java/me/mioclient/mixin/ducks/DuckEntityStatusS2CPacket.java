package me.mioclient.mixin.ducks;

import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientboundEntityEventPacket.class})

public interface DuckEntityStatusS2CPacket {
    @Accessor("entityId")
    int getId();
}
