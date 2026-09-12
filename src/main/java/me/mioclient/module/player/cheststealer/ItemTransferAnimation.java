package me.mioclient.module.player.cheststealer;

import me.mioclient.util.anim.AnimationProgress;
import me.mioclient.util.math.GridPoint;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.math.GridPoint;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import me.mioclient.util.anim.AnimationProgress;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ItemTransferAnimation.class */
public class ItemTransferAnimation implements ClientAccessor {
    public final AnimationProgress progress = new AnimationProgress(Float.intBitsToFloat(1073741824));
    public final ItemStack itemStack;
    public final GridPoint positionData;
    public final GridPoint positionData2;

    public ItemTransferAnimation(ItemStack itemStack, GridPoint positionData, GridPoint positionData2) {
        this.itemStack = itemStack.copy();
        this.positionData = positionData2;
        this.positionData2 = positionData;
        this.progress.setToggledImmediate(false);
    }

    public void do2548(DrawContext drawContext) {
        this.progress.animateToggle(true);
        float f = this.progress.getValue();
        int i = (int) (this.positionData2.get476() + ((this.positionData.get476() - this.positionData2.get476()) * f));
        int i2 = (int) (this.positionData2.getGridY() + ((this.positionData.getGridY() - this.positionData2.getGridY()) * f));
        drawContext.getMatrices().push();
        if (f > Double.longBitsToDouble(4605380978949069210L)) {
            RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216) - ((f - Float.intBitsToFloat(1061997773)) / Float.intBitsToFloat(1045220557)));
        }
        drawContext.drawItem(this.itemStack, i, i2, 0, 100);
        drawContext.drawItemInSlot(minecraftClient.textRenderer, this.itemStack, i, i2);
        if (f > Double.longBitsToDouble(4605380978949069210L)) {
            RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
        }
        drawContext.getMatrices().pop();
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public GridPoint getTargetPosition() {
        return this.positionData;
    }

    public boolean is2551() {
        return this.progress.getRawValue() == Float.intBitsToFloat(1073741824);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ItemTransferAnimation chestStealerSearchHelper4_2 = (ItemTransferAnimation) obj;
        return Objects.equals(this.itemStack.getItem(), chestStealerSearchHelper4_2.itemStack.getItem()) && Objects.equals(this.positionData, chestStealerSearchHelper4_2.positionData);
    }

    public int hashCode() {
        return Objects.hash(this.itemStack.getItem(), this.positionData);
    }
}
