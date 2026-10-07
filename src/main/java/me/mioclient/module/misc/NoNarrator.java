package me.mioclient.module.misc;

import me.mioclient.api.Category;
import me.mioclient.module.Module;

public class NoNarrator extends Module {
    public NoNarrator() {
        super("NoNarrator", "Disables the narrator feature.", Category.MISC, new String[0]);
        setDrawn(false);
    }
}