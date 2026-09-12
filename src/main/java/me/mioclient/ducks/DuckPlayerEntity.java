package me.mioclient.ducks;

import me.mioclient.mode.render.NameTagsHelperMode;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import me.mioclient.mode.render.NameTagsHelperMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/DuckPlayerEntity.class */
public interface DuckPlayerEntity {
    long mio$getLastEatingTime();

    NameTagsHelperMode mio$getRole();

    void mio$setRole(NameTagsHelperMode nameTagsHelperMode);

    boolean mio$isNextToWall();

    default float get70(ItemStack itemStack, LivingEntity livingEntity) {
        return ((float) (System.currentTimeMillis() - mio$getLastEatingTime())) / ((itemStack.getMaxUseTime(livingEntity) * 50.0f) + 50.0f);
    }
}
