package me.mioclient.command;

import me.mioclient.api.ClientServices;

import me.mioclient.util.world.Waypoint;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.Waypoint;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WaypointArgumentType.class */
public class WaypointArgumentType implements com.mojang.brigadier.arguments.ArgumentType<Waypoint> {
    /* renamed from: parse, reason: merged with bridge method [inline-methods] */
    public Waypoint parse(StringReader stringReader) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String readString = stringReader.readString();
        for (Waypoint waypointsEnumSettingHelper : ClientServices.waypointsManager.getRegistry()) {
            if (waypointsEnumSettingHelper != null && waypointsEnumSettingHelper.getName() != null && waypointsEnumSettingHelper.getName().equalsIgnoreCase(readString)) {
                return waypointsEnumSettingHelper;
            }
        }
        throw new DynamicCommandExceptionType(obj -> {
            return Text.literal(String.format("Waypoint %s doesn't exists", obj));
        }).create(readString);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        ArrayList arrayList = new ArrayList();
        for (Waypoint waypointsEnumSettingHelper : ClientServices.waypointsManager.getRegistry()) {
            if (waypointsEnumSettingHelper != null && (waypointsEnumSettingHelper.getServer() == null || waypointsEnumSettingHelper.getDimension() == null || waypointsEnumSettingHelper.getName() == null)) {
                arrayList.add(waypointsEnumSettingHelper);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ClientServices.waypointsManager.unregister((Waypoint) it.next());
        }
        return CommandSource.suggestMatching(ClientServices.waypointsManager.getRegistry().stream().map((v0) -> {
            return v0.getName();
        }), suggestionsBuilder);
    }
}
