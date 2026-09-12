package me.mioclient.module.render;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.client.SearchMode;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.PlayerUtils;

import java.awt.Color;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.mode.client.SearchMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import me.mioclient.module.client.Colors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL32C;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Tracers.class */
public class Tracers extends Module {
    public static Colors colors = (Colors) ClientServices.moduleInjector.getModule(Colors.class);
    public Setting<TracersMode> hitbox = add(new EnumSetting<>("Hitbox", TracersMode.HEAD));
    public Setting<Float> maxDistance = add(new NumberSetting<>("MaxDistance", 256.0f, 8.0f, 256.0f).setSuffix("m"));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 3.0f));
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-65536, true)).setThemeSync(true));
    public Setting<Boolean> stem = add(new BooleanSetting("Stem", true));
    public Setting<Boolean> distanceColor = add(new BooleanSetting("DistanceColor", false));
    public Setting<Boolean> enemyColor = add(new BooleanSetting("EnemyColor", true));
    public Setting<Boolean> ignoreFriends = add(new BooleanSetting("IgnoreFriends", false));
    public Setting<Boolean> ignoreNakeds = add(new BooleanSetting("IgnoreNakeds", false));

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Tracers$TracersMode.class */
    private enum TracersMode implements NamedValue {
        HEAD("Head", 0.85f),
        BODY("Body", 0.5f),
        LEGS("Legs", 0.0f);

        public final String name;
        public final float val;

        TracersMode(String str, float f) {
            this.name = str;
            this.val = f;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public float getHeightFraction() {
            return this.val;
        }
    }

    public Tracers() {
        super("Tracers", "Draws lines from your crosshair to the players nearby.", Category.RENDER, new String[0]);
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        Camera camera = minecraftClient.gameRenderer.getCamera();
        Vec3d add = new Vec3d(0.0d, 0.0d, Double.longBitsToDouble(4636737291354636288L)).rotateX(-((float) Math.toRadians(camera.getPitch()))).rotateY(-((float) Math.toRadians(camera.getYaw()))).add(minecraftClient.getEntityRenderDispatcher().camera.getPos());
        GL32C.glLineWidth(this.lineWidth.getValue().floatValue());
        for (PlayerEntity playerEntity : minecraftClient.world.getPlayers()) {
            if (minecraftClient.player != playerEntity) {
                if (minecraftClient.gameRenderer.getCamera().getPos().distanceTo(((AbstractClientPlayerEntity) playerEntity).getPos()) <= this.maxDistance.getValue().floatValue() && (!this.ignoreFriends.getValue().booleanValue() || !ClientServices.socialManager.isFriendEntity(playerEntity))) {
                    if (!this.ignoreNakeds.getValue().booleanValue() || PlayerUtils.hasArmor((LivingEntity) playerEntity)) {
                        Vec3d add2 = ((AbstractClientPlayerEntity) playerEntity).getLerpedPos(RenderUtil.getPartialTick()).add(0.0d, ((AbstractClientPlayerEntity) playerEntity).getHeight() * this.hitbox.getValue().getHeightFraction(), 0.0d);
                        Color value = this.color.getValue();
                        boolean booleanValue = this.distanceColor.getValue().booleanValue();
                        if (booleanValue) {
                            value = ColorUtils.withAlpha(ColorUtils.lerpColor(Color.red, Color.green, Float.intBitsToFloat(1065353216) - ((float) ((MathHelper.clamp(minecraftClient.player.getEyePos().distanceTo(add2), Double.longBitsToDouble(4620693217682128896L), Double.longBitsToDouble(4634204016564240384L)) - Double.longBitsToDouble(4620693217682128896L)) / Double.longBitsToDouble(4633078116657397760L)))), this.color.getValue().getAlpha());
                        }
                        if (ClientServices.socialManager.isFriendEntity(playerEntity)) {
                            value = ColorUtils.withAlpha(ClientServices.socialManager.getFriendColor(), this.color.getValue().getAlpha());
                        } else if (ClientServices.socialManager.isEnemyEntity(playerEntity) && this.enemyColor.getValue().booleanValue()) {
                            value = ColorUtils.withAlpha(ClientServices.socialManager.getEnemyColor(), this.color.getValue().getAlpha());
                        }
                        SearchMode value2 = booleanValue ? colors.scheme.getValue() : SearchMode.NORMAL;
                        Color color = value;
                        value2.runWithShaderColor(() -> {
                            RenderUtil.INSTANCE.drawDebugLine(inner_3.getMatrixStack(), add, add2, color);
                            if (this.stem.getValue().booleanValue()) {
                                RenderUtil.INSTANCE.drawDebugLine(inner_3.getMatrixStack(), playerEntity.getLerpedPos(RenderUtil.getPartialTick()), playerEntity.getLerpedPos(RenderUtil.getPartialTick()).add(0.0d, playerEntity.getEyeHeight(playerEntity.getPose()), 0.0d), color);
                            }
                        });
                    }
                }
            }
        }
        GL32C.glLineWidth(Float.intBitsToFloat(1065353216));
    }
}
