package me.mioclient.module.combat;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.module.combat.bedaura.BedDetonationSelector;
import me.mioclient.module.combat.bedaura.BedPlacement;
import me.mioclient.module.combat.bedaura.BedPlacementSelector;
import me.mioclient.network.PacketSender;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.inventory.InventoryUtils;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/BedAura.class */
public final class BedAura extends Module {
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Setting<Boolean> place = add(new BooleanSetting("Place", true).setVisible());
    public Setting<Integer> delay2 = add(new NumberSetting<>("Delay", 1, 1, 20).requiresSettings(this.place));
    public Setting<Float> range2 = add(new NumberSetting<>("Range", 4.0f, 0.1f, 6.0f).requiresSettings(this.place));
    public Setting<Boolean> strictDirection = add(new BooleanSetting("StrictDirection", false).requiresSettings(this.place));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true).requiresSettings(this.place));
    public Setting<Boolean> liquidPlace = add(new BooleanSetting("LiquidPlace", false).requiresSettings(this.place));
    public Setting<Boolean> airPlace = add(new BooleanSetting("AirPlace", false).requiresSettings(this.place));
    public Setting<Boolean> break_ = add(new BooleanSetting("Break", true).setVisible());
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 1, 1, 20).requiresSettings(this.break_));
    public Setting<Float> range = add(new NumberSetting<>("Range", 4.0f, 0.1f, 6.0f).requiresSettings(this.break_));
    public Setting<Boolean> tPSSync = add(new BooleanSetting("TPSSync", false).requiresSettings(this.break_));
    public Setting<Boolean> instant = add(new BooleanSetting("Instant", true).requiresSettings(this.break_));
    public Setting<Boolean> extrapolation = add(new BooleanSetting("Extrapolation", false).setVisible());
    public Setting<Integer> ticks = add(new NumberSetting<>("Ticks", 3, 1, 8).requiresSettings(this.extrapolation));
    public Setting<Boolean> refill = add(new BooleanSetting("Refill", false).setVisible());
    public Setting<Integer> slot = add(new NumberSetting<>("Slot", 1, 1, 9).requiresSettings(this.refill));
    public Setting<Boolean> render = add(new BooleanSetting("Render", true).setVisible());
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(1136499199, true)).setThemeSync(true).requiresSettings(this.render));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(-910321153, true)).setThemeSync(true).requiresSettings(this.render));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", true).setVisible().requiresSettings(this.render));
    public Setting<Float> fadeTime = add(new NumberSetting<>("FadeTime", 0.5f, 0.1f, 1.0f).setSuffix("s").requiresSettings(this.render, this.fade));
    public Setting<Boolean> targeting = add(new BooleanSetting("Targeting", false).setDependent());
    public Setting<Float> minDamage = add(new NumberSetting<>("MinDamage", 6.0f, 0.0f, 12.0f).requiresSettings(this.targeting));
    public Setting<Float> maxSelfDamage = add(new NumberSetting<>("MaxSelfDamage", 10.0f, 0.0f, 20.0f).requiresSettings(this.targeting));
    public Setting<Boolean> antiSucide = add(new BooleanSetting("AntiSucide", true).requiresSettings(this.targeting));
    public final BedPlacementSelector bedAuraHelper;
    public final BedDetonationSelector bedAuraSearchHelper46;
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public PlayerEntity playerEntity;
    public BedPlacement bedAuraData;
    public BlockPos blockPos;

    public BedAura() {
        super("BedAura", new StringTemplate().arg(String.valueOf(Formatting.RED)).format("Blows your enemies up using beds. \n\u0001Doesn't work in the overworld."), Category.COMBAT, new String[0]);
        this.bedAuraHelper = new BedPlacementSelector(this);
        this.bedAuraSearchHelper46 = new BedDetonationSelector(this);
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        ClientServices.antiPhaseBlockHelper.register(new BlockHighlightRenderer.Record(this, this.fill, this.outline, () -> {
            return Float.valueOf(Float.intBitsToFloat(1065353216));
        }, this.fadeTime, () -> {
            return false;
        }, this.fade, 1000));
        this.range2.setConfigName("PlaceRange");
        this.range.setConfigName("BreakRange");
        this.delay2.setConfigName("PlaceDelay");
        this.delay.setConfigName("BreakDelay");
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (this.playerEntity == null) {
            return null;
        }
        return this.playerEntity.getGameProfile().getName();
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        this.playerEntity = getTargetPlayer();
        if (this.playerEntity == null) {
            this.bedAuraData = null;
        } else {
            this.bedAuraData = this.bedAuraHelper.selectTarget(this.playerEntity);
            this.blockPos = this.bedAuraSearchHelper46.selectTarget(this.playerEntity);
        }
        if (this.bedAuraData == null) {
            return;
        }
        refillBedSlot();
        if (this.break_.getValue().booleanValue()) {
            if (isDelayElapsed(this.stopwatch2, this.delay)) {
                detonateBed(this.blockPos);
            }
        }
        if (this.place.getValue().booleanValue()) {
            if (isDelayElapsed(this.stopwatch, this.delay2)) {
                placeBed(this.bedAuraData);
            }
        }
    }

    public PlayerEntity getTargetPlayer() {
        PlayerEntity playerEntity = null;
        float intBitsToFloat = Float.intBitsToFloat(1203982208);
        for (PlayerEntity playerEntity2 : minecraftClient.world.getPlayers()) {
            if (playerEntity2 != minecraftClient.player && !ClientServices.socialManager.isFriendEntity(playerEntity2)) {
                if (intBitsToFloat > minecraftClient.player.getEyePos().distanceTo(playerEntity2.getPos())) {
                    playerEntity = playerEntity2;
                    intBitsToFloat = (float) minecraftClient.player.getEyePos().distanceTo(playerEntity2.getPos());
                }
            }
        }
        return playerEntity;
    }

    public void placeBed(BedPlacement bedAuraData) {
        int i = InventoryUtils.findInHotbar(itemStack -> {
            return itemStack.getItem() instanceof BedItem;
        });
        int i2 = minecraftClient.player.getInventory().selectedSlot;
        if (i == -1) {
            return;
        }
        InventoryUtils.do438(i);
        do1422(bedAuraData);
        InventoryUtils.do438(i2);
    }

    public void do1422(BedPlacement bedAuraData) {
        float[] floatArray2486;
        if (bedAuraData != null && BlockPlacer.isSpaceFree(bedAuraData.getBedPos(), true)) {
            if (this.rotate.getValue().booleanValue()) {
                floatArray2486 = RotationManager.getRotationsTo(bedAuraData.getHitResult().getPos());
            } else {
                floatArray2486 = RotationManager.getRotations(bedAuraData.getBedPos().toCenterPos(), bedAuraData.getHeadPos().toCenterPos());
            }
            ClientServices.RotationManager.requestRotations(floatArray2486, 500);
            BlockHitResult blockHitResult157 = bedAuraData.getHitResult();
            ClientServices.antiPhaseHelper.withSneakPress(() -> {
                PacketSender.sendInteractBlock(Hand.MAIN_HAND, blockHitResult157);
            });
            minecraftClient.player.swingHand(Hand.MAIN_HAND);
            Box enclosing = Box.enclosing(bedAuraData.getBedPos(), bedAuraData.getHeadPos());
            Box expand = enclosing.withMaxY(enclosing.minY + Double.longBitsToDouble(4603241769126068224L)).expand(Double.longBitsToDouble(4566758108763783168L));
            if (this.render.getValue().booleanValue()) {
                ClientServices.antiPhaseBlockHelper.addHighlight(this, expand);
            }
            if (this.instant.getValue().booleanValue()) {
                ClientServices.antiPhaseHelper.withSneakRelease(() -> {
                    PacketSender.sendInteractBlock(Hand.MAIN_HAND, new BlockHitResult(bedAuraData.getBedPos().toCenterPos(), getInteractDirection(bedAuraData.getBedPos(), Direction.UP), bedAuraData.getBedPos(), false));
                    minecraftClient.player.swingHand(Hand.MAIN_HAND);
                });
            }
            this.stopwatch.reset();
        }
    }

    public void detonateBed(BlockPos blockPos) {
        if (blockPos == null) {
            return;
        }
        BlockHitResult blockHitResult = new BlockHitResult(blockPos.toCenterPos(), getInteractDirection(blockPos, Direction.UP), blockPos, false);
        ClientServices.antiPhaseHelper.withSneakRelease(() -> {
            minecraftClient.interactionManager.interactBlock(minecraftClient.player, Hand.MAIN_HAND, blockHitResult);
            minecraftClient.player.swingHand(Hand.MAIN_HAND);
        });
        Iterator it = Direction.Type.HORIZONTAL.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BlockPos offset = blockPos.offset((Direction) it.next());
            if (minecraftClient.world.getBlockState(offset).getBlock() instanceof BedBlock) {
                ClientServices.stashFinderHelper.breakBlockWithSnapshot(offset);
                break;
            }
        }
        ClientServices.stashFinderHelper.breakBlockWithSnapshot(blockPos);
        this.stopwatch2.reset();
    }

    public void refillBedSlot() {
        int i;
        if (this.refill.getValue().booleanValue()) {
            int intValue = this.slot.getValue().intValue() - 1;
            if ((minecraftClient.player.getInventory().getStack(intValue).getItem() instanceof BedItem) || (i = InventoryUtils.findInInventory(itemStack -> {
                return itemStack.getItem() instanceof BedItem;
            }, true)) == -1) {
                return;
            }
            InventoryUtils.swapSlots(i, InventoryUtils.toNetworkSlot(intValue));
        }
    }

    public Direction getInteractDirection(BlockPos blockPos, Direction direction) {
        List<Direction> list3031 = BlockPlacer.getInteractableSides(blockPos);
        if (!list3031.isEmpty()) {
            list3031.getFirst();
        }
        return direction;
    }

    public boolean isBedPartAt(BlockPos blockPos, BedPart bedPart) {
        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        return (blockState.getBlock() instanceof BedBlock) && blockState.get(BedBlock.PART) == bedPart;
    }

    public boolean isDelayElapsed(Stopwatch stopwatch, Setting<Integer> setting) {
        return stopwatch.hasElapsedMs((long) (((float) (50 * setting.getValue().intValue())) * (this.tPSSync.getValue().booleanValue() ? ClientServices.holeSnapRenderHelper.getTickRateFactor() : Float.intBitsToFloat(1065353216))));
    }

    public Box getPredictedBox(LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (this.extrapolation.getValue().booleanValue()) {
                return ClientServices.mainhandHelper.getPredictedBox(playerEntity, this.ticks.getValue().intValue());
            }
        }
        return livingEntity.getBoundingBox();
    }
}
