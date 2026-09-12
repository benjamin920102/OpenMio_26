package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.client.SoundPlayer;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.io.FileIO;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.manager.ConfigManager;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.io.FileIO;
import me.mioclient.util.player.PlayerUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.client.SoundPlayer;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.player.AttackHookPostEvent;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.IRC;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.ThreadSafeRandom;
import org.apache.commons.io.FileUtils;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.setting.StringSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/KillEffects.class */
public class KillEffects extends Module {
    public Setting<Boolean> killSound = add(new BooleanSetting("KillSound", false).setVisible());
    public Setting<Boolean> players = add(new BooleanSetting("Players", false, v -> this.killSound.isShown()).setVisible());
    public Setting<SoundIdentifier> sound = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "css"), v -> this.killSound.isShown() && this.players.isShown()));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 0.5f, 0.0f, 1.0f, v -> this.killSound.isShown() && this.players.isShown()));
    public Setting<Boolean> self = add(new BooleanSetting("Self", false, v -> this.killSound.isShown()).setVisible());
    public Setting<SoundIdentifier> sound2 = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "bodysplat"), v -> this.killSound.isShown() && this.self.isShown()));
    public Setting<Float> volume3 = add(new NumberSetting<>("Volume", 0.5f, 0.0f, 1.0f, v -> this.killSound.isShown() && this.self.isShown()));
    public Setting<Boolean> killStreak = add(new BooleanSetting("KillStreak", false).setVisible());
    public Setting<Float> volume2 = add(new NumberSetting<>("Volume", 0.4f, 0.0f, 1.0f, v -> this.killStreak.isShown()));
    public Setting<Boolean> thunder = add(new BooleanSetting("Thunder", true).setVisible());
    public Setting<Integer> lightnings = add(new NumberSetting<>("Lightnings", 1, 1, 4).requiresSettings(this.thunder));
    public Setting<Boolean> self2 = add(new BooleanSetting("Self", false, v -> this.thunder.isShown()));
    public Setting<Boolean> ignoreNakeds = add(new BooleanSetting("IgnoreNakeds", true).requiresSettings(this.thunder));
    public Setting<Boolean> ashes = add(new BooleanSetting("Ashes", false).setVisible());
    public Setting<Boolean> self3 = add(new BooleanSetting("Self", false).requiresSettings(this.ashes));
    public Setting<Boolean> autoEZ = add(new BooleanSetting("AutoEZ", false).setVisible());
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 0.0f, 0.0f, 5.0f).setSuffix("s").requiresSettings(this.autoEZ));
    public Setting<String> path = add(new StringSetting("Path", "autoez.txt").requiresSettings(this.autoEZ));
    public Setting<Boolean> random = add(new BooleanSetting("Random", true).requiresSettings(this.autoEZ));
    public Setting<Boolean> refresh = add(new BooleanSetting("Refresh", false).requiresSettings(this.autoEZ));
    public static IRC iRC = (IRC) ClientServices.moduleInjector.getModule(IRC.class);
    public static final Random random2 = new ThreadSafeRandom(System.currentTimeMillis());
    public final ConcurrentHashMap<UUID, Integer> concurrentHashMap;
    public final ObjectList<UUID> objectList;
    public int num;
    public int num2;
    public List<String> list;

    public KillEffects() {
        super("KillEffects", "Does various things when someone dies.", Category.MISC, new String[0]);
        this.concurrentHashMap = new ConcurrentHashMap<>();
        this.objectList = ObjectLists.synchronize(new ObjectArrayList());
        this.num = 0;
        this.num2 = 0;
        this.list = Collections.synchronizedList(new ArrayList());
        this.players.setConfigName("KillSoundPlayers");
        this.sound.setConfigName("KillSoundPlayersSound");
        this.volume.setConfigName("KillSoundPlayersVolume");
        this.self.setConfigName("KillSoundSelf");
        this.sound2.setConfigName("KillSoundSelfSound");
        this.volume3.setConfigName("KillSoundSelfVolume");
        this.self2.setConfigName("ThunderSelf");
        this.volume2.setConfigName("KillStreakSoundVolume");
        this.self3.setConfigName("AshesSelf");
        this.autoEZ.setChangeCallback(() -> {
            if (this.autoEZ.getValue().booleanValue()) {
                loadAutoEZLines(false);
            }
        });
        this.refresh.setChangeCallback(() -> {
            if (this.refresh.getValue().booleanValue()) {
                loadAutoEZLines(true);
                this.refresh.setValue(false);
            }
        });
        this.random.setChangeCallback(() -> {
            this.num2 = 0;
        });
        this.path.setChangeCallback(() -> {
            loadAutoEZLines(true);
        });
        this.path.setConfigName("AutoEZPath");
        this.random.setConfigName("AutoEZRandom");
        this.refresh.setConfigName("AutoEZRefresh");
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.num = 0;
        loadAutoEZLines(true);
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        this.num = 0;
    }

    @Listen
    public void onAttackHookPost(AttackHookPostEvent attackHookPostEvent) {
        PlayerEntity playerEntity;
        if (attackHookPostEvent.getPhase() == KeyPearlMode.Post || isNotInGame()) {
            return;
        }
        PlayerEntity entity181 = (attackHookPostEvent.getEntity()) instanceof PlayerEntity ? (PlayerEntity) (attackHookPostEvent.getEntity()) : null;
        if (!(entity181 instanceof PlayerEntity) || (playerEntity = entity181) == minecraftClient.player) {
            return;
        }
        if (ClientServices.socialManager.isFriend(playerEntity.getName().getString()) || EntityUtil.getEntityHealth((Entity) playerEntity) <= 0.0f) {
            return;
        }
        try {
            this.concurrentHashMap.put(playerEntity.getUuid(), Integer.valueOf(minecraftClient.player.age));
        } catch (Exception e) {
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof DeathMessageS2CPacket) {
            this.num = -1;
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        PlayerEntity playerByUuid = null;
        String string3005;
        if (isNotInGame()) {
            return;
        }
        if (this.num == -1) {
            this.num = 0;
            if (this.killSound.getValue().booleanValue() && this.self.getValue().booleanValue() && this.volume3.getValue().floatValue() > 0.0f) {
                SoundPlayer searchHelper4_42970 = ClientServices.killstreakSoundManager.findSoundPlayer(this.sound2.getValue());
                if (searchHelper4_42970 != null) {
                    searchHelper4_42970.play(this.volume3.getValue().floatValue());
                }
            }
            if (this.thunder.getValue().booleanValue() && this.self2.getValue().booleanValue()) {
                spawnLightning(minecraftClient.player.getPos());
            }
            if (this.ashes.getValue().booleanValue() && this.self3.getValue().booleanValue()) {
                spawnAshParticles(minecraftClient.player.getPos());
            }
        }
        this.objectList.clear();
        synchronized (this.concurrentHashMap) {
            Iterator it = this.concurrentHashMap.keySet().iterator();
            while (it.hasNext()) {
                UUID uuid = (UUID) it.next();
                try {
                    playerByUuid = minecraftClient.world.getPlayerByUuid(uuid);
                } catch (Exception e) {
                }
                if (playerByUuid == null) {
                    this.objectList.add(uuid);
                } else if (minecraftClient.player.age - this.concurrentHashMap.get(uuid).intValue() > 20) {
                    this.objectList.add(uuid);
                } else if (playerByUuid.getHealth() <= 0.0f) {
                    this.objectList.add(uuid);
                    this.num++;
                    if (this.autoEZ.getValue().booleanValue() && (string3005 = getNextAutoEZMessage()) != null) {
                        String replace = string3005.replace("{name}", playerByUuid.getName().getString());
                        if (!iRC.isToggled() || !replace.startsWith(iRC.prefix.getValue())) {
                            Runnable runnable = () -> {
                                ChatUtils.sendChatOrCommand(replace);
                            };
                            if (this.delay.isAtMinimum()) {
                                runnable.run();
                            } else {
                                ClientServices.tooltipsHelper.schedule(runnable, (int) (this.delay.getValue().floatValue() / Float.intBitsToFloat(1028443341)));
                            }
                        }
                    }
                    if (this.thunder.getValue().booleanValue() && (PlayerUtils.hasArmor((LivingEntity) playerByUuid) || !this.ignoreNakeds.getValue().booleanValue())) {
                        spawnLightning(playerByUuid.getPos());
                    }
                    if (this.ashes.getValue().booleanValue()) {
                        spawnAshParticles(playerByUuid.getPos());
                    }
                    if (this.killSound.getValue().booleanValue() && this.players.getValue().booleanValue() && this.volume.getValue().floatValue() > 0.0f) {
                        SoundPlayer searchHelper4_429702 = ClientServices.killstreakSoundManager.findSoundPlayer(this.sound.getValue());
                        if (searchHelper4_429702 != null) {
                            searchHelper4_429702.play(this.volume.getValue().floatValue());
                        }
                    }
                    if (this.killStreak.getValue().booleanValue() && this.num >= 2 && this.volume2.getValue().floatValue() > 0.0f) {
                        SoundPlayer searchHelper4_42968 = ClientServices.killstreakSoundManager.getRandomKillstreakSound(this.num);
                        if (searchHelper4_42968 != null) {
                            searchHelper4_42968.play(this.volume2.getValue().floatValue());
                        }
                    }
                }
            }
        }
        synchronized (this.objectList) {
            try {
                Iterator it2 = this.objectList.iterator();
                while (it2.hasNext()) {
                    this.concurrentHashMap.remove((UUID) it2.next());
                }
            } catch (Exception e2) {
            }
        }
    }

    public void registerAttackTarget(PlayerEntity playerEntity) {
        if (isNotInGame() || playerEntity == minecraftClient.player) {
            return;
        }
        if (ClientServices.socialManager.isFriend(playerEntity.getName().getString())) {
            return;
        }
        try {
            this.concurrentHashMap.put(playerEntity.getUuid(), Integer.valueOf(minecraftClient.player.age));
        } catch (Exception e) {
        }
    }

    public void loadAutoEZLines(boolean z) {
        if (this.autoEZ.getValue().booleanValue() || z) {
            Path path1566 = FileIO.resolvePath(ConfigManager.path4.resolve(this.path.getValue()), ".txt");
            try {
                if (!path1566.toFile().exists()) {
                    throw new IOException("AutoEZ file %s not found".formatted(this.path.getValue()));
                }
                this.list.clear();
                this.list.addAll(FileUtils.readLines(path1566.toFile(), StandardCharsets.UTF_8));
                this.num2 = 0;
            } catch (Exception e) {
                if (this.autoEZ.getValue().booleanValue()) {
                    ChatUtils.sendPrefixedMessage(Text.literal(new StringTemplate().arg(String.valueOf(e)).format("Failed to update AutoEZ lines: \u0001")).styled(style -> {
                        return style.withColor(Formatting.RED);
                    }), ChatUtils.createSignature(-9634482), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
                    try {
                        Files.createFile(path1566, new FileAttribute[0]);
                    } catch (Exception e2) {
                    }
                }
            }
        }
    }

    public String getNextAutoEZMessage() {
        try {
            if (this.list.isEmpty()) {
                return null;
            }
            if (this.random.getValue().booleanValue()) {
                List<String> list = this.list;
                return list.get(ThreadLocalRandom.current().nextInt(this.list.size()));
            }
            List<String> list2 = this.list;
            int i = this.num2;
            this.num2 = i + 1;
            return list2.get(i % this.list.size());
        } catch (Exception e) {
            return null;
        }
    }

    public void spawnLightning(Vec3d vec3d) {
        for (int i = 0; i < this.lightnings.getValue().intValue(); i++) {
            Entity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, minecraftClient.world);
            ((LightningEntity) lightningEntity).setPosition(vec3d);
            ((LightningEntity) lightningEntity).refreshPositionAfterTeleport(vec3d);
            minecraftClient.world.addEntity(lightningEntity);
        }
    }

    public void spawnAshParticles(Vec3d vec3d) {
        for (int i = 0; i < random2.nextInt(35) + 25; i++) {
            Particle addParticle = minecraftClient.particleManager.addParticle((ParticleEffect) (random2.nextBoolean() ? ParticleTypes.ASH : ParticleTypes.WHITE_ASH), vec3d.getX() + (random2.nextGaussian() * Double.longBitsToDouble(4597454643433098445L)), (vec3d.getY() + Double.longBitsToDouble(4611686018427387904L)) - (random2.nextGaussian() * Double.longBitsToDouble(4602949035107339469L)), vec3d.getZ() + (random2.nextGaussian() * Double.longBitsToDouble(4597454643433098445L)), 0.0d, Double.longBitsToDouble(-4631501856787818086L) - (random2.nextGaussian() * Double.longBitsToDouble(4602678819172646912L)), 0.0d);
            if (addParticle != null) {
                addParticle.maxAge += 15;
            }
        }
    }
}
