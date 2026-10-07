package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.command.PanicCommand;

import me.mioclient.module.client.UI;
import me.mioclient.module.exploit.FakeVanilla;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({JoinMultiplayerScreen.class})

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

    protected MixinMultiplayerScreen(Component text) {
        super(text);
    }

    /* 26.2 port (verified via JDK25 javap): JoinMultiplayerScreen.init exists;
       keyPressed(int,int,int) gone (now keyPressed(KeyEvent)) — hook deleted. */
    @Inject(method = {"init"}, at = {@At("TAIL")}, require = 0)
    private void initHook(CallbackInfo callbackInfo) {
        FakeVanilla fakevanilla_lcl = getFakevanilla();

        if (fakevanilla_lcl == null) return;

        if (PanicCommand.flag) {
            return;
        }
        addRenderableWidget(Checkbox.builder(Component.literal("Vanilla"), this.font).pos(2, 6).selected(fakevanilla_lcl.isToggled()).onValueChange((checkboxWidget, z) -> {
            fakevanilla_lcl.setToggled(z);
        }).build());
    }
}
