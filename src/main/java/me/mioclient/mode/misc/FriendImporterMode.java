package me.mioclient.mode.misc;

import me.mioclient.api.ClientAccessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtIo;

public enum FriendImporterMode {
    FUTURE("Future") { // from class: me.mioclient.Mode.Inner_2
        @Override // me.mioclient.Mode
        public List<String> getImportedFriends() {
            try {
                String property = System.getProperty("user.home");
                if (!property.endsWith(File.separator)) {
                    property = property + File.separator;
                }
                File file = new File(property + "Future%sfriends.json".formatted(File.separator));
                if (!file.exists() || !file.isFile()) {
                    throw new FileNotFoundException();
                }
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] readAllBytes = fileInputStream.readAllBytes();
                fileInputStream.close();
                if (readAllBytes.length == 0) {
                    return Collections.emptyList();
                }
                ArrayList arrayList = new ArrayList();
                JsonArray jsonArray = (JsonArray) ClientAccessor.gson.fromJson(new String(readAllBytes, StandardCharsets.UTF_8), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject asJsonObject = jsonArray.get(i).getAsJsonObject();
                    if (asJsonObject.has("friend-label")) {
                        String asString = asJsonObject.get("friend-label").getAsString();
                        if (!arrayList.contains(asString)) {
                            arrayList.add(asString);
                        }
                    }
                }
                return arrayList;
            } catch (Exception e) { throw new java.lang.RuntimeException(e); }
        }
    },
    RUSHERHACK("RusherHack") { // from class: me.mioclient.Mode.Inner
        @Override // me.mioclient.Mode
        public List<String> getImportedFriends() {
            try {
                File file = new File("rusherhack" + File.separator + "config" + File.separator + "relations.json");
                if (!file.exists() || !file.isFile()) {
                    throw new FileNotFoundException();
                }
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] readAllBytes = fileInputStream.readAllBytes();
                fileInputStream.close();
                if (readAllBytes.length == 0) {
                    return Collections.emptyList();
                }
                ArrayList arrayList = new ArrayList();
                JsonArray jsonArray = (JsonArray) ClientAccessor.gson.fromJson(new String(readAllBytes, StandardCharsets.UTF_8), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject asJsonObject = jsonArray.get(i).getAsJsonObject();
                    if (asJsonObject.has("username") && asJsonObject.has("state")) {
                        String asString = asJsonObject.get("username").getAsString();
                        if ("FRIEND".equalsIgnoreCase(asJsonObject.get("state").getAsString()) && !arrayList.contains(asString)) {
                            arrayList.add(asString);
                        }
                    }
                }
                return arrayList;
            } catch (Exception e) { throw new java.lang.RuntimeException(e); }
        }
    },
    METEOR("Meteor") { // from class: me.mioclient.Mode.Inner_3
        @Override // me.mioclient.Mode
        public List<String> getImportedFriends() {
            try {
                File file = new File("meteor-client" + File.separator + "friends.nbt");
                if (!file.exists() || !file.isFile() || file.length() == 0) {
                    throw new FileNotFoundException();
                }
                ArrayList arrayList = new ArrayList();
                CompoundTag root = NbtIo.read(file.toPath());
                if (root == null) {
                    return Collections.emptyList();
                }
                ListTag friends = root.getListOrEmpty("friends");
                Iterator it = friends.iterator();
                while (it.hasNext()) {
                    CompoundTag nbtCompound = (CompoundTag)((Tag) it.next());
                    if (nbtCompound.contains("name")) {
                        String string = nbtCompound.getStringOr("name", "");
                        if (!string.isEmpty() && !arrayList.contains(string)) {
                            arrayList.add(string);
                        }
                    }
                }
                return arrayList;
            } catch (Exception e) { throw new java.lang.RuntimeException(e); }
        }
    };

    public final String string;

    FriendImporterMode(String str) {
        this.string = str;
    }

    public String getImporterName() {
        return this.string;
    }

    public List<String> getImportedFriends() {
        try {
            return Collections.emptyList();
        } catch (Exception e) { throw new java.lang.RuntimeException(e); }
    }
}
