package me.mioclient.module.movement.velocity;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.mixin.ducks.DuckEntityVelocityUpdateS2CPacket;
import me.mioclient.module.movement.Velocity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PlainVelocityHandler.class */
public class PlainVelocityHandler extends VelocityModeHandler {
    public PlainVelocityHandler(Velocity velocity) {
        super(velocity);
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
        int intValue = this.velocity.horizontal.getValue().intValue();
        int intValue2 = this.velocity.vertical.getValue().intValue();
        int i = this.velocity.inverse.getValue().booleanValue() ? -1 : 1;
        EntityVelocityUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof EntityVelocityUpdateS2CPacket ? (EntityVelocityUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket = packet904;
            if (entityVelocityUpdateS2CPacket.getEntityId() == minecraftClient.player.getId()) {
                DuckEntityVelocityUpdateS2CPacket duckEntityVelocityUpdateS2CPacket = (DuckEntityVelocityUpdateS2CPacket) entityVelocityUpdateS2CPacket;
                duckEntityVelocityUpdateS2CPacket.setX((int) (entityVelocityUpdateS2CPacket.getVelocityX() * Double.longBitsToDouble(4665518107723300864L) * intValue * Float.intBitsToFloat(1008981770) * i));
                duckEntityVelocityUpdateS2CPacket.setY((int) (entityVelocityUpdateS2CPacket.getVelocityY() * Double.longBitsToDouble(4665518107723300864L) * intValue2 * Float.intBitsToFloat(1008981770)));
                duckEntityVelocityUpdateS2CPacket.setZ((int) (entityVelocityUpdateS2CPacket.getVelocityZ() * Double.longBitsToDouble(4665518107723300864L) * intValue * Float.intBitsToFloat(1008981770) * i));
                if (intValue == 0 && intValue2 == 0) {
                    channelRead0Event.cancel();
                }
            }
        }
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handleExplosionVelocity(ExplosionVelocityEvent explosionVelocityEvent) {
        if (this.velocity.explosions.getValue().booleanValue()) {
            int intValue = this.velocity.horizontal.getValue().intValue();
            int intValue2 = this.velocity.vertical.getValue().intValue();
            int i = this.velocity.inverse.getValue().booleanValue() ? -1 : 1;
            explosionVelocityEvent.setPlayerVelocityX((int) (explosionVelocityEvent.getPlayerVelocityX() * intValue * Float.intBitsToFloat(1008981770) * i));
            explosionVelocityEvent.setPlayerVelocityY((int) (explosionVelocityEvent.getPlayerVelocityY() * intValue2 * Float.intBitsToFloat(1008981770)));
            explosionVelocityEvent.setPlayerVelocityZ((int) (explosionVelocityEvent.getPlayerVelocityZ() * intValue * Float.intBitsToFloat(1008981770) * i));
        }
    }
}
