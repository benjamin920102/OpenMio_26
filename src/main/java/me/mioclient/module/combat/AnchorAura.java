package me.mioclient.module.combat;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.module.combat.anchoraura.AnchorPlacement;
import me.mioclient.module.combat.anchoraura.AnchorTarget;
import me.mioclient.network.PacketSender;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.PlayerUtils;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/AnchorAura.class */
public class AnchorAura extends Module {
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public static final Offhand offhand = (Offhand) ClientServices.moduleInjector.getModule(Offhand.class);
    public static final AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 50, 0, 1000).setSuffix("ms"));
    public Setting<Float> range = add(new NumberSetting<>("Range", 5.0f, 0.0f, 6.0f).setSuffix("m"));
    public Setting<Float> wallRange = add(new NumberSetting<>("WallRange", 5.0f, 0.0f, 6.0f).setSuffix("m"));
    public Setting<Float> targetRange = add(new NumberSetting<>("TargetRange", 6.0f, 1.0f, 16.0f).setSuffix("m"));
    public Setting<Float> minDamage = add(new NumberSetting<>("MinDamage", 6.0f, 0.1f, 20.0f).setSuffix("hp"));
    public Setting<Float> maxSelfDamage = add(new NumberSetting<>("MaxSelfDamage", 12.0f, 0.0f, 20.0f).setSuffix("hp"));
    public Setting<Boolean> antiSuicide = add(new BooleanSetting("AntiSuicide", true));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false));
    public Setting<Boolean> strictDirection = add(new BooleanSetting("StrictDirection", false));
    public Setting<Boolean> airPlace = add(new BooleanSetting("AirPlace", false));
    public Setting<Boolean> liquidPlace = add(new BooleanSetting("LiquidPlace", false));
    public Setting<Boolean> inhibit = add(new BooleanSetting("Inhibit", true));
    public Setting<Boolean> sequential = add(new BooleanSetting("Sequential", true));
    public Setting<Boolean> render = add(new BooleanSetting("Render", true).setVisible());
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(1136499199, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(-910321153, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", true).setVisible().requiresSettings(this.render));
    public Setting<Float> fadeTime = add(new NumberSetting<>("FadeTime", 1.0f, 0.1f, 1.0f).setSuffix("s").requiresSettings(this.render, this.fade));
    public Setting<Boolean> pause = add(new BooleanSetting("Pause", false).setDependent());
    public Setting<Boolean> mining = add(new BooleanSetting("Mining", false).requiresSettings(this.pause));
    public Setting<Boolean> eating = add(new BooleanSetting("Eating", false).requiresSettings(this.pause));
    public Setting<Float> health = add(new NumberSetting<>("Health", 4.0f, 0.0f, 36.0f).setSuffix("hp").requiresSettings(this.pause));
    public CompletableFuture<AnchorPlacement> completableFuture;
    public final BlockPos.Mutable mutable;
    public final Stopwatch stopwatch;
    public final AtomicReference<AnchorPlacement> atomicReference;
    public AnchorTarget autoCrystalData_2;

    public AnchorAura() {
        super("AnchorAura", new StringTemplate().arg(String.valueOf(Formatting.RED)).format("Explodes respawn anchors to kill players. \n\u0001Doesn't work in the nether."), Category.COMBAT, new String[0]);
        this.mutable = new BlockPos.Mutable();
        this.stopwatch = new Stopwatch();
        this.atomicReference = new AtomicReference<>();
        ClientServices.antiPhaseBlockHelper.register(new BlockHighlightRenderer.Record(this, this.fill, this.outline, () -> {
            return Float.valueOf(Float.intBitsToFloat(1065353216));
        }, this.fadeTime, () -> {
            return false;
        }, this.fade, 450));
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.autoCrystalData_2 = null;
        if (this.completableFuture != null) {
            try {
                this.completableFuture.complete(null);
            } catch (CancellationException e) {
            }
        }
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.atomicReference.set(null);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (this.autoCrystalData_2 != null) {
            return "%s, %.1f".formatted(this.autoCrystalData_2.getAnchorPlayer().getName().getString(), Double.valueOf(this.autoCrystalData_2.getDamage()));
        }
        return null;
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        AnchorPlacement anchorAuraData;
        if (minecraftClient.world.getDimension().respawnAnchorWorks()) {
            ChatUtils.sendPrefixedMessage(Text.of("Doesn't work in the nether!"), ChatUtils.createSignature(-1), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
            disable();
            return;
        }
        tickTargetSearch();
        if (is929() || (anchorAuraData = this.atomicReference.get()) == null || getHand() == null) {
            return;
        }
        BlockPos blockPos12 = anchorAuraData.getBlockPos();
        BlockState blockState = minecraftClient.world.getBlockState(blockPos12);
        int i = minecraftClient.player.getInventory().selectedSlot;
        if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            Direction direction2870 = getInteractableSide(blockPos12);
            if (direction2870 == null) {
                return;
            }
            if (!this.stopwatch.hasElapsedMs(this.delay.getValue().intValue() + ClientServices.holeSnapRenderHelper.getPing())) {
                return;
            }
            if (((Integer) blockState.get(RespawnAnchorBlock.CHARGES)).intValue() == 0) {
                do2866(blockPos12, direction2870, i);
            }
            if (this.rotate.getValue().booleanValue()) {
                ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(blockPos12.toCenterPos()), 100);
            }
            sendAnchorInteraction(blockPos12, direction2870, Vec3d.ofCenter((Vec3i) blockPos12), Hand.OFF_HAND);
            if (this.inhibit.getValue().booleanValue()) {
                minecraftClient.world.setBlockState(blockPos12, Blocks.AIR.getDefaultState());
            }
        }
        placeAnchor(blockPos12, anchorAuraData.getDirection(), getHand());
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (!this.sequential.getValue().booleanValue() || this.atomicReference.get() == null) {
            return;
        }
        BlockUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof BlockUpdateS2CPacket ? (BlockUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof BlockUpdateS2CPacket) {
            BlockUpdateS2CPacket blockUpdateS2CPacket = packet904;
            if (this.autoCrystalData_2 != null && blockUpdateS2CPacket.getState().isAir() && blockUpdateS2CPacket.getPos().equals(this.atomicReference.get().getBlockPos())) {
                placeAnchor(this.atomicReference.get().getBlockPos(), this.atomicReference.get().getDirection(), getHand());
            }
        }
    }

    public void do2866(BlockPos blockPos, Direction direction, int i) {
        Vec3d offset = blockPos.toCenterPos().offset(direction, MathConstants.val2);
        Hand hand450 = InventoryUtils.getHandWith(Items.GLOWSTONE);
        if (this.rotate.getValue().booleanValue()) {
            ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(offset), 100);
        }
        if (hand450 != null) {
            sendAnchorInteraction(blockPos, direction, offset, Hand.MAIN_HAND);
        } else {
            int i2 = InventoryUtils.findInHotbar(Items.GLOWSTONE);
            if (i2 != -1) {
                InventoryUtils.selectSlot(i2);
                sendAnchorInteraction(blockPos, direction, offset, Hand.MAIN_HAND);
                InventoryUtils.selectSlot(i);
            }
        }
        this.stopwatch.reset();
    }

    public void placeAnchor(BlockPos blockPos, Direction direction, Hand hand) {
        if (isRespawnAnchor(blockPos)) {
            return;
        }
        int i = minecraftClient.player.getInventory().selectedSlot;
        int i2 = InventoryUtils.findInHotbar(Items.RESPAWN_ANCHOR);
        boolean z = BlockPlacer.findSupportSide(blockPos) == null && antiCheat.isF2b2t() && this.airPlace.getValue().booleanValue();
        if (i2 == -1 && hand == Hand.MAIN_HAND) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            ClientServices.RotationManager.requestRotations(RotationManager.getRotationsToSide(blockPos.toCenterPos(), direction), 100);
        }
        boolean z2 = !minecraftClient.player.getMainHandStack().isOf(Items.RESPAWN_ANCHOR);
        if (z2) {
            InventoryUtils.selectSlot(i2);
        }
        if (z) {
            hand = PlayerUtils.getOppositeHand(hand);
            PacketSender.sendSwapOffhand();
        }
        if (BlockPlacer.placeBlockPacket(blockPos, getVec3d2871(blockPos.offset(direction)), direction, this.airPlace.getValue().booleanValue(), hand) && this.render.getValue().booleanValue()) {
            ClientServices.antiPhaseBlockHelper.addBlockHighlight(this, blockPos);
        }
        if (z) {
            PacketSender.sendSwapOffhand();
        }
        if (z2) {
            InventoryUtils.selectSlot(i);
        }
    }

    public void sendAnchorInteraction(BlockPos blockPos, Direction direction, Vec3d vec3d, Hand hand) {
        ClientServices.antiPhaseHelper.withSneakRelease(() -> {
            PacketSender.sendInteractBlock(Hand.MAIN_HAND, new BlockHitResult(vec3d, direction, blockPos, false));
            PacketSender.sendHandSwing(hand);
        });
    }

    public void tickTargetSearch() {
        if (this.atomicReference.get() != null && shouldSkipPosition(this.atomicReference.get().getBlockPos())) {
            this.atomicReference.set(null);
        }
        if (this.completableFuture != null && this.completableFuture.isDone() && this.atomicReference.get() == null) {
            try {
                AnchorPlacement anchorAuraData = this.completableFuture.get();
                if (anchorAuraData == null) {
                    this.atomicReference.set(null);
                } else {
                    this.atomicReference.set(anchorAuraData);
                    this.autoCrystalData_2 = anchorAuraData.getTarget();
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (this.completableFuture == null || this.completableFuture.isDone() || this.completableFuture.isCancelled()) {
            if (this.autoCrystalData_2 != null) {
                if (AnchorTarget.isInvalidTarget(this.autoCrystalData_2.getAnchorPlayer(), this.targetRange.getValue().floatValue())) {
                    this.autoCrystalData_2 = null;
                }
            }
            this.completableFuture = CompletableFuture.supplyAsync(this::findBestAnchorPlacement, executorService).exceptionally(th -> {
                return null;
            });
        }
    }

    public Direction getInteractableSide(BlockPos blockPos) {
        List<Direction> list3031 = BlockPlacer.getInteractableSides(blockPos);
        Direction direction = this.strictDirection.getValue().booleanValue() ? null : Direction.DOWN;
        if (!list3031.isEmpty()) {
            direction = list3031.get(0);
        }
        return direction;
    }

    public Vec3d getVec3d2871(BlockPos blockPos) {
        return AutoCraftMode.X8.getWorldCorners(blockPos).stream().filter(vec3d -> {
            return WorldUtils.isPositionVisible(vec3d) || minecraftClient.player.getEyePos().distanceTo(vec3d) <= ((double) this.wallRange.getValue().floatValue());
        }).min(Comparator.comparing(vec3d2 -> {
            return Double.valueOf(minecraftClient.player.getEyePos().distanceTo(vec3d2));
        })).orElse(blockPos.toCenterPos());
    }

    public AnchorPlacement findBestAnchorPlacement() {
        AnchorPlacement anchorAuraData = null;
        double d = -Math.floor(this.range.getValue().floatValue());
        while (true) {
            double d2 = d;
            if (d2 >= Math.ceil(this.range.getValue().floatValue())) {
                return anchorAuraData;
            }
            double d3 = -Math.floor(this.range.getValue().floatValue());
            while (true) {
                double d4 = d3;
                if (d4 < Math.ceil(this.range.getValue().floatValue())) {
                    double d5 = -Math.floor(this.range.getValue().floatValue());
                    while (true) {
                        double d6 = d5;
                        if (d6 < Math.ceil(this.range.getValue().floatValue())) {
                            this.mutable.set(minecraftClient.player.getX() + d2, minecraftClient.player.getEyeY() + d4, minecraftClient.player.getZ() + d6);
                            if ((BlockPlacer.canPlaceAt(this.mutable, Blocks.RESPAWN_ANCHOR, true, false) || isRespawnAnchor(this.mutable)) && this.mutable.getY() >= minecraftClient.world.getBottomY() && this.mutable.getY() <= minecraftClient.world.getTopY()) {
                                boolean isAnyPositionVisible = WorldUtils.isAnyPositionVisible(AutoCraftMode.X8.getWorldCorners(this.mutable));
                                double distanceTo = minecraftClient.player.getEyePos().distanceTo(this.mutable.toCenterPos());
                                if ((isAnyPositionVisible || distanceTo <= this.wallRange.getValue().floatValue()) && distanceTo <= this.range.getValue().floatValue()) {
                                    Direction direction3030 = BlockPlacer.findSupportSide(this.mutable, this.strictDirection.getValue().booleanValue(), this.wallRange.getValue().floatValue() <= 0.0f);
                                    AnchorTarget autoCrystalData_22873 = getAnchorTarget(this.mutable);
                                    if ((direction3030 == null && this.airPlace.getValue().booleanValue()) || isRespawnAnchor(this.mutable)) {
                                        direction3030 = Direction.DOWN;
                                    }
                                    if (direction3030 == null) {
                                        if (!minecraftClient.world.getFluidState(this.mutable).isEmpty() && this.liquidPlace.getValue().booleanValue()) {
                                            direction3030 = Direction.DOWN;
                                        }
                                    }
                                    if (direction3030 != null && autoCrystalData_22873 != null && !shouldSkipForDamage(this.mutable, autoCrystalData_22873)) {
                                        AnchorPlacement anchorAuraData2 = new AnchorPlacement(this.mutable.toImmutable(), direction3030, autoCrystalData_22873);
                                        if (isRespawnAnchor(this.mutable)) {
                                            return anchorAuraData2;
                                        }
                                        anchorAuraData = anchorAuraData2.preferOver(anchorAuraData);
                                    }
                                }
                            }
                            d5 = d6 + Double.longBitsToDouble(4607182418800017408L);
                        } else {
                            break;
                        }
                    }
                } else {
                    break;
                }
                d3 = d4 + Double.longBitsToDouble(4607182418800017408L);
            }
            d = d2 + Double.longBitsToDouble(4607182418800017408L);
        }
    }

    public AnchorTarget getAnchorTarget(BlockPos blockPos) {
        AnchorTarget autoCrystalData_2 = null;
        for (LivingEntity livingEntity : minecraftClient.world.getPlayers()) {
            if (!AnchorTarget.isInvalidTarget((PlayerEntity) livingEntity, this.targetRange.getValue().floatValue())) {
                double d = ExplosionDamageCalculator.getExplosionDamage(blockPos.toCenterPos(), livingEntity, ((PlayerEntity) livingEntity).getBoundingBox(), Double.longBitsToDouble(4617315517961601024L), true, isRespawnAnchor(blockPos) ? blockPos : null, (BlockPos) null);
                if (this.minDamage.getValue().floatValue() <= d && d >= MathConstants.val2 && (autoCrystalData_2 == null || d > autoCrystalData_2.getDamage())) {
                    autoCrystalData_2 = new AnchorTarget((PlayerEntity) livingEntity, d);
                }
            }
        }
        return autoCrystalData_2;
    }

    public Hand getHand() {
        boolean z = offhand.isToggled() && offhand.item.getValue().getOffhandItem(false) == Items.RESPAWN_ANCHOR;
        Hand hand450 = InventoryUtils.getHandWith(Items.RESPAWN_ANCHOR);
        if (z && hand450 == null) {
            return null;
        }
        return z ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    public boolean shouldSkipForDamage(BlockPos blockPos, AnchorTarget autoCrystalData_2) {
        if (autoCrystalData_2 == null) {
            return true;
        }
        boolean isRespawnAnchor = isRespawnAnchor(blockPos);
        double d = ExplosionDamageCalculator.getExplosionDamage(Vec3d.ofCenter((Vec3i) blockPos), minecraftClient.player, minecraftClient.player.getBoundingBox(), Double.longBitsToDouble(4617315517961601024L), true, isRespawnAnchor(blockPos) ? blockPos : null, (BlockPos) null);
        boolean z = autoCrystalData_2.getDamage() >= ((double) EntityUtil.getEntityHealth(autoCrystalData_2.getAnchorPlayer()));
        double d2 = ExplosionDamageCalculator.getExplosionDamage(Vec3d.ofCenter((Vec3i) blockPos), autoCrystalData_2.getAnchorPlayer(), autoCrystalData_2.getAnchorPlayer().getBoundingBox(), Double.longBitsToDouble(4617315517961601024L), true, isRespawnAnchor(blockPos) ? blockPos : null, (BlockPos) null);
        boolean z2 = (!z && d >= ((double) this.maxSelfDamage.getValue().floatValue())) || (d >= ((double) EntityUtil.getPlayerHealth()) && this.antiSuicide.getValue().booleanValue());
        if (isRespawnAnchor && !z2) {
            return false;
        }
        if (this.minDamage.getValue().floatValue() > d2 || d2 < MathConstants.val2) {
            return true;
        }
        return z2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0146, code lost:
    
        if (me.mioclient.util.world.BlockPlacer.findSupportSide(r6, r5.strictDirection.getValue().booleanValue(), r5.wallRange.getValue().floatValue() <= 0.0f) == null) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean shouldSkipPosition(BlockPos blockPos) {
        double floatValue = (!WorldUtils.isAnyPositionVisible(AutoCraftMode.X8.getWorldCorners(blockPos)) ? this.wallRange.getValue() : this.range.getValue()).floatValue();
        if (shouldSkipForDamage(blockPos, getAnchorTarget(blockPos))) {
            return true;
        }
        if (minecraftClient.player.getEyePos().distanceTo(blockPos.toCenterPos()) > floatValue) {
            return true;
        }
        if (minecraftClient.world.getBlockState(blockPos).isAir()) {
            if (getInteractableSide(blockPos) != null) {
            }
            if (!this.airPlace.getValue().booleanValue()) {
                return true;
            }
        }
        return (BlockPlacer.canPlaceAt(blockPos, Blocks.RESPAWN_ANCHOR, true, false) || isRespawnAnchor(blockPos)) ? false : true;
    }

    public boolean isRespawnAnchor(BlockPos blockPos) {
        return minecraftClient.world.getBlockState(blockPos).isOf(Blocks.RESPAWN_ANCHOR);
    }

    public boolean is929() {
        if (isNotInGame() || minecraftClient.player.isSleeping()) {
            return true;
        }
        if (minecraftClient.player.isUsingItem() && this.eating.getValue().booleanValue()) {
            return true;
        }
        if (minecraftClient.interactionManager.isBreakingBlock() && this.mining.getValue().booleanValue()) {
            return true;
        }
        return (autoCrystal.isToggled() && autoCrystal.atomicReference.get() != null && this.autoCrystalData_2 != null && autoCrystal.atomicReference.get().getDamage() > this.autoCrystalData_2.getDamage()) || EntityUtil.getPlayerHealth() < this.health.getValue().floatValue();
    }
}
