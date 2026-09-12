package me.mioclient.render.font;

import me.mioclient.mode.client.FontStyle;
import me.mioclient.render.mesh.TrajectoriesVertexConsumer;
import me.mioclient.render.shape.Shape2DRenderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Font;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import me.mioclient.mode.client.FontStyle;
import me.mioclient.mode.client.FontStyle;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AdvanceGlyph.class */
public class AdvanceGlyph implements Glyph {
    public static RenderLayer renderLayer;
    public final TextHandler textHandler = new TextHandler(this);
    public final FontGlyphAtlas fontHelper;
    public float val;
    public float val2;
    public static final TrajectoriesVertexConsumer trajectoriesVertexConsumer = TrajectoriesVertexConsumer.createDefault();
    public static final TrajectoriesVertexConsumer trajectoriesVertexConsumer2 = TrajectoriesVertexConsumer.createDefault();
    public static final VertexConsumerProvider vertexConsumerProvider = renderLayer2 -> {
        if (renderLayer != null && renderLayer != renderLayer2) {
            Shape2DRenderer.resetShadowOffset();
        }
        if (!trajectoriesVertexConsumer2.isBuilding()) {
            trajectoriesVertexConsumer2.beginBuffer(renderLayer2.getDrawMode(), renderLayer2.getVertexFormat());
        }
        renderLayer = renderLayer2;
        return trajectoriesVertexConsumer2;
    };
    public static final int[] intArr = new int[32];

    public AdvanceGlyph(FontGlyphAtlas fontHelper) {
        this.fontHelper = fontHelper;
    }

    public static AdvanceGlyph createForFontStyle(String str, int i, FontStyle fontStyle) {
        FontGlyphAtlas fontHelper;
        char[] cArr = new char[1501];
        for (int i2 = 0; i2 < cArr.length - 1; i2++) {
            cArr[i2] = (char) i2;
        }
        cArr[cArr.length - 1] = 8734;
        switch (fontStyle) {
            case PLAIN:
                fontHelper = new FontGlyphAtlas(new Font(str, 0, i), true, true);
                break;
            case BOLD:
                fontHelper = new FontGlyphAtlas(new Font(str, 1, i), true, true);
                break;
            case ITALIC:
                fontHelper = new FontGlyphAtlas(new Font(str, 2, i), true, true);
                break;
            case BOLDITALIC:
                fontHelper = new FontGlyphAtlas(new Font(str, 3, i), true, true);
                break;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
        FontGlyphAtlas fontHelper2 = fontHelper;
        fontHelper2.buildGlyphAtlas(cArr);
        fontHelper2.uploadAtlasTexture();
        return new AdvanceGlyph(fontHelper2);
    }

    public int drawStringShadowFloat(MatrixStack matrixStack, String str, float f, float f2, int i) {
        return drawStringWithShadow(matrixStack, str, f, f2, i, true);
    }

    public int drawStringShadowDouble(MatrixStack matrixStack, String str, double d, double d2, int i) {
        return drawStringWithShadow(matrixStack, str, (float) d, (float) d2, i, true);
    }

    public int drawStringPlainFloat(MatrixStack matrixStack, String str, float f, float f2, int i) {
        return drawStringWithShadow(matrixStack, str, f, f2, i, false);
    }

    public int drawStringPlainDouble(MatrixStack matrixStack, String str, double d, double d2, int i) {
        return drawStringWithShadow(matrixStack, str, (float) d, (float) d2, i, false);
    }

    public int drawCenteredString(MatrixStack matrixStack, String str, double d, double d2, int i) {
        return drawStringWithShadow(matrixStack, str, ((float) d) - (measureStringWidth(str) / Float.intBitsToFloat(1073741824)), (float) d2, i, false);
    }

    public int drawCenteredStringShadow(MatrixStack matrixStack, String str, double d, double d2, int i) {
        return drawStringWithShadow(matrixStack, str, ((float) d) - (measureStringWidth(str) / Float.intBitsToFloat(1073741824)), (float) d2, i, true);
    }

    public int renderOrderedText(MatrixStack matrixStack, OrderedText orderedText, float f, float f2, int i, boolean z) {
        return z ? Math.max(new MatrixStackCharacterVisitor(matrixStack, i, Float.intBitsToFloat(1048576000), this.fontHelper).renderText(orderedText, f + ClientTextRenderer.getShadowOffset(), f2 + ClientTextRenderer.getShadowOffset()), new MatrixStackCharacterVisitor(matrixStack, i, Float.intBitsToFloat(1065353216), this.fontHelper).renderText(orderedText, f, f2)) : new MatrixStackCharacterVisitor(matrixStack, i, Float.intBitsToFloat(1065353216), this.fontHelper).renderText(orderedText, f, f2);
    }

    public int drawStringWithShadow(MatrixStack matrixStack, String str, float f, float f2, int i, boolean z) {
        return z ? Math.max(renderStringWithColor(matrixStack, str, f + ClientTextRenderer.getShadowOffset(), f2 + ClientTextRenderer.getShadowOffset(), i, true), renderStringWithColor(matrixStack, str, f, f2, i, false)) : renderStringWithColor(matrixStack, str, f, f2, i, false);
    }

    public int renderStringWithColor(MatrixStack matrixStack, String str, float f, float f2, int i, boolean z) {
        if (str == null) {
            return 0;
        }
        if ((i & (-67108864)) == 0) {
            i |= -16777216;
        }
        if (z) {
            i = ((i & 16579836) >> 2) | (i & (-16777216));
        }
        this.val = f * Float.intBitsToFloat(1073741824);
        this.val2 = f2 * Float.intBitsToFloat(1073741824);
        renderStringCharacters(matrixStack, str, z, i);
        return (int) (this.val / Float.intBitsToFloat(1082130432));
    }

    public void renderStringCharacters(MatrixStack matrixStack, String str, boolean z, int i) {
        float intBitsToFloat = ((i >> 24) & 255) / Float.intBitsToFloat(1132396544);
        float intBitsToFloat2 = ((i >> 16) & 255) / Float.intBitsToFloat(1132396544);
        float intBitsToFloat3 = ((i >> 8) & 255) / Float.intBitsToFloat(1132396544);
        float intBitsToFloat4 = (i & 255) / Float.intBitsToFloat(1132396544);
        matrixStack.push();
        matrixStack.translate(ClientTextRenderer.getTranslateOffset(), ClientTextRenderer.getShiftOffset(), 0.0f);
        matrixStack.scale(Float.intBitsToFloat(1056964608), Float.intBitsToFloat(1056964608), Float.intBitsToFloat(1056964608));
        if (!trajectoriesVertexConsumer.isBuilding()) {
            trajectoriesVertexConsumer.beginBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        }
        int i2 = 0;
        while (i2 < str.length()) {
            char charAt = str.charAt(i2);
            if (charAt != 167 || i2 + 1 >= str.length()) {
                this.fontHelper.bindAtlasTexture();
                advanceCursor(this.fontHelper.renderGlyphQuad(matrixStack, trajectoriesVertexConsumer, charAt, this.val, this.val2, intBitsToFloat2, intBitsToFloat4, intBitsToFloat3, intBitsToFloat), this.fontHelper);
            } else {
                int indexOf = "0123456789abcdefklmnor".indexOf(str.toLowerCase(Locale.ENGLISH).charAt(i2 + 1));
                if (indexOf < 16) {
                    if (indexOf < 0) {
                        indexOf = 15;
                    }
                    if (z) {
                        indexOf += 16;
                    }
                    int i3 = intArr[indexOf];
                    intBitsToFloat2 = ((i3 >> 16) & 255) / Float.intBitsToFloat(1132396544);
                    intBitsToFloat3 = ((i3 >> 8) & 255) / Float.intBitsToFloat(1132396544);
                    intBitsToFloat4 = (i3 & 255) / Float.intBitsToFloat(1132396544);
                } else {
                    intBitsToFloat2 = ((i >> 16) & 255) / Float.intBitsToFloat(1132396544);
                    intBitsToFloat3 = ((i >> 8) & 255) / Float.intBitsToFloat(1132396544);
                    intBitsToFloat4 = (i & 255) / Float.intBitsToFloat(1132396544);
                }
                i2++;
            }
            i2++;
        }
        matrixStack.pop();
    }

    public void advanceCursor(float f, FontGlyphAtlas fontHelper) {
        this.val += f;
    }

    public FontGlyphAtlas getFontAtlas() {
        return this.fontHelper;
    }

    public int getHalfLineHeight() {
        return this.fontHelper.getMaxCharHeight() / 2;
    }

    public int measureStringWidth(String str) {
        if (str == null) {
            return 0;
        }
        int i = 0;
        int length = str.length();
        int i2 = 0;
        while (i2 < length) {
            if (str.charAt(i2) != 167 || i2 + 1 >= length) {
                i += (int) (getFontAtlas().getCharacterWidth(str.charAt(i2)) - Float.intBitsToFloat(1090519040));
            } else {
                i2++;
            }
            i2++;
        }
        return (i / 2) + 1;
    }

    public float getCharacterAdvance(char c) {
        return (getFontAtlas().getCharacterWidth(c) - Float.intBitsToFloat(1090519040)) / Float.intBitsToFloat(1073741824);
    }

    public void resetShadowOffset() {
        if (trajectoriesVertexConsumer.isBuilding()) {
            FontGlyphAtlas fontHelper2770 = getFontAtlas();
            GlStateManager._enableBlend();
            GlStateManager._blendFunc(770, 771);
            fontHelper2770.bindAtlasTexture();
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            RenderSystem.disableDepthTest();
            TrajectoriesVertexConsumer.flush(trajectoriesVertexConsumer);
            RenderSystem.enableDepthTest();
            fontHelper2770.unbindAtlasTexture();
        }
        flushPendingText();
    }

    public static void flushPendingText() {
        if (!trajectoriesVertexConsumer2.isBuilding() || renderLayer == null) {
            return;
        }
        renderLayer.startDrawing();
        RenderSystem.disableDepthTest();
        BufferRenderer.drawWithGlobalProgram(trajectoriesVertexConsumer2.endBuffer());
        RenderSystem.enableDepthTest();
        renderLayer.endDrawing();
        renderLayer = null;
    }

    public VertexConsumerProvider getVertexConsumerProvider() {
        return vertexConsumerProvider;
    }

    public TextHandler getTextHandler() {
        return this.textHandler;
    }

    public float getAdvance() {
        return 0.0f;
    }

    public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
        return null;
    }

    static {
        for (int i = 0; i < 32; i++) {
            int i2 = ((i >> 3) & 1) * 85;
            int i3 = (((i >> 2) & 1) * 170) + i2;
            int i4 = (((i >> 1) & 1) * 170) + i2;
            int i5 = ((i & 1) * 170) + i2;
            if (i == 6) {
                i3 += 85;
            }
            if (i >= 16) {
                i3 /= 4;
                i4 /= 4;
                i5 /= 4;
            }
            intArr[i] = ((i3 & 255) << 16) | ((i4 & 255) << 8) | (i5 & 255);
        }
    }
}
