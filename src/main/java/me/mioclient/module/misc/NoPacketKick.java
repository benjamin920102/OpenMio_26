package me.mioclient.module.misc;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import me.mioclient.setting.BooleanSetting;

public class NoPacketKick extends Module {
    public Setting<Boolean> message = add(new BooleanSetting("Message", false));

    public NoPacketKick() {
        super("NoPacketKick", "Cancels broken packets that may cause you getting kicked.", Category.MISC, new String[0]);
        setDrawn(false);
    }

    public Component getText1411(Throwable th) {
        return Component.translatable("disconnect.genericReason", new Object[]{new StringTemplate().arg(String.valueOf(th)).format("Internal Exception: \u0001")}).withStyle(ChatFormatting.RED);
    }
}