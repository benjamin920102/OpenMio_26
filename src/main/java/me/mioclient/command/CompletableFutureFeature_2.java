package me.mioclient.command;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.util.client.ChatUtils;

import me.mioclient.manager.ConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import me.mioclient.mixin.ducks.DuckTextureManager;

public class CompletableFutureFeature_2 extends CommandFeature {
    public static final byte[] byteArr = {-119, 80, 78, 71, 13, 10, 26, 10};
    public CompletableFuture<Void> completableFuture;

    public CompletableFutureFeature_2() {
        super("reload");
        this.completableFuture = CompletableFuture.completedFuture(null);
        setAliases("rl");
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder) {
        literalArgumentBuilder.executes(commandContext -> {
            if (!this.completableFuture.isDone()) {
                return 0;
            }
            ChatUtils.sendMessage(Component.literal("Reloading Mio resources..."), ChatUtils.signatureOf(this));
            this.completableFuture = CompletableFuture.runAsync(() -> {
                HashSet<Identifier> hashSet = new HashSet<>();
                for (Identifier identifier : ((DuckTextureManager) minecraftClient.getTextureManager()).getAllTextures().keySet()) {
                    if (identifier.getNamespace().equals("mio-mount")) {
                        hashSet.add(identifier);
                    }
                }
                ClientServices.presetHelper.createDirectories();
                hashSet.forEach(identifier2 -> {
                    byte[] readAllBytes;
                    try {
                        readAllBytes = Files.readAllBytes(ConfigManager.path.resolve(identifier2.getPath()));
                    } catch (IOException | java.lang.RuntimeException e) {
                        ChatUtils.sendMessage(Component.literal(new StringTemplate().arg(e.getMessage()).format("Invalid Image! \u0001")).withStyle(style -> {
                            return style.applyFormat(ChatFormatting.RED);
                        }), ChatUtils.createSignature(-999));
                        return;
                    }
                    if (readAllBytes.length < 8) {
                        throw new java.lang.RuntimeException("File size is too small to be png");
                    }
                    for (int i = 0; i < 8; i++) {
                        if (readAllBytes[i] != byteArr[i]) {
                            throw new java.lang.RuntimeException("File is not a png");
                        }
                    }
                    minecraftClient.getTextureManager().release(identifier2);
                    minecraftClient.getTextureManager().register(identifier2, new SimpleTexture(identifier2));
                });
                ClientServices.killstreakSoundManager.reloadWorldRenderer();
            }, executorService);
            this.completableFuture.whenComplete((r4, th) -> {
                ChatUtils.sendMessage(Component.literal("Reload complete!").withStyle(style -> {
                    return style.applyFormat(ChatFormatting.GREEN);
                }), ChatUtils.signatureOf(this));
            });
            return 1;
        });
    }
}
