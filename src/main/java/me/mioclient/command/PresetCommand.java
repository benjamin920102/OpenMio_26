package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.module.HudModule;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.gui.panel.PresetPanel;
import me.mioclient.gui.screen.PresetScreen;
import me.mioclient.manager.PresetEntry;
import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.manager.PresetEntry;
import me.mioclient.gui.screen.PresetScreen;
import me.mioclient.manager.PresetFileRegistry;
import me.mioclient.util.client.ChatUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.mioclient.api.Category;
import me.mioclient.api.Keybind;
import me.mioclient.command.EnumArgumentType;
import me.mioclient.module.Module;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import me.mioclient.module.HudModule;
import me.mioclient.gui.panel.PresetPanel;
import me.mioclient.mode.misc.PresetHelperMode;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_13.class */
public final class PresetCommand extends CommandFeature {
    public PresetCommand() {
        super("preset");
        setAliases("config", "cfg");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("category", new PresetCategoryArgumentType()).then(CommandFeature.literal("save").then(CommandFeature.argument("preset", StringArgumentType.string()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("preset", String.class);
            try {
                PresetCategoryArgumentType.getManager(commandContext, "category").savePreset(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(str).format("Preset \u0001 has been saved.")), ChatUtils.createSignature(-1));
            return 1;
        }))).then(CommandFeature.literal("delete").then(CommandFeature.argument("preset", new PresetArgumentType()).executes(commandContext2 -> {
            PresetEntry preset = PresetArgumentType.getPreset(commandContext2, "category", "preset");
            PresetCategoryArgumentType.getManager(commandContext2, "category").deletePreset(preset.getRawName());
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(preset.getRawName()).format("Preset \u0001 has been deleted.")), ChatUtils.createSignature(-1));
            return 1;
        }))).then(CommandFeature.literal("rename").then(CommandFeature.argument("preset", new PresetArgumentType()).then(CommandFeature.argument("name", StringArgumentType.string()).executes(commandContext3 -> {
            PresetEntry preset = PresetArgumentType.getPreset(commandContext3, "category", "preset");
            PresetCategoryArgumentType.getManager(commandContext3, "category").renamePreset(preset.getRawName(), (String) commandContext3.getArgument("name", String.class));
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg((String) commandContext3.getArgument("name", String.class)).arg(preset.getRawName()).format("The name of \u0001 has been set to \u0001.")), ChatUtils.createSignature(-1));
            return 1;
        })))).then(CommandFeature.literal("load").then(CommandFeature.argument("preset", new PresetArgumentType()).executes(commandContext4 -> {
            ClientServices.presetHelper.getSnapshot().do41();
            PresetHelperMode presetHelperMode = (PresetHelperMode) commandContext4.getArgument("category", PresetHelperMode.class);
            PresetEntry preset = PresetArgumentType.getPreset(commandContext4, "category", "preset");
            PresetFileRegistry manager = PresetCategoryArgumentType.getManager(commandContext4, "category");
            manager.do34();
            manager.loadPreset(preset.getRawName());
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(preset.getRawName()).format("Preset \u0001 has been loaded.")), ChatUtils.createSignature(-1));
            if (presetHelperMode != PresetHelperMode.ALL) {
                return 1;
            }
            runLater(this::displayPresetBinds);
            return 1;
        }))).then(CommandFeature.literal("specific").then(CommandFeature.argument("preset", new PresetArgumentType()).then(CommandFeature.argument("module_category", new EnumArgumentType(Category.class, "Category")).executes(commandContext5 -> {
            ClientServices.presetHelper.getSnapshot().do41();
            PresetEntry preset = PresetArgumentType.getPreset(commandContext5, "category", "preset");
            PresetFileRegistry manager = PresetCategoryArgumentType.getManager(commandContext5, "category");
            Category category = (Category) commandContext5.getArgument("module_category", Category.class);
            manager.do34();
            loadPresetForModules(preset, module -> {
                return module.getCategory() == category;
            });
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(category.getName()).arg(preset.getRawName()).format("Preset \u0001 for \u0001 has been loaded.")), ChatUtils.createSignature(-1));
            return 1;
        })).then(CommandFeature.argument("modules", new CollectionArgumentType(new ModuleArgumentType())).executes(commandContext6 -> {
            ClientServices.presetHelper.getSnapshot().do41();
            PresetEntry preset = PresetArgumentType.getPreset(commandContext6, "category", "preset");
            PresetFileRegistry manager = PresetCategoryArgumentType.getManager(commandContext6, "category");
            Set set = (Set) ((List) commandContext6.getArgument("modules", List.class)).stream().map((v0) -> {
                return v0.getClass();
            }).collect(Collectors.toSet());
            manager.do34();
            java.util.function.Predicate<Module> predicate = module -> {
                return set.contains(module.getClass());
            };
            loadPresetForModules(preset, predicate);
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(getString1574(predicate)).arg(preset.getRawName()).format("Preset \u0001 for \u0001 has been loaded.")), ChatUtils.createSignature(-1));
            return 1;
        })))).then(CommandFeature.literal("list").executes(commandContext7 -> {
            PresetFileRegistry manager = PresetCategoryArgumentType.getManager(commandContext7, "category");
            ChatUtils.sendMessage(Text.literal("Preset list: ").append(Texts.join(manager.getRegistry(), presetSearchHelper4 -> {
                return Text.literal(presetSearchHelper4.getRawName());
            })).append("."), ChatUtils.createSignature(-1));
            return 1;
        }))).then(CommandFeature.literal("export").then(CommandFeature.argument("module", new ModuleArgumentType()).executes(commandContext8 -> {
            Module module = (Module) commandContext8.getArgument("module", Module.class);
            JsonObject asJsonObject = module.toJson().getAsJsonObject();
            asJsonObject.remove("bind");
            asJsonObject.remove("toggled");
            asJsonObject.remove("key");
            minecraftClient.keyboard.setClipboard(Base64.getEncoder().encodeToString(asJsonObject.toString().getBytes(StandardCharsets.UTF_8)));
            ChatUtils.sendMessage(Text.literal("Successfully copied ").append(Text.literal(module.getName()).formatted(Formatting.GRAY)).append(" config to your clipboard"), ChatUtils.createSignature(-1));
            return 1;
        }))).then(CommandFeature.literal("import").then(CommandFeature.argument("module", new ModuleArgumentType()).executes(commandContext9 -> {
            Module module = (Module) commandContext9.getArgument("module", Module.class);
            try {
                module.fromJson(JsonParser.parseString(new String(Base64.getDecoder().decode(minecraftClient.keyboard.getClipboard().trim().replace("\n", "")))));
                ChatUtils.sendMessage(Text.literal("Successfully loaded ").append(Text.literal(module.getName()).formatted(Formatting.GRAY)).append(" config from your clipboard"), ChatUtils.createSignature(-1));
                return 1;
            } catch (Exception e) {
                MutableText append = Text.literal("Failed to load ").append(Text.literal(module.getName()).formatted(Formatting.GRAY)).append(" config");
                ChatUtils.sendPrefixedMessage((Text) (e.getStackTrace()[0].toString().contains("Base64") ? append.append(" due to invalid input") : append.append(" due to an unknown reason")), ChatUtils.createSignature(-1), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                e.printStackTrace();
                return 1;
            }
        })));
        literalArgumentBuilder.then(CommandFeature.literal("restore").executes(commandContext10 -> {
            JsonElement jsonElement1434 = ClientServices.presetHelper.getSnapshot().consumeJsonElement();
            if (jsonElement1434 == null) {
                return 1;
            }
            PresetHelperMode.ALL.fromJson(jsonElement1434);
            ChatUtils.sendMessage(Text.literal("Restored previous preset."), ChatUtils.createSignature(-1));
            return 1;
        }));
        literalArgumentBuilder.executes(commandContext11 -> {
            runLater(() -> {
                minecraftClient.setScreen(new PresetScreen());
            });
            return 1;
        });
    }

    public void loadPresetForModules(PresetEntry presetSearchHelper4, java.util.function.Predicate<Module> predicate) {
        for (Module module : ClientServices.moduleRegistry.getRegistry()) {
            if (!(module instanceof HudModule) && predicate.test(module)) {
                try {
                    module.fromJson(presetSearchHelper4.getJsonObject().getAsJsonObject().get(module.getConfigName()));
                } catch (Exception e) {
                }
            }
        }
    }

    public String getString1574(java.util.function.Predicate<Module> predicate) {
        return (String) ClientServices.moduleRegistry.getRegistry().stream().filter(predicate).map((v0) -> {
            return v0.getName();
        }).collect(Collectors.joining(", "));
    }

    public void displayPresetBinds() {
        MutableText empty = Text.empty();
        empty.append("Preset binds: ");
        for (Module module : ClientServices.moduleRegistry.getRegistry()) {
            Keybind keybind = module.getKeybind();
            if (!keybind.isUnbound()) {
                empty.append("\n");
                empty.append(module.getName());
                empty.append(" - ");
                empty.append(keybind.getBindString());
                empty.append(" ");
                empty.append(ChatUtils.bracketed(keybind.getMode().getName()));
            }
        }
        ChatUtils.sendMessage((Text) empty, -2);
    }
}
