package me.mioclient.render.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/* 26.2 backend: one batched TextRenderable per string (a single glyph render node)
   instead of one blit node per glyph. Quads reference the registered font atlas;
   setters are safe — BufferBuilder no-ops elements the format lacks (verified). */
public class FontGlyphRun implements TextRenderable {
    public static final class Quad {
        public float x;
        public float y;
        public float w;
        public float h;
        public float u;
        public float v;
        public float gw;
        public float gh;
        public int color;
    }

    public final List<Quad> quads = new ArrayList<>();
    public int atlasSize;
    public float minX = Float.MAX_VALUE;
    public float minY = Float.MAX_VALUE;
    public float maxX = -Float.MAX_VALUE;
    public float maxY = -Float.MAX_VALUE;

    public void addQuad(float x, float y, float w, float h, float u, float v, float gw, float gh, int color) {
        Quad quad = new Quad();
        quad.x = x;
        quad.y = y;
        quad.w = w;
        quad.h = h;
        quad.u = u;
        quad.v = v;
        quad.gw = gw;
        quad.gh = gh;
        quad.color = color;
        quads.add(quad);
        if (x < minX) {
            minX = x;
        }
        if (y < minY) {
            minY = y;
        }
        if (x + w > maxX) {
            maxX = x + w;
        }
        if (y + h > maxY) {
            maxY = y + h;
        }
    }

    public boolean isEmpty() {
        return quads.isEmpty();
    }

    @Override
    public void render(Matrix4fc pose, VertexConsumer vertexConsumer, int packedLight, boolean dropShadow) {
        if (atlasSize <= 0) {
            return;
        }
        Vector3f vec = new Vector3f();
        for (Quad quad : quads) {
            float u0 = quad.u / atlasSize;
            float v0 = quad.v / atlasSize;
            float u1 = (quad.u + quad.gw) / atlasSize;
            float v1 = (quad.v + quad.gh) / atlasSize;
            emit(vertexConsumer, pose, quad.x, quad.y, u0, v0, quad.color, vec);
            emit(vertexConsumer, pose, quad.x, quad.y + quad.h, u0, v1, quad.color, vec);
            emit(vertexConsumer, pose, quad.x + quad.w, quad.y + quad.h, u1, v1, quad.color, vec);
            emit(vertexConsumer, pose, quad.x + quad.w, quad.y, u1, v0, quad.color, vec);
        }
    }

    private static void emit(VertexConsumer vertexConsumer, Matrix4fc pose, float x, float y, float u, float v, int color, Vector3f vec) {
        vec.set(x, y, 0.0f);
        pose.transformPosition(vec);
        vertexConsumer.addVertex(vec.x, vec.y, 0.0f).setColor(color).setUv(u, v).setLight(15728880);
    }

    @Override
    public RenderType renderType(Font.DisplayMode displayMode) {
        return RenderTypes.text(FontGlyphAtlas.ATLAS_ID);
    }

    @Override
    public GpuTextureView textureView() {
        AbstractTexture texture = FontGlyphAtlas.getRegisteredTexture();
        return texture == null ? null : texture.getTextureView();
    }

    @Override
    public RenderPipeline guiPipeline() {
        return RenderPipelines.GUI_TEXT;
    }

    @Override
    public float left() {
        return minX == Float.MAX_VALUE ? 0.0f : minX;
    }

    @Override
    public float top() {
        return minY == Float.MAX_VALUE ? 0.0f : minY;
    }

    @Override
    public float right() {
        return maxX == -Float.MAX_VALUE ? 0.0f : maxX;
    }

    @Override
    public float bottom() {
        return maxY == -Float.MAX_VALUE ? 0.0f : maxY;
    }
}
