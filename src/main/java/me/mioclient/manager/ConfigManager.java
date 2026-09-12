package me.mioclient.manager;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.command.PanicCommand;

import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.util.io.FileIO;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.io.FileIO;

import com.google.gson.JsonParser;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import me.mioclient.command.PanicCommand;
import me.mioclient.mode.misc.PresetHelperMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ConfigManager.class */
public final class ConfigManager implements ClientAccessor {
    public static final Path path = FabricLoader.getInstance().getGameDir().resolve("mio-fabric");
    public static final Path path2 = path.resolve("presets");
    public static final Path path3 = path.resolve("spammer");
    public static final Path path4 = path.resolve("autoez");
    public static final Path path5 = path.resolve("textures");
    public static final Path path6 = path.resolve("maps");
    public static final Path path7 = path.resolve("chunks");
    public static final Path path8 = path.resolve("sounds");
    public static final Path path9 = Path.of(System.getProperty("user.home"), "_____a");
    public static final List<Path> list = new ArrayList();
    public final List<JsonConfigurable> list2 = List.<JsonConfigurable>of(new JsonConfigurable[]{ClientServices.discordNotifsHelper, ClientServices.moduleRegistry, ClientServices.socialManager, ClientServices.waypointsManager, ClientServices.macroManager, ClientServices.stashFinderDataManager, ClientServices.hudModuleRegistry, ClientServices.nameTagsManager, ClientServices.chestStealerHelper, ClientServices.notificationsHelper, ClientServices.chatFilterHelper, ClientServices.items, ClientServices.chatFilterChatHelper});
    public final Map<PresetHelperMode, PresetFileRegistry> map = new HashMap();
    public final DefaultResourceExtractor presetHelper_6 = new DefaultResourceExtractor();
    public final ConfigSnapshot presetHelper_3 = new ConfigSnapshot();

    public void createDirectories() {
        list.stream().filter(path10 -> {
            return !path10.toFile().exists();
        }).forEachOrdered(path11 -> {
            path11.toFile().mkdir();
        });
        this.presetHelper_6.extractDefaults();
    }

    public void extractDefaults() {
        list.addAll(Arrays.asList(path, path2, path3, path4, path5, path6, path8));
        list.add(path7);
        for (PresetHelperMode presetHelperMode : PresetHelperMode.values()) {
            list.add(presetHelperMode.getPresetPath());
        }
        createDirectories();
        try {
            FriendsFileMigrator.run();
        } catch (Throwable th) {
        }
        for (JsonConfigurable presetHelper_7 : this.list2) {
            try {
                Path resolve = path.resolve(presetHelper_7.getConfigName());
                if (resolve.toFile().exists()) {
                    presetHelper_7.fromJson(JsonParser.parseString(FileIO.readString(resolve)).getAsJsonObject());
                }
            } catch (Throwable th2) {
                th2.printStackTrace();
            }
        }
        for (PresetHelperMode presetHelperMode2 : PresetHelperMode.values()) {
            PresetFileRegistry presetHelperSearchHelper4_2 = new PresetFileRegistry(presetHelperMode2);
            this.map.put(presetHelperMode2, presetHelperSearchHelper4_2);
            presetHelperSearchHelper4_2.do34();
        }
    }

    public void do41() {
        if (PanicCommand.flag) {
            return;
        }
        for (JsonConfigurable presetHelper_7 : this.list2) {
            try {
                FileIO.writeString(path.resolve(presetHelper_7.getConfigName()), gson.toJson(presetHelper_7.toJson().getAsJsonObject()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public PresetFileRegistry getPresetRegistry(PresetHelperMode presetHelperMode) {
        return this.map.get(presetHelperMode);
    }

    public ConfigSnapshot getSnapshot() {
        return this.presetHelper_3;
    }
}
