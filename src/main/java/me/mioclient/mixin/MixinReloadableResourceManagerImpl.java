package me.mioclient.mixin;

import me.mioclient.loader.EasterEggMessages;
import me.mioclient.util.io.StubResourcePack;

import me.mioclient.api.ClientAccessor;
import me.mioclient.manager.ConfigManager;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import java.io.FileInputStream;
import java.util.Optional;
import me.mioclient.loader.EasterEggMessages;
import me.mioclient.util.io.StubResourcePack;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/* compiled from: 0.java */
@Mixin({ReloadableResourceManagerImpl.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinReloadableResourceManagerImpl.class */
public class MixinReloadableResourceManagerImpl {
    @ModifyReturnValue(method = {"getResource"}, at = {@At("RETURN")})
    private Optional<Resource> getResource(Optional<Resource> optional, @Local(argsOnly = true) Identifier identifier) {
        if (identifier.getNamespace().equals("mio-mount")) {
            return Optional.of(new Resource(new StubResourcePack(), () -> {
                return new FileInputStream(ConfigManager.path.resolve(identifier.getPath()).toFile());
            }));
        }
        if (!optional.isEmpty() || ((!identifier.getNamespace().equals("mio") && !identifier.getPath().contains("/blur_mask.")) || !ClientAccessor.isGlobalsLoaded())) {
            return optional;
        }
        String str = identifier.getPath().contains("blur_mask") ? "minecraft" : "mio";
        return Optional.of(new Resource(new StubResourcePack(), () -> {
            return EasterEggMessages.class.getClassLoader().getResourceAsStream("assets/%s/%s".formatted(str, identifier.getPath()));
        }));
    }
}
