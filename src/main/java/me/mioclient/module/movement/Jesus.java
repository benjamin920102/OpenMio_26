package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.world.BlockCollisionShapeEvent;

import me.mioclient.api.NamedValue;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.network.PacketSender;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class Jesus extends Module {
    public Setting<JesusMode> mode = add(new EnumSetting<>("Mode", JesusMode.SOLID));
    public Setting<Boolean> strict = add(new BooleanSetting("Strict", false, v -> this.mode.getValue() == Jesus.JesusMode.SOLID));
    public Setting<Float> ascending = add(new NumberSetting<>("Ascending", 1.0f, 0.1f, 1.0f, v -> this.mode.getValue() == Jesus.JesusMode.SOLID));
    public final double[] doubleArr;
    public int num;

    
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
        if (canJesus() && minecraftClient.player.isInWater()) {
            minecraftClient.player.addDeltaMovement(new Vec3(0.0d, this.mode.getValue() == JesusMode.DOLPHIN ? Double.longBitsToDouble(4585911017040021081L) : Double.longBitsToDouble(4590429028186199163L) * this.ascending.getValue().floatValue(), 0.0d));
        }
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (this.mode.getValue() == JesusMode.SOLID && canJesus()) {
            if (!(sendImmediatelyEvent.getPacket() instanceof ServerboundMovePlayerPacket playerMoveC2SPacket)) {
                return;
            }
            if (this.strict.getValue().booleanValue() && isOverWater()) {
                // 26.2 MojMap: packets IMMUTABLE — cancel + resend with modified Y / onGround=false
                sendImmediatelyEvent.cancel();
                double y = playerMoveC2SPacket.getY(0.0d);
                double[] dArr = this.doubleArr;
                int i = this.num;
                this.num = i + 1;
                double newY = y + dArr[i];
                this.num %= this.doubleArr.length;
                boolean hc = playerMoveC2SPacket.horizontalCollision();
                if (playerMoveC2SPacket.hasPosition() && playerMoveC2SPacket.hasRotation()) {
                    PacketSender.sendPacket(new ServerboundMovePlayerPacket.PosRot(new net.minecraft.world.phys.Vec3(playerMoveC2SPacket.getX(0.0d), newY, playerMoveC2SPacket.getZ(0.0d)), playerMoveC2SPacket.getYRot(0.0f), playerMoveC2SPacket.getXRot(0.0f), false, hc));
                } else if (playerMoveC2SPacket.hasPosition()) {
                    PacketSender.sendPacket(new ServerboundMovePlayerPacket.Pos(new net.minecraft.world.phys.Vec3(playerMoveC2SPacket.getX(0.0d), newY, playerMoveC2SPacket.getZ(0.0d)), false, hc));
                } else {
                    PacketSender.sendPacket(new ServerboundMovePlayerPacket.StatusOnly(false, hc));
                }
            }
        }
    }

    @Listen
    public void onBlockCollisionShape(BlockCollisionShapeEvent voxelShapeEvent) {
        if (isNotInGame() || minecraftClient.player.isInWater() || this.mode.getValue() != JesusMode.SOLID || !canJesus()) {
            return;
        }
        if (voxelShapeEvent.getBlockState().is(Blocks.WATER)) {
            voxelShapeEvent.setShape(Shapes.create(new AABB(0.0d, 0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607002274814922588L), Double.longBitsToDouble(4607182418800017408L))));
        }
    }

    public boolean canJesus() {
        return (isNotInGame() || minecraftClient.player.input == null || minecraftClient.player.isPassenger() || minecraftClient.player.input.keyPresses.shift()) ? false : true;
    }

    public boolean isOverWater() {
        return minecraftClient.level.getBlockState(BlockPos.containing(minecraftClient.player.position().subtract(0.0d, Double.longBitsToDouble(4576918229304087675L), 0.0d))).is(Blocks.WATER);
    }
}