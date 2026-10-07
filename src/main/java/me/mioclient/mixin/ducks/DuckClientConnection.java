package me.mioclient.mixin.ducks;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Connection.class})

public interface DuckClientConnection {
    @Accessor("channel")
    Channel getChannel();
}
