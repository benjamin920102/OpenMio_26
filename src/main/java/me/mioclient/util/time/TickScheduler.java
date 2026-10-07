package me.mioclient.util.time;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.util.registry.CollectionRegistry;

import me.mioclient.util.registry.AbstractCollectionRegistry;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import me.mioclient.event.Listen;
import me.mioclient.render.RenderConstants;

public class TickScheduler extends AbstractCollectionRegistry<TickScheduler.Inner, List<TickScheduler.Inner>> implements ClientAccessor {
    public final Queue<Inner> queue;

    
    public static class Inner {
        public final java.lang.Runnable runnable;
        public int num;

        public Inner(java.lang.Runnable runnable, int i) {
            this.runnable = runnable;
            this.num = i;
        }

        public java.lang.Runnable getRunnable() {
            return this.runnable;
        }

        public int get2093() {
            return this.num;
        }

        public boolean tickAndRun() {
            int i = this.num;
            this.num = i - 1;
            if (i >= 0) {
                return false;
            }
            this.runnable.run();
            return true;
        }
    }

    public TickScheduler() {
        super(Collections.synchronizedList(new ArrayList()));
        this.queue = new ArrayDeque();
        eventBus.register(this);
    }

    @Listen(get219= RenderConstants.num4)
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        synchronized (((List) this.registry)) {
            while (!this.queue.isEmpty()) {
                ((List) this.registry).add(this.queue.poll());
            }
            ((List<Inner>) this.registry).removeIf((v0) -> {
                return v0.tickAndRun();
            });
        }
    }

    public void schedule(java.lang.Runnable runnable, int i) {
        Inner inner = new Inner(runnable, i);
        if (minecraftClient.isSameThread()) {
            register(inner);
            return;
        }
        minecraftClient.execute(() -> {
            register(inner);
        });
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is165, reason: merged with bridge method [inline-methods] */
    public boolean register(Inner inner) {
        boolean add;
        synchronized (((List) this.registry)) {
            add = this.queue.add(inner);
        }
        return add;
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is166, reason: merged with bridge method [inline-methods] */
    public boolean unregister(Inner inner) {
        throw new UnsupportedOperationException();
    }
}
