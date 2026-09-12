package me.mioclient.mode.setting;

import me.mioclient.api.Setting;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HoleSnapMode.class */
public enum HoleSnapMode {
    MIN { // from class: me.mioclient.mode.setting.HoleSnapMode.Inner
        @Override // me.mioclient.mode.setting.HoleSnapMode
        public boolean isAtExtremum(Setting<?> setting) {
            return setting.getValue().equals(setting.getMinValue());
        }
    },
    MAX { // from class: me.mioclient.mode.setting.HoleSnapMode.Inner_2
        @Override // me.mioclient.mode.setting.HoleSnapMode
        public boolean isAtExtremum(Setting<?> setting) {
            return setting.getValue().equals(setting.getMaxValue());
        }
    };

    public boolean isAtExtremum(Setting<?> setting) {
        return false;
    }
}
