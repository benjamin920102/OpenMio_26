package me.mioclient.module.misc;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.text.StringTemplate;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/NoPacketKick.class */
public class NoPacketKick extends Module {
    public Setting<Boolean> message = add(new BooleanSetting("Message", false));

    public NoPacketKick() {
        super("NoPacketKick", "Cancels broken packets that may cause you getting kicked.", Category.MISC, new String[0]);
        setDrawn(false);
    }

    public Text getText1411(Throwable th) {
        return Text.translatable("disconnect.genericReason", new Object[]{new StringTemplate().arg(String.valueOf(th)).format("Internal Exception: \u0001")}).formatted(Formatting.RED);
    }
}
