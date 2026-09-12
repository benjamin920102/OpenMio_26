package me.mioclient.module.misc;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.AddMessageEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/PMSound.class */
public class PMSound extends Module {
    public Setting<SoundIdentifier> type = add(new SoundIdentifierSetting("Type", new SoundIdentifier("system", "vk")));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 0.5f, 0.0f, 1.0f));

    public PMSound() {
        super("PMSound", "Plays a sound whenever you get a private message.", Category.MISC, new String[0]);
    }

    @Listen(get219= RenderConstants.num2)
    public void onAddMessage(AddMessageEvent addMessageEvent) {
        if (!addMessageEvent.isCancelled() && addMessageEvent.getPhase() == KeyPearlMode.Pre && addMessageEvent.getText() != null && ChatUtils.isIncomingWhisper(addMessageEvent.getText().getString())) {
            minecraftClient.executeSync(() -> {
                ClientServices.killstreakSoundManager.findSoundPlayer(this.type.getValue()).play(this.volume.getValue().floatValue());
            });
        }
    }
}
