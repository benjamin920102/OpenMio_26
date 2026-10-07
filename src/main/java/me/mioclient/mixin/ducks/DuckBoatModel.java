package me.mioclient.mixin.ducks;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.boat.AbstractBoatModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/* 26.2 port (verified): BoatModel itself holds no parts; paddles live on
   AbstractBoatModel as `private final ModelPart leftPaddle/rightPaddle`.
   Model.root()/allParts() are public, so chams mostly uses those. */
@Mixin({AbstractBoatModel.class})
public interface DuckBoatModel {
    @Accessor("leftPaddle")
    ModelPart mio$getLeftPaddle();

    @Accessor("rightPaddle")
    ModelPart mio$getRightPaddle();
}
