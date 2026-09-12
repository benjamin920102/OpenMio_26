package me.mioclient.module;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.util.text.StringTemplate;
import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.manager.RotationManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Direction.class */
public class Direction extends me.mioclient.module.HudModule {
    public Direction() {
        super("Direction", "facing");
        setElement(new TextHudElement(this, new HudTextLine(() -> {
            return Text.literal(getString1217());
        }, () -> {
            return true;
        })));
        getElement().setAnchor(ModuleListMode.BOTTOM_LEFT);
    }

    public String getString1217() {
        String string2921 = new StringTemplate().arg((int) MathHelper.wrapDegrees(minecraftClient.gameRenderer.getCamera().getYaw())).arg(String.valueOf(Formatting.WHITE)).format("\u0001 (\u0001, ");
        switch (RotationManager.getYawOctant(minecraftClient.gameRenderer.getCamera().getYaw())) {
            case 0:
                return new StringTemplate().arg(string2921).format("South\u0001+Z)");
            case 1:
                return new StringTemplate().arg(string2921).format("SouthWest\u0001-X +Z)");
            case 2:
                return new StringTemplate().arg(string2921).format("West\u0001-X)");
            case 3:
                return new StringTemplate().arg(string2921).format("NorthWest\u0001-X -Z)");
            case 4:
                return new StringTemplate().arg(string2921).format("North\u0001-Z)");
            case 5:
                return new StringTemplate().arg(string2921).format("NorthEast\u0001+X -Z)");
            case 6:
                return new StringTemplate().arg(string2921).format("East\u0001+X)");
            case 7:
                return new StringTemplate().arg(string2921).format("SouthEast\u0001+X +Z)");
            default:
                return "Waiting...";
        }
    }
}
