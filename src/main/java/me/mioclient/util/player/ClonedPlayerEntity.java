package me.mioclient.util.player;

import com.mojang.authlib.GameProfile;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.mioclient.ducks.DuckLivingEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class ClonedPlayerEntity extends net.minecraft.client.player.RemotePlayer {
    public final Set<PlayerModelPart> set;
    public PlayerSkin skin;
    public boolean flag;
    public Vec3 vec3d;

    public ClonedPlayerEntity(ClientLevel clientWorld) {
        super(clientWorld, new GameProfile(UUID.randomUUID(), ""));
        this.set = new HashSet();
    }

    public void tickMovement() {
    }

    public void copyFromPlayer(Player playerEntity) {
        this.vec3d = new Vec3(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ());
        copyPosition(playerEntity);
        this.yRotO = playerEntity.getYRot();
        setYRot(playerEntity.getYRot());
        this.xRotO = playerEntity.getXRot();
        setXRot(playerEntity.getXRot());
        this.yBodyRot = playerEntity.yBodyRot;
        this.yBodyRotO = playerEntity.yBodyRot;
        this.yHeadRot = playerEntity.getYHeadRot();
        this.yHeadRotO = playerEntity.getYHeadRot();
        this.deathTime = 0;
        setDeltaMovement(0.0d, 0.0d, 0.0d);
        setPose(playerEntity.getPose());
        setShiftKeyDown(playerEntity.isCrouching());
        if (playerEntity instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractClientPlayerEntity = (AbstractClientPlayer) playerEntity;
            this.skin = abstractClientPlayerEntity.getSkin();
            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                if (playerModelPart != PlayerModelPart.CAPE) {
                    this.set.add(playerModelPart);
                }
            }
        }
        for (int i = 0; i < getInventory().getContainerSize(); i++) {
            getInventory().setItem(i, playerEntity.getInventory().getItem(i).copy());
        }
        setItemInHand(InteractionHand.MAIN_HAND, playerEntity.getMainHandItem().copy());
        setItemInHand(InteractionHand.OFF_HAND, playerEntity.getOffhandItem().copy());
        getInventory().setSelectedSlot(playerEntity.getInventory().getSelectedSlot());
    }

    @Override
    public void setPos(double d, double d2, double d3) {
        super.setPos(d, d2, d3);
    }

    public Vec3 getLastPosition() {
        return this.vec3d;
    }

    @Override
    public PlayerSkin getSkin() {
        return this.skin != null ? this.skin : super.getSkin();
    }

    public boolean isPartVisible(PlayerModelPart playerModelPart) {
        return this.set.contains(playerModelPart);
    }

    public boolean isChamsVisible() {
        return this.flag;
    }

    public void setChamsVisible(boolean z) {
        this.flag = z;
    }
}
