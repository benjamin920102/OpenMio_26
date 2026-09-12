package me.mioclient.module.player.freecam;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.mixin.ducks.DuckKeyBinding;
import me.mioclient.module.movement.NoSlow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FreecamInput.class */
public class FreecamInput extends Input implements ClientAccessor {
    public static NoSlow noslow = (NoSlow) ClientServices.moduleInjector.getModule(NoSlow.class);

    public void tick(boolean z, float f) {
        this.movementForward = getKeyMovementValue(isKeyBindingActive(minecraftClient.options.forwardKey), isKeyBindingActive(minecraftClient.options.backKey));
        this.movementSideways = getKeyMovementValue(isKeyBindingActive(minecraftClient.options.leftKey), isKeyBindingActive(minecraftClient.options.rightKey));
    }

    public float getKeyMovementValue(boolean z, boolean z2) {
        if (z == z2) {
            return 0.0f;
        }
        return z ? Float.intBitsToFloat(1065353216) : Float.intBitsToFloat(-1082130432);
    }

    public boolean isKeyBindingActive(KeyBinding keyBinding) {
        return (noslow.isToggled() && noslow.isOffhandSwapAllowed() && noslow.guiMove.getValue().booleanValue()) ? GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((DuckKeyBinding) keyBinding).getKey().getCode()) == 1 : keyBinding.isPressed();
    }
}
