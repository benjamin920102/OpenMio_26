package me.mioclient.module.render;

 import me.mioclient.api.ClientServices;
 import me.mioclient.api.NamedValue;
 import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.shape.Shape2DRenderer;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.EnchantmentHelper;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientAccessor;
import me.mioclient.module.render.nametags.MaxEnchantmentFilter;
import me.mioclient.render.RenderUtil;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.mioclient.render.RenderConstants;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.ducks.DuckPlayerEntity;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.render.RenderLabelEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.player.Freecam;
 import net.minecraft.world.level.block.state.BlockState;
 import net.minecraft.client.gui.GuiGraphicsExtractor;
 import net.minecraft.client.multiplayer.PlayerInfo;
 import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
 import net.minecraft.world.entity.EquipmentSlot;
 import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
 import org.joml.Matrix3x2fStack;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

 public class NameTags extends Module {
     public static final Identifier identifier = Identifier.fromNamespaceAndPath("mio", "textures/mio.png");
    /* Resolved lazily -- Animations/Freecam/Fonts register after NameTags, so eager
       static lookups left the refs null (NPE on render). */
    private static Animations animations;
    private static Freecam freecam;
    private static Fonts fonts;

    public static Animations getAnimations() {
        if (animations == null && ClientServices.moduleInjector != null) {
            try {
                animations = (Animations) ClientServices.moduleInjector.getModule(Animations.class);
            } catch (Exception e) {
            }
        }
        return animations;
    }

    public static Freecam getFreecam() {
        if (freecam == null && ClientServices.moduleInjector != null) {
            try {
                freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class);
            } catch (Exception e) {
            }
        }
        return freecam;
    }

    public static Fonts getFonts() {
        if (fonts == null && ClientServices.moduleInjector != null) {
            try {
                fonts = (Fonts) ClientServices.moduleInjector.getModule(Fonts.class);
            } catch (Exception e) {
            }
        }
        return fonts;
    }

    public float getPlayerScaleFactor(net.minecraft.world.entity.player.Player player) {
        Animations animations_lcl = getAnimations();
        if (animations_lcl != null && animations_lcl.isPlayerScaleModified() && player != minecraftClient.player) {
            return animations_lcl.playerScale.getValue().floatValue();
        }
        return Float.intBitsToFloat(1065353216);
    }
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
    public Setting<Color> text = add(new ColorSetting("Component", new Color(-1, true), v -> this.colors.isShown()));
    public Setting<Color> invisibles = add(new ColorSetting("Invisibles", new Color(-65536, true), v -> this.colors.isShown()));
    public Setting<Color> sneak = add(new ColorSetting("Sneak", new Color(-28416, true), v -> this.colors.isShown()));
    public Setting<Color> eating = add(new ColorSetting("Eating", new Color(-16711936, true), v -> this.colors.isShown()));
    public final Color color;
    public final List<Player> list;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;

    
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
            return getAlignmentOffset(z ? ClientAccessor.minecraftClient.font.width(str) : ClientTextRenderer.INSTANCE.getStringWidth(str));
        }
    }

    public NameTags() {
        super("NameTags", "Draws advanced nametags for players.", Category.RENDER, new String[0]);
        this.color = new Color(153, 69, 58);
        this.list = new ObjectArrayList();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        flag = false;
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        synchronized (this.list) {
            this.list.clear();
            this.list.addAll(minecraftClient.level.players());
            this.list.sort(Comparator.comparing(playerEntity -> {
                return Double.valueOf(minecraftClient.gameRenderer.mainCamera().position().distanceToSqr(playerEntity.position()));
            }));
        }
    }

    
    
    @Listen(get219= RenderConstants.num4)
    
    public void onEvent(MatrixStackEvent.Inner_3 inner_3) {
        // 26.2: fixed-function GL state (polygonOffset/depthRange/blend/DiffuseLighting/shaderLights) removed (GpuBackend).
        // Real behavior: interpolate, translate world PoseStack, render nametag, flush 2D batch.
        Vec3 pos = minecraftClient.gameRenderer.mainCamera().position();
        synchronized (this.list) {
            for (Player playerEntity : this.list) {
                Freecam freecam_lcl = getFreecam();
                if (playerEntity == minecraftClient.player && (freecam_lcl == null || !freecam_lcl.isToggled())) {
                    continue;
                }
                if (playerEntity.isRemoved() && !this.dead.getValue().booleanValue()) {
                    continue;
                }
                boolean mio = ClientServices.nameTagsManager.isConnected()
                    && this.mioCheck.getValue().booleanValue()
                    && ClientServices.nameTagsManager.getModeIdentifier(playerEntity.getGameProfile().name()) != null;
                this.flag2 = mio;
                if (!RotationManager.isInFrustum(playerEntity.getBoundingBox())) {
                    continue;
                }
                if (minecraftClient.gameRenderer.mainCamera().position().distanceTo(playerEntity.position()) > this.range.getValue().intValue()) {
                    continue;
                }
                float f = RenderUtil.getPartialTick();
                double ix = Mth.lerp(f, (float) playerEntity.xo, (float) playerEntity.getX());
                double iy = Mth.lerp(f, (float) playerEntity.yo, (float) playerEntity.getY());
                double iz = Mth.lerp(f, (float) playerEntity.zo, (float) playerEntity.getZ());
                float height = (playerEntity.getBbHeight() * getPlayerScaleFactor(playerEntity)) + Float.intBitsToFloat(1056964608);
                /* 26.2 backend: world-anchored tag through vanilla's nametag pipeline
                   (bg plate, distance scaling and depth handled by vanilla). */
                BoxRenderer.submitNameTag(inner_3.getMatrixStack(), new Vec3(ix, iy, iz), height, parseLegacyColors(buildStatusString(playerEntity)), inner_3.cameraState, this.scale.getValue().floatValue());
            }
        }
        Shape2DRenderer.flush(false);
        ClientTextRenderer.INSTANCE.resetShadowOffset();
    }

    @Listen
    public void onRenderLabel(RenderLabelEvent renderLabelEvent) {
        if (renderLabelEvent.getEntity() instanceof Player) {
            renderLabelEvent.cancel();
        }
    }

    public void renderNametagBackgrounds(PoseStack matrixStack, Vec3 vec3d) {
        // 26.2: GL polygonOffset/depthRange removed (GpuBackend); real behavior preserved via world PoseStack.
        Iterator<Player> it = this.list.iterator();
        while (it.hasNext()) {
            Player clientPlayerEntity = it.next();
            if (minecraftClient.player != clientPlayerEntity) {
                float f = (float) MathUtils.getDistanceScale(minecraftClient.gameRenderer.mainCamera().position(), clientPlayerEntity.position(), this.scale.getValue().floatValue());
                float f2 = RenderUtil.getPartialTick();
                double lerp = Mth.lerp(f2, (float) clientPlayerEntity.xo, (float) clientPlayerEntity.getX()) - vec3d.x;
                double lerp2 = Mth.lerp(f2, (float) clientPlayerEntity.yo, (float) clientPlayerEntity.getY()) - vec3d.y;
                double lerp3 = Mth.lerp(f2, (float) clientPlayerEntity.zo, (float) clientPlayerEntity.getZ()) - vec3d.z;
                matrixStack.pushPose();
                matrixStack.translate(lerp, lerp2, lerp3);
                matrixStack.pushPose();
                matrixStack.translate(0.0d, (clientPlayerEntity.getBbHeight() * getPlayerScaleFactor(clientPlayerEntity)) + Float.intBitsToFloat(1056964608), 0.0d);
                matrixStack.mulPose(minecraftClient.gameRenderer.mainCamera().rotation());
                matrixStack.scale((-f) * Float.intBitsToFloat(1020054733), (-f) * Float.intBitsToFloat(1020054733), f * Float.intBitsToFloat(1020054733));
                float longBitsToDouble = (float) ((-getStringWidth(buildStatusString(clientPlayerEntity))) * Double.longBitsToDouble(4602678819172646912L));
                RenderUtil.INSTANCE.drawFilledRectColor(matrixStack, longBitsToDouble - Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-1082130432), (longBitsToDouble * Float.intBitsToFloat(-1082130432)) + Float.intBitsToFloat(1065353216), getSpeed() + Float.intBitsToFloat(1065353216), Color.white);
                matrixStack.popPose();
                matrixStack.popPose();
            }
        }
    }

    public void renderPlayerNametag(GuiGraphicsExtractor drawContext, PoseStack matrixStack, Player playerEntity) {
        this.flag3 = false;
        this.flag4 = false;
        // 26.2: Entity.getHeight/blockPosition renamed; camera rotation via Camera.rotation(); GL state removed.
        float height = (playerEntity.getBbHeight() * getPlayerScaleFactor(playerEntity)) + Float.intBitsToFloat(1056964608);
        float f = (float) MathUtils.getDistanceScale(minecraftClient.gameRenderer.mainCamera().position(), playerEntity.position(), this.scale.getValue().floatValue());
        matrixStack.pushPose();
        matrixStack.translate(0.0f, height, 0.0f);
        matrixStack.mulPose(minecraftClient.gameRenderer.mainCamera().rotation());
        matrixStack.scale(f * Float.intBitsToFloat(1020054733), (-f) * Float.intBitsToFloat(1020054733), (-f) * Float.intBitsToFloat(1020054733));
        String string1312 = buildStatusString(playerEntity);
        float longBitsToDouble = (float) ((-getStringWidth(string1312)) * Double.longBitsToDouble(4602678819172646912L));
        Color value = this.outline.getValue();
        if (this.smart.getValue().booleanValue()) {
            if (isEntitySuffocating((Entity) playerEntity)) {
                value = this.phase.getValue();
            } else {
                if (ClientServices.holeSnapOverlayHelper.isHoleAt(playerEntity.blockPosition())) {
                    value = this.hole.getValue();
                }
            }
        }
        float intBitsToFloat = Float.intBitsToFloat(1056964608);
        Shape2DRenderer.drawRect(matrixStack, longBitsToDouble - Float.intBitsToFloat(1065353216), Float.intBitsToFloat(-1082130432), (longBitsToDouble * Float.intBitsToFloat(-1082130432)) + Float.intBitsToFloat(1065353216), getSpeed() + Float.intBitsToFloat(1065353216), this.fill.getValue());
        Shape2DRenderer.drawOutline(matrixStack, (longBitsToDouble - Float.intBitsToFloat(1073741824)) + intBitsToFloat, Float.intBitsToFloat(-1073741824) + intBitsToFloat, (longBitsToDouble * Float.intBitsToFloat(-1082130432)) + Float.intBitsToFloat(1065353216), getSpeed() + Float.intBitsToFloat(1065353216), intBitsToFloat, value);
        Color value2 = this.text.getValue();
        if (playerEntity.isShiftKeyDown()) {
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
        Fonts fonts_lcl = getFonts();
        boolean isToggled = fonts_lcl != null && fonts_lcl.isToggled();
        ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, string1312, longBitsToDouble, Float.intBitsToFloat(1065353216), value2);
        flag = true;
        renderItemIcons(drawContext, matrixStack, playerEntity);
        flag = false;
        if (this.flag2) {
            // 26.2: fixed-function texture state removed; draw mio badge via GUI blit pipeline.
            Matrix3x2fStack guiPose = drawContext.pose();
            guiPose.pushMatrix();
            float intBitsToFloat2 = Float.intBitsToFloat(1032847360);
            guiPose.translate(longBitsToDouble - Float.intBitsToFloat(1082130432), Float.intBitsToFloat(-1065353216));
            guiPose.scale(intBitsToFloat2, intBitsToFloat2);
            if (isToggled) {
                guiPose.translate((float) Double.longBitsToDouble(-4616189618054758400L), (float) (Double.longBitsToDouble(4608533498688228557L) + (fonts_lcl != null ? fonts_lcl.shift.getValue().intValue() : 0)));
            }
            drawContext.blit(RenderPipelines.GUI_TEXTURED, identifier, 0, 0, 0.0f, 0.0f, 256, 256, 256, 256);
            guiPose.popMatrix();
        }
        matrixStack.popPose();
    }

    public void renderItemIcons(GuiGraphicsExtractor drawContext, PoseStack matrixStack, Player playerEntity) {
        ArrayList<ItemStack> arrayList = new ArrayList();
        // 26.2: getOffHandItem -> getOffhandItem; getArmorItems removed -> iterate armor EquipmentSlots.
        if (this.items.getValue().booleanValue() && !playerEntity.getOffhandItem().isEmpty()) {
            arrayList.add(playerEntity.getOffhandItem());
        }
        if (this.armor.getValue().booleanValue()) {
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack armorStack = playerEntity.getItemBySlot(slot);
                if (!armorStack.isEmpty()) {
                    arrayList.add(armorStack);
                }
            }
        }
        if (this.items.getValue().booleanValue() && !playerEntity.getMainHandItem().isEmpty()) {
            arrayList.add(playerEntity.getMainHandItem());
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
        if (playerEntity.getMainHandItem().isEmpty() || !this.itemName.getValue().booleanValue()) {
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
        drawLabel(drawContext, ChatFormatting.stripFormatting(playerEntity.getMainHandItem().getDisplayName().getString()), 0.0f, intBitsToFloat, Double.longBitsToDouble(4627005293803339776L), Color.white, false, NameTagsMode_2.nameTagsMode_2);
    }

    public void renderItemSlot(GuiGraphicsExtractor drawContext, Player playerEntity, PoseStack matrixStack, ItemStack itemStack, float f, float f2, float f3) {
        // 26.2: ArmorItem removed -> EQUIPPABLE slot check; isOf -> is; bar/durability/enchant APIs renamed.
        float intBitsToFloat = Float.intBitsToFloat(1058642330);
        int i = ExplosionDamageCalculator.getClampedDurabilityPercent(itemStack);
        if (this.items.getValue().booleanValue() && this.showEating.getValue().booleanValue() && playerEntity.isUsingItem() && playerEntity.getItemInHand(playerEntity.getUsedItemHand()) == itemStack && itemStack.has(DataComponents.FOOD)) {
            Shape2DRenderer.drawRect(matrixStack, (f - Float.intBitsToFloat(1055286886)) * f3, (f2 * f3) - Float.intBitsToFloat(1089575322), ((f - Float.intBitsToFloat(1055286886)) + (Float.intBitsToFloat(1063675494) * Mth.clamp(((float) (System.currentTimeMillis() - ((DuckPlayerEntity) playerEntity).mio$getLastEatingTime())) / ((itemStack.getUseDuration((LivingEntity) playerEntity) * Float.intBitsToFloat(1112014848)) + Float.intBitsToFloat(1112014848)), 0.0f, Float.intBitsToFloat(1065353216)))) * f3, (f2 * f3) + Float.intBitsToFloat(1089575322), ColorUtils.toIntWithAlphaPercent(this.eating.getValue(), Float.intBitsToFloat(1045220557)));
        }
        boolean z = false;
        Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        boolean isArmorPiece = equippable != null && equippable.slot().isArmor();
        if (this.durability.getValue() != NameTagsMode.ONLY || (!isArmorPiece && !itemStack.is(Items.ELYTRA))) {
            z = true;
            this.flag3 = true;
            // 26.2: world ItemRenderer removed; render GUI item icon at nametag offset (real behavior preserved).
            Matrix3x2fStack guiPose = drawContext.pose();
            guiPose.pushMatrix();
            guiPose.translate(f * f3, f2 * f3);
            guiPose.scale(f3 / 16.0f, f3 / 16.0f);
            drawContext.item(itemStack, 0, 0);
            guiPose.popMatrix();
        }
        if (itemStack.getCount() > 1) {
            drawLabel(drawContext, String.valueOf(itemStack.getCount()), (f + Float.intBitsToFloat(1056964608)) * f3, (f2 * f3) + Float.intBitsToFloat(1065353216), f3 * Double.longBitsToDouble(4612248968380809216L), Color.white, true, NameTagsMode_2.nameTagsMode_23);
        }
        if (itemStack.isBarVisible() && z) {
            float intBitsToFloat3 = (f2 * f3) + Float.intBitsToFloat(1085276160);
            Shape2DRenderer.drawRect(matrixStack, (f - Float.intBitsToFloat(1055286886)) * f3, intBitsToFloat3 - Float.intBitsToFloat(1028443341), (f + Float.intBitsToFloat(1055286886)) * f3, intBitsToFloat3 + Float.intBitsToFloat(1073741824), new Color(-16777216));
            Shape2DRenderer.drawRect(matrixStack, (f - Float.intBitsToFloat(1055286886)) * f3, intBitsToFloat3 - Float.intBitsToFloat(1028443341), (f + Mth.lerp(itemStack.getBarWidth() / Float.intBitsToFloat(1095761920), Float.intBitsToFloat(-1092196762), Float.intBitsToFloat(1055286886))) * f3, intBitsToFloat3 + Float.intBitsToFloat(1065772646), new Color(itemStack.getBarColor(), false));
        }
        Set<Object2IntMap.Entry<Holder<Enchantment>>> enchantmentEntries = (Set) (itemStack.getEnchantments().entrySet());
        float intBitsToFloat4 = (-intBitsToFloat) * Float.intBitsToFloat(1073741824);
        if (itemStack.is(Items.ENCHANTED_GOLDEN_APPLE) && z) {
            drawLabel(drawContext, "God", f * f3, ((f2 * f3) - Float.intBitsToFloat(1065353216)) + (getSpeed() * intBitsToFloat * intBitsToFloat4), f3 * Float.intBitsToFloat(1067869798), this.color, false, NameTagsMode_2.nameTagsMode_23);
            return;
        }
        if (z && this.enchants.getValue().booleanValue()) {
            boolean z2 = this.hideMax.getValue().booleanValue() && MaxEnchantmentFilter.isFullyEnchanted(itemStack);
            if (z2) {
                drawLabel(drawContext, "Max", (f - Float.intBitsToFloat(1056964608)) * f3, ((f2 * f3) - Float.intBitsToFloat(1073741824)) + (((getSpeed() * intBitsToFloat) / Float.intBitsToFloat(1067030938)) * intBitsToFloat4), f3 * Float.intBitsToFloat(1067869798), Color.RED, false, NameTagsMode_2.nameTagsMode_22);
            }
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantmentEntries) {
                if (z2) {
                    break;
                }
                boolean z3 = ((Holder<Enchantment>) entry.getKey()).value().getMaxLevel() == 1;
                String string = Enchantment.getFullname((Holder<Enchantment>) entry.getKey(), entry.getIntValue()).getString();
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
        if (!itemStack.isDamageableItem() || this.durability.getValue() == NameTagsMode.HIDE) {
            return;
        }
        float intBitsToFloat5 = ((f2 * f3) - Float.intBitsToFloat(1082130432)) - getSpeed();
        if (!z) {
            intBitsToFloat5 = f2 - getSpeed();
        }
        this.flag4 = true;
        drawLabel(drawContext, String.format("%d%s", Integer.valueOf(i), "%"), ((float) ((f - MathConstants.val2) * f3)) + Float.intBitsToFloat(1065353216), intBitsToFloat5, f3 * Float.intBitsToFloat(1069547520), new Color(itemStack.getBarColor(), false), false, NameTagsMode_2.nameTagsMode_22);
    }

    public void drawLabel(GuiGraphicsExtractor drawContext, String str, float f, float f2, double d, Color color, boolean z, NameTagsMode_2 nameTagsMode_2) {
        // 26.2: GuiGraphicsExtractor.pose() is Matrix3x2fStack (2D); world PoseStack gone from GUI text path.
        Matrix3x2fStack matrices = drawContext.pose();
        matrices.pushMatrix();
        matrices.translate(f, f2);
        matrices.scale(Float.intBitsToFloat(1020054733) * ((float) d), Float.intBitsToFloat(1020054733) * ((float) d));
        float f3 = nameTagsMode_2.getTextOffsetX(str, z);
        if (z) {
            ClientTextRenderer.INSTANCE.drawText(drawContext, str, f3, 0.0f, color);
        } else {
            ClientTextRenderer.INSTANCE.drawTextShadow(drawContext, str, f3, 0.0f, color);
        }
        matrices.popMatrix();
    }

    /* 26.2 backend: the status string carries legacy section-sign codes; convert to a
       styled component so vanilla's nametag pipeline renders the exact colors. */
    public static MutableComponent parseLegacyColors(String str) {
        MutableComponent root = Component.empty();
        if (str == null || str.isEmpty()) {
            return root;
        }
        StringBuilder cur = new StringBuilder();
        Style style = Style.EMPTY;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c == 167 || c == '&') && i + 1 < str.length()) {
                char code = Character.toLowerCase(str.charAt(i + 1));
                ChatFormatting fmt = ChatFormatting.getByCode(code);
                if (fmt != null) {
                    if (cur.length() > 0) {
                        root.append(Component.literal(cur.toString()).withStyle(style));
                        cur.setLength(0);
                    }
                    i++;
                    if (fmt == ChatFormatting.RESET) {
                        style = Style.EMPTY;
                    } else if (fmt.ordinal() >= ChatFormatting.BLACK.ordinal() && fmt.ordinal() <= ChatFormatting.WHITE.ordinal()) {
                        style = Style.EMPTY.withColor(fmt);
                    } else if (fmt == ChatFormatting.BOLD) {
                        style = style.withBold(Boolean.TRUE);
                    } else if (fmt == ChatFormatting.ITALIC) {
                        style = style.withItalic(Boolean.TRUE);
                    } else if (fmt == ChatFormatting.UNDERLINE) {
                        style = style.withUnderlined(Boolean.TRUE);
                    } else if (fmt == ChatFormatting.STRIKETHROUGH) {
                        style = style.withStrikethrough(Boolean.TRUE);
                    } else if (fmt == ChatFormatting.OBFUSCATED) {
                        style = style.withObfuscated(Boolean.TRUE);
                    }
                    continue;
                }
            }
            cur.append(c);
        }
        if (cur.length() > 0) {
            root.append(Component.literal(cur.toString()).withStyle(style));
        }
        return root;
    }

    public String buildStatusString(Player playerEntity) {
        int i;
        StringBuilder sb = new StringBuilder();
        if (this.flag2) {
            sb.append(ChatFormatting.RESET);
            sb.append("   ");
        }
        if (this.name.getValue().booleanValue()) {
            sb.append(ClientServices.notificationsHelper.getPlayerDisplayName(playerEntity.getGameProfile().name()));
        }
        if (this.gamemode.getValue().booleanValue()) {
            sb.append(" [");
            try {
                // 26.2: ClientPacketListener.getPlayerListEntry -> getPlayerInfo; PlayerListEntry -> PlayerInfo.
                PlayerInfo playerListEntry = minecraftClient.player.connection.getPlayerInfo(playerEntity.getGameProfile().id());
                sb.append(playerListEntry == null ? "S" : abbreviateGamemode(playerListEntry.getGameMode().getName()));
            } catch (Throwable th) {
                sb.append(0);
            }
            sb.append("]");
        }
        if (this.ping.getValue().booleanValue()) {
            sb.append(" ");
            try {
                PlayerInfo playerListEntry2 = minecraftClient.player.connection.getPlayerInfo(playerEntity.getGameProfile().id());
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
            sb.append(" ").append(String.format("%.1f", Float.valueOf(f))).append(ChatFormatting.RESET);
        }
        if (this.totemPops.getValue().booleanValue() && (i = ClientServices.logoutSpotsHelper.getPopCount(playerEntity)) > 0) {
            sb.append(getTotemPopColor(i)).append(" -").append(i);
        }
        return sb.toString().trim();
    }

    public ChatFormatting getHealthColor(double d) {
        return d >= Double.longBitsToDouble(4626322717216342016L) ? ChatFormatting.GREEN : d >= Double.longBitsToDouble(4625196817309499392L) ? ChatFormatting.DARK_GREEN : d >= Double.longBitsToDouble(4621819117588971520L) ? ChatFormatting.GOLD : d >= Double.longBitsToDouble(4616189618054758400L) ? ChatFormatting.RED : ChatFormatting.DARK_RED;
    }

    public static ChatFormatting getTotemPopColor(int i) {
        return i > 6 ? ChatFormatting.DARK_RED : i > 3 ? ChatFormatting.GOLD : ChatFormatting.YELLOW;
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
        // 26.2: getHeight -> getBbHeight; BlockPos.stream(AABB) -> betweenClosedStream; shouldSuffocate -> isSuffocating.
        float width = entity.getDimensions(entity.getPose()).width() * Float.intBitsToFloat(1061997773);
        Vec3 center = entity.getBoundingBox().getCenter();
        float h = entity.getBbHeight() * Float.intBitsToFloat(1056964608);
        AABB box = new AABB(center.x - width, center.y, center.z - width, center.x + width, center.y + h, center.z + width);
        return BlockPos.betweenClosedStream(box).anyMatch(blockPos -> {
            BlockState blockState = minecraftClient.level.getBlockState(blockPos);
            return !blockState.isAir() && blockState.isSuffocating(minecraftClient.level, blockPos);
        });
    }
}