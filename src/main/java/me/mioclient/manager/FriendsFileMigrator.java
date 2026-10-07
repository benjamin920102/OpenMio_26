package me.mioclient.manager;

import me.mioclient.api.ClientServices;

import me.mioclient.util.io.FileIO;

import me.mioclient.api.ClientAccessor;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.nio.file.Path;
import java.util.Iterator;

public final class FriendsFileMigrator implements ClientAccessor {
    public static void run() {
        Path resolve = ConfigManager.path.resolve("friends.json");
        Path resolve2 = ConfigManager.path.resolve("socials.json");
        if (!resolve.toFile().exists() || resolve2.toFile().exists()) {
            return;
        }
        Iterator it = JsonParser.parseString(FileIO.readString(resolve)).getAsJsonObject().getAsJsonArray("friends").iterator();
        while (it.hasNext()) {
            JsonElement jsonElement = (JsonElement) it.next();
            ClientServices.socialManager.addFriend(jsonElement.getAsString());
        }
        FileIO.writeString(resolve2, gson.toJson(ClientServices.socialManager.toJson()));
        resolve.toFile().delete();
    }
}
