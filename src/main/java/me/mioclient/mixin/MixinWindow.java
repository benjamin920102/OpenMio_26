package me.mioclient.mixin;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.MonitorManager;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.GpuBackend;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* 26.2 port (verified via JDK25 javap): Window.<init> is now
   (WindowEventHandler, DisplayData, String, boolean, String, MonitorManager,
   GpuBackend); Monitor is a RECORD; Window.handle() exists. */
@Mixin({Window.class})

public class MixinWindow {
    @Inject(method = {"<init>"}, at = {@At(value = "INVOKE_ASSIGN", target = "Lcom/mojang/blaze3d/platform/Window;createWindow(Lcom/mojang/blaze3d/systems/GpuBackend;IILjava/lang/String;J)J", shift = At.Shift.BEFORE, remap = false)}, require = 0)
    private void init(WindowEventHandler windowEventHandler, DisplayData displayData, String str, boolean bl, String str2, MonitorManager monitorManager, GpuBackend gpuBackend, CallbackInfo callbackInfo) {
        GLFW.glfwWindowHint(139270, 0);
    }
}
