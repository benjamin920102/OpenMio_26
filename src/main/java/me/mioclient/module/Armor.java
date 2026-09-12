package me.mioclient.module;

import me.mioclient.event.events.render.HudRenderEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.math.MathConstants;

import me.mioclient.api.ClientServices;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.math.MathConstants;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.event.events.render.HudRenderEvent;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.render.hud.ResizableHudElement;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.client.HUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Armor.class */
public class Armor extends me.mioclient.module.HudModule {
    public static HUD hud = (HUD) ClientServices.moduleInjector.getModule(HUD.class);
    public Setting<Boolean> setting;
    public Setting<Boolean> setting2;
    public Setting<Boolean> setting3;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/Armor$Inner.class */
    class Inner extends ResizableHudElement {
        public Inner(Armor armor, me.mioclient.module.HudModule moduleList) {
            super(moduleList);
        }

        @Override // me.mioclient.render.hud.DraggableHudElement
        public boolean isVisible() {
            return false;
        }
    }

    public Armor() {
        super("Armor", new String[0]);
        this.setting = add(new BooleanSetting("Durability", true));
        this.setting2 = add(new BooleanSetting("Percentage", true));
        this.setting3 = add(new BooleanSetting("BarColor", true));
        setElement(new Inner(this, this));
    }

    @Listen(get219= 1)
    public void onHudRender(HudRenderEvent matrixStackEvent_2) {
        if (hud.isToggled() || isInHudEditor()) {
            int scaledWindowWidth = matrixStackEvent_2.getDrawContext().getScaledWindowWidth();
            int scaledWindowHeight = matrixStackEvent_2.getDrawContext().getScaledWindowHeight();
            int i = 15;
            for (int i2 = 3; i2 >= 0; i2--) {
                ItemStack itemStack = (ItemStack) minecraftClient.player.getInventory().armor.get(i2);
                if (!itemStack.isEmpty()) {
                    drawArmorItem(matrixStackEvent_2.getDrawContext(), itemStack, (scaledWindowWidth / 2) + i, scaledWindowHeight - getHudOffsetY());
                    if (itemStack.isDamageable() && this.setting.getValue().booleanValue()) {
                        String format = String.format("%d%s", Integer.valueOf(ExplosionDamageCalculator.getClampedDurabilityPercent(itemStack)), "%");
                        float intBitsToFloat = Fonts.fonts.isToggled() ? Float.intBitsToFloat(1060320051) : Float.intBitsToFloat(1059481190);
                        if (!this.setting2.getValue().booleanValue()) {
                            format = format.substring(0, format.length() - 1);
                            intBitsToFloat = Float.intBitsToFloat(1065353216);
                        }
                        float intBitsToFloat2 = ((scaledWindowHeight - getHudOffsetY()) - Float.intBitsToFloat(1081291571)) - (this.setting2.getValue().booleanValue() ? 0 : 2);
                        ClientTextRenderer.INSTANCE.drawScaledTextShadow(matrixStackEvent_2.getDrawContext(), format, ((((scaledWindowWidth / Float.intBitsToFloat(1073741824)) + i) + Float.intBitsToFloat(1091567616)) - ((ClientTextRenderer.INSTANCE.getStringWidth(format) * Float.intBitsToFloat(1056964608)) * intBitsToFloat)) - intBitsToFloat, intBitsToFloat2, intBitsToFloat, this.setting3.getValue().booleanValue() ? new Color(itemStack.getItemBarColor(), false) : getHudColor(intBitsToFloat2));
                    }
                    i += 18;
                }
            }
        }
    }

    @Override // me.mioclient.module.HudModule
    public void renderContent(DrawContext drawContext) {
        super.renderContent(drawContext);
    }

    @Override // me.mioclient.module.HudModule
    public float[] getSize() {
        return new float[]{0.0f, 0.0f};
    }

    public void drawArmorItem(DrawContext drawContext, ItemStack itemStack, int i, int i2) {
        drawContext.drawItem(itemStack, i, i2);
        drawContext.drawItemInSlot(minecraftClient.textRenderer, itemStack, i, i2);
        RenderSystem.enableBlend();
    }

    public int getHudOffsetY() {
        int i;
        if ((minecraftClient.player.isSubmergedInWater() || minecraftClient.player.getAir() != minecraftClient.player.getMaxAir()) && minecraftClient.player.getAir() > 0 && !minecraftClient.player.isCreative()) {
            i = 65;
        } else {
            if (minecraftClient.player.hasVehicle()) {
                if (minecraftClient.player.getVehicle() instanceof LivingEntity) {
                    i = (int) (MathConstants.num + (Math.ceil((((LivingEntity) minecraftClient.player.getVehicle()).getMaxHealth() - Float.intBitsToFloat(1065353216)) / Float.intBitsToFloat(1101004800)) * Double.longBitsToDouble(4621819117588971520L)));
                }
            }
            i = 55 - (minecraftClient.player.isCreative() ? 17 : 0);
        }
        return i;
    }
}
