package me.mioclient.module.render.shader;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.OutlineShader;
import me.mioclient.render.shader.ShaderRegistry;
import me.mioclient.util.math.ColorUtils;

import com.mojang.blaze3d.opengl.GlStateManager;
import me.mioclient.module.render.Shader;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.Identifier;

public class SolidShader extends OutlineShader {
    public static final Identifier identifier = Identifier.fromNamespaceAndPath("mio-mount", "textures/overlay.png");
    public static Shader shader;

    public static Shader getShader() {
        if (shader == null && ClientServices.moduleInjector != null) {
            try { shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class); } catch (Exception e) {}
        }
        return shader;
    }

    public SolidShader() {
        initShaderProgram("solid");
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean isOutlineShaderActive() {
        Shader s = getShader();
        return s != null && s.isToggled() && s.shader.getValue() == Shader.MixinHeldItemRendererMode.SOLID;
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean shouldOutlineEntity(Entity entity) {
        Shader s = getShader();
        if (s == null || entity == null) {
            return false;
        }
        return s.shouldRenderEntity(entity);
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public void applyOutlineUniforms() {
        Shader s = getShader();
        if (s == null) return;
        this.framebufferHelper_3.setUniformInt("u_Texture", 0);
        // 26.2: AbstractTexture.getGlId removed (GpuBackend GPU-driven textures).
        // Overlay texture view is managed by the GPU backend; select unit 1 for u_Overlay sampler.
        GlStateManager._activeTexture(33985);
        this.framebufferHelper_3.setUniformBool("u_Image", s.image.getValue().booleanValue());
        this.framebufferHelper_3.setUniformInt("u_Overlay", 1);
        this.framebufferHelper_3.setUniformFloat("u_OverlayAlpha", s.getOverlayAlpha());
        this.framebufferHelper_3.setUniformInt("u_Width", s.isHandOutlineDisabled() ? 0 : s.lineWidth.getValue().intValue());
        this.framebufferHelper_3.setUniformBool("u_FastLines", s.fastLines.getValue().booleanValue() || ShaderRegistry.flag2);
        this.framebufferHelper_3.setUniformInt("u_ShapeMode", 2);
        this.framebufferHelper_3.setUniformFloat("u_GlowMultiplier", s.glow.getValue().floatValue());
        this.framebufferHelper_3.setUniformColor("u_FillColor", s.isHandFillDisabled() ? ColorUtils.color : s.applyClusterAlpha(s.fill.getValue()));
        this.framebufferHelper_3.setUniformColor("u_OutlineColor", s.outline2.getValue());
        this.framebufferHelper_3.setUniformInt("u_Dots", s.type.getValue().ordinal());
        this.framebufferHelper_3.setUniformInt("u_DotsRadius", s.radius.getValue().intValue());
        this.framebufferHelper_3.setUniformFloat("u_DotsAlpha", s.alpha.getValue().floatValue());
    }
}