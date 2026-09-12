package me.mioclient.mixin;

import me.mioclient.event.events.interaction.InteractItemPacketEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.mioclient.ducks.DuckClientPlayerInteractionManager;
import me.mioclient.event.events.interaction.AttackBlockEvent;
import me.mioclient.event.events.interaction.BreakBlockEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.events.interaction.InteractItemEvent;
import me.mioclient.event.events.interaction.InteractItemPacketEvent;
import me.mioclient.event.events.player.StopUsingItemEvent;
import me.mioclient.module.combat.AutoClicker;
import me.mioclient.module.player.InventoryTweaks;
import me.mioclient.module.player.NoInteract;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ClientPlayerInteractionManager.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinClientPlayerInteractionManager.class */
public abstract class MixinClientPlayerInteractionManager implements ClientAccessor, DuckClientPlayerInteractionManager {
    private static AutoClicker autoclicker;

    private static AutoClicker getAutoclicker() {

        if (autoclicker == null && ClientServices.moduleInjector != null) {

            try { autoclicker = (AutoClicker) ClientServices.moduleInjector.getModule(AutoClicker.class); } catch (Exception e) {}

        }

        return autoclicker;

    }
    private static NoInteract nointeract;

    private static NoInteract getNointeract() {

        if (nointeract == null && ClientServices.moduleInjector != null) {

            try { nointeract = (NoInteract) ClientServices.moduleInjector.getModule(NoInteract.class); } catch (Exception e) {}

        }

        return nointeract;

    }
    private static InventoryTweaks inventorytweaks;

    private static InventoryTweaks getInventorytweaks() {

        if (inventorytweaks == null && ClientServices.moduleInjector != null) {

            try { inventorytweaks = (InventoryTweaks) ClientServices.moduleInjector.getModule(InventoryTweaks.class); } catch (Exception e) {}

        }

        return inventorytweaks;

    }
    private static SpeedMine speedmine;

    private static SpeedMine getSpeedmine() {

        if (speedmine == null && ClientServices.moduleInjector != null) {

            try { speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class); } catch (Exception e) {}

        }

        return speedmine;

    }

    @Shadow
    private boolean field_3717;

    @Shadow
    private float field_3715;

    @Shadow
    private BlockPos field_3714;

    @Shadow
    private int field_3716;

    @Shadow
    public abstract void method_2906(int i, int i2, int i3, SlotActionType slotActionType, PlayerEntity playerEntity);

    @Inject(method = {"breakBlock"}, at = {@At("HEAD")}, cancellable = true)
    private void breakBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BreakBlockEvent breakBlockEvent = new BreakBlockEvent(blockPos);
        eventBus.fireEvent(breakBlockEvent);
        if (breakBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"attackBlock"}, at = {@At("HEAD")}, cancellable = true)
    private void attackBlockHook(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        AttackBlockEvent attackBlockEvent = new AttackBlockEvent(blockPos, direction);
        eventBus.fireEvent(attackBlockEvent);
        if (attackBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(true);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"clickSlot"}, at = {@At("HEAD")}, cancellable = true)
    public void onClickArmorSlot(int i, int i2, int i3, SlotActionType slotActionType, PlayerEntity playerEntity, CallbackInfo callbackInfo) {
        InventoryTweaks inventorytweaks_lcl = getInventorytweaks();

        if (inventorytweaks_lcl == null) return;

        if (inventorytweaks_lcl.isToggled()) {
            ScreenHandler screenHandler = playerEntity.currentScreenHandler;
            if (inventorytweaks_lcl.fastArmor.getValue().booleanValue() && i3 == 1 && slotActionType == SlotActionType.QUICK_MOVE) {
                Slot slot = screenHandler.getSlot(i2);
                if ((slot.getStack().getItem() instanceof ArmorItem) || slot.getStack().isOf(Items.ELYTRA)) {
                    InventoryUtils.swapStacks(i2, 8 - minecraftClient.player.getPreferredEquipmentSlot(slot.getStack()).getEntitySlotId());
                    callbackInfo.cancel();
                    return;
                }
            }
            if (inventorytweaks_lcl.xCarryTweaks.getValue().booleanValue() && i3 == 1 && slotActionType == SlotActionType.QUICK_MOVE && i2 > 4 && (screenHandler instanceof PlayerScreenHandler)) {
                for (int i4 = 0; i4 < 4; i4++) {
                    if (((ItemStack) minecraftClient.player.playerScreenHandler.getCraftingInput().getHeldStacks().get(i4)).isEmpty()) {
                        method_2906(i, i2, 0, SlotActionType.PICKUP, minecraftClient.player);
                        method_2906(i, i4 + 1, 0, SlotActionType.PICKUP, minecraftClient.player);
                        callbackInfo.cancel();
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = {"interactBlock"}, at = {@At("HEAD")}, cancellable = true)
    private void interactBlockHook(ClientPlayerEntity clientPlayerEntity, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
        NoInteract nointeract_lcl = getNointeract();

        if (nointeract_lcl == null) return;

        InteractBlockEvent interactBlockEvent = new InteractBlockEvent(blockHitResult, hand);
        eventBus.fireEvent(interactBlockEvent);
        if (interactBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(ActionResult.FAIL);
            callbackInfoReturnable.cancel();
        }
        Hand hand1841 = NoInteract.getUsableHand();
        if (nointeract_lcl.shouldBlockInteraction(minecraftClient.player.getStackInHand(NoInteract.getUsableHand()), blockHitResult.getBlockPos())) {
            if (nointeract_lcl.mode.getValue() == NoInteract.Mode.SHIFT) {
                nointeract_lcl.flag = !ClientServices.holeSnapRenderHelper.isSneaking();
                PacketSender.sendClientCommand(clientPlayerEntity, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY, 0);
            } else {
                callbackInfoReturnable.setReturnValue(ActionResult.FAIL);
                callbackInfoReturnable.cancel();
            }
            minecraftClient.interactionManager.interactItem(minecraftClient.player, hand1841);
        }
    }

    @Inject(method = {"interactBlock"}, at = {@At("RETURN")})
    private void releaseShiftKey(ClientPlayerEntity clientPlayerEntity, Hand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
        NoInteract nointeract_lcl = getNointeract();

        if (nointeract_lcl == null) return;

        if (nointeract_lcl.flag) {
            PacketSender.sendClientCommand(clientPlayerEntity, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY, 0);
            nointeract_lcl.flag = false;
        }
    }

    @Inject(method = {"stopUsingItem"}, at = {@At("HEAD")}, cancellable = true)
    private void stopUsingItemHook(PlayerEntity playerEntity, CallbackInfo callbackInfo) {
        StopUsingItemEvent stopUsingItemEvent = new StopUsingItemEvent();
        eventBus.fireEvent(stopUsingItemEvent);
        if (stopUsingItemEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"interactItem"}, at = {@At("HEAD")}, cancellable = true)
    private void interactItemHook(PlayerEntity playerEntity, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
        InteractItemEvent interactItemEvent = new InteractItemEvent(hand);
        eventBus.fireEvent(interactItemEvent);
        if (interactItemEvent.isCancelled()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = {"attackEntity"}, at = {@At("HEAD")}, cancellable = true)
    private void attackEntityHook(PlayerEntity playerEntity, Entity entity, CallbackInfo callbackInfo) {
        AutoClicker autoclicker_lcl = getAutoclicker();

        if (autoclicker_lcl == null) return;

        if (autoclicker_lcl.is1073(entity) && playerEntity == minecraftClient.player) {
            callbackInfo.cancel();
        }
    }

    @WrapWithCondition(method = {"cancelBlockBreaking"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V")})
    private boolean cancelBlockBreaking(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet packet) {
        SpeedMine speedmine_lcl = getSpeedmine();

        if (speedmine_lcl == null) return true;

        return !speedmine_lcl.isToggled();
    }

    @Inject(method = {"updateBlockBreakingProgress"}, at = {@At("HEAD")})
    private void attackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        SpeedMine speedmine_lcl = getSpeedmine();

        if (speedmine_lcl == null) return;

        if (speedmine_lcl.isToggled()) {
            this.field_3716 = 0;
        }
    }

    @ModifyExpressionValue(method = {"method_41929"}, at = {@At(value = "NEW", target = "(Lnet/minecraft/util/Hand;IFF)Lnet/minecraft/network/packet/c2s/play/PlayerInteractItemC2SPacket;")})
    private PlayerInteractItemC2SPacket interactItemHook(PlayerInteractItemC2SPacket playerInteractItemC2SPacket) {
        InteractItemPacketEvent interactItemEvent_2 = new InteractItemPacketEvent(playerInteractItemC2SPacket);
        eventBus.fireEvent(interactItemEvent_2);
        return interactItemEvent_2.isCancelled() ? interactItemEvent_2.getInteractPacket() : playerInteractItemC2SPacket;
    }

    @Override // me.mioclient.ducks.DuckClientPlayerInteractionManager
    public boolean isBreakingBlock() {
        return this.field_3717;
    }

    @Override // me.mioclient.ducks.DuckClientPlayerInteractionManager
    public void setBreakingBlock(boolean z) {
        this.field_3717 = z;
    }

    @Override // me.mioclient.ducks.DuckClientPlayerInteractionManager
    public float getBreakingProgress() {
        return this.field_3715;
    }

    @Override // me.mioclient.ducks.DuckClientPlayerInteractionManager
    public void setBreakingProgress(float f) {
        this.field_3715 = f;
    }

    @Override // me.mioclient.ducks.DuckClientPlayerInteractionManager
    public BlockPos getCurrentBreakingBlock() {
        return this.field_3714;
    }

    @Override // me.mioclient.ducks.DuckClientPlayerInteractionManager
    public void setCurrentBreakingBlock(BlockPos blockPos) {
        this.field_3714 = blockPos;
    }
}
