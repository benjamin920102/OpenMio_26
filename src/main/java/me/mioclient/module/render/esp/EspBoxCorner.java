package me.mioclient.module.render.esp;

import dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EspBoxCorner.class */
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
