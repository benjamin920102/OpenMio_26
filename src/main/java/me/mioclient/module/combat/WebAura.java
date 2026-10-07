package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.manager.PlayerHitboxTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Delay;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Vec3i;
import me.mioclient.module.Module;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

public class WebAura extends Delay {
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
    public Setting<Float> range = add(new NumberSetting<>("Range", 5.0f, 0.0f, 6.0f).setSuffix("m"));
    public Setting<Boolean> multiTask = add(new BooleanSetting("MultiTask", true));
    public Setting<Boolean> ignoreNakeds = add(new BooleanSetting("IgnoreNakeds", false));
    public Setting<Boolean> onlyFish = add(new BooleanSetting("OnlyFish", false));
    public Setting<Boolean> selfSafety = add(new BooleanSetting("SelfSafety", false));
    public Setting<Boolean> extrapolation = add(new BooleanSetting("Extrapolation", true).setVisible());
    public Setting<Integer> ticks = add(new NumberSetting<>("Ticks", 4, 0, 8).setHoleSnapConfig("Auto", HoleSnapMode.MIN).requiresSettings(this.extrapolation));
    public Setting<Boolean> positions = add(new BooleanSetting("Positions", false).setDependent());
    public Setting<Boolean> head = add(new BooleanSetting("Head", false).requiresSettings(this.positions));
    public Setting<Boolean> legs = add(new BooleanSetting("Legs", true).requiresSettings(this.positions));
    public Player playerEntity;

    public WebAura() {
        super("WebAura", "Traps your enemies with cobwebs.", Category.COMBAT, "autoweb");
        this.setting9.setValue(false);
        this.setting7.setValue(false);
        unregister((Setting<?>) this.setting8);
        unregister((Setting<?>) this.setting9);
        unregister((Setting<?>) this.setting10);
        unregister((Setting<?>) this.setting11);
        unregister((Setting<?>) this.setting7);
    }

    @Override // me.mioclient.module.Delay
    public boolean isPlayerFellTooFar() {
        return false;
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (this.playerEntity != null) {
            return this.playerEntity.getGameProfile().name();
        }
        return null;
    }

    @Override // me.mioclient.module.Delay
    public List<BlockPos> getPlacePositions() {
        this.playerEntity = getTargetPlayer();
        if (this.playerEntity == null || (!this.multiTask.getValue().booleanValue() && minecraftClient.player.isUsingItem())) {
            return Collections.emptyList();
        }
        ArrayList<BlockPos> arrayList = new ArrayList<>();
        AABB boundingBox = minecraftClient.player.getBoundingBox();
        if (this.extrapolation.getValue().booleanValue()) {
            PlayerHitboxTracker mainhandHelper = ClientServices.mainhandHelper;
            boundingBox = mainhandHelper.getPredictedBox(this.playerEntity, BoxMath.getSnapDivisions(this.playerEntity, this.ticks));
        }
        BlockPos ofFloored = BlockPos.containing(BoxMath.getBottomCenter(boundingBox));
        if (this.legs.getValue().booleanValue() && boundingBox.minY - ofFloored.getY() <= MathConstants.val2) {
            arrayList.add(ofFloored.below());
            arrayList.add(ofFloored);
        }
        if (this.head.getValue().booleanValue()) {
            arrayList.add(ofFloored.above());
        }
        arrayList.removeIf(blockPos -> {
            return (minecraftClient.level.getBlockState(blockPos).canBeReplaced() && minecraftClient.level.getFluidState(blockPos).isEmpty() && isSafeToPlace(blockPos) && (WorldUtils.isPositionVisible((Vec3i) blockPos) || !this.setting5.getValue().booleanValue())) ? false : true;
        });
        return arrayList;
    }

    @Override // me.mioclient.module.Delay
    public int getSlotIndex() {
        return InventoryUtils.findInHotbar(Items.COBWEB);
    }

    @Override // me.mioclient.module.Delay
    public void onBlockPlaced(BlockPos blockPos, boolean z) {
        SpeedMine speedMine_lcl = getSpeedMine();
        if (speedMine_lcl != null && blockPos.equals(speedMine_lcl.getRebreakTargetPos())) {
            speedMine_lcl.cancelAndReset();
        }
    }

    public boolean isSafeToPlace(BlockPos blockPos) {
        if (!this.selfSafety.getValue().booleanValue()) {
            return true;
        }
        AABB box = new AABB(blockPos);
        boolean intersects = minecraftClient.player.getBoundingBox().intersects(box);
        if (this.extrapolation.getValue().booleanValue()) {
            int intValue = this.ticks.getValue().intValue();
            while (true) {
                if (intValue < 0) {
                    break;
                }
                if (ClientServices.mainhandHelper.getPredictedBox(minecraftClient.player, BoxMath.getSnapDivisions(minecraftClient.player, this.ticks)).intersects(box)) {
                    intersects = true;
                    break;
                }
                intValue--;
            }
        }
        return !intersects;
    }

    public Player getTargetPlayer() {
        return (Player) minecraftClient.level.players().stream().filter(abstractClientPlayerEntity -> {
            if ((!this.onlyFish.getValue().booleanValue() || abstractClientPlayerEntity.isSwimming()) && abstractClientPlayerEntity != minecraftClient.player && !Objects.equals(abstractClientPlayerEntity.getName().getString(), minecraftClient.player.getGameProfile().name())) {
                if (!ClientServices.socialManager.isFriend(abstractClientPlayerEntity.getName().getString()) && abstractClientPlayerEntity.isAlive() && !abstractClientPlayerEntity.isRemoved() && ((!this.ignoreNakeds.getValue().booleanValue() || PlayerUtils.hasArmor((LivingEntity) abstractClientPlayerEntity)) && abstractClientPlayerEntity.distanceTo(minecraftClient.player) <= this.range.getValue().floatValue())) {
                    return true;
                }
            }
            return false;
        }).min(Comparator.comparing(abstractClientPlayerEntity2 -> {
            return Double.valueOf(abstractClientPlayerEntity2.distanceToSqr(minecraftClient.player));
        })).orElse(null);
    }
}