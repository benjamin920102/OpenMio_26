package me.mioclient.ducks;

import java.util.List;
import net.minecraft.client.multiplayer.chat.GuiMessage;

public interface DuckChatHud {
    List<GuiMessage.Line> getVisible();
}
