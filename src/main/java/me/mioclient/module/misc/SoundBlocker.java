package me.mioclient.module.misc;

import java.util.Iterator;
import java.util.Set;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.sound.PlayEvent;
import me.mioclient.module.Module;
import net.minecraft.sound.SoundEvent;
import me.mioclient.setting.EntityListObjectSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/SoundBlocker.class */
public class SoundBlocker extends Module {
    public Setting<Set<SoundEvent>> sounds = add(new EntityListObjectSetting<>("Sounds", Registries.SOUND_EVENT));

    public SoundBlocker() {
        super("SoundBlocker", "Blocks certain sounds from being played.", Category.MISC, new String[0]);
    }

    @Listen
    public void onPlay(PlayEvent playEvent) {
        Iterator<SoundEvent> it = this.sounds.getValue().iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(playEvent.getSoundInstance().getId())) {
                playEvent.cancel();
                return;
            }
        }
    }
}
