package me.mioclient.module.combat.automine;

public interface MineTargetStrategy {
    void findTargets(MineTargetState autoMineHelper);

    boolean isEnabled();
}