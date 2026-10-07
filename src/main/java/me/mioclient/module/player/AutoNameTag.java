package me.mioclient.module.player;

import me.mioclient.mode.combat.AutoCrystalMode_2;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.mode.player.ScaffoldMode_2;
import me.mioclient.util.inventory.InventoryUtils;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.util.world.BlockPlacer;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import me.mioclient.manager.RotationManager;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.Module;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import net.minecraft.core.registries.BuiltInRegistries;

public class AutoNameTag extends Module {
    public Setting<ScaffoldMode_2> mode = add(new EnumSetting<>("Mode", ScaffoldMode_2.WHITELIST));
    public Setting<Set<EntityType<?>>> whitelist = add(new EntityListObjectSetting<>("Whitelist", BuiltInRegistries.ENTITY_TYPE));
    public Setting<Float> range = add(new NumberSetting<>("Range", 4.0f, 1.0f, 6.0f).setSuffix("m"));
    public Setting<Integer> frequency = add(new NumberSetting<>("Frequency", 1, 1, 8));
    public Setting<Float> delay = add(new NumberSetting<>("Delay", 0.1f, 0.1f, 5.0f).setSuffix("s"));
    public Setting<Boolean> rotate = add(new BooleanSetting("Rotate", true));
    public Setting<AutoCrystalMode_2> swap = add(new EnumSetting<>("Swap", AutoCrystalMode_2.NONE));
    public final Stopwatch stopwatch;

    public AutoNameTag() {
        super("AutoNameTag", "Will use name tags on nearby entities.", Category.PLAYER, new String[0]);
        this.stopwatch = new Stopwatch();
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Post) {
            return;
        }
        if (this.stopwatch.hasElapsed(this.delay.getValue().floatValue(), TimeUnit.SECONDS)) {
            int i = InventoryUtils.findInHotbar(this::isNamedNameTag);
            int i2 = minecraftClient.player.getInventory().getSelectedSlot();
            int i3 = 0;
            if (this.swap.getValue() != AutoCrystalMode_2.NONE) {
                InventoryUtils.selectSlot(i);
            }
            for (Entity entity : minecraftClient.level.entitiesForRendering()) {
                if (!isNamedNameTag(minecraftClient.player.getMainHandItem())) {
                    break;
                }
                if (this.mode.getValue().isAllowedBySetting(entity.getType(), this.whitelist) && !(entity instanceof Player)) {
                    if (!minecraftClient.player.getMainHandItem().getHoverName().getString().equalsIgnoreCase(entity.hasCustomName() ? entity.getCustomName().getString() : "")) {
                        if (minecraftClient.player.getEyePosition().distanceTo(entity.position()) <= this.range.getValue().floatValue()) {
                            BlockPlacer.interactWithEntity(entity, InteractionHand.MAIN_HAND);
                            i3++;
                            if (this.rotate.getValue().booleanValue()) {
                                motionEvent.setRotation(RotationManager.getRotationsTo(entity.position()));
                            }
                            if (i3 >= this.frequency.getValue().intValue()) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            }
            if (this.swap.getValue() == AutoCrystalMode_2.SILENT) {
                InventoryUtils.selectSlot(i2);
            }
            this.stopwatch.reset();
        }
    }

    public boolean isNamedNameTag(ItemStack itemStack) {
        return itemStack.is(Items.NAME_TAG) && itemStack.has(DataComponents.CUSTOM_NAME);
    }
}