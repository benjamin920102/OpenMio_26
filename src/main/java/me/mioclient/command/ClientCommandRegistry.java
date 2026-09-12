package me.mioclient.command;

import me.mioclient.event.events.client.CommandDispatchEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.CollectionRegistry;
import me.mioclient.util.registry.NamedRegistryList;

import me.mioclient.mode.render.NameTagsHelperMode;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.JsonConfigurable;
import me.mioclient.util.registry.NamedRegistryList;
import me.mioclient.loader.ModInitializerBridge;

import me.mioclient.event.EventBus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Iterator;
import java.util.List;
import me.mioclient.event.events.input.CharEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import me.mioclient.module.client.UI;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import me.mioclient.event.events.client.CommandDispatchEvent;
import me.mioclient.command.BaritoneCommand;
import me.mioclient.util.registry.CollectionRegistry;
import me.mioclient.command.CommandFeature;
import me.mioclient.command.LiteralArgumentBuilder_2;
import me.mioclient.command.ModuleSettingCommand;
import me.mioclient.command.NameTagsCommand;
import me.mioclient.mode.render.NameTagsHelperMode;
import me.mioclient.command.LiteralArgumentBuilder;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ClientCommandRegistry.class */
public final class ClientCommandRegistry extends NamedRegistryList<CommandFeature> implements ClientAccessor, JsonConfigurable {
    public static UI clickgui = (UI) ClientServices.moduleInjector.getModule(UI.class);
    public static final CommandSource commandSource = new ClientCommandSource((ClientPlayNetworkHandler) null, minecraftClient);
    public static final CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
    public static String string = ";";

    public ClientCommandRegistry() {
        eventBus.register(this);
        ModInitializerBridge.do1353(this);
        registerBaritoneCommands();
        registerNameTagCommands();
        registerModuleCommands();
    }

    public static String getPrefix() {
        return string;
    }

    public static void setPrefix(String str) {
        if (str.isBlank()) {
            return;
        }
        String substring = str.substring(0, 1);
        string = substring;
        clickgui.prefix.setValue(substring);
    }

    public static char getPrefixChar() {
        return string.charAt(0);
    }

    public void registerBaritoneCommands() {
        if (ClientServices.obstaclePasserHelper.isBaritoneLoaded()) {
            register(new BaritoneCommand());
        }
    }

    public void registerModuleCommands() {
        for (Module module : ClientServices.moduleRegistry.getRegistry()) {
            if (!findFirst(feature -> {
                return feature.getName().equalsIgnoreCase(module.getName());
            }).isPresent()) {
                for (String str : module.getAliases()) {
                    LiteralArgumentBuilder_2 literalArgumentBuilder_2 = new LiteralArgumentBuilder_2(str.toLowerCase());
                    new ModuleSettingCommand(module).exec(literalArgumentBuilder_2);
                    commandDispatcher.register(literalArgumentBuilder_2);
                }
            }
        }
    }

    public static void dispatch(String str) {
        CommandDispatchEvent stringEvent = new CommandDispatchEvent(str);
        ClientAccessor.eventBus.fireEvent(stringEvent);
        if (stringEvent.isCancelled()) {
            return;
        }
        try {
            commandDispatcher.execute(commandDispatcher.parse(str, commandSource));
        } catch (CommandSyntaxException e) {
        }
    }

    public void registerNameTagCommands() {
        register(new NameTagsCommand(NameTagsHelperMode.FRIEND));
        register(new NameTagsCommand(NameTagsHelperMode.ENEMY));
    }

    @Override // me.mioclient.util.registry.NamedRegistryList, me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is2988, reason: merged with bridge method [inline-methods] */
    public boolean register(CommandFeature feature) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literal = com.mojang.brigadier.builder.LiteralArgumentBuilder.literal(feature.getName());
        feature.exec(literal);
        LiteralCommandNode register = commandDispatcher.register(literal);
        for (String str : feature.getAliases()) {
            LiteralArgumentBuilder literalArgumentBuilder = new LiteralArgumentBuilder(str);
            Iterator it = register.getChildren().iterator();
            while (it.hasNext()) {
                literalArgumentBuilder.then((CommandNode) it.next());
            }
            literalArgumentBuilder.executes(commandContext -> {
                return register.getCommand().run(commandContext);
            });
            commandDispatcher.register(literalArgumentBuilder);
        }
        return ((List) this.registry).add(feature);
    }

    @Listen
    public void onChar(CharEvent charEvent) {
        if (getPrefix().length() == 1 && getPrefix().charAt(0) == charEvent.getChar()) {
            minecraftClient.setScreen(new ChatScreen(""));
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("prefix", string);
        return jsonObject;
    }

    @Override // me.mioclient.api.JsonConfigurable
    public void fromJson(JsonElement jsonElement) {
        if (jsonElement.getAsJsonObject().has("prefix")) {
            setPrefix(jsonElement.getAsJsonObject().get("prefix").getAsString());
        }
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return "commands.json";
    }
}
