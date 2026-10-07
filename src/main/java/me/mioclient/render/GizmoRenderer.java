package me.mioclient.render;

import me.mioclient.api.ClientAccessor;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.CuboidGizmo;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.LineGizmo;
import net.minecraft.gizmos.SimpleGizmoCollector;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/* 26.2 world-render backend. All client world-space primitives (ESP boxes, lines,
   tracers, labels) go through vanilla's gizmo system: records created here are
   picked up by LevelRenderer and drawn by the GPU backend, so no custom submit
   pipeline or GL state is needed. Colors are packed ARGB ints (same layout as
   java.awt.Color#hashCode). Partly transparent fills and outlines are both
   supported via GizmoStyle. Gizmos expire after PERSIST_MS so a missed frame never
   leaves stale geometry on screen. */
public final class GizmoRenderer implements ClientAccessor {
    private static final SimpleGizmoCollector collector = new SimpleGizmoCollector();
    private static final int PERSIST_MS = 100;
    private static boolean worldPhase = false;

    public GizmoRenderer() {
        throw new AssertionError();
    }

    /* True while the world-render event is firing. Screen-space drawing must keep
       using the GUI extractor; only world drawing records gizmos. */
    public static void beginWorldPhase() {
        worldPhase = true;
    }

    public static void endWorldPhase() {
        worldPhase = false;
    }

    public static boolean isWorldPhase() {
        return worldPhase;
    }

    private static boolean debugErrorLogged;

    private static GizmoProperties keep(GizmoProperties props, boolean throughWalls) {
        if (props != null) {
            try {
                props.persistForMillis(PERSIST_MS);
                if (throughWalls) {
                    props.setAlwaysOnTop();
                }
            } catch (Exception e) {
                reportRecordError(e);
            }
        }
        return props;
    }

    private static void reportRecordError(Exception e) {
        if (!debugErrorLogged) {
            debugErrorLogged = true;
            System.err.println("[MioRender] GIZMO RECORD FAILED: " + e);
            e.printStackTrace();
        }
    }

    private static void reportEmitError(String where, Exception e) {
        if (!debugErrorLogged) {
            debugErrorLogged = true;
            System.err.println("[MioRender] GIZMO EMIT FAILED at " + where + ": " + e);
            e.printStackTrace();
        }
    }

    public static void box(AABB box, int strokeARGB, float width, int fillARGB, boolean throughWalls) {
        if (box == null) {
            return;
        }
        boolean stroke = (strokeARGB >>> 24) != 0 && width > 0.0f;
        boolean fill = (fillARGB >>> 24) != 0;
        if (!stroke && !fill) {
            return;
        }
        GizmoStyle style;
        if (stroke && fill) {
            style = GizmoStyle.strokeAndFill(strokeARGB, width, fillARGB);
        } else if (stroke) {
            style = GizmoStyle.stroke(strokeARGB, width);
        } else {
            style = GizmoStyle.fill(fillARGB);
        }
        try {
            keep(collector.add(new CuboidGizmo(box, style, false)), true);
        } catch (Exception e) {
            reportEmitError("box", e);
        }
    }

    public static void box(BlockPos pos, int strokeARGB, float width, int fillARGB, boolean throughWalls) {
        if (pos == null) {
            return;
        }
        box(new AABB(pos), strokeARGB, width, fillARGB, throughWalls);
    }

    /* Real gradient fade: bottomARGB at the base fading to topARGB at the top,
       rebuilt from stacked slabs so it stays 100 percent vanilla gizmos. */
    public static void fadingBox(AABB box, int bottomARGB, int topARGB) {
        if (box == null) {
            return;
        }
        int slices = 12;
        double step = (box.maxY - box.minY) / slices;
        if (!(step > 0.0d)) {
            box(box, 0, 0.0f, bottomARGB, false);
            return;
        }
        int a0 = bottomARGB >>> 24;
        int r0 = (bottomARGB >> 16) & 255;
        int g0 = (bottomARGB >> 8) & 255;
        int b0 = bottomARGB & 255;
        int a1 = topARGB >>> 24;
        int r1 = (topARGB >> 16) & 255;
        int g1 = (topARGB >> 8) & 255;
        int b1 = topARGB & 255;
        for (int k = 0; k < slices; k++) {
            float t = (k + 0.5f) / slices;
            int a = Math.round(a0 + (a1 - a0) * t);
            int r = Math.round(r0 + (r1 - r0) * t);
            int g = Math.round(g0 + (g1 - g0) * t);
            int b = Math.round(b0 + (b1 - b0) * t);
            int argb = (a << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
            if ((argb >>> 24) == 0) {
                continue;
            }
            double y0 = box.minY + step * k;
            box(new AABB(box.minX, y0, box.minZ, box.maxX, y0 + step, box.maxZ), 0, 0.0f, argb, false);
        }
    }

    public static void line(Vec3 a, Vec3 b, int argb, float width) {
        if (a == null || b == null || (argb >>> 24) == 0) {
            return;
        }
        try {
            keep(collector.add(new LineGizmo(a, b, argb, width <= 0.0f ? 1.0f : width)), true);
        } catch (Exception e) {
            reportEmitError("line", e);
        }
    }

    public static void text(Vec3 pos, String string, int argb, float scale) {
        if (pos == null || string == null || string.isEmpty()) {
            return;
        }
        try {
            TextGizmo.Style style = TextGizmo.Style.forColorAndCentered(argb).withScale(scale <= 0.0f ? 1.0f : scale);
            keep(collector.add(new TextGizmo(pos, string, style)), true);
        } catch (Exception e) {
            reportEmitError("text", e);
        }
    }

    public static void quad(Vec3 a, Vec3 b, Vec3 c, Vec3 d, int argb) {
        if (a == null || b == null || c == null || d == null || (argb >>> 24) == 0) {
            return;
        }
        try {
            keep(collector.add(new net.minecraft.gizmos.RectGizmo(a, b, c, d, GizmoStyle.fill(argb))), true);
        } catch (Exception e) {
            reportEmitError("quad", e);
        }
    }

    /* Drain this frame's records into vanilla. Called once per frame on the render
       thread right after the world-render event fires, so vanilla draws them in the
       same frame. Same-thread by confinement: the hook and vanilla's consumer both
       run inside LevelRenderer.render. */
    public static void flush() {
        try {
            if (minecraftClient.levelRenderer == null) {
                collector.drainGizmos();
                return;
            }
            List<SimpleGizmoCollector.GizmoInstance> drained = collector.drainGizmos();
            if (!drained.isEmpty()) {
                minecraftClient.levelRenderer.addMainThreadGizmos(drained);
            }
        } catch (Exception ignored) {
        }
    }
}
