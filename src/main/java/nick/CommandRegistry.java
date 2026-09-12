/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package nick;

import me.mioclient.command.PanicCommand;

import me.mioclient.mode.render.NameTagsHelperMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import me.mioclient.command.ActionCommand;
import me.mioclient.command.AliasCommand;
import me.mioclient.command.AllOffCommand;
import me.mioclient.command.AngleCommand;
import me.mioclient.command.BindCommand;
import me.mioclient.command.ChatFilterCommand;
import me.mioclient.command.ClipCommand;
import me.mioclient.command.CommandFeature;
import me.mioclient.command.CompletableFutureFeature;
import me.mioclient.command.CompletableFutureFeature_2;
import me.mioclient.command.CoordsCommand;
import me.mioclient.command.DefaultsCommand;
import me.mioclient.command.DisconnectCommand;
import me.mioclient.command.DrawnCommand;
import me.mioclient.command.ExecutorServiceFeature_2;
import me.mioclient.command.ExecutorServiceFeature_3;
import me.mioclient.command.ExecutorServiceFeature_4;
import me.mioclient.command.FakePlayerCommand;
import me.mioclient.command.GlintCommand;
import me.mioclient.command.GlobalSettingCommand;
import me.mioclient.command.HelpCommand;
import me.mioclient.command.HighlightCommand;
import me.mioclient.command.IrcCommand;
import me.mioclient.command.KitCommand;
import me.mioclient.command.MacroCommand;
import me.mioclient.command.NameTagsCommand;
import me.mioclient.mode.render.NameTagsHelperMode;
import me.mioclient.command.NbtCommand;
import me.mioclient.command.OpenFolderCommand;
import me.mioclient.command.PitchCommand;
import me.mioclient.command.PrefixCommand;
import me.mioclient.command.PresetCommand;
import me.mioclient.command.PanicCommand;
import me.mioclient.command.PrintCommand;
import me.mioclient.command.SayCommand;
import me.mioclient.command.SessionCommand;
import me.mioclient.command.SetYawCommand;
import me.mioclient.command.StopwatchFeature;
import me.mioclient.command.ToggleCommand;
import me.mioclient.command.WaitCommand;
import me.mioclient.command.WaypointsCommand;
import me.mioclient.command.WebhookCommand;

public final class CommandRegistry {
    private static final List<String> COMMANDS = List.of("me/mioclient/command/AllOffCommand", "me/mioclient/command/PresetCommand", "me/mioclient/command/SessionCommand", "me/mioclient/command/FakePlayerCommand", "me/mioclient/command/SetYawCommand", "me/mioclient/command/PitchCommand", "me/mioclient/command/WaypointsCommand", "me/mioclient/command/OpenFolderCommand", "me/mioclient/command/DrawnCommand", "me/mioclient/command/PrefixCommand", "me/mioclient/command/MacroCommand", "me/mioclient/command/CoordsCommand", "me/mioclient/command/ClipCommand", "me/mioclient/command/SayCommand", "me/mioclient/command/PanicCommand", "me/mioclient/command/StopwatchFeature", "me/mioclient/command/ToggleCommand", "me/mioclient/command/NbtCommand", "me/mioclient/command/PrintCommand", "me/mioclient/command/DisconnectCommand", "me/mioclient/command/BindCommand", "me/mioclient/command/WebhookCommand", "me/mioclient/command/CompletableFutureFeature", "me/mioclient/command/HelpCommand", "me/mioclient/command/IrcCommand", "me/mioclient/command/CompletableFutureFeature_2", "me/mioclient/command/KitCommand", "me/mioclient/command/AliasCommand", "me/mioclient/command/ChatFilterCommand", "me/mioclient/command/GlobalSettingCommand", "me/mioclient/command/GlintCommand", "me/mioclient/command/AngleCommand", "me/mioclient/command/HighlightCommand", "me/mioclient/command/DefaultsCommand", "me/mioclient/command/ExecutorServiceFeature_4", "me/mioclient/command/ExecutorServiceFeature_2", "me/mioclient/command/ExecutorServiceFeature_3", "me/mioclient/command/ActionCommand", "me/mioclient/command/WaitCommand");

    public static void initialize(Object manager) {
        try {
            Class<?> k = manager.getClass();
            Method m = k.getDeclaredMethod("register", Class.forName("me.mioclient.command.CommandFeature"));
            for (String cmd : COMMANDS) {
                Class<?> klass = Class.forName(cmd.replace('/', '.'));
                Constructor<?>[] constructors = klass.getDeclaredConstructors();
                if (constructors.length != 1) {
                    throw new RuntimeException(cmd);
                }
                m.invoke(manager, constructors[0].newInstance(new Object[0]));
            }
            Class<?> en = Class.forName("me.mioclient.mode.render.NameTagsHelperMode");
            Object e1 = en.getDeclaredField("FRIEND").get(null);
            Object e2 = en.getDeclaredField("ENEMY").get(null);
            Class<?> klass = Class.forName("me.mioclient.command.NameTagsCommand");
            Constructor<?>[] constructors = klass.getDeclaredConstructors();
            if (constructors.length != 1) {
                throw new RuntimeException();
            }
            m.invoke(manager, constructors[0].newInstance(e1));
            m.invoke(manager, constructors[0].newInstance(e2));
        } catch (Throwable _t) {
            _t.printStackTrace(System.err);
        }
    }
}
