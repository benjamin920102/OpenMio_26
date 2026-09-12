package me.mioclient.render.font;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import java.awt.Color;
import java.util.Objects;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.player.NameProtect;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ClientTextRenderer.class */
public class ClientTextRenderer implements ClientAccessor {
    public static final NameProtect nameprotect = (NameProtect) ClientServices.moduleInjector.getModule(NameProtect.class);
    public static final ClientTextRenderer INSTANCE = new ClientTextRenderer();
    public static int currentShadowOffset = 0;
    public AdvanceGlyph advanceGlyph;

    public static String capitalize(Object obj) {
        return Character.toUpperCase(obj.toString().charAt(0)) + obj.toString().toLowerCase().substring(1);
    }

    public static boolean containsChar(String str, char c) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }

    public AdvanceGlyph getAdvanceGlyph() {
        if (this.advanceGlyph == null) {
            this.advanceGlyph = createAdvanceGlyph();
        }
        return this.advanceGlyph;
    }

    public void setAdvanceGlyph(AdvanceGlyph advanceGlyph) {
        if (advanceGlyph == null) {
            return;
        }
        this.advanceGlyph = advanceGlyph;
        System.gc();
    }

    public AdvanceGlyph createAdvanceGlyph() {
        Fonts fonts = Fonts.fonts;
        return AdvanceGlyph.createForFontStyle(fonts.font.getValue(), ((Number) fonts.size.getValue()).intValue(), fonts.style.getValue());
    }

    public void drawText(DrawContext drawContext, String str, float f, float f2, Color color) {
        drawTextObj(drawContext, Text.of(str), f, f2, color);
    }

    public void drawTextObj(DrawContext drawContext, Text text, float f, float f2, Color color) {
        if (Fonts.fonts.isToggled()) {
            getAdvanceGlyph().renderOrderedText(drawContext.getMatrices(), text.asOrderedText(), f, f2, color.hashCode(), false);
        } else {
            minecraftClient.textRenderer.draw(text, (int) f, (int) f2, color.hashCode(), false, drawContext.getMatrices().peek().getPositionMatrix(), getAdvanceGlyph().getVertexConsumerProvider(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        }
    }

    public void drawTextShadow(DrawContext drawContext, String str, float f, float f2, Color color) {
        drawTextShadowObj(drawContext, Text.of(str), f, f2, color);
    }

    public void drawTextShadowObj(DrawContext drawContext, Text text, float f, float f2, Color color) {
        if (Fonts.fonts.isToggled()) {
            getAdvanceGlyph().renderOrderedText(drawContext.getMatrices(), text.asOrderedText(), f, f2, color.hashCode(), true);
        } else {
            minecraftClient.textRenderer.draw(text, (int) f, (int) f2, color.hashCode(), true, drawContext.getMatrices().peek().getPositionMatrix(), getAdvanceGlyph().getVertexConsumerProvider(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
        }
    }

    public void drawScaledText(DrawContext drawContext, String str, float f, float f2, float f3, Color color) {
        drawScaledTextObj(drawContext, Text.of(str), f, f2, f3, color);
    }

    public void drawScaledTextObj(DrawContext drawContext, Text text, float f, float f2, float f3, Color color) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(f3, f3, f3);
        drawTextObj(drawContext, text, f / f3, f2 / f3, color);
        drawContext.getMatrices().scale(1.0f / f3, 1.0f / f3, 1.0f / f3);
        drawContext.getMatrices().pop();
    }

    public void drawScaledTextShadow(DrawContext drawContext, String str, float f, float f2, float f3, Color color) {
        drawScaledTextShadowObj(drawContext, Text.of(str), f, f2, f3, color);
    }

    public void drawScaledTextShadowObj(DrawContext drawContext, Text text, float f, float f2, float f3, Color color) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().scale(f3, f3, f3);
        drawTextShadowObj(drawContext, text, f / f3, f2 / f3, color);
        drawContext.getMatrices().scale(1.0f / f3, 1.0f / f3, 1.0f / f3);
        drawContext.getMatrices().pop();
    }

    public void resetShadowOffset() {
        getAdvanceGlyph().resetShadowOffset();
    }

    public float getStringWidth(String str) {
        if (nameprotect != null && nameprotect.isToggled() && !isNotInGame()) {
            str = str.replace(minecraftClient.player.getName().getString(), nameprotect.name.getValue());
        }
        return (Fonts.fonts != null && Fonts.fonts.isToggled()) ? getAdvanceGlyph().measureStringWidth(str) : minecraftClient.textRenderer.getWidth(str);
    }

    public int getFontHeight() {
        if (Fonts.fonts != null && Fonts.fonts.isToggled()) {
            return getAdvanceGlyph().getHalfLineHeight() - 1;
        }
        if (minecraftClient.textRenderer == null) {
            return 9;
        }
        Objects.requireNonNull(minecraftClient.textRenderer);
        return 9;
    }

    public static int getShiftOffset() {
        return Fonts.fonts.shift.getValue().intValue();
    }

    public static float getShadowOffset() {
        return Fonts.fonts.shadow.getValue().floatValue();
    }

    public static int getTranslateOffset() {
        return Fonts.fonts.translate.getValue().intValue();
    }

    public static void setCurrentShadowOffset(int i) {
        currentShadowOffset = i;
    }

    public static int getCurrentShadowOffset() {
        return currentShadowOffset;
    }

    public static String sanitizeString(String str) {
        return str.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }
}
