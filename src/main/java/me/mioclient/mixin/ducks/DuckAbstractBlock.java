package me.mioclient.mixin.ducks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;

/* Class mixin on BlockBehaviour (a class) providing the isCollidable duck.
   Callers cast via (DuckAbstractBlock)(Object). */
@Mixin({BlockBehaviour.class})
public class DuckAbstractBlock {
    public boolean isCollidable() {
        return ((Block)(Object) this).defaultBlockState().isSolid();
    }
}
