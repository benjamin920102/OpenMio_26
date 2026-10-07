package me.mioclient.gui.widget;

import me.mioclient.util.time.Stopwatch;

import java.util.ArrayList;

public class ScrollState {
    public static final int num = 5;
    public final java.util.List<String> list = new ArrayList();
    public final Stopwatch stopwatch = new Stopwatch();
    public int num2 = 0;

    public void recalculateLayout() {
        if (this.stopwatch.hasElapsedMs(1500L)) {
            this.num2++;
            this.stopwatch.reset();
        }
        if (this.num2 >= this.list.size()) {
            this.num2 = 0;
        }
    }

    public void reset() {
        this.list.clear();
        this.num2 = 0;
    }

    public java.util.List<String> getVisibleItems() {
        if (this.list.size() <= 5) {
            return this.list;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            arrayList.add(this.list.get((i + this.num2) % this.list.size()));
        }
        return arrayList;
    }

    public int get1296() {
        return this.num2;
    }

    public java.util.List<String> getAllItems() {
        return this.list;
    }
}
