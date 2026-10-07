package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.NamedValue;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientAccessor;
import me.mioclient.network.PacketSender;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class NoFall extends Module {
    public Setting<NoFallMode> mode = add(new EnumSetting<>("Mode", NoFallMode.noFallMode));
    public Setting<Float> groundDistance = add(new NumberSetting<>("GroundDistance", 2.0f, 1.0f, 4.0f, v -> this.mode.getValue() == NoFall.NoFallMode.noFallMode3 || this.mode.getValue() == NoFall.NoFallMode.noFallMode4).setSuffix("m"));

    
    public static enum NoFallMode implements NamedValue {
        noFallMode("Packet") {
            @Override
            public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
                if (!(sendImmediatelyEvent.getPacket() instanceof ServerboundMovePlayerPacket pkt)) {
                    return;
                }
                if (ClientAccessor.minecraftClient.player.fallDistance <= 3.0f || ClientAccessor.minecraftClient.player.isFallFlying()) {
                    return;
                }
                // 26.2 port: ServerboundMovePlayerPacket is immutable - cancel + send grounded replacement.
                sendImmediatelyEvent.cancel();
                boolean hc = pkt.horizontalCollision();
                net.minecraft.client.player.LocalPlayer pl = ClientAccessor.minecraftClient.player;
                if (pkt instanceof ServerboundMovePlayerPacket.PosRot) {
                    pl.connection.send(new ServerboundMovePlayerPacket.PosRot(pkt.getX(pl.getX()), pkt.getY(pl.getY()), pkt.getZ(pl.getZ()), pkt.getYRot(pl.getYRot()), pkt.getXRot(pl.getXRot()), true, hc));
                } else if (pkt instanceof ServerboundMovePlayerPacket.Pos) {
                    pl.connection.send(new ServerboundMovePlayerPacket.Pos(pkt.getX(pl.getX()), pkt.getY(pl.getY()), pkt.getZ(pl.getZ()), true, hc));
                } else if (pkt instanceof ServerboundMovePlayerPacket.Rot) {
                    pl.connection.send(new ServerboundMovePlayerPacket.Rot(pkt.getYRot(pl.getYRot()), pkt.getXRot(pl.getXRot()), true, hc));
                } else {
                    pl.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, hc));
                }
            }
        },
        noFallMode2("Anti") {
            @Override
            public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
                if (!(sendImmediatelyEvent.getPacket() instanceof ServerboundMovePlayerPacket) || ClientAccessor.minecraftClient.player.fallDistance <= 3.0f) {
                    return;
                }
                if (ClientAccessor.minecraftClient.level.noCollision(ClientAccessor.minecraftClient.player.getBoundingBox().expandTowards(0.0d, ClientAccessor.minecraftClient.player.getDeltaMovement().y * 1.1d, 0.0d))) {
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
                    hashSet2372.add(BlockPos.containing(ClientAccessor.minecraftClient.player.position()));
                    for (int i = 0; i <= noFall.groundDistance.getValue().floatValue(); i++) {
                        Iterator<BlockPos> it = hashSet2372.iterator();
                        while (it.hasNext()) {
                            if (ClientAccessor.minecraftClient.level.getBlockState(it.next().relative(Direction.DOWN, i)).getBlock() != Blocks.AIR) {
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
                    hashSet2372.add(BlockPos.containing(ClientAccessor.minecraftClient.player.position()));
                    for (int i = 0; i <= noFall.groundDistance.getValue().floatValue(); i++) {
                        Iterator<BlockPos> it = hashSet2372.iterator();
                        while (it.hasNext()) {
                            if (ClientAccessor.minecraftClient.level.getBlockState(it.next().relative(Direction.DOWN, i)).getBlock() != Blocks.AIR) {
                                boolean z = ClientAccessor.minecraftClient.player.getOffhandItem().getItem() == Items.WATER_BUCKET;
                                int i2 = InventoryUtils.findInHotbar(Items.WATER_BUCKET);
                                if (i2 != -1 || z) {
                                    ClientAccessor.minecraftClient.player.setXRot(MathConstants.num2);
                                    if (!z) {
                                        InventoryUtils.selectSlot(i2);
                                    }
                                    InteractionHand hand = z ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                                    ClientAccessor.minecraftClient.gameMode.useItem(ClientAccessor.minecraftClient.player, hand);
                                    ClientAccessor.minecraftClient.player.swing(hand);
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
                if (noFall.isNotInGame() || noFall.isFallFlyingWithElytra() || ClientAccessor.minecraftClient.player.onGround() || ClientAccessor.minecraftClient.player.fallDistance <= 3.0f) {
                    return;
                }
                HashSet<BlockPos> hashSet2372 = noFall.getBottomCorners(ClientAccessor.minecraftClient.player.getBoundingBox());
                hashSet2372.add(BlockPos.containing(ClientAccessor.minecraftClient.player.position()));
                for (int i = 0; i <= 3; i++) {
                    Iterator<BlockPos> it = hashSet2372.iterator();
                    while (it.hasNext()) {
                        if (ClientAccessor.minecraftClient.level.getBlockState(it.next().relative(Direction.DOWN, i)).getBlock() != Blocks.AIR) {
                            PacketSender.sendFullMove(ClientAccessor.minecraftClient.player.getX(), ClientAccessor.minecraftClient.player.getY() + 1.0E-9d, ClientAccessor.minecraftClient.player.getZ(), ClientAccessor.minecraftClient.player.getYRot(), ClientAccessor.minecraftClient.player.getXRot(), false);
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

    public HashSet<BlockPos> getBottomCorners(AABB box) {
        return new HashSet<>(List.of(BlockPos.containing(box.maxX, box.minY, box.maxZ), BlockPos.containing(box.maxX, box.minY, box.minZ), BlockPos.containing(box.minX, box.minY, box.maxZ), BlockPos.containing(box.minX, box.minY, box.minZ)));
    }

    public boolean isFallFlyingWithElytra() {
        return minecraftClient.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) && minecraftClient.player.isFallFlying();
    }
}