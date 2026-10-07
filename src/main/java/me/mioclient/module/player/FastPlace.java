package me.mioclient.module.player;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.mixin.ducks.DuckMinecraftClient;
import me.mioclient.module.Module;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

public class FastPlace extends Module {
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 2, 0, 4));
    public Setting<Boolean> exp = add(new BooleanSetting("Exp", true));
    public Setting<Boolean> rockets = add(new BooleanSetting("Rockets", false));
    public Setting<Boolean> blocks = add(new BooleanSetting("Blocks", false));
    public Setting<Boolean> enderChests = add(new BooleanSetting("EnderChests", true));
    public Setting<Boolean> all = add(new BooleanSetting("All", false));
    public Setting<Boolean> autoPlace = add(new BooleanSetting("AutoPlace", false));

    public FastPlace() {
        super("FastPlace", "Removes the right-click delay for some items.", Category.PLAYER, new String[0]);
        setDrawn(false);
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        DuckMinecraftClient duckMinecraftClient = (DuckMinecraftClient)(minecraftClient);
        if (!minecraftClient.player.getMainHandItem().has(DataComponents.FOOD) && this.autoPlace.getValue().booleanValue() && minecraftClient.missTime == 0) {
            duckMinecraftClient.interact();
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (isNotInGame()) {
            return;
        }
        Item item = minecraftClient.player.getMainHandItem().getItem();
        if (((item instanceof ExperienceBottleItem) && this.exp.getValue().booleanValue()) || (((item instanceof FireworkRocketItem) && this.rockets.getValue().booleanValue()) || (((item instanceof BlockItem) && this.blocks.getValue().booleanValue()) || ((item == Items.ENDER_CHEST && this.enderChests.getValue().booleanValue()) || this.all.getValue().booleanValue())))) {
            minecraftClient.missTime = Math.min(minecraftClient.missTime, this.delay.getValue().intValue());
        }
    }
}