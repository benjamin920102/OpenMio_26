package me.mioclient.util.io;

import me.mioclient.util.text.StringTemplate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import me.mioclient.util.text.StringTemplate;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/FileIO.class */
public class FileIO {
    public static Path resolvePath(Path path, String... strArr) {
        if (path.toFile().exists()) {
            return path;
        }
        for (String str : strArr) {
            Path of = Path.of(new StringTemplate().arg(str).arg(path.toString()).format("\u0001\u0001"), new String[0]);
            if (of.toFile().exists()) {
                return of;
            }
        }
        return path;
    }

    public static void writeString(Path path, String str) {
        writeBytes(path, str.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeBytes(Path path, byte[] bArr) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path.toFile());
            fileOutputStream.write(bArr);
            fileOutputStream.close();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                }
            }
        } catch (Throwable th) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e2) {
                }
            }
            throw new java.lang.RuntimeException(th);
        }
    }

    public static byte[] readBytes(Path path) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path.toFile());
            byte[] readAllBytes = fileInputStream.readAllBytes();
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                }
            }
            return readAllBytes;
        } catch (Throwable th) {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e2) {
                }
            }
            throw new java.lang.RuntimeException(th);
        }
    }

    public static String readString(Path path) {
        return new String(readBytes(path), StandardCharsets.UTF_8);
    }
}
