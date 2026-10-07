package me.mioclient.mixin.ducks;

import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port: Yarn User is MojMap client.User; username field is name. */
@Mixin({User.class})
public interface DuckSession {
    @Accessor("name")
    @Mutable
    void setUsername(String str);
}
