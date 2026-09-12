package me.mioclient.module.combat.antiphase;

import me.mioclient.module.combat.AntiPhase;
import net.minecraft.entity.player.PlayerEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AntiPhaseTargetResolver.class */
public interface AntiPhaseTargetResolver {
    AntiPhasePlacement resolveTarget(AntiPhase antiPhase, PlayerEntity playerEntity);
}
