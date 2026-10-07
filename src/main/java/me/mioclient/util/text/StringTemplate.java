package me.mioclient.util.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public final class StringTemplate {
    public static final char ch2 = 2;
    public final List<String> list = new ArrayList();
    public static final char ch = 1;
    public static final String string = String.valueOf(ch);

    public StringTemplate arg(boolean z) {
        prependArg(String.valueOf(z));
        return this;
    }

    public StringTemplate arg(byte b) {
        prependArg(String.valueOf((int) b));
        return this;
    }

    public StringTemplate arg(char c) {
        prependArg(String.valueOf(c));
        return this;
    }

    public StringTemplate arg(short s) {
        prependArg(String.valueOf((int) s));
        return this;
    }

    public StringTemplate arg(int i) {
        prependArg(String.valueOf(i));
        return this;
    }

    public StringTemplate arg(long j) {
        prependArg(String.valueOf(j));
        return this;
    }

    public StringTemplate arg(double d) {
        prependArg(String.valueOf(d));
        return this;
    }

    public StringTemplate arg(float f) {
        prependArg(String.valueOf(f));
        return this;
    }

    public StringTemplate argArray(boolean[] zArr) {
        prependArg(Arrays.toString(zArr));
        return this;
    }

    public StringTemplate argArray(byte[] bArr) {
        prependArg(Arrays.toString(bArr));
        return this;
    }

    public StringTemplate argArray(char[] cArr) {
        prependArg(Arrays.toString(cArr));
        return this;
    }

    public StringTemplate argArray(short[] sArr) {
        prependArg(Arrays.toString(sArr));
        return this;
    }

    public StringTemplate argArray(int[] iArr) {
        prependArg(Arrays.toString(iArr));
        return this;
    }

    public StringTemplate argArray(long[] jArr) {
        prependArg(Arrays.toString(jArr));
        return this;
    }

    public StringTemplate argArray(double[] dArr) {
        prependArg(Arrays.toString(dArr));
        return this;
    }

    public StringTemplate argArray(float[] fArr) {
        prependArg(Arrays.toString(fArr));
        return this;
    }

    public StringTemplate argArray(Object[] objArr) {
        prependArg(Arrays.toString(objArr));
        return this;
    }

    public StringTemplate arg(Object obj) {
        prependArg(String.valueOf(obj));
        return this;
    }

    public void prependArg(String str) {
        this.list.add(0, str);
    }

    public String format(String str) {
        return format(str, this.list);
    }

    public static String format(String str, List<String> list) {
        for (int i = 0; str.indexOf(1) >= 0 && i != list.size(); i++) {
            str = str.replaceFirst(string, Matcher.quoteReplacement(list.get(i)));
        }
        return str;
    }
}
