package me.mioclient.module.player;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import java.util.concurrent.TimeUnit;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.InteractionHand;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

public final class AutoMount extends Module {
    public Setting<Double> range = add(new NumberSetting<>("Range", 3.0, 0.1, 6.0));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 1.0f, 0.1f, 5.0f).setSuffix("s"));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true));
    public final Stopwatch stopwatch;

    public AutoMount() {
        super("AutoMount", "Mounts rideable entities automatically.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        if (minecraftClient.player.isPassenger()) {
            return;
        }
        if (this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS)) {
            for (Entity livingEntity : minecraftClient.level.entitiesForRendering()) {
                if (minecraftClient.player.distanceTo((Entity) livingEntity) <= this.range.getValue().doubleValue() && (!(livingEntity instanceof LivingEntity) || !((LivingEntity) livingEntity).isBaby())) {
                    boolean saddled = !(livingEntity instanceof LivingEntity living) || !living.getItemBySlot(EquipmentSlot.SADDLE).isEmpty() || livingEntity instanceof AbstractHorse;
                    if (saddled) {
                        if ((livingEntity instanceof AbstractHorse) || (livingEntity instanceof VehicleEntity)) {
                            BlockPlacer.interactWithEntity((Entity) livingEntity, InteractionHand.MAIN_HAND);
                            if (this.rotate.getValue().booleanValue()) {
                                ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(((Entity) livingEntity).getBoundingBox().getCenter()), 5);
                            }
                            this.stopwatch.reset();
                            return;
                        }
                    }
                }
            }
        }
    }
}