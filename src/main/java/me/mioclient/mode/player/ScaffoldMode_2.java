package me.mioclient.mode.player;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;

import java.util.Collection;
import me.mioclient.api.Setting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ScaffoldMode_2.class */
public enum ScaffoldMode_2 implements NamedValue {
    ANY("Any"),
    WHITELIST("WhiteList") { // from class: me.mioclient.mode.player.ScaffoldMode_2.Inner
        @Override // me.mioclient.mode.player.ScaffoldMode_2
        public <T> boolean isAllowed(T t, Collection<T> collection) {
            return collection.contains(t);
        }
    },
    BLACKLIST("BlackList") { // from class: me.mioclient.mode.player.ScaffoldMode_2.Inner_2
        @Override // me.mioclient.mode.player.ScaffoldMode_2
        public <T> boolean isAllowed(T t, Collection<T> collection) {
            return !WHITELIST.isAllowed(t, collection);
        }
    };

    public final String name;

    ScaffoldMode_2(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public <T> boolean isAllowed(T t, Collection<T> collection) {
        return true;
    }

    public <T> boolean isAllowedBySetting(T t, Setting<? extends Collection<T>> setting) {
        return isAllowed(t, setting.getValue());
    }
}
