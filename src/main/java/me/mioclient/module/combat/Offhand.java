package me.mioclient.module.combat;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.module.combat.offhand.OffhandMode;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.client.InputState;
import me.mioclient.util.combat.CrystalThreatDetector;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.combat.CrystalThreatDetector;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.client.InputState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.render.RenderConstants;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.module.combat.offhand.OffhandMode;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.render.RenderEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.player.NoInteract;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import me.mioclient.module.Lag;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/combat/Offhand.class */
public class Offhand extends Module {
    public static final AutoCrystal autoCrystal = (AutoCrystal) ClientServices.moduleInjector.getModule(AutoCrystal.class);
    public static final Offhand offhand = (Offhand) ClientServices.moduleInjector.getModule(Offhand.class);
    public static final NoInteract noInteract = (NoInteract) ClientServices.moduleInjector.getModule(NoInteract.class);
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Setting<OffhandMode> item = add(new EnumSetting<>("Item", OffhandMode.Totem));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 25, 0, 500).setSuffix("ms"));
    public Setting<Float> health = add(new NumberSetting<>("Health", 16.0f, 2.0f, 36.0f).setSuffix("hp").setHoleSnapConfig("Adaptive", HoleSnapMode.MIN));
    public Setting<Boolean> lethal = add(new BooleanSetting("Lethal", true));
    public Setting<Boolean> f117 = add(new BooleanSetting("1.17+", true));
    public Setting<Boolean> containers = add(new BooleanSetting("Containers", true));
    public Setting<Boolean> gappleBind = add(new BooleanSetting("GappleBind", true).setVisible());
    public Setting<Float> safe = add(new NumberSetting<>("Safe", 8.0f, 0.0f, 36.0f, v -> this.gappleBind.isShown()).setSuffix("hp"));
    public Setting<Boolean> pickaxe = add(new BooleanSetting("Pickaxe", true, v -> this.gappleBind.isShown()));
    public Setting<Boolean> crystals = add(new BooleanSetting("Crystals", true, v -> this.gappleBind.isShown()));
    public Setting<Boolean> totems = add(new BooleanSetting("Totems", false).requiresSettings(this.gappleBind));
    public Setting<Boolean> crapple = add(new BooleanSetting("Crapple", false, v -> this.item.getValue() == OffhandMode.Gapple || this.gappleBind.isShown()));
    public Setting<Set<Item>> custom = add(new EntityListObjectSetting<>("Custom", Registries.ITEM));
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;
    public final Stopwatch stopwatch3;
    public final Stopwatch stopwatch4;
    public final List<Long> list;
    public int num;
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public final CrystalThreatDetector mainhandHelper;
    public static boolean flag4;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/combat/Offhand$Inner.class */
    class Inner extends CrystalThreatDetector {
        public Inner() {
        }

        @Override // me.mioclient.util.combat.CrystalThreatDetector
        public boolean isEnabled() {
            if (minecraftClient.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                return false;
            }
            return Offhand.this.lethal.getValue().booleanValue() || Offhand.this.health.isAtMinimum();
        }
    }

    public Offhand() {
        super("Offhand", "Manages your offhand automatically.", Category.COMBAT, "autototem");
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
        this.stopwatch3 = new Stopwatch();
        this.stopwatch4 = new Stopwatch();
        this.list = new ArrayList();
        this.num = -1;
        this.mainhandHelper = new Inner();
        this.health.setHoleSnapConfig("Adaptive", HoleSnapMode.MIN);
        this.lethal.addVisibilityPredicate(bool -> {
            return !this.health.isAtMinimum();
        });
        this.gappleBind.setConfigName("GappleBind");
        this.safe.setConfigName("SafeHealth");
        setupExclusiveItemCallback(this.custom);
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return this.item.getValue().toString();
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.num = -1;
    }

    @Listen(get219= RenderConstants.num2)
    public void onRender(RenderEvent renderEvent) {
        this.flag2 = renderEvent.isThirdPerson();
        tickOffhand();
    }

    public void tickOffhand() {
        if (isNotInGame()) {
            return;
        }
        if (minecraftClient.player.isAlive()) {
            this.flag3 = false;
        }
        flag4 = false;
        this.mainhandHelper.recalculateLayout();
        if (!isForceSuicideTotemMode() && isOffhandSwapAllowed()) {
            if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue())) {
                this.list.removeIf(l -> {
                    return l == null || System.currentTimeMillis() > l.longValue() + 1000;
                });
                if (this.list.size() > 4) {
                    this.stopwatch2.reset();
                }
                do906();
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        EntityStatusS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof EntityStatusS2CPacket ? (EntityStatusS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof EntityStatusS2CPacket) {
            EntityStatusS2CPacket entityStatusS2CPacket = packet904;
            if (!isForceSuicideTotemMode() && isOffhandSwapAllowed() && entityStatusS2CPacket.getEntity(minecraftClient.world) == minecraftClient.player && entityStatusS2CPacket.getStatus() == 35) {
                if (!minecraftClient.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                    if (minecraftClient.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                        minecraftClient.player.getOffHandStack().decrement(1);
                    }
                }
                this.stopwatch4.reset();
                this.list.clear();
                do906();
            }
        }
        EntitySpawnS2CPacket packet9042 = (channelRead0Event.getPacket()) instanceof EntitySpawnS2CPacket ? (EntitySpawnS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet9042 instanceof EntitySpawnS2CPacket) {
            EntitySpawnS2CPacket entitySpawnS2CPacket = packet9042;
            if (this.mainhandHelper.isEnabled() && entitySpawnS2CPacket.getEntityType() == EntityType.END_CRYSTAL) {
                if (this.mainhandHelper.isLethalExplosionAt(new Vec3d(entitySpawnS2CPacket.getX(), entitySpawnS2CPacket.getY(), entitySpawnS2CPacket.getZ()))) {
                    this.stopwatch3.reset();
                }
            }
        }
        if (channelRead0Event.getPacket() instanceof DeathMessageS2CPacket) {
            if (minecraftClient.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                do2667();
            }
        }
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if (interactBlockEvent.getHand() != Hand.MAIN_HAND || resolveOffhandMode((OffhandMode) null) == null || antiCheat.isF2b2t()) {
            return;
        }
        minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.OFF_HAND);
        interactBlockEvent.cancel();
    }

    public OffhandMode getOffhandMode2661() {
        if (autoCrystal.isToggled() && autoCrystal.forceSuicide.getValue().booleanValue() && this.item.getValue() != OffhandMode.Totem) {
            return OffhandMode.Crystal;
        }
        boolean booleanValue = this.crapple.getValue().booleanValue();
        OffhandMode offhandMode = OffhandMode.Totem;
        if (this.mainhandHelper.isThreatActive()) {
            return OffhandMode.Totem;
        }
        if (EntityUtil.getPlayerHealth() > this.health.getValue().floatValue() && minecraftClient.player.fallDistance < Float.intBitsToFloat(1098907648) && InventoryUtils.countItems(itemStack -> {
            return itemStack.isOf(this.item.getValue().getOffhandItem(booleanValue));
        }) > 0) {
            offhandMode = this.item.getValue();
        }
        return resolveOffhandMode(offhandMode);
    }

    public boolean is2662(Item item) {
        if ((item instanceof SwordItem) || (item instanceof AxeItem)) {
            return true;
        }
        if ((item instanceof PickaxeItem) && this.pickaxe.getValue().booleanValue()) {
            return true;
        }
        if (item == Items.TOTEM_OF_UNDYING && this.totems.getValue().booleanValue()) {
            return true;
        }
        return item == Items.END_CRYSTAL && this.crystals.getValue().booleanValue();
    }

    public OffhandMode resolveOffhandMode(OffhandMode offhandMode) {
        if (offhandMode != null) {
            if (offhandMode.getOffhandItem(this.crapple.getValue().booleanValue()) instanceof ShieldItem) {
                return offhandMode;
            }
        }
        if (WorldUtils.isInteractableHit(minecraftClient.crosshairTarget) && !minecraftClient.player.isSneaking() && !noInteract.isToggled()) {
            return offhandMode;
        }
        float f = EntityUtil.getPlayerHealth();
        Item item = minecraftClient.player.getMainHandStack().getItem();
        boolean z = this.safe.getValue().floatValue() < f;
        if (item == Items.TOTEM_OF_UNDYING) {
            z = true;
        }
        return (this.gappleBind.getValue().booleanValue() && minecraftClient.options.useKey.isPressed() && z && is2662(item) && minecraftClient.currentScreen == null && this.item.getValue() != OffhandMode.Gapple) ? OffhandMode.Gapple : offhandMode;
    }

    public void beginOffhandSwap() {
        this.list.add(Long.valueOf(System.currentTimeMillis()));
        ClientServices.fireworksHelper.setSyncBatching(true);
        if (ClientServices.holeSnapRenderHelper.isSprinting()) {
            PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING, 0);
            this.flag = true;
        }
    }

    public void endOffhandSwap() {
        ClientServices.fireworksHelper.setSyncBatching(false);
        if (!(minecraftClient.currentScreen instanceof HandledScreen)) {
            ClientServices.fireworksHelper.close();
        }
        if (this.flag) {
            PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.START_SPRINTING, 0);
            this.flag = false;
        }
    }

    public void do906() {
        OffhandMode offhandMode2661 = getOffhandMode2661();
        int i = offhandMode2661.findOffhandItemSlot(this.crapple.getValue().booleanValue());
        if (this.num != -1 && (!(minecraftClient.currentScreen instanceof HandledScreen) || (minecraftClient.currentScreen instanceof InventoryScreen))) {
            if (minecraftClient.player.getInventory().getStack(this.num >= 36 ? this.num - 36 : this.num).isOf(offhandMode2661.getOffhandItem(this.crapple.getValue().booleanValue())) && i != -1) {
                i = this.num;
            }
        }
        if (i == -1) {
            return;
        }
        if ((this.containers.getValue().booleanValue() || !((minecraftClient.currentScreen instanceof ShulkerBoxScreen) || (minecraftClient.currentScreen instanceof GenericContainerScreen))) && i < minecraftClient.player.currentScreenHandler.slots.size()) {
            ItemStack cursorStack = minecraftClient.player.currentScreenHandler.getCursorStack();
            ItemStack stack = minecraftClient.player.currentScreenHandler.getSlot(i).getStack();
            if (!stack.contains(DataComponentTypes.FOOD) || this.flag2) {
                beginOffhandSwap();
                int i2 = minecraftClient.player.currentScreenHandler.syncId;
                if (!cursorStack.isEmpty() && !InventoryUtils.isInventoryFull()) {
                    minecraftClient.interactionManager.clickSlot(i2, InventoryUtils.findSlot(Items.AIR), 0, SlotActionType.PICKUP, minecraftClient.player);
                }
                if (this.f117.getValue().booleanValue() && minecraftClient.player.currentScreenHandler.getCursorStack().isEmpty()) {
                    this.num = i;
                    minecraftClient.interactionManager.clickSlot(i2, i, 40, SlotActionType.SWAP, minecraftClient.player);
                    this.stopwatch.reset();
                } else {
                    do438(i);
                    this.num = i;
                }
                flag4 = true;
                endOffhandSwap();
                if (InputState.isKeyBindingActive(minecraftClient.options.useKey) && stack.contains(DataComponentTypes.FOOD)) {
                    minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.OFF_HAND);
                }
            }
        }
    }

    public void do438(int i) {
        if (this.stopwatch.hasElapsedMs(this.delay.getValue().intValue()) && minecraftClient.player.currentScreenHandler.slots.size() > 45) {
            InventoryUtils.swapStacks(i, 45);
            this.stopwatch.reset();
        }
    }

    public void setupExclusiveItemCallback(Setting<Set<Item>> setting) {
        setting.setChangeCallback(() -> {
            EntityListObjectSetting entityListObjectSetting = (EntityListObjectSetting) setting;
            if (entityListObjectSetting.getLastAdded() == null) {
                return;
            }
            ((Set) setting.getValue()).clear();
            ((Set) setting.getValue()).add((Item) entityListObjectSetting.getLastAdded());
        });
    }

    public void do2667() {
        long j = this.stopwatch4.getElapsedTime();
        int i = ClientServices.holeSnapRenderHelper.getPing();
        if (this.flag3) {
            return;
        }
        Text append = Text.literal("Since last pop: %dms. Latency: %dms.".formatted(Long.valueOf(j), Integer.valueOf(i))).append(Text.literal(" Possible death reasons: Damage override/Lag spike.*").styled(style -> {
            return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("More info in the game log.")));
        }));
        if (i > j) {
            ChatUtils.sendPrefixedMessage(append, ChatUtils.createSignature((Math.abs(getName().hashCode()) * (-1)) - 1), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode);
            System.out.println("*Death was most likely caused by you taking damage before the server receives the update slot packet, making you die holding a totem client-side.");
            this.flag3 = true;
        }
    }

    public boolean isForceSuicideTotemMode() {
        return autoCrystal.isToggled() && autoCrystal.forceSuicide.getValue().booleanValue() && this.item.getValue() == OffhandMode.Totem;
    }

    public boolean isOffhandSwapAllowed() {
        if (this.f117.getValue().booleanValue()) {
            return true;
        }
        return ((minecraftClient.currentScreen instanceof CreativeInventoryScreen) || minecraftClient.player.playerScreenHandler != minecraftClient.player.currentScreenHandler || (minecraftClient.currentScreen instanceof GenericContainerScreen)) ? false : true;
    }

    public static boolean is929() {
        return flag4 && offhand.isToggled();
    }
}
