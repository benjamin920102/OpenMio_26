package me.mioclient.manager;

import me.mioclient.util.world.HoleData;

import me.mioclient.manager.HoleTracker;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;

public class HoleScanTask implements java.lang.Runnable {
    public static final MutableBlockPos mutable = new MutableBlockPos();
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
        if (Minecraft.getInstance() == null || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        ArrayList<HoleData> arrayList = new ArrayList<>();
        BlockPos blockPos = Minecraft.getInstance().player.blockPosition();
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
