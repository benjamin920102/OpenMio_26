package me.mioclient.network;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.render.NameTagsHelperMode;

import me.mioclient.api.ClientAccessor;
import me.mioclient.network.packet.FriendListSyncMessage;

import io.netty.channel.Channel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FriendListSync implements ClientAccessor {
    public static final CopyOnWriteArrayList<ScheduledFuture<?>> copyOnWriteArrayList = new CopyOnWriteArrayList<>();

    public static void scheduleFriendSync(Channel channel) {
        copyOnWriteArrayList.add(channel.eventLoop().scheduleAtFixedRate(() -> {
            List<String> list526 = ClientServices.socialManager.getPlayersByRole(NameTagsHelperMode.FRIEND);
            if (minecraftClient.player != null) {
                list526.removeIf(str -> {
                    return minecraftClient.player.connection.getPlayerInfo(str) == null;
                });
            } else {
                list526.clear();
            }
            channel.writeAndFlush(new FriendListSyncMessage(minecraftClient.getUser().getName(), (String[]) list526.toArray(i -> {
                return new String[i];
            })));
        }, 0L, 10L, TimeUnit.SECONDS));
    }

    public static void resetTrajectoryData() {
        if (copyOnWriteArrayList.isEmpty()) {
            return;
        }
        copyOnWriteArrayList.forEach(scheduledFuture -> {
            scheduledFuture.cancel(true);
        });
        copyOnWriteArrayList.clear();
    }
}
