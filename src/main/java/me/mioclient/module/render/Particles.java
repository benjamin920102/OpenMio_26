package me.mioclient.module.render;

import me.mioclient.util.math.ColorUtils;

import me.mioclient.util.math.ColorUtils;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import me.mioclient.ducks.DuckParticle;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.render.AddParticleEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.Module;
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.WhiteAshParticle;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Particles.class */
public class Particles extends Module {
    public final Random random;
    public Setting<Boolean> totems = add(new BooleanSetting("Totems", false).setVisible());
    public Setting<Float> totemsScale = add(new NumberSetting<>("TotemsScale", 1.0f, 0.0f, 2.0f).requiresSettings(this.totems));
    public Setting<Float> totemsVelocity = add(new NumberSetting<>("TotemsVelocity", 1.0f, 0.0f, 5.0f).requiresSettings(this.totems));
    public Setting<Color> totemsColor1 = add(new ColorSetting("TotemsColor1", new Color(-4351489, true)).setThemeSync(true).requiresSettings(this.totems));
    public Setting<Color> totemsColor2 = add(new ColorSetting("TotemsColor2", new Color(-9286761, true)).setThemeSync(true).requiresSettings(this.totems));
    public Setting<Boolean> ignoreSelf = add(new BooleanSetting("IgnoreSelf", false).requiresSettings(this.totems));
    public Setting<Boolean> rockets = add(new BooleanSetting("Rockets", false).setVisible());
    public Setting<Float> rocketScale = add(new NumberSetting<>("RocketScale", 1.0f, 0.0f, 2.0f).requiresSettings(this.rockets));
    public Setting<Color> rocketColor = add(new ColorSetting("RocketColor", new Color(-4351489, true)).setThemeSync(true).requiresSettings(this.rockets));
    public Setting<Boolean> damage = add(new BooleanSetting("Damage", false).setVisible());
    public Setting<Float> damageScale = add(new NumberSetting<>("DamageScale", 1.0f, 0.0f, 2.0f).requiresSettings(this.damage));
    public Setting<Float> damageVelocity = add(new NumberSetting<>("DamageVelocity", 1.0f, 0.0f, 5.0f).requiresSettings(this.damage));
    public Setting<Color> damageColor = add(new ColorSetting("DamageColor", new Color(-4351489, true)).setThemeSync(true).requiresSettings(this.damage));
    public Setting<Boolean> portal = add(new BooleanSetting("Portal", false).setVisible());
    public Setting<Float> portalScale = add(new NumberSetting<>("PortalScale", 1.0f, 0.0f, 2.0f).requiresSettings(this.portal));
    public Setting<Color> portalColor = add(new ColorSetting("PortalColor", new Color(-4351489, true)).setThemeSync(true).requiresSettings(this.portal));
    public Setting<Boolean> dust = add(new BooleanSetting("Dust", false).setVisible());
    public Setting<Color> from = add(new ColorSetting("From", new Color(-4351489, true)).requiresSettings(this.dust));
    public Setting<Color> to = add(new ColorSetting("To", new Color(-4351489, true)).requiresSettings(this.dust));

    public Particles() {
        super("Particles", "Allows you to adjust certain particles.", Category.RENDER, new String[0]);
        this.random = new Random();
        setDrawn(false);
    }

    @Listen
    public void onAddParticle(AddParticleEvent addParticleEvent) {
        TotemParticle particle982 = (addParticleEvent.getParticle()) instanceof TotemParticle ? (TotemParticle) (addParticleEvent.getParticle()) : null;
        if (particle982 instanceof TotemParticle) {
            TotemParticle totemParticle = particle982;
            if (this.ignoreSelf.getValue().booleanValue()) {
                if (minecraftClient.player.getBoundingBox().intersects(totemParticle.getBoundingBox())) {
                    addParticleEvent.cancel();
                }
            }
            if (!this.totems.getValue().booleanValue()) {
                return;
            }
            if (this.random.nextInt(4) == 0) {
                applyColor((Particle) totemParticle, this.totemsColor1.getValue().hashCode());
            } else {
                applyColor((Particle) totemParticle, this.totemsColor2.getValue().hashCode());
            }
            totemParticle.scale(this.totemsScale.getValue().floatValue());
            totemParticle.move(this.totemsVelocity.getValue().floatValue());
        }
        if (this.rockets.getValue().booleanValue()) {
            FireworksSparkParticle.FireworkParticle particle9822 = (addParticleEvent.getParticle()) instanceof FireworksSparkParticle.FireworkParticle ? (FireworksSparkParticle.FireworkParticle) (addParticleEvent.getParticle()) : null;
            if (particle9822 instanceof FireworksSparkParticle.FireworkParticle) {
                FireworksSparkParticle.FireworkParticle fireworkParticle = particle9822;
                applyColor((Particle) fireworkParticle, this.rocketColor.getValue().hashCode());
                fireworkParticle.scale(this.rocketScale.getValue().floatValue());
            }
        }
        if (this.rockets.getValue().booleanValue()) {
            FireworksSparkParticle.Explosion particle9823 = (addParticleEvent.getParticle()) instanceof FireworksSparkParticle.Explosion ? (FireworksSparkParticle.Explosion) (addParticleEvent.getParticle()) : null;
            if (particle9823 instanceof FireworksSparkParticle.Explosion) {
                FireworksSparkParticle.Explosion explosion = particle9823;
                applyColor((Particle) explosion, this.rocketColor.getValue().hashCode());
                explosion.scale(this.rocketScale.getValue().floatValue());
            }
        }
        if (this.damage.getValue().booleanValue()) {
            DamageParticle particle9824 = (addParticleEvent.getParticle()) instanceof DamageParticle ? (DamageParticle) (addParticleEvent.getParticle()) : null;
            if (particle9824 instanceof DamageParticle) {
                DamageParticle damageParticle = particle9824;
                applyColor((Particle) damageParticle, this.damageColor.getValue().hashCode());
                damageParticle.scale(this.damageScale.getValue().floatValue());
                damageParticle.move(this.damageVelocity.getValue().floatValue());
            }
        }
        if (this.portal.getValue().booleanValue()) {
            PortalParticle particle9825 = (addParticleEvent.getParticle()) instanceof PortalParticle ? (PortalParticle) (addParticleEvent.getParticle()) : null;
            if (particle9825 instanceof PortalParticle) {
                PortalParticle portalParticle = particle9825;
                applyColor((Particle) portalParticle, this.portalColor.getValue().hashCode());
                portalParticle.scale(this.portalScale.getValue().floatValue());
            }
        }
        if (this.dust.getValue().booleanValue()) {
            Particle particle9826 = addParticleEvent.getParticle();
            if (particle9826 instanceof AscendingParticle) {
                Particle particle = (AscendingParticle) particle9826;
                if (particle instanceof WhiteAshParticle) {
                    applyColor(particle, this.to.getValue().brighter().hashCode());
                }
                if (particle instanceof AshParticle) {
                    applyColor(particle, ColorUtils.lerpColor(this.from.getValue(), this.to.getValue(), ThreadLocalRandom.current().nextFloat()).hashCode());
                }
            }
        }
    }

    public void applyColor(Particle particle, int i) {
        float intBitsToFloat = (i >> 24) / Float.intBitsToFloat(1132396544);
        particle.setColor(((i & 16711680) >> 16) / Float.intBitsToFloat(1132396544), ((i & 65280) >> 8) / Float.intBitsToFloat(1132396544), (i & 255) / Float.intBitsToFloat(1132396544));
        ((DuckParticle) particle).mio$setInitialAlpha(intBitsToFloat);
    }
}
