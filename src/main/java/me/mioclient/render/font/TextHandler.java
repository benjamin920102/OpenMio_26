package me.mioclient.render.font;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/TextHandler.class */
public class TextHandler extends net.minecraft.client.font.TextHandler {
    public TextHandler(AdvanceGlyph advanceGlyph) {
        super((i, style) -> {
            return advanceGlyph.getCharacterAdvance(Character.toString(i).charAt(0));
        });
    }
}
