package me.mioclient.module.player.cheststealer;

public final class SlotStackCount {
    public final int num;
    public final int num2;
    public int num3;

    public SlotStackCount(int i, int i2, int i3) {
        this.num = i;
        this.num3 = i3;
        this.num2 = i2;
    }

    public int getSlotIndex() {
        return this.num;
    }

    public int getMaxCount() {
        return this.num2;
    }

    public int getCount() {
        return this.num3;
    }

    public void setCount(int i) {
        this.num3 = Math.min(i, getMaxCount());
    }
}