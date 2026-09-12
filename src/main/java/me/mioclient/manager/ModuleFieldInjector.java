package me.mioclient.manager;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.mioclient.module.Module;
import sun.misc.Unsafe;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ModuleFieldInjector.class */
public class ModuleFieldInjector {
    public Unsafe unsafe;
    public List<String> classNameCache = new ArrayList();

    public ModuleFieldInjector() {
        try {
            Field declaredField = Unsafe.class.getDeclaredField("theUnsafe");
            declaredField.setAccessible(true);
            this.unsafe = (Unsafe) declaredField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends Module> T getModule(Class<T> cls) {
        if (ClientServices.moduleRegistry != null) {
            return (T) ClientServices.moduleRegistry.findFirstByClass(cls);
        }
        if (this.unsafe == null) {
            return null;
        }
        try {
            String str = null;
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int length = stackTrace.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                String className = stackTrace[i].getClassName();
                if (!className.contains("java.lang.Thread") && !className.contains(getClass().getName())) {
                    str = className;
                    break;
                }
                i++;
            }
            if (str == null) {
                throw new java.lang.RuntimeException();
            }
            if (!this.classNameCache.contains(str)) {
                this.classNameCache.add(str);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void injectModuleFields() {
        Iterator<String> it = this.classNameCache.iterator();
        loop0: while (it.hasNext()) {
            try {
                Class<?> cls = Class.forName(it.next(), false, ClientAccessor.class.getClassLoader());
                for (Field field : cls.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) && Module.class.isAssignableFrom(field.getType())) {
                        try {
                            Module enumSettingHelper120 = (Module)(ClientServices.moduleRegistry.findFirstByClass((Class) field.getType()));
                            if (enumSettingHelper120 == null) {
                                throw new java.lang.RuntimeException();
                            }
                            this.unsafe.putObject(cls, this.unsafe.staticFieldOffset(field), enumSettingHelper120);
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e2) {
                throw new java.lang.RuntimeException();
            }
        }
        this.classNameCache.clear();
        System.gc();
    }

    public void refreshAllModules() {
        if (ClientServices.moduleRegistry == null || this.unsafe == null) {
            return;
        }
        try {
            java.util.Set<Class<?>> loaded = new java.util.HashSet<>();
            java.util.Stack<ClassLoader> stack = new java.util.Stack<>();
            stack.push(ClientAccessor.class.getClassLoader());
            while (!stack.isEmpty()) {
                ClassLoader cl = stack.pop();
                if (cl == null) continue;
                try {
                    Field classesField = cl.getClass().getDeclaredField("classes");
                    classesField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    java.util.Vector<Class<?>> classes = (java.util.Vector<Class<?>>) classesField.get(cl);
                    if (classes != null) {
                        for (Class<?> cls : classes) {
                            if (cls != null && loaded.add(cls)) {
                                fixModuleFields(cls);
                            }
                        }
                    }
                } catch (Exception e) {
                }
                try {
                    Field parentField = ClassLoader.class.getDeclaredField("parent");
                    parentField.setAccessible(true);
                    ClassLoader parent = (ClassLoader) parentField.get(cl);
                    if (parent != null && !loaded.contains(parent.getClass())) {
                        stack.push(parent);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    private void fixModuleFields(Class<?> cls) {
        if (ClientServices.moduleRegistry == null || this.unsafe == null) return;
        try {
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Module.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Object current = field.get(null);
                        if (current == null) {
                            Module mod = (Module) ClientServices.moduleRegistry.findFirstByClass((Class) field.getType());
                            if (mod != null) {
                                this.unsafe.putObject(cls, this.unsafe.staticFieldOffset(field), mod);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
