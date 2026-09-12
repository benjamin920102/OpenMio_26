package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.io.FileIO;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.manager.ConfigManager;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.io.FileIO;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.IRC;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.StringSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/Spammer.class */
public class Spammer extends Module {
    public Setting<String> path = add(new StringSetting("Path", "spammer.txt"));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 5.0f, 0.0f, 300.0f).setSuffix("s"));
    public Setting<Boolean> refresh = add(new BooleanSetting("Refresh", false));
    public Setting<Boolean> smartDelay = add(new BooleanSetting("SmartDelay", true));
    public Setting<Boolean> random = add(new BooleanSetting("Random", false));
    public Setting<Boolean> autoOff = add(new BooleanSetting("AutoOff", false));
    public static IRC iRC = (IRC) ClientServices.moduleInjector.getModule(IRC.class);
    public final Random random2;
    public final List<String> list;
    public final Stopwatch stopwatch;
    public int current;

    public Spammer() {
        super("Spammer", "Spams messages from a selected text file.", Category.MISC, new String[0]);
        this.random2 = new Random();
        this.list = new ArrayList();
        this.stopwatch = new Stopwatch();
        this.current = 0;
        this.path.setChangeCallback(this::recalculateLayout);
        this.refresh.setChangeCallback(() -> {
            if (this.refresh.getValue().booleanValue()) {
                this.refresh.setValue(false);
                recalculateLayout();
            }
        });
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        recalculateLayout();
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if ((sendImmediatelyEvent.getPacket() instanceof ChatMessageC2SPacket) && this.smartDelay.getValue().booleanValue()) {
            this.stopwatch.reset();
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        int i;
        if (!this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS) || this.list.isEmpty()) {
            return;
        }
        List<String> list = this.list;
        if (!this.random.getValue().booleanValue() || this.list.size() <= 1) {
            i = this.current;
        } else {
            i = this.random2.nextInt(this.list.size() - 1);
        }
        String str = list.get(i);
        if (!iRC.isToggled() || !str.startsWith(iRC.prefix.getValue())) {
            ChatUtils.sendChatOrCommand(str);
        }
        this.current = (this.current + 1) % this.list.size();
        this.stopwatch.reset();
        if (this.autoOff.getValue().booleanValue()) {
            toggle();
        }
    }

    public void recalculateLayout() {
        try {
            Path path1566 = FileIO.resolvePath(ConfigManager.path3.resolve(this.path.getValue()), ".txt");
            if (!path1566.toFile().exists()) {
                throw new RuntimeException("Invalid spammer file path");
            }
            this.current = 0;
            this.list.clear();
            this.list.addAll(FileUtils.readLines(path1566.toFile(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            if (isNotInGame()) {
                return;
            }
            ChatUtils.sendPrefixedMessage(Text.literal("Failed to open spammer file").styled(style -> {
                return style.withColor(Formatting.RED);
            }), ChatUtils.createSignature(e.toString().hashCode()), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
        }
    }
}
