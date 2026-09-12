package me.mioclient.mode.misc;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.api.NamedValue;
import me.mioclient.module.HudModule;

import me.mioclient.api.NamedValue;
import me.mioclient.util.io.JsonValidator;
import me.mioclient.util.io.PresetJsonSanitizer;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.api.NamedValue;
import me.mioclient.manager.ConfigManager;
import me.mioclient.util.io.JsonValidator;
import me.mioclient.util.io.PresetJsonSanitizer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import me.mioclient.module.client.Colors;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.client.HUD;
import me.mioclient.module.client.Notifications;
import me.mioclient.module.client.UI;
import me.mioclient.setting.ColorSetting;
import me.mioclient.module.HudModule;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PresetHelperMode.class */
public enum PresetHelperMode implements NamedValue, JsonConfigurable {
    MODULES("Modules") { // from class: me.mioclient.mode.misc.PresetHelperMode.Inner_3
        @Override // me.mioclient.api.JsonConfigurable
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            writePresetMetadata(jsonObject);
            for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                if (!(module instanceof HudModule) && !(module instanceof Fonts)) {
                    JsonObject jsonObject2 = new JsonObject();
                    jsonObject2.addProperty("enabled", Boolean.valueOf(module.isToggled()));
                    JsonObject jsonObject3 = new JsonObject();
                    for (Setting<?> setting : module.getRegistry()) {
                        if (!(setting instanceof ColorSetting) && !setting.isDependent() && !setting.isHovered()) {
                            jsonObject3.add(setting.getConfigName(), setting.toJson());
                        }
                    }
                    if (!JsonValidator.isOnlyJsonObjects(jsonObject3)) {
                        jsonObject2.add("settings", (JsonElement) jsonObject3);
                    }
                    jsonObject.add(module.getConfigName(), (JsonElement) jsonObject2);
                }
            }
            return jsonObject;
        }

        @Override // me.mioclient.mode.misc.PresetHelperMode, me.mioclient.api.JsonConfigurable
        public void fromJson(JsonElement jsonElement) {
            PresetJsonSanitizer.presetHelperModeHelper_2.stripModuleKeys(jsonElement.getAsJsonObject());
            super.fromJson(jsonElement);
        }
    },
    BINDS("Binds") { // from class: me.mioclient.mode.misc.PresetHelperMode.Inner_4
        @Override // me.mioclient.api.JsonConfigurable
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            writePresetMetadata(jsonObject);
            for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                JsonElement jsonObject2 = new JsonObject();
                ((JsonObject) jsonObject2).addProperty("key", Integer.valueOf(module.getKeybind().getKey()));
                ((JsonObject) jsonObject2).addProperty("state", module.getKeybind().getMode().name().toLowerCase());
                ((JsonObject) jsonObject2).addProperty("mouse", Boolean.valueOf(module.getKeybind().isMouseKeybind()));
                jsonObject.add(module.getConfigName(), jsonObject2);
            }
            return jsonObject;
        }

        @Override // me.mioclient.mode.misc.PresetHelperMode, me.mioclient.api.JsonConfigurable
        public void fromJson(JsonElement jsonElement) {
            PresetJsonSanitizer.presetHelperModeHelper_2.do2097(jsonElement.getAsJsonObject());
            super.fromJson(jsonElement);
        }
    },
    COLORS("Colors") { // from class: me.mioclient.mode.misc.PresetHelperMode.Inner_5
        @Override // me.mioclient.api.JsonConfigurable
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            writePresetMetadata(jsonObject);
            for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                JsonElement jsonObject2 = new JsonObject();
                JsonObject jsonObject3 = new JsonObject();
                for (Setting<?> setting : module.getRegistry()) {
                    if ((setting instanceof ColorSetting) && !setting.isHovered()) {
                        jsonObject3.add(setting.getConfigName(), setting.toJson());
                    }
                }
                ((JsonObject) jsonObject2).add("settings", (JsonElement) jsonObject3);
                if (!JsonValidator.isOnlyJsonObjects((JsonObject) jsonObject2)) {
                    jsonObject.add(module.getConfigName(), jsonObject2);
                }
            }
            return jsonObject;
        }

        @Override // me.mioclient.mode.misc.PresetHelperMode, me.mioclient.api.JsonConfigurable
        public void fromJson(JsonElement jsonElement) {
            PresetJsonSanitizer.presetHelperModeHelper_2.sanitizeEnabledModules(jsonElement.getAsJsonObject(), module -> {
                return false;
            });
            super.fromJson(jsonElement);
        }
    },
    VISUALS("Visuals") { // from class: me.mioclient.mode.misc.PresetHelperMode.Inner_6
        public static final Set<Class<? extends Module>> set = new HashSet(List.of(UI.class, Fonts.class, HUD.class, Colors.class, Notifications.class));

        @Override // me.mioclient.api.JsonConfigurable
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            writePresetMetadata(jsonObject);
            for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                JsonObject jsonObject2 = new JsonObject();
                boolean isVisualModule = isVisualModule(module);
                if (isVisualModule) {
                    jsonObject2.addProperty("enabled", Boolean.valueOf(module.isToggled()));
                }
                JsonObject jsonObject3 = new JsonObject();
                for (Setting<?> setting : module.getRegistry()) {
                    if (!setting.isHovered() && ((setting instanceof ColorSetting) || isVisualModule)) {
                        jsonObject3.add(setting.getConfigName(), setting.toJson());
                    }
                }
                jsonObject2.add("settings", (JsonElement) jsonObject3);
                if (!JsonValidator.isOnlyJsonObjects(jsonObject2)) {
                    jsonObject.add(module.getConfigName(), (JsonElement) jsonObject2);
                }
            }
            return jsonObject;
        }

        @Override // me.mioclient.mode.misc.PresetHelperMode, me.mioclient.api.JsonConfigurable
        public void fromJson(JsonElement jsonElement) {
            PresetJsonSanitizer.presetHelperModeHelper_2.sanitizeEnabledModules(jsonElement.getAsJsonObject(), this::isVisualModule);
            super.fromJson(jsonElement);
        }

        public boolean isVisualModule(Module module) {
            if (module instanceof HudModule) {
                return true;
            }
            return set.contains(module.getClass()) || module.getCategory() == Category.RENDER || module.getCategory() == Category.HUD;
        }
    },
    MACRO("Macro") { // from class: me.mioclient.mode.misc.PresetHelperMode.Inner
        @Override // me.mioclient.api.JsonConfigurable
        public JsonElement toJson() {
            return ClientServices.macroManager.toJson();
        }

        @Override // me.mioclient.mode.misc.PresetHelperMode, me.mioclient.api.JsonConfigurable
        public void fromJson(JsonElement jsonElement) {
            synchronized (ClientServices.macroManager.getRegistry()) {
                ClientServices.macroManager.getRegistry().clear();
                ClientServices.macroManager.fromJson(jsonElement);
            }
        }
    },
    ALL("All") { // from class: me.mioclient.mode.misc.PresetHelperMode.Inner_2
        @Override // me.mioclient.api.JsonConfigurable
        public JsonElement toJson() {
            return ClientServices.moduleRegistry.toJson().getAsJsonObject();
        }
    };

    public final String name;
    public final Path path;

    PresetHelperMode(String str) {
        this.name = str;
        this.path = ConfigManager.path2.resolve(str.toLowerCase());
    }

    public Path getPresetPath() {
        return this.path;
    }

    public static PresetHelperMode getByName(String str) {
        for (PresetHelperMode presetHelperMode : values()) {
            if (presetHelperMode.getName().equalsIgnoreCase(str)) {
                return presetHelperMode;
            }
        }
        return null;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        ClientServices.moduleRegistry.fromJson(jsonElement);
    }

    public void writePresetMetadata(JsonObject jsonObject) {
        jsonObject.addProperty("client", "mio-fabric");
        jsonObject.addProperty("category", getName().toLowerCase(Locale.ROOT));
    }
}
