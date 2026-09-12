package me.mioclient.module.combat.autocrystal;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.ScaffoldHelperMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.inventory.InventoryUtils;

import me.mioclient.event.EventBus;

import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.misc.Swing;
import me.mioclient.module.render.Hitmarker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Hand;
import me.mioclient.mode.combat.ScaffoldHelperMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/CrystalAttackHandler.class */
public class CrystalAttackHandler implements ClientAccessor {
    public static final Swing swing = (Swing) ClientServices.moduleInjector.getModule(Swing.class);
    public static AutoCrystal ac = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public static Hitmarker hitmarker = (Hitmarker) ClientServices.moduleInjector.getModule(Hitmarker.class);
    public final Stopwatch stopwatch = new Stopwatch();
    public long num;
    public int current;

    public CrystalAttackHandler() {
        eventBus.register(this);
    }

    @Listen(get219= -300)
    public void onTick(TickEvent tickEvent) {
        this.current = -1;
    }

    public void setCurrentTarget(int i) {
        this.current = i;
    }

    public boolean tryAttackCrystal(int i) {
        return sendAttackPacket(i, false);
    }

    public boolean sendAttackPacket(int i, boolean z) {
        if (!isInstantAttackEnabled() && z) {
            return false;
        }
        if (this.current != -1 && !z) {
            return false;
        }
        int i2 = minecraftClient.player.getInventory().selectedSlot;
        boolean isWeaknessSwapActive = isWeaknessSwapActive();
        this.current = i;
        PacketSender.sendInteractEntity(this.current);
        Entity entityById = minecraftClient.world.getEntityById(i);
        if (entityById != null && entityById.getWorld() != null && entityById != minecraftClient.player && entityById.getWorld().isClient && hitmarker.shouldTrackEntity(entityById) && (entityById instanceof EndCrystalEntity)) {
            hitmarker.num = System.currentTimeMillis();
            hitmarker.num2 = 255;
            hitmarker.flag = true;
        }
        Hand hand = minecraftClient.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (!(entityById instanceof EndCrystalEntity) && entityById != null) {
            hand = Hand.MAIN_HAND;
        }
        do1117(hand);
        if (z) {
            PacketSender.sendHandSwing(Hand.MAIN_HAND);
        } else {
            minecraftClient.player.swingHand(hand);
        }
        if (!isWeaknessSwapActive || ac.weakness.getValue() != ScaffoldHelperMode.SILENT) {
            return true;
        }
        ac.incrementSwapCounter();
        InventoryUtils.selectSlot(i2);
        return true;
    }

    public boolean isInstantAttackEnabled() {
        return ac.instant.getValue() != AutoCrystalMode.NONE;
    }

    public int getAttackDelay() {
        return ac.delay2.getValue().intValue();
    }

    public int get1116() {
        return ac.ticksExisted.getValue().intValue();
    }

    public void do1117(Hand hand) {
        if (!this.stopwatch.hasElapsedAndResetMs(200 + this.num) || hand != Hand.MAIN_HAND || swing.hand.getValue() == Swing.ScaffoldHelperMode.scaffoldHelperMode2 || swing.type.getValue() == Swing.MixinLivingEntityMode.VANILLA) {
            return;
        }
        minecraftClient.player.resetLastAttackedTicks();
        this.num = (long) (Math.random() * Double.longBitsToDouble(4643985272004935680L));
    }

    public boolean isWeaknessSwapActive() {
        int i;
        if (ac.weakness.getValue() == ScaffoldHelperMode.NONE) {
            return false;
        }
        if (!minecraftClient.player.hasStatusEffect(StatusEffects.WEAKNESS) || (minecraftClient.player.getMainHandStack().getItem() instanceof ToolItem) || (i = InventoryUtils.findInHotbar(itemStack -> {
            return itemStack.getItem() instanceof SwordItem;
        })) == -1) {
            return false;
        }
        if (ac.weakness.getValue() == ScaffoldHelperMode.SILENT) {
            ac.incrementSwapCounter();
        }
        InventoryUtils.selectSlot(i);
        return true;
    }
}
