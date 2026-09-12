package me.mioclient.module.render;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.tooltips.ByteCountingOutput;
import me.mioclient.module.render.tooltips.ShulkerPreviewScreen;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.module.render.tooltips.ContainerPreview;
import me.mioclient.render.tooltip.HeightTooltipComponent;
import me.mioclient.render.tooltip.ShulkerContentsTooltip;

import me.mioclient.api.ClientServices;
import me.mioclient.module.render.tooltips.ContainerPreview;
import me.mioclient.render.tooltip.ShulkerContentsTooltip;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.render.tooltip.HeightTooltipComponent;
import me.mioclient.module.render.tooltips.ByteCountingOutput;
import me.mioclient.module.render.tooltips.ShulkerPreviewScreen;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.screen.RenderWithTooltipEvent;
import me.mioclient.module.Module;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import me.mioclient.mixin.ducks.DuckHandledScreen;
import me.mioclient.module.Position;
import me.mioclient.setting.BooleanSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Tooltips.class */
public class Tooltips extends Module {
    public Setting<Boolean> colored = add(new BooleanSetting("Colored", true));
    public Setting<Boolean> majorityItem = add(new BooleanSetting("MajorityItem", false));
    public Setting<Boolean> keepText = add(new BooleanSetting("KeepText", true));
    public Setting<Boolean> shulkerContent = add(new BooleanSetting("ShulkerContent", true));
    public Setting<Boolean> beeNest = add(new BooleanSetting("BeeNest", false));
    public Setting<Boolean> mapOverlay = add(new BooleanSetting("MapOverlay", true));
    public Setting<Boolean> size = add(new BooleanSetting("Size", false));
    public Color color;
    public final List<ContainerPreview> list;

    public Tooltips() {
        super("Tooltips", new StringTemplate().arg(String.valueOf(Formatting.YELLOW)).arg(String.valueOf(Formatting.YELLOW)).format("Draws advanced tool tips on items.\n\u0001Middle Click a shulker to open the preview screen.\n\u0001Hold Left Alt when hovering over a shulker to inspect it's items."), Category.RENDER, new String[0]);
        this.list = Collections.synchronizedList(new ArrayList());
        setDrawn(false);
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        InventoryS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof InventoryS2CPacket ? (InventoryS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof InventoryS2CPacket) {
            InventoryS2CPacket inventoryS2CPacket = packet904;
            if (this.majorityItem.getValue().booleanValue()) {
                minecraftClient.executeSync(() -> {
                    this.list.clear();
                    Iterator it = inventoryS2CPacket.getContents().iterator();
                    while (it.hasNext()) {
                        ContainerPreview tooltipsData2241 = ContainerPreview.fromShulkerBox((ItemStack) it.next());
                        if (tooltipsData2241 != null) {
                            this.list.add(tooltipsData2241);
                        }
                    }
                });
            }
        }
        if ((channelRead0Event.getPacket() instanceof ScreenHandlerSlotUpdateS2CPacket) && this.majorityItem.getValue().booleanValue()) {
            ClientServices.tooltipsHelper.schedule(() -> {
                if (minecraftClient.player.currentScreenHandler == null) {
                    return;
                }
                this.list.clear();
                Iterator it = minecraftClient.player.currentScreenHandler.slots.iterator();
                while (it.hasNext()) {
                    ContainerPreview tooltipsData2241 = ContainerPreview.fromShulkerBox(((Slot) it.next()).getStack());
                    if (tooltipsData2241 != null) {
                        this.list.add(tooltipsData2241);
                    }
                }
            }, 0);
        }
    }

    @Listen
    public void onRenderWithTooltip(RenderWithTooltipEvent renderWithTooltipEvent) {
        Slot mio$getFocusedSlot;
        BlockPos blockPos;
        if ((renderWithTooltipEvent.getScreen() instanceof HandledScreen) && (mio$getFocusedSlot = ((me.mioclient.mixin.ducks.DuckHandledScreen) renderWithTooltipEvent.getScreen()).mio$getFocusedSlot()) != null) {
            ItemStack stack = mio$getFocusedSlot.getStack();
            List<ItemStack> ofSize = DefaultedList.ofSize(27, ItemStack.EMPTY);
            BlockItem item = (stack.getItem()) instanceof BlockItem ? (BlockItem) (stack.getItem()) : null;
            if (item instanceof BlockItem) {
                ShulkerBoxBlock block = (item.getBlock()) instanceof ShulkerBoxBlock ? (ShulkerBoxBlock) (item.getBlock()) : null;
                if (block instanceof ShulkerBoxBlock) {
                    ShulkerBoxBlock shulkerBoxBlock = block;
                    if (shulkerBoxBlock.getColor() != null) {
                        Color color = new Color(shulkerBoxBlock.getColor().getMapColor().color, false);
                        float[] fArr = new float[3];
                        color.getColorComponents(fArr);
                        setShulkerTintColor(new Color(fArr[0], fArr[1], fArr[2], Float.intBitsToFloat(1056964608)));
                    }
                }
            }
            ContainerComponent containerComponent = (ContainerComponent) stack.get(DataComponentTypes.CONTAINER);
            if (containerComponent != null) {
                List list = containerComponent.stream().toList();
                for (int i = 0; i < list.size() && i < 27; i++) {
                    ItemStack itemStack = (ItemStack) list.get(i);
                    if (itemStack.getCount() == 0) {
                        itemStack.setCount(69);
                    }
                    ofSize.set(i, itemStack);
                }
            }
            do584(stack, ofSize);
            if (!ofSize.stream().allMatch((v0) -> {
                return v0.isEmpty();
            }) && this.shulkerContent.getValue().booleanValue()) {
                if (GLFW.glfwGetMouseButton(minecraftClient.getWindow().getHandle(), 2) == 1 && !(renderWithTooltipEvent.getScreen() instanceof ShulkerPreviewScreen)) {
                    minecraftClient.setScreen(new ShulkerPreviewScreen(new ShulkerBoxScreenHandler(1337, minecraftClient.player.getInventory(), new SimpleInventory((ItemStack[]) ofSize.toArray(new ItemStack[27]))), minecraftClient.player.getInventory(), stack.getName(), renderWithTooltipEvent.getScreen()));
                }
                if (this.keepText.getValue().booleanValue()) {
                    renderWithTooltipEvent.getComponents().add(new ShulkerContentsTooltip(ofSize));
                } else {
                    renderWithTooltipEvent.getComponents().clear();
                    renderWithTooltipEvent.getComponents().addAll(List.of(TooltipComponent.of(stack.getName().asOrderedText()), new ShulkerContentsTooltip(ofSize)));
                }
            }
            if (stack.getItem() == Items.FILLED_MAP) {
                MapIdComponent mapIdComponent = (MapIdComponent) stack.get(DataComponentTypes.MAP_ID);
                int id = mapIdComponent == null ? -1 : mapIdComponent.id();
                MapState mapState2653 = ClientServices.tooltipsSearchHelper4.lookupMapState(stack, id);
                if (mapState2653 != null) {
                    if (this.keepText.getValue().booleanValue()) {
                        renderWithTooltipEvent.getComponents().add(new HeightTooltipComponent(id, mapState2653));
                    } else {
                        renderWithTooltipEvent.getComponents().clear();
                        renderWithTooltipEvent.getComponents().addAll(List.of(TooltipComponent.of(stack.getName().asOrderedText()), new HeightTooltipComponent(id, mapState2653)));
                    }
                    if (this.mapOverlay.getValue().booleanValue()) {
                        renderWithTooltipEvent.getComponents().add(TooltipComponent.of(Text.literal("Hold Left-Alt to hide item amount.").styled(style -> {
                            return style.withFormatting(Formatting.YELLOW);
                        }).asOrderedText()));
                    }
                }
            }
            List list2 = (List) stack.get(DataComponentTypes.BEES);
            if (list2 != null && this.beeNest.getValue().booleanValue()) {
                Iterator it = list2.iterator();
                while (it.hasNext()) {
                    NbtCompound copyNbt = ((BeehiveBlockEntity.BeeData) it.next()).entityData().copyNbt();
                    if (copyNbt != null && copyNbt.contains("FlowerPos") && (blockPos = (BlockPos) NbtHelper.toBlockPos(copyNbt, "FlowerPos").orElse(null)) != null) {
                        renderWithTooltipEvent.getComponents().add(TooltipComponent.of(Text.literal(new StringTemplate().arg(blockPos.toShortString()).format("Position \u0001")).asOrderedText()));
                    }
                }
            }
            if (this.size.getValue().booleanValue()) {
                try {
                    ByteCountingOutput tooltipsDataOutput = new ByteCountingOutput();
                    stack.encode(minecraftClient.player.getRegistryManager()).write(tooltipsDataOutput);
                    renderWithTooltipEvent.getComponents().add(TooltipComponent.of(Text.literal(getString585(tooltipsDataOutput.getByteCount())).asOrderedText()));
                } catch (Throwable th) {
                }
            }
        }
    }

    public void do584(ItemStack itemStack, List<ItemStack> list) {
        NbtComponent nbtComponent = (NbtComponent) itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null || !nbtComponent.contains("BlockEntityTag")) {
            return;
        }
        NbtCompound compound = nbtComponent.getNbt().getCompound("BlockEntityTag");
        if (compound.contains("Items")) {
            NbtList list2 = compound.getList("Items", 10);
            for (int i = 0; i < list2.size(); i++) {
                NbtCompound compound2 = list2.getCompound(i);
                ItemStack itemStack2 = (ItemStack) ItemStack.fromNbt(minecraftClient.player.getRegistryManager(), (NbtElement) compound2).orElse(null);
                byte b = compound2.contains("Count", 1) ? compound2.getByte("Count") : (byte) -1;
                if (itemStack2 != null && b == 0) {
                    itemStack2.setCount(69);
                    list.set(i, itemStack2);
                }
            }
        }
    }

    public String getString585(long j) {
        long j2 = j / 1024;
        long j3 = j2 / 1024;
        return j3 > 0 ? String.format("%,d MB", Long.valueOf(j3)) : j2 > 0 ? String.format("%,d KB", Long.valueOf(j2)) : String.format("%,d B", Long.valueOf(j));
    }

    public Color getTintColor() {
        if (this.colored.getValue().booleanValue()) {
            return this.color;
        }
        return null;
    }

    public void setShulkerTintColor(Color color) {
        this.color = color;
    }
}
