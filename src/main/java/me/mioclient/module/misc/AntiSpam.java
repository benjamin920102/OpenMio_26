package me.mioclient.module.misc;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.Tuple;

import me.mioclient.util.math.Tuple;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import me.mioclient.ducks.DuckChatHud;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.AddMessageEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/AntiSpam.class */
public class AntiSpam extends Module {
    public static final Pattern pattern = Pattern.compile("(https?:\\/\\/)?([\\w\\-])+\\.{1}([a-zA-Z]{2,63})([\\/\\w-]*)*\\/?\\??([^#\\n\\r]*)?#?([^\\n\\r]*)");
    public final Map<String, Tuple<Long, ChatHudLine.Visible>> map;
    public Setting<Boolean> indicator = add(new BooleanSetting("Indicator", true));
    public Setting<Boolean> links = add(new BooleanSetting("Links", false));
    public String string;

    public AntiSpam() {
        super("AntiSpam", "Stacks similar chat messages with each other.", Category.MISC, new String[0]);
        this.map = new HashMap();
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.map.clear();
        this.string = null;
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        this.map.clear();
        this.string = null;
    }

    @Listen
    public void onAddMessage(AddMessageEvent addMessageEvent) {
        if (addMessageEvent.getPhase() != KeyPearlMode.Pre) {
            if (this.string != null) {
                this.map.computeIfPresent(this.string, (str, holeSnapData_2) -> {
                    return new Tuple(Long.valueOf(((Long) holeSnapData_2.getLeft()).longValue() + 1), addMessageEvent.getVisible());
                });
                return;
            }
            return;
        }
        if (addMessageEvent.getSignature() == null || addMessageEvent.getSignature().toByteBuffer().getInt() >= 0) {
            this.string = addMessageEvent.getText().getString().toLowerCase(Locale.ROOT);
            this.map.computeIfPresent(this.string, (str2, holeSnapData_22) -> {
                ((DuckChatHud) minecraftClient.inGameHud.getChatHud()).getVisible().remove(holeSnapData_22.getRight());
                return holeSnapData_22;
            });
            this.map.putIfAbsent(this.string, new Tuple<>(1L, addMessageEvent.getVisible()));
            if (this.links.getValue().booleanValue()) {
                if (pattern.matcher(this.string).find()) {
                    addMessageEvent.cancel();
                }
            }
            long longValue = this.map.get(this.string).getLeft().longValue();
            if (longValue <= 1 || !this.indicator.getValue().booleanValue()) {
                return;
            }
            MutableText append = Text.empty().append(addMessageEvent.getText());
            addMessageEvent.setText(append.append(Text.literal(new StringTemplate().arg(longValue).format(" [x\u0001]"))));
        }
    }
}
