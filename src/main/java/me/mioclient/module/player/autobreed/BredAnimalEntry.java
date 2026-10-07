package me.mioclient.module.player.autobreed;

import net.minecraft.world.entity.animal.Animal;

public final class BredAnimalEntry {
    public final Animal animalEntity;
    public int num = 0;

    public BredAnimalEntry(Animal animalEntity) {
        this.animalEntity = animalEntity;
    }

    public void runCallback() {
        this.num++;
    }

    public Animal getAnimalEntity() {
        return this.animalEntity;
    }

    public int get1459() {
        return this.num;
    }
}