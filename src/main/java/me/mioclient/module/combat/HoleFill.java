package me.mioclient.module.combat;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.world.HoleData;

import me.mioclient.module.combat.holefill.HoleFilter;
import me.mioclient.module.combat.holefill.RangeHoleFilter;
import me.mioclient.module.combat.holefill.SmartHoleFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.module.Delay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import me.mioclient.mixin.ducks.DuckAbstractBlock;
import me.mioclient.module.Module;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.core.registries.BuiltInRegistries;

public class HoleFill extends Delay {
    public Setting<Set<Block>> whitelist = add(new EntityListObjectSetting<>("Whitelist", BuiltInRegistries.BLOCK));
    public Setting<Boolean> doubles = add(new BooleanSetting("Doubles", false));
    public Setting<HoleFillPredicateMode> mode = add(new EnumSetting<>("Mode", HoleFillPredicateMode.PLAIN));
    public Setting<Float> smartRange = add(new NumberSetting<>("SmartRange", 1.5f, 0.5f, 4.0f, v -> this.mode.getValue() == HoleFill.HoleFillPredicateMode.SMART).setSuffix("m"));
    public Setting<Float> range = add(new NumberSetting<>("Range", 4.0f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Float> verticalRange = add(new NumberSetting<>("VerticalRange", 3.0f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Boolean> onlySafe = add(new BooleanSetting("OnlySafe", false));
    public Setting<Boolean> selfSafety = add(new BooleanSetting("SelfSafety", false, v -> this.mode.getValue() == HoleFill.HoleFillPredicateMode.SMART));
    public Setting<Boolean> ignoreNakeds = add(new BooleanSetting("IgnoreNakeds", false, v -> this.mode.getValue() == HoleFill.HoleFillPredicateMode.SMART));
    public Setting<Boolean> multiTask = add(new BooleanSetting("MultiTask", true));
    public Setting<Boolean> self = add(new BooleanSetting("Self", true));
    public Setting<Boolean> extrapolation = add(new BooleanSetting("Extrapolation", true).setVisible());
    public Setting<Integer> ticks = add(new NumberSetting<>("Ticks", 4, 0, 8, v -> this.extrapolation.isShown()).setHoleSnapConfig("Auto", HoleSnapMode.MIN));
    public final HoleFilter holeFillHelper;
    public final HoleFilter holeFillHelper2;

    
    
    public enum HoleFillPredicateMode implements NamedValue {
        PLAIN("Plain"),
        SMART("Smart");

        public final String name;

        HoleFillPredicateMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }

        public HoleFilter getHoleFillHelper1726(HoleFill holeFill) {
            return this == PLAIN ? holeFill.holeFillHelper : holeFill.holeFillHelper2;
        }
    }

    public HoleFill() {
        super("HoleFill", "Blocks safe holes nearby with obsidian.", Category.COMBAT, new String[0]);
        this.holeFillHelper = new RangeHoleFilter(this);
        this.holeFillHelper2 = new SmartHoleFilter(this);
        unregister((Setting<?>) this.setting3);
        unregister((Setting<?>) this.setting10);
        this.setting9.setDefaultValue(false);
        this.self.addVisibilityPredicate(bool -> {
            Iterator<Block> it = this.whitelist.getValue().iterator();
            while (it.hasNext()) {
                if (!((me.mioclient.mixin.ducks.DuckAbstractBlock) (Object) it.next()).isCollidable()) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override // me.mioclient.module.Module
    public String getInfo() {
        return String.valueOf(getPlacePositions().size());
    }

    @Override // me.mioclient.module.Delay
    public void do1323() {
        if (!minecraftClient.player.isUsingItem() || this.multiTask.getValue().booleanValue()) {
            if (ClientServices.holeSnapOverlayHelper.isInHole() || !this.onlySafe.getValue().booleanValue()) {
                super.do1323();
            }
        }
    }

    @Override // me.mioclient.module.Delay
    public List<BlockPos> getPlacePositions() {
        ArrayList arrayList = new ArrayList();
        for (HoleData holeSnapData : ClientServices.holeSnapOverlayHelper.getHoles()) {
            if (this.mode.getValue().getHoleFillHelper1726(this).isWithinRange(holeSnapData) && ((this.self.getValue().booleanValue() && this.self.isVisible()) || !holeSnapData.getBlockPos().equals(PlayerUtils.getRoundedBlockPos()))) {
                arrayList.add(holeSnapData.getBlockPos());
            }
        }
        return arrayList;
    }

    @Override // me.mioclient.module.Delay
    public int getSlotIndex() {
        return this.whitelist.getValue().isEmpty() ? super.getSlotIndex() : InventoryUtils.findInHotbar(itemStack -> {
            return this.whitelist.getValue().contains(getItemBlock(itemStack));
        });
    }

    public Block getItemBlock(ItemStack itemStack) {
        BlockItem item = (itemStack.getItem()) instanceof BlockItem ? (BlockItem) (itemStack.getItem()) : null;
        return item instanceof BlockItem ? item.getBlock() : Blocks.VOID_AIR;
    }
}