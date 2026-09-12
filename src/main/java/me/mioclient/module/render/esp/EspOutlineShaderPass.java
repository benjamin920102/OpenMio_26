package me.mioclient.module.render.esp;

import me.mioclient.api.ClientServices;

import me.mioclient.render.shader.OutlineShaderPass;

import me.mioclient.api.ClientServices;
import me.mioclient.render.shader.OutlineShaderPass;

import me.mioclient.module.render.ESP;
import net.minecraft.entity.Entity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EspOutlineShaderPass.class */
public class EspOutlineShaderPass extends OutlineShaderPass {
    public static ESP eSP = (ESP) ClientServices.moduleInjector.getModule(ESP.class);

    public EspOutlineShaderPass() {
        initShaderProgram("outline");
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean isOutlinePassActive() {
        return eSP.isToggled() && eSP.eSPSearchHelper4_2.hasBlockEntityEsp();
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public boolean shouldOutlineEntity(Entity entity) {
        return false;
    }

    @Override // me.mioclient.render.shader.PostProcessEffect
    public void applyOutlineUniforms() {
        this.framebufferHelper_3.setUniformInt("u_Texture", 0);
        this.framebufferHelper_3.setUniformInt("u_Width", 2);
        this.framebufferHelper_3.setUniformBool("u_FastLines", true);
        this.framebufferHelper_3.setUniformFloat("u_GlowMultiplier", Double.longBitsToDouble(4607182418800017408L));
    }
}
