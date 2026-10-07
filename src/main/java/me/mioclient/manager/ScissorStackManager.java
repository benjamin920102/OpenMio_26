package me.mioclient.manager;

import me.mioclient.api.ClientAccessor;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.LinkedList;
import org.joml.Vector4i;

public class ScissorStackManager implements ClientAccessor {
    public static final LinkedList<Vector4i> linkedList = new LinkedList<>();

    public static void pushClipRegion(int i, int i2, int i3, int i4) {
        double scaleFactor = minecraftClient.getWindow().getGuiScale();
        Vector4i vector4i = new Vector4i((int) (i * scaleFactor), (int) ((minecraftClient.getWindow().getGuiScaledHeight() - (i2 + i4)) * scaleFactor), (int) (i3 * scaleFactor), (int) (i4 * scaleFactor));
        RenderSystem.enableScissorForRenderTypeDraws(vector4i.x, vector4i.y, vector4i.z, vector4i.w);
        linkedList.addLast(vector4i);
    }

    public static void popClipRegion() {
        RenderSystem.disableScissorForRenderTypeDraws();
        linkedList.removeLast();
        if (linkedList.isEmpty()) {
            return;
        }
        Vector4i last = linkedList.getLast();
        RenderSystem.enableScissorForRenderTypeDraws(last.x, last.y, last.z, last.w);
    }
}
