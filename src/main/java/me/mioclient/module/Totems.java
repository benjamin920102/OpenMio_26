package me.mioclient.module;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.inventory.InventoryUtils;

import java.awt.Color;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.api.Setting;
import me.mioclient.render.hud.ResizableHudElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Totems.class */
public class Totems extends me.mioclient.module.HudModule {
    public Setting<Boolean> setting;

    public Totems() {
        super("Totems", new String[0]);
        this.setting = add(new BooleanSetting("White", false));
        setElement(new ResizableHudElement(this));
    }

    @Override // me.mioclient.module.HudModule
    public void renderContent(DrawContext drawContext) {
        ItemStack itemStack = new ItemStack(Items.TOTEM_OF_UNDYING, InventoryUtils.countItems(itemStack2 -> {
            return itemStack2.isOf(Items.TOTEM_OF_UNDYING);
        }));
        if (itemStack.getCount() == 0) {
            return;
        }
        drawContext.drawItem(itemStack, 0, 0);
        if (itemStack.getCount() > 1) {
            String valueOf = String.valueOf(itemStack.getCount());
            drawContext.draw();
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.0f, 0.0f, Float.intBitsToFloat(1140457472));
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, valueOf, Float.intBitsToFloat(1099431936) - ClientTextRenderer.INSTANCE.getStringWidth(valueOf), Float.intBitsToFloat(1091567616), this.setting.getValue().booleanValue() ? Color.white : getHudColor(Float.intBitsToFloat(1091567616)));
            drawContext.getMatrices().pop();
        }
    }

    @Override // me.mioclient.module.HudModule
    public float[] getSize() {
        return new float[]{Float.intBitsToFloat(1098907648), Float.intBitsToFloat(1098907648)};
    }
}
