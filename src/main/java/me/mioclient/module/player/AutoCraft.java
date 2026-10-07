package me.mioclient.module.player;
import net.minecraft.world.phys.Vec3;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.player.AutoCraftMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.math.Tuple;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import me.mioclient.network.PacketSender;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.mioclient.render.RenderConstants;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.screen.SetScreenHookPreEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.module.Module;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

public class AutoCraft extends Module {
    public static final RecipeHolder<?> recipeEntry = new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(Registries.RECIPE, Identifier.parse("minecraft:firework_rocket_simple")), (Recipe) null);
    public static final List<Ingredient> list = List.of(Ingredient.of(Items.PAPER), Ingredient.of(Items.GUNPOWDER));
    public Setting<Set<Item>> items = add(new EntityListObjectSetting<>("Items", BuiltInRegistries.ITEM));
    public Setting<Set<Item>> recipeBlackList = add(new EntityListObjectSetting<>("RecipeBlackList", BuiltInRegistries.ITEM));
    public Setting<Integer> delay = add(new NumberSetting<>("Delay", 150, 75, 1500).setSuffix("ms"));
    public Setting<Integer> frequency = add(new NumberSetting<>("Frequency", 1, 1, 6));
    public Setting<AutoCraftMode> drop = add(new EnumSetting<>("Drop", AutoCraftMode.None));
    public Setting<Boolean> craftAll = add(new BooleanSetting("CraftAll", false, v -> this.drop.getValue() == AutoCraft.AutoCraftMode.None));
    public Setting<Boolean> fastClose = add(new BooleanSetting("FastClose", false));
    public Setting<Boolean> autoOpen = add(new BooleanSetting("AutoOpen", false).setVisible());
    public Setting<Float> range = add(new NumberSetting<>("Range", 4.0f, 1.0f, 6.0f).setSuffix("m").requiresSettings(this.autoOpen));
    public Setting<Boolean> raytrace = add(new BooleanSetting("Raytrace", false).requiresSettings(this.autoOpen));
    public Setting<Boolean> strictDirection = add(new BooleanSetting("StrictDirection", false).requiresSettings(this.autoOpen));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", false).requiresSettings(this.autoOpen));
    public Setting<Boolean> limit = add(new BooleanSetting("Limit", false, v -> !this.craftAll.getValue().booleanValue()).setVisible());
    public Setting<Integer> max = add(new NumberSetting<>("Max", 64, 1, 64));
    public Setting<Boolean> stackLimit = add(new BooleanSetting("StackLimit", false).requiresSettings(this.limit));
    public final Stopwatch stopwatch;
    public final Stopwatch stopwatch2;

    
    
    public enum AutoCraftMode implements NamedValue {
        None("None", ContainerInput.QUICK_MOVE),
        Throw("Throw", ContainerInput.THROW),
        Pickup("Pickup", ContainerInput.PICKUP);

        public final String name;
        public final ContainerInput slotActionType;

        AutoCraftMode(String str, ContainerInput slotActionType) {
            this.name = str;
            this.slotActionType = slotActionType;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public ContainerInput getSlotActionType2107() {
            return this.slotActionType;
        }
    }

    public AutoCraft() {
        super("AutoCraft", "Automatically crafts listed items.", Category.PLAYER, "craftbot");
        this.stopwatch = new Stopwatch();
        this.stopwatch2 = new Stopwatch();
    }

    @Listen
    public void onTickPost(TickPostEvent tickPostEvent) {
        Tuple<BlockPos, Direction> holeSnapData_2838;
        net.minecraft.world.item.crafting.display.RecipeDisplayEntry recipeEntry834 = findMatchingRecipe();
        if (recipeEntry834 == null) {
            return;
        }
        if (this.autoOpen.getValue().booleanValue() && this.stopwatch2.hasElapsedMs(500L) && !(minecraftClient.gui.screen() instanceof AbstractContainerScreen) && (holeSnapData_2838 = getHoleSnapData_2838()) != null) {
            if (this.rotate.getValue().booleanValue()) {
                float[] _rot = RotationManager.getRotationsTo(Vec3.atCenterOf(holeSnapData_2838.getLeft()));
                PacketSender.sendLook(_rot, minecraftClient.player.onGround());
            }
            PacketSender.sendInteractBlock(InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(holeSnapData_2838.getLeft()), holeSnapData_2838.getRight(), holeSnapData_2838.getLeft(), false));
            PacketSender.sendHandSwing(InteractionHand.MAIN_HAND);
            this.stopwatch2.reset();
        }
        if ((minecraftClient.player.containerMenu instanceof CraftingMenu) && !this.items.getValue().isEmpty() && this.stopwatch.hasElapsedMs(this.delay.getValue().intValue())) {
            CraftingMenu craftingScreenHandler = (CraftingMenu)(minecraftClient.player.containerMenu);
            ItemStack result = getDisplayResult(recipeEntry834);
            if (result.is(Items.FIREWORK_ROCKET)) {
                int i = -1;
                int i2 = -1;
                for (int i3 = 10; i3 < 46; i3++) {
                    ItemStack stack = craftingScreenHandler.getSlot(i3).getItem();
                    if (stack.is(Items.GUNPOWDER) && i == -1) {
                        i = i3;
                    }
                    if (stack.is(Items.PAPER) && i2 == -1) {
                        i2 = i3;
                    }
                }
                if (i2 != -1 && craftingScreenHandler.getSlot(1).getItem().isEmpty()) {
                    this.stopwatch.reset();
                    InventoryUtils.swapStacks(i2, 1);
                    return;
                }
                int[] iArr = {2, 4, 5};
                boolean z = true;
                for (int i4 : iArr) {
                    if (!craftingScreenHandler.getSlot(i4).getItem().is(Items.GUNPOWDER)) {
                        z = false;
                    }
                }
                if (i != -1 && !z) {
                    minecraftClient.gameMode.handleContainerInput(craftingScreenHandler.containerId, i, 0, ContainerInput.PICKUP, minecraftClient.player);
                    int length = iArr.length;
                    int i5 = 0;
                    while (true) {
                        if (i5 >= length) {
                            break;
                        }
                        int i6 = iArr[i5];
                        if (craftingScreenHandler.getSlot(i6).getItem().is(Items.GUNPOWDER)) {
                            i5++;
                        } else if (!craftingScreenHandler.getCarried().isEmpty()) {
                            minecraftClient.gameMode.handleContainerInput(craftingScreenHandler.containerId, i6, 1, ContainerInput.PICKUP, minecraftClient.player);
                        }
                    }
                    minecraftClient.gameMode.handleContainerInput(craftingScreenHandler.containerId, i, 0, ContainerInput.PICKUP, minecraftClient.player);
                    this.stopwatch.reset();
                    return;
                }
                ItemStack stack2 = craftingScreenHandler.getSlot(0).getItem();
                Fireworks fireworksComponent = (Fireworks) stack2.getOrDefault(DataComponents.FIREWORKS, new Fireworks(0, Collections.emptyList()));
                if (!stack2.is(Items.FIREWORK_ROCKET) || fireworksComponent.flightDuration() != 3 || !z || !this.stopwatch.hasElapsedMs(Math.max(RenderConstants.num, this.delay.getValue().intValue()))) {
                    return;
                }
            } else {
                for (int i7 = 0; i7 < get836(result.getItem()); i7++) {
                    placeRecipeCompat(craftingScreenHandler.containerId, recipeEntry834.id(), this.craftAll.getValue().booleanValue());
                }
            }
            minecraftClient.gameMode.handleContainerInput(craftingScreenHandler.containerId, 0, 1, this.drop.getValue().getSlotActionType2107(), minecraftClient.player);
            if (this.drop.getValue() == AutoCraftMode.Pickup && result.getCount() + craftingScreenHandler.getCarried().getCount() > craftingScreenHandler.getCarried().getMaxStackSize()) {
                minecraftClient.gameMode.handleContainerInput(craftingScreenHandler.containerId, -999, 0, ContainerInput.PICKUP, minecraftClient.player);
            }
            if (this.fastClose.getValue().booleanValue()) {
                ClientServices.tooltipsHelper.schedule(() -> {
                    PacketSender.sendPacket(new ServerboundContainerClosePacket(minecraftClient.player.containerMenu.containerId));
                    minecraftClient.player.containerMenu = minecraftClient.player.inventoryMenu;
                }, 1);
            }
            this.stopwatch.reset();
        }
    }

    @Listen
    public void onSetScreenHookPre(SetScreenHookPreEvent setScreenHookPreEvent) {
        if ((setScreenHookPreEvent.getScreen() instanceof CraftingScreen) && this.fastClose.getValue().booleanValue()) {
            setScreenHookPreEvent.getScreen().init(0, 0);
            setScreenHookPreEvent.cancel();
        }
    }

    public net.minecraft.world.item.crafting.display.RecipeDisplayEntry findMatchingRecipe() {
        if (this.items.getValue().isEmpty() || minecraftClient.level == null || minecraftClient.player == null) {
            return null;
        }
        net.minecraft.util.context.ContextMap ctx;
        try {
            ctx = net.minecraft.world.item.crafting.display.SlotDisplayContext.fromLevel(minecraftClient.level);
        } catch (Exception e) {
            return null;
        }
        for (net.minecraft.world.item.crafting.display.RecipeDisplayEntry entry : getClientDisplays()) {
            java.util.List<ItemStack> results;
            try {
                results = entry.resultItems(ctx);
            } catch (Exception e) {
                continue;
            }
            if (results == null || results.isEmpty()) {
                continue;
            }
            ItemStack result = results.getFirst();
            if (result != null && result.getItem() != null && !(result.getItem() instanceof SmithingTemplateItem)) {
                java.util.List<Ingredient> ingredients = entry.craftingRequirements().orElse(java.util.List.of());
                if (result.is(Items.FIREWORK_ROCKET)) {
                    ingredients = list;
                }
                if (this.items.getValue().contains(result.getItem()) && is837(ingredients)) {
                    if (is835(result.getItem())) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

    public boolean is835(Item item) {
        if (!this.limit.getValue().booleanValue() || this.craftAll.getValue().booleanValue()) {
            return true;
        }
        int i = 0;
        int intValue = this.max.getValue().intValue();
        if (this.stackLimit.getValue().booleanValue()) {
            intValue *= item.getDefaultMaxStackSize();
        }
        Iterator it = minecraftClient.player.getInventory().getNonEquipmentItems().iterator();
        while (it.hasNext()) {
            ItemStack itemStack = (ItemStack) it.next();
            if (itemStack.is(item)) {
                i += itemStack.getCount();
            }
        }
        return i < intValue;
    }

    public int get836(Item item) {
        if (!this.limit.getValue().booleanValue()) {
            return this.frequency.getValue().intValue();
        }
        int i = 0;
        int intValue = this.max.getValue().intValue();
        if (this.stackLimit.getValue().booleanValue()) {
            intValue *= item.getDefaultMaxStackSize();
        }
        Iterator it = minecraftClient.player.getInventory().getNonEquipmentItems().iterator();
        while (it.hasNext()) {
            ItemStack itemStack = (ItemStack) it.next();
            if (itemStack.is(item)) {
                i += itemStack.getCount();
            }
        }
        return Mth.clamp(intValue - i, 0, this.frequency.getValue().intValue());
    }

    public boolean is837(Collection<Ingredient> collection) {
        HashMap<Item, Integer> hashMap = new HashMap<>();
        Iterator it = minecraftClient.player.containerMenu.slots.iterator();
        while (it.hasNext()) {
            Slot slot = (Slot) it.next();
            hashMap.compute(slot.getItem().getItem(), (item, num) -> {
                return (item == null || num == null) ? Integer.valueOf(slot.getItem().getCount()) : Integer.valueOf(slot.getItem().getCount() + num.intValue());
            });
        }
        for (Ingredient ingredient : collection) {
            if (!ingredient.isEmpty()) {
                boolean z = false;
                java.util.List<ItemStack> _ms = ingredient.items().map(h -> new ItemStack(h.value())).toList();
                ItemStack[] matchingStacks = _ms.toArray(new ItemStack[0]);
                int length = matchingStacks.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    ItemStack itemStack = matchingStacks[i];
                    if (this.recipeBlackList.getValue().contains(itemStack.getItem())) {
                        return false;
                    }
                    if (hashMap.containsKey(itemStack.getItem())) {
                        Integer num2 = (Integer) hashMap.get(itemStack.getItem());
                        if (num2.intValue() - itemStack.getCount() >= 0) {
                            hashMap.replace(itemStack.getItem(), Integer.valueOf(num2.intValue() - itemStack.getCount()));
                            z = true;
                            break;
                        }
                    }
                    i++;
                }
                if (!z) {
                    return false;
                }
            }
        }
        return true;
    }

    public Tuple<BlockPos, Direction> getHoleSnapData_2838() {
        for (BlockPos blockPos : WorldUtils.getBlocksInRadius(minecraftClient.player.getEyePosition(), this.range.getValue().floatValue(), true)) {
            if (minecraftClient.level.getBlockState(blockPos).is(Blocks.CRAFTING_TABLE) && (WorldUtils.isAnyPositionVisible(me.mioclient.mode.player.AutoCraftMode.X8.getWorldCorners(blockPos)) || !this.raytrace.getValue().booleanValue())) {
                List<Direction> list3031 = BlockPlacer.getInteractableSides(blockPos);
                if (!list3031.isEmpty() || !this.strictDirection.getValue().booleanValue()) {
                    return new Tuple<>(blockPos, list3031.isEmpty() ? Direction.UP : list3031.get(0));
                }
            }
        }
        return null;
    }

    public static ItemStack getDisplayResult(net.minecraft.world.item.crafting.display.RecipeDisplayEntry entry) {
        // 26.2: results resolve via SlotDisplayContext.fromLevel(level).
        try {
            if (minecraftClient.level == null) {
                return ItemStack.EMPTY;
            }
            java.util.List<ItemStack> results = entry.resultItems(
                    net.minecraft.world.item.crafting.display.SlotDisplayContext.fromLevel(minecraftClient.level));
            return (results == null || results.isEmpty()) ? ItemStack.EMPTY : results.getFirst();
        } catch (Exception ignored) {
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getRecipeResult(RecipeHolder<?> holder) {
        // 26.2 port: Recipe#getResult(registry) removed; assemble with EMPTY input returns result for crafting recipes.
        try {
            if (holder.value() instanceof net.minecraft.world.item.crafting.CraftingRecipe crafting) {
                return crafting.assemble(CraftingInput.EMPTY);
            }
        } catch (Exception ignored) {
        }
        return ItemStack.EMPTY;
    }

    public static java.util.List<net.minecraft.world.item.crafting.display.RecipeDisplayEntry> getClientDisplays() {
        // 26.2: ClientRecipeBook holds RecipeDisplayEntry collections (replaces RecipeManager#values).
        try {
            if (minecraftClient.player == null) {
                return java.util.List.of();
            }
            java.util.List<net.minecraft.client.gui.screens.recipebook.RecipeCollection> collections =
                    minecraftClient.player.getRecipeBook().getCollections();
            java.util.List<net.minecraft.world.item.crafting.display.RecipeDisplayEntry> out = new java.util.ArrayList<>();
            for (net.minecraft.client.gui.screens.recipebook.RecipeCollection c : collections) {
                out.addAll(c.getRecipes());
            }
            return out;
        } catch (Exception e) {
            return java.util.List.of();
        }
    }

    public static java.util.List<RecipeHolder<?>> getClientRecipes() {
        // 26.2: RecipeHolder flow replaced by RecipeDisplayEntry flow; kept for compat, returns empty.
        return java.util.List.of();
    }

    public static void placeRecipeCompat(int containerId, net.minecraft.world.item.crafting.display.RecipeDisplayId displayId, boolean craftAll) {
        // 26.2: MultiPlayerGameMode#handlePlaceRecipe(int, RecipeDisplayId, boolean) replaces clickRecipe.
        try {
            if (minecraftClient.gameMode == null) {
                return;
            }
            minecraftClient.gameMode.handlePlaceRecipe(containerId, displayId, craftAll);
        } catch (Exception ignored) {
        }
    }

}
