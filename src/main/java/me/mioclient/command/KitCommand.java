package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.inventory.InventoryKit;

import me.mioclient.api.ClientServices;
import me.mioclient.util.inventory.InventoryKit;
import me.mioclient.util.client.ChatUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_24.class */
public class KitCommand extends CommandFeature {
    public KitCommand() {
        super("kit");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.literalArgument("save", "add").then(CommandFeature.argument("name", StringArgumentType.string()).executes(commandContext -> {
            String str = (String) commandContext.getArgument("name", String.class);
            ClientServices.chestStealerHelper.saveMapCache(str);
            ChatUtils.sendMessage(Text.literal("Kit %s has been saved".formatted(str)), ChatUtils.createSignature(-1));
            return 1;
        })));
        literalArgumentBuilder.then(CommandFeature.literalArgument("remove", "delete", "del").then(CommandFeature.argument("kit", new ChestStealerKitArgumentType()).executes(commandContext2 -> {
            InventoryKit chestStealerEnumSettingHelper = (InventoryKit) commandContext2.getArgument("kit", InventoryKit.class);
            ClientServices.chestStealerHelper.getRegistry().removeIf(chestStealerEnumSettingHelper2 -> {
                return chestStealerEnumSettingHelper.getName().equalsIgnoreCase(chestStealerEnumSettingHelper2.getName());
            });
            ChatUtils.sendMessage(Text.literal("Kit %s has been removed".formatted(chestStealerEnumSettingHelper.getName())), ChatUtils.createSignature(-1));
            return 1;
        })));
        literalArgumentBuilder.then(CommandFeature.literal("load").then(CommandFeature.argument("kit", new ChestStealerKitArgumentType()).executes(commandContext3 -> {
            InventoryKit chestStealerEnumSettingHelper = (InventoryKit) commandContext3.getArgument("kit", InventoryKit.class);
            ClientServices.chestStealerHelper.do258(chestStealerEnumSettingHelper.getName());
            ChatUtils.sendMessage(Text.literal("Kit %s has been loaded".formatted(chestStealerEnumSettingHelper.getName())), ChatUtils.createSignature(-1));
            return 1;
        })));
    }
}
