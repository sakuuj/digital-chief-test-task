package by.sakuuj.digital.chief.testproj.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@UtilityClass
public class JsonExtractorUtils {

    public String fromFile(String classpathPath) {

        var jsonResource = new ClassPathResource(classpathPath);

        try (var reader = new BufferedReader(new InputStreamReader(jsonResource.getInputStream()))) {

            String line;
            var sb = new StringBuilder();
            do {
                line = reader.readLine();
                sb.append(line);

            } while (line != null);

            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
