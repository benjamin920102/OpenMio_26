package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.NoSlow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({SweetBerryBushBlock.class})

public class MixinSweetBerryBushBlock {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    /* 26.2 port: Block.onEntityCollision renamed to BlockBehaviour.entityInside
       (verified via javap: entityInside still calls makeStuckInBlock). */
    @Redirect(method = {"entityInside"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;makeStuckInBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V"))
    private void onEntityCollisionHook(Entity entity, BlockState blockState, Vec3 vec3d) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return;

        if (noslow_lcl.isToggled() && noslow_lcl.berryBush.getValue().booleanValue()) {
            return;
        }
        entity.makeStuckInBlock(blockState, vec3d);
    }
}
