package me.mioclient.module.client;

import me.mioclient.event.events.entity.TotemPopEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.io.FileIO;

import me.mioclient.api.ClientServices;
import me.mioclient.manager.ConfigManager;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.io.FileIO;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.entity.TotemPopEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.client.EnableEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.io.FileUtils;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.StringSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/client/Notifications.class */
public class Notifications extends Module {
    public static IRC iRC = (IRC) ClientServices.moduleInjector.getModule(IRC.class);
    public Setting<Color> watermark = add(new ColorSetting("Watermark", new Color(-4351489, true)).setThemeSync(true));
    public Setting<Color> brackets = add(new ColorSetting("Brackets", new Color(-4351489, true)).setThemeSync(true));
    public Setting<Boolean> modules = add(new BooleanSetting("Modules", true));
    public Setting<Boolean> totemPops = add(new BooleanSetting("TotemPops", false).setVisible());
    public Setting<Boolean> self = add(new BooleanSetting("Self", false).requiresSettings(this.totemPops));
    public Setting<Color> primary = add(new ColorSetting("Primary", new Color(-2828, true), v -> this.totemPops.isShown()));
    public Setting<Color> secondary = add(new ColorSetting("Secondary", new Color(-4351489, true), v -> this.totemPops.isShown()).setThemeSync(true));
    public Setting<Boolean> sayChat = add(new BooleanSetting("SayChat", false, v -> this.totemPops.isShown()).setVisible());
    public Setting<String> path = add(new StringSetting("Path", "totempop.txt", v -> this.totemPops.isShown() && this.sayChat.isShown()));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 0.0f, 0.0f, 15.0f, v -> this.totemPops.isShown() && this.sayChat.isShown()).setSuffix("s"));
    public Setting<Boolean> random = add(new BooleanSetting("Random", true, v -> this.totemPops.isShown() && this.sayChat.isShown()));
    public Setting<Boolean> refresh = add(new BooleanSetting("Refresh", false, v -> this.totemPops.isShown() && this.sayChat.isShown()));
    public int num;
    public final List<String> list;
    public final Random random2;

    public Notifications() {
        super("Notifications", "Lets you know about what's going on in chat.", Category.CLIENT, new String[0]);
        this.num = 0;
        this.list = new ArrayList();
        this.random2 = new Random();
        setDrawn(false);
        this.sayChat.setChangeCallback(() -> {
            if (this.sayChat.getValue().booleanValue()) {
                reloadTotemPopFile();
            }
        });
        this.refresh.setChangeCallback(() -> {
            if (this.refresh.getValue().booleanValue()) {
                this.refresh.setValue(false);
                reloadTotemPopFile();
            }
        });
        this.random.setChangeCallback(() -> {
            this.num = 0;
        });
        this.path.setChangeCallback(this::reloadTotemPopFile);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        reloadTotemPopFile();
    }

    @Listen
    public void onTotemPop(TotemPopEvent playerEntityEvent) {
        boolean z = minecraftClient.player == playerEntityEvent.getPlayer();
        if (this.totemPops.getValue().booleanValue()) {
            if (!z || this.self.getValue().booleanValue()) {
                String name = z ? "You" : playerEntityEvent.getName();
                int i = playerEntityEvent.getPops();
                if (playerEntityEvent.getType() != TotemPopEvent.LogoutSpotsHelperMode.TOTEM_POP) {
                    sendTotemPopMessage(name, " has died after popping ", i);
                    return;
                }
                sendTotemPopMessage(name, new StringTemplate().arg(z ? " have" : " has").format("\u0001 popped "), i);
                if (ClientServices.socialManager.isFriend(name) || z || !this.sayChat.getValue().booleanValue() || this.list.isEmpty()) {
                    return;
                }
                executorService.submit(() -> {
                    try {
                        if (this.delay.getValue().floatValue() > 0.0f) {
                            Thread.sleep((long) ((this.delay.getValue().floatValue() * Float.intBitsToFloat(1148846080)) + this.random2.nextLong(500L)));
                        }
                        String str = null;
                        if (!this.random.getValue().booleanValue()) {
                            str = this.list.get(this.num);
                            this.num = (this.num + 1) % this.list.size();
                        } else if (this.list.size() == 1) {
                            str = this.list.get(0);
                        } else {
                            while (str == null) {
                                int nextInt = this.random2.nextInt(this.list.size());
                                if (this.num != nextInt) {
                                    this.num = nextInt;
                                    str = this.list.get(nextInt);
                                }
                            }
                        }
                        String replace = str.replace("{name}", name).replace("{totems}", Integer.toString(i));
                        if (!iRC.isToggled() || !replace.startsWith(iRC.prefix.getValue())) {
                            ChatUtils.sendChatOrCommand(replace);
                        }
                    } catch (Exception e) {
                    }
                });
            }
        }
    }

    @Listen
    public void onModuleToggle(EnableEvent enableEvent) {
        if (isNotInGame() || !this.modules.getValue().booleanValue() || (enableEvent.getModule() instanceof HUD)) {
            return;
        }
        MutableText empty = Text.empty();
        if (enableEvent.getModule().isToggled()) {
            empty.append(Text.literal("[").styled(style -> {
                return style.withColor(getPopStateColor(true).darker().hashCode());
            }));
            empty.append(Text.literal("+").styled(style2 -> {
                return style2.withColor(getPopStateColor(true).hashCode());
            }));
            empty.append(Text.literal("] ").styled(style3 -> {
                return style3.withColor(getPopStateColor(true).darker().hashCode());
            }));
        } else {
            empty.append(Text.literal("[").styled(style4 -> {
                return style4.withColor(getPopStateColor(false).darker().hashCode());
            }));
            empty.append(Text.literal("-").styled(style5 -> {
                return style5.withColor(getPopStateColor(false).hashCode());
            }));
            empty.append(Text.literal("] ").styled(style6 -> {
                return style6.withColor(getPopStateColor(false).darker().hashCode());
            }));
        }
        empty.append(Text.literal(ClientServices.notificationsHelper.getModuleDisplayName(enableEvent.getModule())));
        ClientServices.tooltipsHelper.schedule(() -> {
            ChatUtils.sendMessage((Text) empty, ChatUtils.signatureOfModule(enableEvent.getModule()));
        }, 0);
    }

    public String getTotemCountSuffix(int i) {
        return i == 1 ? " totem." : " totems.";
    }

    public void sendTotemPopMessage(String str, String str2, int i) {
        if (minecraftClient.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN) {
            return;
        }
        ChatUtils.sendPrefixedMessage(Text.empty().append(Text.literal(str).styled(style -> {
            return style.withColor(this.primary.getValue().hashCode());
        })).append(Text.literal(str2).styled(style2 -> {
            return style2.withColor(this.secondary.getValue().hashCode());
        })).append(Text.literal(String.valueOf(i)).styled(style3 -> {
            return style3.withColor(this.primary.getValue().hashCode());
        })).append(Text.literal(getTotemCountSuffix(i)).styled(style4 -> {
            return style4.withColor(this.secondary.getValue().hashCode());
        })), ChatUtils.createSignature(Math.abs(str.hashCode()) * (-1)), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
    }

    public Color getPopStateColor(boolean z) {
        return z ? new Color(0, 190, 50) : new Color(RenderConstants.num, 0, 0);
    }

    public Color getWatermarkColor() {
        return this.watermark.getValue();
    }

    public Color getBracketColor() {
        return this.brackets.getValue();
    }

    public void reloadTotemPopFile() {
        try {
            Path path1566 = FileIO.resolvePath(ConfigManager.path.resolve(this.path.getValue()), ".txt");
            if (!path1566.toFile().exists()) {
                throw new RuntimeException("Invalid totem pop file path");
            }
            this.num = 0;
            this.list.clear();
            this.list.addAll(FileUtils.readLines(path1566.toFile(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            if (this.sayChat.getValue().booleanValue()) {
                ChatUtils.sendPrefixedMessage(Text.literal("Failed to open totem pop file").styled(style -> {
                    return style.withColor(Formatting.RED);
                }), ChatUtils.createSignature(e.toString().hashCode()), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
                e.printStackTrace();
            }
        }
    }
}
