package me.mioclient.module.client;

import me.mioclient.event.events.client.ClientTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import java.util.concurrent.CompletableFuture;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.event.events.client.ClientTickEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.client.network.ServerInfo;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/client/Discord.class */
public class Discord extends Module {
    public Setting<Boolean> uid = add(new BooleanSetting("UID", false));
    public Setting<Boolean> server = add(new BooleanSetting("Server", false));
    public CompletableFuture<IPCClient> completableFuture;
    public long num;
    public final Stopwatch stopwatch;

    public Discord() {
        super("Discord", "Displays me.mioclient.me as your discord activity.", Category.CLIENT, new String[0]);
        this.num = System.currentTimeMillis();
        this.stopwatch = new Stopwatch();
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        IPCClient iPCClient = new IPCClient(1162833254580228176L);
        this.completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                iPCClient.connect(new DiscordBuild[0]);
                return iPCClient;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(th -> {
            if (!isToggled()) {
                return null;
            }
            toggle();
            return null;
        });
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (!this.completableFuture.isDone() || this.completableFuture.isCancelled()) {
            return;
        }
        try {
            this.completableFuture.get().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listen
    public void onClientTick(ClientTickEvent fontsEvent) {
        try {
            if (this.completableFuture.isDone() && this.stopwatch.hasElapsedMs(2500L)) {
                this.stopwatch.reset();
                IPCClient now = this.completableFuture.getNow(null);
                if (now == null) {
                    return;
                }
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setLargeImage("https://me.mioclient.me/assets/dcrpc3.png", getClientVersion());
                builder.setStartTimestamp(this.num);
                if (this.uid.getValue().booleanValue()) {
                    builder.setDetails(new StringTemplate().arg(ClientServices.welcomerHelper.getRank()).format("uid \u0001"));
                }
                if (this.server.getValue().booleanValue()) {
                    ServerInfo serverInfo = null;
                    String str = "Not playing";
                    if (!isNotInGame()) {
                        serverInfo = minecraftClient.player.networkHandler.getServerInfo();
                        str = new StringTemplate().arg((minecraftClient.isInSingleplayer() || serverInfo == null) ? "singleplayer" : serverInfo.address).format("Playing \u0001");
                    }
                    builder.setState(str);
                    if (serverInfo != null && serverInfo.players != null) {
                        builder.setParty(Integer.toString(str.hashCode()), serverInfo.players.online(), serverInfo.players.max(), 0);
                    }
                }
                now.sendRichPresence(builder.build());
            }
        } catch (Exception e) {
        }
    }

    public String getClientVersion() {
        return "Mio v2.1.7";
    }
}
