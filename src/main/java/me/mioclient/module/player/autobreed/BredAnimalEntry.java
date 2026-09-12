package me.mioclient.module.player.autobreed;

import net.minecraft.entity.passive.AnimalEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BredAnimalEntry.class */
public final class BredAnimalEntry {
    public final AnimalEntity animalEntity;
    public int num = 0;

    public BredAnimalEntry(AnimalEntity animalEntity) {
        this.animalEntity = animalEntity;
    }

    public void runCallback() {
        this.num++;
    }

    public AnimalEntity getAnimalEntity() {
        return this.animalEntity;
    }

    public int get1459() {
        return this.num;
    }
}
