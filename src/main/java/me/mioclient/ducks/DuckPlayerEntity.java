package me.mioclient.ducks;

import me.mioclient.mode.render.NameTagsHelperMode;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface DuckPlayerEntity {
    long mio$getLastEatingTime();

    NameTagsHelperMode mio$getRole();

    void mio$setRole(NameTagsHelperMode nameTagsHelperMode);

    boolean mio$isNextToWall();

    default float get70(ItemStack itemStack, LivingEntity livingEntity) {
        return ((float) (System.currentTimeMillis() - mio$getLastEatingTime())) / ((itemStack.getUseDuration(livingEntity) * 50.0f) + 50.0f);
    }
}
