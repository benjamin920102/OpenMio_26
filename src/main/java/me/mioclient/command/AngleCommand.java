package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.module.player.RotationLock;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import me.mioclient.manager.RotationManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_7.class */
public class AngleCommand extends CommandFeature {
    public static final RotationLock rotationLock = (RotationLock) ClientServices.moduleInjector.getModule(RotationLock.class);

    public AngleCommand() {
        super("angle");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("pos", new ExamplesArgumentType()).then(CommandFeature.literal("yawlock").executes(commandContext -> {
            float[] floatArray2484 = RotationManager.getRotationsTo((Vec3d) commandContext.getArgument("pos", Vec3d.class));
            rotationLock.value2.setValue(Float.valueOf(floatArray2484[0]));
            return 1;
        })).executes(commandContext2 -> {
            float[] floatArray2484 = RotationManager.getRotationsTo((Vec3d) commandContext2.getArgument("pos", Vec3d.class));
            ChatUtils.sendMessage(Text.literal("Calculated angle. Yaw: %.1f, Pitch: %.1f.".formatted(Float.valueOf(floatArray2484[0]), Float.valueOf(floatArray2484[1]))), ChatUtils.createSignature(-1));
            return 1;
        }));
    }
}
