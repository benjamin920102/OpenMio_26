package me.mioclient.module.combat;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.Category;
import me.mioclient.module.Module;
import net.minecraft.ChatFormatting;

public class NoHitDelay extends Module {
    public NoHitDelay() {
        super("NoHitDelay", new StringTemplate().arg(String.valueOf(ChatFormatting.RED)).format("Removes the weapon attack delay. \n\u0001Only works on pre 1.9 servers."), Category.COMBAT, new String[0]);
    }
}