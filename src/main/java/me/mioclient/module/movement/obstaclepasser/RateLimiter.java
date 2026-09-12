package me.mioclient.module.movement.obstaclepasser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/RateLimiter.class */
public final class RateLimiter {
    public final List<Long> list;
    public final int num;
    public final int num2;

    public RateLimiter(int i) {
        this(1000, i);
    }

    public RateLimiter(int i, int i2) {
        this.list = Collections.synchronizedList(new ArrayList());
        this.num = i;
        this.num2 = i2;
    }

    public void recordTimestamp() {
        this.list.add(Long.valueOf(System.currentTimeMillis()));
    }

    public boolean isRateExceeded() {
        return this.list.size() > this.num2;
    }

    public void recalculateLayout() {
        this.list.removeIf(l -> {
            return System.currentTimeMillis() > l.longValue() + ((long) this.num);
        });
    }

    public void do978() {
        this.list.clear();
    }
}
