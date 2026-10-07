package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.anim.AnimationProgress;
import me.mioclient.util.client.ChatUtils;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.AddMessageEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.ChatEvent;
import me.mioclient.module.Module;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.StringSetting;

public class BetterChat extends Module {
    public static BetterChat betterChat;
    public Setting<Boolean> timestamps = add(new BooleanSetting("Timestamps", false).setVisible());
    public Setting<Boolean> onlyServer = add(new BooleanSetting("OnlyServer", true).requiresSettings(this.timestamps));
    public Setting<BetterChatMode> format = add(new EnumSetting<>("Format", BetterChatMode.MINUTES, v -> this.timestamps.isShown()));
    public Setting<Color> textColor = add(new ColorSetting("TextColor", new Color(-1, true), v -> this.timestamps.isShown()).setThemeSync(true));
    public Setting<Color> bracketsColor = add(new ColorSetting("BracketsColor", new Color(-8355712, true), v -> this.timestamps.isShown()).setThemeSync(true));
    public Setting<Boolean> rainbow = add(new BooleanSetting("Rainbow", false, v -> this.timestamps.isShown()));
    public Setting<Integer> rainbowAmount = add(new NumberSetting<>("RainbowAmount", 10, 1, 100, v -> this.timestamps.isShown() && this.rainbow.getValue().booleanValue()));
    public Setting<String> leftBracket = add(new StringSetting("LeftBracket", "<", v -> this.timestamps.isShown()));
    public Setting<String> rightBracket = add(new StringSetting("RightBracket", ">", v -> this.timestamps.isShown()));
    public Setting<Boolean> mentionSound = add(new BooleanSetting("MentionSound", false).setVisible());
    public Setting<SoundIdentifier> soundMode = add(new SoundIdentifierSetting("SoundMode", new SoundIdentifier("system", "css")).requiresSettings(this.mentionSound));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 1.0f, 0.0f, 1.0f).requiresSettings(this.mentionSound));
    public Setting<Boolean> animation = add(new BooleanSetting("Animation", false).setVisible());
    public Setting<BetterChatMode_2> type = add(new EnumSetting<>("Type", BetterChatMode_2.HORIZONTAL, v -> this.animation.isShown()));
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.1f, 3.0f, v -> this.animation.isShown()));
    public Setting<Integer> alpha = add(new NumberSetting<>("Alpha", 250, 0, 1000).setSuffix("ms").setHoleSnapConfig("None", HoleSnapMode.MIN).requiresSettings(this.animation));
    public Setting<Boolean> always = add(new BooleanSetting("Always", false).requiresSettings(this.animation));
    public Setting<Boolean> customSuffix = add(new BooleanSetting("CustomSuffix", false).setVisible());
    public Setting<String> suffix = add(new StringSetting("Suffix", " ", v -> this.customSuffix.isShown()));
    public Setting<Boolean> highlight = add(new BooleanSetting("Highlight", false).setVisible());
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-43691, true)).requiresSettings(this.highlight));
    public Setting<Boolean> longChatHistory = add(new BooleanSetting("LongChatHistory", false));
    public Setting<Boolean> noReset = add(new BooleanSetting("NoReset", false));
    public final AnimationProgress progress;
    public boolean flag3;
    public static final Pattern pattern = Pattern.compile("^\\d+$");
    public static boolean flag = false;
    public static boolean flag2 = false;
    public static PMSound pMSound = (PMSound) ClientServices.moduleInjector.getModule(PMSound.class);

    
    
    public enum BetterChatMode implements NamedValue {
        HOURS(new SimpleDateFormat("HH"), "Hours"),
        MINUTES(new SimpleDateFormat("HH:mm"), "Minutes"),
        SECONDS(new SimpleDateFormat("HH:mm:ss"), "Seconds");

        public final DateFormat dateFormat;
        public final String name;

        BetterChatMode(DateFormat dateFormat, String str) {
            this.dateFormat = dateFormat;
            this.name = str;
        }

        public DateFormat getDateTimeFormat() {
            return this.dateFormat;
        }

        public String formatDate(Date date) {
            return this.dateFormat.format(date);
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    
    public enum BetterChatMode_2 implements NamedValue {
        VERTICAL("Vertical"),
        HORIZONTAL("Horizontal"),
        BOTH("Both"),
        BOUNCE("Bounce"),
        NONE("None");

        public final String name;

        BetterChatMode_2(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public BetterChat() {
        super("BetterChat", "Allows you to customize your chat as you please.", Category.MISC, new String[0]);
        Setting<Float> setting = this.speed;
        Objects.requireNonNull(setting);
        this.progress = new AnimationProgress((Supplier<Float>) setting::getValue, true);
        this.alpha.setHoleSnapConfig("None", HoleSnapMode.MIN);
        betterChat = this;
        setDrawn(false);
        this.color.setConfigName("HighlightColor");
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (sendImmediatelyEvent.isCancelled()) {
            return;
        }
        if ((sendImmediatelyEvent.getPacket() instanceof ServerboundChatPacket) || (sendImmediatelyEvent.getPacket() instanceof ServerboundChatCommandPacket)) {
            this.flag3 = true;
        }
    }

    @Listen
    public void onAddMessage(AddMessageEvent addMessageEvent) {
        if (addMessageEvent.getPhase() == KeyPearlMode.Pre) {
            playMentionSound(addMessageEvent);
            applyTimestamp(addMessageEvent);
            triggerBounceAnimation();
            this.flag3 = false;
        }
    }

    @Listen
    public void onChat(ChatEvent event) {
        String string2649 = event.getMessage();
        if (!pattern.matcher(string2649).matches() && this.customSuffix.getValue().booleanValue()) {
            String string2921 = new StringTemplate().arg(this.suffix.getValue()).format(" \u0001");
            event.setMessage(new StringTemplate().arg(string2921.substring(0, Mth.clamp(256 - string2649.length(), 0, string2921.length()))).arg(string2649).format("\u0001\u0001"));
        }
    }

    @Listen
    public void onHudRender(HudRenderEvent matrixStackEvent_2) {
        this.progress.animateToggle(false);
        this.progress.getValue();
    }

    public void applyTimestamp(AddMessageEvent addMessageEvent) {
        if (this.timestamps.getValue().booleanValue()) {
            if (isSystemIndicator(addMessageEvent.getIndicator()) || !this.onlyServer.getValue().booleanValue()) {
                // 26.2: Style#withColor(int) is the styling path (ChatUtils.withDynamicColor wraps it).
                int bracketColor = this.bracketsColor.getValue().getRGB();
                int timeColor = this.textColor.getValue().getRGB();
                MutableComponent append = Component.empty()
                        .append(Component.literal(this.leftBracket.getValue()).withStyle(s -> s.withColor(bracketColor)))
                        .append(Component.literal(this.format.getValue().formatDate(new Date())).withStyle(s -> s.withColor(timeColor)))
                        .append(Component.literal(this.rightBracket.getValue()).withStyle(s -> s.withColor(bracketColor)))
                        .append(" ");
                if (this.rainbow.getValue().booleanValue()) {
                    append = Component.empty().append(ChatUtils.gradientText(append.getString(), () -> {
                        return Integer.valueOf(this.rainbowAmount.getValue().intValue() * 10);
                    }, this.textColor.getValue()));
                }
                addMessageEvent.setText(append.append(addMessageEvent.getText()));
            }
        }
    }

    public void playMentionSound(AddMessageEvent addMessageEvent) {
        if (this.mentionSound.getValue().booleanValue()) {
            String lowerCase = addMessageEvent.getText().getString().toLowerCase();
            if ((pMSound.isToggled() && ChatUtils.isIncomingWhisper(lowerCase)) || this.flag3 || !lowerCase.contains(minecraftClient.getUser().getName().toLowerCase())) {
                return;
            }
            ClientServices.killstreakSoundManager.findSoundPlayer(this.soundMode.getValue()).play(this.volume.getValue().floatValue());
        }
    }

    public void triggerBounceAnimation() {
        if (isAnimationEnabled() && this.type.getValue() == BetterChatMode_2.BOUNCE) {
            this.progress.setToggledImmediate(true);
        }
    }

    public boolean isSystemIndicator(GuiMessageTag messageIndicator) {
        return messageIndicator == null || messageIndicator == GuiMessageTag.system() || messageIndicator == GuiMessageTag.systemSinglePlayer() || messageIndicator == GuiMessageTag.chatNotSecure();
    }

    public boolean isAnimationEnabled() {
        return isToggled() && this.animation.getValue().booleanValue();
    }

    public static BetterChat getBetterChat678() {
        return betterChat;
    }
}