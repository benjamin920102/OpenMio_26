package me.mioclient.gui.widget;

public enum PanelComponentFlag {
    PADDING_SHIFT,
    SILENT;

    public final int num = 1 << ordinal();

    PanelComponentFlag() {
    }

    public int getBitmask() {
        return this.num;
    }
}
