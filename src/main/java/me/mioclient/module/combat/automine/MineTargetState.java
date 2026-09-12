package me.mioclient.module.combat.automine;

import java.util.function.Consumer;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/MineTargetState.class */
public class MineTargetState {
    public BlockPos blockPos;
    public BlockPos blockPos2;
    public boolean flag;
    public int num;

    public void reset() {
        this.blockPos = null;
        this.flag = false;
    }

    public void markCompleted() {
        this.flag = true;
    }

    public boolean isCompleted() {
        return this.flag;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getSecondaryBlockPos() {
        return this.blockPos2;
    }

    public void setSecondaryBlockPos(BlockPos blockPos) {
        this.blockPos2 = blockPos;
    }

    public int getPriority() {
        return this.num;
    }

    public void setPriority(int i) {
        this.num = i;
    }

    public void tryUpdatePriority(int i, Consumer<MineTargetState> consumer) {
        if (this.num <= i || getBlockPos() == null || isCompleted()) {
            consumer.accept(this);
            this.num = i;
        }
    }
}
