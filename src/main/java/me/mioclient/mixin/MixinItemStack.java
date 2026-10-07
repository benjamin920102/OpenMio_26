package me.mioclient.mixin;

import me.mioclient.manager.ItemListConfig;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.player.FinishUsingEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})

public abstract class MixinItemStack {
    @Shadow
    public abstract Item getItem();

    @Inject(method = {"finishUsingItem"}, at = {@At("HEAD")})
    private void finishUsing(Level world, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if (livingEntity instanceof LocalPlayer) {
            ClientAccessor.eventBus.fireEvent(new FinishUsingEvent((ItemStack)(Object) this));
        }
    }

    @Inject(method = {"hasFoil"}, at = {@At("HEAD")}, cancellable = true)
    private void hasGlintHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (ItemListConfig.isConfiguredItem(getItem())) {
            callbackInfoReturnable.setReturnValue(true);
            callbackInfoReturnable.cancel();
        }
    }
}
