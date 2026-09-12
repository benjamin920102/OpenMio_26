package me.mioclient.module.movement;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.module.movement.obstaclepasser.ObstaclePasserMode;
import me.mioclient.util.math.Vec3dUtils;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.module.movement.obstaclepasser.RateLimiter;
import me.mioclient.network.PacketSender;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.math.Vec3dUtils;

import me.mioclient.util.text.StringTemplate;
import me.mioclient.render.RenderConstants;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.module.movement.obstaclepasser.ObstaclePasserMode;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.combat.AutoArmor;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import me.mioclient.mixin.ducks.DuckAbstractBlock;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/movement/ObstaclePasser.class */
public final class ObstaclePasser extends Module {
    public static final AutoArmor autoArmor = (AutoArmor) ClientServices.moduleInjector.getModule(AutoArmor.class);
    public static final ElytraFly elytraFly = (ElytraFly) ClientServices.moduleInjector.getModule(ElytraFly.class);
    public final RateLimiter obstaclePasserHelper_2;
    public Setting<Boolean> setting;
    public Setting<Boolean> setting2;
    public Setting<Integer> setting3;
    public Setting<Integer> setting4;
    public final Stopwatch stopwatch;
    public float val;
    public BlockPos blockPos;
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public int num;
    public ObstaclePasserMode obstaclePasserMode;

    public ObstaclePasser() {
        super("ObstaclePasser", new StringTemplate().arg(String.valueOf(Formatting.YELLOW)).format("Passes obstacles in your way. \n\u0001Requires either ElytraFly or Speed enabled."), Category.MOVEMENT, new String[0]);
        this.obstaclePasserHelper_2 = new RateLimiter(3);
        this.stopwatch = new Stopwatch();
        if (this.setting3 != null) {
            this.setting3.setHoleSnapConfig("~", HoleSnapMode.MIN);
        }
        if (this.setting != null) {
            this.setting.setChangeCallback(() -> {
                this.obstaclePasserMode = null;
            });
        }
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        if (isNotInGame()) {
            toggle();
            return;
        }
        this.flag = false;
        this.obstaclePasserMode = null;
        this.num = 0;
        this.val = Math.round(minecraftClient.player.getYaw());
        this.blockPos = minecraftClient.player.getBlockPos();
        this.obstaclePasserHelper_2.do978();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        ClientServices.obstaclePasserHelper.cancelAll();
        if (this.flag && autoArmor.isToggled()) {
            autoArmor.elytra.setValue(true);
        }
        this.flag2 = false;
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (ClientServices.obstaclePasserHelper.isPathing()) {
            return;
        }
        minecraftClient.player.setYaw(this.val);
    }

    @Listen(get219= RenderConstants.num4)
    public void onTickPost(TickPostEvent tickPostEvent) {
        if (areChunksLoaded()) {
            this.stopwatch.reset();
        }
        syncSneakState();
        if (this.obstaclePasserMode == null) {
            this.obstaclePasserMode = ObstaclePasserMode.getObstaclePasserMode478(this.val);
        }
        if (this.num > 0) {
            this.num--;
        }
        this.obstaclePasserHelper_2.recalculateLayout();
        processPathingTick();
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.obstaclePasserHelper_2.recordTimestamp();
        }
    }

    @Listen
    public void onInputTick(InputTickEvent tickEvent_2) {
        if ((this.setting2.getValue().booleanValue() && this.stopwatch.hasElapsedMs(750L)) || isPathing()) {
            return;
        }
        if (shouldJump() || (!minecraftClient.player.isFallFlying() && elytraFly.isToggled())) {
            tickEvent_2.getInput().jumping = true;
        }
        tickEvent_2.getInput().pressingForward = true;
        tickEvent_2.getInput().movementForward = tickEvent_2.hasSpeed() ? tickEvent_2.getSpeed() : Float.intBitsToFloat(1065353216);
    }

    public void processPathingTick() {
        BlockPos blockPos710;
        if (ClientServices.obstaclePasserHelper.isPathing() && (blockPos710 = ClientServices.obstaclePasserHelper.getCurrentGoal()) != null) {
            if (Vec3dUtils.getHorizontalDistance(minecraftClient.player.getPos(), blockPos710.toBottomCenterPos()) <= Float.intBitsToFloat(1065353216)) {
                ClientServices.obstaclePasserHelper.cancelAll();
                this.num = 10;
                if (elytraFly.isToggled()) {
                    this.flag3 = true;
                }
            }
        }
        if (!shouldStartPathing() || ClientServices.obstaclePasserHelper.isPathing()) {
            return;
        }
        this.obstaclePasserHelper_2.do978();
        int y = this.setting3.getValue().intValue() == -1 ? this.blockPos.getY() : this.setting3.getValue().intValue();
        if (this.obstaclePasserMode != null && this.setting.getValue().booleanValue()) {
            BlockPos blockPos = null;
            for (int i = 5; i < this.setting4.getValue().intValue(); i++) {
                blockPos = this.obstaclePasserMode.function.apply(Integer.valueOf(i)).withY(y);
                if (((me.mioclient.mixin.ducks.DuckAbstractBlock) WorldUtils.getBlock(blockPos.down())).isCollidable()) {
                    break;
                }
            }
            ClientServices.obstaclePasserHelper.pathTo(blockPos);
            return;
        }
        Vec3d bottomCenterPos = this.blockPos.toBottomCenterPos();
        Vec3d vec3d930 = getHorizontalDirection(this.val);
        BlockPos ofFloored = BlockPos.ofFloored(bottomCenterPos.add(vec3d930.multiply(Vec3dUtils.getHorizontalDistance(bottomCenterPos, minecraftClient.player.getPos()))));
        BlockPos blockPos2 = null;
        for (int i2 = 5; i2 < this.setting4.getValue().intValue(); i2++) {
            blockPos2 = ofFloored.add(BlockPos.ofFloored(vec3d930.multiply(i2)));
            if (((me.mioclient.mixin.ducks.DuckAbstractBlock) WorldUtils.getBlock(blockPos2.down())).isCollidable()) {
                break;
            }
        }
        ClientServices.obstaclePasserHelper.pathTo(blockPos2.withY(y));
    }

    public boolean shouldStartPathing() {
        if (this.num > 0) {
            return false;
        }
        return minecraftClient.player.horizontalCollision || this.obstaclePasserHelper_2.isRateExceeded() || (!PlayerUtils.isReallyFallFlying() && ClientServices.antiPhaseHelper.getGroundTicks() > 3 && minecraftClient.player.isOnGround() && elytraFly.isToggled());
    }

    public void syncSneakState() {
        if (!ClientServices.obstaclePasserHelper.isPathing()) {
            if (this.flag) {
                this.flag = false;
            }
        } else {
            if (this.flag) {
                return;
            }
            this.flag = true;
            PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY, 0);
            PacketSender.sendClientCommand(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY, 0);
        }
    }

    public boolean hasElytra() {
        return minecraftClient.player.getInventory().getArmorStack(EquipmentSlot.CHEST.getEntitySlotId()).isOf(Items.ELYTRA);
    }

    public boolean areChunksLoaded() {
        ClientChunkManager chunkManager = minecraftClient.world.getChunkManager();
        return ChunkPos.stream(minecraftClient.player.getChunkPos(), 2).allMatch(chunkPos -> {
            return chunkManager.isChunkLoaded(chunkPos.x, chunkPos.z);
        }) || !this.setting2.getValue().booleanValue();
    }

    public boolean shouldJump() {
        return this.flag3 && elytraFly.isToggled();
    }

    public boolean isPathing() {
        return ClientServices.obstaclePasserHelper.isPathing();
    }

    public boolean is929() {
        if (isToggled()) {
            return isPathing();
        }
        return false;
    }

    public Vec3d getHorizontalDirection(float f) {
        return minecraftClient.player.getRotationVector(0.0f, f);
    }
}
