package me.mioclient.module.combat.automine;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.module.combat.automine.AutoMineLogic;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.module.combat.automine.MineStrategyBase;
import me.mioclient.module.combat.automine.MineTargetState;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.player.PlayerUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.combat.AutoMine;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import me.mioclient.module.combat.automine.AutoMineLogic;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoMineSearchHelper42_3.class */
public final class FeetTargetStrategy extends MineStrategyBase {
    public static AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public static SpeedMine speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
    public static final List<Vec3i> list = List.of(new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(0, 1, 0), new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0));

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/AutoMineSearchHelper42_3$Record.class */
    public static final class Record {
        public final BlockPos blockPos;
        public final BlockPos blockPos2;

        public Record(BlockPos blockPos, BlockPos blockPos2) {
            this.blockPos = blockPos;
            this.blockPos2 = blockPos2;
        }




        public BlockPos getTargetBlockPos() {
            return this.blockPos;
        }

        public BlockPos getSecondaryBlockPos() {
            return this.blockPos2;
        }
    }

    public FeetTargetStrategy(AutoMine autoMine) {
        super(autoMine);
    }

    @Override // me.mioclient.module.combat.automine.MineTargetStrategy
    public void findTargets(MineTargetState autoMineHelper) {
        LivingEntity playerEntity886 = this.autoMine.autoMineSearchHelper4.getTargetPlayer();
        boolean z = this.autoMine.fish.getValue().booleanValue() && autoMineHelper.getPriority() == 400 && autoMineHelper.getSecondaryBlockPos() != null;
        if (playerEntity886 == null || isSpaceBelowEmpty((Entity) playerEntity886)) {
            return;
        }
        if (!is1210((PlayerEntity) playerEntity886) || z) {
            BlockPos ofFloored = BlockPos.ofFloored(((PlayerEntity) playerEntity886).getPos());
            List<BlockPos> list2010 = PlayerUtils.getSurroundPositions(playerEntity886);
            Block block = minecraftClient.world.getBlockState(ofFloored).getBlock();
            boolean z2 = block.getBlastResistance() >= Float.intBitsToFloat(1142292480) && block != Blocks.AIR;
            if (!this.autoMine.speedMineHelper.is1228(500L) || ofFloored.equals(this.autoMine.speedMineHelper.getBlockPos())) {
                z2 = false;
            }
            Record record1207 = getRecord1207(list2010, z2, z);
            if (record1207.blockPos == null) {
                return;
            }
            autoMineHelper.tryUpdatePriority(600, autoMineHelper2 -> {
                if (autoMineHelper2.getSecondaryBlockPos() == null) {
                    autoMineHelper2.setSecondaryBlockPos(record1207.blockPos2);
                }
                autoMineHelper2.setBlockPos(record1207.blockPos);
            });
        }
    }

    public Record getRecord1207(List<BlockPos> list2, boolean z, boolean z2) {
        BlockPos blockPos = null;
        BlockPos blockPos2 = null;
        boolean z3 = false;
        list2.sort(Comparator.comparing(blockPos3 -> {
            if (minecraftClient.player.getBoundingBox().intersects(new Box(blockPos3))) {
                return Double.valueOf(Double.longBitsToDouble(-4556649414143246336L));
            }
            if (PlayerUtils.getSurroundPositions(minecraftClient.player).contains(blockPos3)) {
                return Double.valueOf(Double.longBitsToDouble(-4571373524106608640L));
            }
            boolean z4 = false;
            Iterator<Vec3i> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Vec3i next = it.next();
                if (next.getY() == 0) {
                    if (isCrystalPlaceableAt(blockPos3.add(next).down())) {
                        z4 = true;
                        break;
                    }
                }
            }
            double squaredDistance = blockPos3.getSquaredDistance(minecraftClient.player.getPos());
            if (!z4) {
                squaredDistance += Math.pow(speedmine.range.getMaxValue().floatValue(), Double.longBitsToDouble(4611686018427387904L));
            }
            return Double.valueOf(-squaredDistance);
        }));
        Iterator<BlockPos> it = list2.iterator();
        while (it.hasNext()) {
            BlockPos next = it.next();
            if (z2) {
                next = next.down();
            }
            boolean z4 = WorldUtils.isAir(next) && next.equals(speedmine.getMineTargetPos());
            boolean z5 = this.autoMine.logic.getValue() == AutoMineLogic.GRIM || this.autoMine.logic.getValue() == AutoMineLogic.GRIMV3;
            if (z) {
                z4 = false;
            }
            if (speedmine.hasCooldownElapsed(400L) && z5) {
                z4 = false;
            }
            if (is1208(next) || z2 || z5) {
                if (isValidMineTarget(next) || z4) {
                    if (isSpeedMineTarget(next)) {
                        blockPos = next;
                        z3 = true;
                    } else if (z3) {
                        blockPos2 = next;
                    } else {
                        blockPos2 = blockPos;
                        blockPos = next;
                    }
                }
            }
        }
        return new Record(blockPos, blockPos2);
    }

    public boolean is1208(BlockPos blockPos) {
        Iterator<Vec3i> it = list.iterator();
        while (it.hasNext()) {
            if (isCrystalPlaceableAt(blockPos.add(it.next()).down())) {
                return true;
            }
        }
        return false;
    }

    public boolean is1209(BlockPos blockPos) {
        if (this.autoMine.safe.getValue().booleanValue()) {
            Iterator<BlockPos> it = PlayerUtils.getSurroundPositions(minecraftClient.player).iterator();
            while (it.hasNext()) {
                if (it.next().equals(blockPos)) {
                    return true;
                }
            }
        }
        List list2 = BlockPos.stream(minecraftClient.player.getBoundingBox().withMaxY(minecraftClient.player.getY())).map((v0) -> {
            return v0.toImmutable();
        }).filter(blockPos2 -> {
            return minecraftClient.world.getBlockState(blockPos2).getBlock().getBlastResistance() >= Float.intBitsToFloat(1142292480);
        }).toList();
        return list2.size() == 1 && list2.contains(blockPos);
    }

    public boolean is1210(PlayerEntity playerEntity) {
        return BlockPos.stream(playerEntity.getBoundingBox().withMaxY(playerEntity.getY())).map((v0) -> {
            return v0.toImmutable();
        }).allMatch(blockPos -> {
            return minecraftClient.world.getBlockState(blockPos).isOf(Blocks.BEDROCK);
        });
    }

    public boolean isCrystalPlaceableAt(BlockPos blockPos) {
        return BlockPlacer.isCrystalPlaceable(blockPos, autoCrystal.f112.getValue().booleanValue(), true, false, false, autoCrystal.lowerHitBox.getValue().booleanValue(), autoCrystal.hBFix.getValue().booleanValue());
    }

    @Override // me.mioclient.module.combat.automine.MineStrategyBase, me.mioclient.module.combat.automine.MineTargetStrategy
    public boolean isEnabled() {
        return this.autoMine.feet.getValue().booleanValue();
    }

    @Override // me.mioclient.module.combat.automine.MineStrategyBase
    public boolean isValidMineTarget(BlockPos blockPos) {
        if (is1209(blockPos) || WorldUtils.getBlock(blockPos) == Blocks.COBWEB) {
            return false;
        }
        return super.isValidMineTarget(blockPos);
    }
}
