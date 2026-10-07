package me.mioclient.util.io;

import com.google.gson.JsonObject;
import java.util.Map;

public class JsonValidator {
    public static boolean hasAllFields(JsonObject jsonObject, String... strArr) {
        for (String str : strArr) {
            if (!jsonObject.has(str)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOnlyJsonObjects(JsonObject jsonObject) {
        for (Map.Entry entry : jsonObject.entrySet()) {
            if (!(entry.getValue() instanceof JsonObject) || !isOnlyJsonObjects((JsonObject) entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}
