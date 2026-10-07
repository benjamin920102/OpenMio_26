package me.mioclient.module.render.chams;

import me.mioclient.api.NamedValue;

import me.mioclient.module.render.chams.ChamsColorApplier;

import java.awt.Color;
import me.mioclient.module.render.Chams;

public enum ChamsMode_2 implements NamedValue, ChamsColorApplier {
    BOTH("Both") { // from class: me.mioclient.module.render.chams.ChamsMode_2.Inner
        @Override // me.mioclient.module.render.chams.ChamsColorApplier
        public Color[] getChamsColors(Chams chams) {
            return new Color[]{chams.popLine.getValue(), chams.popFill.getValue()};
        }
    },
    FILL("Fill") { // from class: me.mioclient.module.render.chams.ChamsMode_2.Inner_2
        @Override // me.mioclient.module.render.chams.ChamsColorApplier
        public Color[] getChamsColors(Chams chams) {
            return new Color[]{color, chams.popFill.getValue()};
        }
    },
    LINE("Line") { // from class: me.mioclient.module.render.chams.ChamsMode_2.Inner_3
        @Override // me.mioclient.module.render.chams.ChamsColorApplier
        public Color[] getChamsColors(Chams chams) {
            return new Color[]{chams.popLine.getValue(), color};
        }
    };

    public final String name;

    ChamsMode_2(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }
}