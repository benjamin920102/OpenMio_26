package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import me.mioclient.module.misc.ExtraTab;
import me.mioclient.module.player.NameProtect;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin({PlayerListHud.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinPlayerListHud.class */
public class MixinPlayerListHud {

    @Shadow
    @Final
    private MinecraftClient field_2155;
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static ExtraTab extratab;

    private static ExtraTab getExtratab() {

        if (extratab == null && ClientServices.moduleInjector != null) {

            try { extratab = (ExtraTab) ClientServices.moduleInjector.getModule(ExtraTab.class); } catch (Exception e) {}

        }

        return extratab;

    }
    private static NameProtect nameprotect;

    private static NameProtect getNameprotect() {

        if (nameprotect == null && ClientServices.moduleInjector != null) {

            try { nameprotect = (NameProtect) ClientServices.moduleInjector.getModule(NameProtect.class); } catch (Exception e) {}

        }

        return nameprotect;

    }

    @ModifyExpressionValue(method = {"collectPlayerEntries"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;ENTRY_ORDERING:Ljava/util/Comparator;")})
    private Comparator<PlayerListEntry> collectPlayerEntries(Comparator<PlayerListEntry> comparator) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return comparator;

        return (extratab_lcl.isToggled() && extratab_lcl.sort.getValue().booleanValue()) ? extratab_lcl.getComparator() : comparator;
    }

    @ModifyArg(method = {"collectPlayerEntries"}, at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;limit(J)Ljava/util/stream/Stream;"))
    private long renderHook(long j) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return 0L;

        if (extratab_lcl.isToggled()) {
            return Long.MAX_VALUE;
        }
        return j;
    }

    @ModifyExpressionValue(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isPartVisible(Lnet/minecraft/entity/player/PlayerModelPart;)Z")})
    private boolean renderHook(boolean z) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return z;

        if (extratab_lcl.isToggled() && extratab_lcl.betterSkin.getValue().booleanValue()) {
            return true;
        }
        return z;
    }

    @Inject(method = {"getPlayerName"}, at = {@At("RETURN")}, cancellable = true)
    private void suppressIndicatorBg(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> callbackInfoReturnable) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return;

        String strip;
        Color color530;
        if (extratab_lcl.isToggled() && extratab_lcl.highlight.getValue().booleanValue() && (strip = Formatting.strip(((Text) callbackInfoReturnable.getReturnValue()).getString())) != null) {
            String name = playerListEntry.getProfile().getName();
            if (extratab_lcl.social.getValue().booleanValue() && (color530 = ClientServices.socialManager.getSocialColor(name, null)) != null) {
                callbackInfoReturnable.setReturnValue(Text.literal(strip).styled(style -> {
                    return style.withColor(color530.hashCode());
                }));
            }
            if (MinecraftClient.getInstance().getSession().getUsername().equals(name)) {
                callbackInfoReturnable.setReturnValue(Text.literal(strip).styled(style2 -> {
                    return style2.withColor(extratab_lcl.self.getValue().hashCode());
                }));
            }
        }
    }

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z")})
    private void renderHook(DrawContext drawContext, int i, Scoreboard scoreboard, ScoreboardObjective scoreboardObjective, CallbackInfo callbackInfo, @Local(ordinal = 4) LocalIntRef localIntRef, @Local(ordinal = 5) LocalIntRef localIntRef2, @Local(ordinal = 6) LocalIntRef localIntRef3, @Local(ordinal = 0) List<PlayerListEntry> list) {
        if (getExtratab().isToggled()) {
            int size = list.size();
            int i2 = size;
            int i3 = 1;
            while (i2 > 40) {
                i3++;
                i2 = ((size + i3) - 1) / i3;
            }
            localIntRef3.set(i3);
            localIntRef2.set(i2);
            localIntRef.set(size);
        }
    }

    @Inject(method = {"renderLatencyIcon"}, at = {@At("HEAD")}, cancellable = true)
    private void renderLatencyIconHook(DrawContext drawContext, int i, int i2, int i3, PlayerListEntry playerListEntry, CallbackInfo callbackInfo) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return;

        if (!extratab_lcl.isToggled() || extratab_lcl.latency.getValue() == ExtraTab.MixinPlayerListHudMode.VANILLA) {
            return;
        }
        if (extratab_lcl.latency.getValue() == ExtraTab.MixinPlayerListHudMode.NONE) {
            callbackInfo.cancel();
            return;
        }
        String str = String.valueOf(Formatting.YELLOW) + String.valueOf(playerListEntry.getLatency());
        drawContext.drawTextWithShadow(this.field_2155.textRenderer, str, (i2 + i) - this.field_2155.textRenderer.getWidth(str), i3, 16777215);
        callbackInfo.cancel();
    }

    @ModifyConstant(method = {"render"}, constant = {@Constant(intValue = 13)})
    private int renderConstant(int i) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return i;

        if (extratab_lcl.isToggled() && extratab_lcl.latency.getValue() == ExtraTab.MixinPlayerListHudMode.NONE) {
            return 0;
        }
        return i;
    }

    @ModifyArgs(method = {"render"}, at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 0))
    private void render(Args args, @Local PlayerListEntry playerListEntry) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return;

        if (extratab_lcl.isToggled() && extratab_lcl.latency.getValue() == ExtraTab.MixinPlayerListHudMode.TEXT) {
            args.set(1, Integer.valueOf(((Integer) args.get(1)).intValue() + this.field_2155.textRenderer.getWidth(String.valueOf(playerListEntry.getLatency()))));
        }
    }

    @Redirect(method = {"render"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))
    private boolean africanAmerican(ClientConnection clientConnection) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return false;

        return clientConnection.isEncrypted() && !norender_lcl.shouldHideTabIcons();
    }

    @Inject(method = {"render"}, at = {@At("HEAD")})
    private void renderHead(DrawContext drawContext, int i, Scoreboard scoreboard, ScoreboardObjective scoreboardObjective, CallbackInfo callbackInfo) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return;

        if (extratab_lcl.getTabScale() == 1.0f) {
            return;
        }
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(extratab_lcl.getTabScale(), extratab_lcl.getTabScale(), 1.0f);
    }

    @Inject(method = {"render"}, at = {@At("TAIL")})
    private void renderTail(DrawContext drawContext, int i, Scoreboard scoreboard, ScoreboardObjective scoreboardObjective, CallbackInfo callbackInfo) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return;

        if (extratab_lcl.getTabScale() == 1.0f) {
            return;
        }
        drawContext.getMatrices().pop();
    }

    @ModifyVariable(method = {"render"}, at = @At("HEAD"), argsOnly = true)
    private int renderArgs(int i) {
        ExtraTab extratab_lcl = getExtratab();

        if (extratab_lcl == null) return i;

        return extratab_lcl.getTabScale() == 1.0f ? i : (int) (i / extratab_lcl.getTabScale());
    }

    @Inject(method = {"method_46511"}, at = {@At("HEAD")}, cancellable = true)
    private static void methodHook(PlayerListEntry playerListEntry, CallbackInfoReturnable<String> callbackInfoReturnable) {
        NameProtect nameprotect_lcl = getNameprotect();

        if (nameprotect_lcl == null) return;

        GameProfile gameProfile = MinecraftClient.getInstance().player.getGameProfile();
        if (nameprotect_lcl.isToggled() && playerListEntry.getProfile().equals(gameProfile)) {
            callbackInfoReturnable.setReturnValue(nameprotect_lcl.name.getValue());
        }
    }
}
