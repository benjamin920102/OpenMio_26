package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.player.PlayerUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.LoadChunkFromPacketEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

public class VisualRange extends Module {
    public static final int num = -Math.abs("EnderPearl".hashCode());
    public Setting<Boolean> join = add(new BooleanSetting("Join", true));
    public Setting<Boolean> leave = add(new BooleanSetting("Leave", true));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", false).setDependent());
    public Setting<Boolean> players = add(new BooleanSetting("Players", true).requiresSettings(this.targets));
    public Setting<Boolean> friends = add(new BooleanSetting("Friends", false).requiresSettings(this.targets));
    public Setting<Boolean> enemies = add(new BooleanSetting("Enemies", true).requiresSettings(this.targets));
    public Setting<Boolean> nakeds = add(new BooleanSetting("Nakeds", false).requiresSettings(this.targets));
    public Setting<Boolean> signs = add(new BooleanSetting("Signs", false).requiresSettings(this.targets));
    public Setting<Boolean> enderPearls = add(new BooleanSetting("EnderPearls", false).requiresSettings(this.targets));
    public Setting<Boolean> playSound = add(new BooleanSetting("PlaySound", false).setVisible());
    public Setting<Boolean> onlyEnemies = add(new BooleanSetting("OnlyEnemies", false).requiresSettings(this.playSound));
    public Setting<SoundIdentifier> sound = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "warning")).requiresSettings(this.playSound));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 1.0f, 0.0f, 1.0f).requiresSettings(this.playSound));
    public final ArrayList<String> arrayList;
    public final ArrayList<String> arrayList2;

    public VisualRange() {
        super("VisualRange", "Informs you of players who enter/leave your visual range.", Category.MISC, new String[0]);
        this.arrayList = new ArrayList<>();
        this.arrayList2 = new ArrayList<>();
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        this.arrayList.clear();
        this.arrayList2.clear();
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (isNotInGame()) {
            return;
        }
        this.arrayList2.clear();
        try {
            for (AbstractClientPlayer abstractClientPlayerEntity : minecraftClient.level.players()) {
                if (!abstractClientPlayerEntity.getName().equals(minecraftClient.player.getName()) && shouldTrackPlayer((Player) abstractClientPlayerEntity)) {
                    this.arrayList2.add(abstractClientPlayerEntity.getName().getString());
                }
            }
            if (!this.arrayList.equals(this.arrayList2)) {
                Iterator<String> it = this.arrayList2.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    if (!this.arrayList.contains(next) && this.join.getValue().booleanValue()) {
                        Color color530 = ClientServices.socialManager.getSocialColor(next, Color.WHITE);
                        ChatUtils.sendPrefixedMessage(Component.literal("%s%s entered visual range.".formatted(next, ChatFormatting.WHITE)).withStyle(style -> {
                            return style.withColor(color530.hashCode());
                        }), ChatUtils.createSignature((Math.abs(next.hashCode()) * (-1)) / 2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                        if ((!ClientServices.socialManager.isFriend(next) && ClientServices.socialManager.isEnemy(next)) || !this.onlyEnemies.getValue().booleanValue()) {
                            do1016();
                        }
                    }
                }
                Iterator<String> it2 = this.arrayList.iterator();
                while (it2.hasNext()) {
                    String next2 = it2.next();
                    if (!this.arrayList2.contains(next2) && this.leave.getValue().booleanValue()) {
                        Color color5302 = ClientServices.socialManager.getSocialColor(next2, Color.WHITE);
                        ChatUtils.sendPrefixedMessage(Component.literal("%s%s left visual range.".formatted(next2, ChatFormatting.WHITE)).withStyle(style2 -> {
                            return style2.withColor(color5302.hashCode());
                        }), ChatUtils.createSignature((Math.abs(next2.hashCode()) * (-1)) / 2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
                    }
                }
                this.arrayList.clear();
                this.arrayList.addAll(this.arrayList2);
            }
        } catch (Exception e) {
        }
    }

    @Listen
    public void onLoadChunkFromPacket(LoadChunkFromPacketEvent loadChunkFromPacketEvent) {
        if (this.signs.getValue().booleanValue()) {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            loadChunkFromPacketEvent.getChunk().getBlockEntities().forEach((blockPos, blockEntity) -> {
                if (blockEntity instanceof SignBlockEntity) {
                    int i = -Math.abs(blockPos.hashCode());
                    MutableComponent literal = Component.literal("Found a sign: ");
                    boolean z = true;
                    for (Component text : ((SignBlockEntity) blockEntity).getFrontText().getMessages(false)) {
                        if (!ChatFormatting.stripFormatting(text.getString()).isBlank()) {
                            literal.append(text);
                            literal.append(" ");
                            z = false;
                        }
                    }
                    if (z) {
                        literal.append("<empty>");
                    }
                    literal.withStyle(style -> {
                        return style.applyFormat(ChatFormatting.WHITE);
                    });
                    String formatted = "%d, %d, %d".formatted(Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()));
                    HoverEvent hoverEvent = new HoverEvent.ShowText(Component.literal(new StringTemplate().arg(formatted).format("at \u0001")));
                    literal.withStyle(style2 -> {
                        return style2.withHoverEvent(hoverEvent);
                    });
                    literal.withStyle(style3 -> {
                        return style3.withClickEvent(ChatUtils.suggestCommand(new StringTemplate().arg(formatted.replace(",", "")).format("highlight \u0001")));
                    });
                    ChatUtils.sendMessage((Component) literal, ChatUtils.createSignature(i));
                    atomicBoolean.set(true);
                }
            });
            if (atomicBoolean.get()) {
                do1016();
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        ClientboundAddEntityPacket packet904 = (channelRead0Event.getPacket()) instanceof ClientboundAddEntityPacket ? (ClientboundAddEntityPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof ClientboundAddEntityPacket) {
            ClientboundAddEntityPacket entitySpawnS2CPacket = packet904;
            if (entitySpawnS2CPacket.getType() == EntityTypes.ENDER_PEARL && this.enderPearls.getValue().booleanValue()) {
                Component literal = Component.literal("Ender Pearl entered visual range.");
                String formatted = "%.0f, %.0f, %.0f".formatted(Double.valueOf(entitySpawnS2CPacket.getX()), Double.valueOf(entitySpawnS2CPacket.getY()), Double.valueOf(entitySpawnS2CPacket.getZ()));
                MutableComponent literal2 = Component.literal(new StringTemplate().arg(formatted).format("at \u0001"));
                ((MutableComponent) literal).withStyle(style -> {
                    return style.withHoverEvent(new HoverEvent.ShowText(literal2));
                });
                ((MutableComponent) literal).withStyle(style2 -> {
                    return style2.withClickEvent(ChatUtils.suggestCommand(new StringTemplate().arg(formatted.replace(",", "")).format("highlight \u0001")));
                });
                ChatUtils.sendPrefixedMessage(literal, ChatUtils.createSignature(num), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                do1016();
            }
        }
    }

    public boolean shouldTrackPlayer(Player playerEntity) {
        return !PlayerUtils.hasArmor((LivingEntity) playerEntity) ? this.nakeds.getValue().booleanValue() : ClientServices.socialManager.isFriendEntity(playerEntity) ? this.friends.getValue().booleanValue() : ClientServices.socialManager.isEnemyEntity(playerEntity) ? this.enemies.getValue().booleanValue() : this.players.getValue().booleanValue();
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        this.arrayList.clear();
        this.arrayList2.clear();
    }

    public void do1016() {
        if (this.playSound.getValue().booleanValue()) {
            ClientServices.killstreakSoundManager.findSoundPlayer(this.sound.getValue()).play(this.volume.getValue().floatValue());
        }
    }
}