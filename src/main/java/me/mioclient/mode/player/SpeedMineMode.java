package me.mioclient.mode.player;

import me.mioclient.api.NamedValue;

import me.mioclient.module.player.SpeedMine;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public enum SpeedMineMode implements NamedValue {
    PLAIN("Plain") { // from class: me.mioclient.mode.player.SpeedMineMode.Inner_2
        @Override // me.mioclient.mode.player.SpeedMineMode
        public AABB getRenderBox(SpeedMine speedMine, AABB box, float f) {
            return box;
        }
    },
    IN("In") { // from class: me.mioclient.mode.player.SpeedMineMode.Inner_3
        @Override // me.mioclient.mode.player.SpeedMineMode
        public AABB getRenderBox(SpeedMine speedMine, AABB box, float f) {
            float f2 = 1.0f - f;
            double abs = Math.abs(box.getXsize());
            double abs2 = Math.abs(box.getYsize());
            double abs3 = Math.abs(box.getZsize());
            Vec3 center = box.getCenter();
            return new AABB(center.subtract((f2 * abs) / 2.0d, (f2 * abs2) / 2.0d, (f2 * abs3) / 2.0d), center.add((f2 * abs) / 2.0d, (f2 * abs2) / 2.0d, (f2 * abs3) / 2.0d));
        }
    },
    OUT("Out") { // from class: me.mioclient.mode.player.SpeedMineMode.Inner
        @Override // me.mioclient.mode.player.SpeedMineMode
        public AABB getRenderBox(SpeedMine speedMine, AABB box, float f) {
            double abs = Math.abs(box.getXsize());
            double abs2 = Math.abs(box.getYsize());
            double abs3 = Math.abs(box.getZsize());
            Vec3 center = box.getCenter();
            return new AABB(center.subtract((f * abs) / 2.0d, (f * abs2) / 2.0d, (f * abs3) / 2.0d), center.add((f * abs) / 2.0d, (f * abs2) / 2.0d, (f * abs3) / 2.0d));
        }
    };

    public final String name;

    SpeedMineMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public AABB getRenderBox(SpeedMine speedMine, AABB box, float f) {
        return null;
    }
}
