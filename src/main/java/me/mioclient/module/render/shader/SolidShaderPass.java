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
/* loaded from: mio-yarn.jar:me/mioclient/SolidShaderPass.class */
public class SolidShaderPass extends OutlineShaderPass {
    public static final Identifier identifier = Identifier.of("mio-mount", "textures/overlay.png");
    public static Shader shader;

    public static Shader getShader() {
        if (shader == null && ClientServices.moduleInjector != null) {
            try { shader = (Shader) ClientServices.moduleInjector.getModule(Shader.class); } catch (Exception e) {}
        }
        return shader;
    }

    public SolidShaderPass() {
        initShaderProgram("solid");
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean isOutlinePassActive() {
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
        int glId = minecraftClient.getTextureManager().getTexture(identifier).getGlId();
        GlStateManager._activeTexture(33985);
        GlStateManager._bindTexture(glId);
        this.framebufferHelper_3.setUniformBool("u_Image", s.image.getValue().booleanValue());
        this.framebufferHelper_3.setUniformInt("u_Overlay", 1);
        this.framebufferHelper_3.setUniformFloat("u_OverlayAlpha", s.getOverlayAlpha());
        this.framebufferHelper_3.setUniformInt("u_Width", s.isHandOutlineDisabled() ? 0 : s.lineWidth.getValue().intValue());
        this.framebufferHelper_3.setUniformBool("u_FastLines", s.fastLines.getValue().booleanValue() || ShaderPassRegistry.flag2);
        this.framebufferHelper_3.setUniformInt("u_ShapeMode", 2);
        this.framebufferHelper_3.setUniformFloat("u_GlowMultiplier", s.glow.getValue().floatValue());
        this.framebufferHelper_3.setUniformColor("u_FillColor", s.isHandFillDisabled() ? ColorUtils.color : s.applyClusterAlpha(s.fill.getValue()));
        this.framebufferHelper_3.setUniformColor("u_OutlineColor", s.outline2.getValue());
        this.framebufferHelper_3.setUniformInt("u_Dots", s.type.getValue().ordinal());
        this.framebufferHelper_3.setUniformInt("u_DotsRadius", s.radius.getValue().intValue());
        this.framebufferHelper_3.setUniformFloat("u_DotsAlpha", s.alpha.getValue().floatValue());
    }
}
