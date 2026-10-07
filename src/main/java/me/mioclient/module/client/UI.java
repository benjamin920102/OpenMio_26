package me.mioclient.module.client;

import me.mioclient.event.events.network.PacketReceiveEvent;
import net.minecraft.network.chat.Component;

import me.mioclient.event.events.client.ClientTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;

import me.mioclient.gui.screen.AbstractClickGuiScreen;
import me.mioclient.gui.screen.ClickGuiScreen;
import me.mioclient.mode.client.CursorType;
import me.mioclient.mode.client.Season;
import me.mioclient.mode.misc.KeybindMode;

import me.mioclient.command.ClientCommandRegistry;

import me.mioclient.event.EventBus;

import java.awt.Color;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.api.Category;
import me.mioclient.api.Keybind;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import org.lwjgl.glfw.GLFW;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.StringSetting;

public final class UI extends Module {
    public static UI uI;
    public final AtomicBoolean atomicBoolean;
    public Setting<Boolean> reset = add(new BooleanSetting("Reset", false));
    public Setting<String> prefix = add(new StringSetting("Prefix", ";"));
    public Setting<Mode> msaa = add(new EnumSetting<>("MSAA", Mode.NONE));
    public Setting<Boolean> descriptions = add(new BooleanSetting("Descriptions", true).setVisible());
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 0, 0, 3000, v -> this.descriptions.isShown()).setSuffix("ms"));
    public Setting<Boolean> constantReset = add(new BooleanSetting("ConstantReset", false, v -> this.descriptions.isShown() && this.delay.getValue().intValue() > 0));
    public Setting<Integer> buttonHeight = add(new NumberSetting<>("ButtonHeight", 2, 0, 5));
    public Setting<Integer> frameWidth = add(new NumberSetting<>("FrameWidth", 6, -10, 20));
    public Setting<Integer> modulePadding = add(new NumberSetting<>("ModulePadding", 3, 1, 8));
    public Setting<Float> animSpeed = add(new NumberSetting<>("AnimSpeed", 1.0f, 0.2f, 2.5f));
    public Setting<Float> guiScale = add(new NumberSetting<>("GuiScale", 1.0f, 0.1f, 2.0f));
    public Setting<Boolean> snow = add(new BooleanSetting("Snow", true));
    public Setting<Boolean> elements = add(new BooleanSetting("Elements", true).setDependent());
    public Setting<Boolean> line = add(new BooleanSetting("Line", true, v -> this.elements.isShown()));
    public Setting<Boolean> binds = add(new BooleanSetting("Binds", false, v -> this.elements.isShown()));
    public Setting<Boolean> gear = add(new BooleanSetting("Gear", true, v -> this.elements.isShown()));
    public Setting<Boolean> windowShadow = add(new BooleanSetting("WindowShadow", false, v -> this.elements.isShown()).setVisible());
    public Setting<Color> shadowColor = add(new ColorSetting("ShadowColor", new Color(1174405120, true), v -> this.elements.isShown() && this.windowShadow.isShown()).setThemeSync(true));
    public Setting<Float> shadowSize = add(new NumberSetting<>("ShadowSize", 10.0f, 1.0f, 20.0f, v -> this.elements.isShown() && this.windowShadow.isShown()));
    public Setting<Boolean> sounds = add(new BooleanSetting("Sounds", true).setDependent());
    public Setting<Boolean> hover = add(new BooleanSetting("Hover", true, v -> this.sounds.isShown()).setVisible());
    public Setting<SoundIdentifier> sound2 = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "hover")).requiresSettings(this.sounds, this.hover));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 0.5f, 0.0f, 1.0f).requiresSettings(this.sounds, this.hover));
    public Setting<Boolean> leftClick = add(new BooleanSetting("LeftClick", true, v -> this.sounds.isShown()).setVisible());
    public Setting<SoundIdentifier> sound3 = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "click")).requiresSettings(this.sounds, this.leftClick));
    public Setting<Float> volume3 = add(new NumberSetting<>("Volume", 0.5f, 0.0f, 1.0f).requiresSettings(this.sounds, this.leftClick));
    public Setting<Boolean> rightClick = add(new BooleanSetting("RightClick", true, v -> this.sounds.isShown()).setVisible());
    public Setting<SoundIdentifier> sound = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "rclick")).requiresSettings(this.sounds, this.rightClick));
    public Setting<Float> volume2 = add(new NumberSetting<>("Volume", 0.5f, 0.0f, 1.0f).requiresSettings(this.sounds, this.rightClick));
    public Setting<Boolean> text = add(new BooleanSetting("Component", false).setDependent());
    public Setting<Boolean> bounce = add(new BooleanSetting("Bounce", true, v -> this.text.isShown()));
    public Setting<Color> textColor = add(new ColorSetting("TextColor", new Color(-396033, true), v -> this.text.isShown()));
    public Setting<Color> enabledColor = add(new ColorSetting("EnabledColor", new Color(-2242049, true), v -> this.text.isShown()));
    public Setting<Boolean> colors = add(new BooleanSetting("Colors", false).setDependent());
    public Setting<Color> color = add(new ColorSetting("Color", new Color(1941805567, true), v -> this.colors.isShown()).setThemeSync(true));
    public Setting<Color> bgColor = add(new ColorSetting("BgColor", new Color(1996619784, true), v -> this.colors.isShown()));
    public Setting<Color> bgButton = add(new ColorSetting("BgButton", new Color(715096283, true), v -> this.colors.isShown()).setThemeSync(true));
    public Setting<Color> bgEnabled = add(new ColorSetting("BgEnabled", new Color(1941805567, true), v -> this.colors.isShown()).setThemeSync(true));
    public Setting<Color> tint = add(new ColorSetting("Tint", new Color(627064397, true), v -> this.colors.isShown()));
    public Setting<Color> gradientColor = add(new ColorSetting("GradientColor", new Color(583181052, true), v -> this.colors.isShown()).setThemeSync(true));

    
    public enum Mode implements NamedValue {
        NONE("None", 0),
        X2("X2", 2),
        X4("X4", 4),
        X8("X8", 8);

        public final String name;
        public final int num;

        Mode(String str, int i) {
            this.name = str;
            this.num = i;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public int getMsaaMultiplier() {
            return this.num;
        }
    }

    public UI() {
        super("UI", "Displays the client's click gui.", Category.CLIENT, new String[0]);
        this.atomicBoolean = new AtomicBoolean(false);
        eventBus.registerStatic(UI.class);
        setDrawn(false);
        this.reset.setChangeCallback(() -> {
            if (this.reset.getValue().booleanValue()) {
                ClientServices.getClickGui().resetPanelLayout();
                this.reset.setValue(false);
            }
        });
        this.frameWidth.setChangeCallback(() -> {
            this.atomicBoolean.set(true);
        });
        modifyKeybind(keybind -> {
            return keybind.withKey(344);
        });
        uI = this;
        this.sound2.setConfigName("HoverSound");
        this.volume.setConfigName("HoverVolume");
        this.sound3.setConfigName("LeftClickSound");
        this.volume3.setConfigName("LeftClickVolume");
        this.sound.setConfigName("RightClickSound");
        this.volume2.setConfigName("RightClickVolume");
        if (!Season.WINTER.containsMonth(LocalDate.now().getMonthValue())) {
            this.snow.setValue(false);
            unregister((Setting<?>) this.snow);
        }
        this.prefix.setChangeCallback(() -> {
            if (this.prefix.getValue().equals(ClientCommandRegistry.getPrefix())) {
                return;
            }
            ClientCommandRegistry.setPrefix(this.prefix.getValue());
        });
        this.prefix.setHovered(true);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame() || (minecraftClient.gui.screen() instanceof ClickGuiScreen)) {
            return;
        }
        if (minecraftClient.gui.screen() instanceof ChatScreen) {
            ClientServices.tooltipsHelper.schedule(() -> {
                minecraftClient.gui.setScreen(ClientServices.getClickGui());
            }, 0);
        } else {
            minecraftClient.gui.setScreen(ClientServices.getClickGui());
        }
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (minecraftClient.gui.screen() instanceof ClickGuiScreen) {
            ClientServices.getClickGui().requestClose();
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (minecraftClient.gui.screen() instanceof AbstractClickGuiScreen) {
            return;
        }
        toggle();
    }

    @Listen
    public void onClientTick(ClientTickEvent fontsEvent) {
        if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().handle(), 0) == 1 || !this.atomicBoolean.get()) {
            return;
        }
        ClientServices.getClickGui().resetPanelLayout();
        this.reset.setValue(false);
        this.atomicBoolean.set(false);
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof ClientboundDisconnectPacket) {
            disable();
        }
    }

    @Listen
    public static void onClientTickCursor(ClientTickEvent fontsEvent) {
        if (minecraftClient.gui.screen() instanceof AbstractClickGuiScreen) {
            return;
        }
        CursorType.STANDARD.applyCursor();
    }

    @Override // me.mioclient.module.Module
    public Keybind getKeybind() {
        Keybind keybind = super.getKeybind();
        if (keybind.getMode() != Keybind.KeybindMode.TOGGLE) {
            keybind = keybind.withMode(Keybind.KeybindMode.TOGGLE);
            setKeybind(keybind);
        }
        return keybind;
    }
}