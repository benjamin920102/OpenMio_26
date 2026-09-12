package me.mioclient.module.render;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.setting.SoundIdentifierSetting;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.util.player.ClonedPlayerEntity;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.api.NamedValue;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.mode.world.StashFinderMode;
import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.api.NamedValue;
import me.mioclient.render.shape.BoxRenderer;
import me.mioclient.render.chams.ChamsRenderDispatcher;
import me.mioclient.render.font.ClientTextRenderer;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.PlayerUtils;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import me.mioclient.util.text.StringTemplate;
import me.mioclient.command.FakePlayerCommand;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.util.player.ClonedPlayerEntity;
import me.mioclient.manager.KillstreakSoundManager;
import me.mioclient.util.world.WorldUtils;
import me.mioclient.util.client.SoundIdentifier;
import me.mioclient.mode.world.StashFinderMode;
import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ConnectEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.entity.RemoveEntityEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;
import me.mioclient.module.client.Fonts;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import me.mioclient.setting.BooleanSetting;
import me.mioclient.setting.ColorSetting;
import me.mioclient.setting.EnumSetting;
import me.mioclient.setting.NumberSetting;
import me.mioclient.setting.SoundIdentifierSetting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/module/render/LogoutSpots.class */
public class LogoutSpots extends Module {
    public static final Fonts fonts = (Fonts) ClientServices.moduleInjector.getModule(Fonts.class);
    public static boolean flag = false;
    public Setting<Float> distance = add(new NumberSetting<>("Distance", 128.0f, 16.0f, 301.0f).setSuffix("m").setHoleSnapConfig("Unlimited", HoleSnapMode.MAX));
    public Setting<LogoutSpotsMode_2> model = add(new EnumSetting<>("Model", LogoutSpotsMode_2.NONE));
    public Setting<Boolean> textured = add(new BooleanSetting("Textured", false, v -> this.model.getValue() == LogoutSpots.LogoutSpotsMode_2.COMPLEX || this.model.getValue() == LogoutSpots.LogoutSpotsMode_2.BOTH).setVisible());
    public Setting<Float> alpha = add(new NumberSetting<>("Alpha", 0.5f, 0.0f, 1.0f));
    public Setting<Float> width = add(new NumberSetting<>("Width", 1.0f, 0.5f, 3.0f, v -> this.model.getValue() != LogoutSpots.LogoutSpotsMode_2.NONE));
    public Setting<Boolean> tracer = add(new BooleanSetting("Tracer", false).setVisible());
    public Setting<Float> width2 = add(new NumberSetting<>("Width", 1.0f, 0.1f, 1.0f, v -> this.tracer.isShown()));
    public Setting<Boolean> nameTag = add(new BooleanSetting("NameTag", true).setVisible());
    public Setting<Float> textScale = add(new NumberSetting<>("TextScale", 1.0f, 0.5f, 3.0f, v -> this.nameTag.isShown()));
    public Setting<LogoutSpotsMode> position = add(new EnumSetting<>("Position", LogoutSpotsMode.NONE, v -> this.nameTag.isShown()));
    public Setting<Boolean> time = add(new BooleanSetting("Time", false, v -> this.nameTag.isShown()));
    public Setting<Boolean> health = add(new BooleanSetting("Health", true, v -> this.nameTag.isShown()));
    public Setting<Boolean> totems = add(new BooleanSetting("Totems", false).requiresSettings(this.nameTag));
    public Setting<Boolean> sounds = add(new BooleanSetting("Sounds", false).setVisible());
    public Setting<Boolean> login = add(new BooleanSetting("Login", true).setVisible().requiresSettings(this.sounds));
    public Setting<SoundIdentifier> sound2 = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "steam")).setVisible().requiresSettings(this.sounds, this.login));
    public Setting<Boolean> logout = add(new BooleanSetting("Logout", true).setVisible().requiresSettings(this.sounds));
    public Setting<SoundIdentifier> sound = add(new SoundIdentifierSetting("Sound", new SoundIdentifier("system", "steam")).setVisible().requiresSettings(this.sounds, this.logout));
    public Setting<Boolean> colors = add(new BooleanSetting("Colors", true).setDependent());
    public Setting<Color> modelFill = add(new ColorSetting("ModelFill", new Color(1019058687, true)).setThemeSync(true));
    public Setting<Color> modelLine = add(new ColorSetting("ModelLine", new Color(-423781889, true)).setThemeSync(true));
    public Setting<Color> boxFill = add(new ColorSetting("BoxFill", new Color(1019058687, true)));
    public Setting<Color> boxLine = add(new ColorSetting("BoxLine", new Color(-423781889, true)));
    public Setting<Color> nameTagColor = add(new ColorSetting("NameTagColor", new Color(-289564161, true), v -> this.colors.isShown()).setThemeSync(true));
    public Setting<Color> fillColor = add(new ColorSetting("FillColor", new Color(1292503562, true), v -> this.colors.isShown()));
    public Setting<Boolean> filter = add(new BooleanSetting("Filter", false).setDependent());
    public Setting<Boolean> ignoreFriends = add(new BooleanSetting("IgnoreFriends", false).requiresSettings(this.filter));
    public Setting<Boolean> ignoreNakeds = add(new BooleanSetting("IgnoreNakeds", false).requiresSettings(this.filter));
    public final Object2ObjectOpenHashMap<String, Record> object2ObjectOpenHashMap;
    public final List<Record_2> list;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/LogoutSpots$LogoutSpotsMode.class */
    public enum LogoutSpotsMode implements NamedValue {
        NONE("None"),
        COORDINATES("Coordinates"),
        DISTANCE("Distance");

        public final String name;

        LogoutSpotsMode(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/LogoutSpots$LogoutSpotsMode_2.class */
    public enum LogoutSpotsMode_2 implements NamedValue {
        NONE("None"),
        SIMPLE("Simple"),
        COMPLEX("Complex"),
        BOTH("Both");

        public final String name;

        LogoutSpotsMode_2(String str) {
            this.name = str;
        }

        @Override // me.mioclient.api.NamedValue
        public String getName() {
            return this.name;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/LogoutSpots$Record.class */
    public static final class Record {
        public final long num;
        public final Box box;
        public final StashFinderMode stashFinderMode;
        public final int num2;
        public final int num3;
        public final Vec3d vec3d;
        public final ClonedPlayerEntity otherClientPlayerEntity;
        public final String string;

        public Record(long j, Box box, StashFinderMode stashFinderMode, int i, int i2, Vec3d vec3d, ClonedPlayerEntity otherClientPlayerEntity, String str) {
            this.num = j;
            this.box = box;
            this.stashFinderMode = stashFinderMode;
            this.num2 = i;
            this.num3 = i2;
            this.vec3d = vec3d;
            this.otherClientPlayerEntity = otherClientPlayerEntity;
            this.string = str;
        }

        public static Record getRecord797(PlayerEntity playerEntity, String str) {
            ClonedPlayerEntity otherClientPlayerEntity = new ClonedPlayerEntity(ClientAccessor.minecraftClient.world);
            otherClientPlayerEntity.copyFromPlayer(playerEntity);
            otherClientPlayerEntity.deathTime = 0;
            otherClientPlayerEntity.hurtTime = 0;
            return new Record(System.currentTimeMillis(), playerEntity.getBoundingBox(), WorldUtils.getDimension(), Math.round(EntityUtil.getEntityHealth((Entity) playerEntity)), ClientServices.logoutSpotsHelper.getPopCount(playerEntity), playerEntity.getPos(), otherClientPlayerEntity, str);
        }




        public long getTimestamp() {
            return this.num;
        }

        public Box getBox() {
            return this.box;
        }

        public StashFinderMode getRecordedDimension() {
            return this.stashFinderMode;
        }

        public int getHealth() {
            return this.num2;
        }

        public int getTotemPopCount() {
            return this.num3;
        }

        public Vec3d getPosition() {
            return this.vec3d;
        }

        public ClonedPlayerEntity getClonedPlayer() {
            return this.otherClientPlayerEntity;
        }

        public String getString805() {
            return this.string;
        }
    }

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/module/render/LogoutSpots$Record_2.class */
    private static final class Record_2 {
        public final PlayerEntity playerEntity;
        public final long num;
        public final AtomicBoolean atomicBoolean;

        public Record_2(PlayerEntity playerEntity, long j, AtomicBoolean atomicBoolean) {
            this.playerEntity = playerEntity;
            this.num = j;
            this.atomicBoolean = atomicBoolean;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - this.num >= 300 || this.atomicBoolean.get();
        }




        public PlayerEntity getPlayer() {
            return this.playerEntity;
        }

        public long getCreationTimestamp() {
            return this.num;
        }

        public AtomicBoolean getIsProcessed() {
            return this.atomicBoolean;
        }
    }

    public LogoutSpots() {
        super("LogoutSpots", "Highlights log out spots.", Category.RENDER, new String[0]);
        this.object2ObjectOpenHashMap = new Object2ObjectOpenHashMap<>();
        this.list = new ArrayList();
        this.distance.setHoleSnapConfig("Unlimited", HoleSnapMode.MAX);
        this.sound2.setConfigName("LoginSoundPath");
        this.sound.setConfigName("LogoutSoundPath");
        setDrawn(false);
    }

    @Override // me.mioclient.module.Module
    public void onToggle() {
        this.object2ObjectOpenHashMap.clear();
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        Camera camera = minecraftClient.gameRenderer.getCamera();
        Vec3d add = new Vec3d(0.0d, 0.0d, Double.longBitsToDouble(4607182418800017408L)).rotateX(-((float) Math.toRadians(camera.getPitch()))).rotateY(-((float) Math.toRadians(camera.getYaw()))).add(minecraftClient.getEntityRenderDispatcher().camera.getPos());
        synchronized (this.object2ObjectOpenHashMap) {
            Iterator it = this.object2ObjectOpenHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (((Record) entry.getValue()).getString805().equals(getServer()) && ((Record) entry.getValue()).getRecordedDimension() == WorldUtils.getDimension()) {
                    if (((Record) entry.getValue()).box.getCenter().distanceTo(minecraftClient.gameRenderer.getCamera().getPos()) <= this.distance.getValue().floatValue() || this.distance.getValue().floatValue() == Float.intBitsToFloat(1133936640)) {
                        if (this.model.getValue() == LogoutSpotsMode_2.SIMPLE || this.model.getValue() == LogoutSpotsMode_2.BOTH) {
                            BoxRenderer.drawFilledBox(inner_3.getMatrixStack(), ((Record) entry.getValue()).getBox(), this.boxFill.getValue());
                            BoxRenderer.drawBoxOutline(inner_3.getMatrixStack(), ((Record) entry.getValue()).getBox(), this.boxLine.getValue(), this.width.getValue().floatValue());
                        }
                        if (this.model.getValue() == LogoutSpotsMode_2.COMPLEX || this.model.getValue() == LogoutSpotsMode_2.BOTH) {
                            Entity otherClientPlayerEntity804 = ((Record) entry.getValue()).getClonedPlayer();
                            if (this.textured.getValue().booleanValue()) {
                                flag = true;
                                RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), this.alpha.getValue().floatValue());
                                RenderUtil.INSTANCE.renderEntity(otherClientPlayerEntity804, Float.intBitsToFloat(1065353216), inner_3.getMatrixStack(), minecraftClient.getBufferBuilders().getEntityVertexConsumers());
                                minecraftClient.getBufferBuilders().getEntityVertexConsumers().draw();
                                RenderSystem.setShaderColor(Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216), Float.intBitsToFloat(1065353216));
                                flag = false;
                            }
                            ChamsRenderDispatcher.setOutlineWidth(this.width.getValue().floatValue());
                            ChamsRenderDispatcher.setChamsColors(this.modelLine.getValue(), this.modelFill.getValue());
                            ChamsRenderDispatcher.renderEntityChams(inner_3.getMatrixStack(), otherClientPlayerEntity804);
                        }
                        if (this.tracer.getValue().booleanValue()) {
                            RenderUtil.INSTANCE.drawThickLine(inner_3.getMatrixStack(), add, ((Record) entry.getValue()).getBox().getCenter(), this.modelLine.getValue(), this.width2.getValue().floatValue());
                        }
                        if (this.nameTag.getValue().booleanValue()) {
                            Vec3d withAxis = ((Record) entry.getValue()).getBox().getCenter().withAxis(Direction.Axis.Y, ((Record) entry.getValue()).getBox().maxY + Double.longBitsToDouble(4602678819172646912L));
                            String string2921 = new StringTemplate().arg((String) entry.getKey()).format("\u0001 logout spot");
                            String string1289 = getString1289((Record) entry.getValue());
                            float intBitsToFloat = fonts.isToggled() ? Float.intBitsToFloat(1065353216) : 0.0f;
                            if (!string1289.isEmpty()) {
                                string2921 = new StringTemplate().arg(string1289).arg(string2921).format("\u0001\u0001");
                            }
                            double d = MathUtils.getDistanceScale(minecraftClient.gameRenderer.getCamera().getPos(), withAxis, this.textScale.getValue().floatValue());
                            RenderUtil.INSTANCE.drawBillboard(inner_3.getMatrixStack(), withAxis, Float.intBitsToFloat(-1124744561), 0.0f, (-(ClientTextRenderer.INSTANCE.getStringWidth(string2921) / Float.intBitsToFloat(1073741824))) - Float.intBitsToFloat(1073741824), Float.intBitsToFloat(1084227584) + intBitsToFloat, d * Double.longBitsToDouble(4611686018427387904L), this.fillColor.getValue());
                            RenderUtil.INSTANCE.drawTextBillboardOffset(inner_3.getDrawContext(), string2921, withAxis, 0.0f, 0.0f, -(ClientTextRenderer.INSTANCE.getStringWidth(string2921) / Float.intBitsToFloat(1073741824)), Float.intBitsToFloat(1073741824), d, this.nameTagColor.getValue(), true);
                        }
                    }
                }
            }
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        for (Record_2 record_2 : this.list) {
            PlayerEntity playerEntity1236 = record_2.getPlayer();
            if (is1290(playerEntity1236)) {
                record_2.atomicBoolean.set(true);
                this.object2ObjectOpenHashMap.put(playerEntity1236.getGameProfile().getName(), Record.getRecord797(playerEntity1236, getServer()));
                if (this.sounds.getValue().booleanValue() && this.logout.getValue().booleanValue()) {
                    KillstreakSoundManager KillstreakSoundManager = ClientServices.killstreakSoundManager;
                    KillstreakSoundManager.playSound(this.sound.getValue(), Float.intBitsToFloat(1065353216));
                }
            }
        }
        this.list.removeIf((v0) -> {
            return v0.isExpired();
        });
    }

    @Listen
    public void onPacketReceive(PacketReceiveEvent channelRead0Event) {
        PlayerListS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof PlayerListS2CPacket ? (PlayerListS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof PlayerListS2CPacket) {
            for (PlayerListS2CPacket.Entry entry : packet904.getPlayerAdditionEntries()) {
                minecraftClient.executeSync(() -> {
                    GameProfile profile = entry.profile();
                    String name = profile == null ? "" : profile.getName();
                    if (this.sounds.getValue().booleanValue() && this.login.getValue().booleanValue() && this.object2ObjectOpenHashMap.containsKey(name)) {
                        ClientServices.killstreakSoundManager.playSound(this.sound2.getValue(), Float.intBitsToFloat(1065353216));
                    }
                    this.object2ObjectOpenHashMap.remove(name);
                });
            }
        }
    }

    @Listen
    public void onConnect(ConnectEvent connectEvent) {
        if (ClientServices.holeSnapRenderHelper.getServerInfo() == null || connectEvent.getServer().equals(ClientServices.holeSnapRenderHelper.getServerInfo().address)) {
            this.object2ObjectOpenHashMap.clear();
        }
    }

    @Listen
    public void onRemoveEntity(RemoveEntityEvent removeEntityEvent) {
        PlayerEntity entityById = (minecraftClient.world.getEntityById(removeEntityEvent.getId())) instanceof PlayerEntity ? (PlayerEntity) (minecraftClient.world.getEntityById(removeEntityEvent.getId())) : null;
        if (entityById instanceof PlayerEntity) {
            PlayerEntity playerEntity = entityById;
            if (!(playerEntity instanceof FakePlayerCommand.OtherClientPlayerEntity) && playerEntity.deathTime <= 0 && EntityUtil.getEntityHealth((Entity) playerEntity) > 0.0f) {
                if (this.ignoreFriends.getValue().booleanValue() && ClientServices.socialManager.isFriendEntity(playerEntity)) {
                    return;
                }
                if (!this.ignoreNakeds.getValue().booleanValue() || PlayerUtils.hasArmor((LivingEntity) playerEntity)) {
                    this.list.add(new Record_2(playerEntity, System.currentTimeMillis(), new AtomicBoolean()));
                }
            }
        }
    }

    public String getString1289(Record record) {
        StringBuilder sb = new StringBuilder();
        if (this.position.getValue() == LogoutSpotsMode.COORDINATES) {
            Vec3d vec3d803 = record.getPosition();
            sb.append(" ");
            sb.append(String.format(Locale.US, "X: %.1f, Y: %.1f, Z: %.1f", Double.valueOf(vec3d803.getX()), Double.valueOf(vec3d803.getY()), Double.valueOf(vec3d803.getZ())));
        }
        if (this.position.getValue() == LogoutSpotsMode.DISTANCE) {
            double distanceTo = minecraftClient.player.getPos().distanceTo(record.getPosition());
            sb.append(" ");
            sb.append("%.1fm".formatted(Double.valueOf(distanceTo)));
        }
        if (this.time.getValue().booleanValue()) {
            sb.append(" %s".formatted(new SimpleDateFormat("HH:mm").format(new Date(record.getTimestamp()))));
        }
        if (this.health.getValue().booleanValue()) {
            sb.append(" (");
            sb.append(record.num2);
            sb.append("hp)");
        }
        if (record.num3 > 0 && this.totems.getValue().booleanValue()) {
            sb.append(" -");
            sb.append(record.num3);
        }
        return sb.toString();
    }

    public boolean is1290(PlayerEntity playerEntity) {
        return minecraftClient.player.networkHandler.getPlayerList().stream().noneMatch(playerListEntry -> {
            return playerListEntry.getProfile().getName().equals(playerEntity.getGameProfile().getName());
        });
    }

    public static boolean is1291() {
        return flag;
    }

    public String getServer() {
        return (minecraftClient.world == null || minecraftClient.isInSingleplayer() || minecraftClient.player.networkHandler.getServerInfo() == null) ? "singleplayer" : minecraftClient.player.networkHandler.getServerInfo().address;
    }
}
