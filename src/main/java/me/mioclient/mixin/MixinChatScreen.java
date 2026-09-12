package me.mioclient.mixin;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.screen.CategorySelectScreen;
import me.mioclient.gui.widget.ScrollState;
import me.mioclient.util.math.MathConstants;

import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.api.ClientServices;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.MathConstants;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import me.mioclient.ducks.DuckChatHudLineVisible;
import me.mioclient.gui.screen.CategorySelectScreen;
import me.mioclient.gui.widget.ScrollState;
import me.mioclient.mixin.ducks.DuckSuggestionWindow;
import me.mioclient.module.client.HUD;
import me.mioclient.module.client.IRC;
import me.mioclient.module.client.UI;
import me.mioclient.module.misc.BetterChat;
import me.mioclient.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.mioclient.mixin.ducks.DuckChatInputSuggester;

/* compiled from: 0.java */
@Mixin({ChatScreen.class})
/* loaded from: mio-yarn.jar:me/mioclient/mixin/MixinChatScreen.class */
public class MixinChatScreen extends Screen {
    private static BetterChat betterchat;

    private static BetterChat getBetterchat() {

        if (betterchat == null && ClientServices.moduleInjector != null) {

            try { betterchat = (BetterChat) ClientServices.moduleInjector.getModule(BetterChat.class); } catch (Exception e) {}

        }

        return betterchat;

    }
    private static IRC irc;

    private static IRC getIrc() {

        if (irc == null && ClientServices.moduleInjector != null) {

            try { irc = (IRC) ClientServices.moduleInjector.getModule(IRC.class); } catch (Exception e) {}

        }

        return irc;

    }
    private static HUD hud;

    private static HUD getHud() {

        if (hud == null && ClientServices.moduleInjector != null) {

            try { hud = (HUD) ClientServices.moduleInjector.getModule(HUD.class); } catch (Exception e) {}

        }

        return hud;

    }
    private static NoRender norender;

    private static NoRender getNorender() {

        if (norender == null && ClientServices.moduleInjector != null) {

            try { norender = (NoRender) ClientServices.moduleInjector.getModule(NoRender.class); } catch (Exception e) {}

        }

        return norender;

    }

    @Shadow
    protected TextFieldWidget field_2382;

    @Shadow
    ChatInputSuggestor field_21616;

    @Unique
    private ScrollState scroll;

    protected MixinChatScreen(Text text) {
        super(text);
    }

    @Inject(method = {"init"}, at = {@At("HEAD")})
    private void initHook(CallbackInfo callbackInfo) {
        BetterChat betterchat_lcl = getBetterchat();

        if (betterchat_lcl == null) return;

        this.scroll = new ScrollState();
        if (betterchat_lcl.always.getValue().booleanValue() && betterchat_lcl.isAnimationEnabled()) {
            for (DuckChatHudLineVisible signatureHelper : (java.util.List<DuckChatHudLineVisible>)(java.util.List) MinecraftClient.getInstance().inGameHud.getChatHud().visibleMessages) {
                signatureHelper.getProgress().setToggledImmediate(false);
                signatureHelper.mio$setAddTime(System.currentTimeMillis());
            }
        }
    }

    @Inject(method = {"render"}, at = {@At("RETURN")})
    private void drawCommandIndicator(DrawContext drawContext, int i, int i2, float f, CallbackInfo callbackInfo) {
        HUD hud_lcl = getHud();
        IRC irc_lcl = getIrc();

        if (hud_lcl == null) return;

        if (irc_lcl == null) return;

        boolean startsWith = this.field_2382.getText().startsWith(ClientCommandRegistry.getPrefix());
        if (irc_lcl.isToggled() && irc_lcl.chat.getValue().booleanValue() && this.field_2382.getText().startsWith(irc_lcl.prefix.getValue())) {
            startsWith = true;
        }
        Color value = UI.uI.color.getValue();
        if (startsWith) {
            RenderUtil.INSTANCE.drawRectOutline(drawContext.getMatrices(), 1.0f, (this.height - 2) - hud_lcl.getBottomOffsetTarget(), this.width - 2, this.height - 2, value);
        }
    }

    @WrapWithCondition(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V")})
    private boolean suppressIndicatorBg(DrawContext drawContext, int i, int i2, int i3, int i4, int i5) {
        HUD hud_lcl = getHud();

        if (hud_lcl == null) return true;

        RenderUtil.INSTANCE.drawFilledRectColor(drawContext.getMatrices(), i, i2 + ((i4 - i2) * (1.0f - (hud_lcl.getBottomOffsetTarget() / 13.5f))), i3, i4, new Color(i5, true));
        return false;
    }

    @Redirect(method = {"render"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void renderChatInput(TextFieldWidget textFieldWidget, DrawContext drawContext, int i, int i2, float f) {
        HUD hud_lcl = getHud();

        if (hud_lcl == null) return;

        if (hud_lcl.getBottomOffsetTarget() / 13.5f > MathConstants.val3) {
            textFieldWidget.render(drawContext, i, i2, f);
        }
    }

    @Inject(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.BEFORE)})
    private void renderHook4(DrawContext drawContext, int i, int i2, float f, CallbackInfo callbackInfo) {
        if (this.field_2382.getText().startsWith(ClientCommandRegistry.getPrefix())) {
            DuckSuggestionWindow window = (me.mioclient.mixin.ducks.DuckSuggestionWindow)(Object) ((me.mioclient.mixin.ducks.DuckChatInputSuggester)(Object) this.field_21616).getWindow();
            CompletableFuture<Suggestions> suggestion = ((me.mioclient.mixin.ducks.DuckChatInputSuggester)(Object) this.field_21616).getSuggestion();
            if (suggestion != null && suggestion.isDone() && this.field_2382.getText().startsWith(ClientCommandRegistry.getPrefix())) {
                String str = "";
                Suggestions join = suggestion.join();
                ParseResults<CommandSource> parse = ((me.mioclient.mixin.ducks.DuckChatInputSuggester)(Object) this.field_21616).getParse();
                List<String> emptyList = Collections.emptyList();
                if (parse != null && this.field_2382.getCursor() == this.field_2382.getText().length()) {
                    List nodes = parse.getContext().getNodes();
                    if (!parse.getContext().getNodes().isEmpty()) {
                        emptyList = getStrings(((ParsedCommandNode) nodes.get(nodes.size() - 1)).getNode().getChildren());
                    }
                }
                if (window != null) {
                    String apply = ((Suggestion) join.getList().get(window.getSelection())).apply(window.getTypedText());
                    str = str + (apply.startsWith(this.field_2382.getText()) ? apply.substring(this.field_2382.getText().length()) : "");
                }
                if ((this.field_2382.getCursor() > 0 && this.field_2382.getText().charAt(this.field_2382.getCursor() - 1) != ' ') || !str.isEmpty()) {
                    str = str + " ";
                }
                if (parse != null && parse.getReader().canRead() && ((parse.getReader().peek() != ' ' || window != null) && !emptyList.isEmpty())) {
                    emptyList.remove(0);
                }
                this.field_2382.setSuggestion(str + String.join(" ", emptyList));
            }
        }
    }

    @WrapWithCondition(method = {"render"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawOrderedTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V")})
    private boolean shouldSuppressTooltipRender(DrawContext drawContext, TextRenderer textRenderer, List<? extends OrderedText> list, int i, int i2) {
        NoRender norender_lcl = getNorender();

        if (norender_lcl == null) return true;

        return (norender_lcl.isToggled() && norender_lcl.messageIndicator.getValue().booleanValue()) ? false : true;
    }

    public void filesDragged(List<Path> list) {
        CategorySelectScreen.tryOpenForDroppedFiles(this, list);
    }

    private List<String> getStrings(Collection<CommandNode<CommandSource>> collection) {
        ArrayList arrayList = new ArrayList();
        while (true) {
            CommandNode<CommandSource> commandNode = null;
            int i = 0;
            StringBuilder sb = new StringBuilder("<");
            for (CommandNode<CommandSource> commandNode2 : collection) {
                i++;
                sb.append(commandNode2.getName());
                if (i != collection.size()) {
                    sb.append(", ");
                } else {
                    commandNode = commandNode2;
                }
            }
            sb.append(">");
            if (!collection.isEmpty()) {
                arrayList.add(sb.toString());
            }
            if (collection.size() != 1 || commandNode == null) {
                break;
            }
            collection = commandNode.getChildren();
        }
        return arrayList;
    }
}
