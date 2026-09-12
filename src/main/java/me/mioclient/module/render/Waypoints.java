package me.mioclient.module.render;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.math.Vec3dUtils;
import me.mioclient.util.world.Waypoint;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.world.Waypoint;
import me.mioclient.api.NamedValue;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.math.Vec3dUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.Fonts;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL32C;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Waypoints.class */
public class Waypoints extends Module {
    public static Fonts fonts = (Fonts) ClientServices.moduleInjector.getModule(Fonts.class);
    public Setting<Float> distance = add(new NumberSetting<>("Distance", 101.0f, 0.1f, 101.0f).setSuffix("k").setHoleSnapConfig("Unlimited", HoleSnapMode.MAX));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 3.0f));
    public Setting<Boolean> box = add(new BooleanSetting("Box", true));
    public Setting<Boolean> beam = add(new BooleanSetting("Beam", false));
    public Setting<Boolean> tracers = add(new BooleanSetting("Tracers", false));
    public Setting<Boolean> name = add(new BooleanSetting("Name", true));
    public Setting<WaypointsMode> info = add(new EnumSetting<>("Info", WaypointsMode.NONE, v -> this.name.getValue().booleanValue()));
    public Setting<Float> textScale = add(new NumberSetting<>("TextScale", 1.0f, 0.5f, 3.0f, v -> this.name.getValue().booleanValue()));
    public Setting<Boolean> textBackground = add(new BooleanSetting("TextBackground", true, v -> this.name.getValue().booleanValue()));
    public Setting<Boolean> eyesAlign = add(new BooleanSetting("EyesAlign", false).requiresSettings(this.name));
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-457336321, true)).setThemeSync(true));
    public Setting<Integer> beamAlpha = add(new NumberSetting<>("BeamAlpha", 40, 0, 255));
    public final CopyOnWriteArrayList<Waypoint> copyOnWriteArrayList;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Waypoints$WaypointsMode.class */
    private static enum WaypointsMode implements NamedValue {
        NONE("None"),
        waypointsMode("Coords") {
            @Override
            public String formatInfo(Vec3d vec3d) {
                return String.format(Locale.US, "X: %.1f, Y: %.1f, Z: %.1f", Double.valueOf(vec3d.getX()), Double.valueOf(vec3d.getY()), Double.valueOf(vec3d.getZ()));
            }
        },
        waypointsMode2("Distance") {
            @Override
            public String formatInfo(Vec3d vec3d) {
                return String.format("%.1fm", Double.valueOf(vec3d.distanceTo(ClientAccessor.minecraftClient.player.getPos()))).replaceAll(",", ".");
            }
        };

        public final String name;

        WaypointsMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public String formatInfo(Vec3d vec3d) {
            return "";
        }
    }

    public Waypoints() {
        super("Waypoints", "Renders certain places you mark using ;waypoints.", Category.RENDER, new String[0]);
        this.copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        this.distance.setHoleSnapConfig("Unlimited", HoleSnapMode.MAX);
        setDrawn(false);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (minecraftClient.player.age % 2 == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (Waypoint waypointsEnumSettingHelper : ClientServices.waypointsManager.getRegistry()) {
            if (waypointsEnumSettingHelper != null && (waypointsEnumSettingHelper.getServer() == null || waypointsEnumSettingHelper.getDimension() == null || waypointsEnumSettingHelper.getName() == null)) {
                arrayList.add(waypointsEnumSettingHelper);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ClientServices.waypointsManager.unregister((Waypoint) it.next());
        }
        this.copyOnWriteArrayList.clear();
        for (Waypoint waypointsEnumSettingHelper2 : ClientServices.waypointsManager.getRegistry()) {
            if (waypointsEnumSettingHelper2 != null && waypointsEnumSettingHelper2.getDimension() != null && waypointsEnumSettingHelper2.getName() != null && waypointsEnumSettingHelper2.getServer() != null && waypointsEnumSettingHelper2.isToggled()) {
                if (minecraftClient.getCurrentServerEntry() != null || !waypointsEnumSettingHelper2.getServer().equalsIgnoreCase("singleplayer")) {
                    if (minecraftClient.getCurrentServerEntry() != null) {
                        if (waypointsEnumSettingHelper2.getServer().equalsIgnoreCase(minecraftClient.getCurrentServerEntry().address)) {
                        }
                    }
                }
                this.copyOnWriteArrayList.add(waypointsEnumSettingHelper2);
            }
        }
        this.copyOnWriteArrayList.sort(Comparator.comparing(waypointsEnumSettingHelper3 -> {
            return Double.valueOf(minecraftClient.player.squaredDistanceTo(waypointsEnumSettingHelper3.toVec3d()));
        }));
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (minecraftClient.options.hudHidden) {
            return;
        }
        Camera camera = minecraftClient.gameRenderer.getCamera();
        Vec3d add = new Vec3d(0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L)).rotateX(-((float) Math.toRadians(camera.getPitch()))).rotateY(-((float) Math.toRadians(camera.getYaw()))).add(minecraftClient.getEntityRenderDispatcher().camera.getPos());
        Iterator<Waypoint> it = this.copyOnWriteArrayList.iterator();
        while (it.hasNext()) {
            Waypoint next = it.next();
            if (next != null && next.getDimension() != null && next.getName() != null && next.getServer() != null) {
                if (next.toVec3d().distanceTo(minecraftClient.gameRenderer.getCamera().getPos()) <= this.distance.getValue().floatValue() * Float.intBitsToFloat(1148846080) || this.distance.getValue().floatValue() == Float.intBitsToFloat(1120534528)) {
                    Vec3d vec3d2100 = toCurrentDimension(next);
                    if (vec3d2100 != null) {
                        Vec3d vec3d = vec3d2100;
                        if (Vec3dUtils.getHorizontalDistance(minecraftClient.player.getPos(), vec3d2100) >= Float.intBitsToFloat(1123024896)) {
                            float[] floatArray2484 = RotationManager.getRotationsTo(vec3d2100);
                            Vec3d lerpedPos = minecraftClient.player.getLerpedPos(RenderUtil.getPartialTick());
                            vec3d = new Vec3d(lerpedPos.getX() + (Float.intBitsToFloat(1120403456) * ((float) Math.cos(Math.toRadians(floatArray2484[0] + MathConstants.num2)))), vec3d2100.y, lerpedPos.getZ() + (Float.intBitsToFloat(1120403456) * ((float) Math.sin(Math.toRadians(floatArray2484[0] + MathConstants.num2)))));
                        }
                        if (this.beam.getValue().booleanValue()) {
                            BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), new Box(vec3d.add(-MathConstants.val3, -MathConstants.val3, -MathConstants.val3), vec3d.add(MathConstants.val3, MathConstants.val3, MathConstants.val3)).withMaxY(minecraftClient.world.getTopY()).withMinY(minecraftClient.world.getBottomY()), ColorUtils.withAlpha(this.color.getValue(), this.beamAlpha.getValue().intValue()));
                        }
                        if (this.box.getValue().booleanValue()) {
                            BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), new Box(vec3d2100.add(-MathConstants.val3, -MathConstants.val3, -MathConstants.val3), vec3d2100.add(MathConstants.val3, MathConstants.val3, MathConstants.val3)), this.color.getValue(), this.lineWidth.getValue().floatValue());
                        }
                        if (this.tracers.getValue().booleanValue()) {
                            GL32C.glLineWidth(this.lineWidth.getValue().floatValue());
                            RenderUtil.INSTANCE.drawDebugLine(inner_3.getMatrixStack(), add, vec3d, this.color.getValue());
                            GL32C.glLineWidth(Float.intBitsToFloat(1065353216));
                        }
                        if (this.name.getValue().booleanValue()) {
                            if (this.eyesAlign.getValue().booleanValue()) {
                                vec3d = vec3d.withAxis(Direction.Axis.Y, MathHelper.lerp(inner_3.getTickDelta(), minecraftClient.player.prevY, minecraftClient.player.getY()) + minecraftClient.player.getEyeHeight(minecraftClient.player.getPose()));
                            }
                            String string2921 = new StringTemplate().arg(this.info.getValue().formatInfo(vec3d2100)).arg(next.getName()).format("\u0001 \u0001");
                            double d = MathUtils.getDistanceScale(minecraftClient.gameRenderer.getCamera().getPos(), vec3d, this.textScale.getValue().floatValue());
                            float f = (float) MathConstants.val3;
                            if (this.textBackground.getValue().booleanValue()) {
                                RenderUtil.INSTANCE.drawBillboard(inner_3.getMatrixStack(), vec3d, Float.intBitsToFloat(-1124744561), (float) MathConstants.val3, (-(ClientTextRenderer.INSTANCE.getStringWidth(string2921) / Float.intBitsToFloat(1073741824))) - Float.intBitsToFloat(1073741824), Float.intBitsToFloat(1084227584), d * Double.longBitsToDouble(4611686018427387904L), new Color(0, 0, 0, 100));
                                if (!fonts.isToggled()) {
                                    f = Float.intBitsToFloat(1047233823);
                                }
                            }
                            RenderUtil.INSTANCE.drawTextBillboardOffset(inner_3.getDrawContext(), string2921, vec3d, 0.0f, f, -(ClientTextRenderer.INSTANCE.getStringWidth(string2921) / Float.intBitsToFloat(1073741824)), 0.0f, d, Color.WHITE, true);
                        }
                    }
                }
            }
        }
    }

    public static Vec3d toCurrentDimension(Waypoint waypointsEnumSettingHelper) {
        Vec3d vec3d1303 = waypointsEnumSettingHelper.toVec3d();
        String lowerCase = WorldUtils.getDimension().getDisplayName().toLowerCase();
        if (waypointsEnumSettingHelper.getDimension().equals("overworld") && lowerCase.equals("nether")) {
            return vec3d1303.multiply(Double.longBitsToDouble(4593671619917905920L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4593671619917905920L));
        }
        if (waypointsEnumSettingHelper.getDimension().equals("nether") && lowerCase.equals("overworld")) {
            return vec3d1303.multiply(Double.longBitsToDouble(4620693217682128896L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4620693217682128896L));
        }
        if (waypointsEnumSettingHelper.getDimension().equals(lowerCase)) {
            return vec3d1303;
        }
        return null;
    }
}
