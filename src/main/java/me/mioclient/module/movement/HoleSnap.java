package me.mioclient.module.movement;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.math.Tuple;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockRaycastFilter;
import me.mioclient.util.world.CustomRaycastContext;
import me.mioclient.util.world.HoleData;
import me.mioclient.util.world.RaycastUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.HoleData;
import me.mioclient.module.movement.holesnap.WarpChargeTracker;
import me.mioclient.util.world.BlockRaycastFilter;
import me.mioclient.util.world.CustomRaycastContext;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.math.Tuple;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.RaycastUtils;

import java.util.concurrent.TimeUnit;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/HoleSnap.class */
public class HoleSnap extends Module {
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", true));
    public Setting<Boolean> directional = add(new BooleanSetting("Directional", false));
    public Setting<Boolean> shift = add(new BooleanSetting("Shift", false));
    public Setting<Boolean> pauseStep = add(new BooleanSetting("PauseStep", false));
    public Setting<Float> height = add(new NumberSetting<>("Height", 2.0f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Float> timeout = add(new NumberSetting<>("Timeout", 0.5f, 0.0f, 2.0f).setSuffix("s"));
    public Setting<Float> range = add(new NumberSetting<>("Range", 1.0f, 0.1f, 3.0f).setSuffix("m"));
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.5f, 3.0f, v -> !RotationManager.is724()));
    public Setting<Float> pitch = add(new NumberSetting<>("Pitch", 0.0f, 0.0f, 90.0f).setHoleSnapConfig("None", HoleSnapMode.MIN));
    public final WarpChargeTracker holeSnapSearchHelper4_7;
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public boolean flag;
    public boolean flag2;

    public HoleSnap() {
        super("HoleSnap", "Pushes you into holes as you go past them.", Category.MOVEMENT, "holetp", "anchor");
        this.holeSnapSearchHelper4_7 = new WarpChargeTracker(this);
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        this.flag2 = false;
        this.pitch.setHoleSnapConfig("None", HoleSnapMode.MIN);
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.stopwatch2.setTime(-1L);
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        boolean isInHole = ClientServices.holeSnapOverlayHelper.isInHole();
        if (minecraftClient.player.isOnGround() && isInHole != this.flag) {
            this.flag = isInHole;
            this.stopwatch.reset();
        }
        if (!shouldPause()) {
            if (this.stopwatch2.hasElapsed(this.timeout.getValue().floatValue(), TimeUnit.SECONDS)) {
                Tuple<Vec3d, Double> holeSnapData_2129 = getHoleSnapData_2129();
                if (holeSnapData_2129 == null) {
                    this.flag2 = false;
                    ClientServices.inner.releaseTickRate(this);
                    return;
                }
                if (RotationManager.is724()) {
                    ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(holeSnapData_2129.getLeft()), 1337, true);
                    this.flag2 = true;
                }
                WarpChargeTracker.flag = this.holeSnapSearchHelper4_7.tryConsumeCharge();
                if (WarpChargeTracker.flag) {
                    return;
                }
                ClientServices.inner.releaseTickRate(this);
                return;
            }
        }
        ClientServices.inner.releaseTickRate(this);
    }

    @Listen
    public void onMove(MoveEvent moveEvent) {
        if (shouldPause()) {
            return;
        }
        if (ClientServices.holeSnapOverlayHelper.isHoleAt(PlayerUtils.getRoundedBlockPos())) {
            if (this.autoDisable.getValue().booleanValue()) {
                disable();
                this.flag2 = false;
                return;
            }
            this.stopwatch2.reset();
        }
        if (!this.stopwatch2.hasElapsed(this.timeout.getValue().floatValue(), TimeUnit.SECONDS) || RotationManager.is724()) {
            return;
        }
        Tuple<Vec3d, Double> holeSnapData_2129 = getHoleSnapData_2129();
        if (holeSnapData_2129 == null) {
            this.flag2 = false;
            return;
        }
        Vec3d multiply = holeSnapData_2129.getLeft().subtract(minecraftClient.player.getPos()).normalize().multiply(Math.min(MovementUtils.getBaseMoveSpeed(true) * this.speed.getValue().floatValue(), holeSnapData_2129.getRight().doubleValue()));
        moveEvent.setHorizontal(multiply.x, multiply.z);
    }

    @Listen(get219= RenderConstants.num4)
    public void onInputTick(InputTickEvent tickEvent_2) {
        if (!this.flag2 || shouldPause()) {
            return;
        }
        if (this.stopwatch2.hasElapsed(this.timeout.getValue().floatValue(), TimeUnit.SECONDS)) {
            tickEvent_2.getInput().movementForward = tickEvent_2.hasSpeed() ? tickEvent_2.getSpeed() : Float.intBitsToFloat(1065353216);
            tickEvent_2.getInput().pressingForward = true;
        }
    }

    @Listen
    public void onRotation(RotationEvent event_3) {
        Tuple<Vec3d, Double> holeSnapData_2129;
        if (!this.flag2 || shouldPause()) {
            return;
        }
        if (!this.stopwatch2.hasElapsed(this.timeout.getValue().floatValue(), TimeUnit.SECONDS) || (holeSnapData_2129 = getHoleSnapData_2129()) == null) {
            return;
        }
        float[] floatArray2484 = RotationManager.getRotationsTo(holeSnapData_2129.getLeft());
        event_3.setYaw(floatArray2484[0]);
        event_3.setPitch(floatArray2484[1]);
        event_3.cancel();
    }

    public Tuple<Vec3d, Double> getHoleSnapData_2129() {
        double longBitsToDouble = Double.longBitsToDouble(4666722622711529472L);
        Vec3d vec3d = null;
        Vec3d pos = minecraftClient.player.getPos();
        for (HoleData holeSnapData : ClientServices.holeSnapOverlayHelper.getHoles()) {
            Vec3d center = holeSnapData.getBox().getCenter();
            Vec3d withAxis = center.withAxis(Direction.Axis.Y, MathHelper.clamp(pos.getY(), center.y, (center.y + this.height.getValue().floatValue()) - Double.longBitsToDouble(4602678819172646912L)));
            if (holeSnapData.getBlockPos().getY() < pos.getY() && !holeSnapData.isTrapped()) {
                double distanceTo = pos.distanceTo(withAxis);
                if (!this.directional.getValue().booleanValue() || MovementUtils.isLocalPlayerMovingToward(withAxis) || distanceTo <= Double.longBitsToDouble(4587366580439587226L)) {
                    if (distanceTo <= this.range.getValue().floatValue()) {
                        if (minecraftClient.world.isSpaceEmpty(holeSnapData.getBox().withMaxY(minecraftClient.player.getBoundingBox().maxY)) && !isBlockedRay(pos, withAxis)) {
                            if (!isBlockedRay(pos.add(0.0d, Double.longBitsToDouble(4610785298287165440L), 0.0d), withAxis.add(0.0d, Double.longBitsToDouble(4610785298287165440L), 0.0d)) && distanceTo < longBitsToDouble) {
                                longBitsToDouble = distanceTo;
                                vec3d = withAxis;
                            }
                        }
                    }
                }
            }
        }
        if (longBitsToDouble > this.range.getValue().floatValue()) {
            return null;
        }
        return new Tuple<>(vec3d, Double.valueOf(longBitsToDouble));
    }

    public boolean shouldPause() {
        if (minecraftClient.player.isInSneakingPose()) {
            return true;
        }
        return (minecraftClient.player.getPitch() < this.pitch.getValue().floatValue() && this.pitch.getValue().floatValue() != 0.0f) || !ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(250L) || minecraftClient.player.isSpectator() || minecraftClient.player.isFallFlying();
    }

    public boolean isBlockedRay(Vec3d vec3d, Vec3d vec3d2) {
        return RaycastUtils.performCustomBlockRaycast(new CustomRaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, minecraftClient.player, BlockRaycastFilter.holeSnapHelper)).getType() != HitResult.Type.MISS;
    }

    public boolean shouldPauseStep() {
        return !this.stopwatch.hasElapsedMs(400L) && ClientServices.holeSnapOverlayHelper.isInHole() && this.pauseStep.getValue().booleanValue();
    }
}
