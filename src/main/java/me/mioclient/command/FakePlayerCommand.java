package me.mioclient.command;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.text.StringTemplate;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.client.InteractPacketReader;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.util.client.ChatUtils;
import me.mioclient.util.client.InteractPacketReader;

import me.mioclient.event.EventBus;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.DisconnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.player.Freecam;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Feature_14.class */
public final class FakePlayerCommand extends CommandFeature {
    public static Freecam freecam = (Freecam) ClientServices.moduleInjector.getModule(Freecam.class);
    public final List<Record> list;
    public OtherClientPlayerEntity otherClientPlayerEntity;
    public boolean flag;
    public boolean flag2;
    public String string;
    public int current;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/Feature_14$OtherClientPlayerEntity.class */
    public static class OtherClientPlayerEntity extends net.minecraft.client.network.OtherClientPlayerEntity {
        public OtherClientPlayerEntity(PlayerEntity playerEntity, ClientWorld clientWorld, GameProfile gameProfile) {
            super(clientWorld, gameProfile);
            copyPositionAndRotation((Entity) playerEntity);
            this.prevYaw = getYaw();
            this.prevPitch = getPitch();
            this.headYaw = playerEntity.headYaw;
            this.prevHeadYaw = this.headYaw;
            this.bodyYaw = playerEntity.bodyYaw;
            this.prevBodyYaw = this.bodyYaw;
            setPose(playerEntity.getPose());
            this.capeX = getX();
            this.capeY = getY();
            this.capeZ = getZ();
            setHealth(Float.intBitsToFloat(1101004800));
            getInventory().clone(playerEntity.getInventory());
        }

        public void setHealth(float f) {
            super.setHealth(f);
            if (getHealth() <= 0.0f) {
                super.setHealth(Float.intBitsToFloat(1091567616));
                clearStatusEffects();
                addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                ClientAccessor.minecraftClient.particleManager.addEmitter((Entity) this, ParticleTypes.TOTEM_OF_UNDYING, 30);
                ClientWorld clientWorld = ClientAccessor.minecraftClient.world;
                PlayerEntity playerEntity = ClientAccessor.minecraftClient.player;
                BlockPos blockPos = getBlockPos();
                SoundEvent soundEvent = SoundEvents.ITEM_TOTEM_USE;
                clientWorld.playSound(playerEntity, blockPos, soundEvent, SoundCategory.PLAYERS, Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
                ClientAccessor.eventBus.fireEvent(new PacketReceiveEvent(new EntityStatusS2CPacket(this, (byte) 35)));
            }
        }

        public void tick() {
            this.age++;
            if (this.age % 80 == 0) {
                addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
                addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0));
                addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0));
                addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3));
                setAbsorptionAmount(Float.intBitsToFloat(1098907648));
            }
            if (!getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                getInventory().setStack(40, new ItemStack(Items.TOTEM_OF_UNDYING, 1));
            }
            super.tick();
        }

        public boolean isImmuneToExplosion(Explosion explosion) {
            return false;
        }

        public boolean isAttackable() {
            return true;
        }

        public boolean isFireImmune() {
            return true;
        }

        public boolean isPushable() {
            return false;
        }

        public boolean isAlive() {
            return true;
        }

        public void updatePostDeath() {
            this.deathTime = 0;
        }

        public void pushAway(Entity entity) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/Feature_14$Record.class */
    public static final class Record {
        public final Vec3d vec3d;
        public final float val;
        public final float val2;
        public final float val3;
        public final Vec3d vec3d2;
        public final boolean flag;
        public final boolean flag2;

        public Record(Vec3d vec3d, float f, float f2, float f3, Vec3d vec3d2, boolean z, boolean z2) {
            this.vec3d = vec3d;
            this.val = f;
            this.val2 = f2;
            this.val3 = f3;
            this.vec3d2 = vec3d2;
            this.flag = z;
            this.flag2 = z2;
        }




        public Vec3d getPosition() {
            return this.vec3d;
        }

        public float getYaw() {
            return this.val;
        }

        public float getPitch() {
            return this.val2;
        }

        public float getHeadYaw() {
            return this.val3;
        }

        public Vec3d getTargetPos() {
            return this.vec3d2;
        }

        public boolean isSneaking() {
            return this.flag;
        }

        public boolean isOnGround() {
            return this.flag2;
        }
    }

    public FakePlayerCommand() {
        super("fakeplayer");
        this.list = new ArrayList();
        eventBus.register(this);
    }

    @Override // me.mioclient.command.CommandFeature
    public void exec(com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSource> literalArgumentBuilder) {
        literalArgumentBuilder.then(CommandFeature.argument("name", StringArgumentType.word()).executes(commandContext -> {
            spawnOrRemoveFakePlayer((String) commandContext.getArgument("name", String.class));
            return 1;
        })).then(CommandFeature.literal("record").executes(commandContext2 -> {
            this.flag = !this.flag;
            if (!this.flag) {
                ChatUtils.sendMessage(Text.literal("Stopped recording."), ChatUtils.createSignature(-1));
                return 1;
            }
            this.list.clear();
            ChatUtils.sendMessage(Text.literal("Started recording."), ChatUtils.createSignature(-1));
            return 1;
        })).then(CommandFeature.literal("play").executes(commandContext3 -> {
            this.current = 0;
            this.flag2 = !this.flag2;
            return 1;
        })).executes(commandContext4 -> {
            spawnOrRemoveFakePlayer("Herobrine");
            return 1;
        });
    }

    public void spawnOrRemoveFakePlayer(String str) {
        this.list.clear();
        this.flag = false;
        this.flag2 = false;
        if (this.otherClientPlayerEntity != null) {
            minecraftClient.world.removeEntity(-9344, Entity.RemovalReason.DISCARDED);
            this.otherClientPlayerEntity = null;
            ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(this.string == null ? str : this.string).format("\u0001 has been removed.")), ChatUtils.createSignature(-1));
            return;
        }
        this.otherClientPlayerEntity = new OtherClientPlayerEntity(minecraftClient.player, minecraftClient.world, new GameProfile(UUID.randomUUID(), str));
        if (freecam.isToggled()) {
            this.otherClientPlayerEntity.refreshPositionAndAngles(freecam.vec3d.x, freecam.vec3d.y, freecam.vec3d.z, minecraftClient.player.getYaw(), minecraftClient.player.getPitch());
        }
        this.otherClientPlayerEntity.setId(-9344);
        minecraftClient.world.addEntity(this.otherClientPlayerEntity);
        this.otherClientPlayerEntity.setOnGround(minecraftClient.player.isOnGround());
        this.string = str;
        ChatUtils.sendMessage(Text.literal(new StringTemplate().arg(str).format("\u0001 has been spawned.")), ChatUtils.createSignature(-1));
    }

    @Listen
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Post && this.flag) {
            this.list.add(new Record(minecraftClient.player.getPos(), minecraftClient.player.getYaw(), minecraftClient.player.getPitch(), minecraftClient.player.getHeadYaw(), minecraftClient.player.getVelocity(), minecraftClient.player.isSneaking(), minecraftClient.player.isOnGround()));
            return;
        }
        if (motionEvent.getPhase() != KeyPearlMode.Pre || this.otherClientPlayerEntity == null || !this.flag2 || this.list.isEmpty()) {
            return;
        }
        this.current = (this.current + 1) % this.list.size();
        Record record = this.list.get(this.current);
        this.otherClientPlayerEntity.updateTrackedPositionAndAngles(record.vec3d.x, record.vec3d.y, record.vec3d.z, record.val, record.val2, 3);
        this.otherClientPlayerEntity.setVelocity(record.vec3d2);
        this.otherClientPlayerEntity.setYaw(record.val);
        this.otherClientPlayerEntity.setPitch(record.val2);
        this.otherClientPlayerEntity.setHeadYaw(record.val3);
        this.otherClientPlayerEntity.setSneaking(record.flag);
        this.otherClientPlayerEntity.setOnGround(record.flag2);
        if (this.otherClientPlayerEntity.isSneaking()) {
            this.otherClientPlayerEntity.setPose(EntityPose.CROUCHING);
        } else {
            this.otherClientPlayerEntity.setPose(EntityPose.STANDING);
        }
    }

    @Listen
    public void onDisconnect(DisconnectEvent disconnectEvent) {
        this.otherClientPlayerEntity = null;
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (this.otherClientPlayerEntity == null) {
            return;
        }
        if ((sendImmediatelyEvent.getPacket() instanceof DisconnectS2CPacket) || (sendImmediatelyEvent.getPacket() instanceof DeathMessageS2CPacket)) {
            this.otherClientPlayerEntity = null;
        }
        PlayerInteractEntityC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerInteractEntityC2SPacket ? (PlayerInteractEntityC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 != null) {
            // resolvePacketEntity resolves the packet's entity id against the client world and returns
            // null whenever that entity is already gone (common: the crystal we just attacked).
            // JADX dropped the guard, so every attack packet for a dead entity NPE'd out of this
            // listener, which also killed the AutoCrystal break path that sends through here.
            final Entity target = InteractPacketReader.resolvePacketEntity(packet904);
            if (target != null
                    && InteractPacketReader.getCrystalInteractionType(packet904) == InteractPacketReader.LegacyCrystalMode.ATTACK
                    && target.getId() == this.otherClientPlayerEntity.getId()) {
                minecraftClient.execute(() -> {
                    this.otherClientPlayerEntity.attack(target);
                });
            }
        }
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof GameJoinS2CPacket) {
            this.otherClientPlayerEntity = null;
        }
        if (this.otherClientPlayerEntity == null) {
            return;
        }
        ExplosionS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof ExplosionS2CPacket ? (ExplosionS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof ExplosionS2CPacket) {
            ExplosionS2CPacket explosionS2CPacket = packet904;
            double x = explosionS2CPacket.getX();
            double y = explosionS2CPacket.getY();
            double z = explosionS2CPacket.getZ();
            double distanceTo = this.otherClientPlayerEntity.getPos().distanceTo(new Vec3d(x, y, z)) / Double.longBitsToDouble(4622945017495814144L);
            if (distanceTo > Double.longBitsToDouble(4607182418800017408L)) {
                return;
            }
            float radius = explosionS2CPacket.getRadius();
            double longBitsToDouble = (Double.longBitsToDouble(4607182418800017408L) - distanceTo) * Explosion.getExposure(new Vec3d(x, y, z), this.otherClientPlayerEntity);
            this.otherClientPlayerEntity.damage(minecraftClient.world.getDamageSources().explosion(new Explosion(minecraftClient.world, minecraftClient.player, x, y, z, radius, false, Explosion.DestructionType.DESTROY, explosionS2CPacket.getAffectedBlocks())), (float) (((((longBitsToDouble * longBitsToDouble) + y) / Double.longBitsToDouble(4611686018427387904L)) * Double.longBitsToDouble(4619567317775286272L) * radius * Double.longBitsToDouble(4611686018427387904L)) + Double.longBitsToDouble(4607182418800017408L)));
        }
    }
}
