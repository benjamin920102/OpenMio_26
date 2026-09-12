package me.mioclient.manager;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.module.HudModule;
import me.mioclient.util.registry.CollectionRegistry;
import me.mioclient.util.registry.NamedRegistryList;

import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.util.client.InputState;
import me.mioclient.util.registry.EnumSettingRegistry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.NamedRegistryList;
import me.mioclient.util.registry.EnumSettingRegistry;
import me.mioclient.loader.ModInitializerBridge;
import me.mioclient.util.client.InputState;

import me.mioclient.event.EventBus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Keybind;
import me.mioclient.event.events.input.KeyEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Delay;
import me.mioclient.module.Module;
import me.mioclient.module.client.Baritone;
import me.mioclient.module.movement.ObstaclePasser;
import me.mioclient.util.registry.CollectionRegistry;
import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.module.HudModule;
import me.mioclient.render.RenderConstants;
import me.mioclient.render.RenderConstants;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ModuleManager.class */
public final class ModuleManager extends NamedRegistryList<Module> implements ClientAccessor, JsonConfigurable {
    public final Object2ObjectOpenHashMap<Class<? extends Module>, Module> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap<>();

    public ModuleManager() {
        eventBus.register(this);
        ModInitializerBridge.do1352(this);
        registerConditionalModules();
        initModules();
        ((List<Module>) this.registry).sort(Comparator.comparing((v0) -> {
            return v0.getName();
        }));
    }

    public void initModules() {
    }

    public void registerConditionalModules() {
        if (ClientServices.obstaclePasserHelper.isBaritoneLoaded()) {
            register(new Baritone());
            register(new ObstaclePasser());
        }
    }

    @Listen
    public void onKey(KeyEvent keyEvent) {
        for (Module module : (List<Module>) this.registry) {
            if (module.getKeybind().isMouseKeybind() == keyEvent.isPressed() && module.getKeybind().getKey() == keyEvent.getKey() && module.getKeybind().getMode() == Keybind.KeybindMode.TOGGLE) {
                module.toggle();
            }
        }
    }

    @Listen(get219= RenderConstants.num5)
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        for (Module module : (List<Module>) this.registry) {
            Keybind keybind = module.getKeybind();
            boolean z = keybind.getMode() == Keybind.KeybindMode.HOLD;
            if (!keybind.isUnbound() && keybind.getMode() != Keybind.KeybindMode.TOGGLE && (minecraftClient.currentScreen == null || module.isToggled() == z)) {
                module.setToggled(z == InputState.isKeybindPressed(keybind));
            }
        }
    }

    public List<Module> getModulesByCategory(Category category) {
        return (List) ((List<Module>) this.registry).stream().filter(module -> {
            return module.getCategory() == category;
        }).collect(Collectors.toList());
    }

    public List<Delay> getDelayModules() {
        ArrayList arrayList = new ArrayList();
        ((List) this.registry).forEach(module -> {
            if (module instanceof Delay) {
                arrayList.add((Delay) module);
            }
        });
        return arrayList;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("client", "mio-fabric");
        jsonObject.addProperty("category", "all");
        for (Module module : getRegistry()) {
            if (!(module instanceof HudModule)) {
                try {
                    jsonObject.add(module.getConfigName(), module.toJson());
                } catch (Exception e) {
                }
            }
        }
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        for (Module module : getRegistry()) {
            if (!(module instanceof HudModule)) {
                try {
                    module.fromJson(jsonElement.getAsJsonObject().get(module.getConfigName()));
                } catch (Exception e) {
                }
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "modules.json";
    }

    @Override // me.mioclient.util.registry.NamedRegistryList, me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is115, reason: merged with bridge method [inline-methods] */
    public boolean register(Module module) {
        if (EnumSettingRegistry.isExperimentalClass(module.getClass())) {
            return false;
        }
        EnumSettingRegistry.unregisterExperimentalSettings(module);
        this.object2ObjectOpenHashMap.put(module.getClass(), module);
        return super.register(module);
    }

    @Override // me.mioclient.util.registry.NamedRegistryList, me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is116, reason: merged with bridge method [inline-methods] */
    public boolean unregister(Module module) {
        this.object2ObjectOpenHashMap.remove(module.getClass(), module);
        return super.unregister(module);
    }

    @Deprecated
    public <T extends Module> T getModule(Class<T> cls) {
        return (T) this.object2ObjectOpenHashMap.get(cls);
    }
}
