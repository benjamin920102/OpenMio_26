package me.mioclient.module.render;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;

import me.mioclient.render.RenderUtil;

import java.awt.Color;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Markers extends Module {
    public Setting<Float> setting;
    public Setting<Boolean> setting2;

    public Markers() {
        super("Markers", Category.RENDER, new String[0]);
    }

    @Listen
    public void onHudRender(HudRenderEvent matrixStackEvent_2) {
        if (this.setting == null || this.setting2 == null) {
            return;
        }
        float lerp = Mth.lerp(RenderUtil.getPartialTick(), minecraftClient.player.yRotO, minecraftClient.player.getYRot());
        for (Player playerEntity : minecraftClient.level.players()) {
            double distanceTo = minecraftClient.player.distanceTo((Entity) playerEntity);
            if (minecraftClient.player != playerEntity && distanceTo <= Double.longBitsToDouble(4629700416936869888L)) {
                float longBitsToDouble = (float) (Double.longBitsToDouble(4607182418800017408L) - Mth.clamp((distanceTo - Double.longBitsToDouble(4628574517030027264L)) / Double.longBitsToDouble(4616189618054758400L), 0.0d, Double.longBitsToDouble(4607182418800017408L)));
                if (longBitsToDouble >= Double.longBitsToDouble(4591870180066957722L)) {
                    float f = RotationManager.getRotations(getInterpolatedPos(minecraftClient.player), getInterpolatedPos(playerEntity))[0];
                    if (!this.setting2.getValue().booleanValue() || Mth.degreesDifferenceAbs(f, lerp) > ((Integer) minecraftClient.options.fov().get()).intValue() / Float.intBitsToFloat(1073741824)) {
                        double radians = Math.toRadians((f - lerp) - MathConstants.num2);
                        float f2 = ClientTextRenderer.INSTANCE.getStringWidth(">") * Float.intBitsToFloat(1056964608);
                        float clamp = Mth.clamp(minecraftClient.player.getXRot() + Float.intBitsToFloat(1106247680), -MathConstants.num2, MathConstants.num2) / MathConstants.num2;
                        float cos = (float) ((Math.cos(radians) * this.setting.getValue().floatValue()) + (matrixStackEvent_2.getDrawContext().guiWidth() * MathConstants.val2));
                        float sin = (float) ((Math.sin(radians) * this.setting.getValue().floatValue() * clamp) + (matrixStackEvent_2.getDrawContext().guiHeight() * MathConstants.val2));
                        matrixStackEvent_2.getDrawContext().pose().pushMatrix();
                        matrixStackEvent_2.getDrawContext().pose().translate(cos, sin);
                        matrixStackEvent_2.getDrawContext().pose().rotate((float) radians);
                        ClientTextRenderer.INSTANCE.drawText(matrixStackEvent_2.getDrawContext(), ">", -f2, (float) ((-ClientTextRenderer.INSTANCE.getFontHeight()) * MathConstants.val2), ColorUtils.withAlpha(getMarkerColor(playerEntity), (int) (longBitsToDouble * Float.intBitsToFloat(1132396544))));
                        matrixStackEvent_2.getDrawContext().pose().popMatrix();
                    }
                }
            }
        }
    }

    public Color getMarkerColor(Player playerEntity) {
        if (ClientServices.socialManager.isFriendEntity(playerEntity)) {
            return ClientServices.socialManager.getFriendColor();
        }
        if (ClientServices.socialManager.isEnemyEntity(playerEntity)) {
            return ClientServices.socialManager.getEnemyColor();
        }
        return ColorUtils.lerpColor(Color.red, Color.green, Float.intBitsToFloat(1065353216) - ((float) ((Mth.clamp(minecraftClient.player.getEyePosition().distanceTo(playerEntity.position()), Double.longBitsToDouble(4620693217682128896L), Double.longBitsToDouble(4629700416936869888L)) - Double.longBitsToDouble(4620693217682128896L)) / Double.longBitsToDouble(4627448617123184640L))));
    }

    public Vec3 getInterpolatedPos(Player playerEntity) {
        return new Vec3(Mth.lerp(RenderUtil.getPartialTick(), playerEntity.xo, playerEntity.getX()), Mth.lerp(RenderUtil.getPartialTick(), playerEntity.yo, playerEntity.getY()), Mth.lerp(RenderUtil.getPartialTick(), playerEntity.zo, playerEntity.getZ()));
    }
}