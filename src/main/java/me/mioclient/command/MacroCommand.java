package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.event.EventBus;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import me.mioclient.api.Keybind;
import me.mioclient.event.events.input.KeyEvent;
import me.mioclient.event.Listen;
import me.mioclient.command.EnumArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.mode.misc.KeybindMode;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_6.class */
public final class MacroCommand extends CommandFeature {
    public boolean flag;
    public String string;
    public KeybindMode mode_4;

    public MacroCommand() {
        super("macro");
        eventBus.register(this);
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(withMacroArgument("delete", requiredArgumentBuilder -> {
            requiredArgumentBuilder.executes(commandContext -> {
                KeybindFeature keybindFeature = (KeybindFeature) commandContext.getArgument("macro", KeybindFeature.class);
                ClientServices.macroManager.unregister(keybindFeature);
                ChatUtils.sendMessage(Text.literal(ClientTextRenderer.capitalize(keybindFeature.getName())).append(" has been deleted"), ChatUtils.createSignature(-1));
                return 1;
            });
        })).then(withMacroArgument("commands", this::buildCommandSubcommands)).then(CommandFeature.literal("new").then(CommandFeature.argument("name", StringArgumentType.word()).then(CommandFeature.argument("type", new EnumArgumentType(KeybindMode.class, "Macro")).then(CommandFeature.argument("key", new KeybindArgumentType()).then(commandArgument().executes(commandContext -> {
            String str = (String) commandContext.getArgument("name", String.class);
            String str2 = (String) commandContext.getArgument("command", String.class);
            KeybindFeature keybindFeature832 = ((KeybindMode) commandContext.getArgument("type", KeybindMode.class)).createKeybindFeature(str, (Keybind) commandContext.getArgument("key", Keybind.class));
            keybindFeature832.getCommands().add(str2);
            ClientServices.macroManager.register(keybindFeature832);
            ChatUtils.sendMessage(Text.literal("Macro ").append(keybindFeature832.getAsText()).append(" has been created"), ChatUtils.createSignature(-1));
            return 1;
        })).executes(commandContext2 -> {
            String str = (String) commandContext2.getArgument("name", String.class);
            KeybindFeature keybindFeature832 = ((KeybindMode) commandContext2.getArgument("type", KeybindMode.class)).createKeybindFeature(str, (Keybind) commandContext2.getArgument("key", Keybind.class));
            ClientServices.macroManager.register(keybindFeature832);
            ChatUtils.sendMessage(Text.literal("Macro ").append(keybindFeature832.getAsText()).append(" has been created"), ChatUtils.createSignature(-1));
            return 1;
        })).executes(commandContext3 -> {
            this.string = (String) commandContext3.getArgument("name", String.class);
            this.mode_4 = (KeybindMode) commandContext3.getArgument("type", KeybindMode.class);
            this.flag = true;
            ChatUtils.sendMessage(Text.literal("Press a key"), ChatUtils.createSignature(-1));
            return 1;
        })))).then(CommandFeature.literal("list").executes(commandContext4 -> {
            int i = 0;
            ChatUtils.sendMessage(Text.literal("Macro list:"), ChatUtils.createSignature(-1));
            for (KeybindFeature keybindFeature : ClientServices.macroManager.getRegistry()) {
                ChatUtils.sendMessage(Text.literal("%s (%s)".formatted(keybindFeature.getName(), keybindFeature.getKeybind().getBindString())), ChatUtils.createSignature(i));
                i++;
            }
            return 0;
        }));
    }

    public void buildCommandSubcommands(RequiredArgumentBuilder<CommandSource, KeybindFeature> requiredArgumentBuilder) {
        requiredArgumentBuilder.then(CommandFeature.literal("add").then(commandArgument().executes(commandContext -> {
            KeybindFeature keybindFeature = (KeybindFeature) commandContext.getArgument("macro", KeybindFeature.class);
            String str = (String) commandContext.getArgument("command", String.class);
            if (keybindFeature.getKeybindMode() != KeybindMode.HOLD || keybindFeature.getCommands().size() < 2) {
                keybindFeature.getCommands().add(str);
                return 1;
            }
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(String.valueOf(Formatting.RED)).format("\u0001You've reached the command limit of Hold Macro")), ChatUtils.createSignature(-1));
            return 1;
        }))).then(CommandFeature.literal("remove").then(CommandFeature.argument("index", IntegerArgumentType.integer(1)).executes(commandContext2 -> {
            KeybindFeature keybindFeature = (KeybindFeature) commandContext2.getArgument("macro", KeybindFeature.class);
            int intValue = ((Integer) commandContext2.getArgument("index", Integer.class)).intValue() - 1;
            if (intValue < keybindFeature.getCommands().size() && intValue >= 0 && !keybindFeature.getCommands().isEmpty()) {
                keybindFeature.getCommands().remove(intValue);
                return 1;
            }
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(formatCommandList(keybindFeature)).arg(String.valueOf(Formatting.WHITE)).arg(String.valueOf(Formatting.RED)).format("\u0001Invalid index\u0001\n\u0001")), ChatUtils.createSignature(-1));
            return 1;
        }))).then(CommandFeature.literal("clear").executes(commandContext3 -> {
            ((KeybindFeature) commandContext3.getArgument("macro", KeybindFeature.class)).getCommands().clear();
            return 1;
        })).then(CommandFeature.literal("edit").then(CommandFeature.argument("index", IntegerArgumentType.integer(1)).then(commandArgument().executes(commandContext4 -> {
            KeybindFeature keybindFeature = (KeybindFeature) commandContext4.getArgument("macro", KeybindFeature.class);
            String str = (String) commandContext4.getArgument("command", String.class);
            int intValue = ((Integer) commandContext4.getArgument("index", Integer.class)).intValue() - 1;
            if (intValue <= keybindFeature.getCommands().size()) {
                keybindFeature.getCommands().set(intValue, str);
                return 1;
            }
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(formatCommandList(keybindFeature)).arg(String.valueOf(Formatting.WHITE)).arg(String.valueOf(Formatting.RED)).format("\u0001Invalid index\u0001\n\u0001")), ChatUtils.createSignature(-1));
            return 1;
        })))).executes(commandContext5 -> {
            ChatUtils.sendMessage(Text.literal(formatCommandList((KeybindFeature) commandContext5.getArgument("macro", KeybindFeature.class))), ChatUtils.createSignature(-1));
            return 1;
        });
    }

    public com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> withMacroArgument(String str, Consumer<RequiredArgumentBuilder<CommandSource, KeybindFeature>> consumer) {
        RequiredArgumentBuilder<CommandSource, KeybindFeature> argument = CommandFeature.argument("macro", new MacroArgumentType());
        consumer.accept(argument);
        return CommandFeature.literal(str).then(argument);
    }

    public String formatCommandList(KeybindFeature keybindFeature) {
        StringBuffer stringBuffer = new StringBuffer(new StringTemplate().arg(keybindFeature.getName()).format("\u0001's command list: \n"));
        for (int i = 0; i < keybindFeature.getCommands().size(); i++) {
            stringBuffer.append(i + 1);
            stringBuffer.append(". ");
            stringBuffer.append(keybindFeature.getCommands().get(i));
            if (i != keybindFeature.getCommands().size() - 1) {
                stringBuffer.append("\n");
            }
        }
        return stringBuffer.toString();
    }

    public RequiredArgumentBuilder<CommandSource, String> commandArgument() {
        return CommandFeature.argument("command", StringArgumentType.greedyString()).suggests(this::suggestMacroCommands);
    }

    public CompletableFuture<Suggestions> suggestMacroCommands(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) {
        try {
            int start = suggestionsBuilder.getStart();
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (char c : suggestionsBuilder.getRemaining().toCharArray()) {
                i++;
                if (c == ClientCommandRegistry.getPrefixChar() || (i == 1 && c == ' ')) {
                    start += i;
                    i = 0;
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
            int i2 = start;
            return ClientCommandRegistry.commandDispatcher.getCompletionSuggestions(ClientCommandRegistry.commandDispatcher.parse(sb.toString(), (CommandSource) null), i).thenApply(suggestions -> {
                StringRange range = suggestions.getRange();
                return new Suggestions(new StringRange(range.getStart() + i2, range.getEnd() + i2), suggestions.getList().stream().map(suggestion -> {
                    return new Suggestion(new StringRange(suggestion.getRange().getStart() + i2, suggestion.getRange().getEnd() + i2), suggestion.getText(), suggestion.getTooltip());
                }).toList());
            });
        } catch (Exception e) {
            return suggestionsBuilder.buildFuture();
        }
    }

    @Listen
    public void onKey(KeyEvent keyEvent) {
        if (!this.flag || this.mode_4 == null || this.string == null) {
            return;
        }
        KeybindFeature keybindFeature832 = this.mode_4.createKeybindFeature(this.string, new Keybind(keyEvent.getKey(), Keybind.KeybindMode.TOGGLE, keyEvent.isPressed()));
        ClientServices.macroManager.register(keybindFeature832);
        ChatUtils.sendMessage(Text.literal("Macro ").append(keybindFeature832.getAsText()).append(" has been created"), ChatUtils.createSignature(-1));
        this.flag = false;
    }
}
