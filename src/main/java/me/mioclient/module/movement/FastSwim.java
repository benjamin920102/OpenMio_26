package me.mioclient.module.movement;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.util.world.WorldUtils;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/FastSwim.class */
public class FastSwim extends Module {
    public static SpeedMine speedmine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
    public Setting<Boolean> water = add(new BooleanSetting("Water", false).setVisible());
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.1f, 3.0f).requiresSettings(this.water));
    public Setting<Boolean> lava = add(new BooleanSetting("Lava", true).setVisible());
    public Setting<Float> speed2 = add(new NumberSetting<>("Speed", 1.0f, 0.1f, 3.0f).requiresSettings(this.lava));
    public Setting<Boolean> vertical = add(new BooleanSetting("Vertical", false).setVisible());
    public Setting<Float> vSpeed = add(new NumberSetting<>("VSpeed", 1.0f, 0.1f, 2.0f).requiresSettings(this.vertical));
    public Setting<Float> glide = add(new NumberSetting<>("Glide", 0.05f, 0.0f, 5.0f));
    public Setting<Boolean> accelerate = add(new BooleanSetting("Accelerate", false).setVisible());
    public Setting<Float> accelMin = add(new NumberSetting<>("AccelMin", 1.0f, 0.0f, 5.0f).requiresSettings(this.accelerate));
    public Setting<Float> accelTime = add(new NumberSetting<>("AccelTime", 1.0f, 0.1f, 10.0f).setSuffix("s").requiresSettings(this.accelerate));
    public long num;
    public boolean flag;

    public FastSwim() {
        super("FastSwim", "Makes you move faster while in liquids.", Category.MOVEMENT, new String[0]);
        this.speed.setConfigName("WaterSpeed");
        this.speed2.setConfigName("LavaSpeed");
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (((minecraftClient.player.isInLava() && this.lava.getValue().booleanValue()) || (PlayerUtils.isInWater(minecraftClient.player) && this.water.getValue().booleanValue())) && !minecraftClient.player.isFallFlying() && minecraftClient.player.isOnGround()) {
            minecraftClient.player.horizontalSpeed = 0.0f;
        }
    }

    @Listen
    public void onMove(MoveEvent moveEvent) {
        boolean z = speedmine.isToggled() && ((double) speedmine.getMiningProgress()) + speedmine.getCurrentMiningSpeed() >= ((double) speedmine.damage.getValue().floatValue()) - Double.longBitsToDouble(4591870180066957722L) && speedmine.getBreakingPos() != null && WorldUtils.isBreakable(speedmine.getBreakingPos());
        if (!((minecraftClient.player.isInLava() && this.lava.getValue().booleanValue()) || (PlayerUtils.isInWater(minecraftClient.player) && this.water.getValue().booleanValue())) || minecraftClient.player.isFallFlying() || !ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(500L) || z) {
            this.num = System.currentTimeMillis();
            return;
        }
        if (!MovementUtils.is2181()) {
            this.num = System.currentTimeMillis();
        }
        float intBitsToFloat = this.vertical.getValue().booleanValue() ? Float.intBitsToFloat(1039516303) * this.vSpeed.getValue().floatValue() : 0.0f;
        float intBitsToFloat2 = Float.intBitsToFloat(1039516303) * this.accelMin.getValue().floatValue();
        double intBitsToFloat3 = Float.intBitsToFloat(1039516303) * this.speed2.getValue().floatValue();
        if (this.water.getValue().booleanValue() && PlayerUtils.isInWater(minecraftClient.player)) {
            intBitsToFloat3 = Float.intBitsToFloat(1039516303) * this.speed.getValue().floatValue();
        }
        if (this.accelerate.getValue().booleanValue() && intBitsToFloat2 < intBitsToFloat3) {
            intBitsToFloat3 = Math.min(intBitsToFloat2 + ((intBitsToFloat3 - intBitsToFloat2) * MathHelper.clamp(((float) (System.currentTimeMillis() - this.num)) / (this.accelTime.getValue().floatValue() * Float.intBitsToFloat(1148846080)), 0.0f, Float.intBitsToFloat(1065353216))), intBitsToFloat3);
        }
        if (minecraftClient.player.input.jumping && !this.flag) {
            moveEvent.setY(intBitsToFloat);
            minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, intBitsToFloat));
        } else if (minecraftClient.player.input.sneaking) {
            moveEvent.setY(-intBitsToFloat);
            minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, -intBitsToFloat));
            intBitsToFloat3 /= Double.longBitsToDouble(4611686018427387904L);
        } else {
            moveEvent.setY(0.0d);
            minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, 0.0d));
            if (!minecraftClient.player.verticalCollision && this.glide.getValue().floatValue() != 0.0f) {
                minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().add(0.0d, -this.glide.getValue().floatValue(), 0.0d));
                moveEvent.setY(minecraftClient.player.getVelocity().y);
            }
        }
        double[] doubleArray2507 = MovementUtils.applyVelocity(moveEvent, intBitsToFloat3);
        minecraftClient.player.setVelocity(doubleArray2507[0], minecraftClient.player.getVelocity().y, doubleArray2507[1]);
    }
}
