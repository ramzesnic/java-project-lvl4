package hexlet.code.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public static String formatDate(String pattern, Instant date) {
        return DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .format(date);
    }

    public static String getFileContent(String path) throws IOException {
        Path resPath = Paths.get(path);
        return Files.readString(resPath);
    }

}
