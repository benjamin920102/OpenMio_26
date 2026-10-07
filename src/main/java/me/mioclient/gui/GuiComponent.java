package me.mioclient.gui;

import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.module.client.UI;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.vertex.PoseStack;

public interface GuiComponent {
    default void render(GuiGraphicsExtractor drawContext, PoseStack matrixStack, double d, double d2) {
    }

    default void update(double d, double d2) {
    }

    default void mouseClicked(double d, double d2, int i) {
    }

    default void mouseReleased(double d, double d2, int i) {
    }

    default void mouseScrolled(double d, double d2, double d3) {
    }

    default void keyPressed(int i) {
    }

    default void keyTyped(char c) {
    }

    default void setOffsetY(int i) {
    }

    default int getOffsetY() {
        return 0;
    }

    default void init() {
    }

    default int getFontHeight() {
        return getRowHeight();
    }

    default boolean containsPoint(double d, double d2) {
        return false;
    }

    default float getTextOffsetY() {
        return ((getRowHeight() - ClientTextRenderer.INSTANCE.getFontHeight()) + 1) / 2.0f;
    }

    default int getRowHeight() {
        return 2 + ClientTextRenderer.INSTANCE.getFontHeight() + UI.uI.buttonHeight.getValue().intValue();
    }

    default UI getUI() {
        return UI.uI;
    }
}
