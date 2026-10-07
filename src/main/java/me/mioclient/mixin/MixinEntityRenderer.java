package me.mioclient.mixin;

import me.mioclient.api.ClientServices;
import me.mioclient.manager.RotationManager;
import me.mioclient.module.render.NameTags;
import me.mioclient.module.render.Chams;
import me.mioclient.module.render.chams.ChamsColorApplier;
import me.mioclient.ducks.DuckChamsState;
import me.mioclient.util.math.ColorUtils;
import java.awt.Color;
import me.mioclient.module.render.Shader;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified via JDK25 javap): EntityRenderer.extractRenderState sets
   EntityRenderState.outlineColor from vanilla glow (spectral/team) and vanilla's
   LevelRenderer.doEntityOutline() composites every non-zero outlineColor through
   its own post chain. Setting outlineColor here is the 26.2-native backend for the
   Shader module's entity glow — no custom framebuffer pass needed. Vanilla's own
   glow is preserved when the module does not claim the entity. */
@Mixin({EntityRenderer.class})

public abstract class MixinEntityRenderer {
    private static Shader shader;
    private static NameTags nameTags;
    private static Chams chams;

    private static Chams getChams() {
        if (chams == null && ClientServices.moduleInjector != null) {
            try {
                chams = (Chams) ClientServices.moduleInjector.getModule(Chams.class);
            } catch (Exception e) {
            }
        }
        return chams;
    }

    private static Shader getShader() {
        if (shader == null && ClientServices.moduleInjector != null) {
            try {
                shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class);
            } catch (Exception e) {
            }
        }
        return shader;
    }

    private static NameTags getNameTags() {
        if (nameTags == null && ClientServices.moduleInjector != null) {
            try {
                nameTags = (NameTags) ClientServices.moduleInjector.getModule(NameTags.class);
            } catch (Exception e) {
            }
        }
        return nameTags;
    }

    @Inject(method = {"extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V"}, at = {@At("TAIL")})
    private void mio$applyModGlow(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
        if (entity == null || state == null) {
            return;
        }
        try {
            Shader shaderLocal = getShader();
            if (shaderLocal != null && shaderLocal.isToggled() && shaderLocal.shouldRenderEntity(entity)) {
                state.outlineColor = ARGB.opaque(shaderLocal.outline2.getValue().hashCode());
            }
            Chams chamsLocal = getChams();
            if (chamsLocal != null && chamsLocal.isToggled() && chamsLocal.shouldRenderEntity(entity)) {
                ChamsColorApplier mode = chamsLocal.getChamsModeForEntity(entity).getValue();
                if (mode != null && mode.is594()) {
                    Color[] pair = mode.getChamsColors(chamsLocal);
                    Color fill = pair != null && pair.length > 1 ? pair[1] : null;
                    if (fill != null) {
                        if (chamsLocal.fade.getValue().booleanValue()) {
                            float f = chamsLocal.get2044(entity);
                            fill = ColorUtils.withAlpha(fill, (int) (f * fill.getAlpha()));
                        }
                        if (fill.getAlpha() != 0) {
                            ((DuckChamsState) (Object) state).mio$setChamsTint(fill.hashCode());
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @Inject(method = {"extractNameTags(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FDD)V"}, at = {@At("HEAD")}, cancellable = true)
    private void mio$suppressVanillaTags(Entity entity, EntityRenderState state, float partialTick, double d1, double d2, CallbackInfo ci) {
        /* The NameTags module replaces vanilla player labels; without this both would draw. */
        try {
            NameTags nt = getNameTags();
            Minecraft mc = Minecraft.getInstance();
            if (nt == null || !nt.isToggled() || !(entity instanceof Player) || mc.player == null || mc.level == null) {
                return;
            }
            if (entity.isRemoved() || !RotationManager.isInFrustum(entity.getBoundingBox())) {
                return;
            }
            if (mc.player.distanceTo(entity) > nt.range.getValue().intValue()) {
                return;
            }
            state.nameTag = null;
            state.scoreText = null;
            ci.cancel();
        } catch (Exception e) {
        }
    }
}
