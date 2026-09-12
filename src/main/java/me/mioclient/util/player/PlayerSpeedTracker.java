package me.mioclient.util.player;

import me.mioclient.api.ClientServices;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.event.EventBus;

import java.util.ArrayDeque;
import java.util.Iterator;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PlayerSpeedTracker.class */
public class PlayerSpeedTracker implements ClientAccessor {
    public double val;
    public double val2;
    public final ArrayDeque<Double> arrayDeque = new ArrayDeque<>();
    public Stopwatch stopwatch = new Stopwatch();

    public PlayerSpeedTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        this.val = 0.0d;
        this.val2 = 0.0d;
        this.arrayDeque.clear();
        this.stopwatch.reset();
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        double d = getInstantSpeed();
        if (d > 0.0d || minecraftClient.player.age % 4 == 0) {
            this.arrayDeque.add(Double.valueOf(d * ClientServices.holeSnapHelper.getTickRate()));
        } else {
            this.arrayDeque.pollFirst();
        }
        while (!this.arrayDeque.isEmpty() && this.arrayDeque.size() > 9) {
            this.arrayDeque.poll();
        }
        double d2 = 0.0d;
        Iterator<Double> it = this.arrayDeque.iterator();
        while (it.hasNext()) {
            d2 += it.next().doubleValue();
        }
        this.val = this.arrayDeque.size() == 0 ? 0.0d : d2 / this.arrayDeque.size();
        if (this.val < Double.longBitsToDouble(4626604192193052672L)) {
            this.val2 = this.val;
        } else if (this.stopwatch.hasElapsedMs(50L)) {
            this.val2 = this.val;
            this.stopwatch.reset();
        }
    }

    public double getEffectiveSpeed() {
        return this.val;
    }

    public double getSmoothedSpeed() {
        return this.val2;
    }

    public double getInstantSpeed() {
        return Math.hypot(minecraftClient.player.hasVehicle() ? minecraftClient.player.getVehicle().getX() - minecraftClient.player.getVehicle().prevX : minecraftClient.player.getX() - minecraftClient.player.prevX, minecraftClient.player.hasVehicle() ? minecraftClient.player.getVehicle().getZ() - minecraftClient.player.getVehicle().prevZ : minecraftClient.player.getZ() - minecraftClient.player.prevZ) * Double.longBitsToDouble(4626322717216342016L) * Double.longBitsToDouble(4615288898129284301L);
    }
}
