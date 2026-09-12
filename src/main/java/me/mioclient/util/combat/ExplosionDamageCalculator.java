package me.mioclient.util.combat;

import me.mioclient.api.ClientServices;

import me.mioclient.util.inventory.EnchantmentHelper;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.world.BlockRaycastFilter;
import me.mioclient.util.world.CustomRaycastContext;
import me.mioclient.util.world.RaycastUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import me.mioclient.util.inventory.EnchantmentHelper;
import me.mioclient.module.combat.AutoCrystal;
import me.mioclient.module.combat.AutoMine;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ExplosionDamageCalculator.class */
public class ExplosionDamageCalculator implements ClientAccessor {
    public static AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public static AutoMine autoMine = (AutoMine) ClientServices.moduleInjector.getModule(AutoMine.class);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/ArmorSearchHelper4$Inner.class */
    public static /* synthetic */ class Inner {
        public static final /* synthetic */ int[] intArr = new int[Difficulty.values().length];

        static {
            try {
                intArr[Difficulty.PEACEFUL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                intArr[Difficulty.EASY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                intArr[Difficulty.HARD.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static float getCrystalDamage(Vec3d vec3d, LivingEntity livingEntity) {
        return getExplosionDamage(vec3d, livingEntity, Double.longBitsToDouble(4618441417868443648L), true, (BlockPos) null);
    }

    public static float getCrystalDamage(Vec3d vec3d, LivingEntity livingEntity, Box box) {
        return getExplosionDamage(vec3d, livingEntity, box, Double.longBitsToDouble(4618441417868443648L), true, (BlockPos) null, (BlockPos) null);
    }

    public static float getBedDamage(Vec3d vec3d, LivingEntity livingEntity) {
        return getBedDamage(vec3d, livingEntity, livingEntity.getBoundingBox());
    }

    public static float getBedDamage(Vec3d vec3d, LivingEntity livingEntity, Box box) {
        return getExplosionDamage(vec3d, livingEntity, box, Double.longBitsToDouble(4617315517961601024L), true, (BlockPos) null, (BlockPos) null);
    }

    public static float getExplosionDamage(Vec3d vec3d, LivingEntity livingEntity, double d, boolean z, BlockPos blockPos) {
        return getExplosionDamage(vec3d, livingEntity, livingEntity.getBoundingBox(), d, z, blockPos, (BlockPos) null);
    }

    public static float getExplosionDamage(Vec3d vec3d, LivingEntity livingEntity, Box box, double d, boolean z, BlockPos blockPos, BlockPos blockPos2) {
        if (livingEntity.equals(minecraftClient.player) && minecraftClient.player.isCreative()) {
            return 0.0f;
        }
        Vec3d vec3d2 = new Vec3d(MathHelper.lerp(MathConstants.val2, box.minX, box.maxX), box.minY, MathHelper.lerp(MathConstants.val2, box.minZ, box.maxZ));
        double longBitsToDouble = d * Double.longBitsToDouble(4611686018427387904L);
        double distanceTo = vec3d2.distanceTo(vec3d) / longBitsToDouble;
        if (distanceTo > Double.longBitsToDouble(4607182418800017408L)) {
            return 0.0f;
        }
        double longBitsToDouble2 = (Double.longBitsToDouble(4607182418800017408L) - distanceTo) * getExposure(vec3d, (Entity) livingEntity, box, blockPos, blockPos2, z);
        return Math.max(getDamageAfterProtection(livingEntity, getDifficultyScaledDamage((float) (((((longBitsToDouble2 * longBitsToDouble2) + longBitsToDouble2) / Double.longBitsToDouble(4611686018427387904L)) * Double.longBitsToDouble(4619567317775286272L) * longBitsToDouble) + Double.longBitsToDouble(4607182418800017408L)))), 0.0f);
    }

    public static float getExposure(Vec3d vec3d, Entity entity, BlockPos blockPos, boolean z) {
        return getExposure(vec3d, entity, entity.getBoundingBox(), blockPos, (BlockPos) null, z);
    }

    public static float getExposure(Vec3d vec3d, Entity entity, Box box, BlockPos blockPos, BlockPos blockPos2, boolean z) {
        double longBitsToDouble = Double.longBitsToDouble(4607182418800017408L) / (((box.maxX - box.minX) * Double.longBitsToDouble(4611686018427387904L)) + Double.longBitsToDouble(4607182418800017408L));
        double longBitsToDouble2 = Double.longBitsToDouble(4607182418800017408L) / (((box.maxY - box.minY) * Double.longBitsToDouble(4611686018427387904L)) + Double.longBitsToDouble(4607182418800017408L));
        double longBitsToDouble3 = Double.longBitsToDouble(4607182418800017408L) / (((box.maxZ - box.minZ) * Double.longBitsToDouble(4611686018427387904L)) + Double.longBitsToDouble(4607182418800017408L));
        double longBitsToDouble4 = (Double.longBitsToDouble(4607182418800017408L) - (Math.floor(Double.longBitsToDouble(4607182418800017408L) / longBitsToDouble) * longBitsToDouble)) / Double.longBitsToDouble(4611686018427387904L);
        double longBitsToDouble5 = (Double.longBitsToDouble(4607182418800017408L) - (Math.floor(Double.longBitsToDouble(4607182418800017408L) / longBitsToDouble3) * longBitsToDouble3)) / Double.longBitsToDouble(4611686018427387904L);
        if (longBitsToDouble < 0.0d || longBitsToDouble2 < 0.0d || longBitsToDouble3 < 0.0d) {
            return 0.0f;
        }
        int i = 0;
        int i2 = 0;
        double d = 0.0d;
        while (true) {
            double d2 = d;
            if (d2 > Double.longBitsToDouble(4607182418800017408L)) {
                return (float) i / (float) i2;   // çˆ†ç‚¸æš´éœ²çŽ‡(å‘½ä¸­æ ·æœ¬/æ€»æ ·æœ¬), å¿…é¡»æµ®ç‚¹é™¤æ³•; åç¼–è¯‘ä¸¢äº†è½¬åž‹å¯¼è‡´æ•´æ•°é™¤æ³•â†’æ’0â†’æ°´æ™¶ä¼¤å®³â‰ˆ0â†’AutoCrystalä¸æ”¾ç½®
            }
            double d3 = 0.0d;
            while (true) {
                double d4 = d3;
                if (d4 <= Double.longBitsToDouble(4607182418800017408L)) {
                    double d5 = 0.0d;
                    while (true) {
                        double d6 = d5;
                        if (d6 <= Double.longBitsToDouble(4607182418800017408L)) {
                            CustomRaycastContext.Inner inner1603 = new CustomRaycastContext.Inner(new Vec3d(MathHelper.lerp(d2, box.minX, box.maxX) + longBitsToDouble4, MathHelper.lerp(d4, box.minY, box.maxY), MathHelper.lerp(d6, box.minZ, box.maxZ) + longBitsToDouble5), vec3d).setEntity(entity);
                            BlockRaycastFilter[] holeSnapHelperArr = new BlockRaycastFilter[3];
                            BlockPos[] blockPosArr = new BlockPos[2];
                            blockPosArr[0] = blockPos;
                            blockPosArr[1] = blockPos == null ? null : autoMine.speedMineHelper.getBlockPos();
                            holeSnapHelperArr[0] = BlockRaycastFilter.createIgnoreFilter(Arrays.asList(blockPosArr));
                            holeSnapHelperArr[1] = BlockRaycastFilter.getHoleSnapHelper1676(blockPos2);
                            holeSnapHelperArr[2] = z ? BlockRaycastFilter.holeSnapHelper2 : BlockRaycastFilter.holeSnapHelper;
                            if (RaycastUtils.performCustomBlockRaycast(inner1603.setCombinedFilter(holeSnapHelperArr).build()).getType() == HitResult.Type.MISS) {
                                i++;
                            }
                            i2++;
                            d5 = d6 + longBitsToDouble3;
                        } else {
                            break;
                        }
                    }
                    d3 = d4 + longBitsToDouble2;
                } else {
                    break;
                }
            }
            d = d2 + longBitsToDouble;
        }
    }

    public static float getDamageAfterProtection(LivingEntity livingEntity, float f) {
        float damageLeft = DamageUtil.getDamageLeft(livingEntity, f, minecraftClient.world.getDamageSources().explosion((Explosion) null), livingEntity.getArmor(), (float) livingEntity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        if (livingEntity.hasStatusEffect(StatusEffects.RESISTANCE)) {
            damageLeft = (damageLeft * (25 - ((livingEntity.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5))) / Float.intBitsToFloat(1103626240);
        }
        int i = 0;
        Iterator it = livingEntity.getArmorItems().iterator();
        while (it.hasNext()) {
            Map<RegistryKey<Enchantment>, Integer> map1419 = EnchantmentHelper.getEnchantmentMap((ItemStack) it.next());
            i += (map1419.getOrDefault(Enchantments.BLAST_PROTECTION, 0).intValue() * 2) + map1419.getOrDefault(Enchantments.PROTECTION, 0).intValue();
        }
        if (i > 20 || (autoCrystal.isToggled() && autoCrystal.assumeBestArmor.getValue().booleanValue())) {
            i = 20;
        }
        float intBitsToFloat = damageLeft * (Float.intBitsToFloat(1065353216) - (i / Float.intBitsToFloat(1103626240)));
        if (intBitsToFloat < 0.0f) {
            return 0.0f;
        }
        return intBitsToFloat;
    }

    public static float getDifficultyScaledDamage(float f) {
        switch (Inner.intArr[minecraftClient.world.getDifficulty().ordinal()]) {
            case 1:
                return 0.0f;
            case 2:
                return Math.min((f / Float.intBitsToFloat(1073741824)) + Float.intBitsToFloat(1065353216), f);
            case 3:
                return (f * Float.intBitsToFloat(1077936128)) / Float.intBitsToFloat(1073741824);
            default:
                return f;
        }
    }

    public static int getClampedDurabilityPercent(ItemStack itemStack) {
        return MathHelper.clamp(100 - ((int) Math.ceil((Float.intBitsToFloat(1065353216) - ((itemStack.getMaxDamage() - itemStack.getDamage()) / (float) itemStack.getMaxDamage())) * Float.intBitsToFloat(1120403456))), 1, 100);
    }
}
