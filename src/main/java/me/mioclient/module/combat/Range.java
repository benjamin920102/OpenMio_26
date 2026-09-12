package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.command.FakePlayerCommand;
import me.mioclient.setting.NumberSetting;
import me.mioclient.manager.RotationManager;
import me.mioclient.ducks.DuckEndCrystalEntity;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.ChunkDeltaUpdateEvent;
import me.mioclient.event.Listen;
import me.mioclient.mixin.ducks.DuckLivingEntity;
import me.mioclient.module.Delay;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import me.mioclient.module.Module;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/Range.class */
public class Range extends Delay {
    public Setting<Float> setting;
    public Setting<Boolean> setting2;
    public Setting<Boolean> setting3;
    public Setting<Boolean> setting4;
    public Setting<Boolean> setting5;
    public Setting<Boolean> setting6;
    public Setting<Boolean> setting7;
    public Setting<Boolean> setting8;
    public Setting<Boolean> setting9;
    public PlayerEntity playerEntity;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/Range$Record.class */
    private static final class Record {
        public final BlockPos blockPos;
        public final BlockState blockState;

        public Record(BlockPos blockPos, BlockState blockState) {
            this.blockPos = blockPos;
            this.blockState = blockState;
        }




        public BlockPos getBlockPos() {
            return this.blockPos;
        }

        public BlockState getBlockState() {
            return this.blockState;
        }
    }

    public Range(String str, String str2, Category category) {
        super(str, str2, category, new String[0]);
        this.setting = add(new NumberSetting("Range", Float.valueOf(Float.intBitsToFloat(1084227584)), Float.valueOf(Float.intBitsToFloat(1065353216)), Float.valueOf(Float.intBitsToFloat(1090519040))).setSuffix("m"));
        this.setting2 = add(new BooleanSetting("Sequential", false));
        this.setting3 = add(new BooleanSetting("Targets", true).setDependent());
        this.setting4 = add(new BooleanSetting("Head", true).requiresSettings(this.setting3));
        this.setting5 = add(new BooleanSetting("Face", true).requiresSettings(this.setting3));
        this.setting6 = add(new BooleanSetting("Legs", false).requiresSettings(this.setting3));
        this.setting7 = add(new BooleanSetting("AntiStep", false));
        this.setting8 = add(new BooleanSetting("IgnoreNakeds", false));
        this.setting9 = add(new BooleanSetting("OnlyObby", false), this.setting5);
    }

    public Range() {
        super("AutoTrap", "Traps your enemies with obby.", Category.COMBAT, new String[0]);
        this.setting = add(new NumberSetting("Range", Float.valueOf(Float.intBitsToFloat(1084227584)), Float.valueOf(Float.intBitsToFloat((((1333017453 | 593773) - 2926) + 1) ^ 1895642112)), Float.valueOf(Float.intBitsToFloat(1090519040))).setSuffix("m"));
        this.setting2 = add(new BooleanSetting("Sequential", false));
        this.setting3 = add(new BooleanSetting("Targets", true).setDependent());
        this.setting4 = add(new BooleanSetting("Head", true).requiresSettings(this.setting3));
        this.setting5 = add(new BooleanSetting("Face", true).requiresSettings(this.setting3));
        this.setting6 = add(new BooleanSetting("Legs", false).requiresSettings(this.setting3));
        this.setting7 = add(new BooleanSetting("AntiStep", false));
        this.setting8 = add(new BooleanSetting("IgnoreNakeds", false));
        this.setting9 = add(new BooleanSetting("OnlyObby", false), this.setting5);
        setPlacementEnabled(true);
        do1335();
        this.setting9.setDefaultValue(false);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (this.playerEntity != null) {
            return this.playerEntity.getGameProfile().getName();
        }
        return null;
    }

    @Override // me.mioclient.module.Delay
    public List<BlockPos> getPlacePositions() {
        this.playerEntity = getTargetPlayer();
        if (this.playerEntity == null || Math.hypot(this.playerEntity.getX() - this.playerEntity.prevX, this.playerEntity.getZ() - this.playerEntity.prevZ) * Double.longBitsToDouble(4626322717216342016L) * Double.longBitsToDouble(4615288898129284301L) > Double.longBitsToDouble(4626322717216342016L)) {
            return Collections.emptyList();
        }
        if (Math.abs(this.playerEntity.prevY - this.playerEntity.getY()) > Double.longBitsToDouble(4603579539098121011L)) {
            return Collections.emptyList();
        }
        Position vec3d885 = getTargetPos(this.playerEntity);
        List<BlockPos> list877 = collectSurroundPositions(new ArrayList(), BlockPos.ofFloored(vec3d885), 0);
        list877.removeIf(this::isPositionInvalid);
        list877.sort(Comparator.comparing(blockPos -> {
            return Integer.valueOf(-blockPos.getY());
        }));
        if (this.setting5.getValue().booleanValue()) {
            list877.sort(Comparator.comparing(blockPos2 -> {
                return Double.valueOf(-minecraftClient.player.getPos().distanceTo(blockPos2.toCenterPos()));
            }));
        }
        return list877;
    }

    @Override // me.mioclient.module.Delay
    public int getSlotIndex() {
        return this.setting9.getValue().booleanValue() ? InventoryUtils.findInHotbar(Items.OBSIDIAN) : super.getSlotIndex();
    }

    @Listen
    public void onChunkDeltaUpdate(ChunkDeltaUpdateEvent chunkDeltaUpdateEvent) {
        if (this.setting2.getValue().booleanValue()) {
            do1323();
        }
    }

    public List<BlockPos> collectSurroundPositions(List<Integer> list, BlockPos blockPos, int i) {
        BlockPos blockPos878;
        ArrayList arrayList = new ArrayList();
        list.add(Integer.valueOf(blockPos.hashCode()));
        for (Vec3d vec3d : getList882(blockPos, i, this.playerEntity.getHeight())) {
            BlockPos add = blockPos.add((int) vec3d.x, (int) vec3d.y, (int) vec3d.z);
            if (minecraftClient.world.getBlockState(add).isReplaceable()) {
                if (!minecraftClient.world.getEntitiesByClass(PlayerEntity.class, new Box(add), playerEntity -> {
                    return true;
                }).isEmpty()) {
                    if (!list.contains(Integer.valueOf(add.hashCode())) && vec3d.y == i) {
                        arrayList.addAll(collectSurroundPositions(list, add, i));
                    }
                }
                if (findSupportDirection(add, this.setting4.getValue().booleanValue(), arrayList) == null && !this.setting3.getValue().booleanValue() && (blockPos878 = getBlockPos878(add, arrayList)) != null) {
                    arrayList.add(blockPos878);
                }
                arrayList.add(add);
            }
        }
        return arrayList;
    }

    public BlockPos getBlockPos878(BlockPos blockPos, List<BlockPos> list) {
        Stream<Direction> stream = Arrays.stream(Direction.values());
        Objects.requireNonNull(blockPos);
        return (BlockPos) stream.map(blockPos::offset).sorted(Comparator.comparingDouble(this::getPlacementSortKey)).filter(blockPos2 -> {
            if (findSupportDirection(blockPos2, this.setting4.getValue().booleanValue(), list) != null) {
                if (BlockPlacer.isSpaceFree(blockPos2, true, this.setting7.getValue().booleanValue())) {
                    return true;
                }
            }
            return false;
        }).findFirst().orElse(null);
    }

    public double getPlacementSortKey(BlockPos blockPos) {
        if (!this.setting5.getValue().booleanValue()) {
            return blockPos.getY();
        }
        return -minecraftClient.player.getPos().squaredDistanceTo(blockPos.toCenterPos());
    }

    public boolean isPositionInvalid(BlockPos blockPos) {
        if (this.playerEntity == null) {
            return true;
        }
        Box box = new Box(blockPos.up());
        for (Entity entity : minecraftClient.world.getEntities()) {
            if ((entity instanceof DuckEndCrystalEntity) && !((DuckEndCrystalEntity) entity).isMioAttacked() && entity.getBoundingBox().intersects(box) && BlockPos.ofFloored(entity.getPos()).equals(blockPos.up())) {
                return true;
            }
        }
        if (minecraftClient.player.getEyePos().distanceTo(blockPos.toCenterPos()) > this.setting.getValue().floatValue()) {
            return true;
        }
        if (!this.setting6.getValue().booleanValue() && blockPos.getY() == this.playerEntity.getBlockPos().getY()) {
            if (!minecraftClient.world.getBlockState(blockPos.up()).isReplaceable()) {
                return true;
            }
        }
        return false;
    }

    public Direction findSupportDirection(BlockPos blockPos, boolean z, List<BlockPos> list) {
        for (Direction direction : Direction.values()) {
            BlockPos offset = blockPos.offset(direction);
            if (!minecraftClient.world.getBlockState(offset).isReplaceable() || list.contains(offset)) {
                if (z) {
                    if (!BlockPlacer.getInteractableSides(offset).contains(direction.getOpposite())) {
                    }
                }
                return direction;
            }
        }
        return null;
    }

    public List<Vec3d> getList882(BlockPos blockPos, int i, float f) {
        ArrayList arrayList = new ArrayList();
        if (is887()) {
            arrayList.add(new Vec3d(0.0d, i + f + Float.intBitsToFloat(1065353216), 0.0d));
            Iterator<Vec3d> it = getSurroundOffsetVectors(i, f).iterator();
            if (it.hasNext()) {
                Vec3d next = it.next();
                BlockPos add = BlockPos.ofFloored((Position) next).add((Vec3i) blockPos);
                if (minecraftClient.world.getBlockState(add).isReplaceable()) {
                    if (BlockPlacer.findSupportSide(add, this.setting4.getValue().booleanValue(), this.setting5.getValue().booleanValue()) == null) {
                        if (next.getY() > i) {
                            arrayList.add(new Vec3d(Double.longBitsToDouble(-4616189618054758400L), i + 1, 0.0d));
                        } else {
                            arrayList.add(new Vec3d(Double.longBitsToDouble(-4616189618054758400L), i, 0.0d));
                        }
                    }
                }
            }
        } else if (this.setting4.getValue().booleanValue()) {
            arrayList.add(new Vec3d(0.0d, i + f + Float.intBitsToFloat(1065353216), 0.0d));
        }
        for (Vec3d vec3d : getSurroundOffsetVectors(i, f)) {
            if (vec3d.getY() <= f && (this.setting5.getValue().booleanValue() || vec3d.getY() <= i)) {
                if (this.setting6.getValue().booleanValue() || vec3d.getY() != i) {
                    arrayList.add(vec3d);
                }
            }
        }
        if (this.setting7.getValue().booleanValue()) {
            arrayList.add(new Vec3d(0.0d, i + f + Float.intBitsToFloat(1073741824), 0.0d));
        }
        return arrayList;
    }

    public List<Vec3d> getSurroundOffsetVectors(int i, float f) {
        return Arrays.asList(new Vec3d(0.0d, i + f + Float.intBitsToFloat(1065353216), 0.0d), new Vec3d(Double.longBitsToDouble(4607182418800017408L), i, 0.0d), new Vec3d(0.0d, i, Double.longBitsToDouble(4607182418800017408L)), new Vec3d(0.0d, i, Double.longBitsToDouble(-4616189618054758400L)), new Vec3d(Double.longBitsToDouble(-4616189618054758400L), i, 0.0d), new Vec3d(Double.longBitsToDouble(4607182418800017408L), i + 1, 0.0d), new Vec3d(0.0d, i + 1, Double.longBitsToDouble(4607182418800017408L)), new Vec3d(0.0d, i + 1, Double.longBitsToDouble(-4616189618054758400L)), new Vec3d(Double.longBitsToDouble(-4616189618054758400L), i + 1, 0.0d));
    }

    public boolean isValidRangeTarget(PlayerEntity playerEntity) {
        if (playerEntity.isDead() || !playerEntity.isAlive() || playerEntity == minecraftClient.player || ClientServices.socialManager.isFriendEntity(playerEntity)) {
            return false;
        }
        return (!this.setting8.getValue().booleanValue() || PlayerUtils.hasArmor((LivingEntity) playerEntity)) && playerEntity.distanceTo(minecraftClient.player) <= this.setting.getValue().floatValue() && playerEntity.isOnGround();
    }

    public Vec3d getTargetPos(PlayerEntity playerEntity) {
        Vec3d pos = playerEntity.getPos();
        if (!(this.playerEntity instanceof FakePlayerCommand.OtherClientPlayerEntity) && !(this.playerEntity instanceof ClientPlayerEntity)) {
            DuckLivingEntity duckLivingEntity = (DuckLivingEntity)(this.playerEntity);
            pos = new Vec3d(duckLivingEntity.mio$getServerX(), duckLivingEntity.mio$getServerY(), duckLivingEntity.mio$getServerZ());
        }
        return pos;
    }

    public PlayerEntity getTargetPlayer() {
        return (PlayerEntity) minecraftClient.world.getPlayers().stream().filter((v1) -> {
            return isValidRangeTarget(v1);
        }).min(Comparator.comparing(abstractClientPlayerEntity -> {
            return Float.valueOf(MathHelper.angleBetween(minecraftClient.player.getYaw(), RotationManager.getRotationsTo((Entity) abstractClientPlayerEntity)[0]));
        })).orElse(null);
    }

    public boolean is887() {
        return this.setting4.getValue().booleanValue() && !this.setting5.getValue().booleanValue();
    }

    @Override // me.mioclient.module.Delay
    public int getPriority() {
        return 150;
    }
}
