package me.mioclient.command;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.inventory.SlotPair;

import me.mioclient.util.inventory.SlotPair;
import me.mioclient.api.NamedValue;

import me.mioclient.event.EventBus;

import com.mojang.brigadier.context.CommandContext;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.command.EnumArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import me.mioclient.mixin.ducks.DuckMinecraftClient;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_9.class */
public class ActionCommand extends CommandFeature {
    public final Map<Mode, List<Record>> map;
    public boolean flag;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/Feature_9$Mode.class */
    private enum Mode implements NamedValue {
        SWAP("swap", item -> {
            return true;
        }, new SelectSlotAction()),
        ALTERNATIVE("alternative", item2 -> {
            return true;
        }, new HotbarSwapAction()),
        ARMOR("armor", ActionCommand::is1295, new ArmorSwapAction()),
        OFFHAND("offhand", item3 -> {
            return true;
        }, new OffhandSwapAction());

        public final String name;
        public final ItemSwapHandler swapHandler;
        public final java.util.function.Predicate<Item> predicate;

        Mode(String str, java.util.function.Predicate<Item> predicate, ItemSwapHandler swapHandler) {
            this.name = str;
            this.predicate = predicate;
            this.swapHandler = swapHandler;
        }

        public boolean testItem(Item item) {
            return this.predicate.test(item);
        }

        public ItemSwapHandler getSwapHandler() {
            return this.swapHandler;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/Feature_9$Record.class */
    private static final class Record {
        public final ItemSwapHandler itemSwapHandler;
        public final SlotPair data_4;

        public Record(ItemSwapHandler itemSwapHandler, SlotPair data_4) {
            this.itemSwapHandler = itemSwapHandler;
            this.data_4 = data_4;
        }




        public ItemSwapHandler getItemDataHandler764() {
            return this.itemSwapHandler;
        }

        public SlotPair getItemSlotData765() {
            return this.data_4;
        }
    }

    public ActionCommand() {
        super("action");
        this.map = new HashMap();
        eventBus.register(this);
        for (Mode mode : Mode.values()) {
            this.map.put(mode, new ArrayList());
        }
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("swap", new EnumArgumentType(Mode.class)).then(CommandFeature.argument("item", BiPredicateArgumentType.registry(Registries.ITEM, this::isItemValidForSwap)).executes(commandContext -> {
            Item item = (Item) commandContext.getArgument("item", Item.class);
            Mode mode = (Mode) commandContext.getArgument("swap", Mode.class);
            SlotPair data_4253 = mode.getSwapHandler().getSlotPair(item);
            if (data_4253.isInvalid()) {
                this.flag = true;
            }
            this.map.get(mode).add(new Record(mode.getSwapHandler(), data_4253));
            if (data_4253.isInvalid()) {
                return 1;
            }
            mode.getSwapHandler().swapForward(data_4253);
            return 1;
        })).then(CommandFeature.literal("back").executes(commandContext2 -> {
            List<Record> list = this.map.get((Mode) commandContext2.getArgument("swap", Mode.class));
            if (list.isEmpty()) {
                return 1;
            }
            Record record = (Record) list.removeLast();
            if (record.getItemSlotData765().isInvalid()) {
                return 1;
            }
            record.getItemDataHandler764().swapBackward(record.data_4);
            return 1;
        })));
        literalArgumentBuilder.then(CommandFeature.literal("attack").executes(commandContext3 -> {
            if (this.flag) {
                return 1;
            }
            ((DuckMinecraftClient) minecraftClient).attack();
            return 1;
        }));
        literalArgumentBuilder.then(CommandFeature.literal("interact").executes(commandContext4 -> {
            if (this.flag) {
                return 1;
            }
            ((DuckMinecraftClient) minecraftClient).interact();
            return 1;
        }));
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.flag = false;
    }

    public boolean isItemValidForSwap(CommandContext<?> commandContext, Item item) {
        return ((Mode) commandContext.getArgument("swap", Mode.class)).testItem(item);
    }

    public static boolean is1295(Item item) {
        if (item instanceof AnimalArmorItem) {
            return false;
        }
        return (item instanceof ArmorItem) || item == Items.ELYTRA;
    }
}
