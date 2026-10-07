package me.mioclient.render.mesh;

import me.mioclient.api.ClientAccessor;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;

public class GlStateWrapper implements ClientAccessor {
    public static boolean flag;
    public static boolean flag2;
    public static boolean flag3;
    public static boolean flag4;
    public static int num;
    public static int num2;
    public static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    public static boolean flag5 = true;

    public static int generateVertexArray() {
        return GlStateManager._glGenVertexArrays();
    }

    public static int generateBuffer() {
        return GlStateManager._glGenBuffers();
    }

    public static int generateTexture() {
        return GlStateManager._genTexture();
    }

    public static int generateFramebuffer() {
        return GlStateManager.glGenFramebuffers();
    }

    public static void deleteBuffer(int i) {
        GlStateManager._glDeleteBuffers(i);
    }

    public static void deleteVertexArray(int i) {
        GL33C.glDeleteVertexArrays(i);
    }

    public static void deleteShader(int i) {
        GlStateManager.glDeleteShader(i);
    }

    public static void deleteTexture(int i) {
        GlStateManager._deleteTexture(i);
    }

    public static void deleteFramebuffer(int i) {
        GlStateManager._glDeleteFramebuffers(i);
    }

    public static void deleteProgram(int i) {
        GlStateManager.glDeleteProgram(i);
    }

    public static void bindVertexArray(int i) {
        GlStateManager._glBindVertexArray(i);
    }

    public static void bindArrayBuffer(int i) {
        GlStateManager._glBindBuffer(34962, i);
    }

    public static void bindElementBuffer(int i) {
        if (i != 0) {
            num2 = num;
        }
        GlStateManager._glBindBuffer(34963, i != 0 ? i : num2);
    }

    public static void bindFramebuffer(int i) {
        GlStateManager._glBindFramebuffer(36160, i);
    }

    public static void uploadBufferData(int i, ByteBuffer byteBuffer, int i2) {
        GlStateManager._glBufferData(i, byteBuffer, i2);
    }

    public static void drawElements(int i, int i2, int i3) {
        GlStateManager._drawElements(i, i2, i3, 0L);
    }

    public static void enableVertexAttribute(int i) {
        GlStateManager._enableVertexAttribArray(i);
    }

    public static void setVertexAttributePointer(int i, int i2, int i3, boolean z, int i4, long j) {
        GlStateManager._vertexAttribPointer(i, i2, i3, z, i4, j);
    }

    public static int createShader(int i) {
        return GlStateManager.glCreateShader(i);
    }

    public static void setShaderSource(int i, String str) {
        GlStateManager.glShaderSource(i, str);
    }

    public static String compileShader(int i) {
        GlStateManager.glCompileShader(i);
        if (GlStateManager.glGetShaderi(i, 35713) == 0) {
            return GlStateManager.glGetShaderInfoLog(i, 512);
        }
        return null;
    }

    public static int createProgram() {
        return GlStateManager.glCreateProgram();
    }

    public static String linkProgram(int i, int i2, int i3) {
        GlStateManager.glAttachShader(i, i2);
        GlStateManager.glAttachShader(i, i3);
        GlStateManager.glLinkProgram(i);
        if (GlStateManager.glGetProgrami(i, 35714) == 0) {
            return GlStateManager.glGetProgramInfoLog(i, 512);
        }
        return null;
    }

    public static void useProgram(int i) {
        GlStateManager._glUseProgram(i);
    }

    public static int findUniformLocation(int i, String str) {
        return GlStateManager._glGetUniformLocation(i, str);
    }

    public static void uploadUniform1i(int i, int i2) {
        GlStateManager._glUniform1i(i, i2);
    }

    public static void uploadUniform1f(int i, float f) {
        GL32C.glUniform1f(i, f);
    }

    public static void uploadUniform2f(int i, float f, float f2) {
        GL32C.glUniform2f(i, f, f2);
    }

    public static void uploadUniform3f(int i, float f, float f2, float f3) {
        GL32C.glUniform3f(i, f, f2, f3);
    }

    public static void uploadUniform4f(int i, float f, float f2, float f3, float f4) {
        GL32C.glUniform4f(i, f, f2, f3, f4);
    }

    public static void uploadUniform3fv(int i, float[] fArr) {
        GL32C.glUniform3fv(i, fArr);
    }

    public static void uploadUniformMatrix(int i, Matrix4f matrix4f) {
        matrix4f.get(floatBuffer);
        GL33C.glUniformMatrix4fv(i, false, floatBuffer);
    }

    public static void setPixelStore(int i, int i2) {
        GlStateManager._pixelStore(i, i2);
    }

    public static void setTextureParameter(int i, int i2, int i3) {
        GlStateManager._texParameter(i, i2, i3);
    }

    public static void uploadTextureData(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, ByteBuffer byteBuffer) {
        GL32C.glTexImage2D(i, i2, i3, i4, i5, i6, i7, i8, byteBuffer);
    }

    public static void resetPixelStoreDefaults() {
        setPixelStore(3312, 0);
        setPixelStore(3313, 0);
        setPixelStore(3314, 0);
        setPixelStore(32878, 0);
        setPixelStore(3315, 0);
        setPixelStore(3316, 0);
        setPixelStore(32877, 0);
        setPixelStore(3317, 4);
    }

    public static void generateMipmap(int i) {
        GL32C.glGenerateMipmap(i);
    }

    public static void attachFramebufferTexture(int i, int i2, int i3, int i4, int i5) {
        GlStateManager._glFramebufferTexture2D(i, i2, i3, i4, i5);
    }

    public static void saveState() {
        flag = flag2 = flag3 = flag4 = true;
    }

    public static void restoreState() {
        disableSmoothing();
    }

    public static void enableDepthTest() {
        GlStateManager._enableDepthTest();
    }

    public static void disableDepthTest() {
        GlStateManager._disableDepthTest();
    }

    public static void enableBlend() {
        GlStateManager._enableBlend(0);
        GlStateManager._blendFuncSeparate(770, 771, 770, 771);
    }

    public static void disableBlend() {
        GlStateManager._disableBlend(0);
    }

    public static void enableCull() {
        GlStateManager._enableCull();
    }

    public static void disableCull() {
        GlStateManager._disableCull();
    }

    public static void enableScissor() {
        GlStateManager._enableScissorTest();
    }

    public static void disableScissor() {
        GlStateManager._disableScissorTest();
    }

    public static void enableSmoothing() {
        GL32C.glEnable(2848);
        GL32C.glLineWidth(1.0f);
    }

    public static void disableSmoothing() {
        GL32C.glDisable(2848);
    }

    public static void bindNamedTexture(Identifier identifier) {
        // 26.2: raw GL binds are gone (GPU-driven textures); atlas binding is a no-op here.
        GlStateManager._activeTexture(33984);
    }

    public static void bindTextureUnit(int i, int i2) {
        GlStateManager._activeTexture(33984 + i2);
        GlStateManager._bindTexture(i);
    }

    public static void bindTexture(int i) {
        bindTextureUnit(i, 0);
    }

    public static void resetActiveTexture() {
        GlStateManager._activeTexture(33984);
    }
}
