package me.mioclient.module.player.scaffold;

import me.mioclient.api.ClientAccessor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PlacementTarget.class */
public final class PlacementTarget implements ClientAccessor {
    public final Direction direction;
    public final Vec3d vec3d;
    public final VoxelShape voxelShape;

    public PlacementTarget(Direction direction, Vec3d vec3d, VoxelShape voxelShape) {
        this.direction = direction;
        this.vec3d = vec3d;
        this.voxelShape = voxelShape;
    }

    public static PlacementTarget fromCandidate(PlacementCandidate scaffoldData_2, boolean z) {
        Direction direction1462 = scaffoldData_2.getDirection();
        return new PlacementTarget(direction1462, z ? getVec3d1461(scaffoldData_2.getBlockPos().offset(direction1462)) : scaffoldData_2.getBlockPos().offset(direction1462).toCenterPos(), minecraftClient.world.getBlockState(scaffoldData_2.getBlockPos().offset(direction1462)).getCollisionShape(minecraftClient.world, BlockPos.ORIGIN));
    }

    public static Vec3d getVec3d1461(BlockPos blockPos) {
        return new Vec3d(MathHelper.clamp(minecraftClient.player.getX(), blockPos.getX(), blockPos.getX() + 1), blockPos.getY(), MathHelper.clamp(minecraftClient.player.getZ(), blockPos.getZ(), blockPos.getZ() + 1));
    }




    public Direction getDirection() {
        return this.direction;
    }

    public Vec3d getPlacementTarget() {
        return this.vec3d;
    }

    public VoxelShape getCollisionShape() {
        return this.voxelShape;
    }
}
