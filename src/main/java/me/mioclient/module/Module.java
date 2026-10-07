package me.mioclient.module;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.CollectionRegistry;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.misc.KeybindMode;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.Toggleable;

import me.mioclient.event.EventBus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import me.mioclient.api.Category;
import me.mioclient.api.Keybind;
import me.mioclient.api.Setting;
import me.mioclient.event.events.client.EnableEvent;
import net.minecraft.ChatFormatting;

public class Module extends Feature implements ClientAccessor, Toggleable, CollectionRegistry<Setting<?>, List<Setting<?>>>, JsonConfigurable {
    public final Category category;
    public final List<Setting<?>> registry;
    public final String[] aliases;
    public Keybind keybind;
    public boolean toggled;
    public boolean drawn;
    public boolean wip;

    public Module(String str, String str2, Category category, String... strArr) {
        super(str);
        this.registry = new ArrayList();
        this.keybind = Keybind.keybind;
        this.drawn = true;
        this.category = category;
        setDescription(str2);
        String[] strArr2 = (String[]) Objects.requireNonNullElseGet(strArr, () -> {
            return new String[0];
        });
        this.aliases = (String[]) Arrays.copyOf(strArr2, strArr2.length + 1);
        this.aliases[strArr2.length] = str;
    }

    public Module(String str, Category category, String... strArr) {
        this(str, "", category, strArr);
    }

    public Category getCategory() {
        return this.category;
    }

    public Keybind getKeybind() {
        return this.keybind;
    }

    public String getInfoString() {
        String str;
        String string397 = ClientServices.notificationsHelper.getModuleDisplayName(this);
        if (getInfo() != null) {
            str = new StringTemplate().arg(String.valueOf(ChatFormatting.GRAY)).arg(getInfo()).arg(String.valueOf(ChatFormatting.WHITE)).arg(String.valueOf(ChatFormatting.GRAY)).format("\u0001 [\u0001\u0001\u0001]");
        } else {
            str = "";
        }
        return new StringTemplate().arg(str).arg(string397).format("\u0001\u0001");
    }

    public void setKeybind(Keybind keybind) {
        this.keybind = keybind;
    }

    public void modifyKeybind(Function<Keybind, Keybind> function) {
        this.keybind = function.apply(this.keybind);
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    public List<Setting<?>> getRegistry() {
        return this.registry;
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    public boolean register(Setting<?> setting) {
        return this.registry.add(setting);
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    public boolean unregister(Setting<?> setting) {
        return this.registry.remove(setting);
    }

    public <T> Setting<T> add(Setting<T> setting) {
        register((Setting<?>) setting);
        return setting;
    }

    public <T> Setting<T> add(Setting<T> setting, Setting<?> setting2) {
        this.registry.add(this.registry.indexOf(setting2) + 1, setting);
        return setting;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    @Override // me.mioclient.api.Toggleable
    public void enable() {
        if (isToggled()) {
            return;
        }
        this.toggled = true;
        eventBus.register(this);
        eventBus.fireEvent(new EnableEvent(this));
        onToggle();
        onEnable();
    }

    public void disable() {
        if (isToggled()) {
            eventBus.unregister(this);
            this.toggled = false;
            eventBus.fireEvent(new EnableEvent(this));
            onToggle();
            onDisable();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public String getInfo() {
        return null;
    }

    public boolean isDrawn() {
        return this.drawn;
    }

    public void setDrawn(boolean z) {
        this.drawn = z;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("enabled", Boolean.valueOf(this.toggled));
        jsonObject.addProperty("key", Integer.valueOf(this.keybind.getKey()));
        jsonObject.addProperty("state", this.keybind.getMode().getSerialized());
        jsonObject.addProperty("mouse", Boolean.valueOf(this.keybind.isMouseKeybind()));
        jsonObject.addProperty("drawn", Boolean.valueOf(this.drawn));
        JsonObject jsonObject2 = new JsonObject();
        for (Setting<?> setting : getRegistry()) {
            if (!setting.isDependent() && !setting.isHovered()) {
                jsonObject2.add(setting.getConfigName(), setting.toJson());
            }
        }
        jsonObject.add("settings", (JsonElement) jsonObject2);
        return jsonObject;
    }

    public void fromJson(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            if (jsonObject.has("settings")) {
                JsonObject jsonObject2 = (jsonObject.get("settings")) instanceof JsonObject ? (JsonObject) (jsonObject.get("settings")) : null;
                if (jsonObject2 instanceof JsonObject) {
                    JsonObject jsonObject3 = jsonObject2;
                    for (Setting<?> setting : getRegistry()) {
                        if (!setting.isHovered()) {
                            if (jsonObject3.has(setting.getConfigName())) {
                                try {
                                    setting.fromJson(jsonObject3.get(setting.getConfigName()));
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }
            if (jsonObject.has("drawn")) {
                setDrawn(jsonObject.get("drawn").getAsBoolean());
            }
            if (jsonObject.has("key")) {
                modifyKeybind(keybind -> {
                    return keybind.withKey(jsonObject.get("key").getAsInt()).withMode(jsonObject.has("state") ? Keybind.KeybindMode.fromStringMode(jsonObject.get("state").getAsString()) : Keybind.KeybindMode.TOGGLE);
                });
            }
            if (jsonObject.has("mouse")) {
                modifyKeybind(keybind2 -> {
                    return keybind2.withMouse(jsonObject.get("mouse").getAsBoolean());
                });
            }
            if (jsonObject.has("enabled")) {
                setToggled(jsonObject.get("enabled").getAsBoolean());
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return getName();
    }

    public boolean isWip() {
        return this.wip;
    }

    public void setWip(boolean z) {
        this.wip = z;
    }
}