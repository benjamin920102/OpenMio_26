package me.mioclient.mixin;

import net.minecraft.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port: CrashReport.addDetails gone; getDetails(StringBuilder) kept.
   Explicit descriptor selects the StringBuilder overload (a no-arg
   String getDetails() overload also exists). */
@Mixin({CrashReport.class})

public class MixinCrashReport {
    @Inject(method = {"getDetails(Ljava/lang/StringBuilder;)V"}, at = {@At("TAIL")}, require = 0)
    public void addStackTrace(StringBuilder sb, CallbackInfo callbackInfo) {
        sb.append("\n\n-- www.me.mioclient.me --\n");
        sb.append("Details:\n");
        sb.append("\tMagic: rizz\n\n");
    }
}
