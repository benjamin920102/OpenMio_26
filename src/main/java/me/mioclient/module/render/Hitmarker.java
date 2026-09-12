package me.mioclient.module.render;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;

import me.mioclient.util.client.InteractPacketReader;
import me.mioclient.util.math.ColorUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.client.InteractPacketReader;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.render.HudRenderEvent;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.mixin.ducks.DuckPlayerInteractEntityC2SPacket;
import me.mioclient.module.Module;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Hitmarker.class */
public class Hitmarker extends Module {
    public Setting<Boolean> draw = add(new BooleanSetting("Draw", true).setVisible());
    public Setting<Float> length = add(new NumberSetting<>("Length", 3.0f, 0.1f, 10.0f, v -> this.draw.isShown()));
    public Setting<Float> time = add(new NumberSetting<>("Time", 1.0f, 0.5f, 3.0f, v -> this.draw.isShown()).setSuffix("s"));
    public Setting<Float> fadeTime = add(new NumberSetting<>("FadeTime", 0.5f, 0.5f, 3.0f, v -> this.draw.isShown()).setSuffix("s"));
    public Setting<Color> color = add(new ColorSetting("Color", new Color(-1581409793, true), v -> this.draw.isShown()));
    public Setting<Color> outlineColor = add(new ColorSetting("OutlineColor", new Color(-16777216, true), v -> this.draw.isShown()));
    public Setting<Boolean> sound2 = add(new BooleanSetting("Sound", false).setVisible());
    public Setting<SoundIdentifier> sound = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "neverlose")).requiresSettings(this.sound2));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 0.5f, 0.0f, 1.0f, v -> this.sound2.isShown()));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", false).setDependent());
    public Setting<Boolean> animals = add(new BooleanSetting("Animals", false, v -> this.targets.isShown()));
    public Setting<Boolean> hostiles = add(new BooleanSetting("Hostiles", false, v -> this.targets.isShown()));
    public Setting<Boolean> players = add(new BooleanSetting("Players", true, v -> this.targets.isShown()));
    public Setting<Boolean> crystals = add(new BooleanSetting("Crystals", false, v -> this.targets.isShown()));
    public static Crosshair crosshair = (Crosshair) ClientServices.moduleInjector.getModule(Crosshair.class);
    public long num;
    public int num2;
    public boolean flag;

    public Hitmarker() {
        super("Hitmarker", "Marks your screen when you hit entities.", Category.RENDER, new String[0]);
        this.num = -1L;
        this.num2 = 255;
        this.flag = false;
        setDrawn(false);
        this.sound.setConfigName("Type");
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.num = -1L;
        this.num2 = 255;
        this.flag = false;
    }

    @Listen
    public void onHudRender(HudRenderEvent matrixStackEvent_2) {
        if (isNotInGame() || this.num == -1) {
            return;
        }
        if (this.flag) {
            this.flag = false;
            if (this.sound2.getValue().booleanValue()) {
                ClientServices.killstreakSoundManager.playSound(this.sound.getValue(), this.volume.getValue().floatValue());
            }
        }
        long currentTimeMillis = System.currentTimeMillis() - this.num;
        if (currentTimeMillis > this.time.getValue().floatValue() * Float.intBitsToFloat(1148846080)) {
            this.num = -1L;
            return;
        }
        updateFadeAlpha(currentTimeMillis);
        if (this.num2 <= 0 || !this.draw.getValue().booleanValue()) {
            return;
        }
        float scaledWidth = minecraftClient.getWindow().getScaledWidth() / Float.intBitsToFloat(1073741824);
        float scaledHeight = (minecraftClient.getWindow().getScaledHeight() / Float.intBitsToFloat(1073741824)) - Float.intBitsToFloat(1056964608);
        if (crosshair.isToggled()) {
            scaledHeight = (float) (scaledHeight + Double.longBitsToDouble(4602678819172646912L));
        } else {
            scaledWidth = (float) (scaledWidth - Double.longBitsToDouble(4602678819172646912L));
        }
        Color color816 = ColorUtils.withAlpha(this.outlineColor.getValue(), this.num2);
        Color color8162 = ColorUtils.withAlpha(this.color.getValue(), this.num2);
        float intBitsToFloat = Float.intBitsToFloat(1073741824) + this.length.getValue().floatValue();
        int i = 0;
        while (i < 2) {
            Color color = i == 0 ? color816 : color8162;
            matrixStackEvent_2.getMatrixStack().push();
            if (crosshair.isToggled()) {
                matrixStackEvent_2.getMatrixStack().translate(Double.longBitsToDouble(-4619792497756654797L), 0.0d, 0.0d);
            }
            drawHitmarkerLine(matrixStackEvent_2.getMatrixStack(), color, scaledWidth - intBitsToFloat, scaledHeight - intBitsToFloat, scaledWidth - Float.intBitsToFloat(1073741824), scaledHeight - Float.intBitsToFloat(1073741824));
            drawHitmarkerLine(matrixStackEvent_2.getMatrixStack(), color, scaledWidth - intBitsToFloat, scaledHeight + intBitsToFloat, scaledWidth - Float.intBitsToFloat(1073741824), scaledHeight + Float.intBitsToFloat(1073741824));
            drawHitmarkerLine(matrixStackEvent_2.getMatrixStack(), color, scaledWidth + Float.intBitsToFloat(1073741824), scaledHeight - Float.intBitsToFloat(1073741824), scaledWidth + intBitsToFloat, scaledHeight - intBitsToFloat);
            drawHitmarkerLine(matrixStackEvent_2.getMatrixStack(), color, scaledWidth + Float.intBitsToFloat(1073741824), scaledHeight + Float.intBitsToFloat(1073741824), scaledWidth + intBitsToFloat, scaledHeight + intBitsToFloat);
            matrixStackEvent_2.getMatrixStack().pop();
            i++;
        }
    }

    @Listen(get219= RenderConstants.num4)
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (isNotInGame() || sendImmediatelyEvent.isCancelled()) {
            return;
        }
        DuckPlayerInteractEntityC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractEntityC2SPacket ? (DuckPlayerInteractEntityC2SPacket)(sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerInteractEntityC2SPacket) {
            DuckPlayerInteractEntityC2SPacket duckPlayerInteractEntityC2SPacket = (DuckPlayerInteractEntityC2SPacket)((PlayerInteractEntityC2SPacket) packet904);
            if (InteractPacketReader.getCrystalInteractionType((PlayerInteractEntityC2SPacket) duckPlayerInteractEntityC2SPacket) == InteractPacketReader.LegacyCrystalMode.ATTACK) {
                Entity entityById = minecraftClient.world.getEntityById(duckPlayerInteractEntityC2SPacket.getEntityId());
                if (entityById == null || entityById.getWorld() == null || entityById == minecraftClient.player || !entityById.getWorld().isClient || !shouldTrackEntity(entityById)) {
                    return;
                }
                this.num = System.currentTimeMillis();
                this.num2 = 255;
                this.flag = true;
            }
        }
    }

    public void updateFadeAlpha(long j) {
        if (j < (this.time.getValue().floatValue() * Float.intBitsToFloat(1148846080)) - (this.fadeTime.getValue().floatValue() * Float.intBitsToFloat(1148846080))) {
            this.num2 = 255;
            return;
        }
        this.num2 = (int) (this.num2 - ((Float.intBitsToFloat(1132396544) / this.fadeTime.getValue().floatValue()) * ClientServices.hitmarkerHelper.getFrameDelta(Float.intBitsToFloat(1065353216))));
        this.num2 = MathHelper.clamp(this.num2, 0, 255);
    }

    public boolean shouldTrackEntity(Entity entity) {
        return (this.animals.getValue().booleanValue() && (entity instanceof PassiveEntity)) || (this.hostiles.getValue().booleanValue() && (entity instanceof Monster)) || ((this.players.getValue().booleanValue() && (entity instanceof PlayerEntity)) || (this.crystals.getValue().booleanValue() && (entity instanceof EndCrystalEntity)));
    }

    public static void drawHitmarkerLine(MatrixStack matrixStack, Color color, float f, float f2, float f3, float f4) {
        float[] fArr = {color.getRed() / Float.intBitsToFloat(1132396544), color.getGreen() / Float.intBitsToFloat(1132396544), color.getBlue() / Float.intBitsToFloat(1132396544), color.getAlpha() / Float.intBitsToFloat(1132396544)};
        Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        begin.vertex(positionMatrix, f, f2, 0.0f).color(fArr[0], fArr[1], fArr[2], fArr[3]);
        begin.vertex(positionMatrix, f3, f4, 0.0f).color(fArr[0], fArr[1], fArr[2], fArr[3]);
        RenderUtil.INSTANCE.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferRenderer.drawWithGlobalProgram(begin.end());
        RenderUtil.INSTANCE.disableBlend();
    }
}
