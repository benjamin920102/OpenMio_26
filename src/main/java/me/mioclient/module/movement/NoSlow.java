package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.client.InventorySyncEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.api.NamedValue;
import me.mioclient.gui.minigame.MinigamePanel;
import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.screen.ClickGuiScreen;
import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.gui.widget.ChestSearchOverlay;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.anim.InterpolatedFloat;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.api.ClientServices;
import me.mioclient.gui.widget.ChestSearchOverlay;
import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.screen.ClickGuiScreen;
import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.api.NamedValue;
import me.mioclient.network.PacketSender;
import me.mioclient.util.world.BlockPlacer;
import me.mioclient.util.anim.InterpolatedFloat;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import java.util.Iterator;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.event.events.client.InventorySyncEvent;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.player.TickMovementEvent;
import me.mioclient.gui.minigame.MinigamePanel;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.mixin.ducks.DuckPlayerMoveC2SPacket;
import me.mioclient.module.Module;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EnumSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/NoSlow.class */
public class NoSlow extends Module {
    public static AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public Setting<Boolean> guiMove = add(new BooleanSetting("GuiMove", true));
    public Setting<Boolean> portals = add(new BooleanSetting("Portals", true));
    public Setting<Boolean> items = add(new BooleanSetting("Items", true));
    public Setting<Boolean> sneak = add(new BooleanSetting("Sneak", false));
    public Setting<Boolean> crawl = add(new BooleanSetting("Crawl", false));
    public Setting<Boolean> strict = add(new BooleanSetting("Strict", true).setDependent());
    public Setting<NoSlowMode> mode = add(new EnumSetting<>("Mode", NoSlowMode.NONE).requiresSettings(this.strict));
    public Setting<Boolean> strictInventory = add(new BooleanSetting("StrictInventory", false));
    public Setting<Boolean> multiTask = add(new BooleanSetting("MultiTask", true).requiresSettings(this.strict));
    public Setting<Boolean> blocks = add(new BooleanSetting("Blocks", false).setDependent());
    public Setting<Boolean> ice = add(new BooleanSetting("Ice", false, v -> this.blocks.isShown()));
    public Setting<Boolean> slime = add(new BooleanSetting("Slime", false, v -> this.blocks.isShown()));
    public Setting<Boolean> noSlimeBounce = add(new BooleanSetting("NoSlimeBounce", true, v -> this.blocks.isShown() && this.slime.getValue().booleanValue()));
    public Setting<Boolean> soulSand = add(new BooleanSetting("SoulSand", false, v -> this.blocks.isShown()));
    public Setting<Boolean> honey = add(new BooleanSetting("Honey", false, v -> this.blocks.isShown()));
    public Setting<Boolean> berryBush = add(new BooleanSetting("BerryBush", false, v -> this.blocks.isShown()));
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public final Stopwatch stopwatch;
    public final InterpolatedFloat hUDHelper_2;
    public final InterpolatedFloat hUDHelper_22;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/movement/NoSlow$NoSlowMode.class */
    public enum NoSlowMode implements NamedValue {
        NONE("None"),
        NCP("NCP"),
        GRIM("Grim"),
        GRIMV3("Slow"),
        CONSTANTIAM("Constantiam");

        public final String name;

        NoSlowMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    public NoSlow() {
        super("NoSlow", "Cancels several things that may slow you down.", Category.MOVEMENT, new String[0]);
        this.stopwatch = new Stopwatch();
        this.hUDHelper_2 = new InterpolatedFloat(Float.intBitsToFloat(1073741824), true);
        this.hUDHelper_22 = new InterpolatedFloat(Float.intBitsToFloat(1073741824), true);
        setDrawn(false);
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        DuckPlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (DuckPlayerMoveC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerMoveC2SPacket) {
            DuckPlayerMoveC2SPacket duckPlayerMoveC2SPacket = (DuckPlayerMoveC2SPacket)((PlayerMoveC2SPacket) packet904);
            ItemStack stackInHand = minecraftClient.player.getStackInHand(minecraftClient.player.getActiveHand());
            if (minecraftClient.player.isUsingItem() && this.items.getValue().booleanValue() && !minecraftClient.player.isRiding() && !minecraftClient.player.isFallFlying() && stackInHand.contains(DataComponentTypes.FOOD) && isNcpMode()) {
                minecraftClient.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(minecraftClient.player.getInventory().selectedSlot));
            }
            DuckPlayerMoveC2SPacket duckPlayerMoveC2SPacket2 = duckPlayerMoveC2SPacket;
            if (this.guiMove.getValue().booleanValue() && this.strictInventory.getValue().booleanValue() && !isGrimMode() && this.flag && !minecraftClient.player.isRiding() && minecraftClient.player.isOnGround() && this.stopwatch.hasElapsedMs(200L) && !minecraftClient.player.isFallFlying() && this.mode.getValue() != NoSlowMode.GRIMV3) {
                duckPlayerMoveC2SPacket2.setOnGround(false);
                this.flag = false;
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.flag2 = false;
        this.flag3 = false;
        if (minecraftClient.player.isOnGround()) {
            return;
        }
        this.stopwatch.reset();
    }

    @Listen
    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
        if (this.mode.getValue() == NoSlowMode.CONSTANTIAM && this.items.getValue().booleanValue() && !minecraftClient.player.isFallFlying()) {
            if (minecraftClient.player.getStackInHand(interactBlockEvent.getHand()).contains(DataComponentTypes.FOOD) && !BlockPlacer.needsSneakPlace(interactBlockEvent.getHitResult().getBlockPos())) {
                minecraftClient.interactionManager.interactItem(minecraftClient.player, Hand.MAIN_HAND);
                interactBlockEvent.cancel();
            }
        }
    }

    @Listen
    public void onInventorySync(InventorySyncEvent keyPearlModeEvent) {
        if (keyPearlModeEvent.getPhase() != KeyPearlMode.Pre || !this.strictInventory.getValue().booleanValue() || isGrimMode() || !this.guiMove.getValue().booleanValue()) {
            if (keyPearlModeEvent.getPhase() == KeyPearlMode.Post && MovementUtils.is2181() && isNcpMode() && this.guiMove.getValue().booleanValue()) {
                if (minecraftClient.player.isSprinting()) {
                    minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                }
                if (minecraftClient.player.isSneaking()) {
                    minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                    return;
                }
                return;
            }
            return;
        }
        if (!this.multiTask.getValue().booleanValue() && !this.flag2) {
            minecraftClient.interactionManager.stopUsingItem(minecraftClient.player);
            this.flag2 = true;
        }
        if (MovementUtils.is2181() && this.mode.getValue() != NoSlowMode.GRIMV3) {
            if (ClientServices.antiPhaseHelper.getGroundTicks() > 1 && !this.flag3) {
                PacketSender.sendPosition(minecraftClient.player.getX(), minecraftClient.player.getY() + Double.longBitsToDouble(4589175226049939217L), minecraftClient.player.getZ(), false);
                this.flag3 = true;
                this.flag = true;
            }
            if (minecraftClient.player.isSprinting()) {
                minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
            if (minecraftClient.player.isSneaking()) {
                minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
        }
    }

    @Listen
    public void onTickMovement(TickMovementEvent tickMovementEvent) {
        if (this.items.getValue().booleanValue() && minecraftClient.player.isUsingItem() && !minecraftClient.player.hasVehicle()) {
            boolean z = true;
            if (this.mode.getValue() == NoSlowMode.GRIMV3) {
                z = minecraftClient.player.age % 3 == 0 || minecraftClient.player.age % 4 == 0;
                if (minecraftClient.player.age % 12 == 0) {
                    z = false;
                }
                if (minecraftClient.player.isFallFlying()) {
                    z = false;
                }
            }
            if (z) {
                minecraftClient.player.input.movementSideways /= Float.intBitsToFloat(1045220557);
                minecraftClient.player.input.movementForward /= Float.intBitsToFloat(1045220557);
            }
            if (isGrimMode()) {
                do3102(minecraftClient.player.getActiveHand());
            }
        }
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (!isOffhandSwapAllowed() || !this.guiMove.getValue().booleanValue() || (minecraftClient.currentScreen instanceof ClickGuiScreen) || (minecraftClient.currentScreen instanceof HudEditorScreen)) {
            this.hUDHelper_22.setImmediate(minecraftClient.player.getYaw());
            this.hUDHelper_2.setImmediate(minecraftClient.player.getPitch());
            return;
        }
        if (isKeyPressed(264)) {
            this.hUDHelper_2.animate(Math.min(MathHelper.lerp(inner_3.getTickDelta(), minecraftClient.player.prevPitch, minecraftClient.player.getPitch()) + Float.intBitsToFloat(1090519040), MathConstants.num2));
        } else if (isKeyPressed(265)) {
            this.hUDHelper_2.animate(Math.max(MathHelper.lerp(inner_3.getTickDelta(), minecraftClient.player.prevPitch, minecraftClient.player.getPitch()) - Float.intBitsToFloat(1090519040), -MathConstants.num2));
        }
        if (isKeyPressed(262)) {
            this.hUDHelper_22.animate(MathHelper.lerp(inner_3.getTickDelta(), minecraftClient.player.prevYaw, minecraftClient.player.getYaw()) + Float.intBitsToFloat(1090519040));
        } else if (isKeyPressed(263)) {
            this.hUDHelper_22.animate(MathHelper.lerp(inner_3.getTickDelta(), minecraftClient.player.prevYaw, minecraftClient.player.getYaw()) - Float.intBitsToFloat(1090519040));
        }
        if (is3104()) {
            minecraftClient.player.setPitch(this.hUDHelper_2.getValue());
            minecraftClient.player.setYaw(this.hUDHelper_22.getValue());
        }
    }

    public void do3102(Hand hand) {
        Hand hand2015 = PlayerUtils.getOppositeHand(hand);
        if (!isSlowdownItem(minecraftClient.player.getStackInHand(hand2015))) {
            PacketSender.sendInteractItem(hand2015);
            return;
        }
        if (hand2015 == Hand.MAIN_HAND) {
            int i = minecraftClient.player.getInventory().selectedSlot;
            InventoryUtils.do438((i + 1) % 9);
            PacketSender.sendInteractItem(hand2015);
            InventoryUtils.do438(i);
            return;
        }
        int i2 = InventoryUtils.toNetworkSlot(InventoryUtils.findInInventory(itemStack -> {
            return !isSlowdownItem(itemStack);
        }, true));
        minecraftClient.interactionManager.clickSlot(0, i2, 40, SlotActionType.SWAP, minecraftClient.player);
        PacketSender.sendInteractItem(hand2015);
        minecraftClient.interactionManager.clickSlot(0, i2, 40, SlotActionType.SWAP, minecraftClient.player);
    }

    public boolean isSlowdownItem(ItemStack itemStack) {
        return itemStack.contains(DataComponentTypes.FOOD) || itemStack.isOf(Items.BOW) || itemStack.isOf(Items.SHIELD) || itemStack.isOf(Items.ENDER_PEARL);
    }

    public boolean isOffhandSwapAllowed() {
        if (minecraftClient.currentScreen instanceof ClickGuiScreen) {
            Iterator<ClickGuiPanel> it = ClientServices.getClickGui().getGuiPanels().iterator();
            while (it.hasNext()) {
                ClickGuiPanel next = it.next();
                if ((next instanceof MinigamePanel) && next.isShown()) {
                    return false;
                }
            }
            if (ClientServices.getClickGui().textFieldWidget.isFocused() || !ClientServices.getClickGui().textFieldWidget.getText().isBlank()) {
                return false;
            }
        }
        return (minecraftClient.currentScreen == null || (minecraftClient.currentScreen instanceof ChatScreen) || (minecraftClient.currentScreen instanceof SignEditScreen) || (minecraftClient.currentScreen instanceof AnvilScreen) || (minecraftClient.currentScreen instanceof AbstractCommandBlockScreen) || (minecraftClient.currentScreen instanceof StructureBlockScreen) || (ChestSearchOverlay.textFieldWidget != null && ChestSearchOverlay.textFieldWidget.isActive())) ? false : true;
    }

    public boolean is3104() {
        return isKeyPressed(264) || isKeyPressed(265) || isKeyPressed(262) || isKeyPressed(263);
    }

    public boolean isKeyPressed(int i) {
        return GLFW.glfwGetKey(minecraftClient.getWindow().getHandle(), i) == 1;
    }

    public boolean isNcpMode() {
        return this.mode.getValue() == NoSlowMode.NCP;
    }

    public boolean isGrimMode() {
        return this.mode.getValue() == NoSlowMode.GRIM;
    }

    public boolean shouldApply() {
        return isToggled() && !this.multiTask.getValue().booleanValue() && minecraftClient.player.isUsingItem() && !minecraftClient.player.hasVehicle() && minecraftClient.player.getActiveHand() == Hand.MAIN_HAND;
    }
}
