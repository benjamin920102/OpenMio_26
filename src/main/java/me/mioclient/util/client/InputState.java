package me.mioclient.util.client;

import me.mioclient.util.text.StringTemplate;

import me.mioclient.api.ClientAccessor;

import me.mioclient.api.Keybind;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/InputState.class */
public class InputState implements ClientAccessor {
    public static String getString2602(Keybind keybind) {
        return keyCodeToString(keybind.getKey(), keybind.isMouseKeybind());
    }

    public static String keyCodeToString(int i, boolean z) {
        return i < 0 ? "NONE" : z ? new StringTemplate().arg(i).format("MOUSE\u0001") : InputUtil.fromKeyCode(i, 0).getTranslationKey().replaceAll("key.keyboard.", "").replace(".", "_").toUpperCase();
    }

    public static boolean isKeybindPressed(Keybind keybind) {
        return keybind.isMouseKeybind() ? isMouseButtonPressed(keybind.getKey()) : isKeyboardKeyDown(keybind.getKey());
    }

    public static boolean isKeyBindingActive(KeyBinding keyBinding) {
        InputUtil.Key key = keyBinding.boundKey;
        return key.getCategory() == InputUtil.Type.MOUSE ? isMouseButtonPressed(key.getCode()) : isKeyboardKeyDown(key.getCode());
    }

    public static boolean isKeyboardKeyDown(int i) {
        return GLFW.glfwGetKey(minecraftClient.getWindow().getHandle(), i) == 1;
    }

    public static boolean isMouseButtonPressed(int i) {
        return GLFW.glfwGetMouseButton(minecraftClient.getWindow().getHandle(), i) == 1;
    }

    public static boolean isLeftCtrlDown() {
        return GLFW.glfwGetKey(minecraftClient.getWindow().getHandle(), 341) == 1;
    }

    public static boolean isLeftShiftDown() {
        return GLFW.glfwGetKey(minecraftClient.getWindow().getHandle(), 340) == 1;
    }

    public static boolean is2609() {
        return GLFW.glfwGetKey(minecraftClient.getWindow().getHandle(), 342) == 1;
    }
}
