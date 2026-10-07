package me.mioclient.mixin.lithium;

import me.mioclient.api.ClientAccessor;
import me.mioclient.event.events.world.BlockCollisionShapeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/* Hook for Lithium 0.25.x (mc26.2-0.25.3-fabric, compileOnly dep). @Pseudo: skipped silently if Lithium absent. */
@Pseudo
@Mixin(targets = {"net.caffeinemc.mods.lithium.common.entity.movement.ChunkAwareBlockCollisionSweeper"}, remap = false)
public class MixinChunkAwareBlockCollisionSweeper {
    @Redirect(method = {"computeNext"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape onComputeNextCollisionBox(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext shapeContext) {
        VoxelShape collisionShape = blockState.getCollisionShape(blockView, blockPos, shapeContext);
        if (blockView != Minecraft.getInstance().level) {
            return collisionShape;
        }
        BlockCollisionShapeEvent voxelShapeEvent = (BlockCollisionShapeEvent) ClientAccessor.eventBus.fireEvent(BlockCollisionShapeEvent.create(collisionShape, blockPos, blockState));
        return voxelShapeEvent.isCancelled() ? Shapes.empty() : voxelShapeEvent.getShape();
    }
}
