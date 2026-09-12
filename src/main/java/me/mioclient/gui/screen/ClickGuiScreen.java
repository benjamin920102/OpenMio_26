package me.mioclient.gui.screen;

import me.mioclient.api.ClientServices;
import me.mioclient.module.HudModule;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.GuiComponent;

import me.mioclient.gui.minigame.MinigamePanel;
import me.mioclient.gui.panel.CategoryPanel;
import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.widget.ModuleButton;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.manager.ConfigManager;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import me.mioclient.api.Category;
import me.mioclient.gui.minigame.MinigamePanel;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.HUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import me.mioclient.module.HudModule;
import me.mioclient.module.render.Search;
import me.mioclient.setting.SettingPresetSettingButton;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ClickGuiScreen.class */
public class ClickGuiScreen extends AbstractClickGuiScreen implements ClientAccessor {
    public static final int[] intArr = {265, 265, 264, 264, 263, 262, 263, 262, 66, 65};
    public boolean flag2;
    public TextFieldWidget textFieldWidget;
    public String string;
    public SettingPresetSettingButton<?> settingPresetSettingButton = null;
    public int current = 0;
    public final Stopwatch stopwatch = new Stopwatch();
    public List<ModuleButton> list = new ArrayList();
    public boolean flag = ConfigManager.path9.toFile().exists();

    public ClickGuiScreen() {
        int i = 10;
        for (Category category : Category.values()) {
            if (category != Category.HUD) {
                CategoryPanel categorySearchHelper4 = new CategoryPanel(category);
                categorySearchHelper4.setX(i);
                this.arrayList.add(categorySearchHelper4);
                i += categorySearchHelper4.getTotalWidth() + 3;
            }
        }
        if (this.flag) {
            MinigamePanel game = new MinigamePanel();
            game.setX(i);
            game.setExpanded(false);
            this.arrayList.add(game);
        }
    }

    @Override // me.mioclient.gui.screen.AbstractClickGuiScreen
    public void init() {
        super.init();
        this.string = "";
        this.textFieldWidget = new TextFieldWidget(minecraftClient.textRenderer, 7, -100, 100, 9, Text.literal("Search"));
        this.textFieldWidget.setVisible(true);
        this.textFieldWidget.setEditable(true);
        this.textFieldWidget.setMaxLength(64);
        this.textFieldWidget.setChangedListener(str -> {
            if (!str.isEmpty()) {
                for (Module module : ClientServices.moduleRegistry.getRegistry()) {
                    if (!(module instanceof HUD) && !(module instanceof HudModule) && module.getName().toLowerCase(Locale.ROOT).startsWith(str.toLowerCase(Locale.ROOT))) {
                        this.string = module.getName().substring(this.textFieldWidget.getCursor());
                        return;
                    }
                }
            }
            this.string = "";
        });
        addDrawableChild(this.textFieldWidget);
    }

    @Override // me.mioclient.gui.screen.AbstractClickGuiScreen
    public void renderCustomContent(DrawContext drawContext, int i, int i2, float f) {
        if (this.list.size() >= 3) {
            ((ModuleButton) this.list.getFirst()).flag = false;
        }
        this.list.removeIf((v0) -> {
            return v0.isClosed();
        });
        if (!this.textFieldWidget.isFocused()) {
            if (ClientServices.welcomerHelper.getRank() != 8) {
                ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, "Ctrl + F to activate search.", minecraftClient.getWindow().getScaledWidth() - ClientTextRenderer.INSTANCE.getStringWidth("Ctrl + F to activate search."), minecraftClient.getWindow().getScaledHeight() - ClientTextRenderer.INSTANCE.getFontHeight(), ColorUtils.getGradientColor(Color.GRAY, Color.WHITE, Double.longBitsToDouble(4656510908468559872L), Double.longBitsToDouble(4643000109586448384L)));
                return;
            }
            return;
        }
        ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, new StringTemplate().arg(getBlinkingCursor()).arg(String.valueOf(Formatting.WHITE)).arg(this.string).arg(String.valueOf(Formatting.GRAY)).arg(this.textFieldWidget.getText()).format("\u0001\u0001\u0001\u0001\u0001"), (minecraftClient.getWindow().getScaledWidth() / Float.intBitsToFloat(1073741824)) - (ClientTextRenderer.INSTANCE.getStringWidth(new StringTemplate().arg(this.string).arg(this.textFieldWidget.getText()).format("\u0001\u0001_")) / Float.intBitsToFloat(1073741824)), (minecraftClient.getWindow().getScaledHeight() / Float.intBitsToFloat(1073741824)) - (ClientTextRenderer.INSTANCE.getFontHeight() / Float.intBitsToFloat(1073741824)), Color.white);
    }

    @Override // me.mioclient.gui.screen.AbstractClickGuiScreen
    public boolean keyPressed(int i, int i2, int i3) {
        super.keyPressed(i, i2, i3);
        if (this.flag || intArr[this.current] != i) {
            this.current = 0;
        } else {
            this.current++;
            if (this.current == intArr.length) {
                try {
                    ConfigManager.path9.toFile().createNewFile();
                } catch (Exception e) {
                }
                this.flag = true;
                this.current = 0;
                MinigamePanel game = new MinigamePanel();
                game.setExpanded(true);
                game.setX((minecraftClient.getWindow().getScaledWidth() / 2) - (game.getTotalWidth() / 2));
                game.setY((minecraftClient.getWindow().getScaledHeight() / 2) - (game.getFontHeight() / 2));
                this.arrayList.add(game);
            }
        }
        switch (i) {
            case 70:
                if (GLFW.glfwGetKey(minecraftClient.getWindow().getHandle(), 341) != 1) {
                    return true;
                }
                if (this.textFieldWidget.isFocused()) {
                    reset();
                    return true;
                }
                setInitialFocus(this.textFieldWidget);
                return true;
            case 257:
                Iterator<ClickGuiPanel> it = this.arrayList.iterator();
                while (true) {
                    if (it.hasNext()) {
                        Iterator<GuiComponent> it2 = it.next().getChildren().iterator();
                        while (it2.hasNext()) {
                            GuiComponent next = it2.next();
                            if (next instanceof ModuleButton) {
                                ModuleButton arrayListPresetHelper2 = (ModuleButton) next;
                                if (arrayListPresetHelper2.getModule().getName().equalsIgnoreCase(new StringTemplate().arg(this.string).arg(this.textFieldWidget.getText()).format("\u0001\u0001"))) {
                                    arrayListPresetHelper2.flag = true;
                                    arrayListPresetHelper2.flashHighlight();
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }
                reset();
                return true;
            case 258:
                if (!this.textFieldWidget.isFocused()) {
                    return true;
                }
                this.textFieldWidget.setText(new StringTemplate().arg(this.string).arg(this.textFieldWidget.getText()).format("\u0001\u0001"));
                return true;
            case 259:
            case 261:
                if (!this.textFieldWidget.isFocused() || this.textFieldWidget.getText().isEmpty()) {
                    return true;
                }
                this.textFieldWidget.setText(this.textFieldWidget.getText().substring(0, this.textFieldWidget.getText().length() - 1));
                return true;
            default:
                return true;
        }
    }

    public void filesDragged(List<Path> list) {
        me.mioclient.gui.screen.CategorySelectScreen.tryOpenForDroppedFiles(this, list);
    }

    @Override // me.mioclient.gui.screen.AbstractClickGuiScreen
    public void reset() {
        super.reset();
        setFocused(null);
        this.textFieldWidget.setFocused(false);
        this.textFieldWidget.setText("");
        this.textFieldWidget.setSuggestion("");
    }

    @Override // me.mioclient.gui.screen.AbstractClickGuiScreen
    public void requestClose() {
        super.requestClose();
        executorService.submit(() -> {
            ClientServices.presetHelper.do41();
        });
    }

    public TextFieldWidget getSearchField() {
        return this.textFieldWidget;
    }

    public String getBlinkingCursor() {
        if (this.stopwatch.hasElapsedMs(500L)) {
            this.flag2 = !this.flag2;
            this.stopwatch.reset();
        }
        return this.flag2 ? "_" : "";
    }

    public SettingPresetSettingButton<?> getActiveSettingButton() {
        return this.settingPresetSettingButton;
    }

    public void setActiveSetting(SettingPresetSettingButton<?> settingPresetSettingButton) {
        this.settingPresetSettingButton = settingPresetSettingButton;
    }
}
