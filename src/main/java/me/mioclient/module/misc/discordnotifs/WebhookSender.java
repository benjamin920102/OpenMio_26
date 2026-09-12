package me.mioclient.module.misc.discordnotifs;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;

import me.mioclient.util.io.HttpRequests;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.io.HttpRequests;

import me.mioclient.event.EventBus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WebhookSender.class */
public class WebhookSender implements ClientAccessor, JsonConfigurable {
    public String string = "";

    public WebhookSender() {
        eventBus.register(this);
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", this.string);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        this.string = jsonElement.getAsJsonObject().get("url").getAsString();
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "webhook.json";
    }

    public void setWebhookUrl(String str) {
        this.string = str;
    }

    public String getWebhookUrl() {
        return this.string;
    }

    public void joinChannel(String str) {
        executorService.submit(() -> {
            try {
                JsonElement jsonObject = new JsonObject();
                ((JsonObject) jsonObject).addProperty("content", str);
                HttpClient.newHttpClient().send(HttpRequests.getBuilder2964(ClientServices.discordNotifsHelper.getWebhookUrl(), HttpRequest.BodyPublishers.ofString(ClientAccessor.gson.toJson(jsonObject))).header("User-Agent", "MioClient/2.0").headers(new String[]{"Content-Type", "application/json"}).build(), HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
            }
        });
    }
}
