package me.mioclient.render.shape;

import me.mioclient.api.ValueProvider;
import me.mioclient.util.registry.CollectionRegistry;

import me.mioclient.util.registry.AbstractCollectionRegistry;

import me.mioclient.api.ClientAccessor;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.awt.Color;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import me.mioclient.event.events.render.MatrixStackEvent;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import me.mioclient.module.Feature;

public final class BlockHighlightRenderer extends AbstractCollectionRegistry<BlockHighlightRenderer.Record, List<BlockHighlightRenderer.Record>> implements ClientAccessor {
    public final Map<Record, Set<FadingBoxRenderer>> map;

    
    public static final class Record {
        public final me.mioclient.module.Feature feature;
        public final ValueProvider<Color> ValueProvider;
        public final ValueProvider<Color> helper_102;
        public final ValueProvider<Float> helper_103;
        public final ValueProvider<Float> helper_104;
        public final ValueProvider<Boolean> helper_105;
        public final ValueProvider<Boolean> helper_106;
        public final int num;

        public Record(me.mioclient.module.Feature feature, ValueProvider<Color> ValueProvider, ValueProvider<Color> helper_102, ValueProvider<Float> helper_103, ValueProvider<Float> helper_104, ValueProvider<Boolean> helper_105, ValueProvider<Boolean> helper_106, int i) {
            this.feature = feature;
            this.ValueProvider = ValueProvider;
            this.helper_102 = helper_102;
            this.helper_103 = helper_103;
            this.helper_104 = helper_104;
            this.helper_105 = helper_105;
            this.helper_106 = helper_106;
            this.num = i;
        }

        public Color getFillColor() {
            return this.ValueProvider.getValue();
        }

        public Color getOutlineColor() {
            return this.helper_102.getValue();
        }

        public float getStrokeWidth() {
            return this.helper_103.getValue().floatValue();
        }

        public float getFadeDuration() {
            if (this.helper_106.getValue().booleanValue()) {
                return this.helper_104.getValue().floatValue() * this.num;
            }
            return 0.0f;
        }

        public boolean isOverlayEnabled() {
            return this.helper_105.getValue().booleanValue();
        }

        public boolean isFadingEnabled() {
            return this.helper_106.getValue().booleanValue();
        }

        @Override // java.lang.Record
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return Objects.equals(this.feature, ((Record) obj).feature);
        }

        @Override // java.lang.Record
        public int hashCode() {
            return Objects.hash(this.feature);
        }

        public me.mioclient.module.Feature getFeature() {
            return this.feature;
        }

        public ValueProvider<Color> getFillColorProvider() {
            return this.ValueProvider;
        }

        public ValueProvider<Color> getOutlineColorProvider() {
            return this.helper_102;
        }

        public ValueProvider<Float> getStrokeWidthProvider() {
            return this.helper_103;
        }

        public ValueProvider<Float> getFadeDurationProvider() {
            return this.helper_104;
        }

        public ValueProvider<Boolean> getOverlayEnabledProvider() {
            return this.helper_105;
        }

        public ValueProvider<Boolean> getFadingProvider() {
            return this.helper_106;
        }

        public int getDurationMultiplier() {
            return this.num;
        }
    }

    public BlockHighlightRenderer() {
        super(new ArrayList());
        this.map = new HashMap();
        ClientAccessor.eventBus.register(this);
    }

    public void addBlockHighlight(me.mioclient.module.Feature feature, BlockPos blockPos) {
        if (blockPos == null) {
            return;
        }
        addHighlight(feature, new AABB(blockPos));
    }

    public void addHighlight(me.mioclient.module.Feature feature, AABB box) {
        if (box == null) {
            return;
        }
        Record object2405 = findFirstMatch(record -> {
            return record.getFeature().equals(feature);
        });
        if (object2405 == null) {
            throw new NoSuchElementException();
        }
        synchronized (this.map) {
            Set<FadingBoxRenderer> set = this.map.get(object2405);
            if (object2405.isOverlayEnabled()) {
                for (FadingBoxRenderer speedMineHelper_3 : set) {
                    if (box.equals(speedMineHelper_3.getBox())) {
                        speedMineHelper_3.startFadeOut();
                        return;
                    }
                }
            } else {
                set.clear();
            }
            FadingBoxRenderer speedMineHelper_32 = new FadingBoxRenderer();
            speedMineHelper_32.setBoundingBox(box);
            speedMineHelper_32.startFadeOut();
            set.add(speedMineHelper_32);
        }
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        synchronized (this.map) {
            for (Map.Entry<Record, Set<FadingBoxRenderer>> entry : this.map.entrySet()) {
                entry.getValue().removeIf(speedMineHelper_3 -> {
                    return speedMineHelper_3.getFadeProgress(((Record) entry.getKey()).getFadeDuration()) == 0.0f;
                });
            }
        }
    }

    @Listen
    public void onRenderWorld(MatrixStackEvent.Inner_3 inner_3) {
        synchronized (this.map) {
            for (Map.Entry<Record, Set<FadingBoxRenderer>> entry : this.map.entrySet()) {
                Record key = entry.getKey();
                for (FadingBoxRenderer speedMineHelper_3 : entry.getValue()) {
                    speedMineHelper_3.setStrokeWidth(key.getStrokeWidth());
                    speedMineHelper_3.renderFadingBox(inner_3.getMatrixStack(), key.getFillColor(), key.getOutlineColor(), key.getFadeDuration(), key.isFadingEnabled());
                }
            }
        }
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is2134, reason: merged with bridge method [inline-methods] */
    public boolean register(Record record) {
        this.map.put(record, new ObjectLinkedOpenHashSet());
        return getRegistry().add(record);
    }

    @Override // me.mioclient.util.registry.CollectionRegistry
    /* renamed from: is2135, reason: merged with bridge method [inline-methods] */
    public boolean unregister(Record record) {
        throw new UnsupportedOperationException();
    }
}
