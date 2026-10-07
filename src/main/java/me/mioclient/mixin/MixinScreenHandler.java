package me.mioclient.mixin;

import me.mioclient.api.ClientAccessor;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.mioclient.event.events.interaction.InsertItemEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AbstractContainerMenu.class})

public class MixinScreenHandler {
    @WrapWithCondition(method = {"quickMoveStack"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setChanged()V")}, require = 0)
    private boolean insertItemHook(Slot slot) {
        ClientAccessor.eventBus.fireEvent(new InsertItemEvent(slot));
        return true;
    }
}
