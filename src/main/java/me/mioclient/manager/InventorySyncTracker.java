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
import me.mioclient.api.ClientServices;
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
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.network.PacketSendPostEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.network.UpdateSelectedSlotEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.exploit.XCarry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import me.mioclient.event.events.client.InventorySyncEvent;
import me.mioclient.event.events.interaction.SlotChangeEvent;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.render.RenderConstants;
import me.mioclient.manager.RotationManager;
import me.mioclient.render.RenderConstants;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/InventorySyncTracker.class */
public final class InventorySyncTracker implements ClientAccessor {
    public static final XCarry xCarry = (XCarry) ClientServices.moduleInjector.getModule(XCarry.class);
    public volatile boolean flag;
    public volatile boolean flag2;
    public volatile boolean flag3;
    public boolean flag4;
    public final Map<Integer, Record> map = Collections.synchronizedMap(new HashMap());
    public final Stopwatch stopwatch = new Stopwatch();
    public int num = -1;

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/FireworksHelperSearchHelper4$Record.class */
    public static final class Record {
        public final ScreenHandlerSlotUpdateS2CPacket screenHandlerSlotUpdateS2CPacket;
        public final long num;

        public Record(ScreenHandlerSlotUpdateS2CPacket screenHandlerSlotUpdateS2CPacket, long j) {
            this.screenHandlerSlotUpdateS2CPacket = screenHandlerSlotUpdateS2CPacket;
            this.num = j;
        }




        public ScreenHandlerSlotUpdateS2CPacket getScreenHandlerSlotUpdateS2CPacket1571() {
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
                minecraftClient.player.networkHandler.onScreenHandlerSlotUpdate(remove.getScreenHandlerSlotUpdateS2CPacket1571());
            }
        }
        this.flag4 = false;
        if (xCarry.isToggled() || (minecraftClient.currentScreen instanceof HandledScreen) || !minecraftClient.player.currentScreenHandler.getCursorStack().isEmpty() || !this.stopwatch.hasElapsedMs(50L) || !this.flag || System.currentTimeMillis() - ClientServices.holeSnapRenderHelper.getLastPacketTime() > 2500) {
            return;
        }
        close();
    }

    @Listen(get219= RenderConstants.num4)
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (sendImmediatelyEvent.isCancelled()) {
            return;
        }
        if (sendImmediatelyEvent.getPacket() instanceof ClickSlotC2SPacket) {
            this.flag = true;
            this.stopwatch.reset();
            if (!this.flag2 || !this.flag3) {
                eventBus.fireEvent(new InventorySyncEvent(KeyPearlMode.Pre));
            }
        }
        if ((sendImmediatelyEvent.getPacket() instanceof CloseHandledScreenC2SPacket) || (sendImmediatelyEvent.getPacket() instanceof DisconnectS2CPacket)) {
            this.flag = false;
        }
        UpdateSelectedSlotC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof UpdateSelectedSlotC2SPacket ? (UpdateSelectedSlotC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof UpdateSelectedSlotC2SPacket) {
            UpdateSelectedSlotC2SPacket updateSelectedSlotC2SPacket = packet904;
            if (updateSelectedSlotC2SPacket.getSelectedSlot() != this.num) {
                eventBus.fireEvent(new SlotChangeEvent(updateSelectedSlotC2SPacket.getSelectedSlot()));
                this.num = updateSelectedSlotC2SPacket.getSelectedSlot();
            } else if (RotationManager.is724()) {
                sendImmediatelyEvent.cancel();
            }
        }
        if (sendImmediatelyEvent.getPacket() instanceof DisconnectS2CPacket) {
            this.num = -1;
        }
    }

    @Listen(get219= RenderConstants.num2)
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof CloseScreenS2CPacket) {
            this.flag = false;
        }
        if (channelRead0Event.getPacket() instanceof InventoryS2CPacket) {
            this.map.clear();
        }
        ScreenHandlerSlotUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof ScreenHandlerSlotUpdateS2CPacket ? (ScreenHandlerSlotUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof ScreenHandlerSlotUpdateS2CPacket) {
            ScreenHandlerSlotUpdateS2CPacket screenHandlerSlotUpdateS2CPacket = packet904;
            if (RotationManager.is724()) {
                if (screenHandlerSlotUpdateS2CPacket.getSlot() == 36 + minecraftClient.player.getInventory().selectedSlot || screenHandlerSlotUpdateS2CPacket.getSlot() < 36 || screenHandlerSlotUpdateS2CPacket.getSlot() > 44) {
                    return;
                }
                if (!isItemInHotbar(screenHandlerSlotUpdateS2CPacket.getStack(), screenHandlerSlotUpdateS2CPacket.getSlot())) {
                    this.map.remove(Integer.valueOf(screenHandlerSlotUpdateS2CPacket.getSlot()));
                    return;
                } else {
                    this.map.put(Integer.valueOf(screenHandlerSlotUpdateS2CPacket.getSlot()), new Record(screenHandlerSlotUpdateS2CPacket, System.currentTimeMillis() + 500));
                    channelRead0Event.cancel();
                }
            }
        }
        EntityStatusS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof EntityStatusS2CPacket ? (EntityStatusS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof EntityStatusS2CPacket) {
            EntityStatusS2CPacket entityStatusS2CPacket = packet9042;
            if (entityStatusS2CPacket.getStatus() == 35 && entityStatusS2CPacket.getEntity(minecraftClient.world) == minecraftClient.player) {
                this.flag4 = true;
            }
        }
    }

    @Listen
    public void onPacketSendPost(PacketSendPostEvent sendInternalEvent) {
        if (sendInternalEvent.getPacket() instanceof ClickSlotC2SPacket) {
            if (this.flag2 && this.flag3) {
                return;
            }
            eventBus.fireEvent(new InventorySyncEvent(KeyPearlMode.Post));
            this.flag3 = true;
        }
    }

    @Listen
    public void onUpdateSelectedSlot(UpdateSelectedSlotEvent updateSelectedSlotEvent) {
        if (minecraftClient.isInSingleplayer()) {
            return;
        }
        PacketSender.sendSelectedSlot(minecraftClient.player.getInventory().selectedSlot);
        updateSelectedSlotEvent.cancel();
    }

    public boolean isItemInHotbar(ItemStack itemStack, int i) {
        for (int i2 = 0; i2 < 9; i2++) {
            if (i - 36 != i2) {
                if (ItemStack.areEqual(minecraftClient.player.getInventory().getStack(i2), itemStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void close() {
        minecraftClient.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(minecraftClient.player.currentScreenHandler.syncId));
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
