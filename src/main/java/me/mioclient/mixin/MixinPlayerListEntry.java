package me.mioclient.mixin;

import com.mojang.authlib.GameProfile;
import me.mioclient.ducks.DuckPlayerListEntry;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerInfo.class})

public class MixinPlayerListEntry implements DuckPlayerListEntry {

    @Unique
    private long mio$joinTime;

    @Inject(method = {"<init>(Lcom/mojang/authlib/GameProfile;Z)V"}, at = {@At("TAIL")})
    private void mio$init(GameProfile gameProfile, boolean z, CallbackInfo callbackInfo) {
        this.mio$joinTime = System.currentTimeMillis();
    }

    @Override // me.mioclient.ducks.DuckPlayerListEntry
    public long mio$getJoinTime() {
        return this.mio$joinTime;
    }
}
