package me.mioclient.manager;

import me.mioclient.api.NamedValue;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.NamedValue;
import me.mioclient.util.io.FileIO;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.NamedValue;
import me.mioclient.util.io.FileIO;

import com.google.gson.JsonObject;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PresetEntry.class */
public final class PresetEntry implements ClientAccessor, NamedValue {
    public final String name;
    public final JsonObject jsonObject;

    public PresetEntry(String str, JsonObject jsonObject) {
        this.name = str;
        this.jsonObject = jsonObject;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public void saveTo(Path path) {
        FileIO.writeString(path.resolve(new StringTemplate().arg(this.name).format("\u0001.json")), gson.toJson(getJsonObject()));
    }




    public String getRawName() {
        return this.name;
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }
}
