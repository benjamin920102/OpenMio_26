package me.mioclient.command;

import me.mioclient.event.events.client.ClientTickEvent;
import me.mioclient.event.events.client.CommandDispatchEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.time.TickScheduler;

import me.mioclient.api.ClientServices;
import me.mioclient.util.time.TickScheduler;

import me.mioclient.event.EventBus;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.mioclient.event.Listen;
import net.minecraft.command.CommandSource;
import me.mioclient.event.events.client.CommandDispatchEvent;
import me.mioclient.event.events.client.ClientTickEvent;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_12.class */
public class WaitCommand extends CommandFeature {
    public final List<String> list;
    public boolean flag;
    public int num;

    public WaitCommand() {
        super("wait");
        this.list = new ArrayList();
        eventBus.register(this);
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("ticks", IntegerArgumentType.integer(0)).executes(commandContext -> {
            this.num = ((Integer) commandContext.getArgument("ticks", Integer.class)).intValue();
            this.flag = true;
            return 1;
        }));
    }

    @Listen
    public void onCommandDispatch(CommandDispatchEvent stringEvent) {
        if (this.flag) {
            this.list.add(stringEvent.getCommand());
            stringEvent.cancel();
        }
    }

    @Listen
    public void onClientTick(ClientTickEvent fontsEvent) {
        if (this.flag) {
            this.flag = false;
            ArrayList arrayList = new ArrayList(this.list);
            this.list.clear();
            TickScheduler tooltipsHelper = ClientServices.tooltipsHelper;
            java.lang.Runnable runnable = () -> {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ClientCommandRegistry.dispatch((String) it.next());
                }
            };
            tooltipsHelper.schedule(runnable, this.num);
        }
    }
}
