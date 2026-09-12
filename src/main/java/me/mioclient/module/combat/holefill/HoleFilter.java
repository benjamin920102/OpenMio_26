package me.mioclient.module.combat.holefill;

import me.mioclient.util.world.HoleData;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.world.HoleData;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HoleFilter.class */
public interface HoleFilter extends ClientAccessor {
    boolean isWithinRange(HoleData holeSnapData);
}
