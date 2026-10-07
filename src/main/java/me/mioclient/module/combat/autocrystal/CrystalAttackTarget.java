package me.mioclient.module.combat.autocrystal;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.world.entity.player.Player;

public final class CrystalAttackTarget {
    public final Player playerEntity;
    public final double val;
    public final AutoCrystalDataMode autoCrystalDataMode;
    public final DamageOutcome autoCrystalMode_7;

    public CrystalAttackTarget(Player playerEntity, double d, AutoCrystalDataMode autoCrystalDataMode, DamageOutcome autoCrystalMode_7) {
        this.playerEntity = playerEntity;
        this.val = d;
        this.autoCrystalDataMode = autoCrystalDataMode;
        this.autoCrystalMode_7 = autoCrystalMode_7;
    }

    public Player getPlayerTarget() {
        return this.playerEntity;
    }

    public double getDamage() {
        return this.val;
    }

    public AutoCrystalDataMode getDataMode() {
        return this.autoCrystalDataMode;
    }

    public DamageOutcome getDamageOutcome() {
        return this.autoCrystalMode_7;
    }
}