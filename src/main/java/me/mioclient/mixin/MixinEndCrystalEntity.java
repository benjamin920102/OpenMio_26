package me.mioclient.mixin;

import me.mioclient.ducks.DuckEndCrystalEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EndCrystal.class})

public class MixinEndCrystalEntity implements DuckEndCrystalEntity {

    @Unique
    private long spawnTime;

    @Unique
    private boolean mioAttacked;

    @Inject(method = {"<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V"}, at = {@At("RETURN")})
    private void init(EntityType<?> entityType, Level world, CallbackInfo callbackInfo) {
        this.spawnTime = System.currentTimeMillis();
        this.mioAttacked = false;
    }

    @Override // me.mioclient.ducks.DuckEndCrystalEntity
    public long getSpawnTime() {
        return this.spawnTime;
    }

    @Override // me.mioclient.ducks.DuckEndCrystalEntity
    public boolean isMioAttacked() {
        return this.mioAttacked;
    }

    @Override // me.mioclient.ducks.DuckEndCrystalEntity
    public void setMioAttacked(boolean z) {
        this.mioAttacked = z;
    }
}
