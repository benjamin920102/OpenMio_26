package me.mioclient.mixin;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/* 26.2 port (verified against 26.2 merged jar via JDK25 javap):
   Options class still exists as net.minecraft.client.Options. */
@Mixin({Options.class})

public class MixinGameOptions {
    @ModifyConstant(method = {"<init>"}, constant = {@Constant(intValue = 110)}, require = 0)
    private int bullet(int i) {
        return 150;
    }
}
