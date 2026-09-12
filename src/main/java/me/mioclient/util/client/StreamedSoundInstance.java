package me.mioclient.util.client;

import me.mioclient.api.ClientAccessor;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/InputStreamAbstractSoundInstance.class */
public class StreamedSoundInstance extends AbstractSoundInstance {
    public InputStream inputStream;

    public StreamedSoundInstance(Vec3d vec3d, InputStream inputStream) {
        super(Identifier.of(ClientAccessor.isGlobalsLoaded() ? "mioloader" : "mio", "sound"), SoundCategory.MASTER, SoundInstance.createRandom());
        this.x = vec3d.x;
        this.y = vec3d.y;
        this.z = vec3d.z;
        this.inputStream = inputStream;
    }

    public StreamedSoundInstance(InputStream inputStream) {
        super(Identifier.of(ClientAccessor.isGlobalsLoaded() ? "mioloader" : "mio", "sound"), SoundCategory.MASTER, SoundInstance.createRandom());
        this.inputStream = inputStream;
        this.relative = true;
        this.x = 0.0d;
        this.y = 0.0d;
        this.z = Double.longBitsToDouble(-4616189618054758400L);
    }

    public CompletableFuture<AudioStream> getAudioStream(SoundLoader soundLoader, Identifier identifier, boolean z) {
        try {
            return CompletableFuture.completedFuture(new OggAudioStream(this.inputStream));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
