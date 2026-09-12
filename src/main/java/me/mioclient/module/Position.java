package me.mioclient.module;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.mode.world.StashFinderMode;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;
import me.mioclient.util.math.GridPoint;
import me.mioclient.util.math.MathUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.GridPoint;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;
import me.mioclient.util.math.MathUtils;

import me.mioclient.util.text.StringTemplate;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.setting.NumberSetting;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.mode.world.StashFinderMode;
import me.mioclient.api.Setting;
import me.mioclient.module.player.Freecam;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Position.class */
public class Position extends me.mioclient.module.HudModule {
    public static Freecam freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class);
    public Setting<Float> setting;
    public Setting<Boolean> setting2;
    public Setting<Boolean> setting3;
    public GridPoint positionData;

    public Position() {
        super("Position", "coordinates");
        this.setting = add(new NumberSetting("SafeRange", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(Float.intBitsToFloat(1120403456))).setSuffix("k"));
        this.setting2 = add(new BooleanSetting("Nether", true));
        this.setting3 = add(new BooleanSetting("Fake", false));
        this.setting.setHoleSnapConfig("None", HoleSnapMode.MIN);
        this.setting3.setChangeCallback(() -> {
            ClientServices.tooltipsHelper.schedule(() -> {
                boolean isChanceHit = MathUtils.isChanceHit(50);
                boolean is3802 = MathUtils.isChanceHit(50);
                int longBitsToDouble = ((int) (((-minecraftClient.player.getX()) * Double.longBitsToDouble(4611686018427387904L)) + (Math.random() * minecraftClient.player.getX() * Double.longBitsToDouble(4616189618054758400L)))) + 13;
                int longBitsToDouble2 = ((int) (((-minecraftClient.player.getZ()) * Double.longBitsToDouble(4611686018427387904L)) + ((Math.random() * minecraftClient.player.getZ()) * Double.longBitsToDouble(4616189618054758400L)))) - 37;
                if (isChanceHit) {
                    longBitsToDouble = -longBitsToDouble;
                }
                if (is3802) {
                    longBitsToDouble2 = -longBitsToDouble2;
                }
                this.positionData = new GridPoint(longBitsToDouble, longBitsToDouble2);
            }, 0);
        });
        setElement(new TextHudElement(this, new HudTextLine(() -> {
            if (!isNotInGame()) {
                if (this.setting.getValue().floatValue() > 0.0f) {
                    if (!minecraftClient.player.getPos().isInRange(Vec3d.ZERO, this.setting.getValue().floatValue() * Float.intBitsToFloat(1148846080))) {
                    }
                }
                boolean contains = minecraftClient.world.getRegistryKey().getValue().getPath().contains("nether");
                Vec3d pos = minecraftClient.player.getPos();
                if (freecam.isToggled()) {
                    pos = minecraftClient.gameRenderer.getCamera().getPos().subtract(0.0d, minecraftClient.player.getHeight(), 0.0d);
                }
                if (this.setting3.getValue().booleanValue() && this.positionData != null) {
                    if (this.positionData.get476() == 0 && this.positionData.getGridY() == 0) {
                        this.setting3.fireChangeCallback();
                    }
                    pos = pos.add(this.positionData.get476(), 0.0d, this.positionData.getGridY());
                }
                BlockPos ofFloored = BlockPos.ofFloored((net.minecraft.util.math.Position) pos);
                BlockPos ofFloored2 = contains ? BlockPos.ofFloored(pos.multiply(Double.longBitsToDouble(4620693217682128896L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4620693217682128896L))) : BlockPos.ofFloored(pos.multiply(Double.longBitsToDouble(4593671619917905920L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4593671619917905920L)));
                String string2921 = new StringTemplate().arg(String.valueOf(Formatting.WHITE)).format("XYZ: \u0001");
                String string29212 = new StringTemplate().arg(ofFloored.getZ()).arg(ofFloored.getY()).arg(ofFloored.getX()).format("\u0001, \u0001, \u0001.");
                String string29213 = new StringTemplate().arg(ofFloored2.getZ()).arg(ofFloored2.getY()).arg(ofFloored2.getX()).format(" (\u0001, \u0001, \u0001.)");
                if (this.setting2.getValue().booleanValue() && WorldUtils.getDimension() != StashFinderMode.THE_END) {
                    string29212 = new StringTemplate().arg(string29213).arg(string29212).format("\u0001\u0001");
                }
                return Text.literal(new StringTemplate().arg(string29212).arg(string2921).format("\u0001\u0001"));
            }
            return Text.literal("XYZ: %sREDACTED".formatted(Formatting.WHITE));
        }, () -> {
            return true;
        })));
        getElement().setAnchor(ModuleListMode.BOTTOM_LEFT);
    }
}
