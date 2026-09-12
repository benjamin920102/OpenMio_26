package me.mioclient.module.combat;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.module.combat.pusher.FacingDirectionResolver;
import me.mioclient.module.combat.pusher.PusherTargetSelector;
import me.mioclient.network.PacketSender;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;

import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.Iterator;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/Pusher.class */
public class Pusher extends Module {
    public Setting<Float> range = add(new NumberSetting<>("Range", 5.0f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 200, 0, 500).setSuffix("ms"));
    public Setting<Boolean> strictDirection = add(new BooleanSetting("StrictDirection", false));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false));
    public Setting<Boolean> raytrace = add(new BooleanSetting("Raytrace", false));
    public Setting<Boolean> multiTask = add(new BooleanSetting("MultiTask", true));
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", false));
    public Setting<Boolean> targeting = add(new BooleanSetting("Targeting", false).setDependent());
    public Setting<Boolean> ignoreNaked = add(new BooleanSetting("IgnoreNaked", false).requiresSettings(this.targeting));
    public Setting<Boolean> onlySafe = add(new BooleanSetting("OnlySafe", false).requiresSettings(this.targeting));
    public Setting<Boolean> onlyHole = add(new BooleanSetting("OnlyHole", false).requiresSettings(this.targeting));
    public Setting<Boolean> render = add(new BooleanSetting("Render", true).setVisible());
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(1136499199, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(-910321153, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 3.0f).requiresSettings(this.render));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", true).setVisible().requiresSettings(this.render));
    public Setting<Float> fadeTime = add(new NumberSetting<>("FadeTime", 1.0f, 0.1f, 1.0f).setSuffix("s").requiresSettings(this.render, this.fade));
    public final Stopwatch stopwatch;
    public PlayerEntity playerEntity;
    public boolean flag;
    public BlockPos blockPos;
    public boolean flag2;
    public final FacingDirectionResolver pusherHelper;
    public final PusherTargetSelector pusherSearchHelper4;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/Pusher$Record.class */
    public static final class Record {
        public final Direction direction;
        public final BlockPos blockPos;
        public final Vec3d vec3d;

        public Record(Direction direction, BlockPos blockPos, Vec3d vec3d) {
            this.direction = direction;
            this.blockPos = blockPos;
            this.vec3d = vec3d;
        }




        public Direction getDirection() {
            return this.direction;
        }

        public BlockPos getBlockPos() {
            return this.blockPos;
        }

        public Vec3d getPlacementTarget() {
            return this.vec3d;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/Pusher$Record_2.class */
    public static final class Record_2 {
        public final Direction direction;
        public final BlockPos blockPos;

        public Record_2(Direction direction, BlockPos blockPos) {
            this.direction = direction;
            this.blockPos = blockPos;
        }




        public Direction getDirection() {
            return this.direction;
        }

        public BlockPos getBlockPos() {
            return this.blockPos;
        }
    }

    public Pusher() {
        super("Pusher", "Pushes your enemies out of their holes.", Category.COMBAT, new String[0]);
        this.stopwatch = new Stopwatch();
        this.pusherHelper = new FacingDirectionResolver();
        this.pusherSearchHelper4 = new PusherTargetSelector(this);
        ClientServices.antiPhaseBlockHelper.register(new BlockHighlightRenderer.Record(this, this.fill, this.outline, this.lineWidth, this.fadeTime, () -> {
            return true;
        }, this.fade, 450));
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.flag2 = false;
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return this.playerEntity != null ? this.playerEntity.getGameProfile().getName() : super.getInfo();
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        if (minecraftClient.isInSingleplayer()) {
            if (this.flag2) {
                return;
            }
            ClientServices.tooltipsHelper.schedule(() -> {
                ChatUtils.sendPrefixedMessage(Text.literal("Pusher doesn't work in singleplayer."), ChatUtils.createSignature(-2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
            }, 1);
            this.flag2 = true;
            return;
        }
        if (!minecraftClient.player.isUsingItem() || this.multiTask.getValue().booleanValue()) {
            this.playerEntity = this.pusherSearchHelper4.getTargetPlayer();
            if (this.playerEntity == null) {
                return;
            }
            if (this.autoDisable.getValue().booleanValue()) {
                if (BlockPos.stream(this.playerEntity.getBoundingBox()).anyMatch(blockPos -> {
                    if (!minecraftClient.world.getBlockState(blockPos).isOf(Blocks.PISTON_HEAD)) {
                        if (!minecraftClient.world.getBlockState(blockPos).isOf(Blocks.PISTON)) {
                            return false;
                        }
                    }
                    return true;
                })) {
                    disable();
                    return;
                }
            }
            if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue())) {
                this.flag = false;
                this.stopwatch.reset();
                placeRedstoneBlock();
                placePistonBlock();
            }
        }
    }

    public void placeRedstoneBlock() {
        int i = InventoryUtils.findInHotbar(Items.REDSTONE_BLOCK);
        boolean z = false;
        if (i == -1) {
            i = InventoryUtils.findInHotbar(Items.REDSTONE_TORCH);
            z = true;
        }
        int i2 = minecraftClient.player.getInventory().selectedSlot;
        if (i == -1) {
            return;
        }
        Record_2 record_2 = null;
        Iterator it = Direction.Type.HORIZONTAL.iterator();
        while (it.hasNext()) {
            Direction direction = (Direction) it.next();
            BlockPos offset = this.playerEntity.getBlockPos().up().offset(direction);
            if ((WorldUtils.getBlock(offset) instanceof PistonBlock) && !isPistonBlocked(this.playerEntity, offset, direction)) {
                record_2 = findRedstonePlacement(z, offset);
            }
        }
        if (record_2 == null) {
            return;
        }
        this.flag = true;
        if (this.rotate.getValue().booleanValue()) {
            ClientServices.RotationManager.requestRotations(RotationManager.getRotationsToSide(record_2.getBlockPos().toCenterPos(), record_2.getDirection()), 500);
        }
        InventoryUtils.selectSlot(i);
        BlockPlacer.placeBlockPacket(record_2.getBlockPos(), record_2.getDirection(), false, Hand.MAIN_HAND);
        PacketSender.sendHandSwing(Hand.MAIN_HAND);
        InventoryUtils.selectSlot(i2);
        if (this.render.getValue().booleanValue()) {
            ClientServices.antiPhaseBlockHelper.addBlockHighlight(this, record_2.getBlockPos());
        }
    }

    public void placePistonBlock() {
        Record record1962;
        if (this.flag) {
            return;
        }
        int i = InventoryUtils.findInHotbarAny(Items.PISTON, Items.STICKY_PISTON);
        int i2 = minecraftClient.player.getInventory().selectedSlot;
        if (i == -1) {
            return;
        }
        Record record = null;
        Iterator it = Direction.Type.HORIZONTAL.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Direction direction = (Direction) it.next();
            BlockPos offset = this.playerEntity.getBlockPos().up().offset(direction);
            if (!isPistonBlocked(this.playerEntity, offset, direction)) {
                if (getPistonFacing(offset) == direction.getOpposite()) {
                    record = null;
                    break;
                } else if (BlockPlacer.isSpaceFree(offset, true) && (record1962 = findPistonPlacement(offset, direction.getOpposite())) != null) {
                    record = record1962;
                }
            }
        }
        if (record == null) {
            return;
        }
        this.blockPos = record.getBlockPos();
        ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(record.getPlacementTarget()), 500);
        InventoryUtils.selectSlot(i);
        BlockPlacer.placeBlockPacket(record.getBlockPos(), record.getDirection(), false, Hand.MAIN_HAND);
        PacketSender.sendHandSwing(Hand.MAIN_HAND);
        InventoryUtils.selectSlot(i2);
        if (this.render.getValue().booleanValue()) {
            ClientServices.antiPhaseBlockHelper.addBlockHighlight(this, record.getBlockPos());
        }
    }

    public Record_2 findRedstonePlacement(boolean z, BlockPos blockPos) {
        Record_2 record_2 = null;
        for (Direction direction : Direction.values()) {
            BlockPos offset = blockPos.offset(direction);
            if (minecraftClient.world.getBlockState(offset).isOf(Blocks.REDSTONE_BLOCK)) {
                return null;
            }
            if (BlockPlacer.isSpaceFree(offset, true)) {
                Direction direction1961 = findRedstoneSupportSide(z, offset, direction.getOpposite());
                if (direction1961 != null) {
                    record_2 = new Record_2(direction1961, offset);
                }
            }
        }
        return record_2;
    }

    public Direction findRedstoneSupportSide(boolean z, BlockPos blockPos, Direction direction) {
        if (!z) {
            return BlockPlacer.findSupportSide(blockPos, this.strictDirection.getValue().booleanValue());
        }
        for (Direction direction2 : Direction.values()) {
            BlockPos offset = blockPos.offset(direction2);
            if (direction != direction2 && ClientServices.stashFinderHelper.isBlockSolid(offset)) {
                if (this.strictDirection.getValue().booleanValue()) {
                    if (!BlockPlacer.getInteractableSides(offset).contains(direction2.getOpposite())) {
                    }
                }
                return direction2;
            }
        }
        return null;
    }

    public Record findPistonPlacement(BlockPos blockPos, Direction direction) {
        for (Direction direction2 : Direction.values()) {
            BlockPos offset = blockPos.offset(direction2);
            if (ClientServices.stashFinderHelper.isBlockSolid(offset)) {
                if (this.strictDirection.getValue().booleanValue()) {
                    if (!BlockPlacer.getInteractableSides(offset).contains(direction2.getOpposite())) {
                        continue;
                    }
                }
                if (!this.raytrace.getValue().booleanValue() || WorldUtils.isAnyPositionVisible(AutoCraftMode.X8.getWorldCorners(offset))) {
                    if (!this.rotate.getValue().booleanValue()) {
                        return new Record(direction2, blockPos, minecraftClient.player.getEyePos().offset(direction.getOpposite(), Double.longBitsToDouble(4607182418800017408L)));
                    }
                    for (Vec3d vec3d : AutoCraftMode.X8.getVisibleFaceCorners(offset, direction2.getOpposite())) {
                        if (this.pusherHelper.resolveFacingDirection(RotationManager.getRotationsTo(vec3d)) == direction.getOpposite()) {
                            return new Record(direction2, blockPos, vec3d);
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isPistonBlocked(LivingEntity livingEntity, BlockPos blockPos, Direction direction) {
        if (minecraftClient.player.getEyePos().distanceTo(blockPos.toCenterPos()) > getRange()) {
            return true;
        }
        BlockPos offset = blockPos.offset(direction.getOpposite(), 2);
        int i = 0;
        Iterator<BlockPos> it = PlayerUtils.getSurroundPositions(livingEntity).iterator();
        while (it.hasNext()) {
            if (ClientServices.stashFinderHelper.isBlockSolid(it.next())) {
                i++;
            }
        }
        return (i > 2 && minecraftClient.world.getBlockState(offset.down()).isReplaceable()) || !minecraftClient.world.getBlockState(offset).isReplaceable();
    }

    public Direction getPistonFacing(BlockPos blockPos) {
        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        if (blockState.getBlock() instanceof PistonBlock) {
            return blockState.get(FacingBlock.FACING);
        }
        return null;
    }

    public float getRange() {
        return this.range.getValue().floatValue();
    }
}
