package me.mioclient.gui.widget;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HudElementType.class */
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
