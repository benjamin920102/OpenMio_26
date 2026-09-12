package me.mioclient.module.combat;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;

import java.text.DecimalFormat;
import java.util.Iterator;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.render.RenderEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.exploit.IllegalDisconnect;
import me.mioclient.module.misc.AutoReconnect;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/AutoLog.class */
public class AutoLog extends Module {
    public static final IllegalDisconnect illegaldisconnect = (IllegalDisconnect) ClientServices.moduleInjector.getModule(IllegalDisconnect.class);
    public static AutoReconnect autoreconnect = (AutoReconnect) ClientServices.moduleInjector.getModule(AutoReconnect.class);
    public Setting<Integer> health = add(new NumberSetting<>("Health", 12, 1, 20).setSuffix("hp"));
    public Setting<Float> invincTimeout = add(new NumberSetting<>("InvincTimeout", 0.0f, 0.0f, 4.0f).setSuffix("s"));
    public Setting<Boolean> autoDisable = add(new BooleanSetting("AutoDisable", true));
    public Setting<Boolean> gearedOnly = add(new BooleanSetting("GearedOnly", false));
    public Setting<Boolean> unfocusedOnly = add(new BooleanSetting("UnfocusedOnly", false));
    public Setting<Boolean> onRender = add(new BooleanSetting("OnRender", false));
    public Setting<Boolean> crystal = add(new BooleanSetting("Crystal", false));
    public Setting<Boolean> tNTMinecart = add(new BooleanSetting("TNTMinecart", false));
    public Setting<Boolean> totems = add(new BooleanSetting("Totems", false).setVisible());
    public Setting<Integer> totemCount = add(new NumberSetting<>("TotemCount", 1, 0, 16, v -> this.totems.isShown()));
    public Setting<Boolean> elytra = add(new BooleanSetting("Elytra", false).setVisible());
    public Setting<Integer> durability = add(new NumberSetting<>("Durability", 5, 1, 100).setSuffix("%").requiresSettings(this.elytra));
    public Setting<Boolean> assumeInventory = add(new BooleanSetting("AssumeInventory", false).requiresSettings(this.elytra));
    public Setting<Boolean> yLevel = add(new BooleanSetting("YLevel", false).setVisible());
    public Setting<Float> y = add(new NumberSetting<>("Y", 64.0f, -64.0f, 300.0f).setSuffix("m").requiresSettings(this.yLevel));
    public Runnable runnable;
    public final Stopwatch stopwatch;

    public AutoLog() {
        super("AutoLog", "Logs you out so you don't have to fight anyone.", Category.COMBAT, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.runnable = null;
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return new DecimalFormat("0.0").format(this.health.getValue());
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (isNotInGame() || minecraftClient.player.isSpectator() || minecraftClient.player.isInCreativeMode()) {
            return;
        }
        if (!this.gearedOnly.getValue().booleanValue() || PlayerUtils.hasArmor(minecraftClient.player)) {
            if (this.elytra.getValue().booleanValue() && isElytraLowDurability()) {
                triggerDisconnect("Your elytra durability is too low!");
                return;
            }
            if (this.yLevel.getValue().booleanValue() && minecraftClient.player.getY() < this.y.getValue().floatValue()) {
                triggerDisconnect(new StringTemplate().arg(String.valueOf(this.y.getValue())).format("You are below Y level \u0001"));
                return;
            }
            if (minecraftClient.player.age / Float.intBitsToFloat(1101004800) < this.invincTimeout.getValue().floatValue()) {
                return;
            }
            if (this.onRender.getValue().booleanValue()) {
                for (PlayerEntity playerEntity : minecraftClient.world.getPlayers()) {
                    if (!ClientServices.socialManager.isFriend(playerEntity.getName().getString()) && playerEntity != minecraftClient.player) {
                        triggerDisconnect(new StringTemplate().arg(playerEntity.getName().getString()).format("\u0001 has entered your render distance!"));
                    }
                }
            }
            if (!minecraftClient.world.getEntitiesByClass(TntMinecartEntity.class, minecraftClient.player.getBoundingBox().expand(Double.longBitsToDouble(4616189618054758400L), Double.longBitsToDouble(4616189618054758400L), Double.longBitsToDouble(4616189618054758400L)), (v0) -> {
                return v0.isPrimed();
            }).isEmpty() && this.tNTMinecart.getValue().booleanValue()) {
                triggerDisconnect("You are at risk of getting blown up by a TNT minecart!");
            }
            if (this.crystal.getValue().booleanValue()) {
                minecraftClient.world.getEntities().forEach(entity -> {
                    if (!(entity instanceof EndCrystalEntity) || minecraftClient.player.squaredDistanceTo(entity) > Double.longBitsToDouble(4638707616191610880L)) {
                        return;
                    }
                    Vec3d pos = entity.getPos();
                    LivingEntity livingEntity = minecraftClient.player;
                    if (ExplosionDamageCalculator.getExplosionDamage(pos, livingEntity, minecraftClient.player.getBoundingBox(), Double.longBitsToDouble(4618441417868443648L), true, (BlockPos) null, (BlockPos) null) >= EntityUtil.getPlayerHealth()) {
                        triggerDisconnect("You are at risk of getting blown up by an end crystal!");
                    }
                });
            }
            int i = (int) EntityUtil.getPlayerHealth();
            if (minecraftClient.player.isDead() || i == 0) {
                return;
            }
            if (minecraftClient.isWindowFocused() && this.unfocusedOnly.getValue().booleanValue()) {
                return;
            }
            int sum = minecraftClient.player.getInventory().main.stream().filter(itemStack -> {
                return itemStack.getItem() == Items.TOTEM_OF_UNDYING;
            }).mapToInt((v0) -> {
                return v0.getCount();
            }).sum();
            if (minecraftClient.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
                sum += minecraftClient.player.getOffHandStack().getCount();
            }
            boolean z = sum <= this.totemCount.getValue().intValue();
            if (!this.totems.getValue().booleanValue()) {
                z = true;
            }
            if (i > this.health.getValue().intValue() || !z) {
                return;
            }
            triggerDisconnect(new StringTemplate().arg(i).format("Your health is too low! (\u0001)."));
        }
    }

    @Listen
    public void onRender(RenderEvent renderEvent) {
        if (isNotInGame()) {
            this.runnable = null;
        }
        if (this.runnable == null || !this.stopwatch.hasElapsedMs(1500L)) {
            return;
        }
        this.runnable.run();
        this.runnable = null;
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof DisconnectS2CPacket) {
            this.runnable = null;
        }
    }

    public boolean isElytraLowDurability() {
        ItemStack armorStack = minecraftClient.player.getInventory().getArmorStack(EquipmentSlot.CHEST.getEntitySlotId());
        if (!armorStack.isOf(Items.ELYTRA) || !armorStack.isDamageable()) {
            return false;
        }
        if (InventoryUtils.getDurabilityPercent(armorStack) > ((float) this.durability.getValue().intValue())) {
            return false;
        }
        if (!this.assumeInventory.getValue().booleanValue()) {
            return true;
        }
        Iterator it = minecraftClient.player.getInventory().main.iterator();
        while (it.hasNext()) {
            ItemStack itemStack = (ItemStack) it.next();
            if (itemStack.isOf(Items.ELYTRA) && (!itemStack.isDamageable() || InventoryUtils.getDurabilityPercent(itemStack) > this.durability.getValue().intValue())) {
                return false;
            }
        }
        return true;
    }

    public void triggerDisconnect(String str) {
        if (this.autoDisable.getValue().booleanValue()) {
            disable();
        }
        ClientConnection connection = minecraftClient.player.networkHandler.getConnection();
        if (connection != null) {
            ClientServices.holeSnapRenderHelper.flag3 = true;
            autoreconnect.disable();
            Runnable runnable = () -> {
                connection.disconnect(Text.empty().append(Text.of(new StringTemplate().arg(str).arg(String.valueOf(Formatting.RED)).arg(String.valueOf(Formatting.GRAY)).format("\u0001[AutoLog] \u0001\u0001"))));
            };
            boolean z = (illegaldisconnect.ignoreProxy.getValue().booleanValue() && IllegalDisconnect.isKnownProxy()) ? false : true;
            if (!illegaldisconnect.isToggled() || !z) {
                runnable.run();
                return;
            }
            IllegalDisconnect.sendDisconnectPacket();
            if (this.runnable == null) {
                this.stopwatch.reset();
            }
            this.runnable = runnable;
        }
    }
}
