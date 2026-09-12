package me.mioclient.ducks;

import me.mioclient.util.anim.AnimationProgress;

import me.mioclient.util.anim.AnimationProgress;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/DuckChatHudLineVisible.class */
public interface DuckChatHudLineVisible {
    MessageSignatureData getSignature();

    void setSignature(MessageSignatureData messageSignatureData);

    OrderedText getContent();

    void setContent(OrderedText orderedText);

    AnimationProgress getProgress();

    long mio$getAddTime();

    void mio$setAddTime(long j);
}
