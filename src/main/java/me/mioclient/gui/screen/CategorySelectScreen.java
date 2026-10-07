package me.mioclient.gui.screen;

import me.mioclient.api.ClientServices;
import me.mioclient.gui.widget.ActionButton;
import me.mioclient.setting.EnumJsonConverter;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.panel.PresetPanel;
import me.mioclient.mode.misc.PresetHelperMode;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;
import me.mioclient.setting.PresetPresetSettingButton;
import net.minecraft.client.gui.screens.Screen;

public class CategorySelectScreen extends AbstractClickGuiScreen {
    public final ClickGuiPanel presetEnumSettingHelper = new ClickGuiPanel("Category");
    public final Screen screen;

    public CategorySelectScreen(Screen screen, Path path) {
        this.screen = screen;
        this.presetEnumSettingHelper.getChildren().add(new PresetPresetSettingButton(this.presetEnumSettingHelper, new StringTemplate().arg(getPresetFileName(path)).format("Preset: \u0001")));
        PresetHelperMode presetHelperMode2715 = detectPresetCategory(path);
        if (presetHelperMode2715 != null) {
            this.presetEnumSettingHelper.register(new ActionButton(this.presetEnumSettingHelper, "Import", () -> {
                importPresetToCategory(path, presetHelperMode2715);
            }));
        } else {
            for (PresetHelperMode presetHelperMode : PresetHelperMode.values()) {
                this.presetEnumSettingHelper.register(new ActionButton(this.presetEnumSettingHelper, EnumJsonConverter.getEnumDisplayName(presetHelperMode), () -> {
                    importPresetToCategory(path, presetHelperMode);
                }));
            }
        }
        this.presetEnumSettingHelper.register(new ActionButton(this.presetEnumSettingHelper, "Cancel", this::requestClose));
        getGuiPanels().add(this.presetEnumSettingHelper);
    }

    @Override // me.mioclient.gui.screen.AbstractClickGuiScreen
    public void requestClose() {
        minecraftClient.gui.setScreen(this.screen);
    }

    public String getPresetFileName(Path path) {
        return path.toFile().getName().replace(".json", "");
    }

    public void importPresetToCategory(Path path, PresetHelperMode presetHelperMode) {
        try {
            Files.copy(path, presetHelperMode.getPresetPath().resolve(path.toFile().getName()), new CopyOption[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ClientServices.presetHelper.getPresetRegistry(presetHelperMode).do34();
        minecraftClient.gui.setScreen(this.screen);
    }

    public PresetHelperMode detectPresetCategory(Path path) {
        try {
            JsonElement parseString = JsonParser.parseString(Files.readString(path));
            if (!parseString.isJsonObject()) {
                return null;
            }
            JsonObject asJsonObject = parseString.getAsJsonObject();
            if (asJsonObject.has("category")) {
                return PresetHelperMode.getByName(asJsonObject.get("category").getAsString());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean tryOpenForDroppedFiles(Screen screen, java.util.List<Path> list) {
        ArrayList arrayList = new ArrayList();
        Stream<Path> filter = list.stream().filter(path -> {
            return path.toString().endsWith(".json");
        });
        Objects.requireNonNull(arrayList);
        filter.forEach(arrayList::add);
        if (arrayList.size() != 1) {
            return false;
        }
        minecraftClient.gui.setScreen(new CategorySelectScreen(screen, (Path) arrayList.getFirst()));
        return true;
    }
}
