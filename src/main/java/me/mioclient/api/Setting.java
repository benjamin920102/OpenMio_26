package me.mioclient.api;

import me.mioclient.setting.EnumJsonConverter;

import me.mioclient.mode.setting.HoleSnapMode;


import java.util.Objects;
import java.util.function.Predicate;
import me.mioclient.setting.EnumJsonConverter;
import me.mioclient.mode.setting.HoleSnapMode;
import me.mioclient.module.Feature;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/api/Setting.class */
public abstract class Setting<T> extends Feature implements ValueProvider<T>, JsonConfigurable {
    public T defaultValue;
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;
    public boolean flag5;
    public boolean flag6;
    public Predicate<T> predicate;
    public String configName;
    public String string;
    public Runnable runnable;
    public T value;
    public T currentValue;
    public T minValue;
    public T maxValue;
    public T previousValue;
    public boolean hasBounds;
    public boolean isHovered;
    public HoleSnapMode holeSnapMode;

    public Setting(String str, T t, T t2, T t3, Predicate<T> predicate) {
        super(str);
        this.configName = getName();
        this.string = "";
        this.defaultValue = t;
        this.minValue = t2;
        this.maxValue = t3;
        this.predicate = predicate;
        this.value = t;
        this.currentValue = t;
        this.previousValue = t;
        this.hasBounds = true;
    }

    public Setting(String str, T t, Predicate<T> predicate) {
        super(str);
        this.configName = getName();
        this.string = "";
        this.defaultValue = t;
        this.predicate = predicate;
        this.value = t;
        this.currentValue = t;
        this.previousValue = t;
    }

    public Setting(String str, T t, T t2, T t3) {
        super(str);
        this.configName = getName();
        this.string = "";
        this.defaultValue = t;
        this.minValue = t2;
        this.maxValue = t3;
        this.value = t;
        this.currentValue = t;
        this.previousValue = t;
        this.hasBounds = true;
    }

    public Setting(String str, T t) {
        super(str);
        this.configName = getName();
        this.string = "";
        this.defaultValue = t;
        this.value = t;
        this.currentValue = t;
        this.previousValue = t;
    }

    public T getValue() {
        return this.value;
    }

    public T getCurrentValue() {
        return this.currentValue;
    }

    public void setCurrentValue(T t) {
        this.currentValue = t;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getMinValue() {
        return this.minValue;
    }

    public T getMaxValue() {
        return this.maxValue;
    }

    public boolean isAtMinimum() {
        return getMinValue().equals(getValue());
    }

    public boolean isAtMaximum() {
        return getMaxValue().equals(getValue());
    }

    @Override // me.mioclient.api.JsonConfigurable
    public String getConfigName() {
        return this.configName;
    }

    public void setConfigName(String str) {
        this.configName = str;
    }

    public String getCurrentValueString() {
        return EnumJsonConverter.getEnumDisplayName((Enum) this.currentValue);
    }

    public String getHoleSnapString() {
        return this.string;
    }

    public <T> String getTypeName(T t) {
        return t.getClass().getSimpleName();
    }

    public void reset() {
        setValue(this.defaultValue);
    }

    public abstract void loadFromString(String str);

    /* JADX WARN: Multi-variable type inference failed */
    public void setValue(T t) {
        setCurrentValue(t);
        if (this.hasBounds) {
            if (((Number) this.minValue).floatValue() > ((Number) t).floatValue()) {
                setCurrentValue(this.minValue);
            }
            if (((Number) this.maxValue).floatValue() < ((Number) t).floatValue()) {
                setCurrentValue(this.maxValue);
            }
        }
        this.value = this.currentValue;
        if (this.value != this.previousValue) {
            fireChangeCallback();
        }
        this.previousValue = this.value;
    }

    public void setDefaultValue(T t) {
        this.defaultValue = t;
    }

    public T getPreviousValue() {
        return this.previousValue;
    }

    public Setting<T> setVisible() {
        this.flag2 = true;
        return this;
    }

    public Setting<T> setDependent() {
        this.flag4 = true;
        setVisible();
        return this;
    }

    public Setting<T> setHoleSnapConfig(String str, HoleSnapMode holeSnapMode) {
        this.string = str;
        this.holeSnapMode = holeSnapMode;
        return this;
    }

    public void setChangeCallback(Runnable runnable) {
        this.runnable = runnable;
    }

    public void fireChangeCallback() {
        if (this.runnable == null) {
            return;
        }
        try {
            this.runnable.run();
        } catch (Throwable th) {
        }
    }

    public Setting<T> setDisabled() {
        this.flag3 = true;
        return this;
    }

    @SafeVarargs
    public final Setting<T> requiresSettings(Setting<Boolean>... settingArr) {
        addVisibilityPredicate(obj -> {
            for (Setting setting : settingArr) {
                if (setting.flag4 || setting.flag2) {
                    if (!setting.isShown()) {
                        return false;
                    }
                } else if (!((Boolean) setting.getValue()).booleanValue()) {
                    return false;
                }
            }
            return true;
        });
        return this;
    }

    public void addVisibilityPredicate(Predicate<T> predicate) {
        if (this.predicate == null) {
            this.predicate = predicate;
        } else {
            this.predicate = this.predicate.and(predicate);
        }
    }

    public void requireBaritone() {
        addVisibilityPredicate(obj -> {
            return ClientServices.obstaclePasserHelper.isBaritoneLoaded();
        });
    }

    public boolean isNumber() {
        return this.value instanceof Number;
    }

    public boolean isEnum() {
        return this.value instanceof Enum;
    }

    public boolean isString() {
        return this.value instanceof String;
    }

    public boolean isHoleSnapConfigured() {
        return isNumber() && !this.string.isEmpty() && this.holeSnapMode.isAtExtremum(this);
    }

    public boolean isVisible() {
        if (this.predicate == null) {
            return true;
        }
        return this.predicate.test(getValue());
    }

    public boolean isShown() {
        return this.flag && this.flag2;
    }

    public boolean hasBounds() {
        return this.hasBounds;
    }

    public void removeBounds() {
        this.hasBounds = false;
    }

    public boolean isDependent() {
        return this.flag4;
    }

    public void setHovered(boolean z) {
        this.isHovered = z;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    @Override // me.mioclient.module.Feature
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass() && super.equals(obj)) {
            return Objects.equals(this.configName, ((Setting) obj).configName);
        }
        return false;
    }

    @Override // me.mioclient.module.Feature
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.configName);
    }
}
