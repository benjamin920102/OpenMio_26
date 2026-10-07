
package nick;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// 26.2 / Java 25: sun.misc.Unsafe string construction (allocateInstance +
// putByteVolatile/putObjectVolatile/objectFieldOffset on String.coder+value)
// is deprecated for removal. Replaced with pure-JDK charset decoding:
// coder 0 = LATIN_1, coder 1 = UTF_16BE. encode() was unused; implemented via
// getBytes so no Unsafe/VarHandle or --add-opens is needed.
public final class StringLookupTable {
    private static Map<Integer, String> STRINGS;

    public static String decode(byte[] data) {
        try {
            byte coder = data[0];
            byte[] v = new byte[data.length - 1];
            System.arraycopy(data, 1, v, 0, v.length);
            if (coder == 0) {
                return new String(v, StandardCharsets.ISO_8859_1);
            }
            return new String(v, StandardCharsets.UTF_16BE);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            return null;
        }
    }

    public static byte[] encode(String str) {
        byte[] v;
        byte coder;
        if (str.chars().allMatch(c -> c <= 0xFF)) {
            v = str.getBytes(StandardCharsets.ISO_8859_1);
            coder = 0;
        } else {
            v = str.getBytes(StandardCharsets.UTF_16BE);
            coder = 1;
        }
        byte[] r = new byte[v.length + 1];
        r[0] = coder;
        System.arraycopy(v, 0, r, 1, v.length);
        return r;
    }

    public static Map<Integer, String> parse(byte[] data) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            int size = dis.readInt();
            HashMap<Integer, String> m = new HashMap<>(size);
            for (int i = 0; i < size; ++i) {
                int type = dis.read();
                int key = dis.readInt();
                int len = dis.readInt();
                byte[] str = new byte[len];
                dis.readFully(str);
                if (type == 0) {
                    m.put(key, StringLookupTable.decode(str));
                    continue;
                }
                if (type == 1) {
                    m.put(key, new String(str, StandardCharsets.UTF_8));
                    continue;
                }
                throw new RuntimeException(String.valueOf(type));
            }
            if (dis.available() > 0) {
                throw new RuntimeException(new String(dis.readAllBytes()));
            }
            dis.close();
            return m;
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            return null;
        }
    }

    public static String getString(int key) {
        return Objects.requireNonNull(STRINGS.getOrDefault(key, null));
    }

    static {
        try (InputStream is = StringLookupTable.class.getClassLoader().getResourceAsStream("strings.bin")) {
            STRINGS = StringLookupTable.parse(Objects.requireNonNull(is).readAllBytes());
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
