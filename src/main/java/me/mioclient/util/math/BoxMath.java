package me.mioclient.util.math;

import me.mioclient.api.ClientAccessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.mioclient.command.FakePlayerCommand;
import me.mioclient.api.Setting;
import me.mioclient.ducks.DuckLivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

public class BoxMath implements ClientAccessor {
    public static double val = Double.longBitsToDouble(4589168020290535424L);

    public static Vec3 getBottomCenter(AABB box) {
        return new Vec3(Mth.lerp(MathConstants.val2, box.minX, box.maxX), box.minY, Mth.lerp(MathConstants.val2, box.minZ, box.maxZ));
    }

    public static Vec3 getCenter(AABB box) {
        return getBottomCenter(box).add(0.0d, box.getYsize() * Double.longBitsToDouble(4598175219545276416L), 0.0d);
    }

    public static Vec3 getTopCenter(AABB box) {
        return new Vec3(Mth.lerp(MathConstants.val2, box.minX, box.maxX), box.maxY, Mth.lerp(MathConstants.val2, box.minZ, box.maxZ));
    }

    public static AABB getBoxAround(AABB box, Vec3 vec3d) {
        double lengthX = box.getXsize() * MathConstants.val2;
        double lengthY = box.getYsize() * MathConstants.val2;
        double lengthZ = box.getZsize() * MathConstants.val2;
        return new AABB(vec3d.x - lengthX, vec3d.y - lengthY, vec3d.z - lengthZ, vec3d.x + lengthX, vec3d.y + lengthY, vec3d.z + lengthZ);
    }

    public static AABB getBoxAroundBlock(AABB box, BlockPos blockPos) {
        return getBoxAround(box, new Vec3(blockPos.getX() + MathConstants.val2, blockPos.getY() + (box.getYsize() * MathConstants.val2), blockPos.getZ() + MathConstants.val2));
    }

    public static AABB getBlockOutlineBox(BlockPos blockPos) {
        BlockState blockState = minecraftClient.level.getBlockState(blockPos);
        if (blockState.isAir() || blockState.getBlock() == Blocks.AIR) {
            return new AABB(blockPos);
        }
        VoxelShape outlineShape = blockState.getShape(minecraftClient.level, blockPos);
        if (outlineShape.isEmpty()) {
            outlineShape = Shapes.box(0.0d, 0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L));
        }
        return outlineShape.bounds().move(blockPos);
    }

    public static List<Vec3> getCornersSortedByDistance(AABB box, double d) {
        ArrayList<Vec3> arrayList = new ArrayList<Vec3>(List.of(new Vec3(box.minX, d, box.minZ), new Vec3(box.maxX, d, box.minZ), new Vec3(box.minX, d, box.maxZ), new Vec3(box.maxX, d, box.maxZ)));
        arrayList.sort(Comparator.comparing(vec3d -> {
            return Double.valueOf(minecraftClient.player.getEyePosition().distanceToSqr(vec3d));
        }));
        return arrayList;
    }

    public static boolean isFullBlockBox(AABB box) {
        return box.getZsize() == Double.longBitsToDouble(4607182418800017408L) && box.getYsize() == Double.longBitsToDouble(4607182418800017408L) && box.getXsize() == Double.longBitsToDouble(4607182418800017408L);
    }

    public static boolean isFullBlockShape(VoxelShape voxelShape) {
        if (voxelShape.isEmpty()) {
            return false;
        }
        return isFullBlockBox(voxelShape.bounds());
    }

    public static int getSnapDivisions(Player playerEntity, Setting<Integer> setting) {
        if (!setting.isHoleSnapConfigured()) {
            return setting.getValue().intValue();
        }
        double hypot = Math.hypot(playerEntity.getX() - playerEntity.xo, playerEntity.getZ() - playerEntity.zo) * Double.longBitsToDouble(4626322717216342016L) * Double.longBitsToDouble(4615288898129284301L);
        if (hypot < Double.longBitsToDouble(4613937818241073152L)) {
            return 0;
        }
        if (hypot <= Double.longBitsToDouble(4621819117588971520L)) {
            return 1;
        }
        if (hypot <= Double.longBitsToDouble(4626322717216342016L)) {
            return 3;
        }
        return hypot <= Double.longBitsToDouble(4629137466983448576L) ? 4 : 5;
    }

    public static Vec3 getClosestPointInBox(Vec3 vec3d, AABB box) {
        return new Vec3(Mth.clamp(vec3d.x, box.minX, box.maxX), Mth.clamp(vec3d.y, box.minY, box.maxY), Mth.clamp(vec3d.z, box.minZ, box.maxZ));
    }

    public static AABB getInterpolatedBox(Entity entity, float f) {
        return entity.getBoundingBox().move(Mth.lerp(f, (float) entity.xo, (float) entity.getX()) - entity.getX(), Mth.lerp(f, (float) entity.yo, (float) entity.getY()) - entity.getY(), Mth.lerp(f, (float) entity.zo, (float) entity.getZ()) - entity.getZ());
    }

    public static AABB getServerBox(LivingEntity livingEntity) {
        if (livingEntity instanceof FakePlayerCommand.RemotePlayer) {
            return livingEntity.getBoundingBox();
        }
        AABB boundingBox = livingEntity.getBoundingBox();
        DuckLivingEntity duckLivingEntity = (DuckLivingEntity) livingEntity;
        Vec3 vec3d = new Vec3(duckLivingEntity.mio$getServerX(), duckLivingEntity.mio$getServerY(), duckLivingEntity.mio$getServerZ());
        if (vec3d.lengthSqr() == 0.0d) {
            return livingEntity.getBoundingBox();
        }
        double lengthX = boundingBox.getXsize() / Double.longBitsToDouble(4611686018427387904L);
        double lengthZ = boundingBox.getZsize() / Double.longBitsToDouble(4611686018427387904L);
        return new AABB(-lengthX, 0.0d, -lengthZ, lengthX, boundingBox.getYsize(), lengthZ).move(vec3d);
    }

    public static boolean hasSignificantIntersection(AABB box, AABB box2) {
        AABB intersection = box.intersect(box2);
        return ((double) ((float) Math.min(intersection.getXsize(), Math.min(intersection.getYsize(), intersection.getZsize())))) > Double.longBitsToDouble(4502148214488346440L);
    }
}
