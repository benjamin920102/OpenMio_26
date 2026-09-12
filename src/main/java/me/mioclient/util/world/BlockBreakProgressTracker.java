package me.mioclient.util.world;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.player.SpeedMineMode_2;
import me.mioclient.util.inventory.EnchantmentHelper;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.module.render.breakhighlight.BreakProgressTracker;

import me.mioclient.event.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.inventory.EnchantmentHelper;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import me.mioclient.mode.player.SpeedMineMode_2;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BlockBreakProgressTracker.class */
public final class BlockBreakProgressTracker implements ClientAccessor {
    public static final SpeedMine speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
    public static ItemStack itemStack;
    public final List<BreakProgressTracker> list = Collections.synchronizedList(new ArrayList());

    public BlockBreakProgressTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        float f;
        this.list.removeIf((v0) -> {
            return v0.is2817();
        });
        for (BreakProgressTracker entityIdHelper : this.list) {
            float f2 = ((float) WorldUtils.getBlockBreakingDelta(getEfficiencyPickaxe(), minecraftClient.world.getBlockState(entityIdHelper.getBlockPos()), true)) * ClientServices.holeSnapRenderHelper.getTickRateFactor();
            if (speedmine.isToggled()) {
                f2 /= speedmine.damage.getValue().floatValue();
            }
            if (WorldUtils.isBreakable(entityIdHelper.getBlockPos())) {
                f = f2 * Math.max(entityIdHelper.get2093(), 1);
                entityIdHelper.setBreakerId(0);
            } else {
                f = 0.0f;
                entityIdHelper.setBreakerId(entityIdHelper.get2093() + 1);
                entityIdHelper.resetTimer();
            }
            entityIdHelper.setMiningProgress(entityIdHelper.get2142() + f);
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        BlockBreakingProgressS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof BlockBreakingProgressS2CPacket ? (BlockBreakingProgressS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof BlockBreakingProgressS2CPacket) {
            BlockBreakingProgressS2CPacket blockBreakingProgressS2CPacket = packet904;
            minecraftClient.execute(() -> {
                if (blockBreakingProgressS2CPacket.getProgress() < 0 || blockBreakingProgressS2CPacket.getProgress() > 10) {
                    return;
                }
                trackBlockBreak(blockBreakingProgressS2CPacket.getPos(), blockBreakingProgressS2CPacket.getEntityId());
            });
        }
        BlockUpdateS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof BlockUpdateS2CPacket ? (BlockUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof BlockUpdateS2CPacket) {
            BlockUpdateS2CPacket blockUpdateS2CPacket = packet9042;
            if (blockUpdateS2CPacket.getState().isAir()) {
                resetBlockBreakProgress(blockUpdateS2CPacket.getPos());
            }
        }
        ChunkDeltaUpdateS2CPacket packet9043 = (channelRead0Event.getPacket()) instanceof ChunkDeltaUpdateS2CPacket ? (ChunkDeltaUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9043 instanceof ChunkDeltaUpdateS2CPacket) {
            packet9043.visitUpdates((blockPos, blockState) -> {
                if (blockState.isAir()) {
                    resetBlockBreakProgress(blockPos);
                }
            });
        }
    }

    public void resetBlockBreakProgress(BlockPos blockPos) {
        if (speedmine.rebreak2.getValue() == SpeedMineMode_2.INSTANT) {
            return;
        }
        minecraftClient.execute(() -> {
            for (BreakProgressTracker entityIdHelper : this.list) {
                if (blockPos.equals(entityIdHelper.getBlockPos())) {
                    entityIdHelper.setMiningProgress(0.0f);
                    entityIdHelper.setBreakerId(0);
                }
            }
        });
    }

    public void trackBlockBreak(BlockPos blockPos, int i) {
        boolean z = false;
        Iterator<BreakProgressTracker> it = this.list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BreakProgressTracker next = it.next();
            if (next.getEntityId() == i && next.getBlockPos().equals(blockPos)) {
                z = true;
                break;
            }
        }
        if (z) {
            return;
        }
        this.list.removeIf(entityIdHelper -> {
            return entityIdHelper.getBlockPos().equals(blockPos) || entityIdHelper.getEntityId() == i;
        });
        if (WorldUtils.isBreakable(blockPos)) {
            this.list.add(new BreakProgressTracker(i, blockPos));
        }
    }

    public float getBreakProgress(BlockPos blockPos) {
        if (!WorldUtils.isBreakable(blockPos)) {
            return 0.0f;
        }
        return ((Float) this.list.stream().filter(entityIdHelper -> {
            return entityIdHelper.getBlockPos().equals(blockPos);
        }).map((v0) -> {
            return v0.get2142();
        }).findAny().orElse(Float.valueOf(0.0f))).floatValue();
    }

    public List<BreakProgressTracker> getList1517() {
        return this.list;
    }

    public static ItemStack getEfficiencyPickaxe() {
        if (itemStack == null) {
            itemStack = new ItemStack(Items.NETHERITE_PICKAXE);
            itemStack.addEnchantment(EnchantmentHelper.getEnchantmentEntry(Enchantments.EFFICIENCY), 5);
        }
        return itemStack;
    }
}
