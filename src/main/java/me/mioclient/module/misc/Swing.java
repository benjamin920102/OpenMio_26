package me.mioclient.module.misc;

import me.mioclient.api.NamedValue;

import me.mioclient.mode.combat.ScaffoldHelperMode;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import net.minecraft.world.InteractionHand;
import me.mioclient.module.combat.Mainhand;
import me.mioclient.module.combat.Offhand;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class Swing extends Module {
    public Setting<ScaffoldHelperMode> hand = add(new EnumSetting<>("InteractionHand", ScaffoldHelperMode.scaffoldHelperMode));
    public Setting<MixinLivingEntityMode> type = add(new EnumSetting<>("Type", MixinLivingEntityMode.VANILLA));
    public Setting<Double> speed = add(new NumberSetting<>("Speed", 1.0, 0.1, 3.0));

    
    public enum MixinLivingEntityMode implements NamedValue {
        VANILLA("Vanilla"),
        ONE_EIGHT("1.8"),
        ONE_TWELVE("1.12");

        public final String name;

        MixinLivingEntityMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    
    public static enum ScaffoldHelperMode implements NamedValue {
        scaffoldHelperMode("Mainhand") {
            @Override // me.mioclient.module.misc.Swing.ScaffoldHelperMode
            public InteractionHand getHand() {
                return InteractionHand.MAIN_HAND;
            }
        },
        scaffoldHelperMode2("Offhand") {
            @Override // me.mioclient.module.misc.Swing.ScaffoldHelperMode
            public InteractionHand getHand() {
                return InteractionHand.OFF_HAND;
            }
        },
        PACKET("Packet"),
        VANILLA("Vanilla");

        public final String name;

        ScaffoldHelperMode(String str2) {
            this.name = str2;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public InteractionHand getHand() {
            return null;
        }
    }

    public Swing() {
        super("Swing", "Changes your swinging hand.", Category.MISC, new String[0]);
    }
}