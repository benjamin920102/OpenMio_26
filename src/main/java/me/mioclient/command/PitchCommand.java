package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.MathConstants;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.MathConstants;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_2.class */
public final class PitchCommand extends CommandFeature {
    public PitchCommand() {
        super("pitch");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("pitch", FloatArgumentType.floatArg(-MathConstants.num2, MathConstants.num2)).executes(commandContext -> {
            minecraftClient.player.setPitch(((Float) commandContext.getArgument("pitch", Float.class)).floatValue());
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(String.valueOf(commandContext.getArgument("pitch", Float.class))).format("Player's pitch has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        }));
    }
}
