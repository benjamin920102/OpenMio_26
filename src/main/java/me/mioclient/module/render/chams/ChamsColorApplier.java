package me.mioclient.module.render.chams;

import me.mioclient.api.ClientServices;

import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import me.mioclient.module.client.Colors;
import me.mioclient.module.render.Chams;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ChamsColorApplier.class */
public interface ChamsColorApplier extends ClientAccessor {
    public static final Colors colors = (Colors) ClientServices.moduleInjector.getModule(Colors.class);
    public static final Color color = new Color(0, 0, 0, 0);

    default void applyChamsRender(Chams chams, Entity entity, MatrixStack matrixStack) {
        Color[] colorArray593 = getChamsColors(chams);
        if (chams.fade.getValue().booleanValue()) {
            float f = chams.get2044(entity);
            colorArray593 = new Color[]{ColorUtils.withAlpha(colorArray593[0], (int) (f * colorArray593[0].getAlpha())), ColorUtils.withAlpha(colorArray593[1], (int) (f * colorArray593[1].getAlpha()))};
        }
        ChamsRenderDispatcher.setChamsColors(colorArray593[0], colorArray593[1]);
        if (chams.friends.getValue().booleanValue() && (entity instanceof PlayerEntity)) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            if (ClientServices.socialManager.isFriendEntity(playerEntity)) {
                overrideChamsColor(chams, colorArray593, colors.friendColor.getValue());
            }
            if (ClientServices.socialManager.isEnemyEntity(playerEntity)) {
                overrideChamsColor(chams, colorArray593, colors.enemyColor.getValue());
            }
        }
        ChamsRenderDispatcher.setExtraLayer(chams.extraLayer.getValue().booleanValue());
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity2 = (PlayerEntity) entity;
            if (chams.is2048()) {
                playerEntity2.limbAnimator.pos = 0.0f;
                playerEntity2.limbAnimator.speed = 0.0f;
                playerEntity2.limbAnimator.prevSpeed = 0.0f;
            }
        }
        ChamsRenderDispatcher.renderEntityChams(matrixStack, entity);
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
