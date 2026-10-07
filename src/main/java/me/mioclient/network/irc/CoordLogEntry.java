package me.mioclient.network.irc;

import me.mioclient.api.ClientAccessor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

public final class CoordLogEntry {
    public transient long spawnTime = System.currentTimeMillis();
    public final String string;
    public final String string2;
    public final int x;
    public final int y;
    public final int num;
    public transient BlockPos blockPos;

    public CoordLogEntry(String str, String str2, int i, int i2, int i3) {
        this.string = str;
        this.string2 = str2;
        this.x = i;
        this.y = i2;
        this.num = i3;
    }

    public CoordLogEntry(String str, String str2, BlockPos blockPos) {
        this.string = str;
        this.string2 = str2;
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.num = blockPos.getZ();
    }

    public String getPlayerName() {
        return this.string;
    }

    public String getStrippedName() {
        return ChatFormatting.stripFormatting(this.string);
    }

    public String getServer() {
        return this.string2;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.num;
    }

    public BlockPos getBlockPos() {
        if (this.blockPos == null) {
            this.blockPos = new BlockPos(this.x, this.y, this.num);
        }
        return this.blockPos;
    }

    public long getSpawnTime() {
        return this.spawnTime;
    }

    public void reset() {
        this.spawnTime = System.currentTimeMillis();
    }

    public boolean isOnSameServer() {
        String str = null;
        try {
            str = ClientAccessor.minecraftClient.getConnection().getConnection().getRemoteAddress().toString();
            int indexOf = str.indexOf(47);
            if (indexOf > 0) {
                str = str.substring(0, indexOf);
            }
            while (str.endsWith(".")) {
                str = str.substring(0, str.length() - 1);
            }
        } catch (Exception e) {
        }
        if (str == null || str.isEmpty()) {
            str = "singleplayer";
        }
        return str.equals(this.string2);
    }
}
