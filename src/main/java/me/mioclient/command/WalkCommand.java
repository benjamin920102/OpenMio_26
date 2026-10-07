package me.mioclient.command;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalXZ;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;

public class WalkCommand extends CommandFeature {
    public WalkCommand() {
        super("walk");
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
            primaryBaritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ((int) vec3d2126.x(), (int) vec3d2126.z()));
            return 1;
        }));
    }
}
