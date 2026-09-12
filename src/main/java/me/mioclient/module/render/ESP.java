package me.mioclient.module.render;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.module.render.esp.ESPRenderMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shader.ShaderFramebufferSwapper;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.esp.EspBoxRenderer;
import me.mioclient.module.render.esp.EspOutlineRenderer;
import me.mioclient.module.render.esp.EspTargetFilter;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.RenderUtil;
import me.mioclient.render.shader.ShaderFramebufferSwapper;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.module.render.esp.ESPRenderMode;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.Colors;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.light.ChunkLightingView;
import org.joml.Matrix4f;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/ESP.class */
public class ESP extends Module {
    public static Colors colors = (Colors) ClientServices.moduleInjector.getModule(Colors.class);
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", true).setDependent());
    public Setting<Boolean> mobOwner = add(new BooleanSetting("MobOwner", false).requiresSettings(this.targets));
    public Setting<Boolean> light = add(new BooleanSetting("Light", false).requiresSettings(this.targets));
    public Setting<Boolean> hostiles = add(new BooleanSetting("Hostiles", false).setVisible().requiresSettings(this.targets));
    public Setting<Color> fill5 = add(new ColorSetting("Fill", new Color(364747263, true)).setThemeSync(true).requiresSettings(this.hostiles, this.targets));
    public Setting<Color> outline2 = add(new ColorSetting("Outline", new Color(-910321153, true)).setThemeSync(true).requiresSettings(this.hostiles, this.targets));
    public Setting<Boolean> players = add(new BooleanSetting("Players", false).setVisible().requiresSettings(this.targets));
    public Setting<Color> fill6 = add(new ColorSetting("Fill", new Color(364747263, true)).setThemeSync(true).requiresSettings(this.players, this.targets));
    public Setting<Color> outline4 = add(new ColorSetting("Outline", new Color(-910321153, true)).setThemeSync(true).requiresSettings(this.players, this.targets));
    public Setting<Boolean> animals = add(new BooleanSetting("Animals", false).setVisible().requiresSettings(this.targets));
    public Setting<Color> fill2 = add(new ColorSetting("Fill", new Color(364747263, true)).setThemeSync(true).requiresSettings(this.animals, this.targets));
    public Setting<Color> outline6 = add(new ColorSetting("Outline", new Color(-910321153, true)).setThemeSync(true).requiresSettings(this.animals, this.targets));
    public Setting<Boolean> pearls = add(new BooleanSetting("Pearls", false).setVisible().requiresSettings(this.targets));
    public Setting<Color> fill3 = add(new ColorSetting("Fill", new Color(364747263, true)).setThemeSync(true).requiresSettings(this.pearls, this.targets));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(-910321153, true)).setThemeSync(true).requiresSettings(this.pearls, this.targets));
    public Setting<Boolean> items = add(new BooleanSetting("Items", false).setVisible().requiresSettings(this.targets));
    public Setting<ESPRenderMode> mode = add(new EnumSetting<>("Mode", ESPRenderMode.BOX).requiresSettings(this.targets, this.items));
    public Setting<Integer> range = add(new NumberSetting<>("Range", 256, 1, 256).setSuffix("m").requiresSettings(this.targets, this.items));
    public Setting<Boolean> group = add(new BooleanSetting("Group", false));
    public Setting<Color> itemsText = add(new ColorSetting("ItemsText", new Color(-1, true), v -> (this.mode.getValue() == ESPRenderMode.TEXT || this.mode.getValue() == ESPRenderMode.BOTH) && this.targets.isShown() && this.items.isShown()).setThemeSync(true));
    public Setting<Color> background = add(new ColorSetting("Background", new Color(657930, true), v -> this.mode.getValue() == ESPRenderMode.TEXT && this.targets.isShown() && this.items.isShown()));
    public Setting<Float> scale = add(new NumberSetting<>("Scale", 1.0f, 0.5f, 2.0f, v -> this.mode.getValue() == ESPRenderMode.TEXT && this.targets.isShown() && this.items.isShown()));
    public Setting<Color> fill4 = add(new ColorSetting("Fill", new Color(364747263, true), v -> (this.mode.getValue() == ESPRenderMode.BOX || this.mode.getValue() == ESPRenderMode.BOTH) && this.targets.isShown() && this.items.isShown()).setThemeSync(true));
    public Setting<Color> outline3 = add(new ColorSetting("Outline", new Color(-910321153, true), v -> (this.mode.getValue() == ESPRenderMode.BOX || this.mode.getValue() == ESPRenderMode.BOTH) && this.targets.isShown() && this.items.isShown()).setThemeSync(true));
    public Setting<Boolean> exp = add(new BooleanSetting("Exp", false).setVisible().requiresSettings(this.targets));
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(364747263, true)).setThemeSync(true).requiresSettings(this.exp, this.targets));
    public Setting<Color> outline5 = add(new ColorSetting("Outline", new Color(-910321153, true)).setThemeSync(true).requiresSettings(this.exp, this.targets));
    public Setting<Boolean> chorus = add(new BooleanSetting("Chorus", false).setVisible().requiresSettings(this.targets));
    public Setting<Color> chorusText = add(new ColorSetting("ChorusText", new Color(-1, true)).setThemeSync(true).requiresSettings(this.chorus, this.targets));
    public Setting<Boolean> blocks = add(new BooleanSetting("Blocks", true).setDependent());
    public Setting<Boolean> chests = add(new BooleanSetting("Chests", true).requiresSettings(this.blocks));
    public Setting<Boolean> eChests = add(new BooleanSetting("EChests", true).requiresSettings(this.blocks));
    public Setting<Boolean> shulkers = add(new BooleanSetting("Shulkers", true).requiresSettings(this.blocks));
    public Setting<Boolean> beds = add(new BooleanSetting("Beds", false).requiresSettings(this.blocks));
    public Setting<Boolean> signs = add(new BooleanSetting("Signs", false).requiresSettings(this.blocks));
    public Setting<Boolean> dispensers = add(new BooleanSetting("Dispensers", false).requiresSettings(this.blocks));
    public Setting<Boolean> hoppers = add(new BooleanSetting("Hoppers", false).requiresSettings(this.blocks));
    public Setting<Boolean> furnaces = add(new BooleanSetting("Furnaces", false).requiresSettings(this.blocks));
    public Setting<Boolean> pots = add(new BooleanSetting("Pots", false).requiresSettings(this.blocks));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.0f, 4.0f));
    public boolean flag;
    public final EspOutlineRenderer eSPSearchHelper4_2;
    public final Map<Vec3d, Long> map;

    public ESP() {
        super("ESP", "Highlights entities through walls.", Category.RENDER, new String[0]);
        this.eSPSearchHelper4_2 = new EspOutlineRenderer(this);
        this.map = Collections.synchronizedMap(new HashMap());
        this.fill2.setConfigName("AnimalsFill");
        this.outline6.setConfigName("AnimalsOutline");
        this.fill5.setConfigName("HostilesFill");
        this.outline2.setConfigName("HostilesOutline");
        this.fill6.setConfigName("PlayersFill");
        this.outline4.setConfigName("PlayersOutline");
        this.fill4.setConfigName("ItemsFill");
        this.outline3.setConfigName("ItemsOutline");
        this.fill.setConfigName("ExpFill");
        this.outline5.setConfigName("ExpOutline");
        this.mode.setConfigName("ItemsMode");
        this.range.setConfigName("ItemRange");
        setDrawn(false);
    }

    @Listen
    public void onRenderWorldPre(MatrixStackEvent.Inner inner) {
        if (this.light.getValue().booleanValue()) {
            renderSpawnHighlights(inner);
        }
    }

    @Listen(get219= -99999999)
    public void onRenderWorldOutline(MatrixStackEvent.Inner_3 inner_3) {
        if (isNotInGame() || !this.eSPSearchHelper4_2.hasBlockEntityEsp()) {
            return;
        }
        ShaderFramebufferSwapper.swapFramebuffer(ShaderPassRegistry.shaderFramebufferHelper5, false);
        inner_3.getMatrixStack().push();
        this.flag = true;
        Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
        OutlineVertexConsumerProvider outlineVertexConsumerProvider = ShaderPassRegistry.shaderFramebufferHelper5.outlineVertexConsumerProvider;
        EspBoxRenderer.disableEntityCulling();
        for (BlockEntity blockEntity : ClientServices.stashFinderHelper.getLoadedBlockEntities()) {
            if (this.eSPSearchHelper4_2.shouldHighlightBlockEntity(blockEntity)) {
                double x = blockEntity.getPos().getX() - pos.x;
                double y = blockEntity.getPos().getY() - pos.y;
                double z = blockEntity.getPos().getZ() - pos.z;
                inner_3.getMatrixStack().push();
                inner_3.getMatrixStack().translate(x, y, z);
                Color color1596 = BoxRenderer.applyShaderColor(this.eSPSearchHelper4_2.getBlockEntityColor(blockEntity), colors.scheme.getValue().getShaderColor());
                outlineVertexConsumerProvider.setColor(color1596.getRed(), color1596.getGreen(), color1596.getBlue(), 255);
                if (minecraftClient.getBlockEntityRenderDispatcher().get(blockEntity) == null) {
                    minecraftClient.getBlockRenderManager().renderBlock(blockEntity.getCachedState(), blockEntity.getPos(), minecraftClient.world, inner_3.getMatrixStack(), outlineVertexConsumerProvider.getBuffer(RenderLayer.getSolid()), false, minecraftClient.world.getRandom());
                } else {
                    minecraftClient.getBlockEntityRenderDispatcher().render(blockEntity, inner_3.getTickDelta(), inner_3.getMatrixStack(), (VertexConsumerProvider) outlineVertexConsumerProvider);
                }
                inner_3.getMatrixStack().pop();
            }
        }
        EspBoxRenderer.restoreEntityCulling();
        minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();
        this.flag = false;
        inner_3.getMatrixStack().pop();
        ShaderFramebufferSwapper.restoreFramebuffer(false);
    }

    @Listen(get219= RenderConstants.num5)
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (isNotInGame()) {
            return;
        }
        ArrayList<Entity> arrayList = new ArrayList();
        Iterator it = minecraftClient.world.getEntities().iterator();
        while (it.hasNext()) {
            arrayList.add((Entity) it.next());
        }
        arrayList.sort(Comparator.comparing(entity -> {
            return Integer.valueOf(-entity.age);
        }));
        ArrayList arrayList2 = new ArrayList();
        for (Entity entity2 : arrayList) {
            if (this.eSPSearchHelper4_2.shouldRenderEntity(entity2)) {
                BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), BoxMath.getInterpolatedBox(entity2, inner_3.getTickDelta()), this.eSPSearchHelper4_2.getEntityColor(entity2, true));
                BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), BoxMath.getInterpolatedBox(entity2, inner_3.getTickDelta()), this.eSPSearchHelper4_2.getEntityColor(entity2, false), this.lineWidth.getValue().floatValue());
            }
            if (entity2 instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity) entity2;
                if (this.items.getValue().booleanValue() && (this.mode.getValue() == ESPRenderMode.BOTH || this.mode.getValue() == ESPRenderMode.TEXT)) {
                    Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
                    if (!(entity2 instanceof ItemEntity) || entity2.getPos().distanceTo(pos) <= this.range.getValue().intValue()) {
                        if (this.group.getValue().booleanValue()) {
                            boolean z = false;
                            Iterator it2 = arrayList2.iterator();
                            while (it2.hasNext()) {
                                if (((EspTargetFilter) it2.next()).addTrackedItem(itemEntity)) {
                                    z = true;
                                }
                            }
                            if (!z) {
                                arrayList2.add(new EspTargetFilter(itemEntity));
                            }
                        } else {
                            String string = itemEntity.getStack().getName().getString();
                            if (itemEntity.getStack().getCount() > 1) {
                                string = new StringTemplate().arg(itemEntity.getStack().getCount()).arg(string).format("\u0001 x\u0001");
                            }
                            this.eSPSearchHelper4_2.renderBillboardLabel(inner_3, string, itemEntity.getLerpedPos(inner_3.getTickDelta()).add(0.0d, Double.longBitsToDouble(4603579539312869376L), 0.0d), this.scale.getValue().floatValue(), this.itemsText.getValue(), this.background.getValue());
                        }
                    }
                }
            }
            String string1768 = this.eSPSearchHelper4_2.getOwnerName(entity2);
            if (string1768 != null) {
                Vec3d add = entity2.getLerpedPos(inner_3.getTickDelta()).add(0.0d, Double.longBitsToDouble(4596373779694328218L), 0.0d);
                if (!(entity2 instanceof EnderPearlEntity)) {
                    add = add.add(0.0d, Double.longBitsToDouble(4608533498688228557L), 0.0d);
                }
                this.eSPSearchHelper4_2.renderBillboardLabel(inner_3, string1768, add, Float.intBitsToFloat(1065353216), Color.white, (Color) null);
            }
        }
        if (this.group.getValue().booleanValue()) {
            do1915(inner_3, arrayList2);
        }
        if (this.chorus.getValue().booleanValue()) {
            synchronized (this.map) {
                for (Map.Entry<Vec3d, Long> entry : this.map.entrySet()) {
                    this.eSPSearchHelper4_2.renderBillboardLabel(inner_3, "Player teleport", entry.getKey(), Float.intBitsToFloat(1065353216), this.chorusText.getValue(), (Color) null);
                }
            }
        }
    }

    public void do1915(MatrixStackEvent matrixStackEvent, List<EspTargetFilter> list) {
        for (EspTargetFilter eSPSearchHelper4 : list) {
            if (!this.group.getValue().booleanValue()) {
                return;
            }
            Vec3d center = eSPSearchHelper4.getMergedBox().getCenter();
            double d = MathUtils.getDistanceScale(minecraftClient.gameRenderer.getCamera().getPos(), center, this.scale.getValue().floatValue());
            float f = ClientTextRenderer.INSTANCE.getFontHeight();
            float f2 = 0.0f;
            if (eSPSearchHelper4.getItemCountMap().size() == 1) {
                drawGroupedItemBg(matrixStackEvent.getMatrixStack(), eSPSearchHelper4, d, eSPSearchHelper4.getItemCountMap().size() * f);
            } else {
                drawGroupedItemBg(matrixStackEvent.getMatrixStack(), eSPSearchHelper4, d, f);
                drawGroupedItemBg(matrixStackEvent.getMatrixStack(), eSPSearchHelper4, d, ((eSPSearchHelper4.getItemCountMap().size() - 1) * (-f)) - Float.intBitsToFloat(1073741824));
            }
            for (Map.Entry<String, Integer> entry : eSPSearchHelper4.getItemCountMap().entrySet()) {
                String string1752 = EspTargetFilter.formatItemCountLabel(entry.getKey(), entry.getValue().intValue());
                RenderUtil.INSTANCE.drawTextBillboardOffset(matrixStackEvent.getDrawContext(), string1752, center, 0.0f, 0.0f, (-ClientTextRenderer.INSTANCE.getStringWidth(string1752)) / Float.intBitsToFloat(1073741824), f2, d, this.itemsText.getValue(), true);
                f2 -= f;
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        PlaySoundS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof PlaySoundS2CPacket ? (PlaySoundS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof PlaySoundS2CPacket) {
            PlaySoundS2CPacket playSoundS2CPacket = packet904;
            if (((SoundEvent) playSoundS2CPacket.getSound().value()).equals(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT)) {
                this.map.put(new Vec3d(playSoundS2CPacket.getX(), playSoundS2CPacket.getY(), playSoundS2CPacket.getZ()), Long.valueOf(System.currentTimeMillis()));
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.map.entrySet().removeIf(entry -> {
            return System.currentTimeMillis() - ((Long) entry.getValue()).longValue() > 3500;
        });
    }

    public void drawGroupedItemBg(MatrixStack matrixStack, EspTargetFilter eSPSearchHelper4, double d, float f) {
        RenderUtil.INSTANCE.drawBillboard(matrixStack, eSPSearchHelper4.getMergedBox().getCenter(), 0.0f, 0.0f, eSPSearchHelper4.getMaxLabelWidth() + Float.intBitsToFloat(1065353216), f, d, this.background.getValue());
    }

    public void renderSpawnHighlights(MatrixStackEvent matrixStackEvent) {
        float intBitsToFloat = Float.intBitsToFloat(1061158912);
        Vec3d pos = minecraftClient.getEntityRenderDispatcher().camera.getPos();
        MatrixStack matrixStack472 = matrixStackEvent.getMatrixStack();
        BufferBuilder begin = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        ChunkLightingView chunkLightingView = minecraftClient.world.getLightingProvider().get(LightType.BLOCK);
        for (BlockPos blockPos : WorldUtils.getBlocksInRadius(minecraftClient.player.getEyePos(), Float.intBitsToFloat(1094713344), true)) {
            BlockState blockState = minecraftClient.world.getBlockState(blockPos);
            if (WorldUtils.isAir(blockPos.up())) {
                if (chunkLightingView.getLightLevel(blockPos.up()) <= 7) {
                    boolean matchesKey = minecraftClient.world.getBiome(blockPos).matchesKey(BiomeKeys.MUSHROOM_FIELDS);
                    if (blockState.allowsSpawning(minecraftClient.world, blockPos, EntityType.ZOMBIE) && !matchesKey) {
                        double distanceTo = minecraftClient.gameRenderer.getCamera().getPos().distanceTo(blockPos.toCenterPos());
                        float intBitsToFloat2 = Float.intBitsToFloat(1065353216) - ((float) MathHelper.clamp((distanceTo - Double.longBitsToDouble(4620693217682128896L)) / Double.longBitsToDouble(4611686018427387904L), 0.0d, Double.longBitsToDouble(4607182418800017408L)));
                        int hashCode = Color.red.hashCode();
                        if (distanceTo >= Double.longBitsToDouble(4620693217682128896L)) {
                            hashCode = ColorUtils.toIntWithAlphaPercent(Color.red, intBitsToFloat2);
                        }
                        matrixStack472.push();
                        matrixStack472.translate(blockPos.getX() - pos.x, (blockPos.getY() - pos.y) + Double.longBitsToDouble(4607272490792564818L), blockPos.getZ() - pos.z);
                        Matrix4f positionMatrix = matrixStack472.peek().getPositionMatrix();
                        begin.vertex(positionMatrix, Float.intBitsToFloat(1065353216) - intBitsToFloat, 0.0f, Float.intBitsToFloat(1065353216) - intBitsToFloat).color(hashCode);
                        begin.vertex(positionMatrix, intBitsToFloat, 0.0f, intBitsToFloat).color(hashCode);
                        begin.vertex(positionMatrix, intBitsToFloat, 0.0f, Float.intBitsToFloat(1065353216) - intBitsToFloat).color(hashCode);
                        begin.vertex(positionMatrix, Float.intBitsToFloat(1065353216) - intBitsToFloat, 0.0f, intBitsToFloat).color(hashCode);
                        matrixStack472.pop();
                    }
                }
            }
        }
        BuiltBuffer endNullable = begin.endNullable();
        if (endNullable == null) {
            return;
        }
        BufferRenderer.drawWithGlobalProgram(endNullable);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
    }

    public boolean is1918() {
        return this.flag;
    }
}
