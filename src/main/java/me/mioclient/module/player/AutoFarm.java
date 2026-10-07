package me.mioclient.module.player;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ClipContext;
import me.mioclient.module.render.Crosshair;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class AutoFarm extends Module {
    public Setting<AutoFarmMode> mode = add(new EnumSetting<>("Mode", AutoFarmMode.autoFarmMode));
    public Setting<Float> range = add(new NumberSetting<>("Range", 4.0f, 1.0f, 6.0f, v -> this.mode.getValue() == AutoFarm.AutoFarmMode.autoFarmMode2));
    public Setting<Integer> bpt = add(new NumberSetting<>("BPT", 1, 1, 8));
    public Setting<Boolean> place = add(new BooleanSetting("Place", false));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false));
    public Setting<Boolean> fertilize = add(new BooleanSetting("Fertilize", false));
    public Setting<Boolean> remove = add(new BooleanSetting("Remove", false).setVisible());
    public Setting<Float> removeDelay = add(new NumberSetting<>("RemoveDelay", 0.5f, 0.0f, 5.0f, v -> this.remove.isShown()).setSuffix("s"));
    public Setting<Boolean> onlyGrown = add(new BooleanSetting("OnlyGrown", true, v -> this.remove.isShown()));
    public Setting<Boolean> crops = add(new BooleanSetting("Crops", false).setDependent());
    public Setting<Boolean> wheat = add(new BooleanSetting("Wheat", true, v -> this.crops.isShown()));
    public Setting<Boolean> carrots = add(new BooleanSetting("Carrots", true, v -> this.crops.isShown()));
    public Setting<Boolean> potatoes = add(new BooleanSetting("Potatoes", true, v -> this.crops.isShown()));
    public Setting<Boolean> beetRoots = add(new BooleanSetting("BeetRoots", false, v -> this.crops.isShown()));
    public Setting<Boolean> sugarCane = add(new BooleanSetting("SugarCane", false).requiresSettings(this.crops));
    public Setting<Boolean> trees = add(new BooleanSetting("Trees", false).requiresSettings(this.crops));
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public int num;

    
    public static enum AutoFarmMode implements NamedValue {
        autoFarmMode("Crosshair") {
            @Override
            public List<BlockPos> getTargetBlocks(AutoFarm autoFarm) {
                return Collections.singletonList(BlockPos.containing(ClientAccessor.minecraftClient.hitResult.getLocation()));
            }
        },
        autoFarmMode2("Sphere") {
            @Override
            public List<BlockPos> getTargetBlocks(AutoFarm autoFarm) {
                return WorldUtils.getBlocksInRadius(ClientAccessor.minecraftClient.player.position(), autoFarm.range.getValue().floatValue(), true);
            }
        };

        public final String name;

        AutoFarmMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public List<BlockPos> getTargetBlocks(AutoFarm autoFarm) {
            return null;
        }
    }

    public AutoFarm() {
        super("AutoFarm", "Farms your crops for you.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        for (BlockPos blockPos : this.mode.getValue().getTargetBlocks(this)) {
            if (this.num >= this.bpt.getValue().intValue()) {
                break;
            }
            do1849(blockPos, minecraftClient.level.clip(new ClipContext(minecraftClient.player.getEyePosition(), Vec3.atCenterOf(blockPos), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, minecraftClient.player)).getDirection());
        }
        this.num = 0;
    }

    public void do1849(BlockPos blockPos, Direction direction) {
        BlockState blockState = minecraftClient.level.getBlockState(blockPos);
        boolean isOf = blockState.is(Blocks.SUGAR_CANE);
        InteractionHand hand1855 = findHandMatching(itemStack -> {
            return isPlantableCrop(itemStack.getItem());
        });
        if (this.place.getValue().booleanValue() && hand1855 != null) {
            if (is1853(blockPos, blockState, minecraftClient.player.getItemInHand(hand1855).getItem())) {
                do1851(blockPos, hand1855);
                return;
            }
        }
        if (isPlantableCrop(blockState.getBlock().asItem())) {
            boolean is1854 = is1854(blockState, blockPos);
            InteractionHand hand450 = getHandWith(Items.BONE_MEAL);
            if (!this.fertilize.getValue().booleanValue() || is1854 || hand450 == null || isOf) {
                do1850(blockPos, direction, is1854);
            } else {
                do1852(blockPos, hand450);
            }
        }
    }

    public void do1850(BlockPos blockPos, Direction direction, boolean z) {
        if (!(minecraftClient.level.getBlockState(blockPos).getBlock() instanceof SaplingBlock) && this.remove.getValue().booleanValue()) {
            if (this.stopwatch.hasElapsed(this.removeDelay.getValue().floatValue(), TimeUnit.SECONDS)) {
                if (z || !this.onlyGrown.getValue().booleanValue()) {
                    minecraftClient.gameMode.continueDestroyBlock(blockPos, direction);
                    minecraftClient.player.swing(InteractionHand.MAIN_HAND);
                    this.stopwatch2.reset();
                    this.num++;
                    this.stopwatch.reset();
                    if (this.rotate.getValue().booleanValue()) {
                        ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(Vec3.atCenterOf(blockPos)), 0);
                    }
                }
            }
        }
    }

    public void do1851(BlockPos blockPos, InteractionHand hand) {
        minecraftClient.gameMode.useItemOn(minecraftClient.player, hand, new BlockHitResult(Vec3.atCenterOf(blockPos), Direction.UP, blockPos, false));
        minecraftClient.player.swing(hand);
        this.stopwatch2.reset();
        this.num++;
        if (this.rotate.getValue().booleanValue()) {
            ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(Vec3.atCenterOf(blockPos)), 0);
        }
    }

    public void do1852(BlockPos blockPos, InteractionHand hand) {
        if (this.rotate.getValue().booleanValue()) {
            ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(Vec3.atBottomCenterOf( blockPos)), 0);
        }
        minecraftClient.gameMode.useItemOn(minecraftClient.player, hand, new BlockHitResult(Vec3.atBottomCenterOf( blockPos), Direction.UP, blockPos, false));
        minecraftClient.player.swing(hand);
        this.stopwatch2.reset();
        this.num++;
    }

    public boolean is1853(BlockPos blockPos, BlockState blockState, Item item) {
        if (!(item instanceof BlockItem)) {
            return false;
        }
        BlockItem blockItem = (BlockItem) item;
        if (blockItem.getBlock() instanceof CropBlock) {
            if (blockState.is(Blocks.FARMLAND)) {
                if (minecraftClient.level.getBlockState(blockPos.above()).is(Blocks.AIR)) {
                    return true;
                }
            }
            return false;
        }
        if (!(blockItem.getBlock() instanceof SugarCaneBlock)) {
            return false;
        }
        if (!minecraftClient.level.getBlockState(blockPos.above()).is(Blocks.AIR)) {
            return false;
        }
        if (!blockState.is(BlockTags.DIRT) && !blockState.is(BlockTags.SAND)) {
            return false;
        }
        Iterator it = Direction.Plane.HORIZONTAL.iterator();
        while (it.hasNext()) {
            Direction direction = (Direction) it.next();
            FluidState fluidState = minecraftClient.level.getFluidState(blockPos.relative(direction));
            BlockState blockState2 = minecraftClient.level.getBlockState(blockPos.relative(direction));
            if (fluidState.is(FluidTags.WATER) || blockState2.is(Blocks.FROSTED_ICE)) {
                return true;
            }
        }
        return false;
    }

    public boolean is1854(BlockState blockState, BlockPos blockPos) {
        if (blockState.getBlock() instanceof SaplingBlock) {
            return false;
        }
        CropBlock block = (blockState.getBlock()) instanceof CropBlock ? (CropBlock) (blockState.getBlock()) : null;
        if (block instanceof CropBlock) {
            CropBlock cropBlock = block;
            return cropBlock.getAge(blockState) == cropBlock.getMaxAge();
        }
        if (blockState.getBlock() instanceof SugarCaneBlock) {
            return minecraftClient.level.getBlockState(blockPos.below()).is(Blocks.SUGAR_CANE);
        }
        return false;
    }

    public InteractionHand getHandWith(Item item) {
        return findHandMatching(itemStack -> {
            return itemStack.is(item);
        });
    }

    public InteractionHand findHandMatching(Predicate<ItemStack> predicate) {
        if (predicate.test(minecraftClient.player.getMainHandItem())) {
            return InteractionHand.MAIN_HAND;
        }
        if (predicate.test(minecraftClient.player.getOffhandItem())) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    public boolean isPlantableCrop(Item item) {
        return ((item instanceof BlockItem) && (((BlockItem) item).getBlock() instanceof SaplingBlock)) ? this.trees.getValue().booleanValue() : (item == Items.WHEAT_SEEDS && this.wheat.getValue().booleanValue()) || (item == Items.CARROT && this.carrots.getValue().booleanValue()) || ((item == Items.POTATO && this.potatoes.getValue().booleanValue()) || ((item == Items.BEETROOT_SEEDS && this.beetRoots.getValue().booleanValue()) || (item == Items.SUGAR_CANE && this.sugarCane.getValue().booleanValue())));
    }
}