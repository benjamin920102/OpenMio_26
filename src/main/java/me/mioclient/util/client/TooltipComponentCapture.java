package me.mioclient.util.client;

import me.mioclient.api.ClientAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.network.chat.Component;

public final class TooltipComponentCapture implements ClientAccessor {
    public static boolean flag = false;
    public static List<TooltipComponent> list;

    public TooltipComponentCapture() {
        throw new AssertionError();
    }

    // 26.2: TooltipData removed; tooltips render via setTooltipForNextFrame(Font, lines, Optional<TooltipComponent>).
    // Capture keeps the data-layer image component so callers can build ClientTooltipComponents from it.
    public static List<TooltipComponent> captureTooltipComponents(GuiGraphicsExtractor drawContext, List<Component> list2, Optional<TooltipComponent> optional) {
        flag = true;
        try {
            List<TooltipComponent> out = new ArrayList<>();
            optional.ifPresent(out::add);
            setComponents(out);
            return new ArrayList<>(out);
        } finally {
            flag = false;
        }
    }

    // 26.2: ItemStack#getTooltipImage() supplies the data-layer image (bundle/shulker/map) for the capture above.
    public static List<TooltipComponent> captureForStack(net.minecraft.world.item.ItemStack stack) {
        flag = true;
        try {
            List<TooltipComponent> out = new ArrayList<>();
            if (stack != null && !stack.isEmpty()) {
                stack.getTooltipImage().ifPresent(out::add);
            }
            setComponents(out);
            return new ArrayList<>(out);
        } finally {
            flag = false;
        }
    }

    public static boolean is1129() {
        return flag;
    }

    public static void setComponents(List<TooltipComponent> list2) {
        list = list2;
    }
}
