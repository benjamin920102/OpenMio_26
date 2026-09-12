package me.mioclient.module.movement;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import java.util.concurrent.TimeUnit;
import me.mioclient.render.RenderConstants;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.events.interaction.InteractItemEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.sound.PlayEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.mixin.ducks.DuckFireworkEntity;
import me.mioclient.module.Module;
import me.mioclient.module.exploit.RocketExtender;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/Fireworks.class */
public final class Fireworks extends Module {
    public static final RocketExtender rocketExtender = (RocketExtender) ClientServices.moduleInjector.getModule(RocketExtender.class);
    public static final ElytraFly elytraFly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class);
    public Setting<Boolean> antiWaste = add(new BooleanSetting("AntiWaste", true));
    public Setting<Boolean> boost = add(new BooleanSetting("Boost", false).setVisible());
    public Setting<Boolean> withElytraFly = add(new BooleanSetting("WithElytraFly", true).requiresSettings(this.boost));
    public Setting<Float> boostFactor = add(new NumberSetting<>("BoostFactor", 0.0f, 0.0f, 3.0f).requiresSettings(this.boost));
    public Setting<Boolean> autoLaunch = add(new BooleanSetting("AutoLaunch", false).setVisible());
    public Setting<Boolean> onlyElytraFly = add(new BooleanSetting("OnlyElytraFly", true).requiresSettings(this.autoLaunch));
    public Setting<Boolean> onlyFirst = add(new BooleanSetting("OnlyFirst", false).requiresSettings(this.autoLaunch));
    public Setting<Boolean> await = add(new BooleanSetting("Await", false).requiresSettings(this.autoLaunch));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 0.0f, 0.0f, 5.0f).setSuffix("s").setHoleSnapConfig("Auto", HoleSnapMode.MIN).requiresSettings(this.autoLaunch));
    public Setting<Boolean> fastLaunch = add(new BooleanSetting("FastLaunch", false).setVisible());
    public Setting<Integer> ticks = add(new NumberSetting<>("Ticks", 10, 3, 40).requiresSettings(this.fastLaunch));
    public Setting<Boolean> flight = add(new BooleanSetting("Flight", false).setVisible().requiresSettings(this.fastLaunch));
    public Setting<Float> horizontal = add(new NumberSetting<>("Horizontal", 1.0f, 0.0f, 3.0f).requiresSettings(this.fastLaunch, this.flight));
    public Setting<Float> vertical = add(new NumberSetting<>("Vertical", 0.2f, 0.0f, 1.0f).requiresSettings(this.fastLaunch, this.flight));
    public final Stopwatch stopwatch;
    public boolean flag;
    public int num;
    public int num2;
    public boolean flag2;
    public boolean flag3;

    public Fireworks() {
        super("Fireworks", "Enhances the usage of firework rockets.", Category.MOVEMENT, "rockets");
        this.stopwatch = new Stopwatch();
        this.delay.setHoleSnapConfig("Auto", HoleSnapMode.MIN);
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if (this.antiWaste.getValue().booleanValue()) {
            if (minecraftClient.player.getStackInHand(interactBlockEvent.getHand()).isOf(Items.FIREWORK_ROCKET)) {
                minecraftClient.interactionManager.interactItem(minecraftClient.player, interactBlockEvent.getHand());
                minecraftClient.player.swingHand(interactBlockEvent.getHand());
                interactBlockEvent.cancel();
            }
        }
    }

    @Listen
    public void onInteractItem(InteractItemEvent interactItemEvent) {
        if (shouldFastLaunch()) {
            if (minecraftClient.player.isHolding(Items.FIREWORK_ROCKET)) {
                this.num2 = 0;
                swapElytraAndFly();
                this.num = this.ticks.getValue().intValue();
                this.flag3 = true;
            }
        }
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (this.flag2) {
            minecraftClient.player.setSneaking(false);
            PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY, 0);
            this.flag2 = false;
            if (!PlayerUtils.hasElytraEquipped(minecraftClient.player)) {
                minecraftClient.player.stopFallFlying();
            }
        }
        if (this.num > 0) {
            swapElytraAndFly();
            this.num--;
            this.flag2 = this.num == 0;
        }
        if (this.autoLaunch.getValue().booleanValue()) {
            if (elytraFly.isToggled() || !this.onlyElytraFly.getValue().booleanValue()) {
                if (!minecraftClient.player.isFallFlying()) {
                    this.stopwatch.setTime(-1L);
                }
                boolean z = (this.stopwatch.hasElapsed((double) this.delay.getValue().floatValue(), TimeUnit.SECONDS) && (!rocketExtender.isToggled() || rocketExtender.num == -1 || rocketExtender.blockPos == null)) ? false : true;
                if (hasActiveFirework()) {
                    z = true;
                }
                if (z) {
                    return;
                }
                if (!minecraftClient.player.isFallFlying()) {
                    this.flag = false;
                }
                if (this.flag && this.onlyFirst.getValue().booleanValue()) {
                    return;
                }
                autoUseFirework();
            }
        }
    }

    @Listen(get219= RenderConstants.num4)
    public void onMove(MoveEvent moveEvent) {
        if (this.flight.getValue().booleanValue() && this.num > 1 && hasActiveFirework()) {
            do138(moveEvent);
        }
        if (minecraftClient.player.isFallFlying() && this.await.getValue().booleanValue() && this.autoLaunch.getValue().booleanValue() && !hasActiveFirework()) {
            moveEvent.setMovement(new Vec3d(0.0d, 0.0d, 0.0d));
        }
    }

    @Listen
    public void onPlay(PlayEvent playEvent) {
        if (this.num <= 0 || playEvent.getSoundInstance() == null || playEvent.getSoundInstance().getId() == null || !playEvent.getSoundInstance().getId().toString().contains("item.armor.equip")) {
            return;
        }
        playEvent.cancel();
    }

    public void do138(MoveEvent moveEvent) {
        float intBitsToFloat = Float.intBitsToFloat(-1165815185);
        if (minecraftClient.player.input.jumping) {
            intBitsToFloat = this.vertical.getValue().floatValue();
        } else if (minecraftClient.player.input.sneaking) {
            intBitsToFloat = -this.vertical.getValue().floatValue();
        }
        float floatValue = this.horizontal.getValue().floatValue();
        if (floatValue > Double.longBitsToDouble(4609884578576439706L)) {
            floatValue = MathHelper.clamp(Float.intBitsToFloat(1070386381) + (this.num2 * Float.intBitsToFloat(1036831949)), Float.intBitsToFloat(1070386381), floatValue);
            this.num2++;
        }
        double[] doubleArray2507 = MovementUtils.applyVelocity(moveEvent, floatValue);
        Vec3d vec3d = new Vec3d(doubleArray2507[0], intBitsToFloat, doubleArray2507[1]);
        minecraftClient.player.setVelocity(vec3d);
        moveEvent.setMovement(vec3d);
    }

    public void autoUseFirework() {
        if (this.autoLaunch.getValue().booleanValue() && minecraftClient.player.isFallFlying()) {
            Hand hand450 = InventoryUtils.getHandWith(Items.FIREWORK_ROCKET);
            int i = minecraftClient.player.getInventory().selectedSlot;
            int i2 = InventoryUtils.findSlot(Items.FIREWORK_ROCKET);
            int i3 = InventoryUtils.findInHotbar(Items.FIREWORK_ROCKET);
            if (hand450 != null) {
                minecraftClient.interactionManager.interactItem(minecraftClient.player, hand450);
                this.stopwatch.reset();
            } else if (i2 != -1) {
                boolean z = i3 == -1;
                equipSlot(z ? i2 : i3, z);
                minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.MAIN_HAND);
                rocketExtender.vec3d = minecraftClient.player.getPos();
                equipSlot(z ? i2 : i, z);
                this.stopwatch.reset();
            }
            this.flag = true;
        }
    }

    public void equipSlot(int i, boolean z) {
        if (z) {
            InventoryUtils.swapToHotbar(i);
        } else {
            InventoryUtils.selectSlot(i);
        }
    }

    public boolean hasActiveFirework() {
        if (!this.stopwatch.hasElapsedMs(500L)) {
            return true;
        }
        for (Entity entity : minecraftClient.world.getEntities()) {
            if ((entity instanceof DuckFireworkEntity) && ((DuckFireworkEntity) entity).mio$getShooter() == minecraftClient.player) {
                return true;
            }
        }
        return false;
    }

    public float getBoostFactor(boolean z) {
        if (!isToggled() || !this.boost.getValue().booleanValue()) {
            return 0.0f;
        }
        if ((z || this.withElytraFly.getValue().booleanValue()) && hasActiveFirework()) {
            return this.boostFactor.getValue().floatValue();
        }
        return 0.0f;
    }

    public int getRemainingTicks() {
        return this.num;
    }

    public boolean shouldFastLaunch() {
        return (PlayerUtils.hasElytraEquipped(minecraftClient.player) || !isToggled() || !this.fastLaunch.getValue().booleanValue() || minecraftClient.player.isOnGround() || InventoryUtils.findSlot(Items.ELYTRA) == -1) ? false : true;
    }

    public void swapElytraAndFly() {
        int i = InventoryUtils.findInHotbar(Items.ELYTRA);
        boolean z = i == -1;
        if (i == -1) {
            i = InventoryUtils.findSlot(Items.ELYTRA);
        }
        if (i == -1) {
            return;
        }
        int i2 = i;
        if (z) {
            i = 0;
            minecraftClient.interactionManager.clickSlot(0, i2, 0, SlotActionType.SWAP, minecraftClient.player);
        }
        minecraftClient.interactionManager.clickSlot(0, 6, i, SlotActionType.SWAP, minecraftClient.player);
        PacketSender.sendStartFallFlying();
        minecraftClient.player.startFallFlying();
        minecraftClient.interactionManager.clickSlot(0, 6, i, SlotActionType.SWAP, minecraftClient.player);
        if (z) {
            minecraftClient.interactionManager.clickSlot(0, i2, 0, SlotActionType.SWAP, minecraftClient.player);
        }
    }
}
