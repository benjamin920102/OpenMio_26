package me.mioclient.util.world;

import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.math.MathUtils;

import me.mioclient.api.NamedValue;
import me.mioclient.util.math.MathUtils;

import com.google.gson.annotations.SerializedName;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Waypoint.class */
public class Waypoint implements NamedValue {

    @SerializedName("x")
    public final double x;

    @SerializedName("y")
    public final double y;

    @SerializedName("z")
    public final double z;

    @SerializedName("name")
    public final String name;

    @SerializedName("dimension")
    public final String dimension;

    @SerializedName("server")
    public final String server;

    @SerializedName("toggled")
    public boolean toggled;
    public transient Vec3d vec3d;

    public Waypoint(String str, double d, double d2, double d3, String str2, String str3) {
        this.x = roundCoord(d);
        this.y = roundCoord(d2);
        this.z = roundCoord(d3);
        this.dimension = str2;
        this.server = str3;
        this.name = str;
        this.vec3d = new Vec3d(d, d2, d3);
        this.toggled = true;
    }

    public Waypoint(String str, Vec3d vec3d, String str2, String str3) {
        this(str, vec3d.x, vec3d.y, vec3d.z, str2, str3);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public String getDimension() {
        return this.dimension;
    }

    public String getServer() {
        return this.server;
    }

    public double distanceTo(Vec3d vec3d) {
        return vec3d.distanceTo(this.vec3d);
    }

    public double roundCoord(double d) {
        return MathUtils.floorToDecimals(d, 1);
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(boolean z) {
        this.toggled = z;
    }

    public Vec3d toVec3d() {
        if (this.vec3d == null) {
            this.vec3d = new Vec3d(this.x, this.y, this.z);
        }
        return this.vec3d;
    }

    public boolean matches(String str, String str2) {
        return getName().equals(str) && getServer().equalsIgnoreCase(str2);
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    @Override // me.mioclient.api.NamedValue
    public Text getAsText() {
        return Text.literal(this.name).styled(style -> {
            return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(String.format("%s's info:\nx: %.1f\ny: %.1f\nz: %.1f\ndimension: %s\nserver: %s", this.name, Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z), this.dimension, this.server)).styled(style2 -> {
                return style2.withFormatting(Formatting.GRAY);
            })));
        });
    }
}
