package me.mioclient.setting;

import me.mioclient.api.ClientServices;
import me.mioclient.gui.widget.PanelComponent;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.screen.ClickGuiScreen;
import me.mioclient.gui.widget.ModuleButton;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.anim.ValueAnimator;

import me.mioclient.gui.ClosableElement;

import me.mioclient.api.Setting;
import com.mojang.blaze3d.vertex.PoseStack;
import me.mioclient.manager.ScissorStackManager;

public abstract class SettingPresetSettingButton<T> extends PanelComponent {
    public final ClosableElement presetHelper_2;
    public Setting<T> setting;
    public final ValueAnimator zoomHelper;
    public boolean flag;
    public long scrollTime;
    public boolean flag2;

    public SettingPresetSettingButton(ClickGuiPanel presetEnumSettingHelper, Setting<T> setting) {
        this(presetEnumSettingHelper, () -> {
            return false;
        }, setting);
    }

    public SettingPresetSettingButton(ClickGuiPanel presetEnumSettingHelper, ClosableElement presetHelper_2, Setting<T> setting) {
        super(presetEnumSettingHelper, 0);
        this.zoomHelper = new ValueAnimator();
        this.presetHelper_2 = presetHelper_2;
        this.setting = setting;
    }

    public int getFontHeight() {
        if (this.setting.isVisible()) {
            return getRowHeight();
        }
        return 0;
    }

    public ClosableElement getClosable() {
        return this.presetHelper_2;
    }

    public Setting<T> getSetting() {
        return this.setting;
    }

    public boolean isHidden() {
        boolean z = !this.setting.isVisible() || this.presetHelper_2.isClosed();
        if (z) {
            this.setting.flag = false;
        }
        return z;
    }

    public void renderScrolling(PoseStack matrixStack, String str, java.lang.Runnable runnable) {
        matrixStack.pushPose();
        updateScroll(ClientTextRenderer.INSTANCE.getStringWidth(str));
        float f = -this.zoomHelper.getValue();
        boolean z = (f == 0.0f || (this instanceof PresetSettingPresetSettingButton)) ? false : true;
        if (z) {
            ModuleButton arrayListPresetHelper2 = (ModuleButton) this.presetHelper_2;
            Shape2DRenderer.resetShadowOffset();
            ScissorStackManager.pushClipRegion(this.presetEnumSettingHelper.getX() + getPadding(), this.presetEnumSettingHelper.getY() + arrayListPresetHelper2.getOffsetY(), this.presetEnumSettingHelper.getTotalWidth() - getPadding(), arrayListPresetHelper2.getFontHeight());
        }
        matrixStack.translate(f, 0.0f, 0.0f);
        runnable.run();
        if (z) {
            Shape2DRenderer.resetShadowOffset();
            ScissorStackManager.popClipRegion();
        }
        matrixStack.popPose();
    }

    public void updateScroll(float f) {
        if (f <= this.presetEnumSettingHelper.getTotalWidth() - 4) {
            this.zoomHelper.setImmediate(0.0f);
            return;
        }
        if (this.flag) {
            if (!this.flag) {
                this.scrollTime = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() > this.scrollTime + 750) {
                this.zoomHelper.animateTo(f - (this.presetEnumSettingHelper.getTotalWidth() - 4), 500L);
            }
        } else {
            this.zoomHelper.animateTo(0.0f, 500L);
        }
        this.flag = this.flag;
    }

    @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
    public void mouseClicked(double d, double d2, int i) {
        if (isHidden()) {
            return;
        }
        super.mouseClicked(d, d2, i);
    }

    public ClickGuiScreen getClickGuiScreen() {
        return ClientServices.getClickGui();
    }
}
