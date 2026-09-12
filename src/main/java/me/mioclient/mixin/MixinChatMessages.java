package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.font.AdvanceGlyph;
import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.api.ClientServices;
import me.mioclient.render.font.ClientTextRenderer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.awt.Color;
import java.util.Optional;
import me.mioclient.render.font.AdvanceGlyph;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.misc.BetterChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/* compiled from: 0.java */
@Mixin({ChatMessages.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinChatMessages.class */
public class MixinChatMessages {
    private static Fonts fonts;

    private static Fonts getFonts() {

        if (fonts == null && ClientServices.moduleInjector != null) {

            try { fonts = (Fonts) ClientServices.moduleInjector.getModule(Fonts.class); } catch (Exception e) {}

        }

        return fonts;

    }

    @Unique
    private static BetterChat betterchat;

    private static BetterChat getBetterchat() {

        if (betterchat == null && ClientServices.moduleInjector != null) {

            try { betterchat = (BetterChat) ClientServices.moduleInjector.getModule(BetterChat.class); } catch (Exception e) {}

        }

        return betterchat;

    }

    @ModifyExpressionValue(method = {"breakRenderedChatMessageLines"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getTextHandler()Lnet/minecraft/client/font/TextHandler;")})
    private static TextHandler mio$breakRenderedChatMessageLines(TextHandler textHandler) {
        AdvanceGlyph advanceGlyph1686 = ClientTextRenderer.INSTANCE.getAdvanceGlyph();
        return (advanceGlyph1686 != null && getFonts().isToggled() && getFonts().chat.getValue().booleanValue()) ? advanceGlyph1686.getTextHandler() : textHandler;
    }

    @ModifyVariable(method = {"breakRenderedChatMessageLines"}, at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static StringVisitable mio$breakRenderedChatMessageLines(StringVisitable stringVisitable) {
        if (!getBetterchat().isToggled() || !getBetterchat().highlight.getValue().booleanValue()) {
            return stringVisitable;
        }
        String username = MinecraftClient.getInstance().getSession().getUsername();
        MutableText empty = Text.empty();
        stringVisitable.visit((style, str) -> {
            if (str.contains(username)) {
                for (String part : str.split("((?=%s)|(?<=%s))".formatted(username, username))) {
                    if (part.equals(username)) {
                        Color value = getBetterchat().color.getValue();
                        MutableText styled = Text.literal(part).styled(st -> {
                            return st.withColor(new Color(value.getRed(), value.getGreen(), value.getBlue(), 255).getRGB());
                        });
                        styled.styled(style2 -> {
                            style2.withBold(Boolean.valueOf(style.isBold()));
                            style2.withItalic(Boolean.valueOf(style.isItalic()));
                            style2.withUnderline(Boolean.valueOf(style.isUnderlined()));
                            style2.withObfuscated(Boolean.valueOf(style.isObfuscated()));
                            style2.withStrikethrough(Boolean.valueOf(style.isStrikethrough()));
                            return style2;
                        });
                        empty.append(styled);
                    } else {
                        empty.append(Text.literal(part).setStyle(style));
                    }
                }
            } else {
                empty.append(Text.literal(str).setStyle(style));
            }
            return Optional.empty();
        }, Style.EMPTY);
        return empty;
    }
}
