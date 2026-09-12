package me.mioclient.module.movement.entitycontrol;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.network.PacketSender;

import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.mixin.ducks.DuckVehicleMoveC2SPacket;
import me.mioclient.module.movement.EntityControl;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import me.mioclient.event.events.network.Vec3dEvent;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/VehicleMoveHandler.class */
public final class VehicleMoveHandler implements ClientAccessor {
    public final EntityControl entityControl;
    public int num;
    public int num2;
    public Vec3d vec3d;
    public final Stopwatch stopwatch = new Stopwatch();
    public boolean flag = true;

    public VehicleMoveHandler(EntityControl entityControl) {
        this.entityControl = entityControl;
    }

    public boolean updateVehicleState() {
        if (!minecraftClient.player.hasVehicle()) {
            return true;
        }
        long j = 1000;
        if (this.num >= 3) {
            j = 250;
        }
        if (this.stopwatch.hasElapsedMs(j)) {
            sendVehicleSync();
        }
        Vec3d pos = minecraftClient.player.getVehicle().getPos();
        if (this.num2 > 0) {
            this.num2--;
        }
        if (this.vec3d == null || 0 != 0 || Math.abs(pos.y - this.vec3d.y) >= Double.longBitsToDouble(4625759767262920704L)) {
            sendVehicleSync();
            this.vec3d = pos;
        }
        return this.flag;
    }

    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        if ((sendImmediatelyEvent.getPacket() instanceof PlayerMoveC2SPacket) || (sendImmediatelyEvent.getPacket() instanceof BoatPaddleStateC2SPacket)) {
            sendImmediatelyEvent.cancel();
        }
    }

    public void handleVehiclePosition(Vec3dEvent vec3dEvent) {
        this.flag = true;
        Entity vehicle = minecraftClient.player.getVehicle();
        if (vehicle == null) {
            return;
        }
        Vec3d vec3d1954 = vec3dEvent.getPosition();
        vehicle.updatePosition(vec3d1954.getX(), vec3d1954.getY(), vec3d1954.getZ());
        sendVehicleMovePacket(0.0f);
        vec3dEvent.cancel();
        this.num2 = 2;
    }

    public void sendVehicleSync() {
        Entity vehicle = minecraftClient.player.getVehicle();
        if (vehicle == null) {
            return;
        }
        if (this.vec3d == null || this.vec3d.getY() >= vehicle.getY()) {
            this.num = 0;
        } else {
            this.num++;
        }
        sendVehicleMovePacket(Float.intBitsToFloat(1148846080));
        this.stopwatch.reset();
        this.flag = false;
    }

    public void sendVehicleMovePacket(float f) {
        Entity vehicle = minecraftClient.player.getVehicle();
        if (vehicle == null) {
            return;
        }
        Packet vehicleMoveC2SPacket = new VehicleMoveC2SPacket(vehicle);
        ((DuckVehicleMoveC2SPacket) vehicleMoveC2SPacket).setY(((VehicleMoveC2SPacket) vehicleMoveC2SPacket).getY() + f);
        PacketSender.sendPacket(vehicleMoveC2SPacket);
    }

    public boolean isAboveTickThreshold(int i) {
        return this.num >= i;
    }
}
