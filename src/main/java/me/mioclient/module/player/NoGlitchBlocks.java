package me.mioclient.module.player;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.world.BlockPlacer;

import me.mioclient.network.PacketSender;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.BreakBlockEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.world.item.BlockItem;
import net.minecraft.ChatFormatting;
import me.mioclient.setting.BooleanSetting;

public class NoGlitchBlocks extends Module {
    public Setting<Boolean> place = add(new BooleanSetting("Place", true));
    public Setting<Boolean> break_ = add(new BooleanSetting("Break", true));

    public NoGlitchBlocks() {
        super("NoGlitchBlocks", new StringTemplate().arg(String.valueOf(ChatFormatting.RED)).format("Makes sure there are no ghost blocks as you interact with them. \n\u0001Not recommended on 2b2t/Grim servers."), Category.PLAYER, new String[0]);
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        BlockItem item = (minecraftClient.player.getItemInHand(interactBlockEvent.getHand()).getItem()) instanceof BlockItem ? (BlockItem) (minecraftClient.player.getItemInHand(interactBlockEvent.getHand()).getItem()) : null;
        if (this.place.getValue().booleanValue() && (item instanceof BlockItem)) {
            BlockItem blockItem = item;
            if (BlockPlacer.canPlaceAt(interactBlockEvent.getHitResult().getBlockPos().relative(interactBlockEvent.getHitResult().getDirection()), blockItem.getBlock(), false)) {
                PacketSender.sendInteractBlock(interactBlockEvent.getHand(), interactBlockEvent.getHitResult());
                minecraftClient.player.swing(interactBlockEvent.getHand());
                interactBlockEvent.cancel();
            }
        }
    }

    @Listen
    public void onBreakBlock(BreakBlockEvent breakBlockEvent) {
        if (this.break_.getValue().booleanValue()) {
            breakBlockEvent.cancel();
        }
    }
}