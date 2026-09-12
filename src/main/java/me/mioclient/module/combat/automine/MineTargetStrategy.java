package me.mioclient.module.combat.automine;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MineTargetStrategy.class */
public interface MineTargetStrategy {
    void findTargets(MineTargetState autoMineHelper);

    boolean isEnabled();
}
