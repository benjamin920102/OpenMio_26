package me.mioclient.command;

import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class NbtCommand extends CommandFeature {
    public NbtCommand() {
        super("nbt");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            // 26.2: Entity#saveWithoutId(ValueOutput) writes into TagValueOutput; dump held item + player NBT.
            CompoundTag entityNbt = new CompoundTag();
            try {
                net.minecraft.util.ProblemReporter reporter = net.minecraft.util.ProblemReporter.DISCARDING;
                net.minecraft.world.level.storage.TagValueOutput output =
                        net.minecraft.world.level.storage.TagValueOutput.createWithoutContext(reporter);
                minecraftClient.player.saveWithoutId(output);
                entityNbt = output.buildResult();
            } catch (Exception ignored) {
            }
            NbtPathArgument.NbtPath parse;
            try {
                parse = NbtPathArgument.NbtPath.of("SelectedItem");
            } catch (CommandSyntaxException e) {
                parse = null;
            }
            MutableComponent append = Component.empty().append("Nbt: ");
            try {
                List<Tag> list = parse == null ? List.of() : parse.get(entityNbt);
                if (!list.isEmpty()) {
                    append.append(" ").append(Component.literal(NbtUtils.prettyPrint(list.getFirst(), true)));
                }
            } catch (CommandSyntaxException e) {
                append.append("{}");
            }
            ChatUtils.sendMessage((Component) append, ChatUtils.createSignature(-1));
            return 1;
        });
    }
}
