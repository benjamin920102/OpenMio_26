package me.mioclient.mixin;

import me.mioclient.manager.ItemListConfig;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.player.FinishUsingEvent;
import me.mioclient.manager.ItemListConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ItemStack.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinItemStack.class */
public abstract class MixinItemStack {
    @Shadow
    public abstract Item method_7909();

    @Inject(method = {"finishUsing"}, at = {@At("HEAD")})
    private void finishUsing(World world, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> callbackInfoReturnable) {
        if (livingEntity instanceof ClientPlayerEntity) {
            ClientAccessor.eventBus.fireEvent(new FinishUsingEvent((ItemStack)(Object) this));
        }
    }

    @Inject(method = {"hasGlint"}, at = {@At("HEAD")}, cancellable = true)
    private void hasGlintHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (ItemListConfig.isConfiguredItem(method_7909())) {
            callbackInfoReturnable.setReturnValue(true);
            callbackInfoReturnable.cancel();
        }
    }
}
