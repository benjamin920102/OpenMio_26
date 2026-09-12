package me.mioclient.command;

import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.registry.RegistrySuggestionProvider;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.util.registry.RegistrySuggestionProvider;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.math.ColorUtils;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.SwitchBootstraps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.ObjectSetting;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_32.class */
public final class ModuleSettingCommand extends CommandFeature {
    public final Module module;

    public ModuleSettingCommand(Module module) {
        super(module.getName());
        this.module = module;
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("state", BoolArgumentType.bool()).suggests((commandContext, suggestionsBuilder) -> {
            return suggestionsBuilder.buildFuture();
        }).executes(commandContext2 -> {
            this.module.setToggled(((Boolean) commandContext2.getArgument("state", Boolean.class)).booleanValue());
            return 1;
        })).then(CommandFeature.argument("setting", new ModuleSettingArgumentType(this.module)).then(CommandFeature.literalArgument("clear").suggests(this::suggestClear).executes(commandContext3 -> {
            Setting<?> option = ModuleSettingArgumentType.getOption(commandContext3, this.module, "setting");
            if (!(option instanceof ObjectSetting)) {
                return 1;
            }
            ((ObjectSetting<?>) option).getValue().clear();
            ChatUtils.sendPrefixedMessage(Text.literal(this.module.getName()).append(Text.literal(" ").append(option.getName()).styled(style -> {
                return style.withFormatting(Formatting.GRAY);
            })).append(": Cleared"), ChatUtils.signatureOfModule(this.module), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
            return 1;
        })).then(CommandFeature.argument("value", new SettingValueArgumentType(this.module, "setting")).executes(commandContext4 -> {
            Setting<?> option = ModuleSettingArgumentType.getOption(commandContext4, this.module, "setting");
            String str = (String) commandContext4.getArgument("value", String.class);
            try {
                Formatting formatting = null;
                if (option instanceof EntityListObjectSetting) {
                    EntityListObjectSetting entityListObjectSetting = (EntityListObjectSetting) option;
                    Collection<String> collection53 = RegistrySuggestionProvider.getProvider(entityListObjectSetting.getRegistry()).getGroupEntries(str);
                    if (!collection53.isEmpty()) {
                        Stream<String> stream = collection53.stream();
                        Objects.requireNonNull(entityListObjectSetting);
                        if (stream.anyMatch(entityListObjectSetting::containsString)) {
                            Objects.requireNonNull(entityListObjectSetting);
                            collection53.forEach(entityListObjectSetting::removeFromString);
                            formatting = Formatting.RED;
                        } else {
                            Objects.requireNonNull(entityListObjectSetting);
                            collection53.forEach(entityListObjectSetting::addToSetFromString);
                            formatting = Formatting.GREEN;
                        }
                    }
                }
                if (formatting == null) {
                    option.loadFromString(str);
                }
                MutableText text2532 = (MutableText)(formatSettingValue(option));
                if (option instanceof ObjectSetting) {
                    ObjectSetting objectSetting = (ObjectSetting) option;
                    if (formatting == null) {
                        formatting = objectSetting.containsString(str) ? Formatting.GREEN : Formatting.RED;
                    }
                    Formatting formatting2 = formatting;
                    text2532 = Text.literal(str).styled(style -> {
                        return style.withFormatting(formatting2);
                    });
                }
                ChatUtils.sendPrefixedMessage(Text.literal(this.module.getName()).append(Text.literal(" ").append(option.getName()).styled(style2 -> {
                    return style2.withFormatting(Formatting.GRAY);
                })).append(": ").append((Text) text2532), ChatUtils.signatureOfModule(this.module), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
                return 1;
            } catch (Throwable th) {
                ChatUtils.sendPrefixedMessage(Text.literal("Invalid value: ").append(str), ChatUtils.signatureOfModule(this.module), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
                return 1;
            }
        })).then(CommandFeature.literal("reset").executes(commandContext5 -> {
            ModuleSettingArgumentType.getOption(commandContext5, this.module, "setting").reset();
            return 1;
        })).executes(commandContext6 -> {
            Setting<?> option = ModuleSettingArgumentType.getOption(commandContext6, this.module, "setting");
            ChatUtils.sendMessage(Text.literal(this.module.getName()).append(Text.literal(" ").append(option.getName()).styled(style -> {
                return style.withFormatting(Formatting.GRAY);
            })).append(": ").append(formatSettingValue(option)), ChatUtils.signatureOfModule(this.module));
            return 1;
        })).then(CommandFeature.literal("Enabled").then(CommandFeature.argument("state", BoolArgumentType.bool()).executes(commandContext7 -> {
            this.module.setToggled(((Boolean) commandContext7.getArgument("state", Boolean.class)).booleanValue());
            return 1;
        })).executes(commandContext8 -> {
            ChatUtils.sendMessage(Text.literal("Module ").append(Text.literal(this.module.getName()).styled(style -> {
                return style.withFormatting(Formatting.GRAY);
            })).append(" is ").append(Text.literal(this.module.isToggled() ? "enabled" : "disabled").styled(style2 -> {
                return this.module.isToggled() ? style2.withFormatting(Formatting.GREEN) : style2.withFormatting(Formatting.RED);
            })), ChatUtils.signatureOfModule(this.module));
            return 1;
        })).executes(commandContext9 -> {
            MutableText empty = Text.empty();
            empty.append(getMutableText2529()).append(" [%s - %s]".formatted(this.module.getKeybind().getBindString(), this.module.getKeybind().getMode().getName()));
            for (Setting<?> setting : this.module.getRegistry()) {
                MutableText literal = Text.literal(new StringTemplate().arg(setting.getName()).format("\n\u0001: "));
                Objects.requireNonNull(setting);
                int typeSwitchIndex = setting instanceof EnumSetting ? 0
                : setting instanceof ObjectSetting ? 1
                : setting instanceof ColorSetting ? 2
                : -1;
        switch (typeSwitchIndex) {
                    case 0:
                        literal.append(Text.literal(ClientTextRenderer.capitalize(((Enum) ((EnumSetting) setting).getValue()).name())).formatted(Formatting.GRAY));
                        break;
                    case 1:
                        literal.append(Text.literal(String.join(", ", ((ObjectSetting) setting).getValueStrings())).formatted(Formatting.GRAY));
                        break;
                    case 2:
                        ColorSetting colorSetting = (ColorSetting) setting;
                        literal.append(Text.literal(getString2530(colorSetting)).styled(style -> {
                            return style.withColor(colorSetting.getValue().hashCode());
                        }));
                        break;
                    default:
                        literal.append(Text.literal(setting.getValue().toString()).formatted(Formatting.GRAY));
                        break;
                }
                String string2921 = new StringTemplate().arg(setting.getName()).arg(this.module.getName().toLowerCase()).arg(ClientCommandRegistry.getPrefix()).format("\u0001\u0001 \u0001 ");
                literal.styled(style2 -> {
                    return style2.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, string2921));
                });
                empty.append((Text) literal);
            }
            ChatUtils.sendMessage((Text) empty, ChatUtils.createSignature(-1));
            return 1;
        });
        literalArgumentBuilder.then(CommandFeature.literal("reset").executes(commandContext10 -> {
            Iterator<Setting<?>> it = this.module.getRegistry().iterator();
            while (it.hasNext()) {
                it.next().reset();
            }
            return 1;
        }));
    }

    @NotNull
    public MutableText getMutableText2529() {
        MutableText literal = Text.literal(this.module.getName());
        literal.styled(style -> {
            return ChatUtils.withDynamicColor(style, () -> {
                return this.module.isToggled() ? Formatting.GREEN.getColorValue() : Formatting.RED.getColorValue();
            });
        });
        literal.styled(style2 -> {
            return style2.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(this.module.getDescription().split("\n")[0])));
        });
        literal.styled(style3 -> {
            return style3.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, new StringTemplate().arg(this.module.getName()).arg(ClientCommandRegistry.getPrefix()).format("\u0001toggle \u0001")));
        });
        return literal;
    }

    public String getString2530(ColorSetting colorSetting) {
        Color value = colorSetting.getValue();
        return colorSetting.flag3 ? "rgb(%d, %d, %d)".formatted(Integer.valueOf(value.getRed()), Integer.valueOf(value.getGreen()), Integer.valueOf(value.getBlue())) : "rgba(%d, %d, %d, %d)".formatted(Integer.valueOf(value.getRed()), Integer.valueOf(value.getGreen()), Integer.valueOf(value.getBlue()), Integer.valueOf(value.getAlpha()));
    }

    public CompletableFuture<Suggestions> suggestClear(CommandContext<?> commandContext, SuggestionsBuilder suggestionsBuilder) {
        try {
            if (ModuleSettingArgumentType.getOption(commandContext, this.module, "setting") instanceof ObjectSetting) {
                return CommandSource.suggestMatching(new String[]{"clear"}, suggestionsBuilder);
            }
        } catch (Exception e) {
        }
        return Suggestions.empty();
    }

    public Text formatSettingValue(Setting<?> setting) {
        MutableText literal = Text.literal(setting.getValue().toString());
        if (setting instanceof ColorSetting) {
            literal = Text.literal(ColorUtils.colorToHex(((ColorSetting) setting).getValue(), true)).styled(style -> {
                return style.withColor(setting.getValue().hashCode());
            });
        } else if (setting instanceof ObjectSetting) {
            literal = Texts.join(((ObjectSetting) setting).getValueStrings(), Text.literal(", ").styled(style2 -> {
                return style2.withFormatting(Formatting.GRAY);
            }), Text::literal);
        } else if (setting instanceof SoundIdentifierSetting) {
            literal = Text.literal(((SoundIdentifierSetting) setting).getValue().getName());
        }
        return literal;
    }
}
