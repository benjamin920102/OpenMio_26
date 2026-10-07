package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.client.InputState;
import me.mioclient.util.combat.CrystalThreatDetector;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.render.RenderConstants;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.render.RenderEvent;
import me.mioclient.module.Module;
import me.mioclient.module.player.NoInteract;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

public class Mainhand extends Module {
    /* Resolved lazily — static init ran before module registry was ready, leaving field null. */
    private static NoInteract noInteract;

    public static NoInteract getNoInteract() {
        if (noInteract == null && ClientServices.moduleInjector != null) {
            try {
                noInteract = (NoInteract) ClientServices.moduleInjector.getModule(NoInteract.class);
            } catch (Exception e) {
            }
        }
        return noInteract;
    }
    public Setting<Integer> slot = add(new NumberSetting<>("Slot", 1, 1, 9));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 25, 0, 500).setSuffix("ms"));
    public Setting<Boolean> autoSelect = add(new BooleanSetting("AutoSelect", false).setVisible());
    public Setting<Integer> delay2 = add(new NumberSetting<>("Delay", 25, 0, 500).setSuffix("ms").requiresSettings(this.autoSelect));
    public Setting<Boolean> lethal = add(new BooleanSetting("Lethal", true).requiresSettings(this.autoSelect));
    public Setting<Float> health = add(new NumberSetting<>("Health", 8.0f, 0.0f, 36.0f).setSuffix("hp").requiresSettings(this.autoSelect));
    public Setting<Boolean> gappleBind = add(new BooleanSetting("GappleBind", true).setVisible());
    public Setting<Float> safe = add(new NumberSetting<>("Safe", 8.0f, 0.0f, 36.0f).setSuffix("hp").requiresSettings(this.gappleBind));
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public final Stopwatch stopwatch3;
    public boolean flag;
    public int num;
    public final CrystalThreatDetector mainhandHelper;

    
    class Inner extends CrystalThreatDetector {
        public Inner() {
        }

        @Override // me.mioclient.util.combat.CrystalThreatDetector
        public boolean isEnabled() {
            return Mainhand.this.lethal.getValue().booleanValue() && Mainhand.this.autoSelect.getValue().booleanValue();
        }
    }

    public Mainhand() {
        super("Mainhand", "Auto totem for main hand.", Category.COMBAT, new String[0]);
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        this.stopwatch3 = new Stopwatch();
        this.mainhandHelper = new Inner();
        this.delay2.setConfigName("SwapDelay");
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.num = -1;
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return String.valueOf(minecraftClient.player.getInventory().getNonEquipmentItems().stream().filter(itemStack -> {
            return itemStack.getItem() == getDesiredItem();
        }).mapToInt((v0) -> {
            return v0.getCount();
        }).sum());
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        NoInteract noInteract_lcl = getNoInteract();
        if (!((!WorldUtils.isInteractableHit(interactBlockEvent.getHitResult()) || minecraftClient.player.isShiftKeyDown() || (noInteract_lcl != null && noInteract_lcl.isToggled())) ? false : true) || interactBlockEvent.isCancelled()) {
            return;
        }
        this.stopwatch3.reset();
    }

    @Listen(get219= RenderConstants.num2)
    public void onRender(RenderEvent renderEvent) {
        if (isNotInGame()) {
            return;
        }
        this.flag = renderEvent.isThirdPerson();
        this.mainhandHelper.recalculateLayout();
        if (minecraftClient.player.containerMenu != minecraftClient.player.inventoryMenu) {
            return;
        }
        tryAutoSelectMainhand();
        Item item581 = getDesiredItem();
        if (item581 != minecraftClient.player.getInventory().getItem(getSelectedSlotIndex()).getItem() && minecraftClient.player.containerMenu.getCarried().isEmpty()) {
            swapItemToMainhand(item581);
        }
    }

    public void swapItemToMainhand(Item item) {
        int i = InventoryUtils.findInInventory(itemStack -> {
            return itemStack.is(item);
        }, true);
        if (this.num != -1) {
            if (minecraftClient.player.getInventory().getItem(this.num).is(item) && i != -1) {
                i = this.num;
            }
        }
        if (i != -1 && i < minecraftClient.player.containerMenu.slots.size()) {
            ItemStack stack = minecraftClient.player.containerMenu.getSlot(i).getItem();
            if (!stack.has(DataComponents.FOOD) || this.flag) {
                if (this.stopwatch.hasElapsedAndResetMs(this.delay.getValue().intValue()) && this.stopwatch3.hasElapsedMs(200L)) {
                    InventoryUtils.swapSlots(i, getSelectedSlotIndex());
                    this.num = i;
                }
                if (InputState.isKeyBindingActive(minecraftClient.options.keyUse) && stack.has(DataComponents.FOOD)) {
                    minecraftClient.gameMode.useItem(minecraftClient.player, InteractionHand.OFF_HAND);
                }
            }
        }
    }

    public void tryAutoSelectMainhand() {
        if (this.autoSelect.getValue().booleanValue()) {
            if (minecraftClient.player.getInventory().getItem(getSelectedSlotIndex()).getItem() == Items.TOTEM_OF_UNDYING) {
                if (minecraftClient.player.isUsingItem() && minecraftClient.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                    return;
                }
                if (this.mainhandHelper.isThreatActive() || EntityUtil.getPlayerHealth() <= this.health.getValue().floatValue()) {
                    if (this.stopwatch2.hasElapsedAndResetMs(this.delay2.getValue().intValue())) {
                        InventoryUtils.selectSlot(getSelectedSlotIndex());
                    }
                }
            }
        }
    }

    
    public Item getDesiredItem() {
        NoInteract noInteract_lcl = getNoInteract();
        boolean z = WorldUtils.isInteractableHit(minecraftClient.hitResult) && !minecraftClient.player.isShiftKeyDown() && (noInteract_lcl == null || !noInteract_lcl.isToggled())
                && minecraftClient.player.isUsingItem() && minecraftClient.player.getUsedItemHand() == InteractionHand.MAIN_HAND;
        return (minecraftClient.player.getInventory().getSelectedSlot() == getSelectedSlotIndex() || !this.gappleBind.getValue().booleanValue() || !minecraftClient.options.keyUse.isDown() || !mainhandHasTotemOrGapple() || z || EntityUtil.getPlayerHealth() < this.safe.getValue().floatValue()) ? Items.TOTEM_OF_UNDYING : Items.ENCHANTED_GOLDEN_APPLE;
    }

    public boolean mainhandHasTotemOrGapple() {
        ItemStack mainHandStack = minecraftClient.player.getMainHandItem();
        if (minecraftClient.player.getOffhandItem().has(DataComponents.FOOD)) {
            return false;
        }
        return mainHandStack.is(Items.TOTEM_OF_UNDYING) || mainHandStack.is(Items.ENCHANTED_GOLDEN_APPLE);
    }

    public int getSelectedSlotIndex() {
        return this.slot.getValue().intValue() - 1;
    }
}