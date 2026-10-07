package me.mioclient.ducks;

import net.minecraft.world.phys.Vec3;

/* Pure duck interface (no @Mixin — implemented by MixinLivingEntity).
   Lives outside the mixin package so game code may reference it directly.
   26.2 notes: Yarn itemUseTimeLeft is MojMap useItemRemaining;
   server pos comes from the entity position codec. */
public interface DuckLivingEntity {
    void setItemUseTimeLeft(int i);

    double mio$getServerX();

    double mio$getServerY();

    double mio$getServerZ();

    Vec3 mio$getServerPos();

    int getSwingDuration();
}
