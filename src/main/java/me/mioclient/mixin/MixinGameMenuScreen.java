package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.screen.ConfirmDisconnect;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.screen.ConfirmDisconnect;
import me.mioclient.module.exploit.IllegalDisconnect;
import me.mioclient.module.misc.AntiQuit;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({GameMenuScreen.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinGameMenuScreen.class */
public abstract class MixinGameMenuScreen extends Screen {

    @Unique
    private boolean mio$ignoreDisconnect;
    private static IllegalDisconnect illegaldisconnect;

    private static IllegalDisconnect getIllegaldisconnect() {

        if (illegaldisconnect == null && ClientServices.moduleInjector != null) {

            try { illegaldisconnect = (IllegalDisconnect) ClientServices.moduleInjector.getModule(IllegalDisconnect.class); } catch (Exception e) {}

        }

        return illegaldisconnect;

    }
    private static AntiQuit antiquit;

    private static AntiQuit getAntiquit() {

        if (antiquit == null && ClientServices.moduleInjector != null) {

            try { antiquit = (AntiQuit) ClientServices.moduleInjector.getModule(AntiQuit.class); } catch (Exception e) {}

        }

        return antiquit;

    }

    @Shadow
    protected abstract void method_47632();

    protected MixinGameMenuScreen(Text text) {
        super(text);
    }

    @Inject(method = {"disconnect"}, at = {@At("HEAD")}, cancellable = true)
    private void disconnectHook(CallbackInfo callbackInfo) {
        IllegalDisconnect illegaldisconnect_lcl = getIllegaldisconnect();
        AntiQuit antiquit_lcl = getAntiquit();

        if (illegaldisconnect_lcl == null) return;

        if (antiquit_lcl == null) return;

        if (antiquit_lcl.isToggled() && antiquit_lcl.disconnect.getValue().booleanValue() && !this.mio$ignoreDisconnect) {
            this.client.setScreen(new ConfirmDisconnect(() -> {
                this.mio$ignoreDisconnect = true;
                method_47632();
            }));
            callbackInfo.cancel();
        } else {
            if (!illegaldisconnect_lcl.isToggled() || this.client.player == null || this.client.isInSingleplayer()) {
                return;
            }
            illegaldisconnect_lcl.disconnectFromServer();
            callbackInfo.cancel();
        }
    }
}
