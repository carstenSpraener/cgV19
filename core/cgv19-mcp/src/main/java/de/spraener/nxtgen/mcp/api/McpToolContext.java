package de.spraener.nxtgen.mcp.api;

import java.util.Map;

/**
 * Context passed to an @McpTool method invocation.
 * Provides access to tool arguments and the working directory.
 */
public class McpToolContext {
    private final Map<String, Object> arguments;
    private final String workingDir;

    public McpToolContext(Map<String, Object> arguments, String workingDir) {
        this.arguments = arguments;
        this.workingDir = workingDir;
    }

    /**
     * @return the tool arguments as a map.
     */
    public Map<String, Object> getArguments() {
        return arguments;
    }

    /**
     * @return the working directory for this invocation.
     */
    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * Gets a required argument value.
     *
     * @param key the argument key
     * @param <T> the expected type
     * @return the argument value
     * @throws IllegalArgumentException if the argument is missing
     */
    @SuppressWarnings("unchecked")
    public <T> T getRequired(String key) {
        Object value = arguments.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required argument: " + key);
        }
        return (T) value;
    }

    /**
     * Gets an optional argument value.
     *
     * @param key the argument key
     * @param defaultValue the default value if the argument is missing
     * @param <T> the expected type
     * @return the argument value or the default
     */
    @SuppressWarnings("unchecked")
    public <T> T getOptional(String key, T defaultValue) {
        Object value = arguments.get(key);
        return value != null ? (T) value : defaultValue;
    }
}
