package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.manager.SocialEntry;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.render.NameTagsHelperMode;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.manager.SocialEntry;
import me.mioclient.util.world.BlockPlacer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import me.mioclient.command.FakePlayerCommand;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.ducks.DuckPlayerEntity;
import me.mioclient.mode.render.NameTagsHelperMode;
import me.mioclient.event.events.player.AttackHookPostEvent;
import me.mioclient.event.events.player.ClipAtLedgeEvent;
import me.mioclient.module.exploit.Reach;
import me.mioclient.module.movement.Sprint;
import me.mioclient.module.player.Scaffold;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({PlayerEntity.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinPlayerEntity.class */
public abstract class MixinPlayerEntity extends LivingEntity implements DuckPlayerEntity {
    private static Scaffold scaffold;

    private static Scaffold getScaffold() {

        if (scaffold == null && ClientServices.moduleInjector != null) {

            try { scaffold = (Scaffold) ClientServices.moduleInjector.getModule(Scaffold.class); } catch (Exception e) {}

        }

        return scaffold;

    }
    private static Sprint sprint;

    private static Sprint getSprint() {

        if (sprint == null && ClientServices.moduleInjector != null) {

            try { sprint = (Sprint) ClientServices.moduleInjector.getModule(Sprint.class); } catch (Exception e) {}

        }

        return sprint;

    }
    private static Reach reach;

    private static Reach getReach() {

        if (reach == null && ClientServices.moduleInjector != null) {

            try { reach = (Reach) ClientServices.moduleInjector.getModule(Reach.class); } catch (Exception e) {}

        }

        return reach;

    }

    @Unique
    private long mio$lastEatingTime;

    @Unique
    private NameTagsHelperMode mio$role;

    @Unique
    private boolean mio$isNextToWall;

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = {"<init>"}, at = {@At("TAIL")})
    private void initHook(World world, BlockPos blockPos, float f, GameProfile gameProfile, CallbackInfo callbackInfo) {
        for (SocialEntry mixinPlayerEntityData : ClientServices.socialManager.getRegistry()) {
            if (gameProfile.getName().equalsIgnoreCase(mixinPlayerEntityData.getRawName())) {
                mio$setRole(mixinPlayerEntityData.getHelperMode());
                return;
            }
        }
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void tick(CallbackInfo callbackInfo) {
        this.mio$isNextToWall = !getWorld().isSpaceEmpty(getBoundingBox().expand(1.0E-4d, 0.0d, 1.0E-4d));
        if (!isUsingItem() || get70(getActiveItem(), this) > 1.0f) {
            this.mio$lastEatingTime = System.currentTimeMillis();
        }
    }

    @Inject(method = {"clipAtLedge"}, at = {@At("HEAD")}, cancellable = true)
    private void clipAtLedgeHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (ClientAccessor.minecraftClient.player == null) {
            return;
        }
        ClipAtLedgeEvent clipAtLedgeEvent = new ClipAtLedgeEvent();
        ClientAccessor.eventBus.fireEvent(clipAtLedgeEvent);
        if (clipAtLedgeEvent.isCancelled()) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @ModifyExpressionValue(method = {"adjustMovementForSneaking"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStepHeight()F")})
    private float getScaffoldStepHeight(float f) {
        Scaffold scaffold_lcl = getScaffold();

        if (scaffold_lcl == null) return f;

        if (!scaffold_lcl.isToggled() || !scaffold_lcl.safeWalk.getValue().booleanValue() || ClientAccessor.minecraftClient.player == null) {
            return f;
        }
        // 反编译把转型移到了 instanceof 检查之前——空手时 getItem() 是 AirBlockItem(非 BlockItem),
        // 未检查就强转 → ClassCastException。必须先 instanceof 再转。
        net.minecraft.item.Item rawItem = ClientAccessor.minecraftClient.player.getStackInHand(Hand.MAIN_HAND).getItem();
        if (!(rawItem instanceof BlockItem)) {
            return f;
        }
        BlockItem item = (BlockItem) rawItem;
        VoxelShape collisionShape = item.getBlock().getDefaultState().getCollisionShape(ClientAccessor.minecraftClient.world, BlockPos.ORIGIN);
        return (collisionShape == null || collisionShape.isEmpty()) ? f : (float) collisionShape.getBoundingBox().getLengthY();
    }

    @Inject(method = {"attack"}, at = {@At("HEAD")}, cancellable = true)
    public void attackHookPre(Entity entity, CallbackInfo callbackInfo) {
        AttackHookPostEvent attackHookPostEvent = new AttackHookPostEvent(KeyPearlMode.Pre, entity);
        ClientAccessor.eventBus.fireEvent(attackHookPostEvent);
        if (attackHookPostEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"attack"}, at = {@At("TAIL")})
    private void attackHookPost(Entity entity, CallbackInfo callbackInfo) {
        ClientAccessor.eventBus.fireEvent(new AttackHookPostEvent(KeyPearlMode.Post, entity));
    }

    @WrapWithCondition(method = {"attack"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V")})
    private boolean attackHook(PlayerEntity playerEntity, Vec3d vec3d) {
        Sprint sprint_lcl = getSprint();

        if (sprint_lcl == null) return true;

        return (sprint_lcl.isToggled() && sprint_lcl.keep.getValue().booleanValue()) ? false : true;
    }

    @WrapWithCondition(method = {"attack"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V")})
    private boolean attack(LivingEntity livingEntity, double d, double d2, double d3) {
        return (((((Object) this) instanceof FakePlayerCommand.OtherClientPlayerEntity) && livingEntity == ClientAccessor.minecraftClient.player) ? false : true);
    }

    @Inject(method = {"getEntityInteractionRange"}, at = {@At("HEAD")}, cancellable = true)
    private void getEntityInteractionRangeHook(CallbackInfoReturnable<Double> callbackInfoReturnable) {
        Reach reach_lcl = getReach();

        if (reach_lcl == null) return;

        if (reach_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(Double.valueOf(reach_lcl.modifier.getValue().doubleValue()));
        }
    }

    @Inject(method = {"getBlockInteractionRange"}, at = {@At("HEAD")}, cancellable = true)
    private void getBlockInteractionRangeHook(CallbackInfoReturnable<Double> callbackInfoReturnable) {
        Reach reach_lcl = getReach();

        if (reach_lcl == null) return;

        if (reach_lcl.isToggled()) {
            callbackInfoReturnable.setReturnValue(Double.valueOf(reach_lcl.modifier.getValue().doubleValue()));
        }
    }

    @Inject(method = {"shouldCancelInteraction"}, at = {@At("HEAD")}, cancellable = true)
    private void shouldCancelInteractionHook(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (BlockPlacer.flag) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public long mio$getLastEatingTime() {
        return this.mio$lastEatingTime;
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public NameTagsHelperMode mio$getRole() {
        return this.mio$role;
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public void mio$setRole(NameTagsHelperMode nameTagsHelperMode) {
        this.mio$role = nameTagsHelperMode;
    }

    @Override // me.mioclient.ducks.DuckPlayerEntity
    public boolean mio$isNextToWall() {
        return this.mio$isNextToWall;
    }
}
