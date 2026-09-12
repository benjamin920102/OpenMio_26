package me.mioclient.module.client.hud;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.anim.ValueAnimator;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.anim.ValueAnimator;

import java.awt.Color;
import me.mioclient.module.client.HUD;
import net.minecraft.client.gui.DrawContext;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HudEdgeDragHandler.class */
public final class HudEdgeDragHandler implements ClientAccessor {
    public static HUD hud = (HUD) ClientServices.moduleInjector.getModule(HUD.class);
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;
    public final ValueAnimator zoomHelper = new ValueAnimator();
    public final ValueAnimator zoomHelper2 = new ValueAnimator();

    public void do1010(DrawContext drawContext, int i, int i2, float f) {
        int scaledWindowWidth = drawContext.getScaledWindowWidth();
        int scaledWindowHeight = drawContext.getScaledWindowHeight();
        boolean z = isNearHorizontalEdge((double) i) || this.flag3;
        boolean z2 = isNearVerticalEdge((double) i2) || this.flag4;
        this.zoomHelper.animateTo(z ? Float.intBitsToFloat(1124859904) : 0.0f, 250L);
        this.zoomHelper2.animateTo((z2 && this.zoomHelper.getValue() == 0.0f) ? Float.intBitsToFloat(1124859904) : 0.0f, 250L);
        if (this.zoomHelper.getValue() > 0.0f) {
            if (this.flag3) {
                hud.setSafeAreaX(this.flag ? Math.abs(i - scaledWindowWidth) : i);
            }
            int i3 = ColorUtils.toIntWithAlpha(Color.black, (int) this.zoomHelper.getValue());
            Shape2DRenderer.drawRect(drawContext.getMatrices(), 0.0f, 0.0f, hud.getSafeAreaX(), scaledWindowHeight, i3);
            Shape2DRenderer.drawRect(drawContext.getMatrices(), scaledWindowWidth - hud.getSafeAreaX(), 0.0f, scaledWindowWidth, scaledWindowHeight, i3);
            return;
        }
        if (this.zoomHelper2.getValue() > 0.0f) {
            if (this.flag4) {
                hud.setSafeAreaY(this.flag2 ? Math.abs(i2 - scaledWindowHeight) : i2);
            }
            int i4 = ColorUtils.toIntWithAlpha(Color.black, (int) this.zoomHelper2.getValue());
            Shape2DRenderer.drawRect(drawContext.getMatrices(), 0.0f, 0.0f, scaledWindowWidth, hud.getSafeAreaY(), i4);
            Shape2DRenderer.drawRect(drawContext.getMatrices(), 0.0f, scaledWindowHeight - hud.getSafeAreaY(), scaledWindowWidth, scaledWindowHeight, i4);
        }
    }

    public void mouseClicked(double d, double d2, int i) {
        if (i != 0) {
            return;
        }
        this.flag3 = isNearHorizontalEdge(d);
        if (!this.flag3) {
            this.flag4 = isNearVerticalEdge(d2);
        }
        this.flag = d > ((double) (((float) minecraftClient.getWindow().getScaledWidth()) / Float.intBitsToFloat(1073741824)));
        this.flag2 = d2 > ((double) (((float) minecraftClient.getWindow().getScaledHeight()) / Float.intBitsToFloat(1073741824)));
    }

    public void mouseReleased(double d, double d2, int i) {
        if (i != 0) {
            return;
        }
        this.flag3 = false;
        this.flag4 = false;
    }

    public boolean isNearHorizontalEdge(double d) {
        return d < ((double) (hud.getSafeAreaX() + 1)) || d > ((double) ((minecraftClient.getWindow().getScaledWidth() - hud.getSafeAreaX()) - 1));
    }

    public boolean isNearVerticalEdge(double d) {
        return d < ((double) (hud.getSafeAreaY() + 1)) || d > ((double) ((minecraftClient.getWindow().getScaledHeight() - hud.getSafeAreaY()) - 1));
    }
}
