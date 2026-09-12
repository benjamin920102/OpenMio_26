package me.mioclient.module.render.esp;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EspBoxRenderer.class */
public final class EspBoxRenderer {
    public static final String string = "dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase";
    public static final EspBoxEdge eSPHelper_3;
    public static boolean flag;
    public static boolean flag2;

    public static void disableEntityCulling() {
        if (flag || eSPHelper_3 == null) {
            return;
        }
        flag = true;
        flag2 = eSPHelper_3.isCullingEnabled();
        eSPHelper_3.setCullingEnabled(false);
    }

    public static void restoreEntityCulling() {
        if (!flag || eSPHelper_3 == null) {
            return;
        }
        flag = false;
        eSPHelper_3.setCullingEnabled(flag2);
    }

    static {
        EspBoxCorner eSPHelper_2 = null;
        try {
            Class.forName("dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase");
            eSPHelper_2 = new EspBoxCorner();
        } catch (ClassNotFoundException e) {
        }
        eSPHelper_3 = eSPHelper_2;
    }
}
