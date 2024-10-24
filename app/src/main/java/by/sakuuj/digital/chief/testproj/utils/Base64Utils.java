package by.sakuuj.digital.chief.testproj.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class Base64Utils {

    public static String encode(String source) {
        byte[] sourceBytes = (source).getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(sourceBytes);
    }
}
