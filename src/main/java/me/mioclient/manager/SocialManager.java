package me.mioclient.manager;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.setting.EnumJsonConverter;
import me.mioclient.util.registry.CollectionRegistry;
import me.mioclient.util.registry.NamedRegistryList;

import me.mioclient.mode.render.NameTagsHelperMode;

import me.mioclient.api.ClientAccessor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.mioclient.module.client.Colors;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import me.mioclient.ducks.DuckPlayerEntity;

public final class SocialManager extends NamedRegistryList<SocialEntry> implements ClientAccessor, JsonConfigurable {
    public static final Colors colors = (Colors) ClientServices.moduleInjector.getModule(Colors.class);
    public static final EnumJsonConverter enumSettingConverter = new EnumJsonConverter(NameTagsHelperMode.class);

    public boolean isFriend(String str) {
        return getRole(str) == NameTagsHelperMode.FRIEND;
    }

    public boolean isFriendEntity(Player playerEntity) {
        return ((DuckPlayerEntity) playerEntity).mio$getRole() == NameTagsHelperMode.FRIEND;
    }

    public boolean isEnemy(String str) {
        return getRole(str) == NameTagsHelperMode.ENEMY;
    }

    public boolean isEnemyEntity(Player playerEntity) {
        return ((DuckPlayerEntity) playerEntity).mio$getRole() == NameTagsHelperMode.ENEMY;
    }

    public void addFriend(String str) {
        removePlayer(str);
        register(new SocialEntry(str, NameTagsHelperMode.FRIEND));
    }

    public void addEnemy(String str) {
        removePlayer(str);
        register(new SocialEntry(str, NameTagsHelperMode.ENEMY));
    }

    public NameTagsHelperMode getRole(String str) {
        return (NameTagsHelperMode) findFirst(mixinPlayerEntityData -> {
            return mixinPlayerEntityData.getName().equalsIgnoreCase(str);
        }).map((v0) -> {
            return v0.getHelperMode();
        }).orElse(null);
    }

    public List<String> getPlayersByRole(NameTagsHelperMode nameTagsHelperMode) {
        ArrayList arrayList = new ArrayList();
        synchronized (((List) this.registry)) {
            ((List<SocialEntry>) this.registry).forEach(mixinPlayerEntityData -> {
                if (mixinPlayerEntityData.getHelperMode() == nameTagsHelperMode) {
                    arrayList.add(mixinPlayerEntityData.getName());
                }
            });
        }
        return arrayList;
    }

    public boolean removePlayer(String str) {
        setPlayerRole(str, (NameTagsHelperMode) null);
        return ((List<SocialEntry>) this.registry).removeIf(mixinPlayerEntityData -> {
            return mixinPlayerEntityData.getName().equalsIgnoreCase(str);
        });
    }

    public Color getFriendColor() {
        return colors.friendColor.getValue();
    }

    public Color getEnemyColor() {
        return colors.enemyColor.getValue();
    }

    public Color getSocialColor(String str, Color color) {
        return isFriend(str) ? getFriendColor() : isEnemy(str) ? getEnemyColor() : color;
    }

    public void setPlayerRole(String str, NameTagsHelperMode nameTagsHelperMode) {
        if (isNotInGame()) {
            return;
        }
        for (AbstractClientPlayer nameTagsHelper : minecraftClient.level.players()) {
            if (nameTagsHelper.getGameProfile().name().equalsIgnoreCase(str) && (nameTagsHelper instanceof DuckPlayerEntity)) {
                ((DuckPlayerEntity) (Object) nameTagsHelper).mio$setRole(nameTagsHelperMode);
                return;
            }
        }
    }

    @Override // me.mioclient.util.registry.NamedRegistryList, me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is532, reason: merged with bridge method [inline-methods] */
    public boolean register(SocialEntry mixinPlayerEntityData) {
        setPlayerRole(mixinPlayerEntityData.getRawName(), mixinPlayerEntityData.getHelperMode());
        return super.register(mixinPlayerEntityData);
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (SocialEntry mixinPlayerEntityData : (List<SocialEntry>) this.registry) {
            JsonElement jsonObject2 = new JsonObject();
            ((JsonObject) jsonObject2).addProperty("name", mixinPlayerEntityData.getRawName());
            ((JsonObject) jsonObject2).add("role", enumSettingConverter.doForward(mixinPlayerEntityData.getHelperMode()));
            jsonArray.add(jsonObject2);
        }
        jsonObject.add("socials", (JsonElement) jsonArray);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.getAsJsonObject().has("socials")) {
            Iterator it = jsonElement.getAsJsonObject().getAsJsonArray("socials").iterator();
            while (it.hasNext()) {
                JsonObject asJsonObject = ((JsonElement) it.next()).getAsJsonObject();
                if (asJsonObject.has("name") && asJsonObject.has("role")) {
                    register(new SocialEntry(asJsonObject.get("name").getAsString(), (NameTagsHelperMode) enumSettingConverter.doBackward(asJsonObject.get("role"))));
                }
            }
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "socials.json";
    }
}
