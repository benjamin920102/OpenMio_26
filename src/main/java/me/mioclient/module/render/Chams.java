package me.mioclient.module.render;

import me.mioclient.event.events.entity.EntityModelRenderEvent;
import me.mioclient.event.events.entity.TotemPopEvent;

import me.mioclient.event.events.entity.EntityRenderEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.ClonedPlayerEntity;

import me.mioclient.mode.render.ChamsMode;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.module.render.chams.ChamsMode_2;
import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.render.mesh.TrajectoriesVertexConsumer;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.chams.ChamsColorApplier;
import me.mioclient.module.render.esp.EspBoxRenderer;
import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.render.RenderUtil;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.EntityUtil;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.mioclient.mode.render.ChamsMode;
import me.mioclient.module.render.chams.ChamsMode_2;
import me.mioclient.event.events.entity.EntityModelRenderEvent;
import me.mioclient.event.events.entity.EntityRenderEvent;
import me.mioclient.render.RenderConstants;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.player.ClonedPlayerEntity;
import me.mioclient.event.events.entity.TotemPopEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.ducks.DuckEndCrystalEntity;
import me.mioclient.render.mesh.TrajectoriesVertexConsumer;
import me.mioclient.ducks.DuckSimpleOption;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.player.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Chams.class */
public class Chams extends Module {
    public Setting<Integer> range = add(new NumberSetting<>("Range", 60, 1, 300).setSuffix("m"));
    public Setting<Boolean> fade = add(new BooleanSetting("Fade", false).setVisible());
    public Setting<Integer> fadeRadius = add(new NumberSetting<>("FadeRadius", 75, 1, 100).setSuffix("%").requiresSettings(this.fade));
    public Setting<Float> lineWidth = add(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 5.0f));
    public Setting<Boolean> extraLayer = add(new BooleanSetting("ExtraLayer", true));
    public Setting<Boolean> model = add(new BooleanSetting("Model", true).setVisible());
    public Setting<Boolean> xqz = add(new BooleanSetting("XQZ", false).requiresSettings(this.model));
    public Setting<Integer> opacity = add(new NumberSetting<>("Opacity", 100, 0, 100).setSuffix("%").requiresSettings(this.model, this.xqz));
    public Setting<Boolean> shine2 = add(new BooleanSetting("Shine", false).setVisible().requiresSettings(this.model));
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 0.5f, 0.0f, 2.0f).setHoleSnapConfig("Static", HoleSnapMode.MIN).requiresSettings(this.model, this.shine2));
    public Setting<Float> strength = add(new NumberSetting<>("Strength", 1.0f, 0.0f, 1.0f).requiresSettings(this.model, this.shine2));
    public Setting<Integer> progress = add(new NumberSetting<>("Progress", 0, 0, 100).setSuffix("%"));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", true).setDependent());
    public Setting<ChamsMode> animals = add(new EnumSetting<>("Animals", ChamsMode.OFF).requiresSettings(this.targets));
    public Setting<ChamsMode> hostiles = add(new EnumSetting<>("Hostiles", ChamsMode.OFF).requiresSettings(this.targets));
    public Setting<ChamsMode> players = add(new EnumSetting<>("Players", ChamsMode.OFF).requiresSettings(this.targets));
    public Setting<ChamsMode> self = add(new EnumSetting<>("Self", ChamsMode.OFF).requiresSettings(this.targets));
    public Setting<ChamsMode> crystals = add(new EnumSetting<>("Crystals", ChamsMode.OFF).requiresSettings(this.targets));
    public Setting<Boolean> pop = add(new BooleanSetting("Pop", false).setVisible().requiresSettings(this.targets));
    public Setting<Boolean> death = add(new BooleanSetting("Death", false).requiresSettings(this.targets, this.pop));
    public Setting<ChamsMode_2> wireframe = add(new EnumSetting<>("Wireframe", ChamsMode_2.BOTH).requiresSettings(this.targets, this.pop));
    public Setting<Boolean> animate = add(new BooleanSetting("Animate", true).requiresSettings(this.targets, this.pop));
    public Setting<Boolean> boost = add(new BooleanSetting("Boost", false).requiresSettings(this.targets, this.pop, this.animate));
    public Setting<Float> time = add(new NumberSetting<>("Time", 1.0f, 0.0f, 5.0f).requiresSettings(this.targets, this.pop));
    public Setting<Float> motion = add(new NumberSetting<>("Motion", 0.0f, -8.0f, 8.0f).requiresSettings(this.targets, this.pop));
    public Setting<Boolean> colors = add(new BooleanSetting("Colors", false).setDependent());
    public Setting<Boolean> friends = add(new BooleanSetting("Friends", false).requiresSettings(this.colors));
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(935172607, true)).setThemeSync(true).requiresSettings(this.colors));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(-4351489, true)).setThemeSync(true).requiresSettings(this.colors));
    public Setting<Color> shine = add(new ColorSetting("Shine", new Color(2076023295, true)).requiresSettings(this.colors));
    public Setting<Color> popFill = add(new ColorSetting("PopFill", new Color(935172607, true)).setThemeSync(true).requiresSettings(this.colors));
    public Setting<Color> popLine = add(new ColorSetting("PopLine", new Color(12425727, true)).setThemeSync(true).requiresSettings(this.colors));
    public final TrajectoriesVertexConsumer trajectoriesVertexConsumer;
    public final Map<ClonedPlayerEntity, Long> map;
    public boolean flag3;
    public static final Identifier identifier = Identifier.of("mio-mount", "textures/shine.png");
    public static AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public static NoRender norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class);
    public static Animations animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class);
    public static final Freecam freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class);
    public static boolean flag = false;
    public static boolean flag2 = false;

    public Chams() {
        super("Chams", "Wallhack on entities.", Category.RENDER, new String[0]);
        this.trajectoriesVertexConsumer = TrajectoriesVertexConsumer.createDefault();
        this.map = new HashMap();
        this.wireframe.setConfigName("PopMode");
        this.shine.setConfigName("ShineColor");
        this.speed.setHoleSnapConfig("Static", HoleSnapMode.MIN);
    }

    @Listen(get219= RenderConstants.num)
    public void onRenderWorldPre(MatrixStackEvent.Inner inner) {
        if (this.shine2.getValue().booleanValue()) {
            this.trajectoriesVertexConsumer.beginBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        }
        float intBitsToFloat = ((Integer) minecraftClient.options.getFov().getValue()).intValue() > 115 && is2047() ? Float.intBitsToFloat(1075000115) : Float.intBitsToFloat(1065353216);
        EspBoxRenderer.disableEntityCulling();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-900358272) * intBitsToFloat);
        for (Entity entity : minecraftClient.world.getEntities()) {
            if (shouldRenderEntity(entity)) {
                if (this.model.getValue().booleanValue() && this.xqz.getValue().booleanValue() && !norender.isEntityHidden(entity) && this.opacity.getValue().intValue() > 0) {
                    this.flag3 = true;
                    RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), this.opacity.getValue().intValue() / Float.intBitsToFloat(1120403456));
                    RenderUtil.INSTANCE.renderEntity(entity, inner.getTickDelta(), inner.getMatrixStack(), minecraftClient.getBufferBuilders().getEntityVertexConsumers());
                    this.flag3 = false;
                }
                if (this.shine2.getValue().booleanValue()) {
                    flag = true;
                    RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
                    inner.getMatrixStack().push();
                    RenderUtil.applyCameraRotation(inner.getMatrixStack());
                    RenderUtil.INSTANCE.renderEntity(entity, inner.getTickDelta(), inner.getMatrixStack(), minecraftClient.getBufferBuilders().getEntityVertexConsumers());
                    inner.getMatrixStack().pop();
                    flag = false;
                }
            }
        }
        if (this.model.getValue().booleanValue() && this.xqz.getValue().booleanValue()) {
            RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), this.opacity.getValue().intValue() / Float.intBitsToFloat(1120403456));
            minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();
            RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
        }
        RenderSystem.disablePolygonOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1247125376) * intBitsToFloat);
        EspBoxRenderer.restoreEntityCulling();
    }

    @Listen(get219= -1000)
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        ChamsRenderDispatcher.setOutlineWidth(this.lineWidth.getValue().floatValue());
        for (Entity entity : minecraftClient.world.getEntities()) {
            if (shouldRenderEntity(entity) && getChamsModeForEntity(entity).getValue().is594()) {
                getChamsModeForEntity(entity).getValue().applyChamsRender(this, entity, inner_3.getMatrixStack());
            }
        }
        if (this.pop.getValue().booleanValue()) {
            synchronized (this.map) {
                this.map.entrySet().removeIf(entry -> {
                    return ((float) ((Long) entry.getValue()).longValue()) + (this.time.getValue().floatValue() * Float.intBitsToFloat(1148846080)) < ((float) System.currentTimeMillis());
                });
                this.map.forEach((otherClientPlayerEntity, l) -> {
                    float intBitsToFloat = Float.intBitsToFloat(1065353216) - MathHelper.clamp(((float) (System.currentTimeMillis() - l.longValue())) / (this.time.getValue().floatValue() * Float.intBitsToFloat(1148846080)), 0.0f, Float.intBitsToFloat(1065353216));
                    otherClientPlayerEntity.setPosition(otherClientPlayerEntity.getX(), otherClientPlayerEntity.getLastPosition().y + ((Float.intBitsToFloat(1065353216) - intBitsToFloat) * this.motion.getValue().floatValue()), otherClientPlayerEntity.getZ());
                    Color[] colorArray593 = this.wireframe.getValue().getChamsColors(this);
                    ChamsRenderDispatcher.setChamsColors(ColorUtils.withAlphaPercent(colorArray593[0], (colorArray593[0].getAlpha() / Float.intBitsToFloat(1132396544)) * intBitsToFloat), ColorUtils.withAlphaPercent(colorArray593[1], (colorArray593[1].getAlpha() / Float.intBitsToFloat(1132396544)) * intBitsToFloat));
                    ChamsRenderDispatcher.renderEntityChams(inner_3.getMatrixStack(), otherClientPlayerEntity);
                });
            }
        }
        if (this.xqz.getValue().booleanValue()) {
            minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();
        }
        if (this.shine2.getValue().booleanValue()) {
            double doubleValue = ((Double) minecraftClient.options.getGlintSpeed().getValue()).doubleValue();
            double doubleValue2 = ((Double) minecraftClient.options.getGlintStrength().getValue()).doubleValue();
            ((DuckSimpleOption) (Object) minecraftClient.options.getGlintSpeed()).forceSetValue(Double.valueOf(this.speed.getValue().doubleValue()));
            minecraftClient.options.getGlintStrength().setValue(Double.valueOf(this.strength.getValue().doubleValue()));
            Color value = this.shine.getValue();
            RenderSystem.enableBlend();
            flag2 = true;
            RenderLayer.getArmorEntityGlint().startDrawing();
            MinecraftClient.getInstance().getTextureManager().getTexture(identifier).setFilter(true, false);
            RenderSystem.setShaderTexture(0, identifier);
            RenderSystem.setShaderColor(value.getRed() / Float.intBitsToFloat(1132396544), value.getGreen() / Float.intBitsToFloat(1132396544), value.getBlue() / Float.intBitsToFloat(1132396544), Float.intBitsToFloat(1065353216));
            RenderSystem.disableDepthTest();
            this.trajectoriesVertexConsumer.drawBuffer();
            RenderSystem.enableDepthTest();
            RenderLayer.getArmorEntityGlint().endDrawing();
            RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
            flag2 = false;
            minecraftClient.options.getGlintSpeed().setValue(Double.valueOf(doubleValue));
            minecraftClient.options.getGlintStrength().setValue(Double.valueOf(doubleValue2));
        }
    }

    @Listen
    public void onEntityRenderPre(EntityRenderEvent.Inner inner) {
        if (ShaderPassRegistry.flag || this.model.getValue().booleanValue() || !shouldRenderEntity(inner.getEntity()) || this.flag3) {
            return;
        }
        inner.cancel();
    }

    @Listen
    public void onEntityModelRender(EntityModelRenderEvent entityEvent) {
        if (shouldRenderEntity(entityEvent.getEntity())) {
            entityEvent.setLight(15728880);
        }
    }

    @Listen
    public void onTotemPop(TotemPopEvent playerEntityEvent) {
        PlayerEntity playerEntity1890;
        if (this.pop.getValue().booleanValue()) {
            if ((playerEntityEvent.getType() != TotemPopEvent.LogoutSpotsHelperMode.DEATH || this.death.getValue().booleanValue()) && (playerEntity1890 = playerEntityEvent.getPlayer()) != minecraftClient.player) {
                ClonedPlayerEntity otherClientPlayerEntity = new ClonedPlayerEntity(minecraftClient.world);
                otherClientPlayerEntity.copyFromPlayer(playerEntity1890);
                otherClientPlayerEntity.limbAnimator.pos *= Float.intBitsToFloat(1073741824);
                if (!this.animate.getValue().booleanValue()) {
                    otherClientPlayerEntity.limbAnimator.pos = 0.0f;
                    otherClientPlayerEntity.limbAnimator.speed = 0.0f;
                    otherClientPlayerEntity.limbAnimator.prevSpeed = 0.0f;
                } else if (this.boost.getValue().booleanValue()) {
                    otherClientPlayerEntity.setChamsVisible(true);
                    otherClientPlayerEntity.handSwingProgress = 0.0f;
                    otherClientPlayerEntity.limbAnimator.speed = (float) ((Math.random() * Double.longBitsToDouble(4605380979056443392L)) + Double.longBitsToDouble(4596373779801702400L));
                    otherClientPlayerEntity.limbAnimator.pos = 0.0f;
                }
                synchronized (this.map) {
                    this.map.putIfAbsent(otherClientPlayerEntity, Long.valueOf(System.currentTimeMillis()));
                }
            }
        }
    }

    public boolean shouldRenderEntity(Entity entity) {
        if (LogoutSpots.is1291()) {
            return false;
        }
        try {
            boolean z = !RotationManager.isInFrustum(entity.getBoundingBox()) || minecraftClient.gameRenderer.getCamera().getPos().distanceTo(entity.getPos()) > this.range.getValue().intValue();
            if (z || EntityUtil.isFirstPersonView(entity)) {
                return false;
            }
            if (!(entity instanceof EndCrystalEntity)) {
                if (entity instanceof PlayerEntity) {
                    return entity == minecraftClient.player ? this.self.getValue().is594() && (freecam.isToggled() || minecraftClient.options.getPerspective() != Perspective.FIRST_PERSON) : this.players.getValue().is594();
                }
                return ((entity instanceof PassiveEntity) && this.animals.getValue().is594()) || ((entity instanceof Monster) && this.hostiles.getValue().is594());
            }
            DuckEndCrystalEntity spawnTimeHelper_2 = (DuckEndCrystalEntity)((EndCrystalEntity) entity);
            if (System.currentTimeMillis() - spawnTimeHelper_2.getSpawnTime() > Math.max(ClientServices.holeSnapRenderHelper.getPing(), 50) && ((EndCrystalEntity) spawnTimeHelper_2).age < 10 && autoCrystal.isToggled() && autoCrystal.inhibit.getValue().booleanValue() && spawnTimeHelper_2.isMioAttacked()) {
                return false;
            }
            return this.crystals.getValue().is594();
        } catch (Exception e) {
            return false;
        }
    }

    public float get2044(Entity entity) {
        return (float) (Double.longBitsToDouble(4607182418800017408L) - MathHelper.clamp(((minecraftClient.gameRenderer.getCamera().getPos().distanceTo(entity.getPos()) / this.range.getValue().intValue()) - (this.fadeRadius.getValue().intValue() / Float.intBitsToFloat(1120403456))) / (Float.intBitsToFloat(1065353216) - (this.fadeRadius.getValue().intValue() / Float.intBitsToFloat(1120403456))), 0.0d, Double.longBitsToDouble(4607182418800017408L)));
    }

    public boolean shouldRenderChams(Entity entity) {
        if (isToggled() && this.xqz.getValue().booleanValue()) {
            return shouldRenderEntity(entity);
        }
        return false;
    }

    public boolean shouldShineEntity(Entity entity) {
        return isToggled() && (getChamsModeForEntity(entity).getValue().is594() || entity == minecraftClient.player) && this.shine2.getValue().booleanValue() && flag;
    }

    public boolean is2047() {
        float intBitsToFloat = Float.intBitsToFloat(1050253722);
        Vec3d eyePos = minecraftClient.player.getEyePos();
        BlockPos ofFloored = BlockPos.ofFloored((Position) eyePos);
        if (!minecraftClient.world.isBlockSpaceEmpty(minecraftClient.player, Box.of(eyePos, intBitsToFloat, intBitsToFloat, intBitsToFloat))) {
            if (minecraftClient.world.getBlockState(ofFloored).getCollisionShape(minecraftClient.world, ofFloored).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean is2048() {
        return animations.isStaticAnimationEnabled();
    }

    public TrajectoriesVertexConsumer getShineVertexConsumer() {
        return this.trajectoriesVertexConsumer;
    }

    public Setting<? extends ChamsColorApplier> getChamsModeForEntity(Entity entity) {
        return entity == minecraftClient.player ? this.self : entity instanceof EndCrystalEntity ? this.crystals : entity instanceof PlayerEntity ? this.players : entity instanceof PassiveEntity ? this.animals : this.hostiles;
    }
}
