package me.mioclient.command;

import me.mioclient.network.PacketSender;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalXZ;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;

public class ElytraCommand extends CommandFeature {
    public ElytraCommand() {
        super("elytra");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", StringArgumentType.string()).suggests((commandContext, suggestionsBuilder) -> {
            return WaypointsCommand.suggestFilteredWaypoints(commandContext, suggestionsBuilder, WaypointsCommand.formatServerAddress());
        }).executes(commandContext2 -> {
            Vec3 vec3d2126 = WaypointsCommand.resolveWaypointPosition(commandContext2);
            if (vec3d2126 == null) {
                return 0;
            }
            IBaritone primaryBaritone = BaritoneAPI.getProvider().getPrimaryBaritone();
            Goal goalXZ = new GoalXZ((int) vec3d2126.x(), (int) vec3d2126.z());
            primaryBaritone.getCustomGoalProcess().setGoal(goalXZ);
            primaryBaritone.getElytraProcess().pathTo(goalXZ);
            if (minecraftClient.player.isFallFlying()) {
                return 1;
            }
            if (minecraftClient.player.onGround()) {
                minecraftClient.player.push(0.0d, 0.42d, 0.0d);
            }
            runLater(() -> {
                PacketSender.sendStartFallFlying();
                minecraftClient.player.startFallFlying();
            });
            return 1;
        }));
    }
}
