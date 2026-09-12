package me.mioclient.module.combat.bedaura;

import me.mioclient.api.ClientFunction;
import me.mioclient.api.ClientServices;

import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.combat.ExplosionDamageCalculator;

import me.mioclient.module.combat.BedAura;
import net.minecraft.block.BedBlock;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import me.mioclient.api.ClientFunction;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BedDetonationSelector.class */
public class BedDetonationSelector implements ClientFunction<LivingEntity, BlockPos> {
    public final BedAura bedAura;
    public Vec3d vec3d;
    public float val;
    public float val2;
    public float val3;

    public BedDetonationSelector(BedAura bedAura) {
        this.bedAura = bedAura;
    }

    public void recalculateLayout() {
        this.vec3d = minecraftClient.player.getEyePos();
        this.val = this.bedAura.range.getValue().floatValue();
        this.val2 = this.bedAura.minDamage.getValue().floatValue();
        this.val3 = this.bedAura.maxSelfDamage.getValue().floatValue();
    }

    @Override // me.mioclient.api.ClientFunction
    /* renamed from: getBlockPos3124, reason: merged with bridge method [inline-methods] */
    public BlockPos selectTarget(LivingEntity livingEntity) {
        recalculateLayout();
        float f = 0.0f;
        BlockPos blockPos = null;
        for (BlockEntity blockEntity : ClientServices.stashFinderHelper.getLoadedBlockEntities()) {
            if (blockEntity instanceof BedBlockEntity) {
                if (blockEntity.getCachedState().get(BedBlock.PART) != BedPart.FOOT) {
                    if (blockEntity.getPos().toCenterPos().distanceTo(this.vec3d) <= this.val) {
                        float f2 = ExplosionDamageCalculator.getBedDamage(blockEntity.getPos().toCenterPos(), livingEntity);
                        float f3 = ExplosionDamageCalculator.getBedDamage(blockEntity.getPos().toCenterPos(), minecraftClient.player);
                        if (f3 < EntityUtil.getPlayerHealth() || !this.bedAura.antiSucide.getValue().booleanValue()) {
                            if (f3 <= this.val3 && f2 >= this.val2 && (blockPos == null || f2 > f)) {
                                blockPos = blockEntity.getPos();
                                f = f2;
                            }
                        }
                    }
                }
            }
        }
        return blockPos;
    }
}
