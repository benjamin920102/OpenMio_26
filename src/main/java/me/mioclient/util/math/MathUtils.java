package me.mioclient.util.math;

import me.mioclient.api.ClientAccessor;
import me.mioclient.render.RenderUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import me.mioclient.manager.RotationManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MathUtils.class */
public class MathUtils implements ClientAccessor {
    public static double floorToDecimals(double d, int i) {
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        return BigDecimal.valueOf(d).setScale(i, RoundingMode.FLOOR).doubleValue();
    }

    public static float roundFloat(float f, int i) {
        if (i < 0) {
            throw new IllegalArgumentException();
        }
        return BigDecimal.valueOf(f).setScale(i, RoundingMode.FLOOR).floatValue();
    }

    public static float randomFloat(float f, float f2) {
        return f + (new Random().nextFloat() * (f2 - f));
    }

    public static int randomInt(int i, int i2) {
        return i + (new Random().nextInt() * (i2 - i));
    }

    public static double getLerpedValue(double d, double d2, double d3) {
        return d + ((d2 - d) * d3);
    }

    public static float getRadians(float f) {
        return f * MathConstants.val4;
    }

    public static float getDegrees(float f) {
        return f * MathConstants.val5;
    }

    public static Vec3d getRotationVector(float f, float f2) {
        float f3 = getRadians(f);
        float f4 = getRadians(-f2);
        float cos = MathHelper.cos(f4);
        float sin = MathHelper.sin(f4);
        float cos2 = MathHelper.cos(f3);
        return new Vec3d(sin * cos2, -MathHelper.sin(f3), cos * cos2);
    }

    public static double getCameraDistanceScale(Vec3d vec3d, double d) {
        return getDistanceScale(minecraftClient.gameRenderer.getCamera().getPos(), vec3d, d);
    }

    public static double getDistanceScale(Vec3d vec3d, Vec3d vec3d2, double d) {
        double distanceTo = vec3d.distanceTo(vec3d2);
        double intValue = ((Integer) minecraftClient.options.getFov().getValue()).intValue() / Double.longBitsToDouble(4637440978796412928L);
        if (intValue < Double.longBitsToDouble(4607182418800017408L)) {
            intValue = Double.longBitsToDouble(4607182418800017408L);
        }
        return distanceTo <= Double.longBitsToDouble(4621819117588971520L) / d ? Math.min(Double.longBitsToDouble(4607182418800017408L), d) * intValue : distanceTo * d * Double.longBitsToDouble(4591870180066957722L) * intValue;
    }

    public static float getYawDifference(PlayerEntity playerEntity) {
        return MathHelper.angleBetween(minecraftClient.player.getYaw(), RotationManager.getRotationsTo(playerEntity.getPos())[0]);
    }

    public static double snapToNearestBound(double d, double d2, double d3) {
        return d3 - d > d - d2 ? d2 : d3;
    }

    public static boolean isChanceHit(int i) {
        return i != 0 && new Random().nextInt(100) <= i;
    }

    public static float getWrappedDegrees(float f) {
        return (f < 0.0f ? f + Float.intBitsToFloat(1135869952) : f) % Float.intBitsToFloat(1135869952);
    }

    public static float interpolateWithTick(float f, float f2) {
        return MathHelper.lerp(RenderUtil.getPartialTick(), f, f2);
    }

    public static double interpolateWithTick(double d, double d2) {
        return MathHelper.lerp(RenderUtil.getPartialTick(), d, d2);
    }

    public static double getCameraDistanceToBlock(BlockPos blockPos) {
        return getCameraDistance(blockPos.toCenterPos());
    }

    public static double getCameraDistance(Vec3d vec3d) {
        return minecraftClient.gameRenderer.getCamera().getPos().distanceTo(vec3d);
    }
}
