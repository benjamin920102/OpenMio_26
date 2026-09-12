package me.mioclient.manager;

import me.mioclient.util.registry.NamedRegistryList;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.util.io.FileIO;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.registry.NamedRegistryList;
import me.mioclient.util.io.FileIO;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PresetFileRegistry.class */
public final class PresetFileRegistry extends NamedRegistryList<PresetEntry> implements ClientAccessor {
    public static final String string = ".json";
    public final PresetHelperMode presetHelperMode;
    public final Path path;

    public PresetFileRegistry(PresetHelperMode presetHelperMode) {
        this.path = presetHelperMode.getPresetPath();
        this.presetHelperMode = presetHelperMode;
    }

    public void do34() {
        ((List) this.registry).clear();
        try {
            for (File file : (File[]) Objects.requireNonNull(this.path.toFile().listFiles())) {
                if (file.getName().endsWith(".json") && file.length() > 0) {
                    try {
                        JsonObject asJsonObject = JsonParser.parseString(FileIO.readString(file.toPath())).getAsJsonObject();
                        if (asJsonObject != null) {
                            ((List) this.registry).add(new PresetEntry(file.getName().replace(".json", ""), asJsonObject));
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loadPreset(String str) {
        synchronized (getRegistry()) {
            for (PresetEntry presetSearchHelper4 : getRegistry()) {
                if (presetSearchHelper4.getName().equalsIgnoreCase(str)) {
                    this.presetHelperMode.fromJson(presetSearchHelper4.getJsonObject());
                    return true;
                }
            }
            return false;
        }
    }

    public boolean deletePreset(String str) {
        this.path.resolve(new StringTemplate().arg(str).format("\u0001.json")).toFile().delete();
        return ((List<PresetEntry>) this.registry).removeIf(presetSearchHelper4 -> {
            return presetSearchHelper4.getName().equalsIgnoreCase(str);
        });
    }

    public boolean copyPreset(String str, String str2) {
        for (PresetEntry presetSearchHelper4 : (List<PresetEntry>) this.registry) {
            if (presetSearchHelper4.getName().equalsIgnoreCase(str)) {
                try {
                    writeStringToFile(this.path, str2, gson.toJson(presetSearchHelper4.getJsonObject()));
                    ((List) this.registry).add(new PresetEntry(str2, presetSearchHelper4.getJsonObject()));
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public boolean renamePreset(String str, String str2) {
        if (str.equalsIgnoreCase(str2)) {
            return false;
        }
        if (this.path.resolve(new StringTemplate().arg(str).format("\u0001.json")).toFile().exists()) {
            this.path.resolve(new StringTemplate().arg(str).format("\u0001.json")).toFile().renameTo(this.path.resolve(new StringTemplate().arg(str2).format("\u0001.json")).toFile());
        }
        if (copyPreset(str, str2)) {
            if (((List<PresetEntry>) this.registry).removeIf(presetSearchHelper4 -> {
                return presetSearchHelper4.getName().equalsIgnoreCase(str);
            })) {
                return true;
            }
        }
        return false;
    }

    public void savePreset(String str) {
        ((List<PresetEntry>) this.registry).removeIf(presetSearchHelper4 -> {
            return presetSearchHelper4.getName().equalsIgnoreCase(str);
        });
        PresetEntry presetLlamaRenderHelper = new PresetEntry(str, this.presetHelperMode.toJson().getAsJsonObject());
        presetLlamaRenderHelper.saveTo(this.path);
        ((List) this.registry).add(presetLlamaRenderHelper);
    }

    public void saveMapCache(String str) {
        for (PresetEntry presetSearchHelper4 : (List<PresetEntry>) this.registry) {
            if (presetSearchHelper4.getRawName().equals(str)) {
                presetSearchHelper4.saveTo(this.path);
            }
        }
    }

    public void do41() {
        Iterator it = ((List) this.registry).iterator();
        while (it.hasNext()) {
            try {
                ((PresetEntry) it.next()).saveTo(this.path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeStringToFile(Path path, String str, String str2) {
        FileIO.writeString(path.resolve(str), str2);
    }
}
