package me.mioclient.module;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.NamedValue;
import me.mioclient.module.exploit.newchunks.NewChunkEntry;
import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.exploit.newchunks.NewChunkEntry;
import me.mioclient.api.NamedValue;
import me.mioclient.render.RenderUtil;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.math.ColorUtils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Iterator;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.manager.ScissorStackManager;
import me.mioclient.api.Setting;
import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.module.exploit.NewChunks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Map.class */
public class Map extends me.mioclient.module.HudModule {
    public Setting<Mode> setting;
    public Setting<Float> setting2;
    public Setting<Float> setting3;
    public Setting<Color> setting4;
    public Setting<Color> setting5;
    public Setting<Color> setting6;
    public static final float val2 = Float.intBitsToFloat(1090519040);
    public static final float val = Float.intBitsToFloat(1098907648);
    public static final NewChunks newChunks = (NewChunks) ClientServices.moduleInjector.getModule(NewChunks.class);
    public static final Identifier identifier = Identifier.of("mio", "textures/nav.png");

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/Map$Mode.class */
    public enum Mode implements NamedValue {
        NONE("None"),
        ARROW("Arrow"),
        DOT("Dot");

        public final String name;

        Mode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Map() {
        super("Map", "minimap");
        this.setting = add(new EnumSetting("Pointer", Mode.ARROW));
        this.setting2 = add(new NumberSetting("Width", Float.valueOf(Float.intBitsToFloat(1117257728)), Float.valueOf(Float.intBitsToFloat(1112014848)), Float.valueOf(Float.intBitsToFloat(1140457472))));
        this.setting3 = add(new NumberSetting("Height", Float.valueOf(Float.intBitsToFloat(1117257728)), Float.valueOf(Float.intBitsToFloat(1112014848)), Float.valueOf(Float.intBitsToFloat(1132068864))));
        this.setting4 = add(new ColorSetting("Pointer", Color.white));
        this.setting5 = add(new ColorSetting("Background", new Color(10, 10, 10, 50)));
        this.setting6 = add(new ColorSetting("Outline", new Color(10, 10, 10, 100)));
        this.setting4.setConfigName("PointerColor");
        this.setting4.setDisabled();
        setDescription(new StringTemplate().arg(String.valueOf(Formatting.YELLOW)).format("\u0001Primarily used for NewChunks"));
        ResizableHudElement size = new ResizableHudElement(this);
        size.enableScaling(this);
        setElement(size);
    }

    @Override // me.mioclient.module.HudModule
    public void renderContent(DrawContext drawContext) {
        float[] floatArray2950 = this.moduleListSearchHelper4.getMeasuredSize();
        if (floatArray2950 == null) {
            return;
        }
        float f = RenderSystem.getShaderColor()[3];
        RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
        Shape2DRenderer.resetShadowOffset();
        RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), f);
        RenderUtil.INSTANCE.drawFilledRectColor(drawContext.getMatrices(), Float.intBitsToFloat(-1082130432), Float.intBitsToFloat(-1082130432), this.setting2.getValue().floatValue(), this.setting3.getValue().floatValue(), this.setting5.getValue());
        RenderUtil.INSTANCE.drawRectOutline(drawContext.getMatrices(), Float.intBitsToFloat(-1073741824), Float.intBitsToFloat(-1073741824), this.setting2.getValue().floatValue(), this.setting3.getValue().floatValue(), this.setting6.getValue());
        float floatValue = this.setting2.getValue().floatValue() / Float.intBitsToFloat(1073741824);
        float floatValue2 = this.setting3.getValue().floatValue() / Float.intBitsToFloat(1073741824);
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(floatValue, floatValue2, 0.0f);
        Iterator<BlockPos> it = newChunks.getDungeonPositions().iterator();
        while (it.hasNext()) {
            ChunkPos chunkPos = new ChunkPos(it.next());
            if (isChunkInRange(chunkPos)) {
                drawChunkRect(drawContext, chunkPos, ColorUtils.withAlphaPercent(newChunks.fill5.getValue(), Float.intBitsToFloat(1053609165)));
            }
        }
        for (NewChunkEntry newChunksHelperSearchHelper4 : newChunks.getVisibleEntries()) {
            if (isChunkInRange(newChunksHelperSearchHelper4.getChunkPosition())) {
                if (newChunksHelperSearchHelper4.getEntryMode().getColors(newChunks) != null) {
                    Color color817 = ColorUtils.withAlphaPercent(newChunksHelperSearchHelper4.getEntryMode().getColors(newChunks)[0], Float.intBitsToFloat(1048576000));
                    drawChunkRect(drawContext, newChunksHelperSearchHelper4.getChunkPosition(), color817);
                }
            }
        }
        drawContext.getMatrices().pop();
        ScissorStackManager.pushClipRegion((int) this.moduleListSearchHelper4.getX(), (int) this.moduleListSearchHelper4.getY(), (int) floatArray2950[0], (int) floatArray2950[1]);
        Shape2DRenderer.resetShadowOffset();
        ScissorStackManager.popClipRegion();
        do2055(drawContext, floatValue, floatValue2);
    }

    @Override // me.mioclient.module.HudModule
    public float[] getSize() {
        return new float[]{this.setting2.getValue().floatValue(), this.setting3.getValue().floatValue()};
    }

    public boolean isChunkInRange(ChunkPos chunkPos) {
        int ceil = (int) Math.ceil(Math.hypot(this.setting2.getValue().floatValue(), this.setting3.getValue().floatValue()) / Double.longBitsToDouble(4625196817309499392L));
        return chunkPos.getSquaredDistance(minecraftClient.player.getChunkPos()) <= ceil * ceil;
    }

    public void drawChunkRect(DrawContext drawContext, ChunkPos chunkPos, Color color) {
        Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
        float startX = (((float) (pos.x - chunkPos.getStartX())) / Float.intBitsToFloat(1073741824)) - Float.intBitsToFloat(1090519040);
        float startZ = (((float) (pos.z - chunkPos.getStartZ())) / Float.intBitsToFloat(1073741824)) - Float.intBitsToFloat(1090519040);
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(startX, startZ, 0.0f);
        Shape2DRenderer.drawRect(drawContext.getMatrices(), 0.0f, 0.0f, Float.intBitsToFloat(1090519040), Float.intBitsToFloat(1090519040), color);
        drawContext.getMatrices().pop();
    }

    public void do2055(DrawContext drawContext, float f, float f2) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(f, f2, 0.0f);
        if (this.setting.getValue() == Mode.DOT) {
            drawContext.fill(-1, -1, 1, 1, this.setting4.getValue().hashCode());
        } else if (this.setting.getValue() == Mode.ARROW) {
            drawContext.getMatrices().scale(Float.intBitsToFloat(1028443341), Float.intBitsToFloat(1028443341), Float.intBitsToFloat(1065353216));
            drawContext.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Float.intBitsToFloat(1110704128) + minecraftClient.gameRenderer.getCamera().getYaw()));
            GlStateManager._texParameter(3553, 10240, 9729);
            RenderSystem.setShaderColor(this.setting4.getValue().getRed() / Float.intBitsToFloat(1132396544), this.setting4.getValue().getGreen() / Float.intBitsToFloat(1132396544), this.setting4.getValue().getBlue() / Float.intBitsToFloat(1132396544), Float.intBitsToFloat(1065353216));
            drawContext.drawTexture(identifier, -128, -128, 0, 0, 256, 256);
            RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
            GlStateManager._texParameter(3553, 10240, 9728);
        }
        drawContext.getMatrices().pop();
    }
}
