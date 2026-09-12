package me.mioclient.module.combat;

import me.mioclient.util.math.MathConstants;

import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.MathConstants;

import java.util.Comparator;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/AimAssist.class */
public class AimAssist extends Module {
    public Setting<Float> setting;
    public Setting<Float> setting2;
    public Setting<Float> setting3;
    public Setting<Boolean> setting4;
    public Setting<Boolean> setting5;
    public Setting<Boolean> setting6;
    public Setting<Boolean> setting7;
    public Setting<Boolean> setting8;
    public AbstractClientPlayerEntity abstractClientPlayerEntity;

    public AimAssist() {
        super("AimAssist", "Helps you aiming at players.", Category.COMBAT, new String[0]);
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (this.setting == null || this.setting2 == null || this.setting3 == null || this.setting4 == null || this.setting5 == null || this.setting6 == null || this.setting7 == null || this.setting8 == null) {
            return;
        }
        if (minecraftClient.currentScreen != null) {
            return;
        }
        if ((minecraftClient.player.getMainHandStack().getItem() instanceof AxeItem) || (minecraftClient.player.getMainHandStack().getItem() instanceof SwordItem) || !this.setting4.getValue().booleanValue()) {
            if (this.abstractClientPlayerEntity == null || !this.setting6.getValue().booleanValue()) {
                this.abstractClientPlayerEntity = (AbstractClientPlayerEntity) minecraftClient.world.getPlayers().stream().sorted(Comparator.comparing(abstractClientPlayerEntity -> {
                    return Float.valueOf(minecraftClient.player.distanceTo((Entity) abstractClientPlayerEntity));
                })).filter((v1) -> {
                    return meetsTargetCriteria(v1);
                }).findFirst().orElse(null);
            }
            if (!meetsTargetCriteria(this.abstractClientPlayerEntity)) {
                this.abstractClientPlayerEntity = null;
                return;
            }
            if (minecraftClient.targetedEntity == this.abstractClientPlayerEntity) {
                return;
            }
            if (this.setting8.getValue().booleanValue()) {
                float[] floatArray2484 = RotationManager.getRotationsTo(this.abstractClientPlayerEntity.getBoundingBox().getCenter());
                minecraftClient.player.setYaw(floatArray2484[0]);
                minecraftClient.player.setPitch(floatArray2484[1]);
                return;
            }
            double d = getAimYawDelta(this.abstractClientPlayerEntity);
            if (d > Double.longBitsToDouble(4607182418800017408L) || d < Double.longBitsToDouble(-4616189618054758400L)) {
                minecraftClient.player.setYaw(minecraftClient.player.getYaw(RenderUtil.getPartialTick()) - ((float) ((d * this.setting2.getValue().floatValue()) * Double.longBitsToDouble(4576918229175238656L))));
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x00d6, code lost:
    
        if (me.mioclient.api.ClientServices.socialManager.isFriend(((net.minecraft.entity.player.PlayerEntity) r5).getGameProfile().getName()) == false) goto L20;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean meetsTargetCriteria(Entity entity) {
        if ((entity instanceof PlayerEntity) && entity != minecraftClient.player && entity.isAlive() && (!this.setting5.getValue().booleanValue() || !entity.isInvisible())) {
            if (isWithinFovAngle(entity, this.setting3.getValue().floatValue()) && minecraftClient.player.distanceTo(entity) <= this.setting.getValue().floatValue()) {
                if (this.setting7.getValue().booleanValue()) {
                }
                if (hasLineOfSight(entity)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasLineOfSight(Entity entity) {
        Vec3d vec3d = new Vec3d(minecraftClient.player.getX(), minecraftClient.player.getY() + minecraftClient.player.getStandingEyeHeight(), minecraftClient.player.getZ());
        if (minecraftClient.world.raycast(new RaycastContext(vec3d, new Vec3d(entity.getX(), entity.getY(), entity.getZ()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, minecraftClient.player)).getType() == HitResult.Type.MISS) {
            return true;
        }
        return minecraftClient.world.raycast(new RaycastContext(vec3d, new Vec3d(entity.getX(), entity.getY() + ((double) entity.getStandingEyeHeight()), entity.getZ()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, minecraftClient.player)).getType() == HitResult.Type.MISS;
    }

    public static double getAimYawDelta(Entity entity) {
        return ((((minecraftClient.player.getYaw(RenderUtil.getPartialTick()) - getEntityYawAngle(entity)) % MathConstants.num3) + Double.longBitsToDouble(4647961106050973696L)) % MathConstants.num3) - Double.longBitsToDouble(4640537203540230144L);
    }

    public static boolean isWithinFovAngle(Entity entity, double d) {
        double d2 = (float) (d * MathConstants.val2);
        double yaw = ((((minecraftClient.player.getYaw(RenderUtil.getPartialTick()) - getEntityYawAngle(entity)) % MathConstants.num3) + Double.longBitsToDouble(4647961106050973696L)) % MathConstants.num3) - Double.longBitsToDouble(4640537203540230144L);
        return (yaw > 0.0d && yaw < d2) || ((-d2) < yaw && yaw < 0.0d);
    }

    public static float getEntityYawAngle(Entity entity) {
        return (float) (Math.atan2(entity.getX() - minecraftClient.player.getX(), entity.getZ() - minecraftClient.player.getZ()) * Double.longBitsToDouble(4633260481409690083L) * Double.longBitsToDouble(-4616189618054758400L));
    }
}
