package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.module.HudModule;

import java.util.HashSet;
import java.util.Set;
import me.mioclient.module.Module;
import net.minecraft.commands.CommandSourceStack;

public final class AllOffCommand extends CommandFeature {
    public final Set<Module> set;

    public AllOffCommand() {
        super("alloff");
        this.set = new HashSet();
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            this.set.clear();
            for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                if (!(module instanceof HudModule) && module.isToggled()) {
                    module.toggle();
                    this.set.add(module);
                }
            }
            return 1;
        });
        literalArgumentBuilder.then(CommandFeature.literalArgument("restore", "backup").executes(commandContext2 -> {
            for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                if (!(module instanceof HudModule)) {
                    module.setToggled(this.set.contains(module));
                }
            }
            return 1;
        }));
    }
}
