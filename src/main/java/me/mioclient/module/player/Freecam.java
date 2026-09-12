package me.mioclient.module.player;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.interaction.InteractItemPacketEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.player.freecam.FreecamInput;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.events.interaction.InteractItemPacketEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.input.TickHookPreEvent;
import me.mioclient.event.events.input.UpdateMouseEvent;
import me.mioclient.event.events.player.UpdateSetPosEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.item.BucketItem;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/player/Freecam.class */
public class Freecam extends Module {
    public final FreecamInput freecamSearchHelper4;
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.1f, 5.0f));
    public Setting<Float> verticalSpeed = add(new NumberSetting<>("VerticalSpeed", 0.5f, 0.1f, 5.0f));
    public Setting<Boolean> noHands = add(new BooleanSetting("NoHands", true));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false).setVisible());
    public Setting<Boolean> always = add(new BooleanSetting("Always", false).requiresSettings(this.rotate));
    public Vec3d vec3d;
    public Vec3d vec3d2;
    public float[] floatArr;
    public Perspective perspective;
    public float val;
    public float val2;
    public float val3;
    public float val4;

    public Freecam() {
        super("Freecam", "Allows your camera to fly through walls.", Category.PLAYER, new String[0]);
        this.freecamSearchHelper4 = new FreecamInput();
        this.floatArr = null;
        this.speed.removeBounds();
        this.verticalSpeed.removeBounds();
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame()) {
            disable();
            return;
        }
        this.perspective = minecraftClient.options.getPerspective();
        new InputTickEvent(minecraftClient.player.input, 0.0f).reset();
        Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
        this.vec3d = pos;
        this.vec3d2 = pos;
        this.floatArr = new float[]{minecraftClient.player.getYaw(), minecraftClient.player.getPitch()};
        float f = this.floatArr[0];
        this.val = f;
        this.val2 = f;
        float f2 = this.floatArr[1];
        this.val3 = f2;
        this.val4 = f2;
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        if (isNotInGame()) {
            return;
        }
        minecraftClient.player.setYaw(this.floatArr[0]);
        minecraftClient.player.setPitch(this.floatArr[1]);
        minecraftClient.options.setPerspective(this.perspective);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (minecraftClient.currentScreen instanceof DownloadingTerrainScreen) {
            disable();
        }
        minecraftClient.options.setPerspective(Perspective.FIRST_PERSON);
        this.vec3d2 = this.vec3d;
        double[] doubleArray2508 = MovementUtils.computeVelocity(this.val, this.freecamSearchHelper4, this.speed.getValue().floatValue());
        this.vec3d = this.vec3d.add(doubleArray2508[0], 0.0d, doubleArray2508[1]);
        if (minecraftClient.options.jumpKey.isPressed()) {
            this.vec3d = this.vec3d.add(0.0d, this.verticalSpeed.getValue().floatValue(), 0.0d);
        }
        if (minecraftClient.options.sneakKey.isPressed()) {
            this.vec3d = this.vec3d.subtract(0.0d, this.verticalSpeed.getValue().floatValue(), 0.0d);
        }
        this.freecamSearchHelper4.tick(false, Float.intBitsToFloat(1065353216));
    }

    @Listen
    public void onTickHookPre(TickHookPreEvent tickHookPreEvent) {
        tickHookPreEvent.cancel();
    }

    @Listen(get219= 1000)
    public void onMotion(MotionEvent motionEvent) {
        boolean isBreakingBlock = minecraftClient.interactionManager.isBreakingBlock();
        if (motionEvent.getPhase() == KeyPearlMode.Post || !this.rotate.getValue().booleanValue() || minecraftClient.crosshairTarget == null) {
            return;
        }
        if (!RotationManager.is724() || isBreakingBlock || this.always.getValue().booleanValue()) {
            motionEvent.setRotation(RotationManager.getRotationsTo(minecraftClient.crosshairTarget.getPos()));
        }
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if ((minecraftClient.player.getStackInHand(interactBlockEvent.getHand()).getItem() instanceof BucketItem) || (this.rotate.getValue().booleanValue() && RotationManager.is724())) {
            ClientServices.RotationManager.requestRotations(RotationManager.getRotationsTo(interactBlockEvent.getHitResult().getPos()), 1000);
        }
    }

    @Listen
    public void onInteractItemPacket(InteractItemPacketEvent interactItemEvent_2) {
        if (!this.rotate.getValue().booleanValue() || minecraftClient.crosshairTarget == null || this.always.getValue().booleanValue()) {
            return;
        }
        interactItemEvent_2.setRotation(RotationManager.getRotationsTo(minecraftClient.crosshairTarget.getPos()));
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof DisconnectS2CPacket) {
            disable();
        }
    }

    @Listen
    public void onUpdateMouse(UpdateMouseEvent updateMouseEvent) {
        do173(updateMouseEvent.getDeltaX() * Double.longBitsToDouble(4594572340058128384L), updateMouseEvent.getDeltaY() * Double.longBitsToDouble(4594572340058128384L));
        updateMouseEvent.cancel();
    }

    @Listen
    public void onUpdateSetPos(UpdateSetPosEvent updateSetPosEvent) {
        float tickDelta = minecraftClient.getRenderTickCounter().getTickDelta(true);
        updateSetPosEvent.setPos(new Vec3d(lerpX(tickDelta), get175(tickDelta), get176(tickDelta)));
        updateSetPosEvent.cancel();
    }

    public void do173(double d, double d2) {
        this.val2 = this.val;
        this.val4 = this.val3;
        this.val = (float) (this.val + d);
        this.val3 = (float) (this.val3 + d2);
        this.val3 = MathHelper.clamp(this.val3, -MathConstants.num2, MathConstants.num2);
    }

    public double lerpX(float f) {
        return MathHelper.lerp(f, this.vec3d2.x, this.vec3d.x);
    }

    public double get175(float f) {
        return MathHelper.lerp(f, this.vec3d2.y, this.vec3d.y);
    }

    public double get176(float f) {
        return MathHelper.lerp(f, this.vec3d2.z, this.vec3d.z);
    }

    public double get177(float f) {
        return minecraftClient.currentScreen != null ? this.val : MathHelper.lerp(f, this.val2, this.val);
    }

    public double get178(float f) {
        return minecraftClient.currentScreen != null ? this.val3 : MathHelper.lerp(f, this.val4, this.val3);
    }

    public boolean is179() {
        return isToggled() && this.noHands.getValue().booleanValue();
    }
}
