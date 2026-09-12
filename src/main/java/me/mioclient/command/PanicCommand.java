package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.command.ClientCommandRegistry;

import me.mioclient.module.Module;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import me.mioclient.ducks.DuckChatHud;
import me.mioclient.command.CommandFeature;
import me.mioclient.ducks.DuckChatHudLineVisible;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PresetHelperFeature.class */
public final class PanicCommand extends CommandFeature {
    public static final String string = "ㅤ";
    public static boolean flag = false;

    public PanicCommand() {
        super("panic");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            runLater(() -> {
                minecraftClient.setScreen(new ConfirmScreen(z -> {
                    if (z) {
                        do2977();
                    }
                    minecraftClient.setScreen((Screen) null);
                }, Text.literal("Unload the client."), Text.literal("Continue?")));
            });
            return 1;
        });
    }

    public void do2977() {
        ClientServices.presetHelper.do41();
        flag = true;
        String string2982 = ClientCommandRegistry.getPrefix();
        ClientCommandRegistry.setPrefix("ㅤ");
        for (Module module : ClientServices.moduleRegistry.getRegistry()) {
            module.modifyKeybind(keybind -> {
                return keybind.withKey(-1);
            });
            if (module.isToggled()) {
                module.toggle();
            }
        }
        minecraftClient.inGameHud.getChatHud().getMessageHistory().removeIf(str -> {
            return str.startsWith(string2982);
        });
        ((DuckChatHud) minecraftClient.inGameHud.getChatHud()).getVisible().removeIf(visible -> {
            DuckChatHudLineVisible signatureHelper = (DuckChatHudLineVisible)(Object) visible;
            return signatureHelper.getSignature() != null && signatureHelper.getSignature().toByteBuffer().getInt() < 0;
        });
        minecraftClient.inGameHud.getChatHud().getMessageHistory().removeIf(str2 -> {
            return str2.startsWith(string2982);
        });
    }
}
