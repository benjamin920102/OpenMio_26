package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.client.InputState;
import me.mioclient.util.combat.CrystalThreatDetector;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.util.combat.CrystalThreatDetector;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.client.InputState;

import me.mioclient.render.RenderConstants;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.render.RenderEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.player.NoInteract;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/Mainhand.class */
public class Mainhand extends Module {
    public static final NoInteract noInteract = (NoInteract) ClientServices.moduleInjector.getModule(NoInteract.class);
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

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/Mainhand$Inner.class */
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
        return String.valueOf(minecraftClient.player.getInventory().main.stream().filter(itemStack -> {
            return itemStack.getItem() == getDesiredItem();
        }).mapToInt((v0) -> {
            return v0.getCount();
        }).sum());
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if (!((!WorldUtils.isInteractableHit(interactBlockEvent.getHitResult()) || minecraftClient.player.isSneaking() || noInteract.isToggled()) ? false : true) || interactBlockEvent.isCancelled()) {
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
        if (minecraftClient.player.currentScreenHandler != minecraftClient.player.playerScreenHandler) {
            return;
        }
        tryAutoSelectMainhand();
        Item item581 = getDesiredItem();
        if (item581 != minecraftClient.player.getInventory().getStack(getSelectedSlotIndex()).getItem() && minecraftClient.player.currentScreenHandler.getCursorStack().isEmpty()) {
            swapItemToMainhand(item581);
        }
    }

    public void swapItemToMainhand(Item item) {
        int i = InventoryUtils.findInInventory(itemStack -> {
            return itemStack.isOf(item);
        }, true);
        if (this.num != -1) {
            if (minecraftClient.player.getInventory().getStack(this.num).isOf(item) && i != -1) {
                i = this.num;
            }
        }
        if (i != -1 && i < minecraftClient.player.currentScreenHandler.slots.size()) {
            ItemStack stack = minecraftClient.player.currentScreenHandler.getSlot(i).getStack();
            if (!stack.contains(DataComponentTypes.FOOD) || this.flag) {
                if (this.stopwatch.hasElapsedAndResetMs(this.delay.getValue().intValue()) && this.stopwatch3.hasElapsedMs(200L)) {
                    InventoryUtils.swapSlots(i, getSelectedSlotIndex());
                    this.num = i;
                }
                if (InputState.isKeyBindingActive(minecraftClient.options.useKey) && stack.contains(DataComponentTypes.FOOD)) {
                    minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.OFF_HAND);
                }
            }
        }
    }

    public void tryAutoSelectMainhand() {
        if (this.autoSelect.getValue().booleanValue()) {
            if (minecraftClient.player.getInventory().getStack(getSelectedSlotIndex()).getItem() == Items.TOTEM_OF_UNDYING) {
                if (minecraftClient.player.isUsingItem() && minecraftClient.player.getActiveHand() == Hand.MAIN_HAND) {
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

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0075, code lost:
    
        if (me.mioclient.module.combat.Mainhand.minecraftClient.player.getMainHandStack().contains(net.minecraft.component.DataComponentTypes.FOOD) == false) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Item getDesiredItem() {
        boolean z = WorldUtils.isInteractableHit(minecraftClient.crosshairTarget) && !minecraftClient.player.isSneaking() && !noInteract.isToggled()
                && minecraftClient.player.isUsingItem() && minecraftClient.player.getActiveHand() == Hand.MAIN_HAND;
        return (minecraftClient.player.getInventory().selectedSlot == getSelectedSlotIndex() || !this.gappleBind.getValue().booleanValue() || !minecraftClient.options.useKey.isPressed() || !mainhandHasTotemOrGapple() || z || EntityUtil.getPlayerHealth() < this.safe.getValue().floatValue()) ? Items.TOTEM_OF_UNDYING : Items.ENCHANTED_GOLDEN_APPLE;
    }

    public boolean mainhandHasTotemOrGapple() {
        ItemStack mainHandStack = minecraftClient.player.getMainHandStack();
        if (minecraftClient.player.getOffHandStack().contains(DataComponentTypes.FOOD)) {
            return false;
        }
        return mainHandStack.isOf(Items.TOTEM_OF_UNDYING) || mainHandStack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
    }

    public int getSelectedSlotIndex() {
        return this.slot.getValue().intValue() - 1;
    }
}
