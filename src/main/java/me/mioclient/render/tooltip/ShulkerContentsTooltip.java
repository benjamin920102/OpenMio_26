package me.mioclient.render.tooltip;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.InputState;

import me.mioclient.api.ClientAccessor;

import java.util.List;
import me.mioclient.event.events.screen.RenderWithTooltipEvent;
import me.mioclient.module.render.Tooltips;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.ChatFormatting;

public final class ShulkerContentsTooltip implements ClientAccessor, ClientTooltipComponent {
    public final List<ItemStack> list;
    /* Resolved lazily — eager static init ran before the module registry was ready. */
    private static Tooltips tooltips;

    public static Tooltips getTooltips() {
        if (tooltips == null && ClientServices.moduleInjector != null) {
            try {
                tooltips = (Tooltips) ClientServices.moduleInjector.getModule(Tooltips.class);
            } catch (Exception e) {
            }
        }
        return tooltips;
    }

    public ShulkerContentsTooltip(List<ItemStack> list) {
        this.list = list;
    }

    @Override
    public int getHeight(Font font) {
        return 53;
    }

    @Override
    public int getWidth(Font font) {
        return 155;
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        drawItems(font, x, y, graphics);
    }

    public void drawItems(Font textRenderer, int i, int i2, GuiGraphicsExtractor drawContext) {
        int i3 = 0;
        int i4 = 0;
        int i5 = RenderWithTooltipEvent.num;
        int i6 = RenderWithTooltipEvent.num2;
        ItemStack itemStack = null;
        for (ItemStack itemStack2 : this.list) {
            int i7 = i + (17 * i3);
            int i8 = i2 + (17 * i4);
            boolean z = itemStack2.getCount() == 69;
            if (i5 >= i7 && i5 <= i7 + 17 && i6 >= i8 && i6 <= i8 + 17 && itemStack == null) {
                itemStack = itemStack2;
            }
            drawContext.item(itemStack2, i7 + 1, i8 + 1);
            drawContext.itemDecorations(textRenderer, itemStack2, i7 + 1, i8 + 1, z ? new StringTemplate().arg(String.valueOf(ChatFormatting.RED)).format("1") : null);
            i3++;
            if (i3 >= 9) {
                i3 = 0;
                i4++;
            }
        }
        if (itemStack == null || itemStack.is(Items.AIR)) {
            return;
        }
        drawContext.setTooltipForNextFrame(minecraftClient.font, itemStack, i5, i6);
    }

    public List<ItemStack> getItems() {
        return this.list;
    }
}
