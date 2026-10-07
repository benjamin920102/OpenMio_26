package me.mioclient.module.combat.holefill;

import me.mioclient.util.world.HoleData;

import me.mioclient.api.ClientAccessor;

public interface HoleFilter extends ClientAccessor {
    boolean isWithinRange(HoleData holeSnapData);
}