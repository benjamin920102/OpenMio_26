package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.command.PanicCommand;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.command.PanicCommand;
import me.mioclient.module.client.HUD;
import me.mioclient.module.client.UI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({TitleScreen.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinTitleScreen.class */
public class MixinTitleScreen extends Screen implements ClientAccessor {
    private static HUD h;
    private static UI c;

    private static HUD getH() {
        if (h == null && ClientServices.moduleInjector != null) {
            try { h = (HUD) ClientServices.moduleInjector.getModule(HUD.class); } catch (Exception e) {}
        }
        return h;
    }

    private static UI getC() {
        if (c == null && ClientServices.moduleInjector != null) {
            try { c = (UI) ClientServices.moduleInjector.getModule(UI.class); } catch (Exception e) {}
        }
        return c;
    }

    protected MixinTitleScreen(Text text) {
        super(text);
    }

    @Inject(method = {"render"}, at = {@At("TAIL")})
    private void renderHook(DrawContext drawContext, int i, int i2, float f, CallbackInfo callbackInfo) {
        if (PanicCommand.flag) {
            return;
        }
        HUD hud = getH();
        if (hud == null) return;
        drawContext.drawTextWithShadow(minecraftClient.textRenderer, "Mio " + String.valueOf(Formatting.WHITE) + "v2 " + String.valueOf(Formatting.GRAY) + "(patch " + "02/03/2025 22:47".substring(0, 10) + ")", 2, 2, hud.setting8.getValue().computeColor(hud, 0.0f).getRGB());
    }

    public boolean keyPressed(int i, int i2, int i3) {
        UI ui = getC();
        if (ui == null) return true;
        if (ui.getKeybind().isMouseKeybind() || ui.getKeybind().getKey() != i) {
            return true;
        }
        ui.enable();
        ClientServices.getClickGui().openWithParent();
        return true;
    }
}
