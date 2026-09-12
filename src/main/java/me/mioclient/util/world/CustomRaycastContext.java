package me.mioclient.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/CustomRaycastContext.class */
public final class CustomRaycastContext {
    public final Vec3d vec3d;
    public final Vec3d vec3d2;
    public final RaycastContext.ShapeType shapeType;
    public final RaycastContext.FluidHandling fluidHandling;
    public final ShapeContext shapeContext;
    public final BlockRaycastFilter holeSnapHelper;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/HoleSnapHelper_2$Inner.class */
    public static class Inner {
        public final Vec3d vec3d;
        public final Vec3d vec3d2;
        public RaycastContext.ShapeType shapeType = RaycastContext.ShapeType.COLLIDER;
        public RaycastContext.FluidHandling fluidHandling = RaycastContext.FluidHandling.NONE;
        public Entity entity = MinecraftClient.getInstance().player;
        public BlockRaycastFilter holeSnapHelper = BlockRaycastFilter.holeSnapHelper;

        public Inner(Vec3d vec3d, Vec3d vec3d2) {
            this.vec3d = vec3d;
            this.vec3d2 = vec3d2;
        }

        public Inner setShapeType(RaycastContext.ShapeType shapeType) {
            this.shapeType = shapeType;
            return this;
        }

        public Inner setFluidHandling(RaycastContext.FluidHandling fluidHandling) {
            this.fluidHandling = fluidHandling;
            return this;
        }

        public Inner setEntity(Entity entity) {
            this.entity = entity;
            return this;
        }

        public Inner setFilter(BlockRaycastFilter holeSnapHelper) {
            this.holeSnapHelper = holeSnapHelper;
            return this;
        }

        public Inner setCombinedFilter(BlockRaycastFilter... holeSnapHelperArr) {
            this.holeSnapHelper = BlockRaycastFilter.getHoleSnapHelper1678(holeSnapHelperArr);
            return this;
        }

        public CustomRaycastContext build() {
            return new CustomRaycastContext(this.vec3d, this.vec3d2, this.shapeType, this.fluidHandling, this.entity, this.holeSnapHelper);
        }
    }

    public CustomRaycastContext(Vec3d vec3d, Vec3d vec3d2, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, Entity entity, BlockRaycastFilter holeSnapHelper) {
        this.vec3d = vec3d;
        this.vec3d2 = vec3d2;
        this.shapeType = shapeType;
        this.fluidHandling = fluidHandling;
        this.shapeContext = ShapeContext.of(entity);
        this.holeSnapHelper = holeSnapHelper;
    }

    public Vec3d getStart() {
        return this.vec3d;
    }

    public Vec3d getEnd() {
        return this.vec3d2;
    }

    public RaycastContext.ShapeType getShapeType() {
        return this.shapeType;
    }

    public RaycastContext.FluidHandling getFluidHandling() {
        return this.fluidHandling;
    }

    public ShapeContext getShapeContext() {
        return this.shapeContext;
    }

    public BlockRaycastFilter getBlockFilter() {
        return this.holeSnapHelper;
    }

    public VoxelShape getBlockShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return this.shapeType.get(blockState, blockView, blockPos, this.shapeContext);
    }

    public VoxelShape getFluidShape(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
        return this.fluidHandling.handled(fluidState) ? fluidState.getShape(blockView, blockPos) : VoxelShapes.empty();
    }
}
