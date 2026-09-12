package me.mioclient.module.misc;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.AddMessageEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.ChatEvent;
import me.mioclient.module.Module;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.Text;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/ChatFilter.class */
public class ChatFilter extends Module {
    public Setting<Boolean> caseSensitive = add(new BooleanSetting("CaseSensitive", false));
    public Setting<Boolean> ignoreSelf = add(new BooleanSetting("IgnoreSelf", true));
    public String string;

    public ChatFilter() {
        super("ChatFilter", "Filters chat messages based on your filters.", Category.MISC, new String[0]);
        this.string = null;
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (ClientServices.chatFilterHelper.isEmpty()) {
            ChatUtils.sendPrefixedMessage(Text.of("You don't have any chat filters set. Use the \"%schatfilter add <id> <filter>\" command to add new filters.".formatted(ClientCommandRegistry.getPrefix())), ChatUtils.signatureOfModule(this), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
        }
        this.string = null;
    }

    @Listen(get219= RenderConstants.num)
    public void onAddMessage(AddMessageEvent addMessageEvent) {
        String string;
        if (addMessageEvent.getPhase() != KeyPearlMode.Pre || addMessageEvent.getText() == null) {
            return;
        }
        MessageIndicator messageIndicator2284 = addMessageEvent.getIndicator();
        if ((messageIndicator2284 == null || messageIndicator2284 == MessageIndicator.system() || messageIndicator2284 == MessageIndicator.singlePlayer() || messageIndicator2284 == MessageIndicator.notSecure()) && (string = addMessageEvent.getText().getString()) != null) {
            if (this.ignoreSelf.getValue().booleanValue() && this.string != null && string.contains(this.string)) {
                this.string = null;
            } else {
                if (ClientServices.chatFilterHelper.isEmpty() || !ClientServices.chatFilterHelper.is2677(string)) {
                    return;
                }
                addMessageEvent.cancel();
            }
        }
    }

    @Listen
    public void onChat(ChatEvent event) {
        if (event.isCancelled() || event.getMessage() == null || !this.ignoreSelf.getValue().booleanValue()) {
            return;
        }
        this.string = event.getMessage();
    }
}
