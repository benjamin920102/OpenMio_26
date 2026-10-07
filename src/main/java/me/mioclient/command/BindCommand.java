package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.client.InputState;

import java.util.Iterator;
import me.mioclient.api.Keybind;
import me.mioclient.module.Module;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class BindCommand extends CommandFeature {
    public BindCommand() {
        super("bind");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("set").then(CommandFeature.argument("module", new ModuleArgumentType()).then(CommandFeature.argument("key", new KeybindArgumentType()).executes(commandContext -> {
            Module module = (Module) commandContext.getArgument("module", Module.class);
            Keybind keybind = (Keybind) commandContext.getArgument("key", Keybind.class);
            module.modifyKeybind(keybind2 -> {
                return keybind2.withKey(keybind.getKey()).withMouse(keybind.isMouseKeybind());
            });
            ChatUtils.sendMessage(Component.literal("Bind for ").append(themedLiteral(module.getName())).append(" has been set to ").append(themedLiteral(InputState.keyCodeToString(keybind.getKey(), keybind.isMouseKeybind()))), ChatUtils.createSignature(-1));
            return 1;
        })))).then(CommandFeature.literal("clear").then(CommandFeature.argument("module", new ModuleArgumentType()).executes(commandContext2 -> {
            Module module = (Module) commandContext2.getArgument("module", Module.class);
            module.modifyKeybind(keybind -> {
                return keybind.withKey(-1);
            });
            ChatUtils.sendMessage(Component.literal("Bind for ").append(themedLiteral(module.getName())).append(" has been reset"), ChatUtils.createSignature(-1));
            return 1;
        })).then(CommandFeature.literal("all").executes(commandContext3 -> {
            Iterator it = ClientServices.moduleRegistry.getRegistry().iterator();
            while (it.hasNext()) {
                ((Module) it.next()).modifyKeybind(keybind -> {
                    return keybind.withKey(-1);
                });
            }
            ChatUtils.sendMessage(Component.literal("Bind for all modules has been reset"), ChatUtils.createSignature(-1));
            return 1;
        })));
    }
}
