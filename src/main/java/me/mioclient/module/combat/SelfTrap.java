package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientServices;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import me.mioclient.module.Delay;
import me.mioclient.module.Module;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/SelfTrap.class */
public class SelfTrap extends Range {
    public final Object object;
    public Object object2;
    public Object object3;
    public Object object4;

    public SelfTrap() {
        super("SelfTrap", "Covers you with blocks.", Category.COMBAT);
        this.object = getObject1082();
        do1076();
        unregister((Setting<?>) this.setting);
        unregister((Setting<?>) this.setting7);
        unregister((Setting<?>) this.setting8);
    }

    @Override // me.mioclient.module.combat.Range, me.mioclient.module.Module
    public String getInfo() {
        return null;
    }

    @Override // me.mioclient.module.combat.Range, me.mioclient.module.Delay
    public List<BlockPos> getPlacePositions() {
        List<BlockPos> list876 = super.getPlacePositions();
        if ((isSmartEnabled() && getEntity1081() == null) || (is1077() && !ClientServices.holeSnapOverlayHelper.isInHole())) {
            return Collections.emptyList();
        }
        if (list876.isEmpty() && is1083()) {
            setToggled(false);
        }
        return list876;
    }

    @Override // me.mioclient.module.combat.Range
    public PlayerEntity getTargetPlayer() {
        return minecraftClient.player;
    }

    public void do1076() {
        this.object2 = add(new BooleanSetting("OnlyHole", false));
        this.object3 = add(new BooleanSetting("Smart", false).setVisible());
        this.object4 = add(new NumberSetting("EnemyRange", Float.valueOf(Float.intBitsToFloat(1077936128)), Float.valueOf(Float.intBitsToFloat(1065353216)), Float.valueOf(Float.intBitsToFloat(1086324736)), f -> {
            return is1079();
        }).setSuffix("m"));
    }

    public boolean is1077() {
        return ((Boolean) ((Setting) this.object2).getValue()).booleanValue();
    }

    public boolean isSmartEnabled() {
        return ((Boolean) ((Setting) this.object3).getValue()).booleanValue();
    }

    public boolean is1079() {
        return ((Setting) this.object3).isShown();
    }

    public float get1080() {
        return ((Float) ((Setting) this.object4).getValue()).floatValue();
    }

    public Entity getEntity1081() {
        Stream<Entity> filter = StreamSupport.stream(minecraftClient.world.getEntities().spliterator(), false).filter(entity -> {
            if (entity != minecraftClient.player && minecraftClient.player.distanceTo(entity) <= get1080() && (entity instanceof PlayerEntity)) {
                if (!ClientServices.socialManager.isFriend(entity.getName().getString()) && entity.isAlive()) {
                    return true;
                }
            }
            return false;
        });
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        Objects.requireNonNull(clientPlayerEntity);
        return (Entity) filter.min(Comparator.comparing(clientPlayerEntity::squaredDistanceTo)).orElse(null);
    }

    public Object getObject1082() {
        return add(new BooleanSetting("Complete", false).requiresSettings(this.setting8), this.setting8);
    }

    public boolean is1083() {
        return ((Boolean) ((Setting) this.object).getValue()).booleanValue();
    }
}
