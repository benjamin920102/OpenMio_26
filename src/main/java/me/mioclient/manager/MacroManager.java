package me.mioclient.manager;

import me.mioclient.event.events.client.MacroTriggerEvent;

import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.NamedRegistryList;

import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.util.client.InputState;
import me.mioclient.util.io.JsonValidator;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Keybind;
import me.mioclient.event.events.input.KeyEvent;
import me.mioclient.event.Listen;
import me.mioclient.command.KeybindFeature;
import me.mioclient.command.KeybindFeature_3;

public final class MacroManager extends NamedRegistryList<KeybindFeature> implements ClientAccessor, JsonConfigurable {
    public MacroManager() {
        eventBus.register(this);
    }

    @Listen
    public void onKey(KeyEvent keyEvent) {
        if (minecraftClient.gui.screen() != null || isNotInGame()) {
            return;
        }
        for (KeybindFeature keybindFeature : (List<KeybindFeature>) this.registry) {
            if (keybindFeature.getKeybind().isMouseKeybind() == keyEvent.isPressed() && keybindFeature.getKeybind().getKey() == keyEvent.getKey()) {
                keybindFeature.run();
                eventBus.fireEvent(new MacroTriggerEvent(keybindFeature));
            }
        }
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (minecraftClient.gui.screen() != null || isNotInGame()) {
            return;
        }
        for (KeybindFeature keybindFeature : (List<KeybindFeature>) this.registry) {
            if (keybindFeature instanceof KeybindFeature_3) {
                KeybindFeature_3 keybindFeature_3 = (KeybindFeature_3) keybindFeature;
                keybindFeature_3.do1072(InputState.isKeybindPressed(keybindFeature_3.getKeybind()));
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        for (KeybindFeature keybindFeature : (List<KeybindFeature>) this.registry) {
            JsonElement jsonObject2 = new JsonObject();
            ((JsonObject) jsonObject2).addProperty("name", keybindFeature.getName());
            ((JsonObject) jsonObject2).addProperty("type", keybindFeature.getKeybindMode().getName());
            ((JsonObject) jsonObject2).addProperty("key", Integer.valueOf(keybindFeature.getKeybind().getKey()));
            ((JsonObject) jsonObject2).addProperty("mouse", Boolean.valueOf(keybindFeature.getKeybind().isMouseKeybind()));
            JsonArray jsonArray2 = new JsonArray();
            Iterator<String> it = keybindFeature.getCommands().iterator();
            while (it.hasNext()) {
                jsonArray2.add(it.next());
            }
            ((JsonObject) jsonObject2).add("commands", (JsonElement) jsonArray2);
            jsonArray.add(jsonObject2);
        }
        jsonObject.add("macros", (JsonElement) jsonArray);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("macros")) {
            Iterator it = jsonElement.getAsJsonObject().getAsJsonArray("macros").iterator();
            while (it.hasNext()) {
                JsonElement jsonElement2 = (JsonElement) it.next();
                if (jsonElement2.isJsonObject()) {
                    JsonObject asJsonObject = jsonElement2.getAsJsonObject();
                    if (JsonValidator.hasAllFields(asJsonObject, "name", "type", "key", "mouse", "commands")) {
                        try {
                            KeybindMode mode_4831 = KeybindMode.getByName(asJsonObject.get("type").getAsString());
                            KeybindFeature keybindFeature832 = mode_4831.createKeybindFeature(asJsonObject.get("name").getAsString(), new Keybind(asJsonObject.get("key").getAsInt(), Keybind.KeybindMode.TOGGLE, asJsonObject.get("mouse").getAsBoolean()));
                            Iterator it2 = asJsonObject.get("commands").getAsJsonArray().iterator();
                            while (it2.hasNext()) {
                                keybindFeature832.getCommands().add(((JsonElement) it2.next()).getAsString());
                            }
                            if (mode_4831 == KeybindMode.HOLD) {
                                while (keybindFeature832.getCommands().size() > 2) {
                                    keybindFeature832.getCommands().removeLast();
                                }
                            }
                            register(keybindFeature832);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "macros.json";
    }
}
