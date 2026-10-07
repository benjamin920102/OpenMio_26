package me.mioclient.setting;

import me.mioclient.api.ClientServices;
import me.mioclient.gui.widget.PanelComponent;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.widget.ModuleButton;
import me.mioclient.mode.client.CursorType;
import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.render.font.ClientTextRenderer;

import java.util.function.Function;
import me.mioclient.api.Keybind;
import me.mioclient.api.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.vertex.PoseStack;

public class KeybindPresetSettingButton extends PanelComponent {
    public final ModuleButton arrayListPresetHelper2;
    public Setting<Keybind> setting;
    public boolean flag;

    public KeybindPresetSettingButton(int i, ModuleButton arrayListPresetHelper2) {
        super(arrayListPresetHelper2.getPanel(), i);
        this.arrayListPresetHelper2 = arrayListPresetHelper2;
        this.setting = null;
    }

    public KeybindPresetSettingButton(int i, ModuleButton arrayListPresetHelper2, Setting<Keybind> setting) {
        super(arrayListPresetHelper2.getPanel(), i);
        this.arrayListPresetHelper2 = arrayListPresetHelper2;
        this.setting = setting;
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void mouseClicked(double d, double d2, int i) {
        if (this.arrayListPresetHelper2.flag) {
            super.mouseClicked(d, d2, i);
            if (containsPoint(d, d2) && this.arrayListPresetHelper2.flag) {
                if (i == 0) {
                    this.flag = !this.flag;
                    return;
                }
                if (this.flag) {
                    modifyKeybind(keybind -> {
                        return keybind.withKey(i).withMouse(true);
                    });
                    this.flag = !this.flag;
                } else if (i == 1) {
                    modifyKeybind(keybind2 -> {
                        return keybind2.withMode(Keybind.KeybindMode.values()[(keybind2.getMode().ordinal() + 1) % Keybind.KeybindMode.values().length]);
                    });
                }
            }
        }
    }

    @Override // me.mioclient.gui.GuiComponent
    public void keyPressed(int i) {
        if (this.flag) {
            boolean z = i == 261 || i == 256;
            modifyKeybind(keybind -> {
                return keybind.withKey(z ? -1 : i).withMouse(false);
            });
            this.flag = false;
            ClientServices.getClickGui().suppressEscapeClose();
        }
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void render(GuiGraphicsExtractor drawContext, PoseStack matrixStack, double d, double d2) {
        super.render(drawContext, matrixStack, d, d2);
        if (this.flag) {
            AbstractClickGuiScreen.cursorType = CursorType.INPUT;
        }
        String name = this.flag ? getKeybind().getMode().getName() : "Key";
        String string773 = getKeybind().getBindString();
        if (this.flag) {
            string773 = "...";
        }
        ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, new StringTemplate().arg(string773).arg(name).format("\u0001 \u0001"), this.presetEnumSettingHelper.getX() + 4, ((this.presetEnumSettingHelper.getY() + getTextOffsetY()) - getAnimationOffset()) + this.num, getUI().textColor.getValue());
    }

    public void modifyKeybind(Function<Keybind, Keybind> function) {
        if (this.setting == null) {
            this.arrayListPresetHelper2.module.modifyKeybind(function);
        } else {
            this.setting.setValue(function.apply(this.setting.getValue()));
        }
    }

    public Keybind getKeybind() {
        return this.setting == null ? this.arrayListPresetHelper2.module.getKeybind() : this.setting.getValue();
    }
}
