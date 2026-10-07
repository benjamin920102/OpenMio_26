package me.mioclient.module.player.scaffold;

import me.mioclient.api.ClientAccessor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PlacementTarget implements ClientAccessor {
    public final Direction direction;
    public final Vec3 vec3d;
    public final VoxelShape voxelShape;

    public PlacementTarget(Direction direction, Vec3 vec3d, VoxelShape voxelShape) {
        this.direction = direction;
        this.vec3d = vec3d;
        this.voxelShape = voxelShape;
    }

    public static PlacementTarget fromCandidate(PlacementCandidate scaffoldData_2, boolean z) {
        Direction direction1462 = scaffoldData_2.getDirection();
        return new PlacementTarget(direction1462, z ? getVec3d1461(scaffoldData_2.getBlockPos().relative(direction1462)) : Vec3.atCenterOf(scaffoldData_2.getBlockPos().relative(direction1462)), minecraftClient.level.getBlockState(scaffoldData_2.getBlockPos().relative(direction1462)).getCollisionShape(minecraftClient.level, BlockPos.ZERO));
    }

    public static Vec3 getVec3d1461(BlockPos blockPos) {
        return new Vec3(Mth.clamp(minecraftClient.player.getX(), blockPos.getX(), blockPos.getX() + 1), blockPos.getY(), Mth.clamp(minecraftClient.player.getZ(), blockPos.getZ(), blockPos.getZ() + 1));
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Vec3 getPlacementTarget() {
        return this.vec3d;
    }

    public VoxelShape getCollisionShape() {
        return this.voxelShape;
    }
}