package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.NamedValue;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.NamedValue;
import me.mioclient.network.PacketSender;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.mixin.ducks.DuckPlayerMoveC2SPacket;
import me.mioclient.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/NoFall.class */
public class NoFall extends Module {
    public Setting<NoFallMode> mode = add(new EnumSetting<>("Mode", NoFallMode.noFallMode));
    public Setting<Float> groundDistance = add(new NumberSetting<>("GroundDistance", 2.0f, 1.0f, 4.0f, v -> this.mode.getValue() == NoFall.NoFallMode.noFallMode3 || this.mode.getValue() == NoFall.NoFallMode.noFallMode4).setSuffix("m"));

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/NoFall$NoFallMode.class */
    public static enum NoFallMode implements NamedValue {
        noFallMode("Packet") {
            @Override
            public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
                DuckPlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (DuckPlayerMoveC2SPacket)(sendImmediatelyEvent.getPacket()) : null;
                if (packet904 instanceof PlayerMoveC2SPacket) {
                    DuckPlayerMoveC2SPacket duckPlayerMoveC2SPacket = (DuckPlayerMoveC2SPacket)((PlayerMoveC2SPacket) packet904);
                    if (ClientAccessor.minecraftClient.player.fallDistance <= 3.0f || ClientAccessor.minecraftClient.player.isFallFlying()) {
                        return;
                    }
                    duckPlayerMoveC2SPacket.setOnGround(true);
                }
            }
        },
        noFallMode2("Anti") {
            @Override
            public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
                if (!(sendImmediatelyEvent.getPacket() instanceof PlayerMoveC2SPacket) || ClientAccessor.minecraftClient.player.fallDistance <= 3.0f) {
                    return;
                }
                if (ClientAccessor.minecraftClient.world.isSpaceEmpty(ClientAccessor.minecraftClient.player.getBoundingBox().stretch(0.0d, ClientAccessor.minecraftClient.player.getVelocity().y * 1.1d, 0.0d))) {
                    return;
                }
                ClientAccessor.minecraftClient.player.setPos(ClientAccessor.minecraftClient.player.getX(), ClientAccessor.minecraftClient.player.getY() + 2.0d, ClientAccessor.minecraftClient.player.getZ());
            }
        },
        noFallMode3("Teleport") {
            @Override
            public void executeFallMode(NoFall noFall) {
                if (ClientAccessor.minecraftClient.player.fallDistance > 3.0f) {
                    HashSet<BlockPos> hashSet2372 = noFall.getBottomCorners(ClientAccessor.minecraftClient.player.getBoundingBox());
                    hashSet2372.add(BlockPos.ofFloored(ClientAccessor.minecraftClient.player.getPos()));
                    for (int i = 0; i <= noFall.groundDistance.getValue().floatValue(); i++) {
                        Iterator<BlockPos> it = hashSet2372.iterator();
                        while (it.hasNext()) {
                            if (ClientAccessor.minecraftClient.world.getBlockState(it.next().offset(Direction.DOWN, i)).getBlock() != Blocks.AIR) {
                                if (WorldUtils.getDimension().isTheEnd()) {
                                    PacketSender.sendPosition(0.0d, 64.0d, 0.0d, false);
                                } else {
                                    PacketSender.sendPosition(ClientAccessor.minecraftClient.player.getX(), 0.0d, ClientAccessor.minecraftClient.player.getZ(), false);
                                }
                                ClientAccessor.minecraftClient.player.fallDistance = 0.0f;
                            }
                        }
                    }
                }
            }
        },
        noFallMode4("MLG") {
            @Override
            public void executeFallMode(NoFall noFall) {
                if (ClientAccessor.minecraftClient.player.fallDistance > 3.0f) {
                    HashSet<BlockPos> hashSet2372 = noFall.getBottomCorners(ClientAccessor.minecraftClient.player.getBoundingBox());
                    hashSet2372.add(BlockPos.ofFloored(ClientAccessor.minecraftClient.player.getPos()));
                    for (int i = 0; i <= noFall.groundDistance.getValue().floatValue(); i++) {
                        Iterator<BlockPos> it = hashSet2372.iterator();
                        while (it.hasNext()) {
                            if (ClientAccessor.minecraftClient.world.getBlockState(it.next().offset(Direction.DOWN, i)).getBlock() != Blocks.AIR) {
                                boolean z = ClientAccessor.minecraftClient.player.getOffHandStack().getItem() == Items.WATER_BUCKET;
                                int i2 = InventoryUtils.findInHotbar(Items.WATER_BUCKET);
                                if (i2 != -1 || z) {
                                    ClientAccessor.minecraftClient.player.setPitch(MathConstants.num2);
                                    if (!z) {
                                        InventoryUtils.selectSlot(i2);
                                    }
                                    Hand hand = z ? Hand.OFF_HAND : Hand.MAIN_HAND;
                                    ClientAccessor.minecraftClient.interactionManager.interactItem(ClientAccessor.minecraftClient.player, hand);
                                    ClientAccessor.minecraftClient.player.swingHand(hand);
                                }
                            }
                        }
                    }
                }
            }
        },
        noFallMode5("Grim") {
            @Override
            public void executeFallMode(NoFall noFall) {
                if (noFall.isNotInGame() || noFall.isFallFlyingWithElytra() || ClientAccessor.minecraftClient.player.isOnGround() || ClientAccessor.minecraftClient.player.fallDistance <= 3.0f) {
                    return;
                }
                HashSet<BlockPos> hashSet2372 = noFall.getBottomCorners(ClientAccessor.minecraftClient.player.getBoundingBox());
                hashSet2372.add(BlockPos.ofFloored(ClientAccessor.minecraftClient.player.getPos()));
                for (int i = 0; i <= 3; i++) {
                    Iterator<BlockPos> it = hashSet2372.iterator();
                    while (it.hasNext()) {
                        if (ClientAccessor.minecraftClient.world.getBlockState(it.next().offset(Direction.DOWN, i)).getBlock() != Blocks.AIR) {
                            PacketSender.sendFullMove(ClientAccessor.minecraftClient.player.getX(), ClientAccessor.minecraftClient.player.getY() + 1.0E-9d, ClientAccessor.minecraftClient.player.getZ(), ClientAccessor.minecraftClient.player.getYaw(), ClientAccessor.minecraftClient.player.getPitch(), false);
                        }
                    }
                }
            }
        };

        public final String name;

        NoFallMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        }

        public void executeFallMode(NoFall noFall) {
        }
    }

    public NoFall() {
        super("NoFall", "Prevents falling damage.", Category.MOVEMENT, new String[0]);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return ClientTextRenderer.capitalize(this.mode.getValue());
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.mode.getValue().executeFallMode(this);
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        this.mode.getValue().handlePacketSend(sendImmediatelyEvent);
    }

    public HashSet<BlockPos> getBottomCorners(Box box) {
        return new HashSet<>(List.of(BlockPos.ofFloored(box.maxX, box.minY, box.maxZ), BlockPos.ofFloored(box.maxX, box.minY, box.minZ), BlockPos.ofFloored(box.minX, box.minY, box.maxZ), BlockPos.ofFloored(box.minX, box.minY, box.minZ)));
    }

    public boolean isFallFlyingWithElytra() {
        return minecraftClient.player.getInventory().getArmorStack(EquipmentSlot.CHEST.getEntitySlotId()).isOf(Items.ELYTRA) && minecraftClient.player.isFallFlying();
    }
}
