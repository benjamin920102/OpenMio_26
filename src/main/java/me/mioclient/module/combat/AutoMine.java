package me.mioclient.module.combat;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.module.combat.automine.BurrowTargetStrategy;
import me.mioclient.module.combat.automine.EnderChestTargetStrategy;
import me.mioclient.module.combat.automine.FeetTargetStrategy;
import me.mioclient.module.combat.automine.SelfTargetStrategy;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.module.combat.automine.AutoMineLogic;
import me.mioclient.util.time.TickScheduler;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.module.combat.automine.FloorTargetStrategy;
import me.mioclient.module.combat.automine.HeadTargetStrategy;
import me.mioclient.module.combat.automine.MineTargetSelector;
import me.mioclient.module.combat.automine.MineTargetState;
import me.mioclient.module.combat.automine.MineTargetStrategy;
import me.mioclient.module.combat.automine.MineTargetTracker;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public final class AutoMine extends Module {
    public static final int num = 1500;
    /* Resolved lazily — static init ran before module registry was ready, leaving field null. */
    private static SpeedMine speedMine;

    public static SpeedMine getSpeedMine() {
        if (speedMine == null && ClientServices.moduleInjector != null) {
            try {
                speedMine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
            } catch (Exception e) {
            }
        }
        return speedMine;
    }

    private static AntiCheat antiCheat;

    public static AntiCheat getAntiCheat() {
        if (antiCheat == null && ClientServices.moduleInjector != null) {
            try {
                antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
            } catch (Exception e) {
            }
        }
        return antiCheat;
    }
    public Setting<AutoMineLogic> logic = add(new EnumSetting<>("Logic", AutoMineLogic.PLAIN));
    public Setting<Boolean> strictDirection = add(new BooleanSetting("StrictDirection", false));
    public Setting<Boolean> raytrace = add(new BooleanSetting("Raytrace", false));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false));
    public Setting<Boolean> ignoreNaked = add(new BooleanSetting("IgnoreNaked", false));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", false).setDependent());
    public Setting<Boolean> enderChests = add(new BooleanSetting("EnderChests", true).requiresSettings(this.targets));
    public Setting<Boolean> burrow = add(new BooleanSetting("Burrow", true).requiresSettings(this.targets));
    public Setting<Boolean> fish = add(new BooleanSetting("Fish", false).requiresSettings(this.targets));
    public Setting<Boolean> head = add(new BooleanSetting("Head", false).setVisible().requiresSettings(this.targets));
    public Setting<Boolean> trapOverride = add(new BooleanSetting("TrapOverride", false).requiresSettings(this.targets, this.head));
    public Setting<Boolean> feet = add(new BooleanSetting("Feet", true).setVisible().requiresSettings(this.targets));
    public Setting<Boolean> safe = add(new BooleanSetting("Safe", true).requiresSettings(this.targets, this.feet));
    public Setting<Boolean> self = add(new BooleanSetting("Self", true).setVisible().requiresSettings(this.targets));
    public Setting<Boolean> face = add(new BooleanSetting("Face", false).requiresSettings(this.targets, this.self));
    public Setting<Boolean> onlyCrawl = add(new BooleanSetting("OnlyCrawl", false).requiresSettings(this.targets, this.self));
    public Setting<Boolean> downPriority = add(new BooleanSetting("DownPriority", false).requiresSettings(this.targets, this.self));
    public Setting<Integer> grimDelay = add(new NumberSetting<>("GrimDelay", 1, 0, 5, v -> false));
    public final List<MineTargetStrategy> list;
    public final MineTargetSelector autoMineSearchHelper4;
    public final MineTargetTracker speedMineHelper;
    public final AtomicBoolean atomicBoolean;
    public boolean flag;

    public AutoMine() {
        super("AutoMine", new StringTemplate().arg(String.valueOf(ChatFormatting.RED)).format("Mines out traps for your enemies and farms obsidian. \n\u0001Requires SpeedMine module enabled."), Category.COMBAT, "combatmine", "autocity");
        this.list = List.of(new EnderChestTargetStrategy(this), new BurrowTargetStrategy(this), new FloorTargetStrategy(this), new FeetTargetStrategy(this), new SelfTargetStrategy(this), new HeadTargetStrategy(this));
        this.autoMineSearchHelper4 = new MineTargetSelector(this);
        this.speedMineHelper = new MineTargetTracker(this);
        this.atomicBoolean = new AtomicBoolean();
    }

    @Listen(get219= 300)
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        SpeedMine speedMine_lcl = getSpeedMine();
        AntiCheat antiCheat_lcl = getAntiCheat();
        if (speedMine_lcl != null && speedMine_lcl.isToggled()) {
            MineTargetState autoMineHelper = new MineTargetState();
            for (MineTargetStrategy autoMineHelper_2 : this.list) {
                if (autoMineHelper_2.isEnabled()) {
                    autoMineHelper_2.findTargets(autoMineHelper);
                }
            }
            if ((!hasActiveMineTarget() || autoMineHelper.getPriority() >= 1500) && !this.atomicBoolean.get()) {
                if (autoMineHelper.isCompleted()) {
                    speedMine_lcl.cancelAndReset();
                    return;
                }
                if (autoMineHelper.getBlockPos() == null) {
                    if (autoMineHelper.getSecondaryBlockPos() == null || autoMineHelper.getSecondaryBlockPos().equals(this.speedMineHelper.getBlockPos())) {
                        return;
                    }
                    autoMineHelper.setBlockPos(autoMineHelper.getSecondaryBlockPos());
                    autoMineHelper.setSecondaryBlockPos((BlockPos) null);
                }
                if (isExtraBreakEnabled() && this.speedMineHelper.hasActiveMiningTarget() && autoMineHelper.getPriority() < 1500) {
                    return;
                }
                BlockPos blockPos386 = autoMineHelper.getBlockPos();
                BlockPos blockPos2898 = autoMineHelper.getSecondaryBlockPos();
                Direction direction2750 = getMiningDirection(blockPos386);
                if (direction2750 == null || blockPos386.equals(speedMine_lcl.getMineTargetPos())) {
                    return;
                }
                if (isExtraBreakEnabled() && blockPos2898 != null && !blockPos2898.equals(blockPos386) && !this.speedMineHelper.hasActiveMiningTarget()) {
                    executeMineAction(blockPos2898, direction2750, false);
                }
                if (antiCheat_lcl == null || !antiCheat_lcl.isF2b2t()) {
                    executeMineAction(blockPos386, direction2750, true);
                    return;
                }
                this.atomicBoolean.set(true);
                TickScheduler tooltipsHelper = ClientServices.tooltipsHelper;
                Runnable runnable = () -> {
                    this.atomicBoolean.set(false);
                    executeMineAction(blockPos386, direction2750, true);
                };
                tooltipsHelper.schedule(runnable, this.grimDelay.getValue().intValue());
            }
        }
    }

    public void executeMineAction(BlockPos blockPos, Direction direction, boolean z) {
        Vec3 vec3d2430;
        if (this.rotate.getValue().booleanValue()) {
            float[] floatArray2484 = RotationManager.getRotationsTo(Vec3.atCenterOf(blockPos));
            if (this.raytrace.getValue().booleanValue() && (vec3d2430 = WorldUtils.findVisiblePoint(blockPos, AutoCraftMode.X8)) != null) {
                floatArray2484 = RotationManager.getRotationsTo(vec3d2430);
            }
            ClientServices.RotationManager.requestRotations(floatArray2484, 100);
        }
        SpeedMine.flag = z;
        minecraftClient.gameMode.startDestroyBlock(blockPos, direction);
        SpeedMine.flag = false;
    }

    public Direction getMiningDirection(BlockPos blockPos) {
        if (!this.strictDirection.getValue().booleanValue()) {
            return Direction.UP;
        }
        List<Direction> list3031 = BlockPlacer.getInteractableSides(blockPos);
        if (list3031.isEmpty()) {
            return null;
        }
        return (Direction) list3031.getFirst();
    }

    public boolean hasActiveMineTarget() {
        if (isMiningInProgress()) {
            SpeedMine speedMine_lcl = getSpeedMine();
            if (speedMine_lcl == null || speedMine_lcl.getMineTargetPos() == null) {
                setTargetFlagged(false);
            } else {
                setTargetFlagged(!WorldUtils.isAir(speedMine_lcl.getMineTargetPos()));
            }
        }
        return isMiningInProgress();
    }

    public void setTargetFlagged(boolean z) {
        this.flag = z;
    }

    public boolean isMiningInProgress() {
        return this.flag;
    }

    public float getRange() {
        SpeedMine speedMine_lcl = getSpeedMine();
        if (speedMine_lcl == null) {
            return 4.5f;
        }
        return speedMine_lcl.range.getValue().floatValue();
    }

    public boolean isExtraBreakEnabled() {
        SpeedMine speedMine_lcl = getSpeedMine();
        if (speedMine_lcl == null) {
            return false;
        }
        return speedMine_lcl.extraBreak.getValue().booleanValue();
    }
}