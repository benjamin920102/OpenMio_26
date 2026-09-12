package me.mioclient.module.movement;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/AntiVoid.class */
public class AntiVoid extends Module {
    public Setting<AntiVoidMode> mode = add(new EnumSetting<>("Mode", AntiVoidMode.CANCEL));
    public Setting<Integer> height = add(new NumberSetting<>("Height", 30, 1, 100).setSuffix("m"));
    public Setting<Boolean> forceGround = add(new BooleanSetting("ForceGround", false, v -> this.mode.getValue() == AntiVoid.AntiVoidMode.CANCEL));

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/AntiVoid$AntiVoidMode.class */
    public enum AntiVoidMode implements NamedValue {
        TELEPORT("Teleport"),
        CANCEL("Cancel");

        public final String name;

        AntiVoidMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public AntiVoid() {
        super("AntiVoid", "Prevents you from falling into the void.", Category.MOVEMENT, new String[0]);
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return ClientTextRenderer.capitalize(this.mode.getValue());
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (minecraftClient.player.isOnGround() || minecraftClient.player.fallDistance <= 0.0f) {
            return;
        }
        if (minecraftClient.world.raycast(new BlockStateRaycastContext(minecraftClient.player.getPos(), new Vec3d(minecraftClient.player.getX(), minecraftClient.world.getBottomY() - 1, minecraftClient.player.getZ()), blockState -> {
            return blockState.isSolid();
        })).getType() == HitResult.Type.MISS && minecraftClient.world.getBottomY() + this.height.getValue().intValue() >= minecraftClient.player.getY()) {
            if (this.mode.getValue() == AntiVoidMode.TELEPORT) {
                minecraftClient.player.setVelocity(0.0d, Double.longBitsToDouble(4621819117588971520L), 0.0d);
            } else {
                minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, 0.0d));
                if (this.forceGround.getValue().booleanValue()) {
                    minecraftClient.player.setOnGround(true);
                }
            }
        }
    }
}
