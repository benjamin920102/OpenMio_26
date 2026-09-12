package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.render.font.AdvanceGlyph;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.api.ClientServices;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.client.ChatUtils;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.ConcurrentModificationException;
import java.util.List;
import me.mioclient.render.font.AdvanceGlyph;
import me.mioclient.ducks.DuckChatHud;
import me.mioclient.ducks.DuckChatHudLineVisible;
import me.mioclient.event.events.network.AddMessageEvent;
import me.mioclient.module.Chat;
import me.mioclient.module.client.Fonts;
import me.mioclient.module.misc.BetterChat;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* compiled from: 0.java */
@Mixin({ChatHud.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinChatHud.class */
public abstract class MixinChatHud implements ClientAccessor, DuckChatHud {
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }
    private static BetterChat betterchat;

    private static BetterChat getBetterchat() {

        if (betterchat == null && ClientServices.moduleInjector != null) {

            try { betterchat = (BetterChat) ClientServices.moduleInjector.getModule(BetterChat.class); } catch (Exception e) {}

        }

        return betterchat;

    }
    private static Chat chathud;

    private static Chat getChathud() {

        if (chathud == null && ClientServices.moduleInjector != null) {

            try { chathud = (Chat) ClientServices.moduleInjector.getModule(Chat.class); } catch (Exception e) {}

        }

        return chathud;

    }
    private static Fonts fonts;

    private static Fonts getFonts() {

        if (fonts == null && ClientServices.moduleInjector != null) {

            try { fonts = (Fonts) ClientServices.moduleInjector.getModule(Fonts.class); } catch (Exception e) {}

        }

        return fonts;

    }

    @Shadow
    @Final
    private List<ChatHudLine.Visible> field_2064;

    @Unique
    private MessageSignatureData last;

    @Unique
    private int current;

    @Unique
    private static boolean skip;

    @Shadow
    protected abstract void method_45027(ChatHudLine chatHudLine);

    @Shadow
    protected abstract void method_1815(ChatHudLine chatHudLine);

    @Shadow
    protected abstract void method_58744(ChatHudLine chatHudLine);

    @Shadow
    public abstract void method_44811(Text text, @Nullable MessageSignatureData messageSignatureData, @Nullable MessageIndicator messageIndicator);

    @Shadow
    public abstract double method_1814();

    @Shadow
    protected abstract int method_44752();

    private void drawIndicatorIcon(MatrixStack matrixStack, int i, int i2, MessageIndicator.Icon icon) {
    }

    @Inject(method = {"logChatMessage"}, at = {@At("HEAD")}, cancellable = true)
    private void logChatMessageHook(ChatHudLine chatHudLine, CallbackInfo callbackInfo) {
        if (chatHudLine.indicator() == ChatUtils.messageIndicator) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"addVisibleMessage"}, at = {@At("HEAD")})
    private void addMessage(ChatHudLine chatHudLine, CallbackInfo callbackInfo) {
        BetterChat.flag = true;
        MessageSignatureData signature = chatHudLine.signature();
        this.last = signature;
        if (signature == null || signature.toByteBuffer().getInt() >= 0) {
            return;
        }
        try {
            minecraftClient.inGameHud.getChatHud().visibleMessages.removeIf(visible -> {
                return ((visible == null || ((DuckChatHudLineVisible)(Object) visible).getSignature() == null || !((DuckChatHudLineVisible)(Object) visible).getSignature().equals(signature)) ? false : true);
            });
        } catch (ConcurrentModificationException e) {
        }
    }

    @Inject(method = {"addVisibleMessage"}, at = {@At("RETURN")})
    private void noracismcattyn(ChatHudLine chatHudLine, CallbackInfo callbackInfo) {
        BetterChat.flag = false;
    }

    @Redirect(method = {"addVisibleMessage"}, at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0))
    private <E> void visibleInitHook(List<E> list, int i, E e) {
        ((DuckChatHudLineVisible) e).setSignature(this.last);
        ClientAccessor.eventBus.fireEvent(new AddMessageEvent((ChatHudLine.Visible) e));
        list.add(i, e);
    }

    @Redirect(method = {"render"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;indicator()Lnet/minecraft/client/gui/hud/MessageIndicator;"), require = 0)
    private MessageIndicator renderHook(ChatHudLine.Visible visible) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return null;

        if (norender_lcl.isToggled() && norender_lcl.messageIndicator.getValue().booleanValue()) {
            return null;
        }
        return visible.indicator();
    }

    @WrapWithCondition(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 1)})
    private boolean suppressIndicatorBg(DrawContext drawContext, int i, int i2, int i3, int i4, int i5) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return true;

        return (norender_lcl.isToggled() && norender_lcl.messageIndicator.getValue().booleanValue()) ? false : true;
    }

    @WrapOperation(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I")})
    private int textHook(DrawContext drawContext, TextRenderer textRenderer, OrderedText orderedText, int i, int i2, int i3, Operation<Integer> operation) {
        BetterChat betterchat_lcl = getBetterchat();
        NoRender norender_lcl = getNorender();
        Fonts fonts_lcl = getFonts();

        if (betterchat_lcl == null) return 0;

        if (norender_lcl == null) return 0;

        if (fonts_lcl == null) return 0;

        int i4 = i - ((norender_lcl.isToggled() && norender_lcl.messageIndicator.getValue().booleanValue()) ? 3 : 0);
        if (betterchat_lcl.isAnimationEnabled() && this.current < this.field_2064.size()) {
            DuckChatHudLineVisible signatureHelper = (DuckChatHudLineVisible)(Object)(this.field_2064.get(this.current));
            signatureHelper.getProgress().animateToggle(true);
            float f = signatureHelper.getProgress().getValue();
            long mio$getAddTime = signatureHelper.mio$getAddTime();
            boolean z = betterchat_lcl.type.getValue() == BetterChat.BetterChatMode_2.BOTH;
            if (betterchat_lcl.type.getValue() == BetterChat.BetterChatMode_2.VERTICAL || z) {
                i2 = (int) (i2 + (9.0f * (1.0f - f)));
            }
            if (betterchat_lcl.type.getValue() == BetterChat.BetterChatMode_2.HORIZONTAL || z) {
                i4 = (int) (i4 - (textRenderer.getWidth(orderedText) * (1.0f - f)));
            }
            if (betterchat_lcl.alpha.getValue().intValue() != 0) {
                i3 = (i3 & 16777215) | (((int) Math.max(((i3 >> 24) & 255) * MathHelper.clamp(((float) (System.currentTimeMillis() - mio$getAddTime)) / betterchat_lcl.alpha.getValue().intValue(), 0.0f, 1.0f), 10.0f)) << 24);
            }
        }
        AdvanceGlyph advanceGlyph1686 = ClientTextRenderer.INSTANCE.getAdvanceGlyph();
        if (advanceGlyph1686 != null && fonts_lcl.isToggled() && fonts_lcl.chat.getValue().booleanValue()) {
            advanceGlyph1686.renderOrderedText(drawContext.getMatrices(), orderedText, i4, i2, i3, true);
            return 0;
        }
        operation.call(new Object[]{drawContext, textRenderer, orderedText, Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3)});
        return 0;
    }

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0)})
    private void renderPreHook(DrawContext drawContext, int i, int i2, int i3, boolean z, CallbackInfo callbackInfo) {
        BetterChat betterchat_lcl = getBetterchat();
        Chat chathud_lcl = getChathud();

        if (betterchat_lcl == null) return;

        if (chathud_lcl == null) return;

        float f = 0.0f;
        float f2 = 0.0f;
        if (chathud_lcl.isToggled()) {
            f = 0.0f + chathud_lcl.getChatXOffset();
            f2 = 0.0f + chathud_lcl.getChatYOffset();
        }
        if (betterchat_lcl.isAnimationEnabled() && betterchat_lcl.type.getValue() == BetterChat.BetterChatMode_2.BOUNCE) {
            f2 += (betterchat_lcl.progress.getRawValue() - 1.0f) * 8.0f;
        }
        if (f == 0.0f && f2 == 0.0f) {
            return;
        }
        drawContext.getMatrices().translate(f, f2, 0.0f);
    }

    @Inject(method = {"render"}, at = {@At("TAIL")})
    private void render(DrawContext drawContext, int i, int i2, int i3, boolean z, CallbackInfo callbackInfo) {
        ClientTextRenderer.INSTANCE.resetShadowOffset();
    }

    @ModifyArg(method = {"render"}, at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0))
    private int renderHook4(int i) {
        this.current = i;
        return i;
    }

    @Inject(method = {"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;logChatMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", shift = At.Shift.BEFORE)}, cancellable = true)
    private void addMessageHook(Text text, MessageSignatureData messageSignatureData, MessageIndicator messageIndicator, CallbackInfo callbackInfo, @Local ChatHudLine chatHudLine) {
        if (skip) {
            return;
        }
        AddMessageEvent addMessageEvent = new AddMessageEvent(messageIndicator, messageSignatureData, text);
        ClientAccessor.eventBus.fireEvent(addMessageEvent);
        Text text2279 = addMessageEvent.getText();
        MessageSignatureData signature = addMessageEvent.getSignature();
        callbackInfo.cancel();
        if (addMessageEvent.isCancelled()) {
            return;
        }
        if (text.equals(text2279)) {
            method_45027(chatHudLine);
            method_1815(chatHudLine);
            method_58744(chatHudLine);
        } else {
            skip = true;
            method_44811(text2279, signature, messageIndicator);
            skip = false;
        }
    }

    @ModifyExpressionValue(method = {"addVisibleMessage"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2)})
    public int chatHistory(int i) {
        BetterChat betterchat_lcl = getBetterchat();

        if (betterchat_lcl == null) return i;

        if (betterchat_lcl.isToggled() && betterchat_lcl.longChatHistory.getValue().booleanValue()) {
            return 1;
        }
        return i;
    }

    @ModifyExpressionValue(method = {"addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"}, at = {@At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0)})
    public int extendAddMessageHistory(int i) {
        BetterChat betterchat_lcl = getBetterchat();

        if (betterchat_lcl == null) return i;

        if (betterchat_lcl.isToggled() && betterchat_lcl.longChatHistory.getValue().booleanValue()) {
            return 1;
        }
        return i;
    }

    @Inject(method = {"clear"}, at = {@At("HEAD")}, cancellable = true)
    private void clearHook(boolean z, CallbackInfo callbackInfo) {
        BetterChat betterchat_lcl = getBetterchat();

        if (betterchat_lcl == null) return;

        if (betterchat_lcl.isToggled() && betterchat_lcl.noReset.getValue().booleanValue() && !BetterChat.flag2) {
            callbackInfo.cancel();
        }
    }

    @WrapOperation(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V")})
    private void fillHook(DrawContext drawContext, int i, int i2, int i3, int i4, int i5, Operation<Void> operation) {
        operation.call(new Object[]{drawContext, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5)});
    }

    @WrapOperation(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIIII)V")})
    private void fillHook(DrawContext drawContext, int i, int i2, int i3, int i4, int i5, int i6, Operation<Void> operation) {
        operation.call(new Object[]{drawContext, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)});
    }

    @ModifyExpressionValue(method = {"addToMessageHistory"}, at = {@At(value = "INVOKE", target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z")})
    private boolean addToMessageHistory(boolean z, @Local(argsOnly = true) String str) {
        return z || str.startsWith(ClientCommandRegistry.getPrefix());
    }

    @ModifyExpressionValue(method = {"getTextStyleAt"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getTextHandler()Lnet/minecraft/client/font/TextHandler;")})
    private TextHandler getTextStyleAtHook(TextHandler textHandler) {
        Fonts fonts_lcl = getFonts();

        if (fonts_lcl == null) return textHandler;

        AdvanceGlyph advanceGlyph1686 = ClientTextRenderer.INSTANCE.getAdvanceGlyph();
        return (advanceGlyph1686 != null && fonts_lcl.isToggled() && fonts_lcl.chat.getValue().booleanValue()) ? advanceGlyph1686.getTextHandler() : textHandler;
    }

    @Inject(method = {"toChatLineX"}, at = {@At("HEAD")}, cancellable = true)
    private void toChatLineX(double d, CallbackInfoReturnable<Double> callbackInfoReturnable) {
        Chat chathud_lcl = getChathud();

        if (chathud_lcl == null) return;

        if (!chathud_lcl.isToggled() || chathud_lcl.getChatXOffset() == 0.0f) {
            return;
        }
        callbackInfoReturnable.setReturnValue(Double.valueOf((d - chathud_lcl.getElement().getX()) / method_1814()));
    }

    @Inject(method = {"toChatLineY"}, at = {@At("HEAD")}, cancellable = true)
    private void toChatLineY(double d, CallbackInfoReturnable<Double> callbackInfoReturnable) {
        Chat chathud_lcl = getChathud();

        if (chathud_lcl == null) return;

        if (!chathud_lcl.isToggled() || chathud_lcl.getChatYOffset() == 0.0f) {
            return;
        }
        callbackInfoReturnable.setReturnValue(Double.valueOf(((chathud_lcl.getElement().getY() - d) + chathud_lcl.getElement().getMeasuredSize()[1]) / (method_1814() * method_44752())));
    }

    @Override // me.mioclient.ducks.DuckChatHud
    public List<ChatHudLine.Visible> getVisible() {
        return this.field_2064;
    }
}
