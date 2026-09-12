package me.mioclient.module.movement;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.Vec3dUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.util.math.Box;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/LongJump.class */
public class LongJump extends Module {
    public Setting<Float> jumpSpeed = add(new NumberSetting<>("JumpSpeed", 1.0f, 0.0f, 5.0f));
    public Setting<Boolean> inLiquid = add(new BooleanSetting("InLiquid", false));
    public Setting<Boolean> useTimer = add(new BooleanSetting("UseTimer", true));
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", true));
    public double val;
    public double val2;
    public int num;
    public boolean flag;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/LongJump$Mode.class */
    public enum Mode implements NamedValue {
        PLAIN("Plain"),
        GRIMHOP("GrimHop");

        public final String name;

        Mode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public LongJump() {
        super("LongJump", "Makes you jump fast 'n far.", Category.MOVEMENT, new String[0]);
        this.num = 4;
    }

    /* JADX WARN: Code restructure failed: missing block: B:53:0x0292, code lost:
    
        if (me.mioclient.module.movement.LongJump.minecraftClient.world.isSpaceEmpty(me.mioclient.module.movement.LongJump.minecraftClient.player.getBoundingBox().offset(0.0d, me.mioclient.module.movement.LongJump.minecraftClient.player.getVelocity().y, 0.0d)) == false) goto L49;
     */
    @Listen
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void applyMovement(MoveEvent moveEvent) {
        double d;
        double d2;
        if (hasHeadCollision() || isInLiquidBlocked() || minecraftClient.player.isFallFlying() || minecraftClient.player.isSpectator()) {
            return;
        }
        double d3 = moveEvent.getY();
        if (this.useTimer.getValue().booleanValue() && ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(250L)) {
            ClientServices.holeSnapHelper.requestTickRate(this, Float.intBitsToFloat(1066098124));
        } else if (this.useTimer.getValue().booleanValue()) {
            ClientServices.holeSnapHelper.releaseTickRate(this);
        }
        if (!ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(250L) && this.autoDisable.getValue().booleanValue()) {
            toggle();
        }
        double intBitsToFloat = Float.intBitsToFloat(1073741824) * this.jumpSpeed.getValue().floatValue();
        if (this.num == 1 && MovementUtils.is2181()) {
            this.val = (Double.longBitsToDouble(4608758678669597082L) * MovementUtils.computeMoveSpeed(false, Double.longBitsToDouble(4598847156609680094L) * intBitsToFloat)) - Double.longBitsToDouble(4576918229304087675L);
        } else if (this.num == 2 && MovementUtils.is2181()) {
            d3 = Double.longBitsToDouble(4601237667291888353L) + MovementUtils.getJumpBoostBonus();
            this.val *= this.flag ? Double.longBitsToDouble(4610260629145325142L) : Double.longBitsToDouble(4608961340652828754L);
        } else if (this.num == 3) {
            this.val = this.val2 - (Double.longBitsToDouble(4604119971289628672L) * (this.val2 - MovementUtils.computeMoveSpeed(true, Double.longBitsToDouble(4598847156609680094L) * intBitsToFloat)));
            this.flag = !this.flag;
        } else {
            if (!minecraftClient.player.verticalCollision) {
            }
            if (this.num > 0) {
                this.num = MovementUtils.is2181() ? 1 : 0;
            }
            this.val = this.val2 - (this.val2 / Double.longBitsToDouble(4639798331726364672L));
        }
        this.val = Math.max(this.val, MovementUtils.computeMoveSpeed(false, Double.longBitsToDouble(4598847156609680094L) * intBitsToFloat));
        if (MovementUtils.is2181()) {
            double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(RenderUtil.getPartialTick()), minecraftClient.player.input, this.val);
            d = doubleArray2508[0];
            d2 = doubleArray2508[1];
        } else {
            d = 0.0d;
            d2 = 0.0d;
        }
        Vec3dUtils.setVectorComponents(moveEvent.getMovement(), d, d3, d2);
        if (MovementUtils.is2181()) {
            this.num++;
        }
    }

    @Listen(get219= RenderConstants.num2)
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Post) {
            return;
        }
        if (!MovementUtils.is2181()) {
            this.num = 4;
        }
        double x = minecraftClient.player.getX() - minecraftClient.player.prevX;
        double z = minecraftClient.player.getZ() - minecraftClient.player.prevZ;
        this.val2 = Math.sqrt((x * x) + (z * z));
    }

    public boolean isInLiquidBlocked() {
        return PlayerUtils.isInLiquid(minecraftClient.player) && !this.inLiquid.getValue().booleanValue();
    }

    public boolean hasHeadCollision() {
        Box boundingBox = minecraftClient.player.getBoundingBox();
        return minecraftClient.world.canCollide(minecraftClient.player, new Box(minecraftClient.player.getBlockPos().getX(), boundingBox.minY, minecraftClient.player.getBlockPos().getZ(), minecraftClient.player.getBlockPos().getX() + Double.longBitsToDouble(4607182418800017408L), boundingBox.maxY, minecraftClient.player.getBlockPos().getZ() + Double.longBitsToDouble(4607182418800017408L)).contract(Double.longBitsToDouble(4502148214488346440L)));
    }
}
