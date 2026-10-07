package me.mioclient.mixin;

import com.mojang.authlib.GameProfile;
import me.mioclient.api.ClientServices;
import me.mioclient.module.client.IRC;
import me.mioclient.module.player.NameProtect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayerEntity extends Player {
    private static final Identifier PEPSI = Identifier.fromNamespaceAndPath("mio", "capes/pepsi.png");
    private static final Identifier TETRIS = Identifier.fromNamespaceAndPath("mio", "capes/tetris.png");
    private static final Identifier HEROBRINE = Identifier.fromNamespaceAndPath("mio-mount", "textures/skin_protect.png");
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

    public MixinAbstractClientPlayerEntity(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Unique
    private static ClientAsset.Texture mio$capeOf(Identifier id) {
        return new ClientAsset.Texture() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Identifier texturePath() {
                return id;
            }
        };
    }

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void getCape(CallbackInfoReturnable<PlayerSkin> callbackInfoReturnable) {
        IRC irc_lcl = getIrc();
        NameProtect nameprotect_lcl = getNameprotect();
        if (irc_lcl == null) return;
        if (nameprotect_lcl == null) return;
        PlayerSkin skin = callbackInfoReturnable.getReturnValue();
        ClientAsset.Texture body = skin.body();
        ClientAsset.Texture elytra = skin.elytra();
        PlayerModelType model = skin.model();
        if (nameprotect_lcl.isToggled() && nameprotect_lcl.skin.getValue().booleanValue() && Minecraft.getInstance().player.equals(this)) {
            body = mio$capeOf(HEROBRINE);
            model = PlayerModelType.WIDE;
            if (nameprotect_lcl.slim.getValue().booleanValue()) {
                model = PlayerModelType.SLIM;
            }
            callbackInfoReturnable.setReturnValue(new PlayerSkin(body, skin.cape(), elytra, model, skin.secure()));
            skin = callbackInfoReturnable.getReturnValue();
            body = skin.body();
            elytra = skin.elytra();
            model = skin.model();
        }
        if ("cattyyyn".equals(getGameProfile().name())) {
            callbackInfoReturnable.setReturnValue(new PlayerSkin(body, mio$capeOf(PEPSI), elytra, model, skin.secure()));
        }
        if ("u3o".equals(getGameProfile().name())) {
            callbackInfoReturnable.setReturnValue(new PlayerSkin(body, mio$capeOf(TETRIS), elytra, model, skin.secure()));
        }
        Identifier modeId;
        if (ClientServices.nameTagsManager.isConnected() && irc_lcl.capes.getValue().booleanValue() && (modeId = ClientServices.nameTagsManager.getModeIdentifier(getGameProfile().name())) != null) {
            callbackInfoReturnable.setReturnValue(new PlayerSkin(body, mio$capeOf(modeId), elytra, model, skin.secure()));
            callbackInfoReturnable.cancel();
        }
    }
}
