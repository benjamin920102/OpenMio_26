package me.mioclient.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import me.mioclient.event.Listen;
import me.mioclient.event.EventBus;

public class ReflectiveEventBus implements EventBus {
    public final Map<Object, List<EventListener>> instanceListeners = new ConcurrentHashMap();
    public final Map<Class<?>, List<EventListener>> staticListeners = new ConcurrentHashMap();
    public final Map<Class<?>, List<EventListener>> activeListeners = new ConcurrentHashMap();
    public final List<Inner> lookupList = new ArrayList();

    
    
    public static class Inner {
        public final String prefix;
        public final LambdaEventListener.Inner inner;

        public Inner(String str, LambdaEventListener.Inner inner) {
            this.prefix = str;
            this.inner = inner;
        }
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public void registerLookup(String str, LambdaEventListener.Inner inner) {
        synchronized (this.lookupList) {
            this.lookupList.add(new Inner(str, inner));
        }
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public <T> T fireEvent(T t) {
        List<EventListener> lookupList = this.activeListeners.get(t.getClass());
        if (lookupList != null) {
            Iterator<EventListener> it = lookupList.iterator();
            while (it.hasNext()) {
                EventListener listener = it.next();
                try {
                    listener.accept(t);
                } catch (NullPointerException e) {
                    // Skip handlers with uninitialized settings
                    e.printStackTrace();
                } catch (Exception e) {
                    // Skip failing handlers to prevent cascading crashes
                    e.printStackTrace();
                }
            }
        }
        return t;
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public <T extends Cancellable> T fireCancellableEvent(T t) {
        List<EventListener> lookupList = this.activeListeners.get(t.getClass());
        if (lookupList != null) {
            t.setCancelled(false);
            Iterator<EventListener> it = lookupList.iterator();
            while (it.hasNext()) {
                EventListener listener = it.next();
                try {
                    listener.accept(t);
                } catch (NullPointerException e) {
                    // Skip handlers with uninitialized settings
                    e.printStackTrace();
                } catch (Exception e) {
                    // Skip failing handlers to prevent cascading crashes
                    e.printStackTrace();
                }
                if (t.isCancelled()) {
                    break;
                }
            }
        }
        return t;
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public void register(Object obj) {
        addListenerBatch(getListeners(obj.getClass(), obj), false);
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public void registerStatic(Class<?> cls) {
        addListenerBatch(getListeners(cls, null), true);
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public void addListener(EventListener baritoneHelper_5) {
        addListenerInternal(baritoneHelper_5, false);
    }

    public void addListenerBatch(List<EventListener> lookupList, boolean z) {
        Iterator<EventListener> it = lookupList.iterator();
        while (it.hasNext()) {
            addListenerInternal(it.next(), z);
        }
    }

    public void addListenerInternal(EventListener baritoneHelper_5, boolean z) {
        if (!z) {
            insertSorted(this.activeListeners.computeIfAbsent(baritoneHelper_5.getEventType(), cls -> {
                return new CopyOnWriteArrayList();
            }), baritoneHelper_5);
        } else if (baritoneHelper_5.isStaticListener()) {
            insertSorted(this.activeListeners.computeIfAbsent(baritoneHelper_5.getEventType(), cls2 -> {
                return new CopyOnWriteArrayList();
            }), baritoneHelper_5);
        }
    }

    public void insertSorted(List<EventListener> lookupList, EventListener baritoneHelper_5) {
        int i = 0;
        while (i < lookupList.size() && baritoneHelper_5.getPriority() <= lookupList.get(i).getPriority()) {
            i++;
        }
        lookupList.add(i, baritoneHelper_5);
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public void unregister(Object obj) {
        removeListenerBatch(getListeners(obj.getClass(), obj), false);
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public void unregisterStatic(Class<?> cls) {
        removeListenerBatch(getListeners(cls, null), true);
    }

    @Override // me.mioclient.mixin.ducks.BaritoneMixinInterface
    public void removeListener(EventListener baritoneHelper_5) {
        removeListenerInternal(baritoneHelper_5, false);
    }

    public void removeListenerBatch(List<EventListener> lookupList, boolean z) {
        Iterator<EventListener> it = lookupList.iterator();
        while (it.hasNext()) {
            removeListenerInternal(it.next(), z);
        }
    }

    public void removeListenerInternal(EventListener baritoneHelper_5, boolean z) {
        List<EventListener> lookupList = this.activeListeners.get(baritoneHelper_5.getEventType());
        if (lookupList != null) {
            if (!z) {
                lookupList.remove(baritoneHelper_5);
            } else if (baritoneHelper_5.isStaticListener()) {
                lookupList.remove(baritoneHelper_5);
            }
        }
    }

    public List<EventListener> getListeners(Class<?> cls, Object obj) {
        Function<Object, List<EventListener>> function = obj2 -> {
            CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
            scanMethods(copyOnWriteArrayList, cls, obj);
            return copyOnWriteArrayList;
        };
        if (obj == null) {
            return this.staticListeners.computeIfAbsent(cls, function);
        }
        Iterator<Object> it = this.instanceListeners.keySet().iterator();
        while (it.hasNext()) {
            if (it.next() == obj) {
                return this.instanceListeners.get(obj);
            }
        }
        List<EventListener> lookupList = (List) function.apply(obj);
        this.instanceListeners.put(obj, lookupList);
        return lookupList;
    }

    public void scanMethods(List<EventListener> lookupList, Class<?> cls, Object obj) {
        for (Method method : cls.getDeclaredMethods()) {
            if (isValidListener(method)) {
                lookupList.add(new LambdaEventListener(getLookupForClass(cls), cls, obj, method));
            }
        }
        if (cls.getSuperclass() != null) {
            scanMethods(lookupList, cls.getSuperclass(), obj);
        }
    }

    public boolean isValidListener(Method method) {
        return method.isAnnotationPresent(Listen.class) && method.getReturnType() == Void.TYPE && method.getParameterCount() == 1 && !method.getParameters()[0].getType().isPrimitive();
    }

    public LambdaEventListener.Inner getLookupForClass(Class<?> cls) {
        synchronized (this.lookupList) {
            for (Inner inner : this.lookupList) {
                if (cls.getName().startsWith(inner.prefix)) {
                    return inner.inner;
                }
            }
            throw new ListenerLookupException(cls);
        }
    }
}
