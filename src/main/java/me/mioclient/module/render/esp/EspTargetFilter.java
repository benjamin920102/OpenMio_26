package me.mioclient.module.render.esp;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.BoxMath;

import me.mioclient.api.ClientAccessor;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.BoxMath;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EspTargetFilter.class */
public class EspTargetFilter implements ClientAccessor {
    public final Map<String, Integer> map = new TreeMap(Comparator.comparing(obj -> {
        return (String) obj;
    }).reversed());
    public final Box box;
    public Box box2;
    public float val;

    public EspTargetFilter(ItemEntity itemEntity) {
        float tickDelta = minecraftClient.getRenderTickCounter().getTickDelta(true);
        this.box = Box.of(itemEntity.getLerpedPos(tickDelta), Double.longBitsToDouble(4617315517961601024L), Double.longBitsToDouble(4617315517961601024L), Double.longBitsToDouble(4617315517961601024L));
        this.box2 = BoxMath.getInterpolatedBox((Entity) itemEntity, tickDelta);
        addTrackedItem(itemEntity);
    }

    public Box getClusterBox() {
        return this.box;
    }

    public Box getMergedBox() {
        return this.box2;
    }

    public boolean addTrackedItem(ItemEntity itemEntity) {
        if (!this.box.intersects(itemEntity.getBoundingBox())) {
            return false;
        }
        float tickDelta = minecraftClient.getRenderTickCounter().getTickDelta(true);
        String string = itemEntity.getName().getString();
        this.map.compute(string, (str, num) -> {
            return Integer.valueOf(itemEntity.getStack().getCount() + (num == null ? 0 : num.intValue()));
        });
        this.val = Math.max(ClientTextRenderer.INSTANCE.getStringWidth(formatItemCountLabel(string, this.map.get(string).intValue())), this.val);
        if (new Vec3d(itemEntity.getX() - itemEntity.prevX, itemEntity.getY() - itemEntity.prevY, itemEntity.getZ() - itemEntity.prevZ).lengthSquared() >= Double.longBitsToDouble(4547007122018943789L)) {
            return true;
        }
        this.box2 = this.box2.union(BoxMath.getInterpolatedBox((Entity) itemEntity, tickDelta));
        return true;
    }

    public Map<String, Integer> getItemCountMap() {
        return this.map;
    }

    public float getMaxLabelWidth() {
        return this.val;
    }

    public static String formatItemCountLabel(String str, int i) {
        return i <= 1 ? str : new StringTemplate().arg(i).arg(str).format("\u0001 x\u0001");
    }
}
