package me.mioclient.gui.panel;

import me.mioclient.api.ClientServices;

import me.mioclient.gui.GuiComponent;

import me.mioclient.gui.widget.ModuleButton;
import me.mioclient.render.font.ClientTextRenderer;

import me.mioclient.api.ClientAccessor;

import java.util.Iterator;
import me.mioclient.api.Category;
import me.mioclient.module.Module;

public class CategoryPanel extends ClickGuiPanel implements ClientAccessor {
    public Category category;

    public CategoryPanel(Category category) {
        super(ClientTextRenderer.capitalize(category.getName()));
        this.category = category;
        Iterator<Module> it = ClientServices.moduleRegistry.getModulesByCategory(category).iterator();
        while (it.hasNext()) {
            this.registry.add(new ModuleButton(it.next(), this, 0));
        }
        recalculateLayout();
    }

    @Override // me.mioclient.gui.panel.ClickGuiPanel
    public boolean shouldShowChild(GuiComponent presetHelper_5) {
        if (!(presetHelper_5 instanceof ModuleButton)) {
            return true;
        }
        ModuleButton arrayListPresetHelper2 = (ModuleButton) presetHelper_5;
        if (ClientServices.getClickGui().getSearchField().getValue().isEmpty()) {
            return true;
        }
        for (String str : arrayListPresetHelper2.getModule().getAliases()) {
            if (str.toLowerCase().contains(ClientServices.getClickGui().getSearchField().getValue().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
