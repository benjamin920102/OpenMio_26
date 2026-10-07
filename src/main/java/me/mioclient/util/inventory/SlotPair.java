package me.mioclient.util.inventory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

public final class SlotPair {
    public final int num;
    public final int num2;
    public static final SlotPair data_4 = new SlotPair(-1, -1);

    public SlotPair(int i, int i2) {
        this.num = i;
        this.num2 = i2;
    }

    public boolean isInvalid() {
        return this.num == -1 || this.num2 == -1;
    }

    public int getFirstSlot() {
        return this.num;
    }

    public int getSecondSlot() {
        return this.num2;
    }
}
