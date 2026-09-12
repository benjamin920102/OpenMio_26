package me.mioclient.util.math;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Tuple.class */
public final class Tuple<L, R> {
    public final L object;
    public final R object2;

    public Tuple(L l, R r) {
        this.object = l;
        this.object2 = r;
    }




    public L getLeft() {
        return this.object;
    }

    public R getRight() {
        return this.object2;
    }
}
