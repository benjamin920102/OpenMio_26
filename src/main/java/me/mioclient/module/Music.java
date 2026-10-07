package me.mioclient.module;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.util.client.MusicTrackState;

import me.mioclient.manager.ConfigManager;

import java.nio.file.Path;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.apache.commons.lang3.SystemUtils;

public class Music extends me.mioclient.module.HudModule {
    public static Path path;

    public Music() {
        super("Music", new String[0]);
        setElement(new ResizableHudElement(this));
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (SystemUtils.IS_OS_WINDOWS_10 || SystemUtils.OS_NAME.startsWith("Windows 11")) {
            return;
        }
        setToggled(false);
    }

    @Override // me.mioclient.module.HudModule
    public void renderContent(GuiGraphicsExtractor drawContext) {
        if (isTrackInactive()) {
            return;
        }
        ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, getBindString(), 0.0f, 0.0f, getHudColor(this.moduleListSearchHelper4.getY()));
    }

    @Override // me.mioclient.module.HudModule
    public float[] getSize() {
        return isTrackInactive() ? new float[]{0.0f, 0.0f} : new float[]{ClientTextRenderer.INSTANCE.getStringWidth(getBindString()), ClientTextRenderer.INSTANCE.getFontHeight()};
    }

    public static Path getMusicDirectory() {
        if (path == null) {
            path = ConfigManager.path.resolve("music");
        }
        return path;
    }

    public String getBindString() {
        return (!isInHudEditor() || MusicTrackState.num == 4) ? MusicTrackState.getString2782() : "HydrachFM - f3dot";
    }

    public boolean isTrackInactive() {
        return (isInHudEditor() || MusicTrackState.num == 4) ? false : true;
    }
}