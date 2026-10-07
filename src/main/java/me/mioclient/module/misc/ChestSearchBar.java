package me.mioclient.module.misc;

import me.mioclient.gui.widget.ChestSearchOverlay;

import me.mioclient.render.RenderUtil;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.screen.ScreenDrawEvent;
import me.mioclient.module.Module;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import me.mioclient.setting.BooleanSetting;

public class ChestSearchBar extends Module {
    public Setting<Boolean> highlightFull = add(new BooleanSetting("HighlightFull", false));

    public ChestSearchBar() {
        super("ChestSearchBar", "Highlights items in chests and shulker boxes.", Category.MISC, new String[0]);
        ChestSearchOverlay.mixinTextFieldWidgetHelper.init();
        setDrawn(false);
    }

    @Listen
    public void onScreenDraw(ScreenDrawEvent event_2) {
        ChestSearchOverlay mixinTextFieldWidgetHelper = ChestSearchOverlay.mixinTextFieldWidgetHelper;
        GuiGraphicsExtractor drawContext474 = event_2.getDrawContext();
        Screen screen247 = event_2.getScreen();
        mixinTextFieldWidgetHelper.onDrawGui(drawContext474, screen247, event_2.getMatrixStack(), event_2.getX(), event_2.getY(), RenderUtil.getPartialTick());
    }

    public boolean match(ItemStack itemStack, boolean z) {
        if (ChestSearchOverlay.textFieldWidget == null) {
            return true;
        }
        String text = ChestSearchOverlay.textFieldWidget.getValue();
        if (text.isEmpty()) {
            return true;
        }
        if (!z || ChestSearchOverlay.isFull(itemStack, text)) {
            return ChestSearchOverlay.namesMatch(itemStack, text);
        }
        return false;
    }
}