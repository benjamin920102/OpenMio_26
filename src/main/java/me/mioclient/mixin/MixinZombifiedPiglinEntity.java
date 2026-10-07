package me.mioclient.mixin;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.ducks.DuckZombifiedPiglinEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ZombifiedPiglin.class})

public abstract class MixinZombifiedPiglinEntity extends Mob implements DuckZombifiedPiglinEntity {

    @Unique
    private Stopwatch stopwatch;

    protected MixinZombifiedPiglinEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = {"<init>"}, at = {@At("TAIL")})
    private void initHook(EntityType<?> entityType, Level world, CallbackInfo callbackInfo) {
        this.stopwatch = new Stopwatch();
    }

    @Override
    public boolean mio$isAttacking() {
        if (getTarget() != null) {
            this.stopwatch.reset();
        }
        return !this.stopwatch.hasElapsedMs(1000L);
    }
}
