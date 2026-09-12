package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.CrystalRotationMode;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.network.PacketSender;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.mode.combat.CrystalRotationMode;
import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.player.AttackHookPostEvent;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.SprintUpdateEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.player.RotationLock;
import me.mioclient.module.player.Scaffold;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import me.mioclient.mixin.ducks.DuckEntity;
import me.mioclient.manager.RotationManager;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/Sprint.class */
public class Sprint extends Module {
    public static final Scaffold scaffold = (Scaffold) ClientServices.moduleInjector.getModule(Scaffold.class);
    public static final ElytraFly efly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class);
    public static final LongJump longJump = (LongJump) ClientServices.moduleInjector.getModule(LongJump.class);
    public static final FastFall fastFall = (FastFall) ClientServices.moduleInjector.getModule(FastFall.class);
    public static final Speed speed = (Speed) ClientServices.moduleInjector.getModule(Speed.class);
    public static final FastWeb fastWeb = (FastWeb) ClientServices.moduleInjector.getModule(FastWeb.class);
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public static RotationLock rotationLock = (RotationLock) ClientServices.moduleInjector.getModule(RotationLock.class);
    public Setting<SprintPredicateMode> mode = add(new EnumSetting<>("Mode", SprintPredicateMode.RAGE));
    public Setting<Boolean> forceSprint = add(new BooleanSetting("ForceSprint", true, v -> this.mode.getValue() == Sprint.SprintPredicateMode.INSTANT));
    public Setting<Boolean> vanillaSlowdown = add(new BooleanSetting("VanillaSlowdown", false, v -> this.mode.getValue() == Sprint.SprintPredicateMode.INSTANT));
    public Setting<Boolean> boost = add(new BooleanSetting("Boost", false, v -> this.mode.getValue() == Sprint.SprintPredicateMode.INSTANT).setVisible());
    public Setting<Float> boostSpeed = add(new NumberSetting<>("BoostSpeed", 0.5f, 0.1f, 1.0f, v -> this.mode.getValue() == Sprint.SprintPredicateMode.INSTANT && this.boost.isShown()));
    public Setting<Boolean> stopInLiquids = add(new BooleanSetting("StopInLiquids", true));
    public Setting<Boolean> directionSpoof = add(new BooleanSetting("DirectionSpoof", false));
    public Setting<Boolean> keep = add(new BooleanSetting("Keep", true, v -> this.mode.getValue() != Sprint.SprintPredicateMode.INSTANT));
    public final Stopwatch stopwatch;
    public float val;
    public boolean flag;
    public boolean flag2;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/Sprint$SprintPredicateMode.class */
    public enum SprintPredicateMode implements NamedValue {
        LEGIT("Legit"),
        RAGE("Rage"),
        INSTANT("Instant");

        public final String name;

        SprintPredicateMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Sprint() {
        super("Sprint", "Sprints automatically.", Category.MOVEMENT, new String[0]);
        this.stopwatch = new Stopwatch();
        this.val = 0.0f;
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return ClientTextRenderer.capitalize(this.mode.getValue());
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.val = 0.0f;
        }
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if (!ClientServices.obstaclePasserHelper.isPathing() && antiCheat.movementSync.getValue().booleanValue() && antiCheat.rotations.getValue() == CrystalRotationMode.SILENT && this.directionSpoof.getValue().booleanValue() && ClientServices.RotationManager.getActiveRequest() == null && MovementUtils.is2181() && MovementUtils.getMovementYaw() != minecraftClient.player.getYaw() && !this.flag2) {
            PacketSender.sendPacket(new PlayerMoveC2SPacket.Full(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), minecraftClient.player.getYaw(), minecraftClient.player.getPitch(), minecraftClient.player.isOnGround()));
        }
    }

    @Listen
    public void onExplosionVelocity(ExplosionVelocityEvent explosionVelocityEvent) {
        if (ClientServices.obstaclePasserHelper.isPathing()) {
            return;
        }
        Vec3d vec3d = new Vec3d(explosionVelocityEvent.getX(), explosionVelocityEvent.getY(), explosionVelocityEvent.getZ());
        if (MovementUtils.is2181() && ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(750L) && this.boost.getValue().booleanValue() && Math.sqrt(minecraftClient.player.squaredDistanceTo(vec3d)) <= Double.longBitsToDouble(4618441417868443648L)) {
            this.stopwatch.reset();
            this.val = ((float) Math.hypot(explosionVelocityEvent.getPlayerVelocityX(), explosionVelocityEvent.getPlayerVelocityZ())) * this.boostSpeed.getValue().floatValue();
        }
    }

    @Listen(get219= RenderConstants.num2)
    public void onMotion(MotionEvent motionEvent) {
        if (ClientServices.obstaclePasserHelper.isPathing() || isNotInGame() || shouldStopSprint() || rotationLock.isToggled() || !this.directionSpoof.getValue().booleanValue() || !MovementUtils.is2181() || minecraftClient.player.isFallFlying()) {
            return;
        }
        motionEvent.setYaw(MovementUtils.getMovementYaw());
    }

    @Listen
    public void onSprintUpdate(SprintUpdateEvent sprintUpdateEvent) {
        if (ClientServices.obstaclePasserHelper.isPathing() || isNotInGame() || shouldStopSprint()) {
            return;
        }
        if (this.mode.getValue() != SprintPredicateMode.INSTANT || this.forceSprint.getValue().booleanValue()) {
            setSprinting(is2181());
        }
    }

    @Listen(get219= 1000)
    public void onMove(MoveEvent moveEvent) {
        if (ClientServices.obstaclePasserHelper.isPathing() || isNotInGame() || shouldStopSprint() || this.mode.getValue() != SprintPredicateMode.INSTANT || PlayerUtils.isHungry() || minecraftClient.player.isFallFlying() || minecraftClient.player.getAbilities().flying || minecraftClient.player.isTouchingWater() || minecraftClient.player.isInLava() || minecraftClient.player.isSneaking() || minecraftClient.player.isInSwimmingPose() || fastFall.flag || longJump.isToggled() || speed.isToggled()) {
            return;
        }
        double d = MovementUtils.getBaseMoveSpeed(this.vanillaSlowdown.getValue().booleanValue());
        if (ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(500L)) {
            d = Math.max(d, Math.hypot(moveEvent.getX(), moveEvent.getZ()));
        }
        if (this.vanillaSlowdown.getValue().booleanValue()) {
            net.minecraft.block.Block r0 = minecraftClient.world.getBlockState(((me.mioclient.mixin.ducks.DuckEntity)(Object) minecraftClient.player).mio$getVelocityAffectingPos()).getBlock();
            if (r0 == Blocks.SLIME_BLOCK) {
                d *= Double.longBitsToDouble(4604480259023595110L);
            }
            d *= r0.getVelocityMultiplier();
        }
        if (this.boost.getValue().booleanValue() && !this.stopwatch.hasElapsedMs(500L)) {
            d += this.val;
        }
        MovementUtils.applyVelocity(moveEvent, d);
    }

    @Listen
    public void onRotation(RotationEvent event_3) {
        if (!ClientServices.obstaclePasserHelper.isPathing() && this.directionSpoof.getValue().booleanValue() && MovementUtils.is2181() && !minecraftClient.player.isFallFlying()) {
            if ((event_3.isCurrent() || minecraftClient.player.isInSwimmingPose()) && !shouldStopSprint()) {
                if ((!minecraftClient.player.isCrawling() || event_3.isCurrent()) && !rotationLock.isToggled()) {
                    event_3.setYaw(MovementUtils.getMovementYaw());
                    event_3.cancel();
                }
            }
        }
    }

    @Listen
    public void onAttackHookPost(AttackHookPostEvent attackHookPostEvent) {
        if (!ClientServices.obstaclePasserHelper.isPathing() && this.keep.getValue().booleanValue()) {
            if (attackHookPostEvent.getPhase() == KeyPearlMode.Pre) {
                this.flag = minecraftClient.player.isSprinting();
                if (this.flag) {
                    PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING, 0);
                    return;
                }
                return;
            }
            if (this.flag) {
                minecraftClient.player.setSprinting(true);
                PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.START_SPRINTING, 0);
            }
        }
    }

    public boolean is2181() {
        if (shouldStopSprint()) {
            return false;
        }
        if (this.directionSpoof.getValue().booleanValue() && (ClientServices.RotationManager.getActiveRequest() != null || minecraftClient.player.isFallFlying())) {
            return false;
        }
        boolean z = this.mode.getValue() == SprintPredicateMode.LEGIT || minecraftClient.player.isSubmergedInWater();
        if (PlayerUtils.isHungry()) {
            return false;
        }
        if ((z && (minecraftClient.player.input.movementForward <= 0.0f || minecraftClient.player.horizontalCollision)) || isAboveWater()) {
            return false;
        }
        if (fastWeb.isGrimMode() && PlayerUtils.isInCobweb(minecraftClient.player) && (ClientServices.antiPhaseHelper.getGroundTicks() < 2 || minecraftClient.player.input.jumping)) {
            return false;
        }
        return (minecraftClient.player.input.movementForward == 0.0f && minecraftClient.player.input.movementSideways == 0.0f) ? false : true;
    }

    public boolean shouldStopSprint() {
        if (ClientServices.obstaclePasserHelper.isBaritoneLoaded() && ClientServices.obstaclePasserHelper.isPathing()) {
            return true;
        }
        return (scaffold.isToggled() && scaffold.stopSprinting.getValue().booleanValue()) || (PlayerUtils.isInLiquid(minecraftClient.player) && this.stopInLiquids.getValue().booleanValue()) || (efly.isToggled() && efly.mode.getValue() == ElytraFly.ElytraFlyPredicateMode.BOUNCE && efly.hasElytra());
    }

    public void setSprinting(boolean z) {
        try {
            minecraftClient.player.setSprinting(z);
        } catch (Exception e) {
        }
    }

    public boolean isAboveWater() {
        if (minecraftClient.player.isOnGround()) {
            return false;
        }
        return BlockPos.stream(minecraftClient.player.getBoundingBox().stretch(0.0d, Double.longBitsToDouble(-4631501856787818086L), 0.0d)).anyMatch(blockPos -> {
            return minecraftClient.world.isWater(blockPos);
        });
    }
}
