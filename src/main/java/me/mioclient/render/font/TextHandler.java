package me.mioclient.render.font;

public class TextHandler extends net.minecraft.client.StringSplitter {
    public TextHandler(AdvanceGlyph advanceGlyph) {
        super((codepoint, style) -> advanceGlyph.getCharacterAdvance((char) codepoint));
    }
}
