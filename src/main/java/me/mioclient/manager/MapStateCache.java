package me.mioclient.manager;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.util.io.FileIO;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import me.mioclient.event.events.network.ConnectEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;

public final class MapStateCache implements ClientAccessor {
    public final Stopwatch stopwatch = new Stopwatch();
    public final Map<String, MapItemSavedData> map = Collections.synchronizedMap(new HashMap());
    public String string = null;

    public MapStateCache() {
        eventBus.register(this);
    }

    public MapItemSavedData lookupMapState(ItemStack itemStack, int i) {
        return resolveMapState(itemStack, new MapId(i));
    }

    public MapItemSavedData resolveMapState(ItemStack itemStack, MapId mapId) {
        MapItemSavedData mapState = MapItem.getSavedData(mapId, minecraftClient.level);
        if (mapId.id() != -1 && mapState != null) {
            return mapState;
        }
        String string = itemStack.getDisplayName().getString();
        if (itemStack.has(DataComponents.CUSTOM_NAME)) {
            return this.map.getOrDefault(string, null);
        }
        return null;
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof ClientboundMapItemDataPacket) {
            ClientServices.tooltipsHelper.schedule(() -> {
                Iterator it = minecraftClient.player.getInventory().getNonEquipmentItems().iterator();
                while (it.hasNext()) {
                    updateMapState((ItemStack) it.next());
                }
            }, 1);
        }
    }

    @Listen
    public void onConnect(ConnectEvent connectEvent) {
        this.string = connectEvent.getServer();
        readSavedData(this.string);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (!this.stopwatch.hasElapsedAndReset(5L, TimeUnit.MINUTES) || this.string == null) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            saveMapCache(this.string);
        }, executorService);
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        if (this.string != null) {
            saveMapCache(this.string);
        }
    }

    public void updateMapState(ItemStack itemStack) {
        if (itemStack.getItem() == Items.FILLED_MAP && itemStack.has(DataComponents.CUSTOM_NAME)) {
            String string = itemStack.getDisplayName().getString();
            MapId mapIdComponent = (MapId) itemStack.getOrDefault(DataComponents.MAP_ID, new MapId(-1));
            this.map.compute(string, (str, mapState) -> {
                return MapItem.getSavedData(mapIdComponent, minecraftClient.level);
            });
        }
    }

    public void saveMapCache(String str) {
        if (this.map.isEmpty()) {
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(this.map.size());
            Iterator it = new HashSet(this.map.entrySet()).iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                dataOutputStream.writeUTF((String) entry.getKey());
                dataOutputStream.writeByte(((MapItemSavedData) entry.getValue()).scale);
                for (byte b : ((MapItemSavedData) entry.getValue()).colors) {
                    dataOutputStream.writeByte(b);
                }
            }
            FileIO.writeBytes(ConfigManager.path6.resolve(new StringTemplate().arg(ClientTextRenderer.sanitizeString(str)).format("\u0001.data")), deflate(byteArrayOutputStream.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readSavedData(String str) {
        this.map.clear();
        try {
            Path resolve = ConfigManager.path6.resolve(new StringTemplate().arg(ClientTextRenderer.sanitizeString(str)).format("\u0001.data"));
            if (resolve.toFile().exists()) {
                DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(getByteArray2656(FileIO.readBytes(resolve))));
                int readInt = dataInputStream.readInt();
                for (int i = 0; i < readInt; i++) {
                    String readUTF = dataInputStream.readUTF();
                    MapItemSavedData of = MapItemSavedData.createForClient(dataInputStream.readByte(), true, (ResourceKey) null);
                    for (int i2 = 0; i2 < 16384; i2++) {
                        of.colors[i2] = dataInputStream.readByte();
                    }
                    this.map.put(readUTF, of);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getByteArray2656(byte[] bArr) {
        int inflate;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Inflater inflater = new Inflater();
            inflater.setInput(bArr);
            byte[] bArr2 = new byte[8192];
            while (!inflater.finished() && (inflate = inflater.inflate(bArr2)) != 0) {
                byteArrayOutputStream.write(bArr2, 0, inflate);
            }
            inflater.end();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new java.lang.RuntimeException();
        }
    }

    public byte[] deflate(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Deflater deflater = new Deflater(9);
        deflater.setInput(bArr);
        deflater.finish();
        byte[] bArr2 = new byte[8192];
        while (!deflater.finished()) {
            byteArrayOutputStream.write(bArr2, 0, deflater.deflate(bArr2));
        }
        deflater.end();
        return byteArrayOutputStream.toByteArray();
    }
}
