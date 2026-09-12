package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.registry.RegistrySuggestionProvider;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.registry.EnumSettingRegistry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.registry.RegistrySuggestionProvider;
import me.mioclient.util.registry.EnumSettingRegistry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.command.CommandSource;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.ObjectSetting;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SettingValueArgumentType.class */
public class SettingValueArgumentType implements com.mojang.brigadier.arguments.ArgumentType<String>, ClientAccessor {
    public final String string;
    public final Module module;

    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public String parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String remaining = stringReader.getRemaining();
        stringReader.setCursor(stringReader.getTotalLength());
        return remaining;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        RegistrySuggestionProvider helper56;
        try {
            Setting<?> option = ModuleSettingArgumentType.getOption(commandContext, this.module, this.string);
            if (option instanceof BooleanSetting) {
                return ExamplesArgumentType_2.bool((BooleanSetting) option).listSuggestions(commandContext, suggestionsBuilder);
            }
            if (option instanceof EnumSetting) {
                return CommandSource.suggestMatching(Arrays.stream((Enum[]) ((Enum) ((EnumSetting) option).getDefaultValue()).getDeclaringClass().getEnumConstants()).filter(r2 -> {
                    return !EnumSettingRegistry.isExperimentalEnumValue(r2);
                }).map(r22 -> {
                    return r22.name().toLowerCase();
                }), suggestionsBuilder);
            }
            if (option instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) option;
                Number number = (Number) numberSetting.getValue();
                return suggestionsBuilder.getRemaining().isBlank() ? suggestionsBuilder.suggest(String.valueOf(numberSetting.getDefaultValue())).buildFuture() : number instanceof Float ? FloatArgumentType.floatArg(((Float) ((Number) numberSetting.getMinValue())).floatValue(), ((Float) ((Number) numberSetting.getMaxValue())).floatValue()).listSuggestions(commandContext, suggestionsBuilder) : number instanceof Double ? DoubleArgumentType.doubleArg(((Double) ((Number) numberSetting.getMinValue())).doubleValue(), ((Double) ((Number) numberSetting.getMaxValue())).doubleValue()).listSuggestions(commandContext, suggestionsBuilder) : IntegerArgumentType.integer(((Integer) ((Number) numberSetting.getMinValue())).intValue(), ((Integer) ((Number) numberSetting.getMaxValue())).intValue()).listSuggestions(commandContext, suggestionsBuilder);
            }
            if (!(option instanceof ObjectSetting)) {
                if (!(option instanceof SoundIdentifierSetting)) {
                    return com.mojang.brigadier.arguments.ArgumentType.super.listSuggestions(commandContext, suggestionsBuilder);
                }
                return CommandSource.suggestMatching(ClientServices.killstreakSoundManager.getSoundIdentifiers().stream().map(searchIdentifier -> {
                    return new StringTemplate().arg(searchIdentifier.getName()).arg(searchIdentifier.getSoundSource()).format("\u0001:\u0001");
                }), suggestionsBuilder);
            }
            Collection<String> collection3127 = ((ObjectSetting) option).getAllValueStrings();
            if ((option instanceof EntityListObjectSetting) && (helper56 = RegistrySuggestionProvider.getProvider(((EntityListObjectSetting) option).getRegistry())) != RegistrySuggestionProvider.helper_24) {
                collection3127.addAll(helper56.getGroupNames());
            }
            return CommandSource.suggestMatching(collection3127, suggestionsBuilder);
        } catch (Exception e) {
            return Suggestions.empty();
        }
    }

    public SettingValueArgumentType(Module module, String str) {
        this.string = str;
        this.module = module;
    }

    public static SettingValueArgumentType value(Module module, String str) {
        return new SettingValueArgumentType(module, str);
    }
}
