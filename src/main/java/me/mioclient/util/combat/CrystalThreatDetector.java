package me.mioclient.util.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.util.time.Stopwatch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/CrystalThreatDetector.class */
public abstract class CrystalThreatDetector implements ClientAccessor {
    public final Stopwatch stopwatch = new Stopwatch();

    public abstract boolean isEnabled();

    public void recalculateLayout() {
        if (isLethalCrystalNearby()) {
            this.stopwatch.reset();
        }
    }

    public boolean isThreatActive() {
        return isEnabled() && !this.stopwatch.hasElapsedMs(750L);
    }

    public boolean isLethalCrystalNearby() {
        synchronized (minecraftClient.world.getEntities()) {
            for (Entity entity : minecraftClient.world.getEntities()) {
                if (isNearbyCrystal(entity)) {
                    if (isLethalExplosionAt(entity.getPos())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean isLethalExplosionAt(Vec3d vec3d) {
        if (vec3d.squaredDistanceTo(minecraftClient.player.getPos()) > Double.longBitsToDouble(4639270566145032192L)) {
            return false;
        }
        for (int i = 4; i >= 0; i--) {
            Box box1109 = ClientServices.mainhandHelper.getPredictedBox(minecraftClient.player, i);
            if (ExplosionDamageCalculator.getExplosionDamage(vec3d, minecraftClient.player, box1109, Double.longBitsToDouble(4618441417868443648L), true, (BlockPos) null, (BlockPos) null) >= EntityUtil.getPlayerHealth()) {
                return true;
            }
        }
        return false;
    }

    public boolean isNearbyCrystal(Entity entity) {
        return (entity instanceof EndCrystalEntity) && minecraftClient.player.squaredDistanceTo(entity) <= Double.longBitsToDouble(4639270566145032192L);
    }
}
