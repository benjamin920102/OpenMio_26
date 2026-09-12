package me.mioclient.mixin;

import me.mioclient.gui.widget.ChestSearchOverlay;

import me.mioclient.gui.widget.ChestSearchOverlay;
import me.mioclient.render.RenderUtil;

import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* compiled from: 0.java */
@Mixin({TextFieldWidget.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinTextFieldWidget.class */
public class MixinTextFieldWidget {

    @Shadow
    private boolean field_2095;
    private MatrixStack lastmatrix;

    @Inject(method = {"renderWidget"}, at = {@At("HEAD")})
    public void renderButton(DrawContext drawContext, int i, int i2, float f, CallbackInfo callbackInfo) {
        this.lastmatrix = drawContext.getMatrices();
    }

    @Redirect(method = {"renderWidget"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;drawsBackground()Z", ordinal = 0))
    public boolean drawsBackground(TextFieldWidget textFieldWidget) {
        if (textFieldWidget != ChestSearchOverlay.textFieldWidget) {
            return this.field_2095;
        }
        int x = textFieldWidget.getX() - 1;
        int y = textFieldWidget.getY() - 1;
        int width = textFieldWidget.getWidth() + 1;
        int height = textFieldWidget.getHeight() + 1;
        RenderUtil.INSTANCE.drawFilledRect(this.lastmatrix, x, y, x + width, y + height, new Color(135, 135, 135, 255).getRGB());
        RenderUtil.INSTANCE.drawFilledRect(this.lastmatrix, x, y, (x + width) - 1, y + 1, new Color(85, 85, 85, 255).getRGB());
        RenderUtil.INSTANCE.drawFilledRect(this.lastmatrix, x, y, x + 1, (y + height) - 1, new Color(85, 85, 85, 255).getRGB());
        RenderUtil.INSTANCE.drawFilledRect(this.lastmatrix, x + 1, (y + height) - 1, (x + width) - 1, y + height, -1);
        RenderUtil.INSTANCE.drawFilledRect(this.lastmatrix, (x + width) - 1, y + 1, x + width, y + height, -1);
        return false;
    }
}
