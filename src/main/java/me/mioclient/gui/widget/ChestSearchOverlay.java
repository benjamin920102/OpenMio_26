package me.mioclient.gui.widget;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.ColorUtils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.awt.Color;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;
import me.mioclient.mixin.ducks.DuckHandledScreen;
import me.mioclient.module.misc.ChestSearchBar;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import org.joml.Matrix3x2fStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class ChestSearchOverlay {
    public static EditBox textFieldWidget;
    public static final ChestSearchOverlay mixinTextFieldWidgetHelper = new ChestSearchOverlay();
    public static ChestSearchBar chestSearchBar = (ChestSearchBar) ClientServices.moduleInjector.getModule(ChestSearchBar.class);
    public static String text = "";
    public static int num = 0;

    
    public static class AfterInit implements ScreenEvents.AfterInit {
        public void afterInit(Minecraft minecraftClient, Screen screen, int i, int i2) {
            if (!ChestSearchOverlay.chestSearchBar.isToggled()) {
                ChestSearchOverlay.textFieldWidget = null;
                return;
            }
            if (!(screen instanceof ContainerScreen) && !(screen instanceof ShulkerBoxScreen) && !(screen instanceof HopperScreen)) {
                ChestSearchOverlay.textFieldWidget = null;
                return;
            }
            DuckHandledScreen duckHandledScreen = (DuckHandledScreen)((AbstractContainerScreen) screen);
            ChestSearchOverlay.textFieldWidget = new EditBox(Minecraft.getInstance().font, duckHandledScreen.getX() + 81, duckHandledScreen.getY() + 5, 88, 10, Component.literal(""));
            ChestSearchOverlay.textFieldWidget.setValue(ChestSearchOverlay.text);
            ChestSearchOverlay.textFieldWidget.setFocused(false);
            ChestSearchOverlay.textFieldWidget.setMaxLength(32);
            ChestSearchOverlay.textFieldWidget.setBordered(false);
            ChestSearchOverlay.textFieldWidget.setResponder(str -> {
                ChestSearchOverlay.text = str;
            });
            Screens.getWidgets(screen).add(ChestSearchOverlay.textFieldWidget);
        }
    }

    
    
    public interface BiPredicate extends java.util.function.BiPredicate<String, String> {
    }

    public void init() {
        textFieldWidget = null;
        text = "";
        ScreenEvents.AFTER_INIT.register(new AfterInit());
    }

    public void onDrawGui(GuiGraphicsExtractor drawContext, Screen screen, Matrix3x2fStack matrixStack, float f, float f2, float f3) {
        if (!chestSearchBar.isToggled() || textFieldWidget == null) {
            return;
        }
        matrixStack.pushMatrix();
        matrixStack.translate(0.0f, 0.0f);
        DuckHandledScreen duckHandledScreen = (DuckHandledScreen) screen;
        if (!textFieldWidget.getValue().isBlank() && (screen instanceof AbstractContainerScreen)) {
            AbstractContainerMenu screenHandler = ((AbstractContainerScreen) screen).getMenu();
            int x = duckHandledScreen.getX();
            int y = duckHandledScreen.getY();
            num = 0;
            Iterator it = screenHandler.slots.iterator();
            while (it.hasNext()) {
                Slot slot = (Slot) it.next();
                int i = x + slot.x;
                int i2 = y + slot.y;
                ItemStack stack = slot.getItem();
                if (namesMatch(stack, textFieldWidget.getValue())) {
                    num++;
                } else {
                    drawContext.fill(i, i2, i + 16, i2 + 16, -1442840576);
                }
                if (isFull(stack, textFieldWidget.getValue())) {
                    drawContext.fill(i, i2, i + 16, i2 + 16, ColorUtils.toIntWithAlpha(Color.green, 100));
                }
            }
        }
        if (num != 0 || textFieldWidget.getValue().isEmpty()) {
            textFieldWidget.setTextColor(16777215);
        } else {
            textFieldWidget.setTextColor(16733525);
        }
        matrixStack.popMatrix();
    }

    public static boolean namesMatch(ItemStack itemStack, String str) {
        ItemContainerContents containerComponent;
        String strip = ChatFormatting.stripFormatting(str.trim().toLowerCase(Locale.ROOT));
        if (strip == null || strip.isEmpty()) {
            return true;
        }
        if (itemStack.isEmpty()) {
            return false;
        }
        Item item = itemStack.getItem();
        if (item.getDescriptionId().contains("shulker_box") && (containerComponent = (ItemContainerContents) itemStack.get(DataComponents.CONTAINER)) != null) {
            Iterator it = containerComponent.nonEmptyItemCopyStream().iterator();
            while (it.hasNext()) {
                if (namesMatch((ItemStack) it.next(), strip)) {
                    return true;
                }
            }
        }
        String strip2 = ChatFormatting.stripFormatting(itemStack.getHoverName().getString().trim().toLowerCase(Locale.ROOT));
        BiPredicate biPredicate = (v0, v1) -> {
            return v0.contains(v1);
        };
        if (strip.length() >= 3 && strip.startsWith("\"") && strip.endsWith("\"")) {
            strip = strip.substring(1, strip.length() - 1);
            biPredicate = (v0, v1) -> {
                return v0.equals(v1);
            };
        }
        if (strip.length() >= 3 && strip.startsWith("/") && strip.endsWith("/")) {
            strip = strip.substring(1, strip.length() - 1);
            biPredicate = (str2, str3) -> {
                return Pattern.compile(str3).matcher(str2).find();
            };
        }
        ItemEnchantments itemEnchantmentsComponent = (ItemEnchantments) itemStack.get(DataComponents.STORED_ENCHANTMENTS);
        if (itemEnchantmentsComponent != null) {
            Iterator it2 = itemEnchantmentsComponent.entrySet().iterator();
            while (it2.hasNext()) {
                if (testEnchant(biPredicate, strip, (Object2IntMap.Entry) it2.next())) {
                    return true;
                }
            }
        }
        if (itemStack.isEnchanted()) {
            Iterator it3 = itemStack.getEnchantments().entrySet().iterator();
            while (it3.hasNext()) {
                if (testEnchant(biPredicate, strip, (Object2IntMap.Entry) it3.next())) {
                    return true;
                }
            }
        }
        if (biPredicate.test(I18n.get(item.getDescriptionId()).toLowerCase(Locale.ROOT), strip)) {
            return true;
        }
        PotionContents potionContentsComponent = (PotionContents) itemStack.get(DataComponents.POTION_CONTENTS);
        if (potionContentsComponent != null) {
            Iterator it4 = potionContentsComponent.getAllEffects().iterator();
            while (it4.hasNext()) {
                if (biPredicate.test(I18n.get(((MobEffectInstance) it4.next()).getDescriptionId()).toLowerCase(Locale.ROOT), strip)) {
                    return true;
                }
            }
        }
        return biPredicate.test(strip2, strip);
    }

    public static boolean testEnchant(BiPredicate biPredicate, String str, Object2IntMap.Entry<Holder<Enchantment>> entry) {
        if (entry == null) {
            return false;
        }
        return biPredicate.test(Enchantment.getFullname(entry.getKey(), entry.getIntValue()).getString().toLowerCase(Locale.ROOT).replaceAll("enchantment", ""), str);
    }

    public static boolean isFull(ItemStack itemStack, String str) {
        ItemContainerContents containerComponent;
        if (!chestSearchBar.highlightFull.getValue().booleanValue()) {
            return false;
        }
        String strip = ChatFormatting.stripFormatting(str.trim().toLowerCase(Locale.ROOT));
        if (strip == null || strip.isEmpty() || itemStack.isEmpty() || !itemStack.getItem().getDescriptionId().contains("shulker_box") || (containerComponent = (ItemContainerContents) itemStack.get(DataComponents.CONTAINER)) == null || containerComponent.allItemsCopyStream().toList().size() != 27) {
            return false;
        }
        Iterator it = containerComponent.allItemsCopyStream().toList().iterator();
        while (it.hasNext()) {
            if (!namesMatch((ItemStack) it.next(), strip)) {
                return false;
            }
        }
        return true;
    }
}
