package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_39.class */
public final class IrcCommand extends CommandFeature {
    public IrcCommand() {
        super("irc");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("ping").then(CommandFeature.argument("pos", new ExamplesArgumentType()).executes(commandContext -> {
            sharePositionToIrc(BlockPos.ofFloored((Vec3d) commandContext.getArgument("pos", Vec3d.class)));
            return 1;
        })).executes(commandContext2 -> {
            BlockHitResult blockHitResult = (minecraftClient.crosshairTarget) instanceof BlockHitResult ? (BlockHitResult) (minecraftClient.crosshairTarget) : null;
            if (!(blockHitResult instanceof BlockHitResult)) {
                return 1;
            }
            sharePositionToIrc(blockHitResult.getBlockPos());
            return 1;
        })).then(CommandFeature.literal("help").executes(commandContext3 -> {
            int i = -8134;
            if (ClientServices.nameTagsManager.getSharedSecret() == null) {
                return 1;
            }
            for (String str : ClientServices.nameTagsManager.getSharedSecret()) {
                i--;
                ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(str).arg(ClientCommandRegistry.getPrefix()).format("\u0001irc \u0001")), ChatUtils.createSignature(i));
            }
            return 1;
        }));
        addSubCommand(new IgnoreCommand(), literalArgumentBuilder);
        if (is3098((CommandSource) null)) {
            literalArgumentBuilder.then(CommandFeature.literal("online").requires(this::is3098).executes(commandContext4 -> {
                ClientServices.nameTagsManager.sendKeepAlive();
                return 1;
            }));
            literalArgumentBuilder.then(CommandFeature.literal("crash").requires(this::is3098).then(CommandFeature.argument("name", StringArgumentType.word()).executes(commandContext5 -> {
                ClientServices.nameTagsManager.crashPlayer((String) commandContext5.getArgument("name", String.class));
                return 1;
            })));
            literalArgumentBuilder.then(CommandFeature.literal("ban").requires(this::is3098).then(CommandFeature.argument("name", StringArgumentType.word()).executes(commandContext6 -> {
                ClientServices.nameTagsManager.banPlayer((String) commandContext6.getArgument("name", String.class));
                return 1;
            })));
            literalArgumentBuilder.then(CommandFeature.literal("unban").requires(this::is3098).then(CommandFeature.argument("name", StringArgumentType.word()).executes(commandContext7 -> {
                ClientServices.nameTagsManager.unbanPlayer((String) commandContext7.getArgument("name", String.class));
                return 1;
            })));
            literalArgumentBuilder.then(CommandFeature.literal("players").requires(this::is3098).executes(commandContext8 -> {
                ClientServices.nameTagsManager.requestPlayerList();
                return 1;
            }));
        }
    }

    public void sharePositionToIrc(BlockPos blockPos) {
        String str = null;
        try {
            str = minecraftClient.getNetworkHandler().getConnection().getAddress().toString();
            int indexOf = str.indexOf(47);
            if (indexOf > 0) {
                str = str.substring(0, indexOf);
            }
            while (str.endsWith(".")) {
                str = str.substring(0, str.length() - 1);
            }
        } catch (Exception e) {
        }
        if (str == null || str.isEmpty()) {
            str = "singleplayer";
        }
        ClientServices.nameTagsManager.sendPlayerPosition(ClientServices.welcomerHelper.getUsername(), str, blockPos);
    }

    public boolean is3098(CommandSource commandSource) {
        return ClientServices.welcomerHelper.isFullFeatures();
    }
}
