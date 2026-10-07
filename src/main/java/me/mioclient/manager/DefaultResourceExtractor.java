package me.mioclient.manager;

import me.mioclient.loader.EasterEggMessages;

import me.mioclient.util.io.FileIO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;

public final class DefaultResourceExtractor {
    public final HashMap<String, String> hashMap = new HashMap<>();

    public DefaultResourceExtractor() {
        this.hashMap.put("textures/overlay.png", "assets/mio/textures/overlay.png");
        this.hashMap.put("textures/skin_protect.png", "assets/mio/textures/skin_protect.png");
        this.hashMap.put("textures/shine.png", "assets/mio/textures/shine.png");
    }

    public void extractDefaults() {
        this.hashMap.forEach(this::extractResource);
    }

    public void extractResource(String str, String str2) {
        Path resolve = ConfigManager.path.resolve(str);
        if (resolve.toFile().exists()) {
            return;
        }
        try {
            InputStream resourceAsStream = EasterEggMessages.class.getClassLoader().getResourceAsStream(str2);
            if (resourceAsStream == null) {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            } else {
                try {
                    FileIO.writeBytes(resolve, resourceAsStream.readAllBytes());
                    if (resourceAsStream != null) {
                        resourceAsStream.close();
                    }
                } finally {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
