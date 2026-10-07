package me.mioclient.ducks;

import me.mioclient.util.anim.AnimationProgress;

import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;

public interface DuckChatHudLineVisible {
    MessageSignature getSignature();

    void setSignature(MessageSignature MessageSignature);

    FormattedCharSequence getContent();

    void setContent(FormattedCharSequence orderedText);

    AnimationProgress getProgress();

    long mio$getAddTime();

    void mio$setAddTime(long j);
}
