package me.mioclient.module;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.EnumJsonConverter;

import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.render.hud.DraggableHudElement;

import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.render.hud.DraggableHudElement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Iterator;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import me.mioclient.module.client.HUD;
import net.minecraft.client.gui.DrawContext;
import me.mioclient.mode.client.ModuleListMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ModuleList.class */
public abstract class HudModule extends Module {
    public static final EnumJsonConverter enumSettingConverter = new EnumJsonConverter(ModuleListMode.class);
    public static HUD hud = (HUD) ClientServices.moduleInjector.getModule(HUD.class);
    public DraggableHudElement moduleListSearchHelper4;

    public HudModule(String str, String... strArr) {
        super(str, Category.HUD, strArr);
    }

    public void renderContent(DrawContext drawContext) {
    }

    public float[] getSize() {
        throw new java.lang.RuntimeException();
    }

    @Override // me.mioclient.module.Module, me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("enabled", Boolean.valueOf(isToggled()));
        JsonObject jsonObject2 = new JsonObject();
        for (Setting<?> setting : getRegistry()) {
            if (!setting.isDependent()) {
                jsonObject2.add(setting.getConfigName(), setting.toJson());
            }
        }
        jsonObject.add("settings", (JsonElement) jsonObject2);
        JsonObject asJsonObject = jsonObject.getAsJsonObject();
        JsonObject jsonObject3 = new JsonObject();
        jsonObject3.add("anchor", enumSettingConverter.doForward(this.moduleListSearchHelper4.getAnchor()));
        jsonObject3.addProperty("index", Integer.valueOf(get1296()));
        if (this.moduleListSearchHelper4.getAnchor() == ModuleListMode.NONE) {
            jsonObject3.addProperty("x", Float.valueOf(this.moduleListSearchHelper4.getRelativeX()));
            jsonObject3.addProperty("y", Float.valueOf(this.moduleListSearchHelper4.getRelativeY()));
        }
        asJsonObject.add("hud", (JsonElement) jsonObject3);
        return asJsonObject;
    }

    @Override // me.mioclient.module.Module, me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        super.fromJson(jsonElement);
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        if (asJsonObject.has("hud")) {
            JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("hud");
            setAnchor((ModuleListMode) enumSettingConverter.doBackward(asJsonObject2.get("anchor")));
            if (this.moduleListSearchHelper4.getAnchor() == ModuleListMode.NONE) {
                this.moduleListSearchHelper4.setRelativeX(asJsonObject2.get("x").getAsFloat(), false);
                this.moduleListSearchHelper4.setRelativeY(asJsonObject2.get("y").getAsFloat(), false);
            }
        }
    }

    @Override // me.mioclient.module.Module
    public boolean isDrawn() {
        return false;
    }

    public boolean isInHudEditor() {
        return minecraftClient.currentScreen instanceof HudEditorScreen;
    }

    public ModuleListMode getAnchor() {
        return this.moduleListSearchHelper4.getAnchor();
    }

    public Color getHudColor(float f) {
        return hud.setting8.getValue().computeColor(hud, f);
    }

    public void setAnchor(ModuleListMode moduleListMode) {
        this.moduleListSearchHelper4.setAnchor(moduleListMode);
        ClientServices.getHud().removeModuleFromAll(this.moduleListSearchHelper4);
        ClientServices.getHud().addModuleToList(moduleListMode, this.moduleListSearchHelper4);
    }

    public void setElement(DraggableHudElement moduleListSearchHelper4) {
        this.moduleListSearchHelper4 = moduleListSearchHelper4;
    }

    public int get1296() {
        int i = 0;
        Iterator<DraggableHudElement> it = ClientServices.getHud().getRenderer(this.moduleListSearchHelper4.getAnchor()).getElements().iterator();
        while (it.hasNext()) {
            if (it.next().getHudModule().equals(this)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public DraggableHudElement getElement() {
        return this.moduleListSearchHelper4;
    }
}
