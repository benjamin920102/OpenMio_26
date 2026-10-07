package me.mioclient.mode.client;

import me.mioclient.api.NamedValue;

public enum SearchMode implements NamedValue {
    NORMAL("Normal"),
    PROTANOPIA("Protanopia") { // from class: me.mioclient.mode.client.SearchMode.Inner
        @Override // me.mioclient.mode.client.SearchMode
        public float[] getShaderColor() {
            return new float[]{0.3f, 0.2f, 1.0f, 1.0f};
        }

        @Override // me.mioclient.mode.client.SearchMode
        public void runWithShaderColor(java.lang.Runnable runnable) {
            // 26.2: RenderSystem.setShaderColor was removed with the new GPU backend.
            // BoxRenderer.applyShaderColor is now a pass-through, so just run.
            runnable.run();
        }
    },
    DEUTERANOPIA("Deuteranopia") { // from class: me.mioclient.mode.client.SearchMode.Inner_3
        @Override // me.mioclient.mode.client.SearchMode
        public float[] getShaderColor() {
            return new float[]{0.3f, 0.0f, 1.0f, 1.0f};
        }

        @Override // me.mioclient.mode.client.SearchMode
        public void runWithShaderColor(java.lang.Runnable runnable) {
            // 26.2: RenderSystem.setShaderColor was removed with the new GPU backend.
            runnable.run();
        }
    },
    TRITANOPIA("Tritanopia") { // from class: me.mioclient.mode.client.SearchMode.Inner_2
        @Override // me.mioclient.mode.client.SearchMode
        public float[] getShaderColor() {
            return new float[]{1.0f, 0.7f, 0.0f, 1.0f};
        }

        @Override // me.mioclient.mode.client.SearchMode
        public void runWithShaderColor(java.lang.Runnable runnable) {
            // 26.2: RenderSystem.setShaderColor was removed with the new GPU backend.
            runnable.run();
        }
    };

    public final String name;

    SearchMode(String str) {
        this.name = str;
    }

    @Override // me.mioclient.api.NamedValue
    public String getName() {
        return this.name;
    }

    public void runWithShaderColor(java.lang.Runnable runnable) {
        runnable.run();
    }

    public float[] getShaderColor() {
        return new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    }
}
