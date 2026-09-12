package me.mioclient.module.combat;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.util.client.InteractPacketReader;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.client.InteractPacketReader;
import me.mioclient.util.inventory.InventoryUtils;

import java.util.HashMap;
import java.util.Map;
import me.mioclient.api.Category;
import me.mioclient.mixin.ducks.DuckMinecraftClient;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/LegacyCrystal.class */
public class LegacyCrystal extends Module {
    public Setting<Integer> setting;
    public Setting<Boolean> setting2;
    public final Map<BlockPos, Long> map;
    public final Stopwatch stopwatch;
    public long num;

    public LegacyCrystal() {
        super("LegacyCrystal", "Legit auto crystal (crystal aura).", Category.COMBAT, new String[0]);
        this.map = new HashMap();
        this.stopwatch = new Stopwatch();
        this.num = 150L;
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (this.setting == null || this.setting2 == null) {
            return;
        }
        synchronized (this.map) {
            this.map.entrySet().removeIf(entry -> {
                return ((Long) entry.getValue()).longValue() + 15000 < System.currentTimeMillis();
            });
        }
        if (minecraftClient.crosshairTarget instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult) minecraftClient.crosshairTarget).getEntity();
            if (this.stopwatch.hasElapsedMs(this.num) && (entity instanceof EndCrystalEntity)) {
                if (this.map.containsKey(entity.getBlockPos().down())) {
                    ((DuckMinecraftClient)(Object) minecraftClient).attack();
                    this.num = (long) (Math.random() * this.setting.getValue().intValue());
                    this.stopwatch.reset();
                }
            }
        }
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if (minecraftClient.world.getBlockState(interactBlockEvent.getHitResult().getBlockPos().up(2)).isAir()) {
            boolean isCrystalPlaceable = BlockPlacer.isCrystalPlaceable(interactBlockEvent.getHitResult().getBlockPos(), false, false, true, false, false, false);
            if (minecraftClient.player.getMainHandStack().getItem() instanceof SwordItem) {
                InventoryUtils.selectSlot(InventoryUtils.findInHotbar(isCrystalPlaceable ? Items.END_CRYSTAL : Items.OBSIDIAN));
                return;
            }
            if (minecraftClient.player.getMainHandStack().isOf(Items.OBSIDIAN) && isCrystalPlaceable) {
                InventoryUtils.do438(InventoryUtils.findInHotbar(Items.END_CRYSTAL));
            }
        }
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (this.setting2 == null) {
            return;
        }
        PlayerInteractBlockC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractBlockC2SPacket ? (PlayerInteractBlockC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerInteractBlockC2SPacket) {
            PlayerInteractBlockC2SPacket playerInteractBlockC2SPacket = packet904;
            if (minecraftClient.player.getStackInHand(playerInteractBlockC2SPacket.getHand()).isOf(Items.END_CRYSTAL)) {
                this.map.compute(playerInteractBlockC2SPacket.getBlockHitResult().getBlockPos(), (blockPos, l) -> {
                    return Long.valueOf(System.currentTimeMillis());
                });
            }
        }
        PlayerInteractEntityC2SPacket packet9042 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractEntityC2SPacket ? (PlayerInteractEntityC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet9042 instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket playerInteractEntityC2SPacket = packet9042;
            if (this.setting2.getValue().booleanValue() && InteractPacketReader.getCrystalInteractionType(playerInteractEntityC2SPacket) == InteractPacketReader.LegacyCrystalMode.ATTACK && (InteractPacketReader.resolvePacketEntity(playerInteractEntityC2SPacket) instanceof EndCrystalEntity)) {
                InteractPacketReader.resolvePacketEntity(playerInteractEntityC2SPacket).setRemoved(Entity.RemovalReason.KILLED);
            }
        }
    }
}
