package me.mioclient.render.font;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import java.awt.Color;
import java.util.Objects;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.player.NameProtect;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class ClientTextRenderer implements ClientAccessor {
    /* Resolved lazily — eager static init ran before the module registry was ready. */
    private static NameProtect nameprotect;

    public static NameProtect getNameProtect() {
        if (nameprotect == null && ClientServices.moduleInjector != null) {
            try {
                nameprotect = (NameProtect) ClientServices.moduleInjector.getModule(NameProtect.class);
            } catch (Exception e) {
            }
        }
        return nameprotect;
    }
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

    public void drawText(GuiGraphicsExtractor drawContext, String str, float f, float f2, Color color) {
        drawTextObj(drawContext, Component.literal(str), f, f2, color);
    }

    public void drawTextObj(GuiGraphicsExtractor drawContext, Component text, float f, float f2, Color color) {
        if (Fonts.fonts.isToggled()) {
            getAdvanceGlyph().renderOrderedText(drawContext, text.getVisualOrderText(), f, f2, color.hashCode(), false);
        } else {
            drawContext.text(minecraftClient.font, text, (int) f, (int) f2, color.hashCode(), false);
        }
    }

    public void drawTextShadow(GuiGraphicsExtractor drawContext, String str, float f, float f2, Color color) {
        drawTextShadowObj(drawContext, Component.literal(str), f, f2, color);
    }

    public void drawTextShadowObj(GuiGraphicsExtractor drawContext, Component text, float f, float f2, Color color) {
        if (Fonts.fonts.isToggled()) {
            getAdvanceGlyph().renderOrderedText(drawContext, text.getVisualOrderText(), f, f2, color.hashCode(), true);
        } else {
            drawContext.text(minecraftClient.font, text, (int) f, (int) f2, color.hashCode(), true);
        }
    }

    public void drawScaledText(GuiGraphicsExtractor drawContext, String str, float f, float f2, float f3, Color color) {
        drawScaledTextObj(drawContext, Component.literal(str), f, f2, f3, color);
    }

    public void drawScaledTextObj(GuiGraphicsExtractor drawContext, Component text, float f, float f2, float f3, Color color) {
        drawContext.pose().pushMatrix();
        drawContext.pose().scale(f3, f3);
        drawTextObj(drawContext, text, f / f3, f2 / f3, color);
        drawContext.pose().popMatrix();
    }

    public void drawScaledTextShadow(GuiGraphicsExtractor drawContext, String str, float f, float f2, float f3, Color color) {
        drawScaledTextShadowObj(drawContext, Component.literal(str), f, f2, f3, color);
    }

    public void drawScaledTextShadowObj(GuiGraphicsExtractor drawContext, Component text, float f, float f2, float f3, Color color) {
        drawContext.pose().pushMatrix();
        drawContext.pose().scale(f3, f3);
        drawTextShadowObj(drawContext, text, f / f3, f2 / f3, color);
        drawContext.pose().popMatrix();
    }

    public void resetShadowOffset() {
        getAdvanceGlyph().resetShadowOffset();
    }

    public float getStringWidth(String str) {
        NameProtect nameprotect_lcl = getNameProtect();
        if (nameprotect_lcl != null && nameprotect_lcl.isToggled() && !isNotInGame()) {
            str = str.replace(minecraftClient.player.getName().getString(), nameprotect_lcl.name.getValue());
        }
        return (Fonts.fonts != null && Fonts.fonts.isToggled()) ? getAdvanceGlyph().measureStringWidth(str) : minecraftClient.font.width(str);
    }

    public int getFontHeight() {
        if (Fonts.fonts != null && Fonts.fonts.isToggled()) {
            return getAdvanceGlyph().getHalfLineHeight() - 1;
        }
        if (minecraftClient.font == null) {
            return 9;
        }
        Objects.requireNonNull(minecraftClient.font);
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
