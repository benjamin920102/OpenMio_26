package me.mioclient.module.movement;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.math.Vec3dUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.RaycastUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.RaycastUtils;
import me.mioclient.util.math.Vec3dUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.exploit.Phase;
import me.mioclient.module.movement.Speed;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import me.mioclient.mixin.ducks.DuckAbstractBlock;
import me.mioclient.mixin.ducks.DuckClientPlayerEntity;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/FastFall.class */
public class FastFall extends Module {
    public static Speed speed2 = (Speed) ClientServices.moduleInjector.getModule(Speed.class);
    public static HoleSnap holeSnap = (HoleSnap) ClientServices.moduleInjector.getModule(HoleSnap.class);
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public static Phase phase = (Phase) ClientServices.moduleInjector.getModule(Phase.class);
    public Setting<FastFallMode> mode = add(new EnumSetting<>("Mode", FastFallMode.PLAIN));
    public Setting<Float> height = add(new NumberSetting<>("Height", 2.0f, 1.0f, 15.0f).setSuffix("m"));
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.0f, 5.0f, v -> this.mode.getValue() == FastFall.FastFallMode.PLAIN));
    public Setting<Boolean> stopInLiquids = add(new BooleanSetting("StopInLiquids", true));
    public Setting<Boolean> onlyHole = add(new BooleanSetting("OnlyHole", false));
    public Setting<Boolean> safeDisable = add(new BooleanSetting("SafeDisable", false));
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;
    public boolean flag5;
    public boolean flag6;
    public double val;
    public double val2;
    public final Stopwatch stopwatch;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/FastFall$FastFallMode.class */
    public enum FastFallMode implements NamedValue {
        PLAIN("Plain"),
        STRICT("Strict"),
        INSTANT("Instant");

        public final String name;

        FastFallMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public FastFall() {
        super("FastFall", "Allows you to get in holes with ease!", Category.MOVEMENT, "reversestep");
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.flag = false;
        this.flag6 = false;
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (this.safeDisable.getValue().booleanValue() && ClientServices.holeSnapOverlayHelper.isInHole() && this.flag6) {
            disable();
            return;
        }
        if (!shouldActivateStrictFall() || (!this.flag4 && this.onlyHole.getValue().booleanValue())) {
            releaseStrictTickRate();
            return;
        }
        if (minecraftClient.player.isOnGround()) {
            this.flag2 = true;
            ClientServices.inner.releaseTickRate(this);
            if (ClientServices.antiPhaseHelper.getGroundTicks() > 3) {
                this.stopwatch.reset();
                this.flag3 = true;
                return;
            }
            return;
        }
        if (this.stopwatch.hasElapsedMs(500L)) {
            this.flag3 = !this.flag3;
            this.stopwatch.reset();
        }
        if (minecraftClient.player.getVelocity().y >= 0.0d || !this.flag2 || !this.flag3) {
            releaseStrictTickRate();
        } else {
            ClientServices.inner.requestTickRate(this, Float.intBitsToFloat(1073741824));
            this.flag6 = true;
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        boolean z = MovementUtils.is2181() || (holeSnap.isToggled() && holeSnap.getHoleSnapData_2129() != null);
        if ((this.flag4 || !this.onlyHole.getValue().booleanValue()) && z) {
            this.flag5 = false;
            if (!((me.mioclient.mixin.ducks.DuckClientPlayerEntity) minecraftClient.player).lastOnGround() || minecraftClient.player.getVelocity().getY() > 0.0d || !canFastFall() || isStrictMode()) {
                return;
            }
            Box withMaxY = minecraftClient.player.getBoundingBox().stretch(0.0d, -this.height.getValue().floatValue(), 0.0d).withMaxY(minecraftClient.player.getY());
            if (!minecraftClient.world.isSpaceEmpty(minecraftClient.player, withMaxY) || minecraftClient.world.containsFluid(withMaxY)) {
                Iterable<net.minecraft.util.shape.VoxelShape> blockCollisions = minecraftClient.world.getBlockCollisions(minecraftClient.player, withMaxY);
                AtomicReference atomicReference = new AtomicReference(Double.valueOf(minecraftClient.world.getBottomY()));
                blockCollisions.forEach(voxelShape -> {
                    atomicReference.set(Double.valueOf(Math.max(((Double) atomicReference.get()).doubleValue(), voxelShape.getMax(Direction.Axis.Y))));
                });
                if (((Double) atomicReference.get()).doubleValue() < minecraftClient.player.getY()) {
                    double doubleValue = ((Double) atomicReference.get()).doubleValue();
                    Vec3d velocity = minecraftClient.player.getVelocity();
                    boolean z2 = minecraftClient.world.isAir(BlockPos.ofFloored(minecraftClient.player.getX(), doubleValue, minecraftClient.player.getZ()).add(new Vec3i((int) velocity.x, -1, (int) velocity.z))) || !ClientServices.holeSnapRenderHelper.getTeleportStopwatch().hasElapsedMs(25L) || this.mode.getValue() == FastFallMode.PLAIN;
                    if (minecraftClient.player.getY() - doubleValue > Double.longBitsToDouble(4611686018427387904L) || z2) {
                        doubleValue = minecraftClient.player.getY() - Double.longBitsToDouble(4611686018427387904L);
                        this.flag5 = true;
                        float speedValue = this.mode.getValue() == FastFallMode.PLAIN ? this.speed.getValue().floatValue() : Float.intBitsToFloat(1065353216);
                        if (speedValue != 0.0f) {
                            minecraftClient.player.setVelocity(velocity.withAxis(Direction.Axis.Y, -speedValue));
                        }
                    }
                    if (!z2) {
                        minecraftClient.player.setPosition(minecraftClient.player.getX(), doubleValue, minecraftClient.player.getZ());
                    }
                    phase.resetGroundTicks();
                    this.flag6 = true;
                }
            }
        }
    }

    @Listen
    public void onMove(MoveEvent moveEvent) {
        this.val = RaycastUtils.getDistanceToCeiling();
        this.val2 = RaycastUtils.getDistanceToFloor();
        this.flag4 = ClientServices.holeSnapOverlayHelper.isHoleAt(minecraftClient.player.getBlockPos().down((int) this.val2));
        if (this.flag4 || !this.onlyHole.getValue().booleanValue()) {
            double longBitsToDouble = Double.longBitsToDouble(4599075939470750515L);
            boolean z = holeSnap.isToggled() && holeSnap.stopwatch2.hasElapsedMs(500L) && holeSnap.getHoleSnapData_2129() != null;
            if ((isStrictMode() && canFastFall() && hasGroundBelow() && minecraftClient.player.isOnGround()) || (this.flag5 && !z && hasBlockAhead())) {
                Vec3dUtils.setVectorComponents(moveEvent.getMovement(), moveEvent.getX() * longBitsToDouble, moveEvent.getY(), moveEvent.getZ() * longBitsToDouble);
            }
        }
    }

    public boolean hasGroundBelow() {
        Box boundingBox = minecraftClient.player.getBoundingBox();
        Vec3d center = boundingBox.getCenter();
        double d = boundingBox.minX;
        double d2 = boundingBox.minZ;
        double d3 = boundingBox.maxX;
        double d4 = boundingBox.maxZ;
        HashMap<Vec3d, Vec3d> hashMap = new HashMap<>();
        hashMap.put(center, new Vec3d(center.x, center.y - Double.longBitsToDouble(4607182418800017408L), center.z));
        hashMap.put(new Vec3d(d, center.y, d2), new Vec3d(d, center.y - Double.longBitsToDouble(4607182418800017408L), d2));
        hashMap.put(new Vec3d(d3, center.y, d2), new Vec3d(d3, center.y - Double.longBitsToDouble(4607182418800017408L), d2));
        hashMap.put(new Vec3d(d, center.y, d4), new Vec3d(d, center.y - Double.longBitsToDouble(4607182418800017408L), d4));
        hashMap.put(new Vec3d(d3, center.y, d4), new Vec3d(d3, center.y - Double.longBitsToDouble(4607182418800017408L), d4));
        for (Vec3d vec3d : hashMap.keySet()) {
            BlockHitResult raycast = minecraftClient.world.raycast(new RaycastContext(vec3d, (Vec3d) hashMap.get(vec3d), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, minecraftClient.player));
            if (raycast != null && raycast.getType() == HitResult.Type.BLOCK) {
                return false;
            }
        }
        if (!hasBlockAhead()) {
            return false;
        }
        BlockState blockState = minecraftClient.world.getBlockState(BlockPos.ofFloored(minecraftClient.player.getX(), minecraftClient.player.getY() - Double.longBitsToDouble(4607182418800017408L), minecraftClient.player.getZ()));
        return blockState == null || blockState.getBlock() == Blocks.AIR;
    }

    public boolean hasBlockAhead() {
        Vec3d velocity = minecraftClient.player.getVelocity();
        double d = velocity.x;
        double d2 = velocity.z;
        double signum = Math.abs(d2) > Math.abs(d) ? 0.0d : Math.signum(d);
        BlockState blockState = minecraftClient.world.getBlockState(BlockPos.ofFloored(minecraftClient.player.getX() + signum, minecraftClient.player.getY() - Double.longBitsToDouble(4607182418800017408L), minecraftClient.player.getZ() + (Math.abs(signum) > Math.abs(d2) ? 0.0d : Math.signum(d2))));
        if (blockState != null) {
            return ((me.mioclient.mixin.ducks.DuckAbstractBlock) (Object) blockState.getBlock()).isCollidable();
        }
        return true;
    }

    public boolean canFastFall() {
        if ((!speed2.isToggled() || speed2.mode.getValue() == Speed.SpeedPredicateMode.speedPredicateMode4 || speed2.mode.getValue() == Speed.SpeedPredicateMode.speedPredicateMode3) && !minecraftClient.player.isSneaking() && !minecraftClient.player.isFallFlying() && !minecraftClient.player.isInsideWall() && ((!PlayerUtils.isInLiquid(minecraftClient.player) || !this.stopInLiquids.getValue().booleanValue()) && ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(500L))) {
            if (!(minecraftClient.world.getBlockState(BlockPos.ofFloored(minecraftClient.player.getPos())).getBlock() instanceof BedBlock)) {
                return true;
            }
        }
        return false;
    }

    public boolean isStrictMode() {
        return this.mode.getValue() == FastFallMode.STRICT;
    }

    public boolean shouldActivateStrictFall() {
        return canFastFall() && isStrictMode() && this.val != 0.0d && this.val2 != 0.0d && this.val2 <= ((double) this.height.getValue().floatValue());
    }

    public void releaseStrictTickRate() {
        if (isStrictMode()) {
            this.flag2 = false;
            ClientServices.inner.releaseTickRate(this);
        }
    }
}
