package me.mioclient.util.client;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.ClientAccessor;

import me.mioclient.api.Keybind;
import me.mioclient.mixin.ducks.DuckKeyBinding;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class InputState implements ClientAccessor {
    public static String getString2602(Keybind keybind) {
        return keyCodeToString(keybind.getKey(), keybind.isMouseKeybind());
    }

    public static String keyCodeToString(int i, boolean z) {
        return i < 0 ? "NONE" : z ? new StringTemplate().arg(i).format("MOUSE\u0001") : InputConstants.Type.KEYSYM.getOrCreate(i).getName().replaceAll("key.keyboard.", "").replace(".", "_").toUpperCase();
    }

    public static boolean isKeybindPressed(Keybind keybind) {
        return keybind.isMouseKeybind() ? isMouseButtonPressed(keybind.getKey()) : isKeyboardKeyDown(keybind.getKey());
    }

    public static boolean isKeyBindingActive(KeyMapping keyBinding) {
        InputConstants.Key key = ((DuckKeyBinding)(Object) keyBinding).getKey();
        return key.getType() == InputConstants.Type.MOUSE ? isMouseButtonPressed(key.getValue()) : isKeyboardKeyDown(key.getValue());
    }

    public static boolean isKeyboardKeyDown(int i) {
        return GLFW.glfwGetKey(minecraftClient.getWindow().handle(), i) == 1;
    }

    public static boolean isMouseButtonPressed(int i) {
        return GLFW.glfwGetMouseButton(minecraftClient.getWindow().handle(), i) == 1;
    }

    public static boolean isLeftCtrlDown() {
        return GLFW.glfwGetKey(minecraftClient.getWindow().handle(), 341) == 1;
    }

    public static boolean isLeftShiftDown() {
        return GLFW.glfwGetKey(minecraftClient.getWindow().handle(), 340) == 1;
    }

    public static boolean is2609() {
        return GLFW.glfwGetKey(minecraftClient.getWindow().handle(), 342) == 1;
    }
}
