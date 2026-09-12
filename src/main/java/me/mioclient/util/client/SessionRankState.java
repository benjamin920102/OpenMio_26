package me.mioclient.util.client;

import net.minecraft.client.MinecraftClient;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SessionRankState.class */
public final class SessionRankState {
    public static String username = null;
    public static int rank = -1;
    public static int rankGroup = -1;

    public SessionRankState() {
        if (username == null) {
            username = MinecraftClient.getInstance().getSession().getUsername();
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
