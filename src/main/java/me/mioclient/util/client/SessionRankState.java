package me.mioclient.util.client;

import net.minecraft.client.Minecraft;

public final class SessionRankState {
    public static String username = null;
    public static int rank = -1;
    public static int rankGroup = -1;

    public SessionRankState() {
        if (username == null) {
            username = Minecraft.getInstance().getUser().getName();
        }
    }

    public String getUsername() {
        return username;
    }

    public int getRank() {
        return rank;
    }

    public boolean isFullFeatures() {
        return rankGroup == -1 || rankGroup == 4;
    }

    public boolean hasAdvancedFeatures() {
        return isFullFeatures() || rankGroup == 3;
    }
}
