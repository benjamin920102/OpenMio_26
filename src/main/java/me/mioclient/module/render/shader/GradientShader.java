package me.mioclient.module.render.shader;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.OutlineShader;
import me.mioclient.render.shader.ShaderRegistry;
import me.mioclient.util.math.ColorUtils;

import com.mojang.blaze3d.opengl.GlStateManager;
import me.mioclient.module.render.Shader;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.Identifier;

public class GradientShader extends OutlineShader {
    /* Resolved lazily -- the Shader module registers after these shaders can first
       load, so an eager static lookup left the ref null (NPE). */
    private static Shader shader;

    public static Shader getShader() {
        if (shader == null && ClientServices.moduleInjector != null) {
            try {
                shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class);
            } catch (Exception e) {
            }
        }
        return shader;
    }
    public static final Identifier identifier = Identifier.fromNamespaceAndPath("mio-mount", "textures/overlay.png");
    public final long num = System.currentTimeMillis();

    public GradientShader() {
        initShaderProgram("gradient");
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean isOutlineShaderActive() {
        Shader shader_lcl = getShader();
        return shader_lcl != null && shader_lcl.isToggled() && shader_lcl.shader.getValue() == Shader.MixinHeldItemRendererMode.GRADIENT;
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean shouldOutlineEntity(Entity entity) {
        Shader shader_lcl = getShader();
        if (shader_lcl == null || entity == null) {
            return false;
        }
        return shader_lcl.shouldRenderEntity(entity);
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public void applyOutlineUniforms() {
        Shader shader_lcl = getShader();
        if (shader_lcl == null) {
            return;
        }
        this.framebufferHelper_3.setUniformInt("u_Texture", 0);
        // 26.2: AbstractTexture.getGlId removed (GpuBackend GPU-driven textures).
        // Overlay texture view is managed by the GPU backend; select unit 1 for u_Overlay sampler.
        GlStateManager._activeTexture(33985);
        this.framebufferHelper_3.setUniformBool("u_Image", shader_lcl.image.getValue());
        this.framebufferHelper_3.setUniformInt("u_Overlay", 1);
        this.framebufferHelper_3.setUniformFloat("u_OverlayAlpha", shader_lcl.getOverlayAlpha());
        this.framebufferHelper_3.setUniformInt("u_Width", shader_lcl.isHandOutlineDisabled() ? 0 : shader_lcl.lineWidth.getValue());
        this.framebufferHelper_3.setUniformBool("u_FastLines", shader_lcl.fastLines.getValue() || ShaderRegistry.flag2);
        this.framebufferHelper_3.setUniformInt("u_ShapeMode", 2);
        this.framebufferHelper_3.setUniformFloat("u_GlowMultiplier", shader_lcl.glow.getValue());
        this.framebufferHelper_3.setUniformFloat("u_Time", ((float) (System.currentTimeMillis() - this.num)) * 0.00035f * shader_lcl.speed.getValue());
        if (shader_lcl.isHandFillDisabled()) {
            this.framebufferHelper_3.setUniformColor("u_FillColor", ColorUtils.color);
            this.framebufferHelper_3.setUniformColor("u_FillColor2", ColorUtils.color);
        } else {
            this.framebufferHelper_3.setUniformColor("u_FillColor", shader_lcl.applyClusterAlpha(shader_lcl.fill.getValue()));
            this.framebufferHelper_3.setUniformColor("u_FillColor2", shader_lcl.applyClusterAlpha(shader_lcl.fillSecond.getValue()));
        }
        this.framebufferHelper_3.setUniformColor("u_OutlineColor", shader_lcl.outline2.getValue());
        this.framebufferHelper_3.setUniformColor("u_OutlineColor2", shader_lcl.outlineSecond.getValue());
        this.framebufferHelper_3.setUniformInt("u_Dots", shader_lcl.type.getValue().ordinal());
        this.framebufferHelper_3.setUniformInt("u_DotsRadius", shader_lcl.radius.getValue());
        this.framebufferHelper_3.setUniformFloat("u_DotsAlpha", shader_lcl.alpha.getValue());
        this.framebufferHelper_3.setUniformFloat("u_Step", shader_lcl.step.getValue());
    }
}