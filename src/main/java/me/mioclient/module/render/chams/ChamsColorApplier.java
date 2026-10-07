package me.mioclient.module.render.chams;

import me.mioclient.api.ClientServices;

import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;

import java.awt.Color;
import me.mioclient.module.client.Colors;
import me.mioclient.module.render.Chams;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface ChamsColorApplier extends ClientAccessor {
    /* Interfaces cannot hold mutable static state, so the cached ref lives in
       a nested holder. Resolved lazily -- Colors can register after the first
       chams lookup, and an eager lookup left the ref null (NPE). */
    final class ColorsHolder {
        static Colors colors;
    }

    static Colors getColors() {
        if (ColorsHolder.colors == null && ClientServices.moduleInjector != null) {
            try {
                ColorsHolder.colors = (Colors) ClientServices.moduleInjector.getModule(Colors.class);
            } catch (Exception e) {
            }
        }
        return ColorsHolder.colors;
    }
    public static final Color color = new Color(0, 0, 0, 0);

    default void applyChamsRender(Chams chams, Entity entity, PoseStack matrixStack) {
        Color[] colorArray593 = getChamsColors(chams);
        if (chams.fade.getValue().booleanValue()) {
            float f = chams.get2044(entity);
            colorArray593 = new Color[]{ColorUtils.withAlpha(colorArray593[0], (int) (f * colorArray593[0].getAlpha())), ColorUtils.withAlpha(colorArray593[1], (int) (f * colorArray593[1].getAlpha()))};
        }
        ChamsRenderDispatcher.setChamsColors(colorArray593[0], colorArray593[1]);
        if (chams.friends.getValue().booleanValue() && (entity instanceof Player)) {
            Player playerEntity = (Player) entity;
            if (ClientServices.socialManager.isFriendEntity(playerEntity)) {
                overrideChamsColor(chams, colorArray593, getColors().friendColor.getValue());
            }
            if (ClientServices.socialManager.isEnemyEntity(playerEntity)) {
                overrideChamsColor(chams, colorArray593, getColors().enemyColor.getValue());
            }
        }
        ChamsRenderDispatcher.setExtraLayer(chams.extraLayer.getValue().booleanValue());
        if (entity instanceof Player) {
            Player playerEntity2 = (Player) entity;
            if (chams.is2048()) {
                playerEntity2.walkAnimation.stop();
            }
        }
        /* 26.2 textured chams: the model itself is tinted during raster (see
           MixinModelFeatureRenderer), so no manual box walk happens here. Ghost
           entities outside vanilla's submit flow keep their own box rendering. */
    }

    default void overrideChamsColor(Chams chams, Color[] colorArr, Color color2) {
        ChamsRenderDispatcher.setChamsColors(ColorUtils.withAlpha(color2, colorArr[0].getAlpha()), ColorUtils.withAlpha(color2, colorArr[1].getAlpha()));
    }

    default Color[] getChamsColors(Chams chams) {
        return new Color[]{chams.outline.getValue(), chams.fill.getValue()};
    }

    default boolean is594() {
        return false;
    }
}