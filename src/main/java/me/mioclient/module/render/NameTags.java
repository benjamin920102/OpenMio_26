package me.mioclient.module.render;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.NamedValue;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.EnchantmentHelper;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.module.render.nametags.MaxEnchantmentFilter;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.RenderUtil;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.MathUtils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.ducks.DuckPlayerEntity;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.render.RenderLabelEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.inventory.EnchantmentHelper;
import me.mioclient.module.Module;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.player.Freecam;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/NameTags.class */
public class NameTags extends Module {
    public static final Identifier identifier = Identifier.of("mio", "textures/mio.png");
    public static Animations animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class);
    public static Freecam freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class);
    public static Fonts fonts = (Fonts) ClientServices.moduleInjector.getModule(Fonts.class);
    public static boolean flag = false;
    public Setting<Integer> range = add(new NumberSetting<>("Range", 60, 1, 300).setSuffix("m"));
    public Setting<Float> scale = add(new NumberSetting<>("Scale", 1.2f, 0.1f, 3.0f));
    public Setting<Boolean> dead = add(new BooleanSetting("Dead", true));
    public Setting<Boolean> info = add(new BooleanSetting("Info", true).setDependent());
    public Setting<Boolean> name = add(new BooleanSetting("Name", true, v -> this.info.isShown()));
    public Setting<Boolean> ping = add(new BooleanSetting("Ping", true, v -> this.info.isShown()));
    public Setting<Boolean> health = add(new BooleanSetting("Health", true, v -> this.info.isShown()));
    public Setting<Boolean> healthColor = add(new BooleanSetting("HealthColor", true, v -> this.info.isShown() && this.health.getValue().booleanValue()));
    public Setting<Boolean> gamemode = add(new BooleanSetting("Gamemode", true, v -> this.info.isShown()));
    public Setting<Boolean> itemName = add(new BooleanSetting("ItemName", true).requiresSettings(this.info));
    public Setting<Boolean> armor = add(new BooleanSetting("Armor", true, v -> this.info.isShown()).setVisible());
    public Setting<NameTagsMode> durability = add(new EnumSetting<>("Durability", NameTagsMode.SHOW).requiresSettings(this.info, this.armor));
    public Setting<Boolean> items = add(new BooleanSetting("Items", true, v -> this.info.isShown()));
    public Setting<Boolean> showEating = add(new BooleanSetting("ShowEating", false).requiresSettings(this.info, this.items));
    public Setting<Boolean> enchants = add(new BooleanSetting("Enchants", true, v -> this.info.isShown()));
    public Setting<Boolean> hideMax = add(new BooleanSetting("HideMax", false).requiresSettings(this.info, this.enchants));
    public Setting<Boolean> totemPops = add(new BooleanSetting("TotemPops", false, v -> this.info.isShown()));
    public Setting<Boolean> mioCheck = add(new BooleanSetting("MioCheck", false, v -> this.info.isShown()));
    public Setting<Boolean> colors = add(new BooleanSetting("Colors", true).setDependent());
    public Setting<Boolean> friend = add(new BooleanSetting("Friend", true).requiresSettings(this.colors));
    public Setting<Boolean> enemy = add(new BooleanSetting("Enemy", true).requiresSettings(this.colors));
    public Setting<Color> fill = add(new ColorSetting("Fill", new Color(657930, true), v -> this.colors.isShown()));
    public Setting<Color> outline = add(new ColorSetting("Outline", new Color(12425727, true), v -> this.colors.isShown()).setThemeSync(true));
    public Setting<Boolean> smart = add(new BooleanSetting("Smart", false, v -> this.colors.isShown()).setVisible());
    public Setting<Color> hole = add(new ColorSetting("Hole", new Color(-937522432, true), v -> this.colors.isShown() && this.smart.isShown()).setThemeSync(true));
    public Setting<Color> phase = add(new ColorSetting("Phase", new Color(-922775296, true), v -> this.colors.isShown() && this.smart.isShown()).setThemeSync(true));
    public Setting<Color> text = add(new ColorSetting("Text", new Color(-1, true), v -> this.colors.isShown()));
    public Setting<Color> invisibles = add(new ColorSetting("Invisibles", new Color(-65536, true), v -> this.colors.isShown()));
    public Setting<Color> sneak = add(new ColorSetting("Sneak", new Color(-28416, true), v -> this.colors.isShown()));
    public Setting<Color> eating = add(new ColorSetting("Eating", new Color(-16711936, true), v -> this.colors.isShown()));
    public final Color color;
    public final List<PlayerEntity> list;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;
    public Vector3f[] vector3fArr;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/NameTags$NameTagsMode.class */
    public enum NameTagsMode implements NamedValue {
        SHOW("Show"),
        ONLY("Only"),
        HIDE("Hide");

        public final String name;

        NameTagsMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/NameTags$NameTagsMode_2.class */
    public static enum NameTagsMode_2 {
        nameTagsMode_2 {
            @Override
            public float getAlignmentOffset(float f) {
                return (-f) * 0.5f;
            }
        },
        nameTagsMode_22 {
            @Override
            public float getAlignmentOffset(float f) {
                return 0.0f;
            }
        },
        nameTagsMode_23 {
            @Override
            public float getAlignmentOffset(float f) {
                return -f;
            }
        };

        public float getAlignmentOffset(float f) {
            return 0.0f;
        }

        public float getTextOffsetX(String str, boolean z) {
            return getAlignmentOffset(z ? ClientAccessor.minecraftClient.textRenderer.getWidth(str) : ClientTextRenderer.INSTANCE.getStringWidth(str));
        }
    }

    public NameTags() {
        super("NameTags", "Draws advanced nametags for players.", Category.RENDER, new String[0]);
        this.color = new Color(153, 69, 58);
        this.list = new ObjectArrayList();
        this.vector3fArr = null;
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        flag = false;
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        synchronized (this.list) {
            this.list.clear();
            this.list.addAll(minecraftClient.world.getPlayers());
            this.list.sort(Comparator.comparing(playerEntity -> {
                return Double.valueOf(minecraftClient.gameRenderer.getCamera().getPos().squaredDistanceTo(playerEntity.getPos()));
            }));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x013f A[Catch: all -> 0x0327, TryCatch #0 {, blocks: (B:4:0x003e, B:5:0x0050, B:7:0x0061, B:9:0x007f, B:12:0x0092, B:14:0x00a1, B:17:0x00c2, B:19:0x00d3, B:21:0x0100, B:24:0x0123, B:26:0x013f, B:32:0x01a5, B:50:0x0323), top: B:3:0x003e }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x01a5 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x01a2 A[SYNTHETIC] */
    @Listen(get219= RenderConstants.num4)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onEvent(MatrixStackEvent.Inner_3 inner_3) {
        boolean z;
        boolean z2;
        this.vector3fArr = (Vector3f[]) Arrays.copyOf(RenderSystem.shaderLightDirections, RenderSystem.shaderLightDirections.length);
        Vec3d pos = minecraftClient.gameRenderer.getCamera().getPos();
        synchronized (this.list) {
            for (PlayerEntity playerEntity : this.list) {
                if (playerEntity != minecraftClient.player || freecam.isToggled()) {
                    if (!playerEntity.isDead() || this.dead.getValue().booleanValue()) {
                        if (ClientServices.nameTagsManager.isConnected()) {
                            if (ClientServices.nameTagsManager.getModeIdentifier(playerEntity.getGameProfile().getName()) != null && this.mioCheck.getValue().booleanValue()) {
                                z = true;
                                this.flag2 = z;
                                if (RotationManager.isInFrustum(playerEntity.getBoundingBox())) {
                                    if (minecraftClient.gameRenderer.getCamera().getPos().distanceTo(playerEntity.getPos()) <= this.range.getValue().intValue()) {
                                        z2 = false;
                                        if (!z2) {
                                            float f = RenderUtil.getPartialTick();
                                            double lerp = MathHelper.lerp(f, playerEntity.lastRenderX, playerEntity.getX()) - pos.x;
                                            double lerp2 = MathHelper.lerp(f, playerEntity.lastRenderY, playerEntity.getY()) - pos.y;
                                            double lerp3 = MathHelper.lerp(f, playerEntity.lastRenderZ, playerEntity.getZ()) - pos.z;
                                            Vec3d positionOffset = minecraftClient.getEntityRenderDispatcher().getRenderer((Entity) playerEntity).getPositionOffset((Entity) playerEntity, f);
                                            double x = lerp + positionOffset.getX();
                                            double y = lerp2 + positionOffset.getY();
                                            double z3 = lerp3 + positionOffset.getZ();
                                            inner_3.getMatrixStack().push();
                                            inner_3.getMatrixStack().translate(x, y, z3);
                                            renderPlayerNametag(inner_3.getDrawContext(), inner_3.getMatrixStack(), playerEntity);
                                            inner_3.getMatrixStack().pop();
                                        }
                                    }
                                }
                                z2 = true;
                                if (!z2) {
                                }
                            }
                        }
                        z = false;
                        this.flag2 = z;
                        if (RotationManager.isInFrustum(playerEntity.getBoundingBox())) {
                        }
                        z2 = true;
                        if (!z2) {
                        }
                    }
                }
            }
        }
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-910746880));
        GL20.glDepthRange(0.0d, Double.longBitsToDouble(4591870180066957722L));
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        DiffuseLighting.disableGuiDepthLighting();
        inner_3.getDrawContext().getVertexConsumers().draw();
        RenderSystem.setShaderLights(this.vector3fArr[0], this.vector3fArr[1]);
        RenderSystem.disableBlend();
        Shape2DRenderer.flush(false);
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-979615744));
        ClientTextRenderer.INSTANCE.resetShadowOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1167867904));
        GL20.glDepthRange(0.0d, Double.longBitsToDouble(4607182418800017408L));
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1236736768));
        RenderSystem.disablePolygonOffset();
    }

    @Listen
    public void onRenderLabel(RenderLabelEvent renderLabelEvent) {
        if (renderLabelEvent.getEntity() instanceof PlayerEntity) {
            renderLabelEvent.cancel();
        }
    }

    public void renderNametagBackgrounds(MatrixStack matrixStack, Vec3d vec3d) {
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-913146880));
        GL20.glDepthRange(0.0d, Double.longBitsToDouble(4591870180066957722L));
        Iterator<PlayerEntity> it = this.list.iterator();
        while (it.hasNext()) {
            ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)((PlayerEntity) it.next());
            if (minecraftClient.player != clientPlayerEntity) {
                float f = (float) MathUtils.getDistanceScale(minecraftClient.gameRenderer.getCamera().getPos(), ((PlayerEntity) clientPlayerEntity).getPos(), this.scale.getValue().floatValue());
                float f2 = RenderUtil.getPartialTick();
                double lerp = MathHelper.lerp(f2, ((PlayerEntity) clientPlayerEntity).lastRenderX, ((PlayerEntity) clientPlayerEntity).getX()) - vec3d.x;
                double lerp2 = MathHelper.lerp(f2, ((PlayerEntity) clientPlayerEntity).lastRenderY, ((PlayerEntity) clientPlayerEntity).getY()) - vec3d.y;
                double lerp3 = MathHelper.lerp(f2, ((PlayerEntity) clientPlayerEntity).lastRenderZ, ((PlayerEntity) clientPlayerEntity).getZ()) - vec3d.z;
                Vec3d positionOffset = minecraftClient.getEntityRenderDispatcher().getRenderer((Entity) clientPlayerEntity).getPositionOffset((Entity) clientPlayerEntity, f2);
                double x = lerp + positionOffset.getX();
                double y = lerp2 + positionOffset.getY();
                double z = lerp3 + positionOffset.getZ();
                matrixStack.push();
                matrixStack.translate(x, y, z);
                matrixStack.push();
                matrixStack.translate(0.0d, (((PlayerEntity) clientPlayerEntity).getHeight() * (animations.isPlayerScaleModified() && clientPlayerEntity != minecraftClient.player ? animations.playerScale.getValue().floatValue() : Float.intBitsToFloat(1065353216))) + Float.intBitsToFloat(1056964608), 0.0d);
                matrixStack.multiply(minecraftClient.getEntityRenderDispatcher().getRotation());
                matrixStack.scale((-f) * Float.intBitsToFloat(1020054733), (-f) * Float.intBitsToFloat(1020054733), f * Float.intBitsToFloat(1020054733));
                float longBitsToDouble = (float) ((-getStringWidth(buildStatusString((PlayerEntity) clientPlayerEntity))) * Double.longBitsToDouble(4602678819172646912L));
                RenderUtil.INSTANCE.drawFilledRectColor(matrixStack, longBitsToDouble - Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-1082130432), (longBitsToDouble * Float.intBitsToFloat(-1082130432)) + Float.intBitsToFloat(1065353216), getSpeed() + Float.intBitsToFloat(1065353216), Color.white);
                matrixStack.pop();
                matrixStack.pop();
            }
        }
        GL20.glDepthRange(0.0d, Double.longBitsToDouble(4607182418800017408L));
        RenderSystem.disablePolygonOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1234336768));
    }

    public void renderPlayerNametag(DrawContext drawContext, MatrixStack matrixStack, PlayerEntity playerEntity) {
        this.flag3 = false;
        this.flag4 = false;
        float height = (playerEntity.getHeight() * (animations.isPlayerScaleModified() && playerEntity != minecraftClient.player ? animations.playerScale.getValue().floatValue() : Float.intBitsToFloat(1065353216))) + Float.intBitsToFloat(1056964608);
        float f = (float) MathUtils.getDistanceScale(minecraftClient.gameRenderer.getCamera().getPos(), playerEntity.getPos(), this.scale.getValue().floatValue());
        matrixStack.push();
        matrixStack.translate(0.0f, height, 0.0f);
        matrixStack.multiply(minecraftClient.getEntityRenderDispatcher().getRotation());
        matrixStack.scale(f * Float.intBitsToFloat(1020054733), (-f) * Float.intBitsToFloat(1020054733), (-f) * Float.intBitsToFloat(1020054733));
        String string1312 = buildStatusString(playerEntity);
        float longBitsToDouble = (float) ((-getStringWidth(string1312)) * Double.longBitsToDouble(4602678819172646912L));
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-910746880));
        GL20.glDepthRange(0.0d, Double.longBitsToDouble(4591870180066957722L));
        Color value = this.outline.getValue();
        if (this.smart.getValue().booleanValue()) {
            if (isEntitySuffocating((Entity) playerEntity)) {
                value = this.phase.getValue();
            } else {
                if (ClientServices.holeSnapOverlayHelper.isHoleAt(playerEntity.getBlockPos())) {
                    value = this.hole.getValue();
                }
            }
        }
        float intBitsToFloat = Float.intBitsToFloat(1056964608);
        Shape2DRenderer.drawRect(matrixStack, longBitsToDouble - Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-1082130432), (longBitsToDouble * Float.intBitsToFloat(-1082130432)) + Float.intBitsToFloat(1065353216), getSpeed() + Float.intBitsToFloat(1065353216), this.fill.getValue());
        Shape2DRenderer.drawOutline(matrixStack, (longBitsToDouble - Float.intBitsToFloat(1073741824)) + intBitsToFloat, Float.intBitsToFloat(-1073741824) + intBitsToFloat, (longBitsToDouble * Float.intBitsToFloat(-1082130432)) + Float.intBitsToFloat(1065353216), getSpeed() + Float.intBitsToFloat(1065353216), intBitsToFloat, value);
        Color value2 = this.text.getValue();
        if (playerEntity.isSneaking()) {
            value2 = this.sneak.getValue();
        }
        if (playerEntity.isInvisible()) {
            value2 = this.invisibles.getValue();
        }
        if (ClientServices.socialManager.isFriendEntity(playerEntity) && this.friend.getValue().booleanValue()) {
            value2 = ClientServices.socialManager.getFriendColor();
        }
        if (ClientServices.socialManager.isEnemyEntity(playerEntity) && this.enemy.getValue().booleanValue()) {
            value2 = ClientServices.socialManager.getEnemyColor();
        }
        boolean isToggled = fonts.isToggled();
        ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, string1312, longBitsToDouble, Float.intBitsToFloat(1065353216), value2);
        flag = true;
        renderItemIcons(drawContext, matrixStack, playerEntity);
        flag = false;
        if (this.flag2) {
            matrixStack.push();
            float intBitsToFloat2 = Float.intBitsToFloat(1032847360);
            matrixStack.translate(longBitsToDouble - Float.intBitsToFloat(1082130432), Float.intBitsToFloat(-1065353216), 0.0f);
            matrixStack.scale(intBitsToFloat2, intBitsToFloat2, intBitsToFloat2);
            if (isToggled) {
                matrixStack.translate(Double.longBitsToDouble(-4616189618054758400L), Double.longBitsToDouble(4608533498688228557L) + fonts.shift.getValue().intValue(), 0.0d);
            }
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            GlStateManager._blendFunc(770, 771);
            GlStateManager._texParameter(3553, 10240, 9729);
            drawContext.drawTexture(identifier, 0, 0, 0, 0, 256, 256);
            GlStateManager._texParameter(3553, 10240, 9728);
            matrixStack.pop();
        }
        GL20.glDepthRange(0.0d, Double.longBitsToDouble(4607182418800017408L));
        RenderSystem.disablePolygonOffset();
        RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1236736768));
        matrixStack.pop();
    }

    public void renderItemIcons(DrawContext drawContext, MatrixStack matrixStack, PlayerEntity playerEntity) {
        ArrayList<ItemStack> arrayList = new ArrayList();
        if (this.items.getValue().booleanValue() && !playerEntity.getOffHandStack().isEmpty()) {
            arrayList.add(playerEntity.getOffHandStack());
        }
        for (ItemStack itemStack : playerEntity.getArmorItems()) {
            if (!itemStack.isEmpty() && this.armor.getValue().booleanValue()) {
                arrayList.add(itemStack);
            }
        }
        if (this.items.getValue().booleanValue() && !playerEntity.getMainHandStack().isEmpty()) {
            arrayList.add(playerEntity.getMainHandStack());
        }
        int i = -3;
        for (ItemStack itemStack2 : arrayList) {
            int size = EnchantmentHelper.getEnchantmentMap(itemStack2).size();
            if (this.hideMax.getValue().booleanValue() && MaxEnchantmentFilter.isFullyEnchanted(itemStack2)) {
                size = 0;
            }
            if (size > i) {
                i = size;
            }
        }
        int max = Math.max(i, 0);
        if (this.durability.getValue() == NameTagsMode.ONLY || !this.enchants.getValue().booleanValue()) {
            max = 0;
        }
        float f = -Math.max((((max * getSpeed()) * Float.intBitsToFloat(1056964608)) / Float.intBitsToFloat(1067030938)) / Float.intBitsToFloat(1099563008), Float.intBitsToFloat(1058642330));
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            renderItemSlot(drawContext, playerEntity, matrixStack, (ItemStack) arrayList.get((arrayList.size() - 1) - i2), (i2 + Float.intBitsToFloat(1056964608)) - (arrayList.size() * Float.intBitsToFloat(1056964608)), f, Float.intBitsToFloat(1099563008));
        }
        if (playerEntity.getMainHandStack().isEmpty() || !this.itemName.getValue().booleanValue()) {
            return;
        }
        float intBitsToFloat = ((f * Float.intBitsToFloat(1099563008)) - Float.intBitsToFloat(1082130432)) - getSpeed();
        if (!this.flag3) {
            intBitsToFloat = -getSpeed();
        }
        if (this.flag4) {
            intBitsToFloat -= getSpeed() * Float.intBitsToFloat(1056964608);
            if (!this.flag3) {
                intBitsToFloat -= Float.intBitsToFloat(1065353216);
            }
        }
        drawLabel(drawContext, Formatting.strip(playerEntity.getMainHandStack().getName().getString()), 0.0f, intBitsToFloat, Double.longBitsToDouble(4627005293803339776L), Color.white, false, NameTagsMode_2.nameTagsMode_2);
    }

    public void renderItemSlot(DrawContext drawContext, PlayerEntity playerEntity, MatrixStack matrixStack, ItemStack itemStack, float f, float f2, float f3) {
        float intBitsToFloat = Float.intBitsToFloat(1058642330);
        int i = ExplosionDamageCalculator.getClampedDurabilityPercent(itemStack);
        if (this.items.getValue().booleanValue() && this.showEating.getValue().booleanValue() && playerEntity.isUsingItem() && playerEntity.getStackInHand(playerEntity.getActiveHand()) == itemStack && itemStack.contains(DataComponentTypes.FOOD)) {
            Shape2DRenderer.drawRect(matrixStack, (f - Float.intBitsToFloat(1055286886)) * f3, (f2 * f3) - Float.intBitsToFloat(1089575322), ((f - Float.intBitsToFloat(1055286886)) + (Float.intBitsToFloat(1063675494) * MathHelper.clamp(((float) (System.currentTimeMillis() - ((DuckPlayerEntity) playerEntity).mio$getLastEatingTime())) / ((itemStack.getMaxUseTime((LivingEntity) playerEntity) * Float.intBitsToFloat(1112014848)) + Float.intBitsToFloat(1112014848)), 0.0f, Float.intBitsToFloat(1065353216)))) * f3, (f2 * f3) + Float.intBitsToFloat(1089575322), ColorUtils.toIntWithAlphaPercent(this.eating.getValue(), Float.intBitsToFloat(1045220557)));
        }
        boolean z = false;
        if (this.durability.getValue() != NameTagsMode.ONLY || (!(itemStack.getItem() instanceof ArmorItem) && !itemStack.isOf(Items.ELYTRA))) {
            z = true;
            this.flag3 = true;
            RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1167867904));
            matrixStack.push();
            matrixStack.translate(f * f3, f2 * f3, 0.0f);
            matrixStack.scale(-f3, -f3, Float.intBitsToFloat(981668463));
            minecraftClient.getItemRenderer().renderItem(itemStack, ModelTransformationMode.FIXED, false, matrixStack, drawContext.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, minecraftClient.getItemRenderer().getModel(itemStack, minecraftClient.world, (LivingEntity) null, 0));
            matrixStack.pop();
            RenderSystem.polygonOffset(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-979615744));
        }
        if (itemStack.getCount() > 1) {
            drawLabel(drawContext, String.valueOf(itemStack.getCount()), (f + Float.intBitsToFloat(1056964608)) * f3, (f2 * f3) + Float.intBitsToFloat(1065353216), f3 * Double.longBitsToDouble(4612248968380809216L), Color.white, true, NameTagsMode_2.nameTagsMode_23);
        }
        if (itemStack.isItemBarVisible() && z) {
            float intBitsToFloat2 = ((int) (f + Float.intBitsToFloat(1045220557))) * f3;
            float intBitsToFloat3 = (f2 * f3) + Float.intBitsToFloat(1085276160);
            Shape2DRenderer.drawRect(matrixStack, (f - Float.intBitsToFloat(1055286886)) * f3, intBitsToFloat3 - Float.intBitsToFloat(1028443341), (f + Float.intBitsToFloat(1055286886)) * f3, intBitsToFloat3 + Float.intBitsToFloat(1073741824), new Color(-16777216));
            Shape2DRenderer.drawRect(matrixStack, (f - Float.intBitsToFloat(1055286886)) * f3, intBitsToFloat3 - Float.intBitsToFloat(1028443341), (f + MathHelper.lerp(itemStack.getItemBarStep() / Float.intBitsToFloat(1095761920), Float.intBitsToFloat(-1092196762), Float.intBitsToFloat(1055286886))) * f3, intBitsToFloat3 + Float.intBitsToFloat(1065772646), new Color(itemStack.getItemBarColor(), false));
        }
        Set<Object2IntMap.Entry> enchantmentEntries = (Set)(itemStack.getEnchantments().getEnchantmentEntries());
        float intBitsToFloat4 = (-intBitsToFloat) * Float.intBitsToFloat(1073741824);
        if (itemStack.isOf(Items.ENCHANTED_GOLDEN_APPLE) && z) {
            drawLabel(drawContext, "God", f * f3, ((f2 * f3) - Float.intBitsToFloat(1065353216)) + (getSpeed() * intBitsToFloat * intBitsToFloat4), f3 * Float.intBitsToFloat(1067869798), this.color, false, NameTagsMode_2.nameTagsMode_23);
            return;
        }
        if (z && this.enchants.getValue().booleanValue()) {
            boolean z2 = this.hideMax.getValue().booleanValue() && MaxEnchantmentFilter.isFullyEnchanted(itemStack);
            if (z2) {
                drawLabel(drawContext, "Max", (f - Float.intBitsToFloat(1056964608)) * f3, ((f2 * f3) - Float.intBitsToFloat(1073741824)) + (((getSpeed() * intBitsToFloat) / Float.intBitsToFloat(1067030938)) * intBitsToFloat4), f3 * Float.intBitsToFloat(1067869798), Color.RED, false, NameTagsMode_2.nameTagsMode_22);
            }
            for (Object2IntMap.Entry entry : enchantmentEntries) {
                if (z2) {
                    break;
                }
                boolean z3 = ((Enchantment) ((RegistryEntry) entry.getKey()).value()).getMaxLevel() == 1;
                String string = Enchantment.getName((RegistryEntry) entry.getKey(), entry.getIntValue()).getString();
                String substring = string.substring(0, Math.min(z3 ? 3 : 2, string.length()));
                if (!substring.equalsIgnoreCase("Cu")) {
                    if (!z3) {
                        substring = new StringTemplate().arg(entry.getIntValue()).arg(substring).format("\u0001\u0001");
                    }
                    drawLabel(drawContext, substring, (f - Float.intBitsToFloat(1056964608)) * f3, ((f2 * f3) - Float.intBitsToFloat(1073741824)) + (((getSpeed() * intBitsToFloat) / Float.intBitsToFloat(1067030938)) * intBitsToFloat4), f3 * Float.intBitsToFloat(1067869798), Color.WHITE, false, NameTagsMode_2.nameTagsMode_22);
                    intBitsToFloat4 += Float.intBitsToFloat(1065353216);
                }
            }
        }
        if (!itemStack.isDamageable() || this.durability.getValue() == NameTagsMode.HIDE) {
            return;
        }
        float intBitsToFloat5 = ((f2 * f3) - Float.intBitsToFloat(1082130432)) - getSpeed();
        if (!z) {
            intBitsToFloat5 = f2 - getSpeed();
        }
        this.flag4 = true;
        drawLabel(drawContext, String.format("%d%s", Integer.valueOf(i), "%"), ((float) ((f - MathConstants.val2) * f3)) + Float.intBitsToFloat(1065353216), intBitsToFloat5, f3 * Float.intBitsToFloat(1069547520), new Color(itemStack.getItemBarColor(), false), false, NameTagsMode_2.nameTagsMode_22);
    }

    public void drawLabel(DrawContext drawContext, String str, float f, float f2, double d, Color color, boolean z, NameTagsMode_2 nameTagsMode_2) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.translate(f, f2, 0.0f);
        matrices.scale(Float.intBitsToFloat(1020054733) * ((float) d), Float.intBitsToFloat(1020054733) * ((float) d), Float.intBitsToFloat(1065353216));
        float f3 = nameTagsMode_2.getTextOffsetX(str, z);
        if (z) {
            Shape2DRenderer.drawString(drawContext.getMatrices(), str, (int) f3, 0, color.hashCode(), true);
        } else {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str, f3, 0.0f, color);
        }
        matrices.pop();
    }

    public String buildStatusString(PlayerEntity playerEntity) {
        int i;
        StringBuilder sb = new StringBuilder();
        if (this.flag2) {
            sb.append(Formatting.RESET);
            sb.append("   ");
        }
        if (this.name.getValue().booleanValue()) {
            sb.append(ClientServices.notificationsHelper.getPlayerDisplayName(playerEntity.getGameProfile().getName()));
        }
        if (this.gamemode.getValue().booleanValue()) {
            sb.append(" [");
            try {
                PlayerListEntry playerListEntry = minecraftClient.player.networkHandler.getPlayerListEntry(playerEntity.getGameProfile().getId());
                sb.append(playerListEntry == null ? "S" : abbreviateGamemode(playerListEntry.getGameMode().getName()));
            } catch (Throwable th) {
                sb.append(0);
            }
            sb.append("]");
        }
        if (this.ping.getValue().booleanValue()) {
            sb.append(" ");
            try {
                PlayerListEntry playerListEntry2 = minecraftClient.player.networkHandler.getPlayerListEntry(playerEntity.getGameProfile().getId());
                sb.append(playerListEntry2 == null ? 0 : playerListEntry2.getLatency());
            } catch (Throwable th2) {
                sb.append(0);
            }
            sb.append("ms");
        }
        if (this.health.getValue().booleanValue()) {
            float f = EntityUtil.getEntityHealth((Entity) playerEntity);
            if (this.healthColor.getValue().booleanValue()) {
                sb.append(getHealthColor(f));
            }
            sb.append(" ").append(String.format("%.1f", Float.valueOf(f))).append(Formatting.RESET);
        }
        if (this.totemPops.getValue().booleanValue() && (i = ClientServices.logoutSpotsHelper.getPopCount(playerEntity)) > 0) {
            sb.append(getTotemPopColor(i)).append(" -").append(i);
        }
        return sb.toString().trim();
    }

    public Formatting getHealthColor(double d) {
        return d >= Double.longBitsToDouble(4626322717216342016L) ? Formatting.GREEN : d >= Double.longBitsToDouble(4625196817309499392L) ? Formatting.DARK_GREEN : d >= Double.longBitsToDouble(4621819117588971520L) ? Formatting.GOLD : d >= Double.longBitsToDouble(4616189618054758400L) ? Formatting.RED : Formatting.DARK_RED;
    }

    public static Formatting getTotemPopColor(int i) {
        return i > 6 ? Formatting.DARK_RED : i > 3 ? Formatting.GOLD : Formatting.YELLOW;
    }

    public String abbreviateGamemode(String str) {
        String lowerCase = str.toLowerCase(Locale.ROOT);
        int z = -1;
        switch (lowerCase.hashCode()) {
            case -1684593425:
                if (lowerCase.equals("spectator")) {
                    z = 3;
                    break;
                }
                break;
            case -1600582850:
                if (lowerCase.equals("survival")) {
                    z = 0;
                    break;
                }
                break;
            case -694094064:
                if (lowerCase.equals("adventure")) {
                    z = 2;
                    break;
                }
                break;
            case 1820422063:
                if (lowerCase.equals("creative")) {
                    z = 1;
                    break;
                }
                break;
        }
        switch (z) {
            case 0:
                return "S";
            case 1:
                return "C";
            case 2:
                return "A";
            case 3:
                return "SP";
            default:
                return "";
        }
    }

    public float getSpeed() {
        return ClientTextRenderer.INSTANCE.getFontHeight();
    }

    public float getStringWidth(String str) {
        return ClientTextRenderer.INSTANCE.getStringWidth(str);
    }

    public boolean isEntitySuffocating(Entity entity) {
        float width = entity.getDimensions(entity.getPose()).width() * Float.intBitsToFloat(1061997773);
        return BlockPos.stream(Box.of(entity.getBoundingBox().getCenter(), width, entity.getHeight() * Float.intBitsToFloat(1056964608), width)).anyMatch(blockPos -> {
            BlockState blockState = minecraftClient.world.getBlockState(blockPos);
            return !blockState.isAir() && blockState.shouldSuffocate(minecraftClient.world, blockPos);
        });
    }
}
