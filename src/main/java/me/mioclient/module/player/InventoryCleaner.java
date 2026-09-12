package me.mioclient.module.player;

import me.mioclient.module.render.tooltips.ShulkerPreviewScreen;

import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.util.time.Stopwatch;

import java.util.Iterator;
import java.util.Set;
import me.mioclient.render.RenderConstants;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.module.render.tooltips.ShulkerPreviewScreen;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/InventoryCleaner.class */
public class InventoryCleaner extends Module {
    public Setting<Set<Item>> whitelist = add(new EntityListObjectSetting<>("Whitelist", Registries.ITEM));
    public Setting<ScaffoldMode_2> selection = add(new EnumSetting<>("Selection", ScaffoldMode_2.WHITELIST));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 100, 0, 1000).setSuffix("ms"));
    public Setting<Integer> frequency = add(new NumberSetting<>("Frequency", 1, 1, 4));
    public Setting<Boolean> onlyUI = add(new BooleanSetting("OnlyUI", false));
    public Setting<Boolean> ignoreHotbar = add(new BooleanSetting("IgnoreHotbar", true));
    public final Stopwatch stopwatch;

    public InventoryCleaner() {
        super("InventoryCleaner", "Gets rid of the selected items in your inventory.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Listen(get219= RenderConstants.num2)
    public void onTick(TickEvent tickEvent) {
        int i = 0;
        if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue())) {
            Screen screen = minecraftClient.currentScreen;
            boolean z = (screen instanceof InventoryScreen) || (screen instanceof GenericContainerScreen) || ((screen instanceof ShulkerBoxScreen) && !(screen instanceof ShulkerPreviewScreen));
            if (!this.onlyUI.getValue().booleanValue() || z) {
                int size = minecraftClient.player.currentScreenHandler.slots.size();
                if (minecraftClient.player.currentScreenHandler instanceof PlayerScreenHandler) {
                    size--;
                }
                Iterator it = minecraftClient.player.currentScreenHandler.slots.iterator();
                while (it.hasNext()) {
                    Slot slot = (Slot) it.next();
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty()) {
                        if (this.selection.getValue().isAllowedBySetting(stack.getItem(), this.whitelist) && (slot.id < size - 9 || !this.ignoreHotbar.getValue().booleanValue())) {
                            minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, slot.id, 1, SlotActionType.THROW, minecraftClient.player);
                            this.stopwatch.reset();
                            i++;
                            if (i >= this.frequency.getValue().intValue()) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
