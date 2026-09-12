package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.module.render.tooltips.ContainerPreview;
import me.mioclient.util.client.TooltipComponentCapture;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.tooltips.ContainerPreview;
import me.mioclient.util.client.TooltipComponentCapture;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Optional;
import me.mioclient.module.render.Tooltips;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin({DrawContext.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinDrawContext.class */
public abstract class MixinDrawContext {

    @Unique
    private static Tooltips tooltips;

    private static Tooltips getTooltips() {
        if (tooltips == null && ClientServices.moduleInjector != null) {
            try { tooltips = (Tooltips) ClientServices.moduleInjector.getModule(Tooltips.class); } catch (Exception e) {}
        }
        return tooltips;
    }

    @Unique
    private boolean translate = false;

    @Shadow
    public abstract void method_51427(ItemStack itemStack, int i, int i2);

    @Shadow
    public abstract MatrixStack method_51448();

    @Inject(at = {@At(value = "INVOKE", target = "net/minecraft/item/ItemStack.isItemBarVisible()Z")}, method = {"drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"})
    private void drawItemInSlotHook(TextRenderer textRenderer, ItemStack itemStack, int i, int i2, @Nullable String str, CallbackInfo callbackInfo) {
        Tooltips tt = getTooltips();
        if (tt != null && tt.isToggled() && tt.majorityItem.getValue().booleanValue()) {
            for (ContainerPreview tooltipsData : tt.list) {
                if (tooltipsData.getItemStack2242() == itemStack && tooltipsData.getSecondaryItemStack() != null) {
                    this.translate = true;
                    method_51427(tooltipsData.getSecondaryItemStack(), i, i2);
                    this.translate = false;
                }
            }
        }
    }

    @ModifyArgs(method = {"drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V"}, at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.translate(FFF)V"))
    private void drawItemHook(Args args) {
        if (this.translate) {
            args.set(0, Float.valueOf(((Float) args.get(0)).floatValue() + 3.5f));
            args.set(1, Float.valueOf(((Float) args.get(1)).floatValue() - 4.0f));
            args.set(2, Float.valueOf(((Float) args.get(2)).floatValue() + 100.0f));
        }
    }

    @ModifyArgs(method = {"drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V"}, at = @At(value = "INVOKE", target = "net/minecraft/client/util/math/MatrixStack.scale(FFF)V"))
    private void modifyItemScale(Args args) {
        if (this.translate) {
            args.set(0, Float.valueOf(10.0f));
            args.set(1, Float.valueOf(-10.0f));
        }
    }

    @Inject(method = {"drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", shift = At.Shift.BEFORE)}, cancellable = true)
    private void drawTooltipHook(TextRenderer textRenderer, List<Text> list, Optional<TooltipData> optional, int i, int i2, CallbackInfo callbackInfo, @Local(ordinal = 1) List<TooltipComponent> list2) {
        if (TooltipComponentCapture.is1129()) {
            TooltipComponentCapture.setComponents(list2);
            callbackInfo.cancel();
        }
    }
}
