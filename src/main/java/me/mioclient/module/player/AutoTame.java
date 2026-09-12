package me.mioclient.module.player;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.NamedValue;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.inventory.InventoryUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/AutoTame.class */
public class AutoTame extends Module {
    public Setting<AutoTameMode> action = add(new EnumSetting<>("Action", AutoTameMode.autoTameMode));
    public Setting<Float> range = add(new NumberSetting<>("Range", 3.0f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Integer> frequency = add(new NumberSetting<>("Frequency", 1, 1, 8));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 0.1f, 0.1f, 5.0f).setSuffix("s"));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", false).setDependent());
    public Setting<Boolean> dogs = add(new BooleanSetting("Dogs", true, v -> this.targets.isShown()));
    public Setting<Boolean> cats = add(new BooleanSetting("Cats", true, v -> this.targets.isShown()));
    public Setting<Boolean> parrots = add(new BooleanSetting("Parrots", true, v -> this.targets.isShown()));
    public final Stopwatch stopwatch;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/player/AutoTame$AutoTameMode.class */
    private static enum AutoTameMode implements NamedValue {
        autoTameMode("Tame") {
            @Override
            public Vec3d getActionTarget(TameableEntity tameableEntity) {
                int i;
                if (tameableEntity.getOwner() != null || (i = InventoryUtils.findInHotbar(AutoTame.getPredicate1845(tameableEntity))) == -1) {
                    return null;
                }
                InventoryUtils.selectSlot(i);
                BlockPlacer.interactWithEntity((Entity) tameableEntity, Hand.MAIN_HAND);
                return tameableEntity.getBoundingBox().getCenter();
            }
        },
        autoTameMode2("Sit") {
            @Override
            public Vec3d getActionTarget(TameableEntity tameableEntity) {
                if (tameableEntity.isInSittingPose() || tameableEntity.isSitting() || ClientAccessor.minecraftClient.player != tameableEntity.getOwner() || tameableEntity.isBreedingItem(ClientAccessor.minecraftClient.player.getMainHandStack())) {
                    return null;
                }
                BlockPlacer.interactWithEntity((Entity) tameableEntity, Hand.MAIN_HAND);
                return tameableEntity.getBoundingBox().getCenter();
            }
        },
        autoTameMode3("Stand") {
            @Override
            public Vec3d getActionTarget(TameableEntity tameableEntity) {
                if ((!tameableEntity.isInSittingPose() && !tameableEntity.isSitting()) || ClientAccessor.minecraftClient.player != tameableEntity.getOwner() || tameableEntity.isBreedingItem(ClientAccessor.minecraftClient.player.getMainHandStack())) {
                    return null;
                }
                BlockPlacer.interactWithEntity((Entity) tameableEntity, Hand.MAIN_HAND);
                return tameableEntity.getBoundingBox().getCenter();
            }
        };

        public final String name;

        AutoTameMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public Vec3d getActionTarget(TameableEntity tameableEntity) {
            return null;
        }
    }

    public AutoTame() {
        super("AutoTame", "Automatically interacts with tame-able entities.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        Vec3d vec3d650;
        if (motionEvent.getPhase() == KeyPearlMode.Post) {
            return;
        }
        if (this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS)) {
            int i = 0;
            for (Entity entity : minecraftClient.world.getEntities()) {
                if (entity instanceof TameableEntity) {
                    TameableEntity tameableEntity = (TameableEntity) entity;
                    if (isAllowedAnimal(tameableEntity) && entity.distanceTo(minecraftClient.player) <= this.range.getValue().floatValue() && (vec3d650 = this.action.getValue().getActionTarget(tameableEntity)) != null) {
                        i++;
                        if (this.rotate.getValue().booleanValue()) {
                            motionEvent.setRotation(RotationManager.getRotationsTo(vec3d650));
                        }
                        if (i >= this.frequency.getValue().intValue()) {
                            break;
                        }
                    }
                }
            }
            this.stopwatch.reset();
        }
    }

    public boolean isAllowedAnimal(TameableEntity tameableEntity) {
        return ((tameableEntity instanceof CatEntity) && this.cats.getValue().booleanValue()) || ((tameableEntity instanceof WolfEntity) && this.dogs.getValue().booleanValue()) || ((tameableEntity instanceof ParrotEntity) && this.parrots.getValue().booleanValue());
    }

    public static Predicate<ItemStack> getPredicate1845(TameableEntity tameableEntity) {
        if (tameableEntity instanceof WolfEntity) {
            return itemStack -> {
                return itemStack.isOf(Items.BONE);
            };
        }
        if (tameableEntity instanceof ParrotEntity) {
            return itemStack2 -> {
                return List.of(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS).contains(itemStack2.getItem());
            };
        }
        Objects.requireNonNull(tameableEntity);
        return tameableEntity::isBreedingItem;
    }
}
