package me.mioclient.module.render;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.math.ColorUtils;
import me.mioclient.util.player.EntityUtil;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.manager.RotationManager;
import me.mioclient.network.ChunkUpdateHandler;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.Module;
import me.mioclient.module.client.Colors;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.chunk.Chunk;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.EntityListObjectSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;
import net.minecraft.registry.Registries;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/Search.class */
public class Search extends Module {
    public static Colors colors = (Colors) ClientServices.moduleInjector.getModule(Colors.class);
    public static final BlockPos.Mutable mutable = new BlockPos.Mutable();
    public Setting<Set<Block>> whitelist = add(new EntityListObjectSetting<>("Whitelist", Registries.BLOCK));
    public Setting<Set<EntityType<?>>> entityTypes = add(new EntityListObjectSetting<>("EntityTypes", Registries.ENTITY_TYPE));
    public Setting<Boolean> entities = add(new BooleanSetting("Entities", false));
    public Setting<Boolean> ignoreNeg = add(new BooleanSetting("IgnoreNeg", false));
    public Setting<Boolean> fill = add(new BooleanSetting("Fill", true));
    public Setting<Float> fillOpacity = add(new NumberSetting<>("FillOpacity", 0.1f, 0.0f, 1.0f, v -> this.fill.getValue().booleanValue()));
    public Setting<Boolean> outline = add(new BooleanSetting("Outline", true));
    public Setting<Boolean> tracers = add(new BooleanSetting("Tracers", true));
    public Setting<Float> tracerOpacity = add(new NumberSetting<>("TracerOpacity", 0.5f, 0.0f, 1.0f, v -> this.tracers.getValue().booleanValue()));
    public Setting<Boolean> sound = add(new BooleanSetting("Sound", false).setVisible());
    public Setting<SoundIdentifier> type = add(new SoundIdentifierSetting("Type", new SoundIdentifier("system", "warning")).requiresSettings(this.sound));
    public Setting<Float> volume = add(new NumberSetting<>("Volume", 1.0f, 0.0f, 1.0f).requiresSettings(this.sound));
    public Setting<Boolean> ignoreNatural = add(new BooleanSetting("IgnoreNatural", false).setVisible());
    public Setting<Boolean> dungeon = add(new BooleanSetting("Dungeon", true).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> fortress = add(new BooleanSetting("Fortress", true).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> bastion = add(new BooleanSetting("Bastion", true).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> ancientCity = add(new BooleanSetting("AncientCity", false).requiresSettings(this.ignoreNatural));
    public Setting<Boolean> trialChambers = add(new BooleanSetting("TrialChambers", true).requiresSettings(this.ignoreNatural));
    public final Stopwatch stopwatch;
    public final Set<BlockPos> set;
    public final ChunkUpdateHandler ChunkUpdateHandler;
    public boolean flag;

    public Search() {
        super("Search", "Helps finding whitelisted stuff.", Category.RENDER, new String[0]);
        this.stopwatch = new Stopwatch();
        this.set = Collections.synchronizedSet(new HashSet());
        this.ChunkUpdateHandler = new ChunkUpdateHandler(1, (chunkPos, worldChunk) -> {
            if (this.set.size() > 100000) {
                return;
            }
            Set<Block> value = this.whitelist.getValue();
            for (int i = 0; i < 16; i++) {
                for (int bottomY = this.ignoreNeg.getValue().booleanValue() ? 0 : minecraftClient.world.getBottomY(); bottomY < minecraftClient.world.getTopY(); bottomY++) {
                    for (int i2 = 0; i2 < 16; i2++) {
                        mutable.set(chunkPos.getStartX() + i, bottomY, chunkPos.getStartZ() + i2);
                        BlockState blockState = worldChunk.getBlockState(mutable);
                        if (!blockState.isAir()) {
                            if (this.ignoreNatural.getValue().booleanValue()) {
                                if (WorldUtils.isStashChest((Chunk) worldChunk, mutable, this.fortress.getValue().booleanValue(), this.bastion.getValue().booleanValue(), this.ancientCity.getValue().booleanValue(), this.dungeon.getValue().booleanValue(), this.trialChambers.getValue().booleanValue())) {
                                }
                            }
                            if (value.contains(blockState.getBlock())) {
                                this.flag = true;
                                this.set.add(mutable.toImmutable());
                            }
                        }
                    }
                }
            }
        }, (chunkPos2, worldChunk2) -> {
            this.set.removeIf(blockPos -> {
                return blockPos.getX() >= chunkPos2.getStartX() && blockPos.getX() <= chunkPos2.getEndX() && blockPos.getZ() >= chunkPos2.getStartZ() && blockPos.getZ() <= chunkPos2.getEndZ();
            });
        }, (blockPos, blockState) -> {
            if (!this.whitelist.getValue().contains(blockState.getBlock())) {
                this.set.remove(blockPos);
            } else if (!blockState.isAir() && this.set.add(blockPos)) {
                this.flag = true;
            }
        });
        this.whitelist.setChangeCallback(() -> {
            if (minecraftClient.world == null || this.ChunkUpdateHandler.getThreadPool() == null) {
                return;
            }
            this.set.clear();
            this.ChunkUpdateHandler.processAllChunks();
        });
    }

    @Override // me.mioclient.module.Module
    public void onEnable() {
        this.flag = false;
        this.ChunkUpdateHandler.do1640();
        if (isNotInGame()) {
            return;
        }
        this.ChunkUpdateHandler.processAllChunks();
    }

    @Override // me.mioclient.module.Module
    public void onDisable() {
        this.set.clear();
        this.ChunkUpdateHandler.stopListening();
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        resetSearchState();
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof PlayerRespawnS2CPacket) {
            resetSearchState();
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.sound.getValue().booleanValue() && this.flag) {
            this.flag = false;
            if (this.stopwatch.hasElapsedMs(100L)) {
                ClientServices.killstreakSoundManager.findSoundPlayer(this.type.getValue()).play(this.volume.getValue().floatValue());
                this.stopwatch.reset();
            }
        }
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        if (this.entities.getValue().booleanValue()) {
            for (Entity entity : minecraftClient.world.getEntities()) {
                if (this.entityTypes.getValue().contains(entity.getType())) {
                    Box box233 = BoxMath.getInterpolatedBox(entity, inner_3.getTickDelta());
                    Color color61 = getColor61(entity);
                    colors.scheme.getValue().runWithShaderColor(() -> {
                        if (RotationManager.isInFrustum(box233)) {
                            if (this.fill.getValue().booleanValue()) {
                                BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), box233, ColorUtils.withAlpha(color61, (int) (Float.intBitsToFloat(1132396544) * this.fillOpacity.getValue().floatValue())));
                            }
                            if (this.outline.getValue().booleanValue()) {
                                BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), box233, color61, Float.intBitsToFloat(1065353216));
                            }
                        }
                        if (this.tracers.getValue().booleanValue()) {
                            Camera camera = minecraftClient.gameRenderer.getCamera();
                            RenderUtil.INSTANCE.drawDebugLine(inner_3.getMatrixStack(), new Vec3d(0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L)).rotateX(-((float) Math.toRadians(camera.getPitch()))).rotateY(-((float) Math.toRadians(camera.getYaw()))).add(minecraftClient.getEntityRenderDispatcher().camera.getPos()), box233.getCenter(), ColorUtils.withAlpha(color61, (int) (Float.intBitsToFloat(1132396544) * this.tracerOpacity.getValue().floatValue())));
                        }
                    });
                }
            }
        }
        synchronized (this.set) {
            for (BlockPos blockPos : this.set) {
                BlockState blockState = minecraftClient.world.getBlockState(blockPos);
                if (!blockState.isAir() && !blockState.isOf(Blocks.AIR)) {
                    VoxelShape outlineShape = blockState.getOutlineShape(minecraftClient.world, blockPos);
                    if (outlineShape.isEmpty()) {
                        outlineShape = VoxelShapes.cuboid(0.0d, 0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L), Double.longBitsToDouble(4607182418800017408L));
                    }
                    Box offset = outlineShape.getBoundingBox().offset(blockPos);
                    Color color60 = getColor60(blockState, blockPos);
                    colors.scheme.getValue().runWithShaderColor(() -> {
                        if (RotationManager.isInFrustum(offset)) {
                            if (this.fill.getValue().booleanValue()) {
                                BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), offset, ColorUtils.withAlpha(color60, (int) (Float.intBitsToFloat(1132396544) * this.fillOpacity.getValue().floatValue())));
                            }
                            if (this.outline.getValue().booleanValue()) {
                                BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), offset, color60, Float.intBitsToFloat(1065353216));
                            }
                        }
                        if (this.tracers.getValue().booleanValue()) {
                            Camera camera = minecraftClient.gameRenderer.getCamera();
                            RenderUtil.INSTANCE.drawDebugLine(inner_3.getMatrixStack(), new Vec3d(0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L)).rotateX(-((float) Math.toRadians(camera.getPitch()))).rotateY(-((float) Math.toRadians(camera.getYaw()))).add(minecraftClient.getEntityRenderDispatcher().camera.getPos()), Vec3d.ofCenter((Vec3i) blockPos), ColorUtils.withAlpha(color60, (int) (Float.intBitsToFloat(1132396544) * this.tracerOpacity.getValue().floatValue())));
                        }
                    });
                }
            }
        }
    }

    public void resetSearchState() {
        this.ChunkUpdateHandler.restartExecutor();
        this.set.clear();
    }

    public Color getColor60(BlockState blockState, BlockPos blockPos) {
        if (blockState.getBlock() == Blocks.NETHER_PORTAL) {
            return new Color(144, 28, 255);
        }
        if (blockState.getBlock() == Blocks.ENDER_CHEST) {
            return new Color(125, 40, 180);
        }
        int renderColor = blockState.getMapColor(minecraftClient.world, blockPos).getRenderColor(MapColor.Brightness.HIGH);
        BedBlock block = (blockState.getBlock()) instanceof BedBlock ? (BedBlock) (blockState.getBlock()) : null;
        if (block instanceof BedBlock) {
            renderColor = block.getColor().getMapColor().getRenderColor(MapColor.Brightness.HIGH);
        }
        return new Color(renderColor & 255, (renderColor >> 8) & 255, (renderColor >> 16) & 255);
    }

    public Color getColor61(Entity entity) {
        Color color = Color.GRAY;
        if (entity instanceof AbstractMinecartEntity) {
            color = Color.blue;
        }
        if (EntityUtil.isHostile(entity)) {
            color = Color.RED;
        }
        if (entity instanceof PlayerEntity) {
            color = ClientServices.socialManager.getSocialColor(((PlayerEntity) entity).getName().getString(), color);
        }
        if ((entity instanceof AnimalEntity) || (entity instanceof FishEntity)) {
            color = Color.GREEN;
        }
        return color;
    }
}
