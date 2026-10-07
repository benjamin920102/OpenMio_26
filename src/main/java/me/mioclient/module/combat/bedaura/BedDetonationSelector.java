package me.mioclient.module.combat.bedaura;

import me.mioclient.api.ClientFunction;
import me.mioclient.api.ClientServices;

import me.mioclient.util.combat.ExplosionDamageCalculator;
import me.mioclient.util.player.EntityUtil;

import me.mioclient.module.combat.BedAura;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class BedDetonationSelector implements ClientFunction<LivingEntity, BlockPos> {
    public final BedAura bedAura;
    public Vec3 vec3d;
    public float val;
    public float val2;
    public float val3;

    public BedDetonationSelector(BedAura bedAura) {
        this.bedAura = bedAura;
    }

    public void recalculateLayout() {
        this.vec3d = minecraftClient.player.getEyePosition();
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
            // 26.2 port: BedBlockEntity removed; detect beds via BedBlock state.
            if (blockEntity.getBlockState().getBlock() instanceof BedBlock) {
                if (blockEntity.getBlockState().getValue(BedBlock.PART) != BedPart.FOOT) {
                    Vec3 bedCenter = Vec3.atCenterOf(blockEntity.getBlockPos());
                    if (bedCenter.distanceTo(this.vec3d) <= this.val) {
                        float f2 = ExplosionDamageCalculator.getBedDamage(bedCenter, livingEntity);
                        float f3 = ExplosionDamageCalculator.getBedDamage(bedCenter, minecraftClient.player);
                        if (f3 < EntityUtil.getPlayerHealth() || !this.bedAura.antiSucide.getValue().booleanValue()) {
                            if (f3 <= this.val3 && f2 >= this.val2 && (blockPos == null || f2 > f)) {
                                blockPos = blockEntity.getBlockPos();
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