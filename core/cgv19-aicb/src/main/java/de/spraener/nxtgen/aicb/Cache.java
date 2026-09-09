package de.spraener.nxtgen.aicb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Cache {

    private static final Path CACHE_ROOT = Paths.get(System.getProperty("user.home"), ".cgv19", "llm-cache");

    static {
        try {
            Files.createDirectories(CACHE_ROOT);
        } catch (IOException ignored) {
        }
    }

    public static Optional<String> get(String key) {
        Path file = CACHE_ROOT.resolve(key);
        if (Files.isRegularFile(file)) {
            try {
                return Optional.of(Files.readString(file, StandardCharsets.UTF_8));
            } catch (IOException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static void put(String key, String value) {
        Path file = CACHE_ROOT.resolve(key);
        try {
            Files.writeString(file, value, StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {
        }
    }

    public static void clear() {
        try {
            Files.walk(CACHE_ROOT)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
