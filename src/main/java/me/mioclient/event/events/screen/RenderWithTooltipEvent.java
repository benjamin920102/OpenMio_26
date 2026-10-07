package me.mioclient.event.events.screen;

import me.mioclient.event.Event;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class RenderWithTooltipEvent extends Event {
    public static int num;
    public static int num2;
    public final Screen screen;
    public List<ClientTooltipComponent> list;
    public int x;
    public int y;

    public RenderWithTooltipEvent(Screen screen, List<ClientTooltipComponent> list, int i, int i2, int i3, int i4) {
        this.screen = screen;
        this.list = list;
        this.x = i;
        this.y = i2;
        num = i3;
        num2 = i4;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public List<ClientTooltipComponent> getComponents() {
        return this.list;
    }

    public void setComponents(List<ClientTooltipComponent> list) {
        this.list = list;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int i) {
        this.x = i;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int i) {
        this.y = i;
    }
}
