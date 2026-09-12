package me.mioclient.module.render.zoom;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.event.Listen;
import me.mioclient.module.render.Zoom;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ZoomFovListener.class */
public class ZoomFovListener implements ClientAccessor {
    public final Zoom zoom;

    public ZoomFovListener(Zoom zoom) {
        this.zoom = zoom;
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        int i = this.zoom.getZoomTargetFov();
        this.zoom.forceSetFov(i);
        minecraftClient.options.smoothCameraEnabled = this.zoom.isToggled() && this.zoom.smoothCamera.getValue().booleanValue();
        if (i < this.zoom.num || this.zoom.isToggled()) {
            this.zoom.flag2 = true;
        } else {
            eventBus.unregister(this);
            this.zoom.flag2 = false;
        }
    }
}
