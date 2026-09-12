package me.mioclient.module.render;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shader.OutlineShaderPass;
import me.mioclient.render.shader.ShaderFramebufferSwapper;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shader.OutlineShaderPass;
import me.mioclient.render.shader.ShaderFramebufferSwapper;
import me.mioclient.render.shader.ShaderPassRegistry;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.EntityUtil;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Color;
import java.util.Comparator;
import java.util.function.Supplier;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.module.player.Freecam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Shader.class */
public class Shader extends Module {
    public static Freecam freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class);
    public static NoRender norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class);
    public Setting<MixinHeldItemRendererMode> shader = add(new EnumSetting<>("Shader", MixinHeldItemRendererMode.SOLID));
    public Setting<Integer> range2 = add(new NumberSetting<>("Range", 60, 1, 300).setSuffix("m"));
    public Setting<Integer> lineWidth = add(new NumberSetting<>("LineWidth", 1, 0, 10));
    public Setting<Boolean> fastLines = add(new BooleanSetting("FastLines", false, v -> this.lineWidth.getValue().intValue() > 2));
    public Setting<Float> glow = add(new NumberSetting<>("Glow", 1.0f, 0.0f, 5.0f));
    public Setting<Integer> quality = add(new NumberSetting<>("Quality", 1, 1, 6, v -> this.shader.getValue() == Shader.MixinHeldItemRendererMode.BLOOM));
    public Setting<Float> step = add(new NumberSetting<>("Step", 0.5f, 0.1f, 2.0f, v -> this.shader.getValue() == Shader.MixinHeldItemRendererMode.GRADIENT));
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 0.1f, 5.0f, v -> this.shader.getValue() == Shader.MixinHeldItemRendererMode.GRADIENT));
    public Setting<Float> fillSpeed = add(new NumberSetting<>("FillSpeed", 1.0f, 0.0f, 5.0f, v -> this.shader.getValue() == Shader.MixinHeldItemRendererMode.RAINBOW));
    public Setting<Float> outlineSpeed = add(new NumberSetting<>("OutlineSpeed", 1.0f, 0.0f, 5.0f, v -> this.shader.getValue() == Shader.MixinHeldItemRendererMode.RAINBOW));
    public Setting<Float> fillStrength = add(new NumberSetting<>("FillStrength", 1.0f, 0.0f, 5.0f, v -> this.shader.getValue() == Shader.MixinHeldItemRendererMode.RAINBOW));
    public Setting<Float> outlineStrength = add(new NumberSetting<>("OutlineStrength", 1.0f, 0.0f, 5.0f, v -> this.shader.getValue() == Shader.MixinHeldItemRendererMode.RAINBOW));
    public Setting<Boolean> image = add(new BooleanSetting("Image", false).setVisible());
    public Setting<Float> overlayAlpha = add(new NumberSetting<>("OverlayAlpha", 1.0f, 0.0f, 1.0f).requiresSettings(this.image));
    public Setting<Boolean> decorator = add(new BooleanSetting("Decorator", false).setDependent());
    public Setting<Mode> type = add(new EnumSetting<>("Type", Mode.NONE).requiresSettings(this.decorator));
    public Setting<Integer> radius = add(new NumberSetting<>("Radius", 5, 1, 20).requiresSettings(this.decorator));
    public Setting<Float> alpha = add(new NumberSetting<>("Alpha", 1.0f, 0.0f, 1.0f).requiresSettings(this.decorator));
    public Setting<Boolean> colors = add(new BooleanSetting("Colors", false).setDependent());
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(2025691647, true)).requiresSettings(this.colors));
    public Setting<Color> fillSecond = add(new ColorSetting("FillSecond", new Color(1936677274, true)));
    public Setting<Color> outline2 = add(new ColorSetting("Outline", new Color(-4351489, true)).requiresSettings(this.colors));
    public Setting<Color> outlineSecond = add(new ColorSetting("OutlineSecond", new Color(-8625746, true)));
    public Setting<Boolean> targets = add(new BooleanSetting("Targets", true).setDependent());
    public Setting<Boolean> hands = add(new BooleanSetting("Hands", true).setVisible().requiresSettings(this.targets));
    public Setting<Boolean> fill2 = add(new BooleanSetting("Fill", true).requiresSettings(this.targets, this.hands));
    public Setting<Boolean> outline = add(new BooleanSetting("Outline", true).requiresSettings(this.targets, this.hands));
    public Setting<Boolean> crystals = add(new BooleanSetting("Crystals", false).setVisible().requiresSettings(this.targets));
    public Setting<Integer> range = add(new NumberSetting<>("Range", 60, 1, 300).setSuffix("m").requiresSettings(this.targets, this.crystals));
    public Setting<Boolean> items = add(new BooleanSetting("Items", false).setVisible().requiresSettings(this.targets));
    public Setting<Integer> range3 = add(new NumberSetting<>("Range", 60, 1, 300).setSuffix("m").requiresSettings(this.targets, this.items));
    public Setting<Boolean> animals = add(new BooleanSetting("Animals", false).requiresSettings(this.targets));
    public Setting<Boolean> hostiles = add(new BooleanSetting("Hostiles", false).requiresSettings(this.targets));
    public Setting<Boolean> players = add(new BooleanSetting("Players", false).requiresSettings(this.targets));
    public Setting<Boolean> self = add(new BooleanSetting("Self", false).requiresSettings(this.targets, this.players));
    public Setting<Boolean> exp = add(new BooleanSetting("Exp", false).requiresSettings(this.targets));
    public Setting<Boolean> pearls = add(new BooleanSetting("Pearls", false).requiresSettings(this.targets));
    public Setting<Boolean> minecarts = add(new BooleanSetting("Minecarts", false).requiresSettings(this.targets));
    public Setting<Boolean> dead = add(new BooleanSetting("Dead", false).requiresSettings(this.targets));
    public final ObjectArrayList<Entity> objectArrayList;
    public float val;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Shader$MixinHeldItemRendererMode.class */
    public enum MixinHeldItemRendererMode implements NamedValue {
        SOLID("Solid", () -> {
            return ShaderPassRegistry.shaderFramebufferHelper;
        }),
        RAINBOW("Rainbow", () -> {
            return ShaderPassRegistry.shaderFramebufferHelper2;
        }),
        GRADIENT("Gradient", () -> {
            return ShaderPassRegistry.shaderFramebufferHelper3;
        }),
        BLOOM("Bloom", () -> {
            return ShaderPassRegistry.shaderFramebufferHelper4;
        });

        public final String name;
        public final Supplier<OutlineShaderPass> supplier;

        MixinHeldItemRendererMode(String str, Supplier supplier) {
            this.name = str;
            this.supplier = supplier;
        }

        public OutlineShaderPass getShaderPass() {
            return this.supplier.get();
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/Shader$Mode.class */
    public enum Mode implements NamedValue {
        NONE("None"),
        DOTS("Dots"),
        GRID("Grid");

        public final String name;

        Mode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Shader() {
        super("Shader", "Advanced ESP using shaders.", Category.RENDER, new String[0]);
        this.objectArrayList = new ObjectArrayList<>();
        this.val = Float.intBitsToFloat(1065353216);
        this.radius.setConfigName("DecoratorRadius");
        this.alpha.setConfigName("DecoratorAlpha");
        this.fill2.setConfigName("HandsFill");
        this.outline.setConfigName("HandsOutline");
        this.range3.setConfigName("ItemRange");
        this.range.setConfigName("CrystalRange");
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return ClientTextRenderer.capitalize(this.shader.getValue());
    }

    @Listen(get219= -99999999)
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        OutlineShaderPass shaderFramebufferHelper21 = this.shader.getValue().getShaderPass();
        this.objectArrayList.clear();
        for (Entity entity : minecraftClient.world.getEntities()) {
            if (shouldRenderEntity(entity)) {
                this.objectArrayList.add(entity);
            }
        }
        this.objectArrayList.sort(Comparator.comparing(entity2 -> {
            return Double.valueOf(minecraftClient.gameRenderer.getCamera().getPos().squaredDistanceTo(entity2.getPos()));
        }));
        ShaderFramebufferSwapper.swapAndRender(shaderFramebufferHelper21, false, () -> {
            inner_3.getMatrixStack().push();
            this.objectArrayList.forEach(entity3 -> {
                renderEntityToOutline(inner_3, entity3, shaderFramebufferHelper21);
            });
            inner_3.getMatrixStack().pop();
        });
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.val = getClusterAlpha((Entity) this.objectArrayList.stream().min(Comparator.comparing(this::getClusterAlpha)).orElse(null));
    }

    public void renderEntityToOutline(MatrixStackEvent matrixStackEvent, Entity entity, OutlineShaderPass shaderFramebufferHelper) {
        if (entity.isAlive() || this.dead.getValue().booleanValue()) {
            Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
            float f = matrixStackEvent.getTickDelta();
            double lerp = MathHelper.lerp(f, entity.lastRenderX, entity.getX()) - pos.x;
            double lerp2 = MathHelper.lerp(f, entity.lastRenderY, entity.getY()) - pos.y;
            double lerp3 = MathHelper.lerp(f, entity.lastRenderZ, entity.getZ()) - pos.z;
            float lerp4 = MathHelper.lerp(f, entity.prevYaw, entity.getYaw());
            shaderFramebufferHelper.outlineVertexConsumerProvider.setColor(255, 255, 255, 255);
            minecraftClient.getEntityRenderDispatcher().render(entity, lerp, lerp2, lerp3, lerp4, f, matrixStackEvent.getMatrixStack(), shaderFramebufferHelper.outlineVertexConsumerProvider, 0);
        }
    }

    public float getClusterAlpha(Entity entity) {
        if (entity != null && norender.isToggled() && norender.noCluster.getValue().booleanValue()) {
            double distanceTo = minecraftClient.player.getPos().distanceTo(entity.getPos());
            float width = entity.getWidth();
            if (distanceTo <= width && entity != minecraftClient.player) {
                float f = (float) (distanceTo / width);
                float intValue = norender.clusterAlpha.getValue().intValue() / Float.intBitsToFloat(1132396544);
                return intValue + (f * (Float.intBitsToFloat(1065353216) - intValue));
            }
        }
        return Float.intBitsToFloat(1065353216);
    }

    public Color applyClusterAlpha(Color color) {
        float f = this.val;
        if (ShaderPassRegistry.flag2) {
            f = Float.intBitsToFloat(1065353216);
        }
        return ColorUtils.withAlpha(color, (int) (f * color.getAlpha()));
    }

    public float getOverlayAlpha() {
        return ShaderPassRegistry.flag2 ? this.overlayAlpha.getValue().floatValue() : this.overlayAlpha.getValue().floatValue() * this.val;
    }

    public boolean shouldRenderEntity(Entity entity) {
        float intValue = this.range2.getValue().intValue();
        if (entity instanceof EndCrystalEntity) {
            intValue = this.range.getValue().intValue();
        }
        if (entity instanceof ItemEntity) {
            intValue = this.range3.getValue().intValue();
        }
        boolean z = !RotationManager.isInFrustum(entity.getBoundingBox()) || minecraftClient.player.distanceTo(entity) > intValue;
        if (((entity instanceof PlayerEntity) && ((PlayerEntity) entity).isDead()) || z || EntityUtil.isFirstPersonView(entity)) {
            return false;
        }
        boolean z2 = minecraftClient.gameRenderer.getCamera().isThirdPerson() || freecam.isToggled();
        if (!(entity instanceof PlayerEntity)) {
            return ((entity instanceof ItemEntity) && this.items.getValue().booleanValue()) || ((entity instanceof ExperienceBottleEntity) && this.exp.getValue().booleanValue()) || ((((entity instanceof PassiveEntity) || (entity instanceof FishEntity) || (entity instanceof SquidEntity)) && this.animals.getValue().booleanValue()) || (((entity instanceof Monster) && this.hostiles.getValue().booleanValue()) || (((entity instanceof EnderPearlEntity) && this.pearls.getValue().booleanValue()) || (((entity instanceof EndCrystalEntity) && this.crystals.getValue().booleanValue()) || ((entity instanceof AbstractMinecartEntity) && this.minecarts.getValue().booleanValue())))));
        }
        if (z2 && this.self.getValue().booleanValue() && entity == minecraftClient.player) {
            return true;
        }
        return this.players.getValue().booleanValue() && entity != minecraftClient.player;
    }

    public boolean isHandFillDisabled() {
        return ShaderPassRegistry.flag2 && !this.fill2.getValue().booleanValue();
    }

    public boolean isHandOutlineDisabled() {
        return ShaderPassRegistry.flag2 && !this.outline.getValue().booleanValue();
    }
}
