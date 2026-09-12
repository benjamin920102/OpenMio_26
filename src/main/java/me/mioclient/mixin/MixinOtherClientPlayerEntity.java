package me.mioclient.mixin;

import com.mojang.authlib.GameProfile;
import me.mioclient.command.FakePlayerCommand;
import me.mioclient.ducks.DuckOtherClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({OtherClientPlayerEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinOtherClientPlayerEntity.class */
public abstract class MixinOtherClientPlayerEntity extends PlayerEntity implements DuckOtherClientPlayerEntity {
    public MixinOtherClientPlayerEntity(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Inject(method = {"damage"}, at = {@At("HEAD")}, cancellable = true)
    public void damageHook(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (((Object) this) instanceof FakePlayerCommand.OtherClientPlayerEntity) {
            callbackInfoReturnable.setReturnValue(Boolean.valueOf(damageSuper(damageSource, f)));
        }
    }

    @Override // me.mioclient.mixin.ducks.DamageOverrider
    public boolean damageSuper(DamageSource damageSource, float f) {
        return super.damage(damageSource, f);
    }
}
