package me.mioclient.render.tooltip;

import me.mioclient.api.ClientAccessor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.resources.Identifier;

public final class HeightTooltipComponent implements ClientAccessor, ClientTooltipComponent {
    public final int num;
    public final MapItemSavedData mapState;
    public static final Identifier identifier = Identifier.fromNamespaceAndPath("minecraft", "textures/map/map_background.png");

    public HeightTooltipComponent(int i, MapItemSavedData mapState) {
        this.num = i;
        this.mapState = mapState;
    }

    @Override
    public int getHeight(Font font) {
        return 110;
    }

    @Override
    public int getWidth(Font font) {
        return 108;
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(0.75f, 0.75f);
        graphics.blit(RenderPipelines.GUI_TEXTURED, identifier, 0, 0, 0.0f, 0.0f, 64, 64, 64, 64);
        graphics.pose().popMatrix();
    }

    public int getEntityId() {
        return this.num;
    }

    public MapItemSavedData getMapState() {
        return this.mapState;
    }
}
