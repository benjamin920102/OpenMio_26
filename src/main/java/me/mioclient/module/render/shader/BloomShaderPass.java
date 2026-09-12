package me.mioclient.module.render.shader;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.OutlineShaderPass;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.OutlineShaderPass;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;

import com.mojang.blaze3d.platform.GlStateManager;
import me.mioclient.module.render.Shader;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BloomShaderPass.class */
public class BloomShaderPass extends OutlineShaderPass {
    public static final Identifier identifier = Identifier.of("mio-mount", "textures/overlay.png");
    public static Shader shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class);

    public BloomShaderPass() {
        initShaderProgram("bloom");
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean isOutlinePassActive() {
        return shader.isToggled() && shader.shader.getValue() == Shader.MixinHeldItemRendererMode.BLOOM;
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean shouldOutlineEntity(Entity entity) {
        if (shader == null || entity == null) {
            return false;
        }
        return shader.shouldRenderEntity(entity);
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public void applyOutlineUniforms() {
        this.framebufferHelper_3.setUniformInt("u_Texture", 0);
        int glId = minecraftClient.getTextureManager().getTexture(identifier).getGlId();
        GlStateManager._activeTexture(33985);
        GlStateManager._bindTexture(glId);
        this.framebufferHelper_3.setUniformBool("u_Image", shader.image.getValue().booleanValue());
        this.framebufferHelper_3.setUniformInt("u_Overlay", 1);
        this.framebufferHelper_3.setUniformFloat("u_OverlayAlpha", shader.getOverlayAlpha());
        this.framebufferHelper_3.setUniformInt("u_Width", shader.isHandOutlineDisabled() ? 0 : shader.lineWidth.getValue().intValue());
        this.framebufferHelper_3.setUniformBool("u_FastLines", shader.fastLines.getValue().booleanValue() || ShaderPassRegistry.flag2);
        this.framebufferHelper_3.setUniformInt("u_ShapeMode", 2);
        this.framebufferHelper_3.setUniformFloat("u_GlowMultiplier", shader.glow.getValue().floatValue());
        this.framebufferHelper_3.setUniformInt("u_GlowQuality", shader.quality.getValue().intValue());
        this.framebufferHelper_3.setUniformColor("u_FillColor", shader.isHandFillDisabled() ? ColorUtils.withTransparentAlpha(shader.outline2.getValue()) : shader.applyClusterAlpha(shader.fill.getValue()));
        this.framebufferHelper_3.setUniformColor("u_OutlineColor", shader.outline2.getValue());
        this.framebufferHelper_3.setUniformInt("u_Dots", shader.type.getValue().ordinal());
        this.framebufferHelper_3.setUniformInt("u_DotsRadius", shader.radius.getValue().intValue());
        this.framebufferHelper_3.setUniformFloat("u_DotsAlpha", shader.alpha.getValue().floatValue());
    }
}
