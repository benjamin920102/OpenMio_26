package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.io.HttpRequests;

import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.io.HttpRequests;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.awt.Desktop;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/CompletableFutureFeature.class */
public class CompletableFutureFeature extends CommandFeature {
    public final String string = "https://api.mojang.com/users/profiles/minecraft/";
    public final String string2 = "https://laby.net/api/user/%s/get-names";
    public CompletableFuture<List<Record>> completableFuture;

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/CompletableFutureFeature$Record.class */
    public static final class Record {

        @SerializedName("username")
        public final String string;

        @SerializedName("changed_at")
        public final String string2;

        @SerializedName("hidden")
        public final boolean flag;
        public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss");

        public Record(String str, String str2, boolean z) {
            this.string = str;
            this.string2 = str2;
            this.flag = z;
        }

        public String getFormattedDate() {
            if (this.string2 == null) {
                return "first name";
            }
            String str = this.string2.split("\\+")[0];
            try {
                LocalDateTime parse = LocalDateTime.parse(str);
                return simpleDateFormat.format(Date.from(parse.atZone(ZoneId.systemDefault()).toInstant()));
            } catch (Exception e) {
                return str;
            }
        }

        public String getDisplayName() {
            return this.flag ? "HIDDEN" : this.string;
        }




        @SerializedName("changed_at")
        public String getChangedAt() {
            return this.string2;
        }

        @SerializedName("hidden")
        public boolean isHidden() {
            return this.flag;
        }
    }

    public CompletableFutureFeature() {
        super("namehistory");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", new ExamplesArgumentType_3()).executes(commandContext -> {
            if (this.completableFuture != null && !this.completableFuture.isDone() && !this.completableFuture.isCompletedExceptionally()) {
                return 1;
            }
            String str = (String) commandContext.getArgument("name", String.class);
            this.completableFuture = getCompletableFuture1179(str);
            this.completableFuture.thenAccept(list -> {
                ClientServices.tooltipsHelper.schedule(() -> {
                    displayNameHistory(list);
                }, 0);
            });
            this.completableFuture.exceptionally(th -> {
                ClientServices.tooltipsHelper.schedule(() -> {
                    do1182(th);
                }, 0);
                return null;
            });
            ChatUtils.sendMessage(Text.literal("Loading name history for ").append(themedLiteral(str)), ChatUtils.createSignature(-3));
            return 1;
        }));
        literalArgumentBuilder.then(CommandFeature.literal("open").then(CommandFeature.argument("name", new ExamplesArgumentType_3()).executes(commandContext2 -> {
            try {
                Desktop.getDesktop().browse(new URL(new StringTemplate().arg((String) commandContext2.getArgument("name", String.class)).format("https://namemc.com/profile/\u0001")).toURI());
                return 1;
            } catch (Throwable th) {
                return 1;
            }
        })));
    }

    public CompletableFuture<List<Record>> getCompletableFuture1179(String str) {
        return CompletableFuture.supplyAsync(() -> {
            JsonElement parseString = JsonParser.parseString((String) HttpRequests.sendSyncRequest(HttpRequests.newGetRequest(new StringTemplate().arg(str).format("https://api.mojang.com/users/profiles/minecraft/\u0001")).build()).body());
            if (parseString.isJsonObject() && parseString.getAsJsonObject().has("id")) {
                return getList1180((String) HttpRequests.sendSyncRequest(HttpRequests.newGetRequest("https://laby.net/api/user/%s/get-names".formatted(parseString.getAsJsonObject().get("id").getAsString())).build()).body());
            }
            throw new java.lang.RuntimeException("Player not found");
        }, executorService);
    }

    public List<Record> getList1180(String str) {
        JsonElement parseString = JsonParser.parseString(str);
        if (!parseString.isJsonArray()) {
            return null;
        }
        return (List) parseString.getAsJsonArray().asList().stream().map(jsonElement -> {
            return (Record) gson.fromJson(jsonElement, Record.class);
        }).collect(Collectors.toList());
    }

    public void displayNameHistory(List<Record> list) {
        if (list == null) {
            return;
        }
        try {
            Text empty = Text.empty();
            ((MutableText) empty).append(themedLiteral(list.get(list.size() - 1).getDisplayName()));
            ((MutableText) empty).append("'s Name History: \n");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                Record record = list.get(i);
                sb.append("%d. %s (%s)".formatted(Integer.valueOf(i + 1), record.getDisplayName(), record.getFormattedDate()));
                if (i != list.size() - 1) {
                    sb.append('\n');
                }
            }
            ((MutableText) empty).append(sb.toString());
            ChatUtils.sendMessage(empty, ChatUtils.createSignature(-3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void do1182(Throwable th) {
        ChatUtils.sendPrefixedMessage(Text.literal(th.getCause().getMessage()), ChatUtils.createSignature(-3), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
    }
}
