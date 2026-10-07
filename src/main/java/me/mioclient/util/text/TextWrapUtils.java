package me.mioclient.util.text;

import me.mioclient.render.font.ClientTextRenderer;

import java.util.ArrayList;
import java.util.List;

public class TextWrapUtils {
    public static List<String> wrapText(String str, double d) {
        ArrayList arrayList = new ArrayList();
        StringBuilder sb = new StringBuilder();
        char c = 65535;
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == 167 && i < str.length() - 1) {
                c = str.charAt(i + 1);
            }
            if (ClientTextRenderer.INSTANCE.getStringWidth(new StringTemplate().arg(charAt).arg(sb.toString()).format("\u0001\u0001")) < d) {
                sb.append(charAt);
            } else {
                arrayList.add(sb.toString());
                sb = new StringBuilder().append((char) 167).append(c).append(charAt);
            }
        }
        if (sb.length() > 0) {
            arrayList.add(sb.toString());
        }
        return arrayList;
    }

    public static String[] splitByNewlines(String str) {
        ArrayList arrayList = new ArrayList(1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '\n') {
                arrayList.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(charAt);
            }
        }
        arrayList.add(sb.toString());
        return (String[]) arrayList.toArray(new String[0]);
    }

    public static Character getLastFormattingCode(String str) {
        char c = ' ';
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == 167 && i + 1 < str.length()) {
                c = str.charAt(i + 1);
            }
        }
        return Character.valueOf(c);
    }
}
