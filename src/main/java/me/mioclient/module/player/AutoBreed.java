package me.mioclient.module.player;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.module.player.autobreed.BredAnimalEntry;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.inventory.InventoryUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/AutoBreed.class */
public class AutoBreed extends Module {
    public Setting<Float> range = add(new NumberSetting<>("Range", 3.0f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Integer> frequency = add(new NumberSetting<>("Frequency", 1, 1, 8));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 0.0f, 0.0f, 5.0f).setSuffix("s"));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", false).setDependent());
    public Setting<Boolean> dogs = add(new BooleanSetting("Dogs", true, v -> this.targets.isShown()));
    public Setting<Boolean> cats = add(new BooleanSetting("Cats", true, v -> this.targets.isShown()));
    public Setting<Boolean> parrots = add(new BooleanSetting("Parrots", true, v -> this.targets.isShown()));
    public Setting<Boolean> donkeys = add(new BooleanSetting("Donkeys", true, v -> this.targets.isShown()));
    public Setting<Boolean> horses = add(new BooleanSetting("Horses", true, v -> this.targets.isShown()));
    public Setting<Boolean> others = add(new BooleanSetting("Others", true, v -> this.targets.isShown()));
    public final Stopwatch stopwatch;
    public final List<BredAnimalEntry> list;

    public AutoBreed() {
        super("AutoBreed", "Breeds animals in between each other automatically.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
        this.list = Collections.synchronizedList(new ArrayList());
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.list.clear();
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        this.list.forEach((v0) -> {
            v0.runCallback();
        });
        this.list.removeIf(autoBreedHelper -> {
            return autoBreedHelper.get1459() >= 6000;
        });
        if (this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS)) {
            int i = 0;
            for (Entity animalEntity : minecraftClient.world.getEntities()) {
                if (animalEntity instanceof AnimalEntity) {
                    AnimalEntity animalEntity2 = (AnimalEntity) animalEntity;
                    Iterator<BredAnimalEntry> it = this.list.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            if (isAllowedAnimal(animalEntity2) && animalEntity2.distanceTo(minecraftClient.player) <= this.range.getValue().floatValue()) {
                                int i2 = InventoryUtils.findInHotbar(getBreedingItemPredicate(animalEntity2));
                                if (i2 == -1) {
                                    return;
                                }
                                Vec3d center = animalEntity2.getBoundingBox().getCenter();
                                if (center != null) {
                                    InventoryUtils.selectSlot(i2);
                                    BlockPlacer.interactWithEntity((Entity) animalEntity2, Hand.MAIN_HAND);
                                    this.list.add(new BredAnimalEntry(animalEntity2));
                                    i++;
                                    if (this.rotate.getValue().booleanValue()) {
                                        ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(center), 5);
                                    }
                                    if (i >= this.frequency.getValue().intValue()) {
                                        break;
                                    }
                                } else {
                                    continue;
                                }
                            }
                        } else {
                            if (animalEntity2 == it.next().getAnimalEntity()) {
                                break;
                            }
                        }
                    }
                }
            }
            this.stopwatch.reset();
        }
    }

    public static Predicate<ItemStack> getBreedingItemPredicate(AnimalEntity animalEntity) {
        if (animalEntity instanceof ParrotEntity) {
            return itemStack -> {
                return List.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS).contains(itemStack.getItem());
            };
        }
        Objects.requireNonNull(animalEntity);
        return animalEntity::isBreedingItem;
    }

    public boolean isAllowedAnimal(AnimalEntity animalEntity) {
        if (animalEntity.isBaby()) {
            return false;
        }
        Objects.requireNonNull(animalEntity);
        int typeSwitchIndex = animalEntity instanceof WolfEntity ? 0
                : animalEntity instanceof CatEntity ? 1
                : animalEntity instanceof ParrotEntity ? 2
                : animalEntity instanceof DonkeyEntity ? 3
                : animalEntity instanceof HorseEntity ? 4
                : -1;
        switch (typeSwitchIndex) {
            case 0:
                return this.dogs.getValue().booleanValue();
            case 1:
                return this.cats.getValue().booleanValue();
            case 2:
                return this.parrots.getValue().booleanValue();
            case 3:
                return this.donkeys.getValue().booleanValue();
            case 4:
                return this.horses.getValue().booleanValue();
            default:
                return animalEntity.getBreedingAge() != 0 && this.others.getValue().booleanValue();
        }
    }
}
