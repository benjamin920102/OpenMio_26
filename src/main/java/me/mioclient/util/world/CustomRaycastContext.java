package me.mioclient.util.world;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;

public final class CustomRaycastContext {
    public final Vec3 vec3d;
    public final Vec3 vec3d2;
    public final ClipContext.Block shapeType;
    public final ClipContext.Fluid fluidHandling;
    public final CollisionContext shapeContext;
    public final BlockRaycastFilter holeSnapHelper;

    
    public static class Inner {
        public final Vec3 vec3d;
        public final Vec3 vec3d2;
        public ClipContext.Block shapeType = ClipContext.Block.COLLIDER;
        public ClipContext.Fluid fluidHandling = ClipContext.Fluid.NONE;
        public Entity entity = Minecraft.getInstance().player;
        public BlockRaycastFilter holeSnapHelper = BlockRaycastFilter.holeSnapHelper;

        public Inner(Vec3 vec3d, Vec3 vec3d2) {
            this.vec3d = vec3d;
            this.vec3d2 = vec3d2;
        }

        public Inner setShapeType(ClipContext.Block shapeType) {
            this.shapeType = shapeType;
            return this;
        }

        public Inner setFluidHandling(ClipContext.Fluid fluidHandling) {
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

    public CustomRaycastContext(Vec3 vec3d, Vec3 vec3d2, ClipContext.Block shapeType, ClipContext.Fluid fluidHandling, Entity entity, BlockRaycastFilter holeSnapHelper) {
        this.vec3d = vec3d;
        this.vec3d2 = vec3d2;
        this.shapeType = shapeType;
        this.fluidHandling = fluidHandling;
        this.shapeContext = CollisionContext.of(entity);
        this.holeSnapHelper = holeSnapHelper;
    }

    public Vec3 getStart() {
        return this.vec3d;
    }

    public Vec3 getEnd() {
        return this.vec3d2;
    }

    public ClipContext.Block getShapeType() {
        return this.shapeType;
    }

    public ClipContext.Fluid getFluidHandling() {
        return this.fluidHandling;
    }

    public CollisionContext getShapeContext() {
        return this.shapeContext;
    }

    public BlockRaycastFilter getBlockFilter() {
        return this.holeSnapHelper;
    }

    public VoxelShape getBlockShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos) {
        return this.shapeType.get(blockState, blockView, blockPos, this.shapeContext);
    }

    public VoxelShape getFluidShape(FluidState fluidState, BlockGetter blockView, BlockPos blockPos) {
        return this.fluidHandling.canPick(fluidState) ? fluidState.getShape(blockView, blockPos) : Shapes.empty();
    }
}
