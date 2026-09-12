package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.math.BoxMath;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.BoxMath;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.setting.NumberSetting;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/FishTrap.class */
public class FishTrap extends Range {
    public Object object;

    public FishTrap() {
        super("FishTrap", "Traps your enemies in fish (crawling) position.", Category.COMBAT);
        addExtrapolationSetting();
        setPlacementEnabled(false);
        unregister((Setting<?>) this.setting3);
        unregister((Setting<?>) this.setting4);
        unregister((Setting<?>) this.setting5);
        unregister((Setting<?>) this.setting6);
        this.setting4.setValue(true);
        this.setting5.setValue(false);
        this.setting6.setValue(false);
    }

    @Override // me.mioclient.module.combat.Range
    public Vec3d getTargetPos(PlayerEntity playerEntity) {
        if (getExtrapolationSetting().isAtMinimum()) {
            return super.getTargetPos(playerEntity);
        }
        return BoxMath.getBottomCenter(ClientServices.mainhandHelper.getPredictedBox(playerEntity, getExtrapolationSetting().getValue().intValue()));
    }

    @Override // me.mioclient.module.combat.Range
    public boolean isValidRangeTarget(PlayerEntity playerEntity) {
        if (playerEntity.getBoundingBox().getLengthY() > Double.longBitsToDouble(4607182418800017408L)) {
            return false;
        }
        return super.isValidRangeTarget(playerEntity);
    }

    public void addExtrapolationSetting() {
        Setting add = add(new NumberSetting("Extrapolation", 0, 0, 6));
        add.setHoleSnapConfig("None", HoleSnapMode.MIN);
        this.object = add;
    }

    public Setting<Integer> getExtrapolationSetting() {
        return (Setting) this.object;
    }
}
