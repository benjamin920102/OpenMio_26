package me.mioclient.manager;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.network.PacketSendPostEvent;

import me.mioclient.event.events.client.InventorySyncEvent;
import me.mioclient.event.events.interaction.SlotChangeEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.network.PacketSender;

import me.mioclient.event.EventBus;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.network.UpdateSelectedSlotEvent;
import me.mioclient.module.exploit.XCarry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import me.mioclient.render.RenderConstants;
import me.mioclient.manager.RotationManager;

public final class InventorySyncTracker implements ClientAccessor {
    public static final XCarry xCarry = (XCarry) ClientServices.moduleInjector.getModule(XCarry.class);
    public volatile boolean flag;
    public volatile boolean flag2;
    public volatile boolean flag3;
    public boolean flag4;
    public final Map<Integer, Record> map = Collections.synchronizedMap(new HashMap());
    public final Stopwatch stopwatch = new Stopwatch();
    public int num = -1;

    
    
    public static final class Record {
        public final ClientboundContainerSetSlotPacket screenHandlerSlotUpdateS2CPacket;
        public final long num;

        public Record(ClientboundContainerSetSlotPacket screenHandlerSlotUpdateS2CPacket, long j) {
            this.screenHandlerSlotUpdateS2CPacket = screenHandlerSlotUpdateS2CPacket;
            this.num = j;
        }

        public ClientboundContainerSetSlotPacket getScreenHandlerSlotUpdateS2CPacket1571() {
            return this.screenHandlerSlotUpdateS2CPacket;
        }

        public long getExpiryTime() {
            return this.num;
        }
    }

    public InventorySyncTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        synchronized (this.map) {
            ArrayList arrayList = new ArrayList();
            this.map.forEach((num, record) -> {
                if (System.currentTimeMillis() < record.getExpiryTime()) {
                    return;
                }
                arrayList.add(num);
            });
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Record remove = this.map.remove((Integer) it.next());
                minecraftClient.player.connection.handleContainerSetSlot(remove.getScreenHandlerSlotUpdateS2CPacket1571());
            }
        }
        this.flag4 = false;
        if (xCarry.isToggled() || (minecraftClient.gui.screen() instanceof AbstractContainerScreen) || !minecraftClient.player.containerMenu.getCarried().isEmpty() || !this.stopwatch.hasElapsedMs(50L) || !this.flag || System.currentTimeMillis() - ClientServices.holeSnapRenderHelper.getLastPacketTime() > 2500) {
            return;
        }
        close();
    }

    @Listen(get219= RenderConstants.num4)
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (sendImmediatelyEvent.isCancelled()) {
            return;
        }
        if (sendImmediatelyEvent.getPacket() instanceof ServerboundContainerClickPacket) {
            this.flag = true;
            this.stopwatch.reset();
            if (!this.flag2 || !this.flag3) {
                eventBus.fireEvent(new InventorySyncEvent(KeyPearlMode.Pre));
            }
        }
        if ((sendImmediatelyEvent.getPacket() instanceof ServerboundContainerClosePacket) || (sendImmediatelyEvent.getPacket() instanceof ClientboundDisconnectPacket)) {
            this.flag = false;
        }
        ServerboundSetCarriedItemPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof ServerboundSetCarriedItemPacket ? (ServerboundSetCarriedItemPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof ServerboundSetCarriedItemPacket) {
            ServerboundSetCarriedItemPacket updateSelectedSlotC2SPacket = packet904;
            if (updateSelectedSlotC2SPacket.getSlot() != this.num) {
                eventBus.fireEvent(new SlotChangeEvent(updateSelectedSlotC2SPacket.getSlot()));
                this.num = updateSelectedSlotC2SPacket.getSlot();
            } else if (RotationManager.is724()) {
                sendImmediatelyEvent.cancel();
            }
        }
        if (sendImmediatelyEvent.getPacket() instanceof ClientboundDisconnectPacket) {
            this.num = -1;
        }
    }

    @Listen(get219= RenderConstants.num2)
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof ClientboundContainerClosePacket) {
            this.flag = false;
        }
        if (channelRead0Event.getPacket() instanceof ClientboundContainerSetContentPacket) {
            this.map.clear();
        }
        ClientboundContainerSetSlotPacket packet904 = (channelRead0Event.getPacket()) instanceof ClientboundContainerSetSlotPacket ? (ClientboundContainerSetSlotPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof ClientboundContainerSetSlotPacket) {
            ClientboundContainerSetSlotPacket screenHandlerSlotUpdateS2CPacket = packet904;
            if (RotationManager.is724()) {
                if (screenHandlerSlotUpdateS2CPacket.getSlot() == 36 + minecraftClient.player.getInventory().getSelectedSlot() || screenHandlerSlotUpdateS2CPacket.getSlot() < 36 || screenHandlerSlotUpdateS2CPacket.getSlot() > 44) {
                    return;
                }
                if (!isItemInHotbar(screenHandlerSlotUpdateS2CPacket.getItem(), screenHandlerSlotUpdateS2CPacket.getSlot())) {
                    this.map.remove(Integer.valueOf(screenHandlerSlotUpdateS2CPacket.getSlot()));
                    return;
                } else {
                    this.map.put(Integer.valueOf(screenHandlerSlotUpdateS2CPacket.getSlot()), new Record(screenHandlerSlotUpdateS2CPacket, System.currentTimeMillis() + 500));
                    channelRead0Event.cancel();
                }
            }
        }
        ClientboundEntityEventPacket packet9042 = (channelRead0Event.getPacket()) instanceof ClientboundEntityEventPacket ? (ClientboundEntityEventPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof ClientboundEntityEventPacket) {
            ClientboundEntityEventPacket entityStatusS2CPacket = packet9042;
            if (entityStatusS2CPacket.getEventId() == 35 && entityStatusS2CPacket.getEntity(minecraftClient.level) == minecraftClient.player) {
                this.flag4 = true;
            }
        }
    }

    @Listen
    public void onPacketSendPost(PacketSendPostEvent sendInternalEvent) {
        if (sendInternalEvent.getPacket() instanceof ServerboundContainerClickPacket) {
            if (this.flag2 && this.flag3) {
                return;
            }
            eventBus.fireEvent(new InventorySyncEvent(KeyPearlMode.Post));
            this.flag3 = true;
        }
    }

    @Listen
    public void onUpdateSelectedSlot(UpdateSelectedSlotEvent updateSelectedSlotEvent) {
        if (minecraftClient.hasSingleplayerServer()) {
            return;
        }
        PacketSender.sendSelectedSlot(minecraftClient.player.getInventory().getSelectedSlot());
        updateSelectedSlotEvent.cancel();
    }

    public boolean isItemInHotbar(ItemStack itemStack, int i) {
        for (int i2 = 0; i2 < 9; i2++) {
            if (i - 36 != i2) {
                if (ItemStack.isSameItemSameComponents(minecraftClient.player.getInventory().getItem(i2), itemStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void close() {
        minecraftClient.player.connection.send(new ServerboundContainerClosePacket(minecraftClient.player.containerMenu.containerId));
        this.flag = false;
    }

    public void setSyncBatching(boolean z) {
        if (this.flag2 == z) {
            return;
        }
        this.flag2 = z;
        this.flag3 = false;
    }

    public boolean hasPendingTotemPop() {
        return this.flag4;
    }

    public int getTrackedSelectedSlot() {
        return this.num;
    }
}
