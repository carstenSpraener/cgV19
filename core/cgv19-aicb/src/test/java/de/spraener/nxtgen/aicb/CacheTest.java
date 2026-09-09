package de.spraener.nxtgen.aicb;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CacheTest {

    @AfterEach
    void cleanup() {
        Cache.clear();
    }

    @Test
    void cacheMiss_returnsEmpty() {
        Optional<String> result = Cache.get("non-existent-key");
        assertThat(result).isEmpty();
    }

    @Test
    void cacheHit_returnsStoredValue() {
        Cache.put("test-key", "cached value");
        Optional<String> result = Cache.get("test-key");
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("cached value");
    }

    @Test
    void cacheIsolation_differentKeysAreIndependent() {
        Cache.put("key-a", "value A");
        Cache.put("key-b", "value B");

        assertThat(Cache.get("key-a").orElse(null)).isEqualTo("value A");
        assertThat(Cache.get("key-b").orElse(null)).isEqualTo("value B");
    }

    @Test
    void cacheOverwrite_replacesOldValue() {
        Cache.put("key", "old value");
        assertThat(Cache.get("key").orElse(null)).isEqualTo("old value");

        Cache.put("key", "new value");
        assertThat(Cache.get("key").orElse(null)).isEqualTo("new value");
    }

    @Test
    void cacheClear_removesAllEntries() {
        Cache.put("key1", "value1");
        Cache.put("key2", "value2");

        Cache.clear();

        assertThat(Cache.get("key1")).isEmpty();
        assertThat(Cache.get("key2")).isEmpty();
    }
}
