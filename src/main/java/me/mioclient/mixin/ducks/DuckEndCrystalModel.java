package me.mioclient.mixin.ducks;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.crystal.EndCrystalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): EndCrystalModel exposes public final
   base/outerGlass/innerGlass/cube. Accessors kept so chams code has a stable
   duck type even though fields are already public. */
@Mixin({EndCrystalModel.class})
public interface DuckEndCrystalModel {
    @Accessor("base")
    ModelPart mio$getBase();

    @Accessor("outerGlass")
    ModelPart mio$getOuterGlass();

    @Accessor("innerGlass")
    ModelPart mio$getInnerGlass();

    @Accessor("cube")
    ModelPart mio$getCube();
}
