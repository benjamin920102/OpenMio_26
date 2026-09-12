package me.mioclient.render.mesh;

import me.mioclient.api.ClientAccessor;

import me.mioclient.ducks.DuckCapabilityTracker;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL32C;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/GlStateWrapper.class */
public class GlStateWrapper implements ClientAccessor {
    public static boolean flag;
    public static boolean flag2;
    public static boolean flag3;
    public static boolean flag4;
    public static int num;
    public static int num2;
    public static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    public static final DuckCapabilityTracker stateHelper = getStateCapability("DEPTH");
    public static final DuckCapabilityTracker stateHelper2 = getStateCapability("BLEND");
    public static final DuckCapabilityTracker stateHelper3 = getStateCapability("CULL");
    public static final DuckCapabilityTracker stateHelper4 = getStateCapability("SCISSOR");
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
        GlStateManager._glDeleteVertexArrays(i);
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
        if (flag5) {
            BufferRenderer.currentVertexBuffer = null;
        }
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
        GlStateManager.glShaderSource(i, ImmutableList.of(str));
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
        GlStateManager._glUniformMatrix4(i, false, floatBuffer);
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

    public static void clearBuffers(int i) {
        GlStateManager._clearColor(0.0f, 0.0f, 0.0f, Float.intBitsToFloat(1065353216));
        GlStateManager._clear(i, false);
    }

    public static void saveState() {
        flag = stateHelper.getState();
        flag2 = stateHelper2.getState();
        flag3 = stateHelper3.getState();
        flag4 = stateHelper4.getState();
    }

    public static void restoreState() {
        stateHelper.set(flag);
        stateHelper2.set(flag2);
        stateHelper3.set(flag3);
        stateHelper4.set(flag4);
        disableSmoothing();
    }

    public static void enableDepthTest() {
        GlStateManager._enableDepthTest();
    }

    public static void disableDepthTest() {
        GlStateManager._disableDepthTest();
    }

    public static void enableBlend() {
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(770, 771);
    }

    public static void disableBlend() {
        GlStateManager._disableBlend();
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
        GL32C.glLineWidth(Float.intBitsToFloat(1065353216));
    }

    public static void disableSmoothing() {
        GL32C.glDisable(2848);
    }

    public static void bindNamedTexture(Identifier identifier) {
        GlStateManager._activeTexture(33984);
        minecraftClient.getTextureManager().bindTexture(identifier);
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

    public static DuckCapabilityTracker getStateCapability(String str) {
        try {
            Field declaredField = GlStateManager.class.getDeclaredField(str);
            declaredField.setAccessible(true);
            Object obj = declaredField.get(null);
            String mapClassName = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "com.mojang.blaze3d.platform.GlStateManager$class_1018");
            Field field = null;
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            int length = declaredFields.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Field field2 = declaredFields[i];
                if (field2.getType().getName().equals(mapClassName)) {
                    field = field2;
                    break;
                }
                i++;
            }
            field.setAccessible(true);
            return (DuckCapabilityTracker) field.get(obj);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
