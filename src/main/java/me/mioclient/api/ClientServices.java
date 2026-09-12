package me.mioclient.api;

import me.mioclient.event.events.client.ClientInitEvent;

import me.mioclient.manager.AsyncTaskTracker;
import me.mioclient.manager.HudManager;
import me.mioclient.manager.ModuleFieldInjector;
import me.mioclient.module.misc.stashfinder.StashRegistry;

import me.mioclient.gui.screen.ClickGuiScreen;
import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.manager.ItemListConfig;
import me.mioclient.module.combat.autocrystal.CrystalAttackHandler;
import me.mioclient.network.irc.IrcClient;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.baritone.BaritonePathingController;
import me.mioclient.util.baritone.NoopPathingController;
import me.mioclient.util.baritone.PathingController;
import me.mioclient.util.client.SessionRankState;
import me.mioclient.util.inventory.KitRegistry;
import me.mioclient.util.player.PlayerSpeedTracker;
import me.mioclient.util.player.SentMovementTracker;
import me.mioclient.util.time.FrameRateTracker;
import me.mioclient.util.time.TickScheduler;
import me.mioclient.util.world.BlockBreakProgressTracker;

import me.mioclient.api.ClientAccessor;
import me.mioclient.command.ClientCommandRegistry;
import me.mioclient.gui.screen.ClickGuiScreen;
import me.mioclient.gui.screen.HudEditorScreen;
import me.mioclient.module.combat.autocrystal.CrystalAttackHandler;
import me.mioclient.util.inventory.KitRegistry;
import me.mioclient.manager.AliasManager;
import me.mioclient.manager.ConfigManager;
import me.mioclient.manager.HoleTracker;
import me.mioclient.manager.InventorySyncTracker;
import me.mioclient.manager.MapStateCache;
import me.mioclient.manager.ModuleManager;
import me.mioclient.manager.PlayerHitboxTracker;
import me.mioclient.manager.ServerStateTracker;
import me.mioclient.manager.TickRateManager;
import me.mioclient.manager.TotemPopTracker;
import me.mioclient.manager.WaypointRegistry;
import me.mioclient.manager.WorldStateTracker;
import me.mioclient.module.misc.chatfilter.ChatFilterRegistry;
import me.mioclient.module.misc.discordnotifs.WebhookSender;
import me.mioclient.network.irc.IrcClient;
import me.mioclient.render.shape.BlockHighlightRenderer;
import me.mioclient.util.baritone.BaritonePathingController;
import me.mioclient.util.world.BlockBreakProgressTracker;
import me.mioclient.util.time.FrameRateTracker;
import me.mioclient.util.baritone.NoopPathingController;
import me.mioclient.util.baritone.PathingController;
import me.mioclient.util.player.PlayerSpeedTracker;
import me.mioclient.util.player.SentMovementTracker;
import me.mioclient.util.client.SessionRankState;
import me.mioclient.util.time.TickScheduler;

import java.util.Locale;
import me.mioclient.manager.ItemListConfig;
import me.mioclient.event.events.client.ClientInitEvent;
import me.mioclient.manager.AsyncTaskTracker;
import me.mioclient.api.Category;
import me.mioclient.manager.HudManager;
import me.mioclient.manager.KillstreakSoundManager;
import me.mioclient.manager.MacroManager;
import me.mioclient.manager.RotationManager;
import me.mioclient.manager.SocialManager;
import me.mioclient.module.misc.stashfinder.StashRegistry;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ClientServices.class */
public class ClientServices {
    public static ModuleFieldInjector moduleInjector = new ModuleFieldInjector();
    public static TickRateManager holeSnapHelper;
    public static ConfigManager presetHelper;
    public static RotationManager RotationManager;
    public static ModuleManager moduleRegistry;
    public static ClientCommandRegistry chatFilterChatHelper;
    public static FrameRateTracker hitmarkerHelper;
    public static ServerStateTracker holeSnapRenderHelper;
    public static SocialManager socialManager;
    public static WaypointRegistry waypointsManager;
    public static HoleTracker holeSnapOverlayHelper;
    public static KillstreakSoundManager killstreakSoundManager;
    public static MacroManager macroManager;
    public static AsyncTaskTracker asyncTaskTracker;
    public static SessionRankState welcomerHelper;
    public static IrcClient nameTagsManager;
    public static InventorySyncTracker fireworksHelper;
    public static PlayerHitboxTracker mainhandHelper;
    public static TotemPopTracker logoutSpotsHelper;
    public static PlayerSpeedTracker feetPlaceHelper;
    public static SentMovementTracker antiPhaseHelper;
    public static BlockHighlightRenderer antiPhaseBlockHelper;
    public static TickRateManager.Inner inner;
    public static StashRegistry stashFinderDataManager;
    public static TickScheduler tooltipsHelper;
    public static HudManager hudModuleRegistry;
    public static WebhookSender discordNotifsHelper;
    public static WorldStateTracker stashFinderHelper;
    public static CrystalAttackHandler scaffoldHelper;
    public static MapStateCache tooltipsSearchHelper4;
    public static KitRegistry chestStealerHelper;
    public static AliasManager notificationsHelper;
    public static ChatFilterRegistry chatFilterHelper;
    public static ItemListConfig items;
    public static BlockBreakProgressTracker breakHighlightHelper;
    public static PathingController obstaclePasserHelper;
    public static ClickGuiScreen clickGui;
    public static HudEditorScreen hUdSearchHelper4;

    public static void initialize() {
        try {
            Class.forName("baritone.api.BaritoneAPI");
            obstaclePasserHelper = new BaritonePathingController();
        } catch (Throwable th) {
            obstaclePasserHelper = new NoopPathingController();
        }
        welcomerHelper = new SessionRankState();
        antiPhaseHelper = new SentMovementTracker();
        holeSnapRenderHelper = new ServerStateTracker();
        breakHighlightHelper = new BlockBreakProgressTracker();
        holeSnapHelper = new TickRateManager();
        inner = new TickRateManager.Inner();
        mainhandHelper = new PlayerHitboxTracker();
        holeSnapOverlayHelper = new HoleTracker();
        logoutSpotsHelper = new TotemPopTracker();
        feetPlaceHelper = new PlayerSpeedTracker();
        RotationManager = new RotationManager();
        waypointsManager = new WaypointRegistry();
        antiPhaseBlockHelper = new BlockHighlightRenderer();
        tooltipsHelper = new TickScheduler();
        moduleRegistry = new ModuleManager();
        hudModuleRegistry = new HudManager();
        nameTagsManager = new IrcClient();
        chatFilterChatHelper = new ClientCommandRegistry();
        hitmarkerHelper = new FrameRateTracker();
        macroManager = new MacroManager();
        socialManager = new SocialManager();
        killstreakSoundManager = new KillstreakSoundManager();
        fireworksHelper = new InventorySyncTracker();
        asyncTaskTracker = new AsyncTaskTracker();
        stashFinderDataManager = new StashRegistry();
        discordNotifsHelper = new WebhookSender();
        stashFinderHelper = new WorldStateTracker();
        scaffoldHelper = new CrystalAttackHandler();
        tooltipsSearchHelper4 = new MapStateCache();
        chestStealerHelper = new KitRegistry();
        notificationsHelper = new AliasManager();
        chatFilterHelper = new ChatFilterRegistry();
        items = new ItemListConfig();
        Locale.setDefault(Locale.Category.FORMAT, Locale.US);
        completeInitialization();
    }

    public static void completeInitialization() {
        moduleInjector.injectModuleFields();
        moduleInjector.refreshAllModules();
        ClientAccessor.eventBus.fireEvent(new ClientInitEvent());
        killstreakSoundManager.reloadWorldRenderer();
        presetHelper = new ConfigManager();
        presetHelper.extractDefaults();
    }

    public static ClickGuiScreen getClickGui() {
        if (clickGui == null) {
            clickGui = new ClickGuiScreen();
        }
        return clickGui;
    }

    public static HudEditorScreen getHud() {
        if (hUdSearchHelper4 == null) {
            hUdSearchHelper4 = new HudEditorScreen();
        }
        return hUdSearchHelper4;
    }
}
