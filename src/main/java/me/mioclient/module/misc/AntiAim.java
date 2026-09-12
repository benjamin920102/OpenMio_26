package me.mioclient.module.misc;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.world.RaycastUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.world.RaycastUtils;

import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/AntiAim.class */
public class AntiAim extends Module {
    public Setting<Boolean> yaw2 = add(new BooleanSetting("Yaw", false).setVisible());
    public Setting<AntiAimPredicateMode> yaw = add(new EnumSetting<>("Yaw", AntiAimPredicateMode.antiAimPredicateMode).requiresSettings(this.yaw2));
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.1f, 90.0f));
    public Setting<Float> static_2 = add(new NumberSetting<>("Static", 0.0f, 0.0f, 360.0f));
    public Setting<Float> flip = add(new NumberSetting<>("Flip", 0.0f, 0.0f, 90.0f));
    public Setting<Float> jitterRange = add(new NumberSetting<>("JitterRange", 0.0f, 0.0f, 180.0f));
    public Setting<Float> base = add(new NumberSetting<>("Base", 0.0f, 0.0f, 360.0f));
    public Setting<Boolean> pitch = add(new BooleanSetting("Pitch", false).setVisible());
    public Setting<AntiAimMode> pitch2 = add(new EnumSetting<>("Pitch", AntiAimMode.antiAimMode).requiresSettings(this.pitch));
    public Setting<Float> static_ = add(new NumberSetting<>("Static", 0.0f, -90.0f, 90.0f));
    public Setting<Boolean> enderman = add(new BooleanSetting("Enderman", false).setVisible());
    public Setting<AntiAimMode_2> look = add(new EnumSetting<>("Look", AntiAimMode_2.AVOID).requiresSettings(this.enderman));
    public boolean flag;
    public float val;
    public int num;

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/misc/AntiAim$AntiAimMode.class */
    public enum AntiAimMode implements NamedValue {
        antiAimMode("Static") {
            @Override
            public float computePitchAngle(AntiAim antiAim, float f) {
                return antiAim.static_.getValue().floatValue();
            }
        },
        antiAimMode2("Random") {
            @Override
            public float computePitchAngle(AntiAim antiAim, float f) {
                return (float) ((Math.random() * 180.0d) - MathConstants.num2);
            }
        };

        public final String name;

        AntiAimMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public float computePitchAngle(AntiAim antiAim, float f) {
            return 0.0f;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/misc/AntiAim$AntiAimMode_2.class */
    public enum AntiAimMode_2 implements NamedValue {
        AVOID("Avoid"),
        STARE("Stare");

        public final String name;

        AntiAimMode_2(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/misc/AntiAim$AntiAimPredicateMode.class */
    public enum AntiAimPredicateMode implements NamedValue {
        antiAimPredicateMode("Jitter") {
            @Override
            public float computeYawAngle(AntiAim antiAim, float f) {
                return (float) MathHelper.wrapDegrees((((Math.random() * antiAim.jitterRange.getValue().floatValue()) * 2.0d) - antiAim.jitterRange.getValue().floatValue()) + antiAim.base.getValue().floatValue());
            }
        },
        antiAimPredicateMode2("Spin") {
            @Override
            public float computeYawAngle(AntiAim antiAim, float f) {
                antiAim.val += antiAim.speed.getValue().floatValue();
                return MathHelper.wrapDegrees(antiAim.val);
            }
        },
        RANDOMIRECTION("Flip") {
            @Override
            public float computeYawAngle(AntiAim antiAim, float f) {
                return MathHelper.wrapDegrees(antiAim.base.getValue().floatValue() + (ClientAccessor.minecraftClient.player.age % 2 == 0 ? -antiAim.flip.getValue().floatValue() : antiAim.flip.getValue().floatValue()));
            }
        },
        antiAimPredicateMode4("Static") {
            @Override
            public float computeYawAngle(AntiAim antiAim, float f) {
                return antiAim.static_2.getValue().floatValue();
            }
        },
        antiAimPredicateMode5("Random") {
            @Override
            public float computeYawAngle(AntiAim antiAim, float f) {
                return (float) (Math.random() * MathConstants.num3);
            }
        };

        public final String name;

        AntiAimPredicateMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public float computeYawAngle(AntiAim antiAim, float f) {
            return 0.0f;
        }
    }

    public AntiAim() {
        super("AntiAim", "Sets your rotations server-side.", Category.MISC, new String[0]);
        this.yaw.setConfigName("YawMode");
        this.pitch2.setConfigName("PitchMode");
    }

    @Listen(get219= RenderConstants.num4)
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        EndermanEntity endermanEntity852;
        this.flag = false;
        if (ClientServices.RotationManager.getActiveRequest() != null) {
            return;
        }
        if (this.num > 0) {
            this.num--;
            return;
        }
        float f = holeSnapEvent.getYaw();
        float f2 = holeSnapEvent.getPitch();
        if (this.yaw2.getValue().booleanValue()) {
            f = this.yaw.getValue().computeYawAngle(this, minecraftClient.player.getYaw());
            markRotationModified();
        }
        if (this.pitch.getValue().booleanValue()) {
            f2 = this.pitch2.getValue().computePitchAngle(this, minecraftClient.player.getPitch());
            markRotationModified();
        }
        if (this.enderman.getValue().booleanValue() && (endermanEntity852 = getEndermanEntity852()) != null) {
            if (this.look.getValue() == AntiAimMode_2.AVOID) {
                f2 = Float.intBitsToFloat(1119092736);
                markRotationModified();
            }
            if (this.look.getValue() == AntiAimMode_2.STARE) {
                float[] floatArray2484 = RotationManager.getRotationsTo(endermanEntity852.getEyePos());
                f = floatArray2484[0];
                f2 = floatArray2484[1];
                markRotationModified();
            }
        }
        if (this.flag) {
            ClientServices.RotationManager.requestRotations(new float[]{f, f2}, -999, true);
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        Item item = minecraftClient.player.getMainHandStack().getItem();
        if (((item instanceof RangedWeaponItem) || (item instanceof TridentItem)) && minecraftClient.player.isUsingItem()) {
            this.num = 2;
        }
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        PlayerInteractItemC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractItemC2SPacket ? (PlayerInteractItemC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerInteractItemC2SPacket) {
            handleItemUse(packet904.getHand());
        }
        PlayerInteractBlockC2SPacket packet9042 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractBlockC2SPacket ? (PlayerInteractBlockC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet9042 instanceof PlayerInteractBlockC2SPacket) {
            handleItemUse(packet9042.getHand());
        }
    }

    public EndermanEntity getEndermanEntity852() {
        for (Entity entity : minecraftClient.world.getEntities()) {
            if (entity instanceof EndermanEntity) {
                EndermanEntity endermanEntity = (EndermanEntity) entity;
                if (!endermanEntity.isAngry() && endermanEntity.isAlive() && (this.look.getValue() != AntiAimMode_2.AVOID || isEndermanInCrosshairFOV(endermanEntity))) {
                    if (RaycastUtils.hasLineOfSight(entity)) {
                        return endermanEntity;
                    }
                }
            }
        }
        return null;
    }

    public boolean isEndermanInCrosshairFOV(EndermanEntity endermanEntity) {
        Vec3d normalize = minecraftClient.player.getRotationVec(Float.intBitsToFloat(1065353216)).normalize();
        Vec3d vec3d = new Vec3d(endermanEntity.getX() - minecraftClient.player.getX(), endermanEntity.getEyeY() - minecraftClient.player.getEyeY(), endermanEntity.getZ() - minecraftClient.player.getZ());
        return normalize.dotProduct(vec3d.normalize()) > Double.longBitsToDouble(4607182418800017408L) - (Double.longBitsToDouble(4582862980812216730L) / vec3d.length());
    }

    public void handleItemUse(Hand hand) {
        if (minecraftClient.player == null) {
            return;
        }
        ItemStack stackInHand = minecraftClient.player.getStackInHand(hand);
        if (stackInHand.isEmpty()) {
            return;
        }
        Item item = stackInHand.getItem();
        if ((item instanceof BlockItem) || BlockPlacer.isThrowableItem(item)) {
            this.num = 2;
        }
    }

    public boolean hasModifiedRotation() {
        return this.flag;
    }

    public void resetActionTicks() {
        this.num = 2;
    }

    public void markRotationModified() {
        this.flag = true;
    }
}
