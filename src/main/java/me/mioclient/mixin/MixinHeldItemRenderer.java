package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.ShaderPassRegistry;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.ShaderPassRegistry;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.mioclient.event.events.render.MixinHeldItemRendererEvent;
import me.mioclient.module.misc.Swing;
import me.mioclient.module.render.Shader;
import me.mioclient.module.render.ViewModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/* compiled from: 0.java */
@Mixin(value = {HeldItemRenderer.class}, priority = 9999)
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinHeldItemRenderer.class */
public abstract class MixinHeldItemRenderer {
    private static Shader shader;

    private static Shader getShader() {

        if (shader == null && ClientServices.moduleInjector != null) {

            try { shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class); } catch (Exception e) {}

        }

        return shader;

    }
    private static ViewModel viewmodel;

    private static ViewModel getViewmodel() {

        if (viewmodel == null && ClientServices.moduleInjector != null) {

            try { viewmodel = (ViewModel) ClientServices.moduleInjector.getModule(ViewModel.class); } catch (Exception e) {}

        }

        return viewmodel;

    }
    private static Swing swing;

    private static Swing getSwing() {

        if (swing == null && ClientServices.moduleInjector != null) {

            try { swing = (Swing) ClientServices.moduleInjector.getModule(Swing.class); } catch (Exception e) {}

        }

        return swing;

    }

    @Shadow
    private ItemStack field_4047;

    @Shadow
    private float field_4043;

    @Shadow
    @Final
    private MinecraftClient field_4050;

    @Shadow
    protected abstract void method_3228(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float f2, Hand hand, float f3, ItemStack itemStack, float f4, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i);

    @Inject(method = {"renderFirstPersonItem"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")})
    private void onRenderItem(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float f2, Hand hand, float f3, ItemStack itemStack, float f4, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo callbackInfo) {
        ViewModel viewmodel_lcl = getViewmodel();

        if (viewmodel_lcl == null) return;

        if (viewmodel_lcl.isToggled()) {
            boolean z = itemStack.contains(DataComponentTypes.FOOD) || (itemStack.getItem() instanceof PotionItem);
            boolean z2 = ClientAccessor.minecraftClient.player.getActiveItem().getItem() == itemStack.getItem();
            if (z && ClientAccessor.minecraftClient.player.getActiveItem() == itemStack && !viewmodel_lcl.eating.getValue().booleanValue()) {
                return;
            }
            if (hand == Hand.MAIN_HAND) {
                float f5 = (z && z2) ? 0.0f : -viewmodel_lcl.mainX.getValue().floatValue();
                float floatValue = (z && z2) ? 0.0f : viewmodel_lcl.mainZ.getValue().floatValue();
                if (ClientAccessor.minecraftClient.player.getMainArm() == Arm.LEFT) {
                    f5 = -f5;
                }
                matrixStack.translate(f5, viewmodel_lcl.mainY.getValue().floatValue(), floatValue);
                matrixStack.scale(viewmodel_lcl.mainScaleX.getValue().floatValue(), viewmodel_lcl.mainScaleY.getValue().floatValue(), viewmodel_lcl.mainScaleZ.getValue().floatValue());
                matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(viewmodel_lcl.mainRotateX.getValue().floatValue()));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(viewmodel_lcl.mainRotateY.getValue().floatValue()));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(viewmodel_lcl.mainRotateZ.getValue().floatValue()));
                return;
            }
            float floatValue2 = (z && z2) ? 0.0f : viewmodel_lcl.offX.getValue().floatValue();
            float floatValue3 = (z && z2) ? 0.0f : viewmodel_lcl.offZ.getValue().floatValue();
            if (ClientAccessor.minecraftClient.player.getMainArm() == Arm.LEFT) {
                floatValue2 = -floatValue2;
            }
            matrixStack.translate(floatValue2, viewmodel_lcl.offY.getValue().floatValue(), floatValue3);
            matrixStack.scale(viewmodel_lcl.offScaleX.getValue().floatValue(), viewmodel_lcl.offScaleY.getValue().floatValue(), viewmodel_lcl.offScaleZ.getValue().floatValue());
            matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(viewmodel_lcl.offRotateX.getValue().floatValue()));
            matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(viewmodel_lcl.offRotateY.getValue().floatValue()));
            matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(viewmodel_lcl.offRotateZ.getValue().floatValue()));
        }
    }

    @Inject(method = {"renderArmHoldingItem"}, at = {@At("HEAD")})
    private void renderArmHook(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, float f2, Arm arm, CallbackInfo callbackInfo) {
        ViewModel viewmodel_lcl = getViewmodel();

        if (viewmodel_lcl == null) return;

        if (viewmodel_lcl.isToggled() && viewmodel_lcl.arm.getValue().booleanValue()) {
            float f3 = -viewmodel_lcl.mainX.getValue().floatValue();
            if (ClientAccessor.minecraftClient.player.getMainArm() == Arm.LEFT) {
                f3 = -f3;
            }
            matrixStack.translate(f3, viewmodel_lcl.mainY.getValue().floatValue(), viewmodel_lcl.mainZ.getValue().floatValue());
            matrixStack.scale(viewmodel_lcl.mainScaleX.getValue().floatValue(), viewmodel_lcl.mainScaleY.getValue().floatValue(), viewmodel_lcl.mainScaleZ.getValue().floatValue());
        }
    }

    @Redirect(method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"), require = 0)
    private void modifySwing(HeldItemRenderer heldItemRenderer, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float f2, Hand hand, float f3, ItemStack itemStack, float f4, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        ViewModel viewmodel_lcl = getViewmodel();

        // A @Redirect must always invoke the original, otherwise the held item simply never
        // renders. When the module is unavailable, pass the vanilla swing progress through.
        float swing = viewmodel_lcl == null ? f3 : viewmodel_lcl.get3142(hand, f3);
        method_3228(abstractClientPlayerEntity, f, f2, hand, swing, itemStack, f4, matrixStack, vertexConsumerProvider, i);
    }

    @ModifyVariable(method = {"renderFirstPersonItem"}, at = @At("HEAD"), argsOnly = true)
    private VertexConsumerProvider renderFirstPersonItemHook(VertexConsumerProvider vertexConsumerProvider) {
        Shader shader_lcl = getShader();

        // A @ModifyVariable must return the incoming value when it does not want to change it.
        // Returning null here replaced the vertex consumer provider argument with null, which
        // made the held item render into nothing.
        if (shader_lcl == null || !ShaderPassRegistry.flag) {
            return vertexConsumerProvider;
        }
        OutlineVertexConsumerProvider outlineVertexConsumerProvider = shader_lcl.shader.getValue().getShaderPass().outlineVertexConsumerProvider;
        outlineVertexConsumerProvider.setColor(255, 255, 255, 255);
        return outlineVertexConsumerProvider;
    }

    @Redirect(method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
    private void multiplyHook(MatrixStack matrixStack, Quaternionf quaternionf) {
        ViewModel viewmodel_lcl = getViewmodel();

        ClientAccessor.eventBus.fireEvent(new MixinHeldItemRendererEvent());
        // A @Redirect must invoke the original unless it deliberately suppresses it. Returning
        // early on a null module skipped the vanilla multiply and broke the item transform.
        if (viewmodel_lcl != null && viewmodel_lcl.isToggled() && viewmodel_lcl.noSway.getValue().booleanValue()) {
            return;
        }
        matrixStack.multiply(quaternionf);
    }

    @ModifyExpressionValue(method = {"updateHeldItems"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 2)})
    private float updateHeldItemsHook(float f) {
        Swing swing_lcl = getSwing();

        // @ModifyExpressionValue must pass the incoming value through when it has no opinion;
        // returning 0.0f here zeroed the clamped swing progress.
        if (swing_lcl == null) return f;

        if (!swing_lcl.isToggled() || swing_lcl.type.getValue() == Swing.MixinLivingEntityMode.VANILLA) {
            return f;
        }
        boolean z = swing_lcl.type.getValue() == Swing.MixinLivingEntityMode.ONE_EIGHT;
        float attackCooldownProgress = this.field_4050.player.getAttackCooldownProgress(1.0f);
        if (z) {
            attackCooldownProgress = 1.0f;
        }
        return MathHelper.clamp((!ItemStack.areItemsAndComponentsEqual(this.field_4047, ClientAccessor.minecraftClient.player.getMainHandStack()) ? 0.0f : (attackCooldownProgress * attackCooldownProgress) * attackCooldownProgress) - this.field_4043, z ? -0.6f : -0.4f, 0.4f);
    }

    @ModifyExpressionValue(method = {"renderFirstPersonItem"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingRiptide()Z")})
    private boolean isUsingRiptideHook(boolean z) {
        ViewModel viewmodel_lcl = getViewmodel();

        // Pass the vanilla value through when the module is unavailable.
        if (viewmodel_lcl == null) return z;

        if (viewmodel_lcl.isToggled() && viewmodel_lcl.noTridentAnim.getValue().booleanValue()) {
            return false;
        }
        return z;
    }

    @ModifyArgs(method = {"applyEquipOffset"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void applyEquipOffsetHook(Args args) {
        ViewModel viewmodel_lcl = getViewmodel();

        if (viewmodel_lcl == null) return;

        if (viewmodel_lcl.isToggled() && viewmodel_lcl.instantSwap.getValue().booleanValue()) {
            args.set(1, Float.valueOf(-0.52f));
        }
    }

    @ModifyArgs(method = {"applyEatOrDrinkTransformation"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0))
    private void applyEatOrDrinkTransformationHook(Args args) {
        ViewModel viewmodel_lcl = getViewmodel();

        if (viewmodel_lcl == null) return;

        if (viewmodel_lcl.isToggled()) {
            args.set(1, Float.valueOf(((Float) args.get(1)).floatValue() * viewmodel_lcl.eatMultiplier.getValue().floatValue()));
        }
    }
}
