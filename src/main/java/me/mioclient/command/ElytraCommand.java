package me.mioclient.command;

import me.mioclient.network.PacketSender;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalXZ;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_15.class */
public class ElytraCommand extends CommandFeature {
    public ElytraCommand() {
        super("elytra");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> {
            return WaypointsCommand.suggestFilteredWaypoints(commandContext, suggestionsBuilder, WaypointsCommand.formatServerAddress());
        }).executes(commandContext2 -> {
            Vec3d vec3d2126 = WaypointsCommand.resolveWaypointPosition(commandContext2);
            if (vec3d2126 == null) {
                return 0;
            }
            IBaritone primaryBaritone = BaritoneAPI.getProvider().getPrimaryBaritone();
            Goal goalXZ = new GoalXZ((int) vec3d2126.getX(), (int) vec3d2126.getZ());
            primaryBaritone.getCustomGoalProcess().setGoal(goalXZ);
            primaryBaritone.getElytraProcess().pathTo(goalXZ);
            if (minecraftClient.player.isFallFlying()) {
                return 1;
            }
            if (minecraftClient.player.isOnGround()) {
                minecraftClient.player.addVelocity(0.0d, Double.longBitsToDouble(4601237667291888353L), 0.0d);
            }
            runLater(() -> {
                PacketSender.sendStartFallFlying();
                minecraftClient.player.startFallFlying();
            });
            return 1;
        }));
    }
}
