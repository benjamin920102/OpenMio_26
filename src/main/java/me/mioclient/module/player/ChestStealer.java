package me.mioclient.module.player;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.tooltips.ShulkerPreviewScreen;

import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.module.player.cheststealer.ChestStealerMode;
import me.mioclient.module.player.cheststealer.ChestStealerMode_2;
import me.mioclient.module.player.cheststealer.ItemComponentKey;
import me.mioclient.util.inventory.InventoryKit;
import me.mioclient.util.inventory.ItemTypeUtils;
import me.mioclient.util.math.GridPoint;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.module.player.cheststealer.ContainerButtonInjector;
import me.mioclient.module.player.cheststealer.ItemTransferAnimation;
import me.mioclient.module.player.cheststealer.SlotStackCount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.InsertItemEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.screen.SetScreenHookPostEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.screen.ScreenDrawEvent;
import me.mioclient.mixin.ducks.DuckHandledScreen;
import me.mioclient.module.Module;
import me.mioclient.module.misc.ChestSearchBar;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.ObjectSetting_2;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

public class ChestStealer extends Module {
    /* 26.2 port: resolved lazily — static init ran before the module registry was ready, leaving chestSearchBar null. */
    private static ChestSearchBar chestSearchBar;

    public static ChestSearchBar getChestSearchBar() {
        if (chestSearchBar == null && ClientServices.moduleInjector != null) {
            try {
                chestSearchBar = (ChestSearchBar) ClientServices.moduleInjector.getModule(ChestSearchBar.class);
            } catch (Exception e) {
            }
        }
        return chestSearchBar;
    }
    public Setting<Set<Item>> whitelist = add(new EntityListObjectSetting<>("Whitelist", BuiltInRegistries.ITEM));
    public Setting<Set<String>> names = add(new ObjectSetting_2("Names"));
    public Setting<ChestStealerMode> mode = add(new EnumSetting<>("Mode", ChestStealerMode.STEAL));
    public Setting<ScaffoldMode_2> select = add(new EnumSetting<>("Select", ScaffoldMode_2.ANY));
    public Setting<ChestStealerMode_2> lookFor = add(new EnumSetting<>("LookFor", ChestStealerMode_2.ITEM, v -> this.select.getValue() != ScaffoldMode_2.ANY));
    public Setting<Boolean> searchBarSync = add(new BooleanSetting("SearchBarSync", false).setVisible());
    public Setting<Boolean> onlyFull = add(new BooleanSetting("OnlyFull", false).requiresSettings(this.searchBarSync));
    public Setting<Boolean> onlyButtons = add(new BooleanSetting("OnlyButtons", false));
    public Setting<Boolean> animate = add(new BooleanSetting("Animate", true));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 50, 0, 1000).setSuffix("ms"));
    public Setting<Integer> timeout = add(new NumberSetting<>("Timeout", 200, 0, 750).setSuffix("ms"));
    public Setting<Integer> frequency = add(new NumberSetting<>("Frequency", 1, 1, 4));
    public final List<ItemTransferAnimation> list;
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public ChestStealerMode chestStealerMode;
    public boolean flag;
    public Slot slot;
    public static final /* synthetic */ boolean flag2;

    public ChestStealer() {
        super("ChestStealer", "Steals items from the storages you open.", Category.PLAYER, "stealer");
        this.list = Collections.synchronizedList(new ArrayList());
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        Setting<Boolean> setting = this.animate;
        List<ItemTransferAnimation> list = this.list;
        Objects.requireNonNull(list);
        setting.setChangeCallback(list::clear);
        this.mode.addVisibilityPredicate(chestStealerMode -> {
            return !this.onlyButtons.getValue().booleanValue();
        });
        ScreenEvents.AFTER_INIT.register(new ContainerButtonInjector(this));
    }

    

    

    

    @Listen
    
    public void handleTick(TickEvent tickEvent) {
        if ((minecraftClient.gui.screen() instanceof ContainerScreen) || (minecraftClient.gui.screen() instanceof ShulkerBoxScreen)) {
            if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue())) {
                if (this.onlyButtons.getValue().booleanValue() && this.chestStealerMode == null) {
                    return;
                }
                if (!this.stopwatch2.hasElapsedMs(this.timeout.getValue().intValue()) || (minecraftClient.gui.screen() instanceof ShulkerPreviewScreen)) {
                    return;
                }
                ChestStealerMode value = this.onlyButtons.getValue().booleanValue() ? this.chestStealerMode : this.mode.getValue();
                AbstractContainerMenu screenHandler = minecraftClient.player.containerMenu;
                int i = 0;
                int i2 = screenHandler.containerId;
                ContainerScreen genericContainerScreen = (minecraftClient.gui.screen()) instanceof ContainerScreen ? (ContainerScreen) (minecraftClient.gui.screen()) : null;
                int rows = genericContainerScreen instanceof ContainerScreen ? 9 * genericContainerScreen.getMenu().getRowCount() : 27;
                DuckHandledScreen duckHandledScreen = (DuckHandledScreen)(minecraftClient.gui.screen());
                Map<ItemComponentKey, List<SlotStackCount>> map2360 = getMap2360(rows);
                if (value == ChestStealerMode.FILL) {
                    i = rows;
                    rows = 36;
                }
                int i3 = 0;
                for (int i4 = i; i4 < i + rows; i4++) {
                    ItemStack stack = screenHandler.getSlot(i4).getItem();
                    if (!stack.isEmpty()) {
                        ChestSearchBar chestSearchBar_lcl = getChestSearchBar();
                        if (this.searchBarSync.getValue().booleanValue() && chestSearchBar_lcl != null && chestSearchBar_lcl.isToggled()) {
                            if (!chestSearchBar_lcl.match(stack, this.onlyFull.getValue().booleanValue())) {
                                continue;
                            }
                        }
                        boolean z = minecraftClient.gui.screen() instanceof ShulkerBoxScreen;
                        if (this.lookFor.getValue() == ChestStealerMode_2.ITEM) {
                            if (!this.select.getValue().isAllowedBySetting(stack.getItem(), this.whitelist)) {
                                continue;
                            }
                            List<SlotStackCount> list = map2360.get(new ItemComponentKey(stack.getItem(), stack.getComponents()));
                            switch (value) {
                                case DROP:
                                    minecraftClient.gameMode.handleContainerInput(i2, i4, 1, ContainerInput.THROW, minecraftClient.player);
                                    this.stopwatch.reset();
                                    i3++;
                                    if (i3 < this.frequency.getValue().intValue()) {
                                        break;
                                    } else {
                                        return;
                                    }
                                case FILL:
                                case STEAL:
                                    if (z && ItemTypeUtils.isShulkerBox(stack.getItem())) {
                                        break;
                                    } else {
                                        this.slot = null;
                                        this.flag = true;
                                        minecraftClient.gameMode.handleContainerInput(i2, i4, 0, ContainerInput.QUICK_MOVE, minecraftClient.player);
                                        this.flag = false;
                                        if (this.slot != null) {
                                            addTransferAnimation(screenHandler.getSlot(i4), this.slot, this.slot.getItem());
                                        }
                                        this.stopwatch.reset();
                                        i3++;
                                        if (i3 < this.frequency.getValue().intValue()) {
                                        }
                                    }
                                    break;
                                case REFILL:
                                    if (list == null) {
                                        continue;
                                    } else {
                                        refillToInventory(i4, list);
                                        i3 = 999;
                                        this.stopwatch.reset();
                                        i3++;
                                        if (i3 < this.frequency.getValue().intValue()) {
                                        }
                                    }
                                    break;
                                case REKIT:
                                    if (list != null) {
                                        refillToInventory(i4, list);
                                        i3 = 999;
                                    } else {
                                        InventoryKit chestStealerEnumSettingHelper257 = ClientServices.chestStealerHelper.getChestStealerEnumSettingHelper257();
                                        if (chestStealerEnumSettingHelper257 == null) {
                                            continue;
                                        } else {
                                            Slot slot = screenHandler.getSlot(i4);
                                            boolean z2 = false;
                                            for (Map.Entry<Integer, InventoryKit.Record> entry : chestStealerEnumSettingHelper257.getSlotRecords().entrySet()) {
                                                if (entry.getValue().matchesItem(stack)) {
                                                    boolean z3 = entry.getKey().intValue() < 9;
                                                    int intValue = rows + (z3 ? entry.getKey().intValue() + 27 : entry.getKey().intValue() - 9);
                                                    ItemStack stack2 = screenHandler.getSlot(intValue).getItem();
                                                        if (!entry.getValue().matchesItem(stack2) && (!ItemTypeUtils.isShulkerBox(stack2.getItem()) || !(screenHandler instanceof ShulkerBoxMenu))) {
                                                        addTransferAnimation(slot, screenHandler.getSlot(intValue), slot.getItem());
                                                        if (z3) {
                                                            minecraftClient.gameMode.handleContainerInput(i2, i4, entry.getKey().intValue(), ContainerInput.SWAP, minecraftClient.player);
                                                        } else {
                                                            minecraftClient.gameMode.handleContainerInput(i2, i4, 0, ContainerInput.PICKUP, minecraftClient.player);
                                                            minecraftClient.gameMode.handleContainerInput(i2, intValue, 0, ContainerInput.PICKUP, minecraftClient.player);
                                                            minecraftClient.gameMode.handleContainerInput(i2, i4, 0, ContainerInput.PICKUP, minecraftClient.player);
                                                        }
                                                        i3 = 999;
                                                        z2 = true;
                                                        if (!z2) {
                                                            continue;
                                                        }
                                                    }
                                                }
                                            }
                                            if (!z2) {
                                            }
                                        }
                                    }
                                    this.stopwatch.reset();
                                    i3++;
                                    if (i3 < this.frequency.getValue().intValue()) {
                                    }
                                    break;
                                default:
                                    this.stopwatch.reset();
                                    i3++;
                                    if (i3 < this.frequency.getValue().intValue()) {
                                    }
                                    break;
                            }
                        } else {
                            if (!this.select.getValue().isAllowedBySetting(stack.getHoverName().getString().toLowerCase(Locale.ROOT), this.names)) {
                                continue;
                            }
                            List<SlotStackCount> list2 = map2360.get(new ItemComponentKey(stack.getItem(), stack.getComponents()));
                            switch (value) {
                            }
                        }
                    }
                }
            }
        }
    }

    @Listen
    public void onSetScreenHookPost(SetScreenHookPostEvent setScreenHookPostEvent) {
        this.stopwatch2.reset();
        this.list.clear();
    }

    @Listen
    public void onScreenDraw(ScreenDrawEvent event_2) {
        synchronized (this.list) {
            this.list.removeIf((v0) -> {
                return v0.is2551();
            });
            Iterator<ItemTransferAnimation> it = this.list.iterator();
            while (it.hasNext()) {
                it.next().do2548(event_2.getDrawContext());
            }
        }
    }

    @Listen
    public void onInsertItem(InsertItemEvent insertItemEvent) {
        if (!this.flag || insertItemEvent.getSlot().getItem().isEmpty()) {
            return;
        }
        this.slot = insertItemEvent.getSlot();
    }

    public void refillToInventory(int i, List<SlotStackCount> list) {
        Slot slot = minecraftClient.player.containerMenu.getSlot(i);
        if (slot.getItem().isStackable()) {
            int i2 = minecraftClient.player.containerMenu.containerId;
            int count = slot.getItem().getCount();
            int i3 = 0;
            minecraftClient.gameMode.handleContainerInput(i2, i, 0, ContainerInput.PICKUP, minecraftClient.player);
            for (SlotStackCount chestStealerHelper : list) {
                Slot slot2 = minecraftClient.player.containerMenu.getSlot(chestStealerHelper.getSlotIndex());
                minecraftClient.gameMode.handleContainerInput(i2, chestStealerHelper.getSlotIndex(), 0, ContainerInput.PICKUP, minecraftClient.player);
                int i4 = chestStealerHelper.getMaxCount() - chestStealerHelper.getCount();
                chestStealerHelper.setCount(chestStealerHelper.getCount() + count);
                count -= i4;
                addTransferAnimation(minecraftClient.player.containerMenu.getSlot(i), slot2, slot2.getItem().copyWithCount(i4));
                if (minecraftClient.player.containerMenu.getCarried().isEmpty()) {
                    break;
                }
                i3++;
                if (i3 >= this.frequency.getValue().intValue()) {
                    break;
                }
            }
            if (minecraftClient.player.containerMenu.getCarried().isEmpty()) {
                return;
            }
            minecraftClient.gameMode.handleContainerInput(i2, i, 0, ContainerInput.PICKUP, minecraftClient.player);
        }
    }

    public boolean canStealItem(ItemStack itemStack) {
        return ((minecraftClient.player.containerMenu instanceof ShulkerBoxMenu) && ItemTypeUtils.isShulkerBox(itemStack.getItem())) ? false : true;
    }

    public void addTransferAnimation(Slot slot, Slot slot2, ItemStack itemStack) {
        DuckHandledScreen duckHandledScreen = (DuckHandledScreen)(minecraftClient.gui.screen());
        if (!flag2 && duckHandledScreen == null) {
            throw new AssertionError();
        }
        if (this.animate.getValue().booleanValue()) {
            this.list.add(new ItemTransferAnimation(itemStack, new GridPoint(duckHandledScreen.getX() + slot.x, duckHandledScreen.getY() + slot.y), new GridPoint(duckHandledScreen.getX() + slot2.x, duckHandledScreen.getY() + slot2.y)));
        }
    }

    public Map<ItemComponentKey, List<SlotStackCount>> getMap2360(int i) {
        HashMap hashMap = new HashMap();
        for (int i2 = 0; i2 < 36; i2++) {
            if (i2 + i < minecraftClient.player.containerMenu.slots.size()) {
                ItemStack stack = minecraftClient.player.containerMenu.getSlot(i + i2).getItem();
                if (stack.getCount() < stack.getMaxStackSize() && !stack.isEmpty() && stack.isStackable()) {
                    ItemComponentKey chestStealerData = new ItemComponentKey(stack.getItem(), stack.getComponents());
                    hashMap.putIfAbsent(chestStealerData, new ArrayList());
                    ((List) hashMap.get(chestStealerData)).add(new SlotStackCount(i + i2, stack.getMaxStackSize(), stack.getCount()));
                }
            }
        }
        return hashMap;
    }

    public void setButtonMode(ChestStealerMode chestStealerMode) {
        this.chestStealerMode = chestStealerMode;
    }

    public ChestStealerMode getChestStealerMode2362() {
        return this.chestStealerMode;
    }

    static {
        flag2 = !ChestStealer.class.desiredAssertionStatus();
    }
}