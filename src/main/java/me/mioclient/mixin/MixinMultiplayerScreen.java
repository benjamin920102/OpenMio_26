package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.command.PanicCommand;

import me.mioclient.api.ClientServices;

import me.mioclient.command.PanicCommand;
import me.mioclient.module.client.UI;
import me.mioclient.module.exploit.FakeVanilla;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({MultiplayerScreen.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinMultiplayerScreen.class */
public class MixinMultiplayerScreen extends Screen {
    private static FakeVanilla fakevanilla;

    private static FakeVanilla getFakevanilla() {

        if (fakevanilla == null && ClientServices.moduleInjector != null) {

            try { fakevanilla = (FakeVanilla) ClientServices.moduleInjector.getModule(FakeVanilla.class); } catch (Exception e) {}

        }

        return fakevanilla;

    }
    private static UI clickgui;

    private static UI getClickgui() {

        if (clickgui == null && ClientServices.moduleInjector != null) {

            try { clickgui = (UI) ClientServices.moduleInjector.getModule(UI.class); } catch (Exception e) {}

        }

        return clickgui;

    }

    protected MixinMultiplayerScreen(Text text) {
        super(text);
    }

    @Inject(method = {"init"}, at = {@At("TAIL")})
    private void initHook(CallbackInfo callbackInfo) {
        FakeVanilla fakevanilla_lcl = getFakevanilla();

        if (fakevanilla_lcl == null) return;

        if (PanicCommand.flag) {
            return;
        }
        addDrawableChild(CheckboxWidget.builder(Text.of("Vanilla"), this.textRenderer).pos(2, 6).checked(fakevanilla_lcl.isToggled()).callback((checkboxWidget, z) -> {
            fakevanilla_lcl.setToggled(z);
        }).build());
    }

    @Inject(method = {"keyPressed"}, at = {@At("HEAD")}, cancellable = true)
    private void keyPressed(int i, int i2, int i3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        UI clickgui_lcl = getClickgui();

        if (clickgui_lcl == null) return;

        if (!(getFocused() instanceof TextFieldWidget) && i == clickgui_lcl.getKeybind().getKey()) {
            callbackInfoReturnable.setReturnValue(false);
            callbackInfoReturnable.cancel();
            clickgui_lcl.enable();
            ClientServices.getClickGui().openWithParent();
        }
    }
}
