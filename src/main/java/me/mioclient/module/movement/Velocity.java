package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.NamedValue;
import me.mioclient.module.movement.velocity.AntiCheatVelocityHandler;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.registry.ModeValueMap;

import me.mioclient.api.ClientServices;
import me.mioclient.module.movement.velocity.AntiCheatVelocityHandler;
import me.mioclient.util.registry.ModeValueMap;
import me.mioclient.api.NamedValue;
import me.mioclient.module.movement.velocity.GrimVelocityHandler;
import me.mioclient.module.movement.velocity.PlainVelocityHandler;
import me.mioclient.module.movement.velocity.VelocityModeHandler;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.math.MathUtils;

import me.mioclient.util.text.StringTemplate;
import me.mioclient.render.RenderConstants;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.PushOutOfBlocksEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/Velocity.class */
public class Velocity extends Module {
    public static final Sprint sprint = (Sprint) ClientServices.moduleInjector.getModule(Sprint.class);
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Setting<VelocityMode> mode = add(new EnumSetting<>("Mode", VelocityMode.PLAIN));
    public Setting<Integer> horizontal = add(new NumberSetting<>("Horizontal", 0, 0, 100, v -> this.mode.getValue() == Velocity.VelocityMode.PLAIN).setSuffix("%"));
    public Setting<Integer> vertical = add(new NumberSetting<>("Vertical", 0, 0, 100, v -> this.mode.getValue() == Velocity.VelocityMode.PLAIN).setSuffix("%"));
    public Setting<Boolean> inverse = add(new BooleanSetting("Inverse", false, v -> this.mode.getValue() == Velocity.VelocityMode.PLAIN));
    public Setting<Boolean> explosions = add(new BooleanSetting("Explosions", true));
    public Setting<Boolean> push = add(new BooleanSetting("Push", true));
    public Setting<Boolean> liquids = add(new BooleanSetting("Liquids", true));
    public final ModeValueMap<VelocityMode, VelocityModeHandler> elytraFlyHelper_4;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/Velocity$VelocityMode.class */
    public enum VelocityMode implements NamedValue {
        PLAIN("Plain"),
        GRIM("Grim"),
        WALLS("Walls");

        public final String name;

        VelocityMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public Velocity() {
        super("Velocity", "Cancels all the pushing you receive.", Category.MOVEMENT, new String[0]);
        this.elytraFlyHelper_4 = new ModeValueMap<>(this.mode);
        ModeValueMap elytraFlyHelper_4 = this.elytraFlyHelper_4;
        elytraFlyHelper_4.put(VelocityMode.PLAIN, new PlainVelocityHandler(this));
        ModeValueMap elytraFlyHelper_42 = this.elytraFlyHelper_4;
        elytraFlyHelper_42.put(VelocityMode.GRIM, new GrimVelocityHandler(this));
        ModeValueMap elytraFlyHelper_43 = this.elytraFlyHelper_4;
        elytraFlyHelper_43.put(VelocityMode.WALLS, new AntiCheatVelocityHandler(this));
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        if (this.mode.getValue() != VelocityMode.PLAIN) {
            return ClientTextRenderer.capitalize(this.mode.getValue().getName());
        }
        return new StringTemplate().arg(MathUtils.roundFloat(this.vertical.getValue().intValue(), 1)).arg(MathUtils.roundFloat(this.horizontal.getValue().intValue(), 1)).format("H\u0001% V\u0001%");
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        sprint.flag2 = false;
        this.elytraFlyHelper_4.getCurrent().onDisable();
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        this.elytraFlyHelper_4.getCurrent().handlePacketSend(sendImmediatelyEvent);
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        this.elytraFlyHelper_4.getCurrent().handlePacketReceive(channelRead0Event);
    }

    @Listen(get219= RenderConstants.num5)
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (this.mode.getValue() != VelocityMode.WALLS) {
            sprint.flag2 = false;
        }
        this.elytraFlyHelper_4.getCurrent().onUpdate();
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        this.elytraFlyHelper_4.getCurrent().onInteractBlock(interactBlockEvent);
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        this.elytraFlyHelper_4.getCurrent().handleMotion(motionEvent);
    }

    @Listen
    public void onExplosionVelocity(ExplosionVelocityEvent explosionVelocityEvent) {
        this.elytraFlyHelper_4.getCurrent().handleExplosionVelocity(explosionVelocityEvent);
    }

    @Listen
    public void onPushOutOfBlocks(PushOutOfBlocksEvent pushOutOfBlocksEvent) {
        if (this.push.getValue().booleanValue()) {
            pushOutOfBlocksEvent.cancel();
        }
    }

    public boolean isPacketVelocity() {
        if (!isToggled()) {
            return false;
        }
        if ((this.elytraFlyHelper_4.getCurrent() instanceof PlainVelocityHandler) && this.horizontal.getValue().intValue() == 0 && this.vertical.getValue().intValue() == 0) {
            return true;
        }
        VelocityModeHandler object996 = this.elytraFlyHelper_4.getCurrent();
        return (object996 instanceof GrimVelocityHandler) && !((GrimVelocityHandler) object996).isGrimBypassDisabled();
    }

    public VelocityModeHandler getVelocityHelper1894() {
        return this.elytraFlyHelper_4.getCurrent();
    }
}
