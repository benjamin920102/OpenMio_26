package me.mioclient.command;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_30.class */
public final class SetYawCommand extends CommandFeature {
    public SetYawCommand() {
        super("yaw");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("yaw", FloatArgumentType.floatArg(Float.intBitsToFloat(-1020002304), Float.intBitsToFloat(1127481344))).executes(commandContext -> {
            minecraftClient.player.setYaw(((Float) commandContext.getArgument("yaw", Float.class)).floatValue());
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(String.valueOf(commandContext.getArgument("yaw", Float.class))).format("Player's yaw has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        }));
    }
}
