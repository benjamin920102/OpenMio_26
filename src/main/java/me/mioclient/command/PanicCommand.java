package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.command.ClientCommandRegistry;

import me.mioclient.module.Module;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import me.mioclient.ducks.DuckChatHud;
import me.mioclient.command.CommandFeature;
import me.mioclient.ducks.DuckChatHudLineVisible;

public final class PanicCommand extends CommandFeature {
    public static final String string = "ㅤ";
    public static boolean flag = false;

    public PanicCommand() {
        super("panic");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            runLater(() -> {
                minecraftClient.setScreenAndShow(new ConfirmScreen(z -> {
                    if (z) {
                        do2977();
                    }
                    minecraftClient.setScreenAndShow((Screen) null);
                }, Component.literal("Unload the client."), Component.literal("Continue?")));
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
        minecraftClient.gui.hud.getChat().getRecentChat().removeIf(str -> {
            return str.startsWith(string2982);
        });
        ((DuckChatHud) minecraftClient.gui.hud.getChat()).getVisible().removeIf(visible -> {
            DuckChatHudLineVisible signatureHelper = (DuckChatHudLineVisible)(Object) visible;
            return signatureHelper.getSignature() != null && signatureHelper.getSignature().asByteBuffer().getInt() < 0;
        });
        minecraftClient.gui.hud.getChat().getRecentChat().removeIf(str2 -> {
            return str2.startsWith(string2982);
        });
    }
}
