
package nick;

import me.mioclient.module.HudModule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import me.mioclient.module.misc.AntiAFK;
import me.mioclient.module.combat.AntiBot;
import me.mioclient.module.exploit.AntiVanish;
import me.mioclient.module.combat.Aura;
import me.mioclient.module.combat.AutoArmor;
import me.mioclient.module.combat.AutoBowRelease;
import me.mioclient.module.combat.AutoExp;
import me.mioclient.module.player.AutoFarm;
import me.mioclient.module.player.AutoTool;
import me.mioclient.module.render.Chams;
import me.mioclient.module.Chat;
import me.mioclient.module.misc.ChestSearchBar;
import me.mioclient.module.player.ChestStealer;
import me.mioclient.module.combat.Criticals;
import me.mioclient.module.Crypto;
import me.mioclient.module.misc.CustomDeathText;
import me.mioclient.module.client.Discord;
import me.mioclient.module.misc.DiscordNotifs;
import me.mioclient.module.render.ESP;
import me.mioclient.module.misc.ExtraScreenshot;
import me.mioclient.module.exploit.FastProjectile;
import me.mioclient.module.movement.FastSwim;
import me.mioclient.module.movement.FastWeb;
import me.mioclient.module.movement.Fireworks;
import me.mioclient.module.render.FreeLook;
import me.mioclient.module.render.Glint;
import me.mioclient.module.Graph;
import me.mioclient.module.exploit.GrimDisabler;
import me.mioclient.module.render.Highlight;
import me.mioclient.module.render.HoleESP;
import me.mioclient.module.movement.HoleSnap;
import me.mioclient.module.client.IRC;
import me.mioclient.module.Inventory;
import me.mioclient.module.player.InventoryTweaks;
import me.mioclient.module.movement.Jesus;
import me.mioclient.module.render.LogoutSpots;
import me.mioclient.module.Module;
import me.mioclient.module.exploit.MultiTask;
import me.mioclient.module.player.NameProtect;
import me.mioclient.module.render.NameTags;
import me.mioclient.module.movement.NoFall;
import me.mioclient.module.player.NoInteract;
import me.mioclient.module.exploit.NoMineAnimation;
import me.mioclient.module.misc.NoNarrator;
import me.mioclient.module.render.NoRender;
import me.mioclient.module.misc.NoRotate;
import me.mioclient.module.movement.NoSlow;
import me.mioclient.module.combat.Offhand;
import me.mioclient.module.misc.PMSound;
import me.mioclient.module.exploit.PingSpoof;
import me.mioclient.module.render.Search;
import me.mioclient.module.combat.SelfFill;
import me.mioclient.module.movement.Speed;
import me.mioclient.module.movement.Sprint;
import me.mioclient.module.movement.Step;
import me.mioclient.module.TextRadar;
import me.mioclient.module.exploit.Timer;
import me.mioclient.module.render.Tracers;
import me.mioclient.module.client.UI;
import me.mioclient.module.misc.VisualRange;
import me.mioclient.module.movement.Warp;
import me.mioclient.module.Welcomer;
import me.mioclient.module.exploit.XCarry;
import me.mioclient.module.render.Zoom;

public final class ModuleRegistry {
    private static final List<String> MODULES = List.of("me/mioclient/module/client/UI", "me/mioclient/module/render/HoleESP", "me/mioclient/module/render/NoRender", "me/mioclient/module/misc/NoNarrator", "me/mioclient/module/player/AutoFarm", "me/mioclient/module/movement/FastSwim", "me/mioclient/module/combat/Offhand", "me/mioclient/module/movement/Warp", "me/mioclient/module/exploit/MultiTask", "me/mioclient/module/client/Discord", "me/mioclient/module/combat/AntiBot", "me/mioclient/module/combat/AutoBowRelease", "me/mioclient/module/exploit/PingSpoof", "me/mioclient/module/client/IRC", "me/mioclient/module/movement/FastWeb", "me/mioclient/module/misc/NoRotate", "me/mioclient/module/misc/VisualRange", "me/mioclient/module/combat/AutoExp", "me/mioclient/module/Welcomer", "me/mioclient/module/Chat", "me/mioclient/module/exploit/NoMineAnimation", "me/mioclient/module/exploit/FastProjectile", "me/mioclient/module/Crypto", "me/mioclient/module/misc/ExtraScreenshot", "me/mioclient/module/combat/SelfFill", "me/mioclient/module/ModuleList", "me/mioclient/module/combat/AutoArmor", "me/mioclient/module/render/Chams", "me/mioclient/module/movement/Sprint", "me/mioclient/module/Graph", "me/mioclient/module/render/Highlight", "me/mioclient/module/combat/Criticals", "me/mioclient/module/Inventory", "me/mioclient/module/movement/NoSlow", "me/mioclient/module/exploit/AntiVanish", "me/mioclient/module/player/NoInteract", "me/mioclient/module/misc/PMSound", "me/mioclient/module/movement/Speed", "me/mioclient/module/player/InventoryTweaks", "me/mioclient/module/misc/DiscordNotifs", "me/mioclient/module/exploit/XCarry", "me/mioclient/module/render/FreeLook", "me/mioclient/module/player/ChestStealer", "me/mioclient/module/movement/Fireworks", "me/mioclient/module/misc/CustomDeathText", "me/mioclient/module/combat/Aura", "me/mioclient/module/movement/HoleSnap", "me/mioclient/module/misc/ChestSearchBar", "me/mioclient/module/render/Glint", "me/mioclient/module/TextRadar", "me/mioclient/module/misc/AntiAFK", "me/mioclient/module/render/NameTags", "me/mioclient/module/render/Search", "me/mioclient/module/render/Tracers", "me/mioclient/module/render/LogoutSpots", "me/mioclient/module/exploit/Timer", "me/mioclient/module/movement/Step", "me/mioclient/module/movement/NoFall", "me/mioclient/module/player/AutoTool", "me/mioclient/module/render/Zoom", "me/mioclient/module/movement/Jesus", "me/mioclient/module/render/ESP", "me/mioclient/module/player/NameProtect", "me/mioclient/module/exploit/GrimDisabler", "me/mioclient/module/render/Ambience", "me/mioclient/module/combat/AnchorAura", "me/mioclient/module/render/Animations", "me/mioclient/module/misc/AntiAim", "me/mioclient/module/client/AntiCheat", "me/mioclient/module/exploit/AntiLevitation", "me/mioclient/module/misc/AntiQuit", "me/mioclient/module/combat/Arrows", "me/mioclient/module/combat/AutoClicker", "me/mioclient/module/combat/AutoCrystal", "me/mioclient/module/player/AutoEat", "me/mioclient/module/combat/AutoMine", "me/mioclient/module/misc/AutoReconnect", "me/mioclient/module/misc/BetterChat", "me/mioclient/module/render/Blur", "me/mioclient/module/misc/ChatFilter", "me/mioclient/module/exploit/ChorusControl", "me/mioclient/module/client/Colors", "me/mioclient/module/render/Crosshair", "me/mioclient/module/movement/ElytraFly", "me/mioclient/module/movement/EntityControl", "me/mioclient/module/misc/ExtraTab", "me/mioclient/module/movement/FakeLag", "me/mioclient/module/exploit/FakeVanilla", "me/mioclient/module/movement/FastFall", "me/mioclient/module/movement/FastLadder", "me/mioclient/module/exploit/FastLatency", "me/mioclient/module/player/FastPlace", "me/mioclient/module/combat/FeetPlace", "me/mioclient/module/movement/Flight", "me/mioclient/module/client/Fonts", "me/mioclient/module/player/Freecam", "me/mioclient/module/render/Hitmarker", "me/mioclient/module/client/HUD", "me/mioclient/module/exploit/IllegalDisconnect", "me/mioclient/module/player/ItemSaver", "me/mioclient/module/misc/KillEffects", "me/mioclient/module/movement/LongJump", "me/mioclient/module/misc/MiddleClick", "me/mioclient/module/exploit/NewChunks", "me/mioclient/module/render/NoBob", "me/mioclient/module/player/NoGlitchBlocks", "me/mioclient/module/combat/NoHitDelay", "me/mioclient/module/movement/NoJumpDelay", "me/mioclient/module/misc/NoPacketKick", "me/mioclient/module/client/Notifications", "me/mioclient/module/movement/ObstaclePasser", "me/mioclient/module/exploit/Phase", "me/mioclient/module/PlayerModel", "me/mioclient/module/combat/Range", "me/mioclient/module/exploit/Reach", "me/mioclient/module/exploit/RocketExtender", "me/mioclient/module/player/RotationLock", "me/mioclient/module/movement/SafeWalk", "me/mioclient/module/player/Scaffold", "me/mioclient/module/render/Shader", "me/mioclient/module/render/SkyColor", "me/mioclient/module/player/SpeedMine", "me/mioclient/module/misc/StashFinder", "me/mioclient/module/misc/Swing", "me/mioclient/module/render/Tooltips", "me/mioclient/module/render/Trajectories", "me/mioclient/module/exploit/Trident", "me/mioclient/module/misc/UnfocusedFPS", "me/mioclient/module/movement/Velocity", "me/mioclient/module/render/ViewClip", "me/mioclient/module/render/ViewModel", "me/mioclient/module/render/Xray", "me/mioclient/module/client/Baritone", "me/mioclient/module/combat/AimAssist", "me/mioclient/module/combat/AntiPhase", "me/mioclient/module/combat/AnvilAura", "me/mioclient/module/combat/AutoLog", "me/mioclient/module/combat/BedAura", "me/mioclient/module/combat/Blocker", "me/mioclient/module/combat/BowAim", "me/mioclient/module/combat/FishTrap", "me/mioclient/module/combat/HoleFill", "me/mioclient/module/combat/KeyPearl", "me/mioclient/module/combat/Mainhand", "me/mioclient/module/combat/Pusher", "me/mioclient/module/combat/SelfTrap", "me/mioclient/module/combat/SelfWeb", "me/mioclient/module/combat/Trigger", "me/mioclient/module/combat/WebAura", "me/mioclient/module/exploit/AntiHunger", "me/mioclient/module/exploit/HitboxDesync", "me/mioclient/module/exploit/PortalGodMode", "me/mioclient/module/exploit/RaytraceBypass", "me/mioclient/module/misc/Announcer", "me/mioclient/module/misc/AntiCyrillic", "me/mioclient/module/misc/AntiSpam", "me/mioclient/module/misc/AutoRespawn", "me/mioclient/module/misc/AutoSign", "me/mioclient/module/misc/CoordLogger", "me/mioclient/module/misc/Heaven", "me/mioclient/module/misc/SkinFlicker", "me/mioclient/module/misc/SoundBlocker", "me/mioclient/module/misc/Spammer", "me/mioclient/module/movement/AntiVoid", "me/mioclient/module/movement/AutoWalk", "me/mioclient/module/movement/HighJump", "me/mioclient/module/player/AutoBreed", "me/mioclient/module/player/AutoCraft", "me/mioclient/module/player/AutoFish", "me/mioclient/module/player/AutoMount", "me/mioclient/module/player/AutoNameTag", "me/mioclient/module/player/AutoTame", "me/mioclient/module/player/AutoUpgrade", "me/mioclient/module/player/BlockMixer", "me/mioclient/module/player/InventoryCleaner", "me/mioclient/module/player/Nuker", "me/mioclient/module/player/Replenish", "me/mioclient/module/render/Borders", "me/mioclient/module/render/BreakHighlight", "me/mioclient/module/render/Markers", "me/mioclient/module/render/Particles", "me/mioclient/module/render/PhaseESP", "me/mioclient/module/render/Skeleton", "me/mioclient/module/render/Trails", "me/mioclient/module/render/Tunnels", "me/mioclient/module/render/VoidESP", "me/mioclient/module/render/Waypoints");
    private static final List<String> EXTRA = List.of();

    public static void initialize(Object manager) {
        Class<?> k = manager.getClass();
        Method m;
        try {
            m = k.getDeclaredMethod("register", Class.forName("me.mioclient.module.Module"));
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
            return;
        }
        for (String module : MODULES) {
            try {
                Class<?> klass = Class.forName(module.replace('/', '.'));
                Constructor<?> c = klass.getDeclaredConstructor(new Class[0]);
                m.invoke(manager, Objects.requireNonNull(c).newInstance(new Object[0]));
            } catch (Throwable _t) {
                System.err.println("[MioClient] Failed to load module: " + module);
                _t.printStackTrace(System.err);
            }
        }
    }

    public static void extra(Object manager) {
        Class<?> k = manager.getClass();
        Method m;
        try {
            m = k.getDeclaredMethod("register", Class.forName("me.mioclient.module.Module"));
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
            return;
        }
        for (String module : EXTRA) {
            try {
                Class<?> klass = Class.forName(module.replace('/', '.'));
                Constructor<?> c = klass.getDeclaredConstructor(new Class[0]);
                m.invoke(manager, Objects.requireNonNull(c).newInstance(new Object[0]));
            } catch (Throwable _t) {
                System.err.println("[MioClient] Failed to load module: " + module);
                _t.printStackTrace(System.err);
            }
        }
    }
}
