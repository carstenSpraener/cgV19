package de.spraener.nxtgen.aicb;

import java.util.function.Function;

@FunctionalInterface
public interface PostProcessor extends Function<String, String> {

    static PostProcessor defaultProcessor() {
        return s -> s;
    }

    static PostProcessor fromConfig(Config cfg) {
        if (cfg == null || cfg.getPostProcessorClass() == null || cfg.getPostProcessorClass().isEmpty()) {
            return defaultProcessor();
        }

        try {
            Class<?> clazz = Class.forName(cfg.getPostProcessorClass());
            return (PostProcessor) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load PostProcessor: " + cfg.getPostProcessorClass(), e);
        }
    }
}
