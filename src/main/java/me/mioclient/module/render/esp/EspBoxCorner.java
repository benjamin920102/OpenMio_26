package me.mioclient.module.render.esp;

import dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase;

public class EspBoxCorner implements EspBoxEdge {
    @Override // me.mioclient.module.render.esp.EspBoxEdge
    public boolean isCullingEnabled() {
        return EntityCullingVersionlessBase.enabled;
    }

    @Override // me.mioclient.module.render.esp.EspBoxEdge
    public void setCullingEnabled(boolean z) {
        EntityCullingVersionlessBase.enabled = z;
    }
}