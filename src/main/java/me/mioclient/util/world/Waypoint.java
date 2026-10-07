package me.mioclient.util.world;

import me.mioclient.api.NamedValue;

import me.mioclient.util.math.MathUtils;

import com.google.gson.annotations.SerializedName;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

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
    public transient Vec3 vec3d;

    public Waypoint(String str, double d, double d2, double d3, String str2, String str3) {
        this.x = roundCoord(d);
        this.y = roundCoord(d2);
        this.z = roundCoord(d3);
        this.dimension = str2;
        this.server = str3;
        this.name = str;
        this.vec3d = new Vec3(d, d2, d3);
        this.toggled = true;
    }

    public Waypoint(String str, Vec3 vec3d, String str2, String str3) {
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

    public double distanceTo(Vec3 vec3d) {
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

    public Vec3 toVec3d() {
        if (this.vec3d == null) {
            this.vec3d = new Vec3(this.x, this.y, this.z);
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
    public Component getAsText() {
        return Component.literal(this.name).withStyle(style -> {
            return style.withHoverEvent(new HoverEvent.ShowText(Component.literal(String.format("%s's info:\nx: %.1f\ny: %.1f\nz: %.1f\ndimension: %s\nserver: %s", this.name, Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z), this.dimension, this.server)).withStyle(style2 -> {
                return style2.withColor(ChatFormatting.GRAY);
            })));
        });
    }
}
