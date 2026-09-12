package me.mioclient.module.movement;

import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.Vec3dUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/Spiral.class */
public class Spiral extends Module {
    public Setting<Integer> setting;
    public final List<Vec3d> list;
    public int current;

    public Spiral() {
        super("Spiral", Category.MOVEMENT, new String[0]);
        this.list = new ArrayList(5000);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (this.setting == null) {
            return;
        }
        this.current = 0;
        this.list.clear();
        Vec3d pos = minecraftClient.player.getPos();
        float f = 0.0f;
        for (int i = 0; i < 5000 * 4; i++) {
            this.list.add(pos);
            pos = pos.add(Vec3d.fromPolar(0.0f, f).multiply(i * this.setting.getValue().intValue() * 16));
            f = MathHelper.wrapDegrees(f + Float.intBitsToFloat(1119092736));
        }
    }

    public Vec3d getWaypoint(int i) {
        return this.list.get(i);
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        for (int max = Math.max(this.current, 1); max < Math.max(this.current, 1) + 2; max++) {
            RenderUtil.INSTANCE.drawDebugLine(inner_3.getMatrixStack(), this.list.get(max - 1), this.list.get(max), Color.white);
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        Vec3d vec3d2994 = getWaypoint(this.current);
        minecraftClient.player.setYaw(RotationManager.getRotationsTo(vec3d2994)[0]);
        if (Vec3dUtils.getHorizontalDistance(vec3d2994, minecraftClient.player.getPos()) < Float.intBitsToFloat(1077936128)) {
            this.current++;
        }
    }
}
