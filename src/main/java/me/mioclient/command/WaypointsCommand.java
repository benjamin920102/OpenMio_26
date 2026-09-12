package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.world.StashFinderMode;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.world.Waypoint;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.Waypoint;
import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import me.mioclient.command.EnumArgumentType;
import me.mioclient.module.render.Waypoints;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.Vec3d;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.mode.world.StashFinderMode;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WaypointsCommand.class */
public final class WaypointsCommand extends CommandFeature {
    public WaypointsCommand() {
        super("waypoints");
        setAliases("wp");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literal("add").then(CommandFeature.argument("name", StringArgumentType.string()).then(CommandFeature.argument("pos", new ExamplesArgumentType()).executes(commandContext -> {
            Waypoint waypointsEnumSettingHelper = new Waypoint((String) commandContext.getArgument("name", String.class), (Vec3d) commandContext.getArgument("pos", Vec3d.class), WorldUtils.getDimension().getDisplayName().toLowerCase(), formatServerAddress());
            ClientServices.waypointsManager.register(waypointsEnumSettingHelper);
            ChatUtils.sendMessage(Text.literal("Waypoint ").append(waypointsEnumSettingHelper.getAsText()).append(" has been created"), -1);
            return 1;
        }).then(CommandFeature.argument("dimension", new EnumArgumentType(StashFinderMode.class, "dimension")).executes(commandContext2 -> {
            Waypoint waypointsEnumSettingHelper = new Waypoint((String) commandContext2.getArgument("name", String.class), (Vec3d) commandContext2.getArgument("pos", Vec3d.class), ((StashFinderMode) commandContext2.getArgument("dimension", StashFinderMode.class)).getDisplayName().toLowerCase(), formatServerAddress());
            ClientServices.waypointsManager.register(waypointsEnumSettingHelper);
            ChatUtils.sendMessage(Text.literal("Waypoint ").append(waypointsEnumSettingHelper.getAsText()).append(" has been created"), -1);
            return 1;
        }))).executes(commandContext3 -> {
            Waypoint waypointsEnumSettingHelper = new Waypoint((String) commandContext3.getArgument("name", String.class), minecraftClient.player.getPos(), WorldUtils.getDimension().getDisplayName().toLowerCase(), formatServerAddress());
            ClientServices.waypointsManager.register(waypointsEnumSettingHelper);
            ChatUtils.sendMessage(Text.literal("Waypoint ").append(waypointsEnumSettingHelper.getAsText()).append(" has been created"), -1);
            return 1;
        }))).then(CommandFeature.literalArgument("remove", "delete", "del").then(CommandFeature.argument("server", StringArgumentType.string()).suggests(WaypointsCommand::suggestWaypointNamesForServer).then(CommandFeature.argument("name", StringArgumentType.string()).suggests(WaypointsCommand::suggestWaypointNames).executes(commandContext4 -> {
            String str = (String) commandContext4.getArgument("name", String.class);
            String str2 = (String) commandContext4.getArgument("server", String.class);
            ClientServices.waypointsManager.getRegistry().removeIf(waypointsEnumSettingHelper -> {
                return str2.equalsIgnoreCase(waypointsEnumSettingHelper.getServer()) && str.equalsIgnoreCase(waypointsEnumSettingHelper.getName());
            });
            ChatUtils.sendMessage(Text.literal("Waypoint ").append(str).append(" has been removed"), -1);
            return 1;
        })))).then(CommandFeature.literal("rename").then(CommandFeature.argument("server", StringArgumentType.string()).suggests(WaypointsCommand::suggestWaypointNamesForServer).then(CommandFeature.argument("name", StringArgumentType.string()).suggests(WaypointsCommand::suggestWaypointNames).then(CommandFeature.argument("target", StringArgumentType.string()).executes(commandContext5 -> {
            String str = (String) commandContext5.getArgument("name", String.class);
            String str2 = (String) commandContext5.getArgument("server", String.class);
            String str3 = (String) commandContext5.getArgument("target", String.class);
            java.util.function.Predicate<Waypoint> predicate = waypointsEnumSettingHelper -> {
                return str2.equalsIgnoreCase(waypointsEnumSettingHelper.getServer()) && str.equalsIgnoreCase(waypointsEnumSettingHelper.getName());
            };
            Waypoint orElse = ClientServices.waypointsManager.findFirst(predicate).orElse(null);
            if (orElse == null) {
                ChatUtils.sendMessage(Text.of("Waypoint not found"), -1);
                return 1;
            }
            ClientServices.waypointsManager.getRegistry().removeIf(predicate);
            ClientServices.waypointsManager.getRegistry().add(new Waypoint(str3, orElse.toVec3d(), orElse.getDimension(), orElse.getServer()));
            ChatUtils.sendMessage(Text.literal("Waypoint ").append(str).append(" has been renamed to").append(str3), -1);
            return 1;
        }))))).then(CommandFeature.literal("list").executes(commandContext6 -> {
            Text empty = Text.empty();
            ((MutableText) empty).append("Waypoints list: ");
            ((MutableText) empty).append(Texts.join(ClientServices.waypointsManager.getRegistry().stream().map((v0) -> {
                return v0.getAsText();
            }).toList(), Text.literal(", ")));
            ChatUtils.sendMessage(empty, -1);
            return 1;
        }));
        literalArgumentBuilder.then(CommandFeature.literal("toggle").then(CommandFeature.argument("server", StringArgumentType.string()).suggests(WaypointsCommand::suggestWaypointNamesForServer).then(CommandFeature.argument("name", StringArgumentType.string()).suggests(WaypointsCommand::suggestWaypointNames).executes(commandContext7 -> {
            String str = (String) commandContext7.getArgument("name", String.class);
            String str2 = (String) commandContext7.getArgument("server", String.class);
            Waypoint orElse = ClientServices.waypointsManager.findFirst(waypointsEnumSettingHelper -> {
                return str2.equalsIgnoreCase(waypointsEnumSettingHelper.getServer()) && str.equalsIgnoreCase(waypointsEnumSettingHelper.getName());
            }).orElse(null);
            if (orElse == null) {
                ChatUtils.sendMessage(Text.of("Waypoint not found"), -1);
                return 1;
            }
            orElse.setToggled(!orElse.isToggled());
            Object[] objArr = new Object[2];
            objArr[0] = str;
            objArr[1] = orElse.isToggled() ? "visible" : "invisible";
            ChatUtils.sendMessage(Text.literal("Made the waypoint %s %s.".formatted(objArr)), -1);
            return 1;
        }))));
        if (ClientServices.obstaclePasserHelper.isBaritoneLoaded()) {
            addSubCommand(new ElytraCommand(), literalArgumentBuilder);
            addSubCommand(new WalkCommand(), literalArgumentBuilder);
        }
    }

    public static CompletableFuture<Suggestions> suggestWaypointNamesForServer(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return CommandSource.suggestMatching(ClientServices.waypointsManager.getRegistry().stream().map(WaypointsCommand::formatWaypointServer).distinct(), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> suggestFilteredWaypoints(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder, String str) {
        return CommandSource.suggestMatching(ClientServices.waypointsManager.getRegistry().stream().filter(waypointsEnumSettingHelper -> {
            return waypointsEnumSettingHelper.getServer().equalsIgnoreCase(str);
        }).map((v0) -> {
            return v0.getName();
        }), suggestionsBuilder);
    }

    public static CompletableFuture<Suggestions> suggestWaypointNames(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return suggestFilteredWaypoints(commandContext, suggestionsBuilder, (String) commandContext.getArgument("server", String.class));
    }

    public static String formatWaypointServer(Waypoint waypointsEnumSettingHelper) {
        String lowerCase = waypointsEnumSettingHelper.getServer().toLowerCase();
        return lowerCase.contains(":") ? new StringTemplate().arg(lowerCase).format("\"\u0001\"") : lowerCase;
    }

    public static String formatServerAddress() {
        ServerInfo serverInfo = minecraftClient.player.networkHandler.getServerInfo();
        if (serverInfo == null) {
            return "singleplayer";
        }
        String[] split = serverInfo.address.split(":");
        return (split.length == 2 && split[1].equalsIgnoreCase("25565")) ? split[0] : serverInfo.address;
    }

    public static Vec3d resolveWaypointPosition(CommandContext<CommandSource> commandContext) {
        String str = (String) commandContext.getArgument("name", String.class);
        Waypoint orElse = ClientServices.waypointsManager.findFirst(waypointsEnumSettingHelper -> {
            return waypointsEnumSettingHelper.matches(str, formatServerAddress());
        }).orElse(null);
        if (orElse == null) {
            return null;
        }
        return Waypoints.toCurrentDimension(orElse);
    }
}
