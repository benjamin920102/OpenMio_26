package me.mioclient.manager;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.mioclient.module.Module;

public class ModuleFieldInjector {
    public List<String> classNameCache = new ArrayList();

    public ModuleFieldInjector() {
    }

    public <T extends Module> T getModule(Class<T> cls) {
        if (ClientServices.moduleRegistry != null) {
            return (T) ClientServices.moduleRegistry.findFirstByClass(cls);
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
        while (it.hasNext()) {
            try {
                Class<?> cls = Class.forName(it.next(), false, ClientAccessor.class.getClassLoader());
                fixModuleFields(cls);
            } catch (Exception e2) {
                throw new java.lang.RuntimeException();
            }
        }
        this.classNameCache.clear();
        System.gc();
    }

    public void refreshAllModules() {
        if (ClientServices.moduleRegistry == null) {
            return;
        }
        for (String className : new ArrayList<>(this.classNameCache)) {
            try {
                Class<?> cls = Class.forName(className, false, ClientAccessor.class.getClassLoader());
                fixModuleFields(cls);
            } catch (Exception e) {
            }
        }
    }

    private static void setStaticField(Field field, Object value) throws Exception {
        field.setAccessible(true);
        if (!Modifier.isFinal(field.getModifiers())) {
            field.set(null, value);
            return;
        }
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
        VarHandle varHandle = lookup.findStaticVarHandle(field.getDeclaringClass(), field.getName(), field.getType());
        varHandle.set(value);
    }

    private void fixModuleFields(Class<?> cls) {
        if (ClientServices.moduleRegistry == null) return;
        try {
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Module.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        Object current = field.get(null);
                        if (current == null) {
                            Module mod = (Module) ClientServices.moduleRegistry.findFirstByClass((Class) field.getType());
                            if (mod != null) {
                                setStaticField(field, mod);
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
