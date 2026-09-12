package me.mioclient.manager;

import me.mioclient.util.registry.NamedRegistryList;

import me.mioclient.util.concurrent.AsyncTaskState;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.concurrent.AsyncTaskState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AsyncTaskTracker.class */
public final class AsyncTaskTracker extends NamedRegistryList<AsyncTaskState> implements ClientAccessor {
    public final List<CompletableFuture<?>> list = new ArrayList();
    public int num = 0;

    public void registerCallback(String str, BiConsumer<String, ? super Throwable> biConsumer) {
    }

    public boolean isAllTasksComplete() {
        Iterator<CompletableFuture<?>> it = this.list.iterator();
        while (it.hasNext()) {
            if (!it.next().isDone()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyTaskFailed() {
        Iterator<CompletableFuture<?>> it = this.list.iterator();
        while (it.hasNext()) {
            if (it.next().isCompletedExceptionally()) {
                return true;
            }
        }
        return false;
    }

    public int getCompletedCount() {
        return this.num;
    }
}
