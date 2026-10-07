package me.mioclient.mode.setting;

import me.mioclient.api.Setting;

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
