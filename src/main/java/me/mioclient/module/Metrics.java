package me.mioclient.module;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.hud.TextHudElement;

import me.mioclient.api.ClientServices;
import me.mioclient.render.hud.HudTextLine;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.hud.TextHudElement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.mode.client.ModuleListMode;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import me.mioclient.module.movement.Speed;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/Metrics.class */
public class Metrics extends me.mioclient.module.HudModule {
    public Setting<Boolean> setting;
    public Setting<Boolean> setting2;
    public Setting<Boolean> setting3;
    public Setting<Boolean> setting4;
    public Setting<Boolean> setting5;
    public Setting<Boolean> setting6;
    public Setting<Boolean> setting7;
    public Setting<Boolean> setting8;
    public Setting<Boolean> setting9;
    public final List<HudTextLine> list;
    public int num;

    public Metrics() {
        super("Metrics", "infohud", "info");
        this.setting = add(new BooleanSetting("Speed", true).setVisible());
        this.setting2 = add(new BooleanSetting("BPS", false).requiresSettings(this.setting));
        this.setting3 = add(new BooleanSetting("TPS", true));
        this.setting4 = add(new BooleanSetting("Ping", true));
        this.setting5 = add(new BooleanSetting("FPS", true));
        this.setting6 = add(new BooleanSetting("ServerBrand", true));
        this.setting7 = add(new BooleanSetting("Durability", false));
        this.setting8 = add(new BooleanSetting("Chest", false).setVisible());
        this.setting9 = add(new BooleanSetting("Double", false).requiresSettings(this.setting8));
        this.list = new ArrayList();
        setElement(new TextHudElement(this, this.list));
        getElement().setAnchor(ModuleListMode.BOTTOM_RIGHT);
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.list.clear();
        List<HudTextLine> list = this.list;
        Supplier supplier = () -> {
            String string2921 = new StringTemplate().arg(this.setting2.getValue().booleanValue() ? "b/s" : "km/h").format("Speed %s%.2f\u0001");
            Object[] objArr = new Object[2];
            objArr[0] = Formatting.WHITE;
            objArr[1] = Double.valueOf(ClientServices.feetPlaceHelper.getSmoothedSpeed() / (this.setting2.getValue().booleanValue() ? Double.longBitsToDouble(4615288898129284301L) : Double.longBitsToDouble(4607182418800017408L)));
            return Text.literal(string2921.formatted(objArr).replace(",", "."));
        };
        Setting<Boolean> setting = this.setting;
        Objects.requireNonNull(setting);
        HudTextLine cryptoHelper = new HudTextLine(supplier, setting::getValue);
        Supplier supplier2 = () -> {
            return Text.literal("TPS %s%.2f".formatted(Formatting.WHITE, Float.valueOf(ClientServices.holeSnapRenderHelper.getSmoothedTickRate())).replace(",", "."));
        };
        Setting<Boolean> setting2 = this.setting3;
        Objects.requireNonNull(setting2);
        HudTextLine cryptoHelper2 = new HudTextLine(supplier2, setting2::getValue);
        Supplier supplier3 = () -> {
            return Text.literal("Ping %s%dms".formatted(Formatting.WHITE, Integer.valueOf(ClientServices.holeSnapRenderHelper.getPing())));
        };
        Setting<Boolean> setting3 = this.setting4;
        Objects.requireNonNull(setting3);
        HudTextLine cryptoHelper3 = new HudTextLine(supplier3, setting3::getValue);
        Supplier supplier4 = () -> {
            return Text.literal("FPS %s%d".formatted(Formatting.WHITE, Integer.valueOf(ClientServices.hitmarkerHelper.getFrameRate())));
        };
        Setting<Boolean> setting4 = this.setting5;
        Objects.requireNonNull(setting4);
        HudTextLine cryptoHelper4 = new HudTextLine(supplier4, setting4::getValue);
        Supplier supplier5 = () -> {
            return Text.literal("ServerBrand %s%s".formatted(Formatting.WHITE, minecraftClient.player.networkHandler.getBrand()));
        };
        Setting<Boolean> setting5 = this.setting6;
        Objects.requireNonNull(setting5);
        HudTextLine cryptoHelper5 = new HudTextLine(supplier5, setting5::getValue);
        HudTextLine cryptoHelper6 = new HudTextLine(() -> {
            return Text.literal("Durability %s%s".formatted(Formatting.WHITE, Integer.valueOf(minecraftClient.player.getMainHandStack().getMaxDamage() - minecraftClient.player.getMainHandStack().getDamage())));
        }, () -> {
            return Boolean.valueOf(this.setting7.getValue().booleanValue() && minecraftClient.player.getMainHandStack().isDamageable());
        });
        Supplier supplier6 = () -> {
            return Text.literal("Chests %s%d".formatted(Formatting.WHITE, Integer.valueOf(this.num)));
        };
        Setting<Boolean> setting6 = this.setting8;
        Objects.requireNonNull(setting6);
        list.addAll(List.of(cryptoHelper, cryptoHelper2, cryptoHelper3, cryptoHelper4, cryptoHelper5, cryptoHelper6, new HudTextLine(supplier6, setting6::getValue)));
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.list.sort(Comparator.comparing(cryptoHelper -> {
            return Float.valueOf(-ClientTextRenderer.INSTANCE.getStringWidth(cryptoHelper.getAsText().getString()));
        }));
        this.num = 0;
        for (BlockEntity blockEntity : ClientServices.stashFinderHelper.getLoadedBlockEntities()) {
            if ((blockEntity instanceof ChestBlockEntity) || (blockEntity instanceof BarrelBlockEntity)) {
                this.num++;
            }
        }
        if (this.setting9.getValue().booleanValue()) {
            this.num /= 2;
        }
    }
}
