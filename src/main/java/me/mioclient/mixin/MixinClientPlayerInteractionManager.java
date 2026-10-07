package me.mioclient.mixin;

import me.mioclient.event.events.interaction.InteractItemPacketEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.mioclient.ducks.DuckClientPlayerInteractionManager;
import me.mioclient.event.events.interaction.AttackBlockEvent;
import me.mioclient.event.events.interaction.BreakBlockEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.events.interaction.InteractItemEvent;
import me.mioclient.event.events.player.StopUsingItemEvent;
import me.mioclient.module.combat.AutoClicker;
import me.mioclient.module.player.InventoryTweaks;
import me.mioclient.module.player.NoInteract;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* 26.2 port (verified against 26.2 merged jar): breakBlock->destroyBlock,
   attackBlock->startDestroyBlock, clickSlot->handleContainerInput (ClickType is now
   ContainerInput enum), interactBlock->useItemOn, stopUsingItem->releaseUsingItem,
   interactItem->useItem, attackEntity->attack, cancelBlockBreaking->stopDestroyBlock,
   updateBlockBreakingProgress->continueDestroyBlock, useItem builds
   ServerboundUseItemPacket(hand, sequence, yRot, xRot). Sneak spoof now sends
   ServerboundPlayerInputPacket (PRESS_SHIFT_KEY action is gone). Slot.getStack is
   getItem; currentScreenHandler is containerMenu; playerScreenHandler is inventoryMenu. */
@Mixin({MultiPlayerGameMode.class})
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
    private boolean isDestroying;

    @Shadow
    private float destroyProgress;

    @Shadow
    private BlockPos destroyBlockPos;

    @Shadow
    private int destroyDelay;

    @Shadow
    public abstract void handleContainerInput(int i, int i2, int i3, ContainerInput containerInput, Player playerEntity);

    @Inject(method = {"destroyBlock"}, at = {@At("HEAD")}, cancellable = true)
    private void breakBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        BreakBlockEvent breakBlockEvent = new BreakBlockEvent(blockPos);
        eventBus.fireEvent(breakBlockEvent);
        if (breakBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"startDestroyBlock"}, at = {@At("HEAD")}, cancellable = true)
    private void attackBlockHook(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        AttackBlockEvent attackBlockEvent = new AttackBlockEvent(blockPos, direction);
        eventBus.fireEvent(attackBlockEvent);
        if (attackBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(true);
            callbackInfoReturnable.cancel();
        }
    }

    @Inject(method = {"handleContainerInput"}, at = {@At("HEAD")}, cancellable = true)
    public void onClickArmorSlot(int i, int i2, int i3, ContainerInput containerInput, Player playerEntity, CallbackInfo callbackInfo) {
        InventoryTweaks inventorytweaks_lcl = getInventorytweaks();

        if (inventorytweaks_lcl == null) return;

        if (inventorytweaks_lcl.isToggled()) {
            AbstractContainerMenu screenHandler = playerEntity.containerMenu;
            if (inventorytweaks_lcl.fastArmor.getValue().booleanValue() && i3 == 1 && containerInput == ContainerInput.QUICK_MOVE) {
                Slot slot = screenHandler.getSlot(i2);
                if (minecraftClient.player.getEquipmentSlotForItem(slot.getItem()).isArmor() || slot.getItem().getItem() == Items.ELYTRA) {
                    InventoryUtils.swapStacks(i2, 8 - minecraftClient.player.getEquipmentSlotForItem(slot.getItem()).getIndex());
                    callbackInfo.cancel();
                    return;
                }
            }
            if (inventorytweaks_lcl.xCarryTweaks.getValue().booleanValue() && i3 == 1 && containerInput == ContainerInput.QUICK_MOVE && i2 > 4 && (screenHandler instanceof InventoryMenu)) {
                for (int i4 = 0; i4 < 4; i4++) {
                    if (minecraftClient.player.inventoryMenu.getCraftSlots().getItems().get(i4).isEmpty()) {
                        handleContainerInput(i, i2, 0, ContainerInput.PICKUP, minecraftClient.player);
                        handleContainerInput(i, i4 + 1, 0, ContainerInput.PICKUP, minecraftClient.player);
                        callbackInfo.cancel();
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = {"useItemOn"}, at = {@At("HEAD")}, cancellable = true)
    private void interactBlockHook(LocalPlayer clientPlayerEntity, InteractionHand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        NoInteract nointeract_lcl = getNointeract();

        if (nointeract_lcl == null) return;

        InteractBlockEvent interactBlockEvent = new InteractBlockEvent(blockHitResult, hand);
        eventBus.fireEvent(interactBlockEvent);
        if (interactBlockEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(InteractionResult.FAIL);
            callbackInfoReturnable.cancel();
        }
        InteractionHand hand1841 = NoInteract.getUsableHand();
        if (nointeract_lcl.shouldBlockInteraction(minecraftClient.player.getItemInHand(NoInteract.getUsableHand()), blockHitResult.getBlockPos())) {
            if (nointeract_lcl.mode.getValue() == NoInteract.Mode.SHIFT) {
                nointeract_lcl.flag = !ClientServices.holeSnapRenderHelper.isSneaking();
                sendShiftSpoof(true);
            } else {
                callbackInfoReturnable.setReturnValue(InteractionResult.FAIL);
                callbackInfoReturnable.cancel();
            }
            minecraftClient.gameMode.useItem(minecraftClient.player, hand1841);
        }
    }

    @Inject(method = {"useItemOn"}, at = {@At("RETURN")})
    private void releaseShiftKey(LocalPlayer clientPlayerEntity, InteractionHand hand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        NoInteract nointeract_lcl = getNointeract();

        if (nointeract_lcl == null) return;

        if (nointeract_lcl.flag) {
            sendShiftSpoof(false);
            nointeract_lcl.flag = false;
        }
    }

    /** 26.2 has no PRESS_SHIFT_KEY packet action; sneak is spoofed via the input packet. */
    private static void sendShiftSpoof(boolean shift) {
        Input presses = minecraftClient.player.input.keyPresses;
        minecraftClient.player.connection.send(new ServerboundPlayerInputPacket(new Input(presses.forward(), presses.backward(), presses.left(), presses.right(), presses.jump(), shift, presses.sprint())));
    }

    @Inject(method = {"releaseUsingItem"}, at = {@At("HEAD")}, cancellable = true)
    private void stopUsingItemHook(Player playerEntity, CallbackInfo callbackInfo) {
        StopUsingItemEvent stopUsingItemEvent = new StopUsingItemEvent();
        eventBus.fireEvent(stopUsingItemEvent);
        if (stopUsingItemEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"useItem"}, at = {@At("HEAD")}, cancellable = true)
    private void interactItemHook(Player playerEntity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        InteractItemEvent interactItemEvent = new InteractItemEvent(hand);
        eventBus.fireEvent(interactItemEvent);
        if (interactItemEvent.isCancelled()) {
            callbackInfoReturnable.cancel();
            callbackInfoReturnable.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = {"attack"}, at = {@At("HEAD")}, cancellable = true)
    private void attackEntityHook(Player playerEntity, Entity entity, CallbackInfo callbackInfo) {
        AutoClicker autoclicker_lcl = getAutoclicker();

        if (autoclicker_lcl == null) return;

        if (autoclicker_lcl.is1073(entity) && playerEntity == minecraftClient.player) {
            callbackInfo.cancel();
        }
    }

    @WrapWithCondition(method = {"stopDestroyBlock"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")})
    private boolean cancelBlockBreaking(ClientPacketListener clientPlayNetworkHandler, Packet packet) {
        SpeedMine speedmine_lcl = getSpeedmine();

        if (speedmine_lcl == null) return true;

        return !speedmine_lcl.isToggled();
    }

    @Inject(method = {"continueDestroyBlock"}, at = {@At("HEAD")})
    private void attackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        SpeedMine speedmine_lcl = getSpeedmine();

        if (speedmine_lcl == null) return;

        if (speedmine_lcl.isToggled()) {
            this.destroyDelay = 0;
        }
    }

    /* 26.2 port: useItem delegates to a prediction lambda; the
       ServerboundUseItemPacket NEW now lives in lambda$useItem$0
       (bytecode-verified, same ctor). */
    @ModifyExpressionValue(method = {"lambda$useItem$0"}, at = {@At(value = "NEW", target = "(Lnet/minecraft/world/InteractionHand;IFF)Lnet/minecraft/network/protocol/game/ServerboundUseItemPacket;")})
    private ServerboundUseItemPacket interactItemHook(ServerboundUseItemPacket playerInteractItemC2SPacket) {
        InteractItemPacketEvent interactItemEvent_2 = new InteractItemPacketEvent(playerInteractItemC2SPacket);
        eventBus.fireEvent(interactItemEvent_2);
        return interactItemEvent_2.isCancelled() ? interactItemEvent_2.getInteractPacket() : playerInteractItemC2SPacket;
    }

    @Override
    public boolean isBreakingBlock() {
        return this.isDestroying;
    }

    @Override
    public void setBreakingBlock(boolean z) {
        this.isDestroying = z;
    }

    @Override
    public float getBreakingProgress() {
        return this.destroyProgress;
    }

    @Override
    public void setBreakingProgress(float f) {
        this.destroyProgress = f;
    }

    @Override
    public BlockPos getCurrentBreakingBlock() {
        return this.destroyBlockPos;
    }

    @Override
    public void setCurrentBreakingBlock(BlockPos blockPos) {
        this.destroyBlockPos = blockPos;
    }
}
