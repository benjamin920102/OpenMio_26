package me.mioclient.module.misc;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.io.ImageTransferable;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;

import com.google.gson.JsonObject;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;
import javax.imageio.ImageIO;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Module;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;

public class ExtraScreenshot extends Module {
    public Setting<ExtraScreenshotMode> mode = add(new EnumSetting<>("Mode", ExtraScreenshotMode.COPY));
    public Setting<Integer> hiResScale = add(new NumberSetting<>("HiResScale", 1, 1, 4));
    public Setting<Boolean> deleteFile = add(new BooleanSetting("DeleteFile", false, v -> this.mode.getValue() != ExtraScreenshot.ExtraScreenshotMode.NONE));
    public Setting<Boolean> sound = add(new BooleanSetting("Sound", false).setVisible());
    public Setting<SoundIdentifier> path = add(new SoundIdentifierSetting("Path", new SoundIdentifier("system", "warning")).requiresSettings(this.sound));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 1.0f, 0.0f, 1.0f).requiresSettings(this.sound));
    public final Stopwatch stopwatch;
    public int num2;
    public static final long num = 1500;
    public static final String string = "Client-ID e7c0b6a4c926098";
    public static final HttpClient httpClient = HttpClient.newHttpClient();

    
    public enum ExtraScreenshotMode implements NamedValue {
        NONE("None"),
        COPY("Copy"),
        IMGUR("Imgur");

        public final String name;

        ExtraScreenshotMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public ExtraScreenshot() {
        super("ExtraScreenshot", "Uploads your screenshots to Imgur/clipboard.", Category.MISC, new String[0]);
        this.stopwatch = new Stopwatch();
        this.num2 = 0;
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return ClientTextRenderer.capitalize(this.mode.getValue());
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.num2 = 0;
    }

    public void processScreenshot(byte[] bArr) {
        if (this.mode.getValue() == ExtraScreenshotMode.IMGUR && !this.stopwatch.hasElapsedMs(1500L)) {
            this.num2++;
            this.num2 %= 3;
            ChatUtils.sendPrefixedMessage(Component.literal(String.format("Wait %dms before uploading a screenshot", Long.valueOf(1500 - this.stopwatch.getElapsedTime()))).withStyle(style -> {
                return style.withColor(ChatFormatting.YELLOW);
            }), ChatUtils.createSignature((-34596741) + this.num2), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
            return;
        }
        try {
            do1016();
        } catch (Exception e) {
        }
        this.stopwatch.reset();
        if (this.mode.getValue() == ExtraScreenshotMode.COPY) {
            try {
                Image read = ImageIO.read(new ByteArrayInputStream(bArr));
                BufferedImage bufferedImage = new BufferedImage(((BufferedImage) read).getWidth(), ((BufferedImage) read).getHeight(), 1);
                Graphics2D createGraphics = bufferedImage.createGraphics();
                createGraphics.drawImage(read, 0, 0, (ImageObserver) null);
                createGraphics.dispose();
                copyToClipboard(new ImageTransferable(bufferedImage));
                ChatUtils.sendMessage(Component.literal("Copied screenshot to clipboard"), ChatUtils.signatureOfModule(this));
                return;
            } catch (Exception e2) {
                e2.printStackTrace();
                ChatUtils.sendPrefixedMessage(Component.literal("Failed to copy the screenshot").withStyle(style2 -> {
                    return style2.withColor(ChatFormatting.RED);
                }), ChatUtils.signatureOfModule(this), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                return;
            }
        }
        if (this.mode.getValue() == ExtraScreenshotMode.IMGUR) {
            ChatUtils.sendMessage(Component.literal("Uploading screenshot..."), ChatUtils.signatureOfModule(this));
            try {
                JsonObject jsonObject = (JsonObject) ClientAccessor.gson.fromJson((String) httpClient.send(HttpRequest.newBuilder().uri(new URI("https://api.imgur.com/3/image")).headers(new String[]{"Content-Type", "application/x-www-form-urlencoded"}).headers(new String[]{"Authorization", "Client-ID e7c0b6a4c926098"}).POST(HttpRequest.BodyPublishers.ofString(new StringTemplate().arg(URLEncoder.encode(Base64.getEncoder().encodeToString(bArr), StandardCharsets.UTF_8)).format("image=\u0001"))).timeout(Duration.ofSeconds(10L)).build(), HttpResponse.BodyHandlers.ofString()).body(), JsonObject.class);
                if (!jsonObject.has("success") || !jsonObject.has("data")) {
                    throw new IOException("Invalid server response");
                }
                if (!jsonObject.get("success").getAsBoolean()) {
                    throw new RuntimeException("Upload failed");
                }
                JsonObject asJsonObject = jsonObject.getAsJsonObject("data");
                if (!asJsonObject.has("link")) {
                    throw new IOException("Invalid server response");
                }
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(asJsonObject.get("link").getAsString()), (ClipboardOwner) null);
                ChatUtils.sendMessage(Component.literal("Copied screenshot link"), ChatUtils.createSignature(-43296436));
            } catch (Exception e3) {
                e3.printStackTrace();
                ChatUtils.sendPrefixedMessage(Component.literal("Failed to upload screenshot to Imgur").withStyle(style3 -> {
                    return style3.withColor(ChatFormatting.RED);
                }), ChatUtils.createSignature(-43296435), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
            }
        }
    }

    public void processNativeImage(NativeImage nativeImage) {
        try {
            try {
                byte[] bytes = nativeImageToPngBytes(nativeImage);
                if (shouldDeleteFile()) {
                    nativeImage.close();
                }
                ClientAccessor.executorService.submit(() -> {
                    try {
                        processScreenshot(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                ChatUtils.sendPrefixedMessage(Component.literal("Failed to save the screenshot"), ChatUtils.signatureOfModule(this), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode2);
                if (shouldDeleteFile()) {
                    nativeImage.close();
                }
            }
        } catch (Throwable th) {
            if (shouldDeleteFile()) {
                nativeImage.close();
            }
            throw th;
        }
    }
    public void copyToClipboard(ImageTransferable transferDataFlavorsTransferable) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferDataFlavorsTransferable, (ClipboardOwner) null);
    }

    /** 26.2: NativeImage has no getBytes(); encode PNG via temp file for real upload bytes. */
    private static byte[] nativeImageToPngBytes(NativeImage nativeImage) throws IOException {
        java.nio.file.Path tmp = Files.createTempFile("mio-screenshot-", ".png");
        try {
            nativeImage.writeToFile(tmp);
            return Files.readAllBytes(tmp);
        } finally {
            try {
                Files.deleteIfExists(tmp);
            } catch (IOException ignored) {
            }
        }
    }

    public void do1016() {
        if (this.sound.getValue().booleanValue()) {
            ClientServices.killstreakSoundManager.findSoundPlayer(this.path.getValue()).play(this.volume.getValue().floatValue());
        }
    }

    public boolean shouldDeleteFile() {
        return this.mode.getValue() != ExtraScreenshotMode.NONE && this.deleteFile.getValue().booleanValue();
    }

    public boolean isModeNone() {
        return this.mode.getValue() == ExtraScreenshotMode.NONE;
    }

    public boolean wantsHiRes() {
        return isToggled() && !isModeNone() && this.hiResScale.getValue() > 1;
    }
}