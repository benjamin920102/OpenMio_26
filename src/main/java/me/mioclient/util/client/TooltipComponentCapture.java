package me.mioclient.util.client;

import me.mioclient.api.ClientAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/TooltipComponentCapture.class */
public final class TooltipComponentCapture implements ClientAccessor {
    public static boolean flag = false;
    public static List<TooltipComponent> list;

    public TooltipComponentCapture() {
        throw new AssertionError();
    }

    public static List<TooltipComponent> captureTooltipComponents(DrawContext drawContext, List<Text> list2, Optional<TooltipData> optional) {
        flag = true;
        drawContext.drawTooltip(minecraftClient.textRenderer, list2, optional, 0, 0);
        flag = false;
        return list == null ? new ArrayList() : new ArrayList(list);
    }

    public static boolean is1129() {
        return flag;
    }

    public static void setComponents(List<TooltipComponent> list2) {
        list = list2;
    }
}
