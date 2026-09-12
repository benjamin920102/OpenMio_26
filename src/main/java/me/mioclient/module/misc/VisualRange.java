package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.player.PlayerUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.LoadChunkFromPacketEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/VisualRange.class */
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
            for (AbstractClientPlayerEntity abstractClientPlayerEntity : minecraftClient.world.getPlayers()) {
                if (!abstractClientPlayerEntity.getName().equals(minecraftClient.player.getName()) && shouldTrackPlayer((PlayerEntity) abstractClientPlayerEntity)) {
                    this.arrayList2.add(abstractClientPlayerEntity.getName().getString());
                }
            }
            if (!this.arrayList.equals(this.arrayList2)) {
                Iterator<String> it = this.arrayList2.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    if (!this.arrayList.contains(next) && this.join.getValue().booleanValue()) {
                        Color color530 = ClientServices.socialManager.getSocialColor(next, Color.WHITE);
                        ChatUtils.sendPrefixedMessage(Text.literal("%s%s entered visual range.".formatted(next, Formatting.WHITE)).styled(style -> {
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
                        ChatUtils.sendPrefixedMessage(Text.literal("%s%s left visual range.".formatted(next2, Formatting.WHITE)).styled(style2 -> {
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
                    MutableText literal = Text.literal("Found a sign: ");
                    boolean z = true;
                    for (Text text : ((SignBlockEntity) blockEntity).getFrontText().getMessages(false)) {
                        if (!Formatting.strip(text.getString()).isBlank()) {
                            literal.append(text);
                            literal.append(" ");
                            z = false;
                        }
                    }
                    if (z) {
                        literal.append("<empty>");
                    }
                    literal.styled(style -> {
                        return style.withFormatting(Formatting.WHITE);
                    });
                    String formatted = "%d, %d, %d".formatted(Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()));
                    HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(new StringTemplate().arg(formatted).format("at \u0001")));
                    literal.styled(style2 -> {
                        return style2.withHoverEvent(hoverEvent);
                    });
                    literal.styled(style3 -> {
                        return style3.withClickEvent(ChatUtils.suggestCommand(new StringTemplate().arg(formatted.replace(",", "")).format("highlight \u0001")));
                    });
                    ChatUtils.sendMessage((Text) literal, ChatUtils.createSignature(i));
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
        EntitySpawnS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof EntitySpawnS2CPacket ? (EntitySpawnS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof EntitySpawnS2CPacket) {
            EntitySpawnS2CPacket entitySpawnS2CPacket = packet904;
            if (entitySpawnS2CPacket.getEntityType() == EntityType.ENDER_PEARL && this.enderPearls.getValue().booleanValue()) {
                Text literal = Text.literal("Ender Pearl entered visual range.");
                String formatted = "%.0f, %.0f, %.0f".formatted(Double.valueOf(entitySpawnS2CPacket.getX()), Double.valueOf(entitySpawnS2CPacket.getY()), Double.valueOf(entitySpawnS2CPacket.getZ()));
                MutableText literal2 = Text.literal(new StringTemplate().arg(formatted).format("at \u0001"));
                ((MutableText) literal).styled(style -> {
                    return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, literal2));
                });
                ((MutableText) literal).styled(style2 -> {
                    return style2.withClickEvent(ChatUtils.suggestCommand(new StringTemplate().arg(formatted.replace(",", "")).format("highlight \u0001")));
                });
                ChatUtils.sendPrefixedMessage(literal, ChatUtils.createSignature(num), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                do1016();
            }
        }
    }

    public boolean shouldTrackPlayer(PlayerEntity playerEntity) {
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
