package me.mioclient.gui.minigame;

import me.mioclient.gui.widget.PanelComponent;

import me.mioclient.gui.panel.ClickGuiPanel;

import me.mioclient.gui.panel.ClickGuiPanel;
import me.mioclient.gui.GuiComponent;

import java.util.function.Supplier;
import me.mioclient.setting.GameStopwatchPresetSettingButton;
import me.mioclient.setting.PresetPresetSettingButton;
import me.mioclient.gui.widget.PanelComponent;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/feature/Game.class */
public class MinigamePanel extends ClickGuiPanel {
    public GameMode gameMode;

    /* compiled from: 0.java */
    /* loaded from: mio-yarn.jar:me/mioclient/feature/Game$Inner_2.class */
    class Inner_2 extends PresetPresetSettingButton {
        public Inner_2(ClickGuiPanel presetEnumSettingHelper, Supplier supplier) {
            super(presetEnumSettingHelper, (Supplier<String>) supplier);
        }

        @Override // me.mioclient.gui.widget.PanelComponent, me.mioclient.gui.GuiComponent
        public void mouseClicked(double d, double d2, int i) {
            super.mouseClicked(d, d2, i);
            if (containsPoint(d, d2) && i == 0) {
                MinigamePanel.this.switchGameMode(GameMode.values()[(MinigamePanel.this.gameMode.ordinal() + 1) % GameMode.values().length]);
            }
        }
    }

    public MinigamePanel() {
        super("Game");
        this.gameMode = GameMode.TETRIS;
        register(new TetrisGame(this, 0));
        register(new Inner_2(this, () -> {
            return this.gameMode.getName();
        }));
    }

    public void switchGameMode(GameMode gameMode) {
        GuiComponent gameStopwatchPresetSettingButton;
        this.gameMode = gameMode;
        switch (gameMode) {
            case TETRIS:
                gameStopwatchPresetSettingButton = new TetrisGame(this, 0);
                break;
            case SNAKE:
                gameStopwatchPresetSettingButton = new GameStopwatchPresetSettingButton(this, 0);
                break;
            default:
                throw new MatchException((String) null, (Throwable) null);
        }
        GuiComponent presetHelper_5 = gameStopwatchPresetSettingButton;
        synchronized (this.registry) {
            this.registry.set(0, presetHelper_5);
        }
    }

    @Override // me.mioclient.gui.panel.ClickGuiPanel
    public int getTotalWidth() {
        return 110;
    }
}
