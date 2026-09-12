package me.mioclient.module.combat;

import me.mioclient.event.events.player.RotationTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.PotionResolver;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.inventory.PotionResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.event.events.player.RotationTickEvent;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.StopUsingItemEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import me.mioclient.ducks.DuckStatusEffectInstance;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/Arrows.class */
public class Arrows extends Module {
    public static final AtomicBoolean atomicBoolean = new AtomicBoolean();
    public Setting<Boolean> autoShoot = add(new BooleanSetting("AutoShoot", false));
    public Setting<Float> fov = add(new NumberSetting<>("Fov", 20.0f, 10.0f, 45.0f, v -> !this.autoShoot.getValue().booleanValue()));
    public Setting<Integer> assumeDuration = add(new NumberSetting<>("AssumeDuration", 50, 0, 100).setSuffix("%"));
    public final Map<StatusEffect, Long> map;
    public final Stopwatch stopwatch;
    public int num;
    public Potion potion;

    public Arrows() {
        super("Arrows", "Will swap between effect arrows in your inventory.", Category.COMBAT, "projectiles");
        this.map = new HashMap();
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (atomicBoolean.get()) {
            disable();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x008f, code lost:
    
        if (scoreArrow(me.mioclient.module.combat.Arrows.minecraftClient.player.getInventory().getStack(r5.num), r0) <= 0) goto L13;
     */
    @Listen
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onStopUsingItem(StopUsingItemEvent stopUsingItemEvent) {
        PlayerEntity playerEntity1934 = findClosestTargetInFov();
        Hand activeHand = minecraftClient.player.getActiveHand();
        if (activeHand == null || !(minecraftClient.player.getStackInHand(activeHand).getItem() instanceof RangedWeaponItem)) {
            return;
        }
        if (playerEntity1934 != null) {
            if (this.num != -1) {
            }
            int i = minecraftClient.player.getInventory().selectedSlot;
            InventoryUtils.selectSlot((i + 1) % 9);
            PacketSender.sendPlayerAction(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN);
            InventoryUtils.selectSlot(i);
            stopUsingItemEvent.cancel();
            return;
        }
        if (this.stopwatch.hasElapsedMs(50L)) {
            for (int i2 = 0; i2 < minecraftClient.player.getInventory().size(); i2++) {
                if (minecraftClient.player.getInventory().getStack(i2).getItem() instanceof ArrowItem) {
                    if (i2 != this.num) {
                        this.stopwatch.reset();
                        ClientServices.fireworksHelper.setSyncBatching(true);
                        InventoryUtils.swapStacks(InventoryUtils.toNetworkSlot(this.num), InventoryUtils.toNetworkSlot(i2));
                        ClientServices.fireworksHelper.setSyncBatching(false);
                        this.num = -1;
                    }
                    if (this.potion == null) {
                        return;
                    }
                    Iterator it = this.potion.getEffects().iterator();
                    while (it.hasNext()) {
                        this.map.put((StatusEffect) ((StatusEffectInstance) it.next()).getEffectType().value(), Long.valueOf(System.currentTimeMillis()));
                    }
                    return;
                }
            }
        }
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (minecraftClient.player.getMainHandStack().getItem() instanceof RangedWeaponItem) {
            ClientPlayerEntity playerEntity1934 = (ClientPlayerEntity)(findClosestTargetInFov());
            if (playerEntity1934 == null) {
                playerEntity1934 = minecraftClient.player;
            }
            this.map.entrySet().removeIf(entry -> {
                return ((Long) entry.getValue()).longValue() + 750 < System.currentTimeMillis();
            });
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < minecraftClient.player.getInventory().size(); i3++) {
                int i4 = scoreArrow(minecraftClient.player.getInventory().getStack(i3), playerEntity1934);
                if (i4 >= 0 && i4 > i) {
                    i2 = i3;
                    i = i4;
                }
            }
            this.num = i2;
            if (this.num != -1) {
                this.potion = PotionResolver.resolvePotion(minecraftClient.player.getInventory().getStack(this.num));
            }
        }
    }

    @Listen
    public void onRotationTick(RotationTickEvent holeSnapEvent) {
        if (this.autoShoot.getValue().booleanValue()) {
            if (this.num != -1) {
                if (scoreArrow(minecraftClient.player.getInventory().getStack(this.num), minecraftClient.player) > 0) {
                    shootBow();
                    return;
                }
            }
            disable();
        }
    }

    public PlayerEntity findClosestTargetInFov() {
        if (!this.autoShoot.getValue().booleanValue() && minecraftClient.player.getPitch() >= Float.intBitsToFloat(-1029046272)) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = null;
            double longBitsToDouble = Double.longBitsToDouble(5183643170566569984L);
            for (AbstractClientPlayerEntity abstractClientPlayerEntity2 : minecraftClient.world.getPlayers()) {
                if (minecraftClient.player != abstractClientPlayerEntity2 && !ClientServices.socialManager.isFriendEntity((PlayerEntity) abstractClientPlayerEntity2)) {
                    float[] floatArray2483 = RotationManager.getRotationsTo((Entity) abstractClientPlayerEntity2);
                    float angleBetween = MathHelper.angleBetween(minecraftClient.player.getYaw(), floatArray2483[0]) - (abstractClientPlayerEntity2.getDimensions(abstractClientPlayerEntity2.getPose()).width() * Float.intBitsToFloat(1056964608));
                    float angleBetween2 = MathHelper.angleBetween(minecraftClient.player.getPitch(), floatArray2483[1]) - (abstractClientPlayerEntity2.getDimensions(abstractClientPlayerEntity2.getPose()).height() * Float.intBitsToFloat(1056964608));
                    if (angleBetween <= this.fov.getValue().floatValue() && angleBetween2 <= this.fov.getValue().floatValue()) {
                        double hypot = Math.hypot(angleBetween, angleBetween2);
                        if (hypot < longBitsToDouble) {
                            abstractClientPlayerEntity = abstractClientPlayerEntity2;
                            longBitsToDouble = hypot;
                        }
                    }
                }
            }
            return abstractClientPlayerEntity;
        }
        return minecraftClient.player;
    }

    public int scoreArrow(ItemStack itemStack, Object obj) {
        if (!(itemStack.getItem() instanceof ArrowItem) || !(obj instanceof PlayerEntity)) {
            return -1;
        }
        PlayerEntity playerEntity = (PlayerEntity) obj;
        Potion potion1565 = PotionResolver.resolvePotion(itemStack);
        boolean z = playerEntity == minecraftClient.player || ClientServices.socialManager.isFriendEntity(playerEntity);
        int i = potion1565.getEffects().isEmpty() ? z ? -1 : 1 : 0;
        for (StatusEffectInstance statusEffectInstance : potion1565.getEffects()) {
            RegistryEntry<StatusEffect> effectType = statusEffectInstance.getEffectType();
            boolean hasStatusEffect = playerEntity.hasStatusEffect(effectType);
            if (hasStatusEffect && ((me.mioclient.ducks.DuckStatusEffectInstance)(Object) playerEntity.getStatusEffect(effectType)).mio$getDurationRation() * Float.intBitsToFloat(1120403456) < this.assumeDuration.getValue().intValue()) {
                hasStatusEffect = false;
            }
            if (!hasStatusEffect) {
                if (!this.map.containsKey(effectType.value())) {
                    int amplifier = (!((StatusEffect) effectType.value()).isInstant() ? statusEffectInstance.getAmplifier() : 0) + 1;
                    if (isBeneficialEffect(effectType) != z) {
                        amplifier *= -1;
                    }
                    i += amplifier;
                }
            }
        }
        return i;
    }

    public boolean isBeneficialEffect(RegistryEntry<StatusEffect> registryEntry) {
        if (registryEntry == StatusEffects.SLOW_FALLING) {
            return false;
        }
        return ((StatusEffect) registryEntry.value()).isBeneficial();
    }

    public void shootBow() {
        int i = minecraftClient.player.getInventory().selectedSlot;
        int i2 = InventoryUtils.findInHotbar(Items.BOW);
        ClientServices.RotationManager.requestRotations(new float[]{minecraftClient.player.getYaw(), Float.intBitsToFloat(-1028390912)}, 999);
        if (i2 == -1 || minecraftClient.player.isUsingItem()) {
            return;
        }
        InventoryUtils.do438(i2);
        minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.MAIN_HAND);
        if (atomicBoolean.get()) {
            return;
        }
        atomicBoolean.set(true);
        ClientServices.tooltipsHelper.schedule(() -> {
            minecraftClient.interactionManager.stopUsingItem(minecraftClient.player);
            InventoryUtils.do438(i);
            atomicBoolean.set(false);
            disable();
        }, 3);
    }
}
