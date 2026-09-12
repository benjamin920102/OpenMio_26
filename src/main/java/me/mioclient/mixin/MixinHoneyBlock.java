package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import me.mioclient.module.movement.NoSlow;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/* compiled from: 0.java */
@Mixin({HoneyBlock.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinHoneyBlock.class */
public class MixinHoneyBlock {
    private static NoSlow noslow;

    private static NoSlow getNoslow() {

        if (noslow == null && ClientServices.moduleInjector != null) {

            try { noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class); } catch (Exception e) {}

        }

        return noslow;

    }

    @Shadow
    private boolean method_23356(BlockPos blockPos, Entity entity) {
        return false;
    }

    @Redirect(method = {"onEntityCollision"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/HoneyBlock;isSliding(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Z"))
    private boolean onEntityCollisionHook(HoneyBlock honeyBlock, BlockPos blockPos, Entity entity) {
        NoSlow noslow_lcl = getNoslow();

        if (noslow_lcl == null) return false;

        if (noslow_lcl.isToggled() && noslow_lcl.honey.getValue().booleanValue()) {
            return false;
        }
        return method_23356(blockPos, entity);
    }
}
