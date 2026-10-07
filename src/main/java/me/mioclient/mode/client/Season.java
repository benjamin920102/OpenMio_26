package me.mioclient.mode.client;

import java.util.ArrayList;
import java.util.List;

public enum Season {
    WINTER(1, 2, 12),
    SPRING(3, 4, 5),
    SUMMER(6, 7, 8),
    AUTUMN(9, 10, 11);

    public final List<Integer> list = new ArrayList();

    Season(int... iArr) {
        for (int i : iArr) {
            this.list.add(Integer.valueOf(i));
        }
    }

    public boolean containsMonth(int i) {
        return this.list.contains(Integer.valueOf(i));
    }

    public static Season getSeasonForMonth(int i) {
        for (Season season : values()) {
            if (season.containsMonth(i)) {
                return season;
            }
        }
        throw new IllegalArgumentException("Unknown month");
    }
}
