package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shape.BlockHighlightRenderer;

import me.mioclient.api.ClientServices;
import me.mioclient.render.shape.BlockHighlightRenderer;

import java.awt.Color;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_38.class */
public class HighlightCommand extends CommandFeature {
    public static final Color color = new Color(255, 0, 0, 120);
    public static final Color color2 = new Color(0, 0, 0, 0);

    public HighlightCommand() {
        super("highlight");
        ClientServices.antiPhaseBlockHelper.register(new BlockHighlightRenderer.Record(this, () -> {
            return color;
        }, () -> {
            return color2;
        }, () -> {
            return Float.valueOf(Float.intBitsToFloat(1065353216));
        }, () -> {
            return Float.valueOf(Float.intBitsToFloat(1084227584));
        }, () -> {
            return true;
        }, () -> {
            return true;
        }, 1000));
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("pos", new ExamplesArgumentType()).then(CommandFeature.literal("beam").executes(commandContext -> {
            ClientServices.antiPhaseBlockHelper.addHighlight(this, Box.of((Vec3d) commandContext.getArgument("pos", Vec3d.class), Double.longBitsToDouble(4598175219545276416L), Double.longBitsToDouble(4598175219545276416L), Double.longBitsToDouble(4598175219545276416L)).withMaxY(minecraftClient.world.getTopY()).withMinY(minecraftClient.world.getBottomY()));
            return 1;
        })).executes(commandContext2 -> {
            ClientServices.antiPhaseBlockHelper.addHighlight(this, Box.of((Vec3d) commandContext2.getArgument("pos", Vec3d.class), Double.longBitsToDouble(4598175219545276416L), Double.longBitsToDouble(4598175219545276416L), Double.longBitsToDouble(4598175219545276416L)));
            return 1;
        }));
    }
}
