package me.mioclient.module.combat.autocrystal;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.player.PlayerEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/CrystalAttackTarget.class */
public final class CrystalAttackTarget {
    public final PlayerEntity playerEntity;
    public final double val;
    public final AutoCrystalDataMode autoCrystalDataMode;
    public final DamageOutcome autoCrystalMode_7;

    public CrystalAttackTarget(PlayerEntity playerEntity, double d, AutoCrystalDataMode autoCrystalDataMode, DamageOutcome autoCrystalMode_7) {
        this.playerEntity = playerEntity;
        this.val = d;
        this.autoCrystalDataMode = autoCrystalDataMode;
        this.autoCrystalMode_7 = autoCrystalMode_7;
    }




    public PlayerEntity getPlayerTarget() {
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
