package me.mioclient.util.client;

import me.mioclient.api.ClientAccessor;

import java.io.ByteArrayInputStream;
import me.mioclient.mixin.ducks.DuckAbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SoundPlayer.class */
public class SoundPlayer implements ClientAccessor {
    public static final SoundPlayer INSTANCE = new Inner(null);
    public final byte[] byteArr;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/SoundPlayer$Inner.class */
    static class Inner extends SoundPlayer {
        public Inner(byte[] bArr) {
            super(bArr);
        }

        @Override // me.mioclient.util.client.SoundPlayer
        public void play(float f) {
        }

        @Override // me.mioclient.util.client.SoundPlayer
        public void playAt(Vec3d vec3d, float f) {
        }
    }

    public SoundPlayer(byte[] bArr) {
        this.byteArr = bArr;
    }

    public void do1016() {
        play(Float.intBitsToFloat(1065353216));
    }

    public void playAt(Vec3d vec3d) {
        playAt(vec3d, Float.intBitsToFloat(1065353216));
    }

    public void play(float f) {
        if (minecraftClient.getSoundManager() != null) {
            try {
                minecraftClient.execute(() -> {
                    SoundInstance inputStreamAbstractSoundInstance = new StreamedSoundInstance(new ByteArrayInputStream(getSoundData()));
                    ((DuckAbstractSoundInstance) inputStreamAbstractSoundInstance).setVolume(f);
                    minecraftClient.getSoundManager().play(inputStreamAbstractSoundInstance);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playAt(Vec3d vec3d, float f) {
        if (minecraftClient.world != null) {
            try {
                minecraftClient.execute(() -> {
                    SoundInstance inputStreamAbstractSoundInstance = new StreamedSoundInstance(vec3d, new ByteArrayInputStream(getSoundData()));
                    ((DuckAbstractSoundInstance) inputStreamAbstractSoundInstance).setVolume(f);
                    minecraftClient.getSoundManager().play(inputStreamAbstractSoundInstance);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getSoundData() {
        return this.byteArr;
    }
}
