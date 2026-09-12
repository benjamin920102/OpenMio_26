package me.mioclient.manager;

import me.mioclient.util.world.HoleData;

import me.mioclient.util.world.HoleData;
import me.mioclient.manager.HoleTracker;

import java.util.ArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Runnable.class */
public class HoleScanTask implements java.lang.Runnable {
    public static final BlockPos.Mutable mutable = new BlockPos.Mutable();
    public final HoleTracker holeSnapOverlayHelper;

    public HoleScanTask(HoleTracker holeSnapOverlayHelper) {
        this.holeSnapOverlayHelper = holeSnapOverlayHelper;
    }

    @Override // java.lang.Runnable
    public void run() {
        while (true) {
            try {
                scanNearbyHoles();
                Thread.sleep(50L);
            } catch (Throwable th) {
            }
        }
    }

    public void scanNearbyHoles() {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().world == null) {
            return;
        }
        ArrayList<HoleData> arrayList = new ArrayList<>();
        BlockPos blockPos = MinecraftClient.getInstance().player.getBlockPos();
        for (int i = -16; i < 16; i++) {
            for (int i2 = -16; i2 < 16; i2++) {
                for (int i3 = -16; i3 < 16; i3++) {
                    mutable.set(blockPos.getX() + i, blockPos.getY() + i2, blockPos.getZ() + i3);
                    HoleData holeSnapData2724 = HoleTracker.detectHole(mutable, (Direction) null);
                    if (holeSnapData2724 != null) {
                        if (!arrayList.stream().anyMatch(holeSnapData -> {
                            return holeSnapData2724.getBox().intersects(holeSnapData.getBox());
                        })) {
                            arrayList.add(holeSnapData2724);
                        }
                    }
                }
            }
        }
        this.holeSnapOverlayHelper.setHoles(arrayList);
    }
}
