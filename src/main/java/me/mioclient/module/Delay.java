package me.mioclient.module;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.event.events.player.MoveSpeedEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.mode.combat.CrystalRotationMode;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.concurrent.LockedTaskRunner;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.world.BlockRaycastFilter;
import me.mioclient.util.world.CustomRaycastContext;
import me.mioclient.util.world.RaycastUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.world.BlockRaycastFilter;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.world.CustomRaycastContext;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.concurrent.LockedTaskRunner;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.RaycastUtils;

import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.mode.combat.CrystalRotationMode;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.setting.NumberSetting;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.player.FinishUsingEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveSpeedEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.combat.Offhand;
import me.mioclient.module.exploit.ChorusControl;
import me.mioclient.module.movement.NoSlow;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Delay.class */
public abstract class Delay extends Module {
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public static final ChorusControl chorusControl = (ChorusControl) ClientServices.moduleInjector.getModule(ChorusControl.class);
    public static final NoSlow noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class);
    public Setting<Integer> setting;
    public Setting<Integer> setting2;
    public Setting<Boolean> setting3;
    public Setting<Boolean> setting4;
    public Setting<Boolean> setting5;
    public Setting<Boolean> setting6;
    public Setting<Boolean> setting7;
    public Setting<Boolean> setting8;
    public Setting<Boolean> setting9;
    public Setting<Boolean> setting10;
    public Setting<Boolean> setting11;
    public Setting<Boolean> setting12;
    public Setting<Boolean> setting13;
    public Setting<Color> setting14;
    public Setting<Color> setting15;
    public Setting<Float> setting16;
    public Setting<Boolean> setting17;
    public Setting<Float> setting18;
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public boolean flag;
    public Vec3d vec3d;
    public net.minecraft.util.math.Direction direction;
    public Entity entity;
    public int num;
    public int num2;
    public double val;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;
    public boolean flag5;
    public boolean flag6;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/Delay$Record.class */
    public static final class Record {
        public final boolean flag;
        public final Record_2 record_2;
        public static final Record record_ = new Record(false, null);

        public Record(boolean z, Record_2 record_2) {
            this.flag = z;
            this.record_2 = record_2;
        }




        public boolean isPlacementEnabled() {
            return this.flag;
        }

        public Record_2 getRecord_2498() {
            return this.record_2;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/Delay$Record_2.class */
    public static final class Record_2 {
        public final float[] floatArr;

        public Record_2(float[] fArr) {
            this.floatArr = fArr;
        }

        public boolean doesRaycastHitBlock(BlockPos blockPos) {
            HitResult hitResult985 = getHitResult985();
            if (hitResult985.getType() != HitResult.Type.BLOCK) {
                return false;
            }
            return new Box(hitResult985.getPos().add(Double.longBitsToDouble(-4646453807550688133L), Double.longBitsToDouble(-4646453807550688133L), Double.longBitsToDouble(-4646453807550688133L)), hitResult985.getPos().add(Double.longBitsToDouble(4576918229304087675L), Double.longBitsToDouble(4576918229304087675L), Double.longBitsToDouble(4576918229304087675L))).intersects(new Box(blockPos));
        }

        public HitResult getHitResult985() {
            float f = this.floatArr[1] * MathConstants.val4;
            float f2 = (-this.floatArr[0]) * MathConstants.val4;
            float cos = MathHelper.cos(f2);
            float sin = MathHelper.sin(f2);
            float cos2 = MathHelper.cos(f);
            Vec3d vec3d = new Vec3d(sin * cos2, -MathHelper.sin(f), cos * cos2);
            return RaycastUtils.performCustomBlockRaycast(new CustomRaycastContext.Inner(ClientAccessor.minecraftClient.player.getEyePos(), ClientAccessor.minecraftClient.player.getEyePos().add(vec3d.x * Double.longBitsToDouble(4617315517961601024L), vec3d.y * Double.longBitsToDouble(4617315517961601024L), vec3d.z * Double.longBitsToDouble(4617315517961601024L))).setFilter(BlockRaycastFilter.createSolidFilter(ClientServices.stashFinderHelper.getTimedBlockMap().keySet())).build());
        }




        public float[] getLookAngles() {
            return this.floatArr;
        }
    }

    public Delay(String str, String str2, Category category, String... strArr) {
        super(str, str2, category, strArr);
        this.setting = add(new NumberSetting("Delay", 50, 0, 250).setSuffix("ms"));
        this.setting2 = add(new NumberSetting("BPT", 8, 1, 8));
        this.setting3 = add(new BooleanSetting("AirPlace", false));
        this.setting4 = add(new BooleanSetting("StrictDirection", false));
        this.setting5 = add(new BooleanSetting("Raytrace", false));
        this.setting6 = add(new BooleanSetting("Rotate", false));
        this.setting7 = add(new BooleanSetting("Attack", true));
        this.setting8 = add(new BooleanSetting("AutoDisable", false).setDependent());
        this.setting9 = add(new BooleanSetting("Jump", true).requiresSettings(this.setting8));
        this.setting10 = add(new BooleanSetting("Chorus", false).requiresSettings(this.setting8));
        this.setting11 = add(new BooleanSetting("Pearl", false).requiresSettings(this.setting8));
        this.setting12 = add(new BooleanSetting("RubberBand", false).requiresSettings(this.setting8));
        this.setting13 = add(new BooleanSetting("Render", false).setVisible());
        this.setting14 = add(new ColorSetting("Fill", new Color(189, 153, 255, 8), color -> {
            return this.setting13.isShown();
        }));
        this.setting15 = add(new ColorSetting("Outline", new Color(189, 153, 255, 91), color2 -> {
            return this.setting13.isShown();
        }));
        this.setting16 = add(new NumberSetting("LineWidth", Float.valueOf(Float.intBitsToFloat(1065353216)), Float.valueOf(Float.intBitsToFloat(1036831949)), Float.valueOf(Float.intBitsToFloat(1077936128))).requiresSettings(this.setting13));
        this.setting17 = add(new BooleanSetting("Fade", true).requiresSettings(this.setting13));
        this.setting18 = add(new NumberSetting("FadeTime", Float.valueOf(Float.intBitsToFloat(1065353216)), Float.valueOf(Float.intBitsToFloat(1036831949)), Float.valueOf(Float.intBitsToFloat(1065353216))).setSuffix("s").requiresSettings(this.setting13, this.setting17));
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        ClientServices.antiPhaseBlockHelper.register(new BlockHighlightRenderer.Record(this, this.setting14, this.setting15, this.setting16, this.setting18, () -> {
            return true;
        }, this.setting17, 450));
    }

    public Delay(String str, Category category, String... strArr) {
        this(str, "", category, strArr);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (!isNotInGame()) {
            this.val = minecraftClient.player.getY();
        }
        this.num = 0;
        this.num2 = 0;
        this.flag6 = false;
    }

    @Override // me.mioclient.module.Module
    public <T> Setting<T> add(Setting<T> setting) {
        if (!getRegistry().contains(this.setting18)) {
            return super.add(setting);
        }
        List<Setting<?>> registry = getRegistry();
        List<Setting<?>> list = registry;
        list.add(getRegistry().indexOf(this.setting7), setting);
        return setting;
    }

    public abstract List<BlockPos> getPlacePositions();

    public boolean is1320(BlockPos blockPos) {
        return true;
    }

    public void onBlockPlaced(BlockPos blockPos, boolean z) {
    }

    public boolean is1322() {
        return true;
    }

    public void do1323() {
        this.flag = false;
        if (this.flag6) {
            this.flag6 = false;
            return;
        }
        if (Offhand.is929() || SpeedMine.isSwapLocked() || minecraftClient.player.isSpectator() || !minecraftClient.player.isAlive() || minecraftClient.player.isRiding() || minecraftClient.player.isSleeping() || isPlayerFellTooFar()) {
            return;
        }
        if (this.flag3 && noslow.shouldApply()) {
            return;
        }
        if (this.stopwatch.hasElapsedMs(this.setting.getValue().intValue()) && is1322()) {
            List<BlockPos> list876 = getPlacePositions();
            int i = getSlotIndex();
            synchronized (list876) {
                filterPlaceablePositions(i, list876);
                sortPositionsBySupport(list876);
                if (!list876.isEmpty()) {
                    LockedTaskRunner.executeLocked(() -> {
                        executePlacements(list876);
                    });
                }
            }
            this.num = 0;
        }
    }

    public void sortPositionsBySupport(List<BlockPos> list) {
        if (this.setting2.getValue().intValue() > 1) {
            list.sort(Comparator.comparing(blockPos -> {
                return Integer.valueOf(BlockPlacer.findSupportSide(blockPos, this.setting4.getValue().booleanValue()) != null ? -1 : 1);
            }));
        }
    }

    @Listen(get219= RenderConstants.num2)
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        this.vec3d = null;
        this.entity = null;
        do1323();
    }

    @Listen
    public void onMoveSpeed(MoveSpeedEvent moveEvent_2) {
        if (moveEvent_2.getPhase() == KeyPearlMode.Post && moveEvent_2.getSpeed() > MathConstants.val2 && this.setting9.getValue().booleanValue()) {
            disable();
        }
    }

    @Listen
    public void onFinishUsing(FinishUsingEvent finishUsingEvent) {
        if (finishUsingEvent.getItemStack().getItem() != Items.CHORUS_FRUIT || minecraftClient.player.isSneaking() || chorusControl.isToggled()) {
            return;
        }
        if (this.setting10.getValue().booleanValue()) {
            disable();
        } else {
            this.flag6 = true;
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (this.setting7.getValue().booleanValue()) {
            EntitySpawnS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof EntitySpawnS2CPacket ? (EntitySpawnS2CPacket) (channelRead0Event.getPacket()) : null;
            if (packet904 instanceof EntitySpawnS2CPacket) {
                EntitySpawnS2CPacket entitySpawnS2CPacket = packet904;
                if (entitySpawnS2CPacket.getEntityType() == EntityType.END_CRYSTAL) {
                    if (Offhand.is929()) {
                        return;
                    }
                    Vec3i ofFloored = BlockPos.ofFloored(entitySpawnS2CPacket.getX(), entitySpawnS2CPacket.getY(), entitySpawnS2CPacket.getZ());
                    List<BlockPos> list876 = getPlacePositions();
                    filterPlaceablePositions(getSlotIndex(), list876);
                    Iterator<BlockPos> it = list876.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        if (it.next().isWithinDistance(ofFloored, Double.longBitsToDouble(4611686018427387904L))) {
                            if (!ClientServices.scaffoldHelper.sendAttackPacket(entitySpawnS2CPacket.getEntityId(), true)) {
                                return;
                            } else {
                                PacketSender.sendHandSwing(Hand.MAIN_HAND);
                            }
                        }
                    }
                    LockedTaskRunner.executeLocked(() -> {
                        executePlacements(list876);
                    });
                }
            }
        }
        PlayerPositionLookS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof PlayerPositionLookS2CPacket ? (PlayerPositionLookS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket playerPositionLookS2CPacket = packet9042;
            this.val = playerPositionLookS2CPacket.getY();
            if (minecraftClient.player.getPos().squaredDistanceTo(playerPositionLookS2CPacket.getX(), playerPositionLookS2CPacket.getY(), playerPositionLookS2CPacket.getZ()) >= Double.longBitsToDouble(4607182418800017408L) && this.setting12.getValue().booleanValue()) {
                disable();
                return;
            }
        }
        if ((channelRead0Event.getPacket() instanceof PlayerRespawnS2CPacket) && this.flag2) {
            disable();
        }
        OpenScreenS2CPacket packet9043 = (channelRead0Event.getPacket()) instanceof OpenScreenS2CPacket ? (OpenScreenS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9043 instanceof OpenScreenS2CPacket) {
            OpenScreenS2CPacket openScreenS2CPacket = packet9043;
            if (this.stopwatch.hasElapsedMs(100L)) {
                return;
            }
            PacketSender.sendPacket(new CloseHandledScreenC2SPacket(openScreenS2CPacket.getSyncId()));
            channelRead0Event.cancel();
        }
    }

    public void executePlacements(List<BlockPos> list) {
        if (list.isEmpty()) {
            return;
        }
        Iterator<BlockPos> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BlockPos next = it.next();
            if (this.setting7.getValue().booleanValue() && this.stopwatch2.hasElapsedMs(ClientServices.scaffoldHelper.getAttackDelay())) {
                this.entity = BlockPlacer.findAndAttackCrystal(next, ClientServices.scaffoldHelper.get1116());
                if (this.entity != null) {
                    if (this.setting6.getValue().booleanValue()) {
                        ClientServices.RotationManager.requestRotations(RotationManager.limitYawSpeed(RotationManager.getRotationsTo(this.entity), antiCheat.getYawStep()), getPriority() + 1);
                    }
                    this.stopwatch2.reset();
                }
            }
        }
        int i = getSlotIndex();
        int i2 = minecraftClient.player.getInventory().selectedSlot;
        Record_2 record_2 = null;
        Iterator<BlockPos> it2 = list.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            BlockPos next2 = it2.next();
            if (this.num2 >= 30) {
                this.num2 = 0;
                ChatUtils.sendPrefixedMessage(Text.literal(getName()).append(" is out of blocks!"), ChatUtils.createSignature(-2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
                this.num = 1337;
                disable();
                break;
            }
            if (i == -1 || minecraftClient.player.getInventory().getStack(i).isEmpty()) {
                break;
            }
            if (this.num >= this.setting2.getValue().intValue()) {
                break;
            }
            Record record1326 = getPlacementRecord(next2);
            if (record1326.isPlacementEnabled()) {
                this.num2 = 0;
            }
            if (record1326.isPlacementEnabled() && next2 != list.getLast() && is1329()) {
                Record_2 record_2498 = record1326.getRecord_2498();
                if (record_2 == null || !record_2.doesRaycastHitBlock(next2)) {
                    if (next2 != list.getFirst() || !new Record_2(new float[]{minecraftClient.player.getYaw(), minecraftClient.player.getPitch()}).doesRaycastHitBlock(next2)) {
                        PacketSender.sendFullMove(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), record_2498.getLookAngles()[0], record_2498.getLookAngles()[1], minecraftClient.player.isOnGround());
                        record_2 = record_2498;
                    }
                }
            }
        }
        this.num2++;
        if (!this.flag5) {
            this.flag5 = true;
            ClientServices.tooltipsHelper.schedule(() -> {
                this.flag5 = false;
            }, 1);
        }
        if (this.flag4 && shouldUseOffhand()) {
            PacketSender.sendSwapOffhand();
        }
        InventoryUtils.selectSlot(i2);
        this.flag4 = false;
    }

    public Record getPlacementRecord(BlockPos blockPos) {
        boolean z;
        if (Offhand.is929()) {
            return Record.record_;
        }
        this.direction = BlockPlacer.findSupportSide(blockPos, this.setting4.getValue().booleanValue(), this.setting5.getValue().booleanValue());
        if (this.direction == null && !this.setting3.getValue().booleanValue()) {
            return Record.record_;
        }
        float[] floatArray2485 = RotationManager.getRotationsToSide(blockPos.toCenterPos(), this.direction);
        boolean booleanValue = this.setting5.getValue().booleanValue();
        if (this.direction != null) {
            z = booleanValue & (!WorldUtils.isAnyPositionVisible(AutoCraftMode.X8.getVisibleFaceCorners(blockPos.offset(this.direction), this.direction.getOpposite())));
        } else {
            z = booleanValue & (!WorldUtils.isPositionVisible((Vec3i) blockPos));
        }
        if (!is1320(blockPos)) {
            return Record.record_;
        }
        boolean z2 = true;
        if (!this.flag4) {
            InventoryUtils.selectSlot(getSlotIndex());
            if (shouldUseOffhand()) {
                PacketSender.sendSwapOffhand();
            }
            this.flag4 = true;
        }
        if (!z && tryPlaceBlock(blockPos)) {
            if (RotationManager.is724() && antiCheat.isF2b2t()) {
                floatArray2485[0] = (float) (floatArray2485[0] + (Math.random() * Double.longBitsToDouble(4576918229175238656L)));
                floatArray2485[1] = (float) (floatArray2485[1] + (Math.random() * Double.longBitsToDouble(4576918229175238656L)));
            }
            this.stopwatch.reset();
            if (this.setting13.getValue().booleanValue()) {
                ClientServices.antiPhaseBlockHelper.addBlockHighlight(this, blockPos);
            }
            this.vec3d = blockPos.toCenterPos();
            if (this.direction != null) {
                this.vec3d = this.vec3d.offset(this.direction, MathConstants.val2);
            }
            if (this.setting6.getValue().booleanValue()) {
                ClientServices.RotationManager.requestRotations(RotationManager.limitYawSpeed(floatArray2485, antiCheat.getYawStep()), getPriority());
            }
            this.num++;
            z2 = false;
        }
        onBlockPlaced(blockPos, z2);
        return new Record(!z2, new Record_2(floatArray2485));
    }

    public boolean tryPlaceBlock(BlockPos blockPos) {
        return BlockPlacer.placeBlockPacket(blockPos, this.direction, this.setting3.getValue().booleanValue(), shouldUseOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND);
    }

    public int getSlotIndex() {
        return InventoryUtils.findInHotbar(Items.OBSIDIAN) == -1 ? InventoryUtils.findInHotbar(itemStack -> {
            BlockItem item = (itemStack.getItem()) instanceof BlockItem ? (BlockItem) (itemStack.getItem()) : null;
            if (item instanceof BlockItem) {
                BlockItem blockItem = item;
                if (blockItem.getBlock() != Blocks.ANVIL && blockItem.getBlock().getBlastResistance() >= Float.intBitsToFloat(1142292480)) {
                    return true;
                }
            }
            return false;
        }) : InventoryUtils.findInHotbar(Items.OBSIDIAN);
    }

    public void do1328(BlockPos blockPos) {
        if (minecraftClient.world.getBlockState(blockPos).isReplaceable()) {
            return;
        }
        if (minecraftClient.world.getBlockState(blockPos).getHardness(minecraftClient.world, blockPos) == 0.0f) {
            minecraftClient.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, net.minecraft.util.math.Direction.DOWN));
            minecraftClient.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, net.minecraft.util.math.Direction.DOWN));
        }
    }

    public boolean is1329() {
        return !(antiCheat.movementSync.getValue().booleanValue() && antiCheat.rotations.getValue() == CrystalRotationMode.SILENT) && this.setting6.getValue().booleanValue() && this.setting2.getValue().intValue() > 1 && minecraftClient.player.isOnGround() && ClientServices.antiPhaseHelper.getGroundTicks() > 1 && !this.flag5;
    }

    public boolean isPlayerFellTooFar() {
        double y = minecraftClient.player.getY() - this.val;
        if (minecraftClient.player.isOnGround()) {
            this.val = minecraftClient.player.getY();
        }
        if ((minecraftClient.player.getY() <= this.val + Double.longBitsToDouble(4591870180066957722L) || y < Double.longBitsToDouble(4600877379321698714L) || !this.setting9.getValue().booleanValue()) && !minecraftClient.player.isSleeping()) {
            return false;
        }
        setToggled(false);
        return true;
    }

    public void filterPlaceablePositions(int i, List<BlockPos> list) {
        ArrayList arrayList = new ArrayList();
        Block block = null;
        if (i != -1) {
            BlockItem item = (minecraftClient.player.getInventory().getStack(i).getItem()) instanceof BlockItem ? (BlockItem) (minecraftClient.player.getInventory().getStack(i).getItem()) : null;
            if (item instanceof BlockItem) {
                block = item.getBlock();
            }
        }
        for (BlockPos blockPos : list) {
            double squaredDistanceTo = minecraftClient.player.getEyePos().squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (!BlockPlacer.canPlaceAt(blockPos, block, true, this.setting7.getValue().booleanValue()) || squaredDistanceTo > Double.longBitsToDouble(4630263366890291200L)) {
                arrayList.add(blockPos);
            } else if (BlockPlacer.findSupportSide(blockPos) == null) {
                this.flag = true;
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            list.remove((BlockPos) it.next());
        }
    }

    public boolean shouldUseOffhand() {
        return antiCheat.isF2b2t() && RotationManager.is724() && this.setting3.getValue().booleanValue() && this.flag;
    }

    public BlockPos getRoundedBlockPos() {
        return PlayerUtils.getRoundedBlockPos();
    }

    public int getPriority() {
        return 20;
    }

    public void setPlacementEnabled(boolean z) {
        this.flag2 = z;
    }

    public void do1335() {
        this.flag3 = true;
    }
}
