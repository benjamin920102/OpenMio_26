package me.mioclient.mode.player;

import me.mioclient.api.NamedValue;

import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;

public enum AutoCraftMode implements ClientAccessor, NamedValue {
    FAST("Fast", new Vec3(MathConstants.val2, MathConstants.val2, MathConstants.val2)),
    X2("X2", new Vec3(MathConstants.val2, 0.05d, MathConstants.val2), new Vec3(MathConstants.val2, 1.0d, MathConstants.val2)),
    X4("X4", new Vec3(0.05d, 0.05d, 0.05d), new Vec3(0.05d, 0.05d, 0.95d), new Vec3(0.95d, 0.05d, 0.05d), new Vec3(0.95d, 0.05d, 0.95d)),
    X8("X8", new Vec3(0.05d, 0.05d, 0.05d), new Vec3(0.05d, 0.05d, 0.95d), new Vec3(0.05d, 0.95d, 0.05d), new Vec3(0.05d, 0.95d, 0.95d), new Vec3(0.95d, 0.05d, 0.05d), new Vec3(0.95d, 0.05d, 0.95d), new Vec3(0.95d, 0.95d, 0.05d), new Vec3(0.95d, 0.95d, 0.95d));

    public final List<Vec3> list = new ArrayList();
    public final String name;

    AutoCraftMode(String str, Vec3... vec3dArr) {
        this.name = str;
        this.list.addAll(Arrays.asList(vec3dArr));
    }

    public List<Vec3> getCornerOffsets() {
        return this.list;
    }

    public List<Vec3> getWorldCorners(BlockPos blockPos) {
        return (List) getCornerOffsets().stream().map(vec3d -> {
            Vec3 add = vec3d.add(new Vec3((Vec3i) blockPos));
            return (this != X4 || minecraftClient.player.getEyeY() <= ((double) (blockPos.getY() + 1))) ? add : add.add(0.0d, 0.9d, 0.0d);
        }).collect(Collectors.toList());
    }

    public List<Vec3> getVisibleFaceCorners(BlockPos blockPos, Direction direction) {
        ArrayList arrayList = new ArrayList();
        for (Vec3 vec3d : this.list) {
            if (!isAxisAligned(direction.getStepX(), (int) vec3d.x)) {
                if (!isAxisAligned(direction.getStepY(), (int) vec3d.y)) {
                    if (!isAxisAligned(direction.getStepZ(), (int) vec3d.z)) {
                        arrayList.add(vec3d.add(new Vec3((Vec3i) blockPos)));
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
