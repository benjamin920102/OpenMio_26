package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.BoolArgumentType;
import java.util.Iterator;
import me.mioclient.module.Module;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class DrawnCommand extends CommandFeature {
    public DrawnCommand() {
        super("drawn");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("module", new ModuleArgumentType()).executes(commandContext -> {
            Module module = (Module) commandContext.getArgument("module", Module.class);
            module.setDrawn(!module.isDrawn());
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(module.isDrawn()).arg(module.getName()).format("Drawing for \u0001 has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        })).then(CommandFeature.literal("all").then(CommandFeature.argument("state", BoolArgumentType.bool()).executes(commandContext2 -> {
            boolean booleanValue = ((Boolean) commandContext2.getArgument("state", Boolean.class)).booleanValue();
            Iterator it = ClientServices.moduleRegistry.getRegistry().iterator();
            while (it.hasNext()) {
                ((Module) it.next()).setDrawn(booleanValue);
            }
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(booleanValue).format("Drawing for all modules has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        })));
    }
}
