package me.mioclient.module.player;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.interaction.SlotChangeEvent;
import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.module.player.speedmine.SlotRestoreTask;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.mode.player.SpeedMineMode;
import me.mioclient.mode.player.SpeedMineMode_2;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.module.player.speedmine.SpeedMineMode_3;
import me.mioclient.module.player.speedmine.SpeedMineMode_4;
import me.mioclient.module.player.speedmine.SpeedMineMode_5;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.shape.FadingBoxRenderer;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.module.player.speedmine.InstantBreakTracker;
import me.mioclient.module.player.speedmine.SpeedMineTarget;
import me.mioclient.network.PacketSender;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.shape.FadingBoxRenderer;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.ducks.DuckClientPlayerInteractionManager;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.event.events.interaction.SlotChangeEvent;
import me.mioclient.mode.player.SpeedMineMode;
import me.mioclient.mode.player.SpeedMineMode_2;
import me.mioclient.module.player.speedmine.SpeedMineMode_3;
import me.mioclient.module.player.speedmine.SpeedMineMode_4;
import me.mioclient.module.player.speedmine.SpeedMineMode_5;
import me.mioclient.module.player.speedmine.SlotRestoreTask;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.AttackBlockEvent;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.combat.AutoExp;
import me.mioclient.module.combat.AutoMine;
import me.mioclient.module.movement.NoSlow;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/SpeedMine.class */
public class SpeedMine extends Module {
    public static boolean flag;
    public Setting<ScaffoldMode_2> mode = add(new EnumSetting<>("Mode", ScaffoldMode_2.ANY));
    public Setting<Float> damage = add(new NumberSetting<>("Damage", 1.0f, 0.7f, 1.0f));
    public Setting<Float> range = add(new NumberSetting<>("Range", 5.2f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Boolean> extraBreak = add(new BooleanSetting("ExtraBreak", false));
    public Setting<Boolean> tPSSync = add(new BooleanSetting("TPSSync", true));
    public Setting<Boolean> swing = add(new BooleanSetting("Swing", true));
    public Setting<Boolean> rebreak = add(new BooleanSetting("Rebreak", true).setDependent());
    public Setting<SpeedMineMode_2> rebreak2 = add(new EnumSetting<>("Rebreak", SpeedMineMode_2.NONE, v -> this.rebreak.isShown()));
    public Setting<Boolean> assistant = add(new BooleanSetting("Assistant", false).requiresSettings(this.rebreak));
    public Setting<Float> instantDelay = add(new NumberSetting<>("InstantDelay", 0.0f, 0.0f, 2.0f, v -> this.rebreak2.getValue() == SpeedMineMode_2.INSTANT && this.rebreak.isShown()).setSuffix("s"));
    public Setting<Boolean> clickReset = add(new BooleanSetting("ClickReset", true, v -> this.rebreak.isShown()));
    public Setting<Boolean> holdingBest = add(new BooleanSetting("HoldingBest", false, v -> this.rebreak2.getValue() == SpeedMineMode_2.INSTANT && this.rebreak.isShown()));
    public Setting<Boolean> autoSwap2 = add(new BooleanSetting("AutoSwap", true).setDependent());
    public Setting<SpeedMineMode_3> autoSwap = add(new EnumSetting<>("AutoSwap", SpeedMineMode_3.NONE, v -> this.autoSwap2.isShown()));
    public Setting<Boolean> swapBack = add(new BooleanSetting("SwapBack", false));
    public Setting<SpeedMineMode_4> f121 = add(new EnumSetting<>("1.21", SpeedMineMode_4.OFF, v -> this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.autoSwap2.isShown()));
    public Setting<Boolean> alternative = add(new BooleanSetting("Alternative", false, v -> this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.autoSwap2.isShown()));
    public Setting<Boolean> swapReset = add(new BooleanSetting("SwapReset", true, v -> this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.alternative.getValue().booleanValue() && this.autoSwap2.isShown()));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false).setVisible());
    public Setting<Float> limit = add(new NumberSetting<>("Limit", 0.9f, 0.0f, 1.0f, v -> this.rotate.isShown()).setHoleSnapConfig("Off", HoleSnapMode.MAX));
    public Setting<Boolean> render = add(new BooleanSetting("Render", true).setVisible());
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.0f, 5.0f, v -> this.render.isShown()));
    public Setting<SpeedMineMode> renderMode = add(new EnumSetting<>("RenderMode", SpeedMineMode.OUT, v -> this.render.isShown()));
    public Setting<Boolean> air = add(new BooleanSetting("Air", false, v -> this.render.isShown() && this.rebreak2.getValue() == SpeedMineMode_2.INSTANT));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", false, v -> this.render.isShown() && this.rebreak2.getValue() == SpeedMineMode_2.INSTANT && !this.air.getValue().booleanValue()));
    public Setting<SpeedMineMode_5> colorMode = add(new EnumSetting<>("ColorMode", SpeedMineMode_5.PROGRESS, v -> this.render.isShown()));
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(280861183, true), v -> this.render.isShown() && this.colorMode.getValue() == SpeedMineMode_5.CUSTOM).setThemeSync(true));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(-1044538881, true), v -> this.render.isShown() && this.colorMode.getValue() == SpeedMineMode_5.CUSTOM).setThemeSync(true));
    public Setting<Set<Block>> blocks = add(new EntityListObjectSetting<>("Blocks", Registries.BLOCK));
    public final ArrayDeque<Boolean> arrayDeque;
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public final Stopwatch stopwatch3;
    public final Stopwatch stopwatch4;
    public final AtomicBoolean atomicBoolean2;
    public final InstantBreakTracker speedMineHelper_2;
    public final FadingBoxRenderer speedMineHelper_3;

    @Nullable
    public SlotRestoreTask speedMineRunnable;
    public float val;
    public float val2;
    public int num;
    public int num2;
    public SpeedMineTarget speedMineSearchHelper4;
    public BlockPos blockPos;
    public BlockPos blockPos2;
    public volatile boolean flag2;
    public static final AutoExp autoExp = (AutoExp) ClientServices.moduleInjector.getModule(AutoExp.class);
    public static AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public static final AutoMine autoMine = (AutoMine) ClientServices.moduleInjector.getModule(AutoMine.class);
    public static final NoGlitchBlocks noGlitchBlocks = (NoGlitchBlocks) ClientServices.moduleInjector.getModule(NoGlitchBlocks.class);
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public static final NoSlow noSlow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class);
    public static ItemSaver itemSaver = (ItemSaver) ClientServices.moduleInjector.getModule(ItemSaver.class);
    public static final AtomicBoolean atomicBoolean = new AtomicBoolean();

    public SpeedMine() {
        super("SpeedMine", "Mines blocks silently.", Category.PLAYER, new String[0]);
        this.arrayDeque = new ArrayDeque<>();
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        this.stopwatch3 = new Stopwatch();
        this.stopwatch4 = new Stopwatch();
        this.atomicBoolean2 = new AtomicBoolean();
        this.speedMineHelper_2 = new InstantBreakTracker(this);
        this.speedMineHelper_3 = new FadingBoxRenderer();
        this.num = -1;
        this.speedMineSearchHelper4 = null;
        this.flag2 = false;
        this.rebreak.setConfigName("RebreakPage");
        this.autoSwap2.setConfigName("SwapPage");
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        reset();
        this.blockPos2 = null;
        atomicBoolean.set(false);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (WorldUtils.isBreakable(this.blockPos)) {
            return "%.2f".formatted(Float.valueOf(MathHelper.clamp(getMiningProgress() / this.damage.getValue().floatValue(), 0.0f, Float.intBitsToFloat(1065353216))));
        }
        return null;
    }

    @Listen
    public void onAttackBlock(AttackBlockEvent attackBlockEvent) {
        if (!isBlockAllowed(attackBlockEvent.getBlockPos())) {
            this.blockPos2 = null;
            reset();
            return;
        }
        if (minecraftClient.player.isSpectator() || minecraftClient.player.isCreative()) {
            return;
        }
        attackBlockEvent.cancel();
        if (!this.stopwatch.hasElapsedMs(100L) && !flag) {
            if (attackBlockEvent.getBlockPos().equals(this.blockPos)) {
                this.stopwatch.reset();
                return;
            }
            return;
        }
        if (WorldUtils.isBreakable(attackBlockEvent.getBlockPos()) || flag) {
            if (this.extraBreak.getValue().booleanValue() && this.speedMineSearchHelper4 != null && attackBlockEvent.getBlockPos().equals(this.speedMineSearchHelper4.getBlockPos())) {
                return;
            }
            if (minecraftClient.player.getEyePos().squaredDistanceTo(attackBlockEvent.getBlockPos().toCenterPos()) > this.range.getValue().floatValue() * this.range.getValue().floatValue()) {
                return;
            }
            this.stopwatch.reset();
            boolean z = false;
            if (this.blockPos != null && !this.blockPos.equals(attackBlockEvent.getBlockPos())) {
                reset();
                z = true;
            }
            if (!z && this.blockPos != null && this.blockPos.equals(attackBlockEvent.getBlockPos())) {
                if (this.clickReset.getValue().booleanValue() && this.stopwatch3.hasElapsedMs(100L)) {
                    this.blockPos2 = null;
                    reset();
                    return;
                }
                return;
            }
            if (this.alternative.getValue().booleanValue() && this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.swapReset.getValue().booleanValue()) {
                if (this.flag2) {
                    this.flag2 = false;
                    reset();
                    return;
                } else if (autoExp.isToggled() && autoExp.mode.getValue() == AutoExp.SpeedMineMode.SILENT && !autoExp.flag) {
                    reset();
                    return;
                }
            }
            if (!flag) {
                autoMine.setTargetFlagged(true);
            }
            BlockPos blockPos386 = attackBlockEvent.getBlockPos();
            this.blockPos2 = blockPos386;
            this.blockPos = blockPos386;
            Direction direction1055 = getInteractDirection(this.blockPos);
            if (this.swing.getValue().booleanValue()) {
                minecraftClient.player.swingHand(Hand.MAIN_HAND);
            }
            int i = get1050(this.blockPos);
            int i2 = minecraftClient.player.getInventory().selectedSlot;
            boolean z2 = i != -1;
            if (this.autoSwap.getValue() == SpeedMineMode_3.NORMAL) {
                i = InventoryUtils.getBestToolSlot(this.blockPos, true);
                if (i != i2 && this.swapBack.getValue().booleanValue()) {
                    this.num2 = i2;
                }
            }
            if (i != -1) {
                if (this.alternative.getValue().booleanValue() && this.autoSwap.getValue() == SpeedMineMode_3.SILENT) {
                    InventoryUtils.swapToHotbar(i);
                } else if (i2 != i) {
                    InventoryUtils.selectSlot(i);
                }
            }
            if (this.extraBreak.getValue().booleanValue()) {
                PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, this.blockPos, direction1055);
                PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.blockPos, direction1055);
                if (this.speedMineSearchHelper4 == null) {
                    this.speedMineSearchHelper4 = new SpeedMineTarget(this.blockPos);
                }
            } else {
                PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, this.blockPos, direction1055);
                if (z2) {
                    PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.blockPos, direction1055);
                }
            }
            if (z2 && i != -1) {
                if (this.autoSwap.getValue() == SpeedMineMode_3.SILENT) {
                    if (this.alternative.getValue().booleanValue()) {
                        InventoryUtils.swapToHotbar(i);
                    } else if (i2 != i) {
                        InventoryUtils.selectSlot(i2);
                    }
                }
                ClientServices.stashFinderHelper.breakBlockWithSnapshot(this.blockPos);
                this.stopwatch.setTime(-1L);
            }
            minecraftClient.player.swingHand(Hand.MAIN_HAND);
            this.stopwatch3.reset();
        }
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        reset();
        this.blockPos2 = null;
    }

    @Listen(get219= RenderConstants.num2)
    public void onSlotChange(SlotChangeEvent speedMineEvent) {
        if (this.alternative.getValue().booleanValue() && this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.swapReset.getValue().booleanValue()) {
            this.atomicBoolean2.set(true);
            this.val2 = 0.0f;
            this.val = 0.0f;
            ClientServices.tooltipsHelper.schedule(() -> {
                this.atomicBoolean2.set(false);
                BlockPos blockPos = this.blockPos;
                if (blockPos == null) {
                    return;
                }
                if (this.swing.getValue().booleanValue()) {
                    PacketSender.sendHandSwing(Hand.MAIN_HAND);
                }
                if (WorldUtils.isBreakable(blockPos)) {
                    flag = true;
                    reset();
                    onAttackBlock(new AttackBlockEvent(blockPos, getInteractDirection(blockPos)));
                    flag = false;
                    return;
                }
                if (this.rebreak2.getValue() == SpeedMineMode_2.FAST) {
                    PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, getInteractDirection(blockPos));
                    this.arrayDeque.clear();
                }
            }, 1);
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        this.speedMineHelper_2.onPacketReceive(channelRead0Event);
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (!RotationManager.is724() || this.damage.getValue().floatValue() >= Float.intBitsToFloat(1065353216) || antiCheat.isF2b2t()) {
            return;
        }
        PlayerActionC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerActionC2SPacket ? (PlayerActionC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerActionC2SPacket) {
            PlayerActionC2SPacket playerActionC2SPacket = packet904;
            if (playerActionC2SPacket.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
                PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, playerActionC2SPacket.getPos().add(0, 500, 0), playerActionC2SPacket.getDirection());
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:131:0x0874, code lost:
    
        if (me.mioclient.api.ClientServices.stashFinderHelper.isBlockConfirmed(r8.blockPos) != false) goto L143;
     */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0396 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0397  */
    @Listen(get219= RenderConstants.num2)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onEvent2(RotationTickEvent holeSnapEvent) {
        boolean z;
        if (atomicBoolean.get()) {
            return;
        }
        if (this.autoSwap.getValue() == SpeedMineMode_3.NONE && !ItemSaver.isToolSafe(minecraftClient.player.getMainHandStack()) && itemSaver.isToggled()) {
            return;
        }
        if (EntityUtil.getPlayerHealth() <= Float.intBitsToFloat(1082130432)) {
            if (minecraftClient.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                z = false;
                boolean z2 = z;
                if (this.speedMineSearchHelper4 == null && !this.speedMineSearchHelper4.getBlockPos().equals(this.blockPos) && this.speedMineSearchHelper4.get2142() >= Float.intBitsToFloat(1065353216) && !noSlow.shouldApply() && z2) {
                    atomicBoolean.set(true);
                    int i = minecraftClient.player.getInventory().selectedSlot;
                    int i2 = InventoryUtils.getBestToolSlot(this.speedMineSearchHelper4.getBlockPos(), !this.alternative.getValue().booleanValue());
                    if (this.alternative.getValue().booleanValue()) {
                        InventoryUtils.swapToHotbar(i2);
                    } else {
                        PacketSender.sendSelectedSlot(i2, true);
                    }
                    rotateToTarget(this.speedMineSearchHelper4.getBlockPos());
                    this.stopwatch4.reset();
                    autoMine.speedMineHelper.do1227(this.speedMineSearchHelper4.getBlockPos());
                    this.speedMineSearchHelper4 = null;
                    ClientServices.tooltipsHelper.schedule(() -> {
                        if (this.alternative.getValue().booleanValue()) {
                            InventoryUtils.swapToHotbar(i2);
                        } else {
                            PacketSender.sendSelectedSlot(i, true);
                            minecraftClient.player.getInventory().selectedSlot = i;
                        }
                        atomicBoolean.set(false);
                    }, 2);
                    return;
                }
                if (this.num2 == -1 && !WorldUtils.isBreakable(this.blockPos) && this.autoSwap.getValue() == SpeedMineMode_3.NORMAL && this.swapBack.getValue().booleanValue()) {
                    InventoryUtils.selectSlot(this.num2);
                    this.num2 = -1;
                    return;
                }
                if (shouldInterruptForSwap() && !minecraftClient.player.isSpectator() && !minecraftClient.player.isCreative() && WorldUtils.isBreakable(this.blockPos)) {
                    if (this.limit.getValue().floatValue() != Float.intBitsToFloat(1065353216) || (getMiningProgress() / this.damage.getValue().floatValue() >= this.limit.getValue().floatValue() && (this.autoSwap.getValue() != SpeedMineMode_3.NONE || !this.holdingBest.getValue().booleanValue() || this.num == minecraftClient.player.getInventory().selectedSlot))) {
                        rotateToTarget(this.blockPos);
                    }
                    if (getMiningProgress() >= this.damage.getValue().floatValue()) {
                        return;
                    }
                    if (this.rebreak2.getValue() == SpeedMineMode_2.INSTANT) {
                        if (!this.stopwatch2.hasElapsed(this.instantDelay.getValue().floatValue(), TimeUnit.SECONDS)) {
                            return;
                        }
                    }
                    if (noSlow.shouldApply()) {
                        return;
                    }
                    if (!this.assistant.getValue().booleanValue() || isSafeToMine()) {
                        int i3 = minecraftClient.player.getInventory().selectedSlot;
                        BlockState blockState = minecraftClient.world.getBlockState(this.blockPos);
                        this.speedMineRunnable = new SlotRestoreTask(this, minecraftClient.player.getMainHandStack().getItem(), this.num, minecraftClient.player.getInventory().selectedSlot);
                        Direction direction1055 = getInteractDirection(this.blockPos);
                        if (this.autoSwap.getValue() == SpeedMineMode_3.NONE || this.num == -1 || minecraftClient.player.getInventory().selectedSlot == this.num) {
                            if (i3 != this.num && this.rebreak2.getValue() == SpeedMineMode_2.INSTANT && this.holdingBest.getValue().booleanValue()) {
                                return;
                            }
                        } else if (this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.alternative.getValue().booleanValue()) {
                            InventoryUtils.swapToHotbar(this.num);
                        } else {
                            this.num2 = minecraftClient.player.getInventory().selectedSlot;
                            InventoryUtils.selectSlot(this.num);
                        }
                        this.stopwatch2.reset();
                        if (WorldUtils.isBreakable(this.blockPos)) {
                            this.stopwatch4.reset();
                        }
                        if (this.swing.getValue().booleanValue()) {
                            PacketSender.sendHandSwing(Hand.MAIN_HAND);
                        }
                        if (!RotationManager.is724() && this.rebreak2.getValue() != SpeedMineMode_2.INSTANT) {
                            PacketSender.sendPosition(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), minecraftClient.player.isOnGround());
                        }
                        BlockPos blockPos = this.blockPos;
                        this.speedMineHelper_2.beginTracking(this.blockPos);
                        PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.blockPos, direction1055);
                        if (antiCheat.isF2b2t() && this.rebreak2.getValue() == SpeedMineMode_2.NONE) {
                            PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.blockPos, direction1055);
                        }
                        ClientServices.stashFinderHelper.setUnconfirmedBreak(this.blockPos);
                        if (this.rebreak2.getValue() == SpeedMineMode_2.FAST) {
                            PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.blockPos, direction1055);
                            PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, this.blockPos, direction1055);
                            PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, this.blockPos, direction1055);
                            this.blockPos = null;
                        }
                        if (this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.num != -1) {
                            int i4 = this.num;
                            Runnable runnable = () -> {
                                if (this.alternative.getValue().booleanValue() && this.autoSwap.getValue() == SpeedMineMode_3.SILENT) {
                                    InventoryUtils.swapToHotbar(i4);
                                } else if (i3 != this.num || this.f121.getValue() == SpeedMineMode_4.TICK) {
                                    InventoryUtils.selectSlot(i3);
                                }
                                autoCrystal.atomicBoolean.set(false);
                            };
                            if (this.f121.getValue() == SpeedMineMode_4.TICK) {
                                atomicBoolean.set(true);
                                ClientServices.tooltipsHelper.schedule(() -> {
                                    atomicBoolean.set(false);
                                    runnable.run();
                                }, 2);
                            } else {
                                runnable.run();
                            }
                        }
                        if (this.autoSwap.getValue() == SpeedMineMode_3.SILENT && this.alternative.getValue().booleanValue()) {
                            ClientServices.tooltipsHelper.schedule(this.speedMineRunnable, 2);
                        }
                        if (this.rebreak2.getValue() == SpeedMineMode_2.INSTANT) {
                            if (blockState != null && (blockState.getBlock() instanceof ShulkerBoxBlock)) {
                            }
                            if (this.rotate.getValue().booleanValue() || !RotationManager.is1144()) {
                                return;
                            }
                            ClientServices.RotationManager.requestRotations(RotationManager.limitYawSpeed(RotationManager.getRotationsTo(blockPos.toCenterPos()), ClientServices.RotationManager.getYawStep()), 150);
                            return;
                        }
                        reset();
                        if (this.rotate.getValue().booleanValue()) {
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                return;
            }
        }
        z = true;
        boolean z22 = z;
        if (this.speedMineSearchHelper4 == null) {
        }
        if (this.num2 == -1) {
        }
        if (shouldInterruptForSwap()) {
            return;
        }
        if (this.limit.getValue().floatValue() != Float.intBitsToFloat(1065353216)) {
        }
        rotateToTarget(this.blockPos);
        if (getMiningProgress() >= this.damage.getValue().floatValue()) {
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        float f = this.tPSSync.getValue().booleanValue() ? ClientServices.holeSnapRenderHelper.getTickRateFactor() : Float.intBitsToFloat(1065353216);
        if (this.speedMineSearchHelper4 != null) {
            this.speedMineSearchHelper4.addMiningProgress((float) (WorldUtils.getBlockBreakingDelta(minecraftClient.player.getInventory().getStack(InventoryUtils.getBestToolSlot(this.speedMineSearchHelper4.getBlockPos(), !this.alternative.getValue().booleanValue())), minecraftClient.world.getBlockState(this.speedMineSearchHelper4.getBlockPos()), minecraftClient.player.isOnGround()) * f));
            if (minecraftClient.world.isAir(this.speedMineSearchHelper4.getBlockPos())) {
                this.speedMineSearchHelper4 = null;
            }
        }
        if (this.blockPos2 != null) {
            if (minecraftClient.world.getBlockState(this.blockPos2).getBlock() instanceof ShulkerBoxBlock) {
                if (ClientServices.stashFinderHelper.isBlockConfirmed(this.blockPos2)) {
                    this.blockPos2 = null;
                }
            }
        }
        if (this.atomicBoolean2.get()) {
            return;
        }
        this.val2 = this.val;
        if (this.blockPos == null) {
            if (this.blockPos2 == null || minecraftClient.player.isCreative() || !WorldUtils.isBreakable(this.blockPos2) || this.rebreak2.getValue() != SpeedMineMode_2.FAST) {
                return;
            }
            this.blockPos = this.blockPos2;
            return;
        }
        this.num = InventoryUtils.getBestToolSlot(this.blockPos, !this.alternative.getValue().booleanValue());
        if (this.rebreak2.getValue() == SpeedMineMode_2.INSTANT && this.blockPos != null) {
            if (minecraftClient.world.getBlockState(this.blockPos).getBlock() == Blocks.AIR) {
                this.stopwatch2.reset();
            }
        }
        if (!WorldUtils.isBreakable(this.blockPos)) {
            if (this.rebreak2.getValue() != SpeedMineMode_2.FAST) {
                if (this.rebreak2.getValue() != SpeedMineMode_2.INSTANT) {
                    reset();
                    return;
                }
                return;
            } else {
                this.arrayDeque.add(Boolean.valueOf(minecraftClient.player.isOnGround()));
                if (this.arrayDeque.size() > 200) {
                    this.arrayDeque.removeLast();
                    return;
                }
                return;
            }
        }
        BlockState blockState = minecraftClient.world.getBlockState(this.blockPos);
        ItemStack stack = minecraftClient.player.getInventory().getStack(getEffectiveToolSlot());
        if (getEffectiveToolSlot() != minecraftClient.player.getInventory().selectedSlot && this.f121.getValue() == SpeedMineMode_4.SLOW) {
            stack = new ItemStack(stack.getItem());
        }
        boolean z = false;
        if (this.rebreak2.getValue() == SpeedMineMode_2.FAST) {
            while (!this.arrayDeque.isEmpty()) {
                this.val += (float) (WorldUtils.getBlockBreakingDelta(stack, blockState, this.arrayDeque.poll().booleanValue()) * f);
                z = true;
            }
            this.val2 = this.val;
        }
        if (!z || !blockState.isOf(Blocks.OBSIDIAN)) {
            this.val += (float) (WorldUtils.getBlockBreakingDelta(stack, blockState, minecraftClient.player.isOnGround()) * f);
        } else {
            this.val = MathHelper.clamp(this.val, 0.0f, Float.intBitsToFloat(1065185444));
            this.val2 = this.val;
        }
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() != KeyPearlMode.Pre || this.blockPos == null) {
            return;
        }
        if (minecraftClient.player.getEyePos().distanceTo(this.blockPos.toCenterPos()) <= this.range.getValue().floatValue()) {
            return;
        }
        cancelAndReset();
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (this.render.getValue().booleanValue()) {
            if (this.extraBreak.getValue().booleanValue() && this.speedMineSearchHelper4 != null && !this.speedMineSearchHelper4.getBlockPos().equals(this.blockPos)) {
                this.speedMineSearchHelper4.do2141(this, inner_3);
            }
            if (this.blockPos == null) {
                return;
            }
            VoxelShape outlineShape = minecraftClient.world.getBlockState(this.blockPos).getOutlineShape(minecraftClient.world, this.blockPos);
            boolean z = this.rebreak2.getValue() == SpeedMineMode_2.INSTANT;
            if (!outlineShape.isEmpty() && WorldUtils.isBreakable(this.blockPos)) {
                this.speedMineHelper_3.startFadeOut();
            } else {
                if (!z) {
                    return;
                }
                if (!this.air.getValue().booleanValue() && !this.fade.getValue().booleanValue()) {
                    return;
                }
            }
            float clamp = MathHelper.clamp(MathHelper.lerp(RenderUtil.getPartialTick(), this.val2, getMiningProgress()) / this.damage.getValue().floatValue(), 0.0f, Float.intBitsToFloat(1065353216));
            Color[] colorArray1773 = this.colorMode.getValue().getRenderColors(this, clamp);
            Box offset = this.renderMode.getValue().getRenderBox(this, outlineShape.isEmpty() ? new Box(0.0d, 0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L)) : outlineShape.getBoundingBox(), clamp).offset(this.blockPos);
            if (minecraftClient.world.getBlockState(this.blockPos).isAir()) {
                offset = new Box(this.blockPos);
            }
            if (!this.fade.getValue().booleanValue() || this.air.getValue().booleanValue()) {
                BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), offset, colorArray1773[0]);
                BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), offset, colorArray1773[1], this.lineWidth.getValue().floatValue());
                return;
            }
            this.speedMineHelper_3.setBoundingBox(offset);
            this.speedMineHelper_3.setStrokeWidth(this.lineWidth.getValue().floatValue());
            this.speedMineHelper_3.renderFadingBox(inner_3.getMatrixStack(), colorArray1773[0], colorArray1773[1], z ? Float.intBitsToFloat(1142292480) : Float.intBitsToFloat(1065353216), true);
        }
    }

    public boolean shouldInterruptForSwap() {
        if (this.rebreak2.getValue() != SpeedMineMode_2.INSTANT || this.autoSwap.getValue() == SpeedMineMode_3.NONE) {
            return false;
        }
        this.speedMineHelper_2.recalculateLayout();
        boolean isInstantBreakReady = this.speedMineHelper_2.isInstantBreakReady();
        BlockPos blockPos = this.blockPos;
        if (isInstantBreakReady) {
            reset();
            this.atomicBoolean2.set(true);
            ClientServices.tooltipsHelper.schedule(() -> {
                this.atomicBoolean2.set(false);
                minecraftClient.interactionManager.attackBlock(blockPos, Direction.UP);
            }, 1);
        }
        return isInstantBreakReady;
    }

    public void rotateToTarget(BlockPos blockPos) {
        if (!this.rotate.getValue().booleanValue() || RotationManager.is1144()) {
            return;
        }
        Vec3d vec3d2430 = WorldUtils.findVisiblePoint(blockPos, AutoCraftMode.X8);
        if (vec3d2430 == null) {
            vec3d2430 = blockPos.toCenterPos();
        }
        ClientServices.RotationManager.requestRotations(RotationManager.limitYawSpeed(RotationManager.getRotationsTo(vec3d2430), ClientServices.RotationManager.getYawStep()), 150, true);
    }

    public double getCurrentMiningSpeed() {
        if (this.blockPos == null) {
            return 0.0d;
        }
        return WorldUtils.getBlockBreakingDelta(minecraftClient.player.getInventory().getStack(getEffectiveToolSlot()), minecraftClient.world.getBlockState(this.blockPos), minecraftClient.player.isOnGround()) * (this.tPSSync.getValue().booleanValue() ? ClientServices.holeSnapRenderHelper.getTickRateFactor() : Float.intBitsToFloat(1065353216));
    }

    public int getEffectiveToolSlot() {
        return this.num < 0 ? minecraftClient.player.getInventory().selectedSlot : this.num;
    }

    public void cancelAndReset() {
        this.blockPos2 = null;
        reset();
    }

    public void reset() {
        if (this.blockPos != null && !isNotInGame()) {
            PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.blockPos, getInteractDirection(this.blockPos));
            PacketSender.sendHandSwing(Hand.MAIN_HAND);
            ((DuckClientPlayerInteractionManager)(Object) minecraftClient.interactionManager).setBreakingBlock(false);
            ((DuckClientPlayerInteractionManager)(Object) minecraftClient.interactionManager).setBreakingProgress(0.0f);
        }
        this.blockPos = null;
        this.val = 0.0f;
        this.num = -1;
        this.arrayDeque.clear();
        this.stopwatch3.reset();
        this.speedMineHelper_2.reset();
    }

    public boolean isSafeToMine() {
        if (!minecraftClient.world.getBlockState(getMineTargetPos()).isOf(Blocks.OBSIDIAN)) {
            return true;
        }
        Box box = new Box(getMineTargetPos());
        boolean z = false;
        for (Entity entity : minecraftClient.world.getEntities()) {
            if ((entity instanceof PlayerEntity) && entity != minecraftClient.player && !z) {
                if (entity.getBoundingBox().stretch(0.0d, Double.longBitsToDouble(4607182418800017408L), 0.0d).expand(Double.longBitsToDouble(4607182418800017408L), 0.0d, Double.longBitsToDouble(4607182418800017408L)).intersects(box)) {
                    z = true;
                }
            } else if (entity instanceof EndCrystalEntity) {
                if (entity.getBoundingBox().expand(0.0d, MathConstants.val3, 0.0d).intersects(box)) {
                    return true;
                }
            } else {
                continue;
            }
        }
        if (z) {
            if (!this.stopwatch4.hasElapsedMs((long) (Float.intBitsToFloat(1148846080) / ClientServices.holeSnapRenderHelper.getTickRateFactor()))) {
                return false;
            }
        }
        return true;
    }

    public int get1050(BlockPos blockPos) {
        if (noGlitchBlocks.isToggled() && noGlitchBlocks.break_.getValue().booleanValue()) {
            return -1;
        }
        int i = InventoryUtils.getBestToolSlot(this.blockPos, !this.alternative.getValue().booleanValue());
        int i2 = minecraftClient.player.getInventory().selectedSlot;
        double d = WorldUtils.getBlockBreakingDelta(minecraftClient.player.getInventory().getStack(i), minecraftClient.world.getBlockState(blockPos), minecraftClient.player.isOnGround());
        if ((this.autoSwap.getValue() != SpeedMineMode_3.NONE || i == i2) && d > this.damage.getValue().floatValue()) {
            return i;
        }
        return -1;
    }

    public BlockPos getBreakingPos() {
        if (isToggled()) {
            return this.blockPos;
        }
        if (!((DuckClientPlayerInteractionManager)(Object) minecraftClient.interactionManager).isBreakingBlock() || ((DuckClientPlayerInteractionManager)(Object) minecraftClient.interactionManager).getBreakingProgress() < Double.longBitsToDouble(4606281698874543309L)) {
            return null;
        }
        return ((DuckClientPlayerInteractionManager)(Object) minecraftClient.interactionManager).getCurrentBreakingBlock();
    }

    public float getMiningProgress() {
        return MathHelper.clamp(this.val, 0.0f, Float.intBitsToFloat(1065353216));
    }

    public BlockPos getMineTargetPos() {
        return this.blockPos;
    }

    public BlockPos getRebreakTargetPos() {
        switch (this.rebreak2.getValue()) {
            case NONE:
                return null;
            case FAST:
                return this.blockPos2;
            case INSTANT:
                return this.blockPos;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
    }

    public Direction getInteractDirection(BlockPos blockPos) {
        List<Direction> list3031 = BlockPlacer.getInteractableSides(blockPos);
        return !list3031.isEmpty() ? list3031.get(0) : Direction.UP;
    }

    public boolean isBlockAllowed(BlockPos blockPos) {
        return this.mode.getValue().isAllowedBySetting(minecraftClient.world.getBlockState(blockPos).getBlock(), this.blocks);
    }

    public float getStrokeWidth() {
        return this.lineWidth.getValue().floatValue();
    }

    public SlotRestoreTask getSlotRestoreTask() {
        return this.speedMineRunnable;
    }

    public boolean hasCooldownElapsed(long j) {
        return this.stopwatch4.hasElapsedMs(j);
    }

    public SpeedMineTarget getSearchTarget() {
        return this.speedMineSearchHelper4;
    }

    public static boolean isSwapLocked() {
        return atomicBoolean.get();
    }
}
