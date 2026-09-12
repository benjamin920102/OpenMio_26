package me.mioclient.module.player;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.inventory.InventoryUtils;

import java.util.Iterator;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/AutoEat.class */
public class AutoEat extends Module {
    public Setting<Integer> health = add(new NumberSetting<>("Health", 16, 0, 36).setHoleSnapConfig("Off", HoleSnapMode.MIN));
    public Setting<Integer> hunger = add(new NumberSetting<>("Hunger", 6, -1, 20).setHoleSnapConfig("Off", HoleSnapMode.MIN));
    public Setting<Boolean> pauseBaritone = add(new BooleanSetting("PauseBaritone", false));
    public Setting<Boolean> autoSwap = add(new BooleanSetting("AutoSwap", true).setVisible());
    public Setting<AutoEatMode> sort = add(new EnumSetting<>("Sort", AutoEatMode.autoEatMode2).requiresSettings(this.autoSwap));
    public Setting<Boolean> swapBack = add(new BooleanSetting("SwapBack", false).requiresSettings(this.autoSwap));
    public Setting<Boolean> autoEscape = add(new BooleanSetting("AutoEscape", false).setVisible());
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", false).requiresSettings(this.autoEscape));
    public int num;
    public boolean flag;
    public int num2;

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/player/AutoEat$AutoEatMode.class */
    public static enum AutoEatMode implements NamedValue {
        autoEatMode("Hunger") {
            @Override
            public double scoreFood(FoodComponent foodComponent) {
                return foodComponent.nutrition();
            }
        },
        autoEatMode2("Saturation") {
            @Override
            public double scoreFood(FoodComponent foodComponent) {
                return foodComponent.saturation();
            }
        },
        autoEatMode3("Health") {
            @Override
            public double scoreFood(FoodComponent foodComponent) {
                Iterator it = foodComponent.effects().iterator();
                while (it.hasNext()) {
                    FoodComponent.StatusEffectEntry statusEffectEntry = (FoodComponent.StatusEffectEntry) it.next();
                    if (statusEffectEntry.effect().getEffectType() == StatusEffects.REGENERATION) {
                        return statusEffectEntry.effect().getAmplifier();
                    }
                }
                return 0.0d;
            }
        };

        public final String name;

        AutoEatMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public double scoreFood(FoodComponent foodComponent) {
            return 0.0d;
        }
    }

    public AutoEat() {
        super("AutoEat", "Eats your food for you.", Category.PLAYER, new String[0]);
        this.pauseBaritone.requireBaritone();
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        setEating(false);
        this.num = -1;
        this.num2 = -1;
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        minecraftClient.options.useKey.setPressed(false);
        ClientServices.obstaclePasserHelper.removePauseRequest(this);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x00e7, code lost:
    
        if (me.mioclient.module.player.AutoEat.minecraftClient.player.getInventory().getStack(r6).isOf(net.minecraft.item.Items.CHORUS_FRUIT) == false) goto L17;
     */
    @Listen
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onResetEvent(MotionEvent motionEvent) {
        int i;
        if (motionEvent.getPhase() == KeyPearlMode.Post) {
            return;
        }
        ClientServices.obstaclePasserHelper.removePauseRequest(this);
        if (!this.autoSwap.getValue().booleanValue() && this.flag) {
            if (!minecraftClient.player.getMainHandStack().contains(DataComponentTypes.FOOD)) {
                minecraftClient.interactionManager.stopUsingItem(minecraftClient.player);
                setEating(false);
                return;
            }
        }
        int i2 = get2085();
        if (i2 != -1) {
        }
        if (isInDangerousProximity() && (i = InventoryUtils.findSlot(Items.CHORUS_FRUIT)) != -1) {
            i2 = -1;
            this.num2 = i;
            do438(this.num2);
        }
        if (is2088() && !isInDangerousProximity()) {
            if (this.flag) {
                minecraftClient.interactionManager.stopUsingItem(minecraftClient.player);
                setEating(false);
                if (this.autoSwap.getValue().booleanValue() && this.swapBack.getValue().booleanValue() && this.num != -1) {
                    InventoryUtils.selectSlot(this.num);
                }
                if (this.num2 != -1) {
                    do438(this.num2);
                    this.num2 = -1;
                }
                if (this.autoDisable.getValue().booleanValue() && this.autoEscape.getValue().booleanValue()) {
                    disable();
                }
                this.num = -1;
                return;
            }
            return;
        }
        boolean z = i2 == 40;
        if (this.autoSwap.getValue().booleanValue() && !z && i2 != -1 && minecraftClient.player.getInventory().selectedSlot != i2) {
            this.num = minecraftClient.player.getInventory().selectedSlot;
            InventoryUtils.selectSlot(i2);
        }
        ItemStack mainHandStack = minecraftClient.player.getMainHandStack();
        if (!minecraftClient.player.isUsingItem() && (z || mainHandStack.contains(DataComponentTypes.FOOD))) {
            minecraftClient.interactionManager.interactItem(minecraftClient.player, z ? Hand.OFF_HAND : Hand.MAIN_HAND);
            setEating(true);
        }
        if (this.pauseBaritone.getValue().booleanValue() && minecraftClient.player.isUsingItem()) {
            ClientServices.obstaclePasserHelper.addPauseRequest(this);
        }
    }

    public int get2085() {
        double longBitsToDouble = Double.longBitsToDouble(-4616189618054758400L);
        int i = -1;
        int i2 = 0;
        while (true) {
            if (i2 >= 10) {
                break;
            }
            if (i2 == 9) {
                i2 = 40;
            }
            ItemStack stack = minecraftClient.player.getInventory().getStack(i2);
            FoodComponent foodComponent = (FoodComponent) stack.get(DataComponentTypes.FOOD);
            if (foodComponent != null && (foodComponent.canAlwaysEat() || minecraftClient.player.getHungerManager().getFoodLevel() < 20)) {
                double d = this.sort.getValue().scoreFood(foodComponent);
                if (d > longBitsToDouble || (d == longBitsToDouble && i2 == minecraftClient.player.getInventory().selectedSlot)) {
                    longBitsToDouble = d;
                    i = i2;
                }
                if (stack.getItem() == Items.CHORUS_FRUIT && isInDangerousProximity()) {
                    i = i2;
                    break;
                }
            }
            i2++;
        }
        return i;
    }

    public boolean is2086() {
        return this.flag;
    }

    public boolean isInDangerousProximity() {
        if (!this.autoEscape.getValue().booleanValue()) {
            return false;
        }
        ClientPlayerEntity clientPlayerEntity = null;
        Iterator it = minecraftClient.world.getPlayers().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ClientPlayerEntity clientPlayerEntity2 = (ClientPlayerEntity)((PlayerEntity) it.next());
            if (((PlayerEntity) clientPlayerEntity2).distanceTo(minecraftClient.player) <= Float.intBitsToFloat(1094713344) && !ClientServices.socialManager.isFriendEntity((PlayerEntity) clientPlayerEntity2) && clientPlayerEntity2 != minecraftClient.player) {
                clientPlayerEntity = clientPlayerEntity2;
                break;
            }
        }
        return clientPlayerEntity != null && minecraftClient.player.age <= 50;
    }

    public boolean is2088() {
        return (this.hunger.getValue().intValue() == -1 || minecraftClient.player.getHungerManager().getFoodLevel() > this.hunger.getValue().intValue()) && (this.health.getValue().intValue() == 0 || EntityUtil.getPlayerHealth() > ((float) this.health.getValue().intValue()));
    }

    public void setEating(boolean z) {
        if (this.flag == z) {
            return;
        }
        this.flag = z;
    }

    public void do438(int i) {
        minecraftClient.interactionManager.clickSlot(minecraftClient.player.currentScreenHandler.syncId, i, minecraftClient.player.getInventory().selectedSlot, SlotActionType.SWAP, minecraftClient.player);
    }
}
