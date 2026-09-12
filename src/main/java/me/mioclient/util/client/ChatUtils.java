package me.mioclient.util.client;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.api.ClientServices;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import me.mioclient.module.Module;
import me.mioclient.module.client.Notifications;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.ducks.DuckStyle;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChatUtils.class */
public class ChatUtils implements ClientAccessor {
    public static final Notifications notifications = (Notifications) ClientServices.moduleInjector.getModule(Notifications.class);
    public static final MessageIndicator messageIndicator = new MessageIndicator(-1, (MessageIndicator.Icon) null, Text.empty(), "Mio");
    public static final Pattern pattern = Pattern.compile("^(\\w{3,16}) -> (\\w{3,16}): (.*)");
    public static final List<Pattern> list = List.of(Pattern.compile("^From (\\w{3,16}): (.*)"), Pattern.compile("^from (\\w{3,16}): (.*)"), Pattern.compile("^(\\w{3,16}) whispers: (.*)"), Pattern.compile("^(\\w{3,16}) -> me (.*)"), Pattern.compile("^(\\w{3,16}) whispers to you: (.*)"), Pattern.compile("^(\\w{3,16}) says: (.*)"), Pattern.compile("^(\\w{3,16}) Ð¿Ð¸ÑˆÐµÑ‚: (.*)"), pattern);
    public static final List<Pattern> list2 = List.of(Pattern.compile("^To (\\w{3,16}): (.*)"), Pattern.compile("^to (\\w{3,16}): (.*)"), Pattern.compile("^\\[me -> (\\w{3,16})\\] (.*)"), Pattern.compile("^You whisper to (\\w{3,16}): (.*)"), Pattern.compile("^Ðš (\\w{3,16}): (.*)"), pattern);

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/MixinMessageIndicatorHelper$MixinClientConnectionMode.class */
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
        if (str.toLowerCase(Locale.ROOT).startsWith(minecraftClient.getSession().getUsername().toLowerCase(Locale.ROOT))) {
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
        boolean startsWith = str.toLowerCase(Locale.ROOT).startsWith(minecraftClient.getSession().getUsername().toLowerCase(Locale.ROOT));
        for (Pattern pattern2 : list2) {
            if (pattern2 != pattern || startsWith) {
                if (pattern2.matcher(str).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static MessageSignatureData createSignature(int i) {
        return new MessageSignatureData(ByteBuffer.allocate(256).putInt(i).array());
    }

    public static MessageSignatureData signatureOf(Object obj) {
        return createSignature(Math.abs(obj.hashCode()) * (-1));
    }

    public static MessageSignatureData signatureOfModule(Module module) {
        return createSignature(Math.abs(module.getName().hashCode()) * (-1));
    }

    public static Style withDynamicColor(Style style, Supplier<Integer> supplier) {
        return ((DuckStyle) (Object) style.withColor(supplier.get().intValue())).mio$withColor(supplier);
    }

    public static MutableText themedLiteral(String str) {
        if (str == null) {
            return Text.empty();
        }
        return Text.literal(str).styled(style -> {
            return withDynamicColor(style, () -> {
                return Integer.valueOf(notifications.watermark.getValue().hashCode());
            });
        });
    }

    public static Text getClientPrefix() {
        MutableText empty = Text.empty();
        empty.append(Text.literal("[").styled(style -> {
            return withDynamicColor(style, () -> {
                return Integer.valueOf(notifications.getBracketColor().hashCode());
            });
        }));
        empty.append(Text.literal("Mio").styled(style2 -> {
            return withDynamicColor(style2, () -> {
                return Integer.valueOf(notifications.getWatermarkColor().hashCode());
            });
        }));
        empty.append(Text.literal("]").styled(style3 -> {
            return withDynamicColor(style3, () -> {
                return Integer.valueOf(notifications.getBracketColor().hashCode());
            });
        }));
        return empty;
    }

    public static void sendMessage(Text text, int i) {
        sendMessage(text, createSignature(i));
    }

    public static void sendMessage(Text text, MessageSignatureData messageSignatureData) {
        minecraftClient.execute(() -> {
            try {
                minecraftClient.inGameHud.getChatHud().addMessage(Text.empty().append(getClientPrefix()).append(" ").append(text), messageSignatureData, messageIndicator);
            } catch (Throwable th) {
            }
        });
    }

    public static void sendPrefixedMessage(Text text, MessageSignatureData messageSignatureData, MixinClientConnectionMode mixinClientConnectionMode) {
        MutableText empty = Text.empty();
        empty.append(Text.literal("[").styled(style -> {
            return style.withColor(mixinClientConnectionMode.getColor().darker().hashCode());
        }));
        empty.append(Text.literal("!").styled(style2 -> {
            return style2.withColor(mixinClientConnectionMode.getColor().hashCode());
        }));
        empty.append(Text.literal("] ").styled(style3 -> {
            return style3.withColor(mixinClientConnectionMode.getColor().darker().hashCode());
        }));
        sendMessage(empty.append(text), messageSignatureData);
    }

    public static Text gradientText(String str, Supplier<Integer> supplier, Color color) {
        MutableText empty = Text.empty();
        float[] RGBtoHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[]) null);
        for (int i = 0; i < str.length(); i++) {
            int i2 = i;
            empty.append(Text.literal(String.valueOf(str.charAt(i))).styled(style -> {
                return withDynamicColor(style, () -> {
                    return Integer.valueOf(ColorUtils.getRainbowColor(i2 * ((Integer) supplier.get()).intValue(), RGBtoHSB[1], RGBtoHSB[2], 255).hashCode());
                });
            }));
        }
        return empty;
    }

    public static void sendChatOrCommand(String str) {
        if (!str.startsWith("/")) {
            minecraftClient.player.networkHandler.sendChatMessage(str);
        } else {
            minecraftClient.player.networkHandler.sendChatCommand(str.substring(1));
        }
    }

    public static ClickEvent suggestCommand(String str) {
        return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, new StringTemplate().arg(str).arg(ClientCommandRegistry.getPrefix()).format("\u0001\u0001"));
    }

    public static Text bracketed(String str) {
        return Text.empty().append(Text.literal("[").formatted(Formatting.GRAY)).append(str).append(Text.literal("]").formatted(Formatting.GRAY));
    }
}
