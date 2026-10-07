package me.mioclient.module.combat.antiphase;

import me.mioclient.module.combat.AntiPhase;
import net.minecraft.world.entity.player.Player;

public interface AntiPhaseTargetResolver {
    AntiPhasePlacement resolveTarget(AntiPhase antiPhase, Player playerEntity);
}