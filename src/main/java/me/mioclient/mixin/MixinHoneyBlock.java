package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.NoSlow;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({HoneyBlock.class})

public class MixinHoneyBlock {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    /* 26.2 port: Block.onEntityCollision -> entityInside; HoneyBlock.isSliding ->
       isSlidingDown (bytecode-verified: entityInside invokes it). Same NoSlow
       bypass semantics. */
    @Shadow
    private boolean isSlidingDown(BlockPos blockPos, Entity entity) {
        return false;
    }

    @Redirect(method = {"entityInside"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/HoneyBlock;isSlidingDown(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean onEntityCollisionHook(HoneyBlock honeyBlock, BlockPos blockPos, Entity entity) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return false;

        if (noslow_lcl.isToggled() && noslow_lcl.honey.getValue().booleanValue()) {
            return false;
        }
        return isSlidingDown(blockPos, entity);
    }
}
