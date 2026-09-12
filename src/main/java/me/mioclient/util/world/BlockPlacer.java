package me.mioclient.util.world;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.client.AntiCheatMode;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.mixin.ducks.DuckAbstractBlock;
import me.mioclient.mixin.ducks.DuckLivingEntity;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.misc.KillEffects;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import me.mioclient.mode.client.AntiCheatMode;
import me.mioclient.mode.player.AutoCraftMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BlockPlacer.class */
public class BlockPlacer implements ClientAccessor {
    public static final Stopwatch stopwatch = new Stopwatch();
    public static boolean flag = false;
    public static final Direction[] directionArr = {Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public static KillEffects killEffects = (KillEffects) ClientServices.moduleInjector.getModule(KillEffects.class);

    public static Direction findSupportSide(BlockPos blockPos) {
        return findSupportSide(blockPos, false);
    }

    public static Direction findSupportSide(BlockPos blockPos, boolean z) {
        return findSupportSide(blockPos, z, false);
    }

    public static Direction findSupportSide(BlockPos blockPos, boolean z, boolean z2) {
        for (Direction direction : directionArr) {
            if (blockPos.add(direction.getVector()).getY() < minecraftClient.world.getTopY()) {
                BlockPos offset = blockPos.offset(direction);
                if (z2) {
                    if (!WorldUtils.isAnyPositionVisible(AutoCraftMode.X8.getVisibleFaceCorners(offset, direction.getOpposite()))) {
                        continue;
                    }
                }
                if (ClientServices.stashFinderHelper.isBlockSolid(offset)) {
                    if (z) {
                        if (!getInteractableSides(offset).contains(direction.getOpposite())) {
                        }
                    }
                    return direction;
                }
                continue;
            }
        }
        return null;
    }

    public static List<Direction> getInteractableSides(BlockPos blockPos) {
        ArrayList arrayList = new ArrayList(List.of((Object[]) Direction.values()));
        for (Direction direction : Direction.values()) {
            BlockPos offset = blockPos.offset(direction);
            BlockState blockState = minecraftClient.world.getBlockState(offset);
            if (!blockState.isReplaceable() && blockState.isOpaqueFullCube(minecraftClient.world, offset)) {
                arrayList.remove(direction);
            }
        }
        if (blockPos.getY() + 1 >= minecraftClient.world.getTopY()) {
            arrayList.remove(Direction.UP);
        }
        double d = minecraftClient.player.getEyePos().x - blockPos.toCenterPos().x;
        double d2 = minecraftClient.player.getEyePos().z - blockPos.toCenterPos().z;
        if (d > MathConstants.val2) {
            arrayList.remove(Direction.WEST);
        } else if (d < (-MathConstants.val2)) {
            arrayList.remove(Direction.EAST);
        } else {
            arrayList.remove(Direction.WEST);
            arrayList.remove(Direction.EAST);
        }
        if (d2 > MathConstants.val2) {
            arrayList.remove(Direction.NORTH);
        } else if (d2 < (-MathConstants.val2)) {
            arrayList.remove(Direction.SOUTH);
        } else {
            arrayList.remove(Direction.NORTH);
            arrayList.remove(Direction.SOUTH);
        }
        if (minecraftClient.player.getEyePos().y < blockPos.getY() + (antiCheat.strictDirection.getValue() == AntiCheatMode.GRIM ? 1 : 0)) {
            arrayList.remove(Direction.UP);
        }
        if (minecraftClient.player.getEyePos().y > blockPos.getY()) {
            arrayList.remove(Direction.DOWN);
        }
        return arrayList;
    }

    public static BlockPos findNearbyPlaceablePos(BlockPos blockPos, int i, boolean z, boolean z2) {
        return findNearbyPlaceablePos(blockPos, i, z, z2, blockPos2 -> {
            return true;
        });
    }

    public static BlockPos findNearbyPlaceablePos(BlockPos blockPos, int i, boolean z, boolean z2, java.util.function.Predicate<BlockPos> predicate) {
        for (int i2 = 1; i2 <= i; i2++) {
            for (Direction direction : directionArr) {
                BlockPos offset = blockPos.offset(direction, i2);
                if (predicate.test(offset) && findSupportSide(offset, z2) != null) {
                    if (minecraftClient.world.getEntitiesByClass(PlayerEntity.class, new Box(offset), playerEntity -> {
                        return z;
                    }).isEmpty()) {
                        return offset;
                    }
                }
            }
        }
        return null;
    }

    public static boolean placeBlock(BlockPos blockPos, boolean z) {
        return placeBlock(blockPos, z, Hand.MAIN_HAND);
    }

    public static boolean placeBlock(BlockPos blockPos, boolean z, Hand hand) {
        return placeBlock(blockPos, (Vec3d) null, findSupportSide(blockPos), z, hand);
    }

    public static boolean placeBlock(BlockPos blockPos, Vec3d vec3d, Direction direction, boolean z, Hand hand) {
        if (minecraftClient.world == null || minecraftClient.player == null || minecraftClient.interactionManager == null) {
            return false;
        }
        BlockItem item = (minecraftClient.player.getStackInHand(hand).getItem()) instanceof BlockItem ? (BlockItem) (minecraftClient.player.getStackInHand(hand).getItem()) : null;
        if (!canPlaceAt(blockPos, item instanceof BlockItem ? item.getBlock() : null, false)) {
            return false;
        }
        boolean z2 = z && direction == null;
        if (direction == null) {
            if (!z) {
                return false;
            }
            BlockHitResult blockHitResult = (minecraftClient.crosshairTarget) instanceof BlockHitResult ? (BlockHitResult) (minecraftClient.crosshairTarget) : null;
            direction = blockHitResult instanceof BlockHitResult ? blockHitResult.getSide() : Direction.DOWN;
        }
        BlockPos offset = z2 ? blockPos : blockPos.offset(direction);
        boolean needsSneakPlace = needsSneakPlace(offset);
        if (vec3d == null) {
            vec3d = blockPos.toCenterPos().add(Vec3d.of(direction.getVector()).multiply(MathConstants.val2));
        }
        if (needsSneakPlace) {
            minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        flag = true;
        ActionResult interactBlock = minecraftClient.interactionManager.interactBlock(minecraftClient.player, hand, new BlockHitResult(vec3d, z2 ? direction : direction.getOpposite(), offset, false));
        flag = false;
        if (interactBlock.shouldSwingHand()) {
            minecraftClient.player.swingHand(hand);
        }
        if (!needsSneakPlace) {
            return true;
        }
        minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        return true;
    }

    public static boolean placeBlockPacket(BlockPos blockPos, Direction direction, boolean z, Hand hand) {
        return placeBlockPacket(blockPos, (Vec3d) null, direction, z, hand);
    }

    public static boolean placeBlockPacket(BlockPos blockPos, Vec3d vec3d, Direction direction, boolean z, Hand hand) {
        if (minecraftClient.world == null || minecraftClient.player == null || minecraftClient.interactionManager == null) {
            return false;
        }
        BlockItem item = (minecraftClient.player.getStackInHand(hand).getItem()) instanceof BlockItem ? (BlockItem) (minecraftClient.player.getStackInHand(hand).getItem()) : null;
        if (!canPlaceAt(blockPos, item instanceof BlockItem ? item.getBlock() : null, false)) {
            return false;
        }
        boolean z2 = z && direction == null;
        if (direction == null) {
            List<Direction> list3031 = getInteractableSides(blockPos);
            if (!z) {
                return false;
            }
            direction = list3031.isEmpty() ? Direction.DOWN : (Direction) list3031.getFirst();
        }
        BlockPos offset = z2 ? blockPos : blockPos.offset(direction);
        boolean needsSneakPlace = needsSneakPlace(offset);
        if (needsSneakPlace) {
            minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        if (vec3d == null) {
            vec3d = blockPos.toCenterPos().add(Vec3d.of(direction.getVector()).multiply(MathConstants.val2));
        }
        PacketSender.sendInteractBlock(hand, new BlockHitResult(vec3d, z2 ? direction : direction.getOpposite(), offset, false));
        if (!antiCheat.isF2b2t() || !antiCheat.movementSync.getValue().booleanValue()) {
            minecraftClient.player.swingHand(hand);
        } else if (!minecraftClient.player.handSwinging || minecraftClient.player.handSwingTicks >= ((DuckLivingEntity) minecraftClient.player).getSwingDuration() / 2 || minecraftClient.player.handSwingTicks < 0) {
            minecraftClient.player.handSwingTicks = -1;
            minecraftClient.player.handSwinging = true;
            minecraftClient.player.preferredHand = hand;
        }
        if (!needsSneakPlace) {
            return true;
        }
        minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        return true;
    }

    public static boolean canPlaceAt(BlockPos blockPos, Block block, boolean z) {
        return canPlaceAt(blockPos, block, z, false);
    }

    public static boolean canPlaceAt(BlockPos blockPos, Block block, boolean z, boolean z2) {
        if (!minecraftClient.world.getBlockState(blockPos).isReplaceable() || !minecraftClient.world.getWorldBorder().contains(blockPos) || !World.isValid(blockPos) || blockPos.getY() >= minecraftClient.world.getTopY() || blockPos.getY() < minecraftClient.world.getBottomY()) {
            return false;
        }
        VoxelShape empty = block == null ? VoxelShapes.empty() : block.getDefaultState().getCollisionShape(minecraftClient.world, BlockPos.ORIGIN);
        Box box = empty.isEmpty() ? new Box(blockPos) : BoxMath.getBoxAroundBlock(empty.getBoundingBox(), blockPos);
        if (block != null && !((DuckAbstractBlock) block).isCollidable()) {
            return true;
        }
        for (Entity clientPlayerEntity : minecraftClient.world.getEntitiesByClass(Entity.class, box, entity -> {
            return ((entity instanceof ExperienceBottleEntity) || (entity instanceof ItemEntity) || (entity instanceof ItemFrameEntity) || (entity instanceof ExperienceOrbEntity)) ? false : true;
        })) {
            if (BoxMath.hasSignificantIntersection(box, ((Entity) clientPlayerEntity).getBoundingBox()) || clientPlayerEntity == minecraftClient.player) {
                if (clientPlayerEntity instanceof ArrowEntity) {
                    continue;
                } else if (clientPlayerEntity instanceof PlayerEntity) {
                    if (((Entity) clientPlayerEntity).getBoundingBox().intersects(box)) {
                        return false;
                    }
                } else if (!(clientPlayerEntity instanceof EndCrystalEntity) || !z2) {
                    return !z;
                }
            }
        }
        return true;
    }

    public static boolean isSpaceFree(BlockPos blockPos, boolean z) {
        return isSpaceFree(blockPos, z, false);
    }

    public static boolean isSpaceFree(BlockPos blockPos, boolean z, boolean z2) {
        if (!minecraftClient.world.getBlockState(blockPos).isReplaceable() || !minecraftClient.world.getWorldBorder().contains(blockPos)) {
            return false;
        }
        for (Entity clientPlayerEntity : minecraftClient.world.getEntitiesByClass(Entity.class, new Box(blockPos), entity -> {
            return ((entity instanceof ExperienceBottleEntity) || (entity instanceof ItemEntity) || (entity instanceof ExperienceOrbEntity)) ? false : true;
        })) {
            if (BoxMath.hasSignificantIntersection(new Box(blockPos), ((Entity) clientPlayerEntity).getBoundingBox()) || clientPlayerEntity == minecraftClient.player) {
                if (!(clientPlayerEntity instanceof EndCrystalEntity) || !z2) {
                    if (!(clientPlayerEntity instanceof ArrowEntity) && ((clientPlayerEntity instanceof PlayerEntity) || z)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isCrystalPlaceable(BlockPos blockPos, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        if (!WorldUtils.isBedrockOrObsidian(blockPos)) {
            return false;
        }
        BlockPos blockPos2 = blockPos.add(0, 1, 0);
        Block block = minecraftClient.world.getBlockState(blockPos2).getBlock();
        if (z4) {
            if (block != Blocks.AIR && block != Blocks.FIRE) {
                return false;
            }
        } else if (block != Blocks.AIR) {
            return false;
        }
        if (z) {
            if (minecraftClient.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() != Blocks.AIR) {
                return false;
            }
        }
        Box box = new Box(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ(), blockPos2.getX() + 1, blockPos2.getY() + (z5 ? Double.longBitsToDouble(4606281698874543309L) : Double.longBitsToDouble(4611686018427387904L)), blockPos2.getZ() + 1);
        int i = 0;
        Iterator it = minecraftClient.world.getEntities().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Entity clientPlayerEntity = (Entity) it.next();
            if (!((Entity) clientPlayerEntity).isRemoved() && ((Entity) clientPlayerEntity).isAlive()) {
                Box boundingBox = ((Entity) clientPlayerEntity).getBoundingBox();
                if ((clientPlayerEntity instanceof PlayerEntity) && clientPlayerEntity != minecraftClient.player && z6) {
                    boundingBox = boundingBox.expand(Double.longBitsToDouble(4576918229304087675L));
                } else if ((clientPlayerEntity instanceof EndCrystalEntity) && !z3) {
                }
                if (boundingBox.intersects(box)) {
                    i = 0 + 1;
                    break;
                }
            }
        }
        return !z2 || i == 0;
    }

    public static Block getBlockAt(BlockPos blockPos) {
        if (blockPos == null) {
            return null;
        }
        return minecraftClient.world.getBlockState(blockPos).getBlock();
    }

    public static boolean isBlockCollidable(BlockPos blockPos) {
        return ((DuckAbstractBlock) getBlockAt(blockPos)).isCollidable();
    }

    public static Entity findAndAttackCrystal(BlockPos blockPos, int i) {
        for (Entity entity : minecraftClient.world.getEntities()) {
            if (entity instanceof EndCrystalEntity) {
                if (entity.getBoundingBox().intersects(new Box(blockPos)) && entity.age >= i) {
                    attackEntity(entity);
                    return entity;
                }
            }
        }
        return null;
    }

    public static void attackEntity(Entity entity) {
        attackEntity(entity, false);
    }

    public static void attackEntity(Entity entity, boolean z) {
        if (ClientServices.scaffoldHelper.tryAttackCrystal(entity.getId())) {
            if (killEffects.isToggled() && (entity instanceof PlayerEntity)) {
                killEffects.registerAttackTarget((PlayerEntity) entity);
            }
            minecraftClient.player.resetLastAttackedTicks();
        }
    }

    public static void interactWithEntity(Entity entity, Hand hand) {
        minecraftClient.interactionManager.interactEntityAtLocation(minecraftClient.player, entity, new EntityHitResult(entity), hand);
        minecraftClient.interactionManager.interactEntity(minecraftClient.player, entity, hand);
        minecraftClient.player.swingHand(hand);
    }

    public static boolean isBlockInteractive(Block block) {
        return (block instanceof CraftingTableBlock) || (block instanceof AnvilBlock) || (block instanceof ButtonBlock) || (block instanceof AbstractPressurePlateBlock) || (block instanceof BlockWithEntity) || (block instanceof BedBlock) || (block instanceof FenceGateBlock) || (block instanceof DoorBlock) || (block instanceof NoteBlock) || (block instanceof TrapdoorBlock);
    }

    public static boolean needsSneakPlace(BlockPos blockPos) {
        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        return block == Blocks.RESPAWN_ANCHOR || blockState.hasBlockEntity() || (block instanceof BedBlock) || (block instanceof AbstractChestBlock) || (block instanceof DoorBlock) || (block instanceof TrapdoorBlock) || (block instanceof ButtonBlock) || (block instanceof ComposterBlock) || (block instanceof NoteBlock) || (block instanceof AnvilBlock) || (block instanceof AbstractFurnaceBlock) || (block instanceof CraftingTableBlock) || (block instanceof EnchantingTableBlock) || (block instanceof CakeBlock);
    }

    public Direction getRotatedDirection(Direction direction, int i) {
        return Direction.values()[2 + ((direction.getId() + i) % Direction.values().length)];
    }

    public static boolean isThrowableItem(Item item) {
        return (item instanceof EggItem) || (item instanceof SnowballItem) || (item instanceof EnderPearlItem) || (item instanceof ExperienceBottleItem) || (item instanceof ThrowablePotionItem) || (item instanceof TridentItem);
    }

    public static void resetPlaceCooldown() {
        stopwatch.reset();
    }

    public static boolean isPlaceOnCooldown() {
        return !stopwatch.hasElapsedMs(50L);
    }
}
