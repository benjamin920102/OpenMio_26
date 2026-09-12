package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.world.BlockCollisionShapeEvent;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.event.events.world.BlockCollisionShapeEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.mixin.ducks.DuckPlayerMoveC2SPacket;
import me.mioclient.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShapes;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/Jesus.class */
public class Jesus extends Module {
    public Setting<JesusMode> mode = add(new EnumSetting<>("Mode", JesusMode.SOLID));
    public Setting<Boolean> strict = add(new BooleanSetting("Strict", false, v -> this.mode.getValue() == Jesus.JesusMode.SOLID));
    public Setting<Float> ascending = add(new NumberSetting<>("Ascending", 1.0f, 0.1f, 1.0f, v -> this.mode.getValue() == Jesus.JesusMode.SOLID));
    public final double[] doubleArr;
    public int num;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/Jesus$JesusMode.class */
    public enum JesusMode implements NamedValue {
        SOLID("Solid"),
        DOLPHIN("Dolphin");

        public final String name;

        JesusMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Jesus() {
        super("Jesus", "Allows you to walk on water.", Category.MOVEMENT, new String[0]);
        this.doubleArr = new double[]{Double.longBitsToDouble(4590596676834315394L), Double.longBitsToDouble(4594186111131777582L), Double.longBitsToDouble(4595128048006538934L), Double.longBitsToDouble(4592898539957638545L), Double.longBitsToDouble(4586206011794593297L), Double.longBitsToDouble(4578280644290880618L)};
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.num = 0;
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (canJesus() && minecraftClient.player.isTouchingWater()) {
            minecraftClient.player.addVelocity(0.0d, this.mode.getValue() == JesusMode.DOLPHIN ? Double.longBitsToDouble(4585911017040021081L) : Double.longBitsToDouble(4590429028186199163L) * this.ascending.getValue().floatValue(), 0.0d);
        }
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (this.mode.getValue() == JesusMode.SOLID && canJesus()) {
            PlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (PlayerMoveC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
            if (packet904 instanceof PlayerMoveC2SPacket) {
                PlayerMoveC2SPacket playerMoveC2SPacket = packet904;
                if (this.strict.getValue().booleanValue() && isOverWater()) {
                    ((DuckPlayerMoveC2SPacket) playerMoveC2SPacket).setOnGround(false);
                    double y = playerMoveC2SPacket.getY(0.0d);
                    double[] dArr = this.doubleArr;
                    int i = this.num;
                    this.num = i + 1;
                    ((DuckPlayerMoveC2SPacket) playerMoveC2SPacket).setY(y + dArr[i]);
                    this.num %= this.doubleArr.length;
                }
            }
        }
    }

    @Listen
    public void onBlockCollisionShape(BlockCollisionShapeEvent voxelShapeEvent) {
        if (isNotInGame() || minecraftClient.player.isTouchingWater() || this.mode.getValue() != JesusMode.SOLID || !canJesus()) {
            return;
        }
        if (voxelShapeEvent.getBlockState().isOf(Blocks.WATER)) {
            voxelShapeEvent.setShape(VoxelShapes.cuboid(new Box(0.0d, 0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607002274814922588L), Double.longBitsToDouble(4607182418800017408L))));
        }
    }

    public boolean canJesus() {
        return (isNotInGame() || minecraftClient.player.input == null || minecraftClient.player.hasVehicle() || minecraftClient.player.input.sneaking) ? false : true;
    }

    public boolean isOverWater() {
        return minecraftClient.world.getBlockState(BlockPos.ofFloored(minecraftClient.player.getPos().subtract(0.0d, Double.longBitsToDouble(4576918229304087675L), 0.0d))).isOf(Blocks.WATER);
    }
}
