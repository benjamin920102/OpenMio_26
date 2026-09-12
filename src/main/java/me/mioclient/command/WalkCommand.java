package me.mioclient.command;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalXZ;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_36.class */
public class WalkCommand extends CommandFeature {
    public WalkCommand() {
        super("walk");
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
            primaryBaritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ((int) vec3d2126.getX(), (int) vec3d2126.getZ()));
            return 1;
        }));
    }
}
