package me.mioclient.module.player;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockPlacer;

import java.util.concurrent.TimeUnit;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Mount;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.util.Hand;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/AutoMount.class */
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
        if (minecraftClient.player.hasVehicle()) {
            return;
        }
        if (this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS)) {
            for (Entity livingEntity : minecraftClient.world.getEntities()) {
                if (minecraftClient.player.distanceTo((Entity) livingEntity) <= this.range.getValue().doubleValue() && (!(livingEntity instanceof LivingEntity) || !((LivingEntity) livingEntity).isBaby())) {
                    if (!(livingEntity instanceof Saddleable) || ((Saddleable) livingEntity).isSaddled() || (livingEntity instanceof AbstractHorseEntity)) {
                        if ((livingEntity instanceof Mount) || (livingEntity instanceof VehicleEntity)) {
                            BlockPlacer.interactWithEntity((Entity) livingEntity, Hand.MAIN_HAND);
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
