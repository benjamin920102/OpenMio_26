package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.mojang.authlib.GameProfile;
import me.mioclient.module.client.IRC;
import me.mioclient.module.player.NameProtect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({AbstractClientPlayerEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinAbstractClientPlayerEntity.class */
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity {
    private static Identifier PEPSI = Identifier.of("mio", "capes/pepsi.png");
    private static Identifier TETRIS = Identifier.of("mio", "capes/tetris.png");
    private static Identifier HEROBRINE = Identifier.of("mio-mount", "textures/skin_protect.png");
    private static IRC irc;

    private static IRC getIrc() {

        if (irc == null && ClientServices.moduleInjector != null) {

            try { irc = (IRC) ClientServices.moduleInjector.getModule(IRC.class); } catch (Exception e) {}

        }

        return irc;

    }
    private static NameProtect nameprotect;

    private static NameProtect getNameprotect() {

        if (nameprotect == null && ClientServices.moduleInjector != null) {

            try { nameprotect = (NameProtect) ClientServices.moduleInjector.getModule(NameProtect.class); } catch (Exception e) {}

        }

        return nameprotect;

    }

    public MixinAbstractClientPlayerEntity(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Inject(method = {"getSkinTextures"}, at = {@At("RETURN")}, cancellable = true)
    private void getCape(CallbackInfoReturnable<SkinTextures> callbackInfoReturnable) {
        IRC irc_lcl = getIrc();
        NameProtect nameprotect_lcl = getNameprotect();

        if (irc_lcl == null) return;

        if (nameprotect_lcl == null) return;

        Identifier identifier2309;
        SkinTextures skinTextures = (SkinTextures) callbackInfoReturnable.getReturnValue();
        Identifier texture = skinTextures.texture();
        SkinTextures.Model model = skinTextures.model();
        if (nameprotect_lcl.isToggled() && nameprotect_lcl.skin.getValue().booleanValue() && MinecraftClient.getInstance().player.equals(this)) {
            texture = HEROBRINE;
            model = SkinTextures.Model.WIDE;
            if (nameprotect_lcl.slim.getValue().booleanValue()) {
                model = SkinTextures.Model.SLIM;
            }
            callbackInfoReturnable.setReturnValue(new SkinTextures(texture, skinTextures.textureUrl(), skinTextures.capeTexture(), skinTextures.elytraTexture(), model, skinTextures.secure()));
        }
        if ("cattyyyn".equals(getGameProfile().getName())) {
            callbackInfoReturnable.setReturnValue(new SkinTextures(texture, skinTextures.textureUrl(), PEPSI, skinTextures.elytraTexture(), model, skinTextures.secure()));
        }
        if ("u3o".equals(getGameProfile().getName())) {
            callbackInfoReturnable.setReturnValue(new SkinTextures(texture, skinTextures.textureUrl(), TETRIS, skinTextures.elytraTexture(), model, skinTextures.secure()));
        }
        if (ClientServices.nameTagsManager.isConnected() && irc_lcl.capes.getValue().booleanValue() && (identifier2309 = ClientServices.nameTagsManager.getModeIdentifier(getGameProfile().getName())) != null) {
            callbackInfoReturnable.setReturnValue(new SkinTextures(texture, skinTextures.textureUrl(), identifier2309, skinTextures.elytraTexture(), model, skinTextures.secure()));
            callbackInfoReturnable.cancel();
        }
    }
}
