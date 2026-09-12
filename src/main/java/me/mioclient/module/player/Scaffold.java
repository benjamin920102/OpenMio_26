package me.mioclient.module.player;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.module.player.scaffold.ScaffoldMode;
import me.mioclient.module.player.scaffold.ScaffoldMode_3;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.world.CustomRaycastContext;
import me.mioclient.util.world.RaycastUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.player.scaffold.PlacementCandidate;
import me.mioclient.module.player.scaffold.PlacementTarget;
import me.mioclient.network.PacketSender;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.world.CustomRaycastContext;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.RaycastUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.module.player.scaffold.ScaffoldMode;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.module.player.scaffold.ScaffoldMode_3;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.player.ClipAtLedgeEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.mixin.ducks.DuckAbstractBlock;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/Scaffold.class */
public class Scaffold extends Module {
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Setting<ScaffoldMode_2> selection = add(new EnumSetting<>("Selection", ScaffoldMode_2.ANY));
    public Setting<Set<Block>> blocks = add(new EntityListObjectSetting<>("Blocks", Registries.BLOCK));
    public Setting<ScaffoldMode_3> swap = add(new EnumSetting<>("Swap", ScaffoldMode_3.NONE));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 180, 0, 300).setSuffix("ms"));
    public Setting<Integer> offset = add(new NumberSetting<>("Offset", 0, 0, 3));
    public Setting<Boolean> tower = add(new BooleanSetting("Tower", true));
    public Setting<Boolean> safeWalk = add(new BooleanSetting("SafeWalk", true));
    public Setting<Boolean> airPlace = add(new BooleanSetting("AirPlace", false));
    public Setting<Boolean> strictDirection = add(new BooleanSetting("StrictDirection", true));
    public Setting<Boolean> telly = add(new BooleanSetting("Telly", false));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true));
    public Setting<Boolean> stopSprinting = add(new BooleanSetting("StopSprinting", false));
    public Setting<Boolean> attack = add(new BooleanSetting("Attack", true));
    public Setting<Boolean> render = add(new BooleanSetting("Render", true).setVisible());
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.5f, 5.0f, v -> this.render.isShown()));
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(146643455, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(-4351489, true), v -> this.render.isShown()).setThemeSync(true));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", true, v -> this.render.isShown()));
    public Setting<Float> fadeTime = add(new NumberSetting<>("FadeTime", 1.0f, 0.1f, 1.0f, v -> this.render.isShown() && this.fade.getValue().booleanValue()).setSuffix("s"));
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public final Stopwatch stopwatch3;
    public final Stopwatch stopwatch4;
    public final Stopwatch stopwatch5;
    public PlacementCandidate scaffoldData_2;
    public float[] floatArr;
    public int num;
    public int num2;
    public double val;
    public float val2;

    public Scaffold() {
        super("Scaffold", "Speedbridges for you automatically.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        this.stopwatch3 = new Stopwatch();
        this.stopwatch4 = new Stopwatch();
        this.stopwatch5 = new Stopwatch();
        this.num2 = -1;
        this.val2 = Float.intBitsToFloat(-1082130432);
        ClientServices.antiPhaseBlockHelper.register(new BlockHighlightRenderer.Record(this, this.fill, this.outline, this.lineWidth, this.fadeTime, () -> {
            return false;
        }, this.fade, 450));
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.stopwatch.reset();
        this.stopwatch2.reset();
        this.scaffoldData_2 = null;
        this.floatArr = null;
        this.num = 0;
        this.num2 = -1;
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame()) {
            return;
        }
        this.val = minecraftClient.player.getY();
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0125  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x012b  */
    @Listen
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onEvent2(RotationTickEvent holeSnapEvent) {
        boolean z;
        boolean z2;
        if (this.val2 > 0.0f) {
            setJumpStrength(this.val2);
            this.val2 = Float.intBitsToFloat(-1082130432);
        }
        if (minecraftClient.player.isRiding()) {
            return;
        }
        if (!this.stopwatch4.hasElapsedMs(50L) && minecraftClient.player.isOnGround() && antiCheat.movementSync.getValue().booleanValue()) {
            return;
        }
        if (!MovementUtils.is2181() || minecraftClient.player.horizontalCollision) {
            this.val = minecraftClient.player.getY();
        }
        do503();
        this.scaffoldData_2 = findPlacementCandidate();
        if (this.num2 != -1) {
            if (!minecraftClient.player.getInventory().getStack(this.num2).isEmpty()) {
                z = false;
                z2 = z;
                if (this.swap.getValue() == ScaffoldMode_3.NONE && getPlacementScore(minecraftClient.player.getMainHandStack()) == 0) {
                    z2 = true;
                }
                if (!z2) {
                    this.scaffoldData_2 = null;
                    return;
                }
                do501(this.scaffoldData_2);
                if (!RotationManager.is1144() && this.scaffoldData_2 != null) {
                    applyScaffoldRotations(getPlacementRotations(ScaffoldMode.PRE));
                }
                if (this.floatArr != null) {
                    ClientServices.RotationManager.requestRotations(this.floatArr, 5);
                }
                if (this.stopwatch2.hasElapsedMs(500L) || RotationManager.is724()) {
                    this.floatArr = null;
                }
                do500();
                if (this.scaffoldData_2 != null) {
                    if (this.stopwatch3.hasElapsedMs(this.delay.getValue().intValue())) {
                        if (!canPlaceAt(this.scaffoldData_2.getBlockPos(), this.num2)) {
                            return;
                        }
                        this.swap.getValue().selectSlotAndRun(this.num2, () -> {
                            Vec3d add;
                            if (this.render.getValue().booleanValue()) {
                                ClientServices.antiPhaseBlockHelper.addBlockHighlight(this, this.scaffoldData_2.getBlockPos());
                            }
                            PlacementTarget scaffoldData1460 = PlacementTarget.fromCandidate(this.scaffoldData_2, true);
                            Vec3d vec3d843 = scaffoldData1460.getPlacementTarget();
                            if (scaffoldData1460.getDirection() == Direction.DOWN) {
                                add = vec3d843.withAxis(Direction.Axis.Y, this.scaffoldData_2.getBlockPos().getY());
                            } else {
                                add = vec3d843.add(0.0d, scaffoldData1460.getCollisionShape().isEmpty() ? MathConstants.val2 : scaffoldData1460.getCollisionShape().getBoundingBox().maxY - Double.longBitsToDouble(4576918229304087675L), 0.0d);
                            }
                            boolean isSneaking = ClientServices.holeSnapRenderHelper.isSneaking();
                            PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY, 0);
                            BlockPlacer.placeBlock(this.scaffoldData_2.getBlockPos(), add, scaffoldData1460.getDirection(), this.airPlace.getValue().booleanValue(), Hand.MAIN_HAND);
                            if (!isSneaking) {
                                PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY, 0);
                            }
                            if (RotationManager.is1144()) {
                                applyScaffoldRotations(getPlacementRotations(ScaffoldMode.POST.getScaffoldMode874(scaffoldData1460, add)));
                            }
                            this.stopwatch3.reset();
                            this.stopwatch2.reset();
                            this.stopwatch.reset();
                        });
                    }
                }
                if (this.stopwatch.hasElapsedMs(150L)) {
                    this.stopwatch.reset();
                    this.scaffoldData_2 = null;
                    return;
                }
                return;
            }
        }
        z = true;
        z2 = z;
        if (this.swap.getValue() == ScaffoldMode_3.NONE) {
            z2 = true;
        }
        if (!z2) {
        }
    }

    @Listen
    public void onClipAtLedge(ClipAtLedgeEvent clipAtLedgeEvent) {
        if (!this.safeWalk.getValue().booleanValue() || minecraftClient.player.isRiding()) {
            return;
        }
        clipAtLedgeEvent.cancel();
    }

    @Listen
    public void onMove(MoveEvent moveEvent) {
        if (this.safeWalk.getValue().booleanValue() && RotationManager.is724() && minecraftClient.player.isOnGround() && !minecraftClient.player.isRiding()) {
            Box offset = minecraftClient.player.getBoundingBox().offset(moveEvent.getX(), 0.0d, moveEvent.getZ());
            boolean isSneaking = minecraftClient.player.isSneaking();
            if (minecraftClient.world.isSpaceEmpty(offset.stretch(0.0d, Double.longBitsToDouble(-4616189618054758400L), 0.0d))) {
                if (!isSneaking) {
                    minecraftClient.player.setSneaking(true);
                }
                moveEvent.setHorizontal(0.0d, 0.0d);
            } else {
                if (isSneaking) {
                    return;
                }
                minecraftClient.player.setSneaking(false);
            }
        }
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (!sendImmediatelyEvent.isCancelled() && (sendImmediatelyEvent.getPacket() instanceof ClickSlotC2SPacket)) {
            this.stopwatch4.reset();
        }
    }

    public void do500() {
        int floor = (int) Math.floor(minecraftClient.player.getY());
        if (this.num != floor || RotationManager.is724()) {
            this.num = floor;
            if (this.tower.getValue().booleanValue() && minecraftClient.player.input.jumping && minecraftClient.player.forwardSpeed == 0.0f && minecraftClient.player.sidewaysSpeed == 0.0f) {
                if (RotationManager.is724()) {
                    if (minecraftClient.player.isOnGround()) {
                        this.val2 = get510();
                        setJumpStrength(Float.intBitsToFloat(1052539785));
                        return;
                    }
                    return;
                }
                if (this.scaffoldData_2 != null) {
                    minecraftClient.player.setVelocity(0.0d, Double.longBitsToDouble(4601237667055665185L), 0.0d);
                }
            }
        }
    }

    public void do501(PlacementCandidate scaffoldData_2) {
        if (scaffoldData_2 != null && this.attack.getValue().booleanValue()) {
            if (this.stopwatch5.hasElapsedMs(ClientServices.scaffoldHelper.getAttackDelay())) {
                Entity entity3046 = BlockPlacer.findAndAttackCrystal(scaffoldData_2.getBlockPos(), ClientServices.scaffoldHelper.get1116());
                if (entity3046 != null) {
                    applyScaffoldRotations(RotationManager.getRotationsTo(entity3046));
                    this.stopwatch5.reset();
                }
            }
        }
    }

    public void applyScaffoldRotations(float[] fArr) {
        if (this.rotate.getValue().booleanValue()) {
            if (!RotationManager.is1144()) {
                this.floatArr = fArr;
            } else {
                ClientServices.RotationManager.applyRotations(fArr, 10);
                PacketSender.sendFullMove(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), fArr[0], fArr[1], minecraftClient.player.isOnGround());
            }
        }
    }

    public void do503() {
        if (getPlacementScore(minecraftClient.player.getMainHandStack()) > 0) {
            this.num2 = minecraftClient.player.getInventory().selectedSlot;
            return;
        }
        int i = minecraftClient.player.getInventory().selectedSlot;
        int i2 = getPlacementScore(minecraftClient.player.getInventory().getStack(i));
        for (int i3 = 0; i3 < 9; i3++) {
            int i4 = getPlacementScore(minecraftClient.player.getInventory().getStack(i3));
            if (i4 > i2) {
                i = i3;
                i2 = i4;
            }
        }
        if (i2 == 0) {
            this.num2 = -1;
        } else {
            this.num2 = i;
        }
    }

    public int getPlacementScore(ItemStack itemStack) {
        // è½¬åž‹å¿…é¡»åœ¨ instanceof æ£€æŸ¥ä¹‹åŽâ€”â€”æ‰‹æŒéžæ–¹å—ç‰©å“(æœ«å½±æ°´æ™¶/ç©ºæ°”ç­‰)å¦åˆ™å´©æºƒã€‚
        net.minecraft.item.Item rawItem = itemStack.getItem();
        if (!(rawItem instanceof BlockItem)) {
            return 0;
        }
        BlockItem blockItem = (BlockItem) rawItem;
        if (this.selection.getValue().isAllowed(blockItem.getBlock(), this.blocks.getValue()) && ((DuckAbstractBlock)(Object) blockItem.getBlock()).isCollidable() && is505(blockItem)) {
            return BoxMath.isFullBlockShape(blockItem.getBlock().getDefaultState().getCollisionShape(minecraftClient.world, BlockPos.ORIGIN)) ? 2 : 1;
        }
        return 0;
    }

    public boolean is505(BlockItem blockItem) {
        return !(blockItem.getBlock() instanceof FallingBlock) || RaycastUtils.getDistanceToFloor() <= 1;
    }

    public float[] getPlacementRotations(ScaffoldMode scaffoldMode) {
        if (scaffoldMode != ScaffoldMode.PRE) {
            return RotationManager.getRotationsTo(scaffoldMode.vec3d.add(0.0d, scaffoldMode.scaffoldData.getCollisionShape().isEmpty() ? MathConstants.val2 : scaffoldMode.scaffoldData.getCollisionShape().getBoundingBox().maxY - Double.longBitsToDouble(4576918229304087675L), 0.0d));
        }
        PlacementTarget scaffoldData1460 = PlacementTarget.fromCandidate(this.scaffoldData_2, false);
        float[] floatArray2484 = RotationManager.getRotationsTo(scaffoldData1460.getPlacementTarget().add(0.0d, scaffoldData1460.getCollisionShape().isEmpty() ? MathConstants.val2 : scaffoldData1460.getCollisionShape().getBoundingBox().maxY - Double.longBitsToDouble(4576918229304087675L), 0.0d));
        if (MovementUtils.is2181()) {
            double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(), minecraftClient.player.input, Double.longBitsToDouble(4607182418800017408L));
            float degrees = (float) (Math.toDegrees(Math.atan2(doubleArray2508[1], doubleArray2508[0])) - Double.longBitsToDouble(4643457506423603200L));
            if (MathHelper.angleBetween(degrees, floatArray2484[0]) < Float.intBitsToFloat(1114636288)) {
                floatArray2484[0] = degrees;
            }
        }
        this.stopwatch2.reset();
        return floatArray2484;
    }

    public PlacementCandidate findPlacementCandidate() {
        BlockPos blockPos = null;
        Vec3d vec3d222 = BoxMath.getBottomCenter(minecraftClient.player.getBoundingBox());
        if (this.telly.getValue().booleanValue()) {
            vec3d222 = vec3d222.withAxis(Direction.Axis.Y, this.val);
        }
        boolean z = minecraftClient.player.hasVehicle() ? minecraftClient.player.getVehicle().groundCollision : minecraftClient.player.groundCollision;
        if (minecraftClient.player.hasVehicle()) {
            vec3d222 = BoxMath.getBottomCenter(minecraftClient.player.getVehicle().getBoundingBox());
        }
        double d = get508((float) vec3d222.getY());
        for (int i = 0; i < this.offset.getValue().intValue() + 1 && ((MovementUtils.is2181() && minecraftClient.player.isOnGround()) || i <= 0); i++) {
            blockPos = BlockPos.ofFloored(vec3d222.withAxis(Direction.Axis.Y, d)).offset(PlayerUtils.getMovementDirection(), i);
            if (!z && !this.telly.getValue().booleanValue() && !minecraftClient.world.getBlockState(blockPos).isReplaceable()) {
                blockPos = blockPos.up();
            }
            if (canPlaceAt(blockPos, this.num2)) {
                break;
            }
            blockPos = null;
        }
        if (blockPos == null) {
            return null;
        }
        Direction direction3029 = BlockPlacer.findSupportSide(blockPos, this.strictDirection.getValue().booleanValue());
        if (direction3029 != null) {
            return new PlacementCandidate(blockPos, direction3029);
        }
        for (int i2 = 1; i2 < 3; i2++) {
            for (Direction direction : Direction.values()) {
                if (direction != Direction.UP) {
                    Direction direction30292 = BlockPlacer.findSupportSide(blockPos.offset(direction, i2), this.strictDirection.getValue().booleanValue());
                    if (direction30292 != null) {
                        return new PlacementCandidate(blockPos.offset(direction), direction30292);
                    }
                }
            }
        }
        return null;
    }

    public double get508(float f) {
        double y;
        double longBitsToDouble;
        Box boundingBox = minecraftClient.player.getBoundingBox();
        boolean isOnGround = minecraftClient.player.isOnGround();
        if (minecraftClient.player.hasVehicle()) {
            boundingBox = minecraftClient.player.getVehicle().getBoundingBox();
            isOnGround = minecraftClient.player.getVehicle().isOnGround();
        }
        double d = boundingBox.minX;
        double d2 = boundingBox.minZ;
        double d3 = boundingBox.maxX;
        double d4 = boundingBox.maxZ;
        ArrayList<Vec3d> arrayList = new ArrayList();
        arrayList.add(BoxMath.getBottomCenter(boundingBox).withAxis(Direction.Axis.Y, f));
        if (isOnGround) {
            arrayList.addAll(List.of(new Vec3d(d, f, d2), new Vec3d(d3, f, d2), new Vec3d(d, f, d4), new Vec3d(d3, f, d4)));
        }
        double longBitsToDouble2 = Double.longBitsToDouble(-4571373524106608640L);
        for (Vec3d vec3d : arrayList) {
            BlockHitResult blockHitResult2784 = RaycastUtils.performCustomBlockRaycast(new CustomRaycastContext.Inner(vec3d, vec3d.add(0.0d, Double.longBitsToDouble(-4616189618054758400L), 0.0d)).setEntity(minecraftClient.player.hasVehicle() ? minecraftClient.player.getVehicle() : minecraftClient.player).build());
            if (blockHitResult2784.getType() == HitResult.Type.MISS) {
                y = vec3d.getY();
                longBitsToDouble = Double.longBitsToDouble(4607182418800017408L);
            } else {
                y = blockHitResult2784.getPos().getY();
                longBitsToDouble = Double.longBitsToDouble(4576918229175238656L);
            }
            longBitsToDouble2 = Math.max(longBitsToDouble2, y - longBitsToDouble);
        }
        return longBitsToDouble2;
    }

    public boolean canPlaceAt(BlockPos blockPos, int i) {
        BlockItem item = (minecraftClient.player.getInventory().getStack(i == -1 ? minecraftClient.player.getInventory().selectedSlot : i).getItem()) instanceof BlockItem ? (BlockItem) (minecraftClient.player.getInventory().getStack(i == -1 ? minecraftClient.player.getInventory().selectedSlot : i).getItem()) : null;
        if (!(item instanceof BlockItem)) {
            return BlockPlacer.isSpaceFree(blockPos, true, this.attack.getValue().booleanValue());
        }
        return BlockPlacer.canPlaceAt(blockPos, item.getBlock(), true, this.attack.getValue().booleanValue());
    }

    public float get510() {
        return (float) minecraftClient.player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).getBaseValue();
    }

    public void setJumpStrength(float f) {
        minecraftClient.player.getAttributeInstance(EntityAttributes.GENERIC_JUMP_STRENGTH).setBaseValue(f);
    }
}
