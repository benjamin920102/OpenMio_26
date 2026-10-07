package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.MathConstants;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class PitchCommand extends CommandFeature {
    public PitchCommand() {
        super("pitch");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("pitch", FloatArgumentType.floatArg(-MathConstants.num2, MathConstants.num2)).executes(commandContext -> {
            minecraftClient.player.setXRot(((Float) commandContext.getArgument("pitch", Float.class)).floatValue());
            ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(String.valueOf(commandContext.getArgument("pitch", Float.class))).format("Player's pitch has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        }));
    }
}
