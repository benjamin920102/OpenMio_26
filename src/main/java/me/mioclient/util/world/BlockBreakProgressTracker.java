package me.mioclient.util.world;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.player.SpeedMineMode_2;
import me.mioclient.util.inventory.EnchantmentHelper;

import me.mioclient.api.ClientAccessor;
import me.mioclient.module.render.breakhighlight.BreakProgressTracker;

import me.mioclient.event.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.core.BlockPos;

public final class BlockBreakProgressTracker implements ClientAccessor {
    /* 26.2 port: resolved lazily — the static initializer used to run before the
       module registry was ready, leaving speedmine null forever (NPE spam). */
    private static SpeedMine speedmine;

    public static SpeedMine getSpeedmine() {
        if (speedmine == null && ClientServices.moduleInjector != null) {
            try {
                speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
            } catch (Exception e) {
            }
        }
        return speedmine;
    }
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
            float f2 = ((float) WorldUtils.getBlockBreakingDelta(getEfficiencyPickaxe(), minecraftClient.level.getBlockState(entityIdHelper.getBlockPos()), true)) * ClientServices.holeSnapRenderHelper.getTickRateFactor();
            SpeedMine speedmine_lcl = getSpeedmine();
            if (speedmine_lcl != null && speedmine_lcl.isToggled()) {
                f2 /= speedmine_lcl.damage.getValue().floatValue();
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
        ClientboundBlockDestructionPacket packet904 = (channelRead0Event.getPacket()) instanceof ClientboundBlockDestructionPacket ? (ClientboundBlockDestructionPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof ClientboundBlockDestructionPacket) {
            ClientboundBlockDestructionPacket blockBreakingProgressS2CPacket = packet904;
            minecraftClient.execute(() -> {
                if (blockBreakingProgressS2CPacket.getProgress() < 0 || blockBreakingProgressS2CPacket.getProgress() > 10) {
                    return;
                }
                trackBlockBreak(blockBreakingProgressS2CPacket.getPos(), blockBreakingProgressS2CPacket.getId());
            });
        }
        ClientboundBlockUpdatePacket packet9042 = (channelRead0Event.getPacket()) instanceof ClientboundBlockUpdatePacket ? (ClientboundBlockUpdatePacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof ClientboundBlockUpdatePacket) {
            ClientboundBlockUpdatePacket blockUpdateS2CPacket = packet9042;
            if (blockUpdateS2CPacket.getBlockState().isAir()) {
                resetBlockBreakProgress(blockUpdateS2CPacket.getPos());
            }
        }
        ClientboundSectionBlocksUpdatePacket packet9043 = (channelRead0Event.getPacket()) instanceof ClientboundSectionBlocksUpdatePacket ? (ClientboundSectionBlocksUpdatePacket) (channelRead0Event.getPacket()) : null;
        if (packet9043 instanceof ClientboundSectionBlocksUpdatePacket) {
            packet9043.runUpdates((blockPos, blockState) -> {
                if (blockState.isAir()) {
                    resetBlockBreakProgress(blockPos);
                }
            });
        }
    }

    public void resetBlockBreakProgress(BlockPos blockPos) {
        SpeedMine speedmine_lcl = getSpeedmine();
        if (speedmine_lcl == null || speedmine_lcl.rebreak2.getValue() == SpeedMineMode_2.INSTANT) {
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
            // 26.2 port: ItemStack.addEnchantment(Holder,int) -> enchant(Holder,int).
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> eff = EnchantmentHelper.getEnchantmentEntry(Enchantments.EFFICIENCY);
            if (eff != null) {
                itemStack.enchant(eff, 5);
            }
        }
        return itemStack;
    }
}
