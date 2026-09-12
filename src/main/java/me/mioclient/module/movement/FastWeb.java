package me.mioclient.module.movement;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.network.PacketSender;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.render.RenderConstants;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.SprintUpdateEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/FastWeb.class */
public class FastWeb extends Module {
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Setting<FastWebPredicateMode> mode = add(new EnumSetting<>("Mode", FastWebPredicateMode.PLAIN));
    public Setting<Boolean> horizontal = add(new BooleanSetting("Horizontal", false, v -> this.mode.getValue() != FastWeb.FastWebPredicateMode.GRIM).setVisible());
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 1.0f, 1.0f, 2.0f));
    public Setting<Boolean> vertical = add(new BooleanSetting("Vertical", false, v -> this.mode.getValue() != FastWeb.FastWebPredicateMode.GRIM).setVisible());
    public Setting<Float> vSpeed = add(new NumberSetting<>("VSpeed", 1.0f, 1.0f, 2.0f));
    public Setting<Boolean> upwards = add(new BooleanSetting("Upwards", false));
    public boolean flag;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/FastWeb$FastWebPredicateMode.class */
    public enum FastWebPredicateMode implements NamedValue {
        PLAIN("Plain"),
        STRICT("Strict"),
        LAGBACK("LagBack"),
        GRIM("Grim");

        public final String name;

        FastWebPredicateMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public FastWeb() {
        super("FastWeb", "Makes you faster in cobwebs.", Category.MOVEMENT, new String[0]);
        this.flag = false;
        setDrawn(false);
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() != KeyPearlMode.Pre) {
            return;
        }
        Vec3d vec3d = new Vec3d(minecraftClient.player.getX() - minecraftClient.player.prevX, minecraftClient.player.getY() - minecraftClient.player.prevY, minecraftClient.player.getZ() - minecraftClient.player.prevZ);
        if (isGrimMode() && vec3d.lengthSquared() > 0.0d) {
            BlockPos.stream(minecraftClient.player.getBoundingBox()).map((v0) -> {
                return v0.toImmutable();
            }).filter(blockPos -> {
                return minecraftClient.world.getBlockState(blockPos).isOf(Blocks.COBWEB);
            }).forEach(blockPos2 -> {
                if (!antiCheat.isF2b2t()) {
                    PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, blockPos2, Direction.UP);
                }
                PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos2, Direction.UP);
            });
        }
        if (this.mode.getValue() == FastWebPredicateMode.GRIM && minecraftClient.player.input.sneaking && !minecraftClient.player.isOnGround() && PlayerUtils.isInCobweb(minecraftClient.player)) {
            motionEvent.setY(minecraftClient.player.getY() + Math.random() + Double.longBitsToDouble(4607182418800017408L));
        }
    }

    @Listen(get219= RenderConstants.num4)
    public void onSprintUpdate(SprintUpdateEvent sprintUpdateEvent) {
        if (isGrimMode() && !minecraftClient.player.isOnGround() && minecraftClient.player.input.jumping && PlayerUtils.isInCobweb(minecraftClient.player)) {
            minecraftClient.player.setSprinting(false);
        }
    }

    @Listen
    public void onMove(MoveEvent moveEvent) {
        if (!ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(500L) || !PlayerUtils.isInCobweb(minecraftClient.player) || this.mode.getValue() == FastWebPredicateMode.GRIM) {
            reset();
            return;
        }
        if (this.vertical.getValue().booleanValue()) {
            if (!minecraftClient.player.input.sneaking || minecraftClient.player.isOnGround()) {
                reset();
            } else if (isStrictMode()) {
                this.flag = true;
                ClientServices.inner.requestTickRate(this, this.vSpeed.getValue().floatValue() * Float.intBitsToFloat(1086324736));
            } else {
                reset();
                minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, -this.vSpeed.getValue().floatValue()));
                moveEvent.setY(-this.vSpeed.getValue().floatValue());
            }
            if (this.upwards.getValue().booleanValue() && minecraftClient.player.input.jumping && !isStrictMode()) {
                minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, this.vSpeed.getValue().floatValue()));
                moveEvent.setY(this.vSpeed.getValue().floatValue());
            }
        }
        if (MovementUtils.is2181() && this.horizontal.getValue().booleanValue()) {
            double[] doubleArray2507 = MovementUtils.applyVelocity(moveEvent, MovementUtils.getBaseMoveSpeed(true) * this.speed.getValue().floatValue());
            minecraftClient.player.setVelocity(doubleArray2507[0], minecraftClient.player.getVelocity().y, doubleArray2507[1]);
        }
    }

    public boolean isStrictMode() {
        return this.mode.getValue() == FastWebPredicateMode.STRICT;
    }

    public boolean isTickRateActive() {
        return this.flag;
    }

    public void reset() {
        ClientServices.inner.releaseTickRate(this);
        this.flag = false;
    }

    public boolean isGrimMode() {
        return isToggled() && this.mode.getValue() == FastWebPredicateMode.GRIM;
    }
}
