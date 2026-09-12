package me.mioclient.mode.player;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.NamedValue;
import me.mioclient.util.math.MathConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AutoCraftMode.class */
public enum AutoCraftMode implements ClientAccessor, NamedValue {
    FAST("Fast", new Vec3d(MathConstants.val2, MathConstants.val2, MathConstants.val2)),
    X2("X2", new Vec3d(MathConstants.val2, 0.05d, MathConstants.val2), new Vec3d(MathConstants.val2, 1.0d, MathConstants.val2)),
    X4("X4", new Vec3d(0.05d, 0.05d, 0.05d), new Vec3d(0.05d, 0.05d, 0.95d), new Vec3d(0.95d, 0.05d, 0.05d), new Vec3d(0.95d, 0.05d, 0.95d)),
    X8("X8", new Vec3d(0.05d, 0.05d, 0.05d), new Vec3d(0.05d, 0.05d, 0.95d), new Vec3d(0.05d, 0.95d, 0.05d), new Vec3d(0.05d, 0.95d, 0.95d), new Vec3d(0.95d, 0.05d, 0.05d), new Vec3d(0.95d, 0.05d, 0.95d), new Vec3d(0.95d, 0.95d, 0.05d), new Vec3d(0.95d, 0.95d, 0.95d));

    public final List<Vec3d> list = new ArrayList();
    public final String name;

    AutoCraftMode(String str, Vec3d... vec3dArr) {
        this.name = str;
        this.list.addAll(Arrays.asList(vec3dArr));
    }

    public List<Vec3d> getCornerOffsets() {
        return this.list;
    }

    public List<Vec3d> getWorldCorners(BlockPos blockPos) {
        return (List) getCornerOffsets().stream().map(vec3d -> {
            Vec3d add = vec3d.add(Vec3d.of((Vec3i) blockPos));
            return (this != X4 || minecraftClient.player.getEyeY() <= ((double) (blockPos.getY() + 1))) ? add : add.add(0.0d, 0.9d, 0.0d);
        }).collect(Collectors.toList());
    }

    public List<Vec3d> getVisibleFaceCorners(BlockPos blockPos, Direction direction) {
        ArrayList arrayList = new ArrayList();
        for (Vec3d vec3d : this.list) {
            if (!isAxisAligned(direction.getOffsetX(), (int) vec3d.x)) {
                if (!isAxisAligned(direction.getOffsetY(), (int) vec3d.y)) {
                    if (!isAxisAligned(direction.getOffsetZ(), (int) vec3d.z)) {
                        arrayList.add(vec3d.add(Vec3d.of((Vec3i) blockPos)));
                    }
                }
            }
        }
        return arrayList;
    }

    public boolean isAxisAligned(int i, int i2) {
        return (i | (i2 ^ (-1))) == 1;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}
