package me.mioclient.util.client;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.command.ClientCommandRegistry;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import me.mioclient.module.Module;
import me.mioclient.module.client.Notifications;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import me.mioclient.ducks.DuckStyle;

public class ChatUtils implements ClientAccessor {
    public static final Notifications notifications = (Notifications) ClientServices.moduleInjector.getModule(Notifications.class);
    public static final GuiMessageTag GuiMessageTag = new GuiMessageTag(-1, (GuiMessageTag.Icon) null, Component.empty(), "Mio");
    public static final Pattern pattern = Pattern.compile("^(\\w{3,16}) -> (\\w{3,16}): (.*)");
    public static final List<Pattern> list = List.of(Pattern.compile("^From (\\w{3,16}): (.*)"), Pattern.compile("^from (\\w{3,16}): (.*)"), Pattern.compile("^(\\w{3,16}) whispers: (.*)"), Pattern.compile("^(\\w{3,16}) -> me (.*)"), Pattern.compile("^(\\w{3,16}) whispers to you: (.*)"), Pattern.compile("^(\\w{3,16}) says: (.*)"), Pattern.compile("^(\\w{3,16}) Ð¿Ð¸ÑˆÐµÑ‚: (.*)"), pattern);
    public static final List<Pattern> list2 = List.of(Pattern.compile("^To (\\w{3,16}): (.*)"), Pattern.compile("^to (\\w{3,16}): (.*)"), Pattern.compile("^\\[me -> (\\w{3,16})\\] (.*)"), Pattern.compile("^You whisper to (\\w{3,16}): (.*)"), Pattern.compile("^Ðš (\\w{3,16}): (.*)"), pattern);

    
    public static enum MixinClientConnectionMode {
        mixinClientConnectionMode {
            @Override // me.mioclient.util.client.ChatUtils.MixinClientConnectionMode
            public Color getColor() {
                return new Color(225, 10, 10);
            }
        },
        mixinClientConnectionMode2 {
            @Override // me.mioclient.util.client.ChatUtils.MixinClientConnectionMode
            public Color getColor() {
                return new Color(225, 170, 10);
            }
        },
        mixinClientConnectionMode3 {
            @Override // me.mioclient.util.client.ChatUtils.MixinClientConnectionMode
            public Color getColor() {
                return new Color(225, 225, 225);
            }
        };

        public Color getColor() {
            return null;
        }
    }

    public static boolean isIncomingWhisper(String str) {
        if (str.toLowerCase(Locale.ROOT).startsWith(minecraftClient.getUser().getName().toLowerCase(Locale.ROOT))) {
            return false;
        }
        if (str.contains(" ÑˆÐµÐ¿Ñ‡ÐµÑ‚: ") || str.contains(" ÑˆÐµÐ¿Ñ‡ÐµÑ‚ Ñ‚ÐµÐ±Ðµ: ")) {
            return true;
        }
        Iterator<Pattern> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().matcher(str).find()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOutgoingWhisper(String str) {
        boolean startsWith = str.toLowerCase(Locale.ROOT).startsWith(minecraftClient.getUser().getName().toLowerCase(Locale.ROOT));
        for (Pattern pattern2 : list2) {
            if (pattern2 != pattern || startsWith) {
                if (pattern2.matcher(str).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static MessageSignature createSignature(int i) {
        return new MessageSignature(ByteBuffer.allocate(256).putInt(i).array());
    }

    public static MessageSignature signatureOf(Object obj) {
        return createSignature(Math.abs(obj.hashCode()) * (-1));
    }

    public static MessageSignature signatureOfModule(Module module) {
        return createSignature(Math.abs(module.getName().hashCode()) * (-1));
    }

    public static Style withDynamicColor(Style style, Supplier<Integer> supplier) {
        return ((DuckStyle) (Object) style.withColor(supplier.get().intValue())).mio$withColor(supplier);
    }

    public static MutableComponent themedLiteral(String str) {
        if (str == null) {
            return Component.empty();
        }
        return Component.literal(str).withStyle(style -> {
            return withDynamicColor(style, () -> {
                return Integer.valueOf(notifications.watermark.getValue().hashCode());
            });
        });
    }

    public static Component getClientPrefix() {
        MutableComponent empty = Component.empty();
        empty.append(Component.literal("[").withStyle(style -> {
            return withDynamicColor(style, () -> {
                return Integer.valueOf(notifications.getBracketColor().hashCode());
            });
        }));
        empty.append(Component.literal("Mio").withStyle(style2 -> {
            return withDynamicColor(style2, () -> {
                return Integer.valueOf(notifications.getWatermarkColor().hashCode());
            });
        }));
        empty.append(Component.literal("]").withStyle(style3 -> {
            return withDynamicColor(style3, () -> {
                return Integer.valueOf(notifications.getBracketColor().hashCode());
            });
        }));
        return empty;
    }

    public static void sendMessage(Component text, int i) {
        sendMessage(text, createSignature(i));
    }

    public static void sendMessage(Component text, MessageSignature MessageSignature) {
        minecraftClient.execute(() -> {
            try {
                // 26.2 port: ChatComponent.addMessage 4-arg is private; addPlayerMessage(Component, MessageSignature, GuiMessageTag) is the public path.
                minecraftClient.gui.hud.getChat().addPlayerMessage(Component.empty().append(getClientPrefix()).append(" ").append(text), MessageSignature, GuiMessageTag);
            } catch (Throwable th) {
            }
        });
    }

    public static void sendPrefixedMessage(Component text, MessageSignature MessageSignature, MixinClientConnectionMode mixinClientConnectionMode) {
        MutableComponent empty = Component.empty();
        empty.append(Component.literal("[").withStyle(style -> {
            return style.withColor(mixinClientConnectionMode.getColor().darker().hashCode());
        }));
        empty.append(Component.literal("!").withStyle(style2 -> {
            return style2.withColor(mixinClientConnectionMode.getColor().hashCode());
        }));
        empty.append(Component.literal("] ").withStyle(style3 -> {
            return style3.withColor(mixinClientConnectionMode.getColor().darker().hashCode());
        }));
        sendMessage(empty.append(text), MessageSignature);
    }

    public static Component gradientText(String str, Supplier<Integer> supplier, Color color) {
        MutableComponent empty = Component.empty();
        float[] RGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null);
        for (int i = 0; i < str.length(); i++) {
            int i2 = i;
            empty.append(Component.literal(String.valueOf(str.charAt(i))).withStyle(style -> {
                return withDynamicColor(style, () -> {
                    return Integer.valueOf(ColorUtils.getRainbowColor(i2 * ((Integer) supplier.get()).intValue(), RGBtoHSB[1], RGBtoHSB[2], 255).hashCode());
                });
            }));
        }
        return empty;
    }

    public static void sendChatOrCommand(String str) {
        if (!str.startsWith("/")) {
            minecraftClient.player.connection.sendChat(str);
        } else {
            minecraftClient.player.connection.sendCommand(str.substring(1));
        }
    }

    public static ClickEvent suggestCommand(String str) {
        return new ClickEvent.SuggestCommand( new StringTemplate().arg(str).arg(ClientCommandRegistry.getPrefix()).format("\u0001\u0001"));
    }

    public static Component bracketed(String str) {
        return Component.empty().append(Component.literal("[").withStyle(ChatFormatting.GRAY)).append(str).append(Component.literal("]").withStyle(ChatFormatting.GRAY));
    }
}
