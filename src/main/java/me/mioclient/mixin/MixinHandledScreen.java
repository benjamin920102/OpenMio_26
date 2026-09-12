package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.widget.ChestSearchOverlay;
import me.mioclient.render.tooltip.ShulkerContentsTooltip;
import me.mioclient.util.client.InputState;
import me.mioclient.util.client.TooltipComponentCapture;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.gui.widget.ChestSearchOverlay;
import me.mioclient.render.tooltip.ShulkerContentsTooltip;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.client.InputState;
import me.mioclient.util.client.TooltipComponentCapture;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Optional;
import me.mioclient.event.events.screen.RenderWithTooltipEvent;
import me.mioclient.event.events.screen.ScreenDrawEvent;
import me.mioclient.mixin.ducks.DuckDrawContext;
import me.mioclient.module.misc.ChestSearchBar;
import me.mioclient.module.render.Tooltips;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({HandledScreen.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinHandledScreen.class */
public class MixinHandledScreen implements ClientAccessor {
    private static ChestSearchBar chestsearchbar;

    private static ChestSearchBar getChestsearchbar() {

        if (chestsearchbar == null && ClientServices.moduleInjector != null) {

            try { chestsearchbar = (ChestSearchBar) ClientServices.moduleInjector.getModule(ChestSearchBar.class); } catch (Exception e) {}

        }

        return chestsearchbar;

    }
    private static Tooltips tooltips;

    private static Tooltips getTooltips() {

        if (tooltips == null && ClientServices.moduleInjector != null) {

            try { tooltips = (Tooltips) ClientServices.moduleInjector.getModule(Tooltips.class); } catch (Exception e) {}

        }

        return tooltips;

    }

    @Shadow
    @Nullable
    protected Slot field_2787;

    @Unique
    private int lastMX;

    @Unique
    private int lastMY;

    @Unique
    private int mio$lastTooltipX;

    @Unique
    private int mio$lastTooltipY;

    @Unique
    private Slot mio$lastSlot;

    @Unique
    private MapState mio$cachedState;

    @Unique
    private int mio$cachedId;

    @Unique
    private final ScreenDrawEvent event = new ScreenDrawEvent((HandledScreen)(Object) this);

    @Inject(method = {"keyPressed"}, at = {@At("HEAD")}, cancellable = true)
    public void keyPressed(int i, int i2, int i3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        ChestSearchBar chestsearchbar_lcl = getChestsearchbar();

        if (chestsearchbar_lcl == null) return;

        if (chestsearchbar != null && chestsearchbar_lcl.isToggled() && ChestSearchOverlay.textFieldWidget != null && ChestSearchOverlay.textFieldWidget.isFocused() && minecraftClient.options.inventoryKey.matchesKey(i, i2)) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void tick(CallbackInfo callbackInfo) {
        this.event.runCallback();
    }

    @Inject(method = {"drawMouseoverTooltip"}, at = {@At("HEAD")})
    private void drawMouseoverTooltipHook(DrawContext drawContext, int i, int i2, CallbackInfo callbackInfo) {
        this.event.setDrawContext(drawContext);
        this.event.setX(i);
        this.event.setY(i2);
        eventBus.fireEvent(this.event);
    }

    @Inject(method = {"render"}, at = {@At("HEAD")})
    private void render(DrawContext drawContext, int i, int i2, float f, CallbackInfo callbackInfo) {
        if (!InputState.isKeyboardKeyDown(342)) {
            this.mio$lastSlot = this.field_2787;
        }
        this.lastMX = i;
        this.lastMY = i2;
    }

    @Inject(method = {"drawMouseoverTooltip"}, at = {@At("HEAD")})
    private void renderWithTooltipHook(DrawContext drawContext, int i, int i2, CallbackInfo callbackInfo) {
        Tooltips tooltips_lcl = getTooltips();

        if (tooltips_lcl == null) return;

        if (tooltips_lcl.isToggled() && this.mio$lastSlot != null && InventoryUtils.isShulkerBox(this.mio$lastSlot.getStack().getItem()) && InputState.isKeyboardKeyDown(342)) {
            this.field_2787 = this.mio$lastSlot;
        }
    }

    @Redirect(method = {"drawMouseoverTooltip"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void renderWithTooltip(DrawContext drawContext, TextRenderer textRenderer, List<Text> list, Optional<TooltipData> optional, int i, int i2) {
        if ((!InputState.isKeyboardKeyDown(342)) || this.mio$lastSlot == null || !InventoryUtils.isShulkerBox(this.mio$lastSlot.getStack().getItem())) {
            this.mio$lastTooltipX = i;
            this.mio$lastTooltipY = i2;
        } else {
            i = this.mio$lastTooltipX;
            i2 = this.mio$lastTooltipY;
        }
        RenderWithTooltipEvent renderWithTooltipEvent = new RenderWithTooltipEvent((Screen)(Object) this, TooltipComponentCapture.captureTooltipComponents(drawContext, list, optional), i, i2, this.lastMX, this.lastMY);
        ClientAccessor.eventBus.fireEvent(renderWithTooltipEvent);
        if (renderWithTooltipEvent.isCancelled()) {
            return;
        }
        ((DuckDrawContext) drawContext).drawTooltipsHook(textRenderer, renderWithTooltipEvent.getComponents(), i, i2, HoveredTooltipPositioner.INSTANCE);
    }

    @Inject(method = {"drawSlot"}, at = {@At("HEAD")})
    private void drawSlot(DrawContext drawContext, Slot slot, CallbackInfo callbackInfo) {
        int id;
        this.mio$cachedState = null;
        ItemStack stack = slot.getStack();
        if (stack.isOf(Items.FILLED_MAP) && (id = ((MapIdComponent) stack.getOrDefault(DataComponentTypes.MAP_ID, new MapIdComponent(-1))).id()) != -1) {
            this.mio$cachedId = id;
            this.mio$cachedState = ClientServices.tooltipsSearchHelper4.lookupMapState(stack, id);
        }
    }

    @WrapWithCondition(method = {"drawSlot"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/item/ItemStack;III)V")})
    private boolean drawSlotHook(DrawContext drawContext, ItemStack itemStack, int i, int i2, int i3) {
        Tooltips tooltips_lcl = getTooltips();

        if (tooltips_lcl == null) return true;

        return (tooltips_lcl.isToggled() && tooltips_lcl.mapOverlay.getValue().booleanValue() && this.mio$cachedState != null) ? false : true;
    }

    @WrapWithCondition(method = {"drawSlot"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")})
    private boolean drawSlotHook(DrawContext drawContext, TextRenderer textRenderer, ItemStack itemStack, int i, int i2, String str, @Local(argsOnly = true) Slot slot) {
        if (!getTooltips().isToggled() || !getTooltips().mapOverlay.getValue().booleanValue() || this.mio$cachedState == null) {
            return true;
        }
        ShulkerContentsTooltip.renderMapPreview(drawContext.getMatrices(), this.mio$cachedState, this.mio$cachedId, i, i2);
        if (InputState.isKeyboardKeyDown(342)) {
            return false;
        }
        drawContext.drawItemInSlot(textRenderer, itemStack, i, i2);
        return false;
    }
}
