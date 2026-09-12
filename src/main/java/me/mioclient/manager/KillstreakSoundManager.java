package me.mioclient.manager;

import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.client.SoundPlayer;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.client.NotificationSound;
import me.mioclient.util.io.FileIO;
import me.mioclient.util.io.HttpRequests;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.io.FileIO;
import me.mioclient.util.io.HttpRequests;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import me.mioclient.mode.client.NotificationSound;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.client.SoundPlayer;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.client.SoundPlayer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/KillstreakSoundManager.class */
public final class KillstreakSoundManager implements ClientAccessor {
    public final Map<SoundIdentifier, SoundPlayer> map = Collections.synchronizedMap(new HashMap());
    public ObjectArrayList<SoundPlayer> objectArrayList = new ObjectArrayList<>();
    public ObjectArrayList<SoundPlayer> objectArrayList2 = new ObjectArrayList<>();
    public ObjectArrayList<SoundPlayer> objectArrayList3 = new ObjectArrayList<>();
    public ObjectArrayList<SoundPlayer> objectArrayList4 = new ObjectArrayList<>();
    public ObjectArrayList<SoundPlayer> objectArrayList5 = new ObjectArrayList<>();
    public ObjectArrayList<SoundPlayer> objectArrayList6 = new ObjectArrayList<>();
    public ObjectArrayList<SoundPlayer> objectArrayList7 = new ObjectArrayList<>();
    public static final String string = "https://me.mioclient.me/assets/killstreaks.zip";
    public static final Logger logger = LogManager.getLogger("Mio Sound System");

    public KillstreakSoundManager() {
        try {
            Path resolve = ConfigManager.path8.resolve("killstreaks");
            File file = resolve.toFile();
            if (!file.exists() || !file.isDirectory()) {
                logger.info("Downloading killstreak sounds");
                if (file.exists() && !file.delete()) {
                    throw new java.lang.RuntimeException("Failed to delete invalid killstreaks file");
                }
                if (!file.mkdirs()) {
                    throw new java.lang.RuntimeException("Failed to create the killstreaks folder");
                }
                HttpResponse httpResponse2960 = HttpRequests.sendSync(HttpRequests.newGetRequest("https://me.mioclient.me/assets/killstreaks.zip").header("User-Agent", "MioClient/2.0").timeout(Duration.ofSeconds(5L)).build(), HttpResponse.BodyHandlers.ofByteArray());
                if (httpResponse2960.statusCode() != 200) {
                    throw new java.lang.RuntimeException("Got invalid response code while downloading killstreak sounds (%d)".formatted(Integer.valueOf(httpResponse2960.statusCode())));
                }
                ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream((byte[]) httpResponse2960.body()));
                while (true) {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        break;
                    } else if (nextEntry.isDirectory()) {
                        resolve.resolve(nextEntry.getName()).toFile().mkdirs();
                    } else {
                        FileIO.writeBytes(resolve.resolve(nextEntry.getName()), zipInputStream.readAllBytes());
                    }
                }
                logger.info("Finished downloading killstreak sounds");
            }
        } catch (Exception e) {
            logger.error("Failed to download killstreak sounds");
            e.printStackTrace();
        }
    }

    public ObjectArrayList<SoundPlayer> loadKillstreakFolder(String str) {
        ObjectArrayList<SoundPlayer> objectArrayList = new ObjectArrayList<>();
        File file = ConfigManager.path8.resolve("killstreaks").resolve(str).toFile();
        if (!file.exists()) {
            logger.warn("Folder %s doesn't exist, ignoring".formatted(file.getName()));
            try {
                file.mkdir();
            } catch (Exception e) {
            }
            return objectArrayList;
        }
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("mio-fabric/killstreaks/%s isn't a folder".formatted(file.getName()));
        }
        for (File file2 : (File[]) Objects.requireNonNull(file.listFiles())) {
            if (!file2.isDirectory()) {
                if (file2.getName().endsWith(".ogg")) {
                    objectArrayList.add(new SoundPlayer(FileIO.readBytes(file2.toPath())));
                } else {
                    logger.warn("Ignoring mio-fabric/killstreaks/%s/%s. Only .ogg sounds are supported".formatted(str, file2.getName()));
                }
            }
        }
        if (objectArrayList.isEmpty()) {
            logger.warn(new StringTemplate().arg(str).format("No sounds were registered for \u0001"));
        }
        return objectArrayList;
    }

    public SoundPlayer getRandomKillstreakSound(int i) {
        if (i < 2) {
            return null;
        }
        switch (i) {
            case 2:
                ObjectArrayList<SoundPlayer> objectArrayList = this.objectArrayList;
                return (SoundPlayer) objectArrayList.get(ThreadLocalRandom.current().nextInt(this.objectArrayList.size()));
            case 3:
                ObjectArrayList<SoundPlayer> objectArrayList2 = this.objectArrayList2;
                return (SoundPlayer) objectArrayList2.get(ThreadLocalRandom.current().nextInt(this.objectArrayList2.size()));
            case 4:
                ObjectArrayList<SoundPlayer> objectArrayList3 = this.objectArrayList3;
                return (SoundPlayer) objectArrayList3.get(ThreadLocalRandom.current().nextInt(this.objectArrayList3.size()));
            case 5:
                ObjectArrayList<SoundPlayer> objectArrayList4 = this.objectArrayList4;
                return (SoundPlayer) objectArrayList4.get(ThreadLocalRandom.current().nextInt(this.objectArrayList4.size()));
            case 6:
                ObjectArrayList<SoundPlayer> objectArrayList5 = this.objectArrayList5;
                return (SoundPlayer) objectArrayList5.get(ThreadLocalRandom.current().nextInt(this.objectArrayList5.size()));
            case 7:
                ObjectArrayList<SoundPlayer> objectArrayList6 = this.objectArrayList6;
                return (SoundPlayer) objectArrayList6.get(ThreadLocalRandom.current().nextInt(this.objectArrayList6.size()));
            case 8:
                ObjectArrayList<SoundPlayer> objectArrayList7 = this.objectArrayList7;
                return (SoundPlayer) objectArrayList7.get(ThreadLocalRandom.current().nextInt(this.objectArrayList7.size()));
            default:
                if (i % 4 != 0) {
                    return null;
                }
                ObjectArrayList<SoundPlayer> objectArrayList8 = this.objectArrayList7;
                return (SoundPlayer) objectArrayList8.get(ThreadLocalRandom.current().nextInt(this.objectArrayList7.size()));
        }
    }

    public Set<SoundIdentifier> getSoundIdentifiers() {
        return this.map.keySet();
    }

    public SoundPlayer findSoundPlayer(SoundIdentifier searchIdentifier) {
        return (SoundPlayer) this.map.entrySet().stream().filter(entry -> {
            return (entry.getKey() == null || ((SoundIdentifier) entry.getKey()).getName() == null || ((SoundIdentifier) entry.getKey()).getSoundSource() == null || !((SoundIdentifier) entry.getKey()).getName().equals(searchIdentifier.getName()) || !((SoundIdentifier) entry.getKey()).getSoundSource().equals(searchIdentifier.getSoundSource())) ? false : true;
        }).map((v0) -> {
            return v0.getValue();
        }).findAny().orElse(SoundPlayer.INSTANCE);
    }

    public void playSound(SoundIdentifier searchIdentifier, float f) {
        findSoundPlayer(searchIdentifier).play(f);
    }

    public void playSoundAt(SoundIdentifier searchIdentifier, Vec3d vec3d, float f) {
        findSoundPlayer(searchIdentifier).playAt(vec3d, f);
    }

    public void reloadWorldRenderer() {
        loadKillstreakSounds();
        this.map.clear();
        for (NotificationSound notificationSound : NotificationSound.values()) {
            try {
                String string2921 = new StringTemplate().arg(notificationSound.getSearchIdentifier().getName()).format("/assets/mio/sounds/\u0001.ogg");
                InputStream resourceAsStream = getClass().getResourceAsStream(string2921);
                if (resourceAsStream == null) {
                    throw new FileNotFoundException(string2921);
                }
                this.map.put(notificationSound.getSearchIdentifier(), new SoundPlayer(resourceAsStream.readAllBytes()));
            } catch (Exception e) {
            }
        }
        for (File file : ConfigManager.path8.toFile().listFiles()) {
            if (file.isDirectory() && !file.getName().equalsIgnoreCase("killstreaks")) {
                loadSoundFolder(file.getName());
            }
        }
    }

    public void loadSoundFolder(String str) {
        for (File file : ConfigManager.path8.resolve(str).toFile().listFiles()) {
            if (!file.isDirectory() && file.getName().endsWith(".ogg")) {
                try {
                    this.map.put(new SoundIdentifier(str, file.getName().replace(".ogg", "")), new SoundPlayer(FileIO.readBytes(file.toPath())));
                } catch (Exception e) {
                    if (e instanceof InvalidIdentifierException) {
                        logger.warn("Failed to load sound: %s".formatted(e.getMessage()));
                    } else {
                        logger.warn("Failed to load sound %s: %s".formatted(file.getName(), e.toString()));
                    }
                }
            }
        }
    }

    public void loadKillstreakSounds() {
        try {
            this.objectArrayList = loadKillstreakFolder("doublekill_2");
            this.objectArrayList2 = loadKillstreakFolder("triplekill_3");
            this.objectArrayList3 = loadKillstreakFolder("dominating_4");
            this.objectArrayList4 = loadKillstreakFolder("megakill_5");
            this.objectArrayList5 = loadKillstreakFolder("unstoppable_6");
            this.objectArrayList6 = loadKillstreakFolder("wickedsick_7");
            this.objectArrayList7 = loadKillstreakFolder("monsterkill_8up");
        } catch (Exception e) {
            logger.warn("Failed to load killstreak sounds");
        }
    }
}
