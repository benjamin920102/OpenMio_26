package me.mioclient.event.events.client;

import me.mioclient.network.irc.CoordLogEntry;

import me.mioclient.network.irc.CoordLogEntry;

import me.mioclient.event.Event;


/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpawnTimeHelperEvent.class */
public class CoordLogReceiveEvent extends Event {
    public final CoordLogEntry spawnTimeHelper;

    public CoordLogReceiveEvent(CoordLogEntry spawnTimeHelper) {
        this.spawnTimeHelper = spawnTimeHelper;
    }

    public CoordLogEntry getEntry() {
        return this.spawnTimeHelper;
    }
}
