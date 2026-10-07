package me.mioclient.render.chams;

import net.minecraft.world.phys.AABB;

public class LlamaChamsRenderer extends LivingEntityChamsRenderer {
    @Override
    public void renderCustomModel(ChamsRenderContext matrixStackData, Object entityModel) {
    }

    public static AABB localBox(net.minecraft.world.entity.Entity entity) {
        AABB bb = entity.getBoundingBox();
        return bb.move(-entity.getX(), -entity.getY(), -entity.getZ());
    }
}
