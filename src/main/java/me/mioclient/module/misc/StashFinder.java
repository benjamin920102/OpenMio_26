package me.mioclient.module.misc;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.module.misc.stashfinder.StashEntry;
import me.mioclient.util.client.ChatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.manager.KillstreakSoundManager;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.LoadChunkFromPacketEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/misc/StashFinder.class */
public class StashFinder extends Module {
    public Setting<Boolean> chests = add(new BooleanSetting("Chests", true).setVisible());
    public Setting<Integer> chestCount = add(new NumberSetting<>("ChestCount", 5, 2, 20).requiresSettings(this.chests));
    public Setting<Boolean> shulkers = add(new BooleanSetting("Shulkers", true));
    public Setting<Boolean> stackedCarts = add(new BooleanSetting("StackedCarts", true));
    public Setting<Boolean> notify = add(new BooleanSetting("Notify", true).setVisible());
    public Setting<Boolean> censorCoords = add(new BooleanSetting("CensorCoords", false).requiresSettings(this.notify));
    public Setting<Boolean> log = add(new BooleanSetting("Log", true));
    public Setting<Boolean> sound = add(new BooleanSetting("Sound", true).setVisible());
    public Setting<SoundIdentifier> type = add(new SoundIdentifierSetting("Type", new SoundIdentifier("system", "warning")).requiresSettings(this.sound));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 1.0f, 0.0f, 1.0f).requiresSettings(this.sound));
    public Setting<Boolean> ignoreNatural = add(new BooleanSetting("IgnoreNatural", false).setVisible());
    public Setting<Boolean> dungeon = add(new BooleanSetting("Dungeon", true).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> fortress = add(new BooleanSetting("Fortress", true).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> bastion = add(new BooleanSetting("Bastion", true).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> ancientCity = add(new BooleanSetting("AncientCity", false).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> trialChambers = add(new BooleanSetting("TrialChambers", true).requiresSettings(this.ignoreNatural));
    public final Stopwatch stopwatch;

    public StashFinder() {
        super("StashFinder", "Logs and/or notifies you about possible stashes.", Category.MISC, new String[0]);
        this.stopwatch = new Stopwatch();
        setDrawn(false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x01b6, code lost:
    
        if (me.mioclient.api.ClientServices.stashFinderHelper.isBlockConfirmed(r0.getPos()) == false) goto L16;
     */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0287 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x0063 A[SYNTHETIC] */
    @Listen
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onLoadChunkFromPacket(LoadChunkFromPacketEvent loadChunkFromPacketEvent) {
        String format = new SimpleDateFormat("MM/dd HH:mm a").format(new Date());
        WorldChunk worldChunk2555 = loadChunkFromPacketEvent.getChunk();
        ChunkPos pos = worldChunk2555.getPos();
        int i = 0;
        for (BlockEntity blockEntity : worldChunk2555.getBlockEntities().values()) {
            if (this.ignoreNatural.getValue().booleanValue()) {
                if (WorldUtils.isStashChest(minecraftClient.world.getChunk(blockEntity.getPos()), blockEntity.getPos(), this.fortress.getValue().booleanValue(), this.bastion.getValue().booleanValue(), this.ancientCity.getValue().booleanValue(), this.dungeon.getValue().booleanValue(), this.trialChambers.getValue().booleanValue())) {
                    continue;
                }
            }
            if (!(blockEntity instanceof ChestBlockEntity)) {
                if (blockEntity instanceof BarrelBlockEntity) {
                }
                if (!this.chests.getValue().booleanValue() && i >= this.chestCount.getValue().intValue()) {
                    String string2921 = new StringTemplate().arg(i).format("\u0001 chests");
                    if (tryRegisterStash(createStashEntry(new StringTemplate().arg(format).arg(string2921).format("\u0001 (\u0001)"), pos.getCenterX(), pos.getCenterZ()), string2921)) {
                        break;
                    }
                } else if (blockEntity instanceof ShulkerBoxBlockEntity) {
                    continue;
                } else {
                    ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity) blockEntity;
                    if (this.shulkers.getValue().booleanValue()) {
                        if (!ClientServices.stashFinderHelper.isBlockConfirmed(shulkerBoxBlockEntity.getPos())) {
                            if (tryRegisterStash(createStashEntry(new StringTemplate().arg(format).arg("a shulkerbox").format("\u0001 (\u0001)"), pos.getCenterX(), pos.getCenterZ()), "a shulkerbox")) {
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
            i++;
            if (!this.chests.getValue().booleanValue()) {
            }
            if (blockEntity instanceof ShulkerBoxBlockEntity) {
            }
        }
        if (this.stackedCarts.getValue().booleanValue()) {
            ArrayList arrayList = new ArrayList();
            for (Entity chestMinecartEntity : minecraftClient.world.getEntities()) {
                if (chestMinecartEntity instanceof ChestMinecartEntity) {
                    ChestMinecartEntity chestMinecartEntity2 = (ChestMinecartEntity) chestMinecartEntity;
                    if (chestMinecartEntity2.getChunkPos().equals(chestMinecartEntity2.getChunkPos())) {
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            if (chestMinecartEntity2.getBoundingBox().intersects(((ChestMinecartEntity) it.next()).getBoundingBox())) {
                                tryRegisterStash(createStashEntry(new StringTemplate().arg(format).format("Stacked minecarts (\u0001)"), pos.getCenterX(), pos.getCenterZ()), "Stacked minecarts");
                                return;
                            }
                        }
                        arrayList.add(chestMinecartEntity2);
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    public StashEntry createStashEntry(String str, int i, int i2) {
        return new StashEntry(str, i, i2, WorldUtils.getDimension().name(), minecraftClient.player.networkHandler.getServerInfo() == null ? "singleplayer" : minecraftClient.player.networkHandler.getServerInfo().address);
    }

    public boolean tryRegisterStash(StashEntry stashFinderHelper, String str) {
        if (!ClientServices.stashFinderDataManager.findFirst(stashFinderHelper2 -> {
            return stashFinderHelper2.getX() == stashFinderHelper.getX() && stashFinderHelper2.getZ() == stashFinderHelper.getZ();
        }).isEmpty() || !ClientServices.stashFinderDataManager.register(stashFinderHelper)) {
            return false;
        }
        if (this.notify.getValue().booleanValue()) {
            MutableText append = Text.literal(getName()).append(new StringTemplate().arg(str).format(" has found \u0001 at: "));
            if (!this.censorCoords.getValue().booleanValue()) {
                append.append(stashFinderHelper.formatCoords());
            }
            ChatUtils.sendPrefixedMessage((Text) append, ChatUtils.createSignature(-13579), ChatUtils.MixinClientConnectionMode.mixinClientConnectionMode3);
        }
        if (!this.sound.getValue().booleanValue()) {
            return true;
        }
        if (!this.stopwatch.hasElapsed(Double.longBitsToDouble(4617315517961601024L), TimeUnit.SECONDS)) {
            return true;
        }
        KillstreakSoundManager KillstreakSoundManager = ClientServices.killstreakSoundManager;
        KillstreakSoundManager.playSound(this.type.getValue(), this.volume.getValue().floatValue());
        this.stopwatch.reset();
        return true;
    }
}
