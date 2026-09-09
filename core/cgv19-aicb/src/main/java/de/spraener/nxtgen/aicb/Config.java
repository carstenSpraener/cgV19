package de.spraener.nxtgen.aicb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

    private String type = "langchain4j";
    private String url;
    private String model;
    private String postProcessorClass;

    public static Config myInstance = null;

    public static void loadTestConfig(String configJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        myInstance = mapper.readValue(configJson.getBytes(), Config.class);
    }

    public static void loadTestConfig(Config testConfig) throws IOException {
        myInstance = testConfig;
    }
        /**
         * Loads configuration from three locations with cascading override:
         * 1. ${HOME}/.cgv19/ai-config.json (base)
         * 2. Classpath /ai-config.json (overrides #1)
         * 3. ./.cgv19/ai-config.json (overrides #2)
         *
         * @return merged config or null if no config file exists anywhere
         */
    public static Config load() {
        if( myInstance == null ) {
            ObjectMapper mapper = new ObjectMapper();
            Config merged = null;

            // 1. HOME config (lowest priority)
            Path homeConfig = Path.of(System.getProperty("user.home"), ".cgv19", "ai-config.json");
            if (new File(homeConfig.toString()).exists()) {
                try {
                    merged = mapper.readValue(new File(homeConfig.toString()), Config.class);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load " + homeConfig, e);
                }
            }

            // 2. Classpath config (overrides HOME)
            InputStream classPathStream = Config.class.getResourceAsStream("/ai-config.json");
            if (classPathStream != null) {
                try {
                    Config classPathConfig = mapper.readValue(classPathStream, Config.class);
                    if (merged == null) {
                        merged = classPathConfig;
                    } else {
                        merged.overrideWith(classPathConfig);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load classpath ai-config.json", e);
                }
            }

            // 3. Current directory config (highest priority)
            Path currentDirConfig = Path.of(".cgv19", "ai-config.json");
            if (new File(currentDirConfig.toString()).exists()) {
                try {
                    Config currentDirCfg = mapper.readValue(new File(currentDirConfig.toString()), Config.class);
                    if (merged == null) {
                        merged = currentDirCfg;
                    } else {
                        merged.overrideWith(currentDirCfg);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load " + currentDirConfig, e);
                }
            }

            myInstance = merged;
        }
        return myInstance;
    }

    /**
     * Overrides non-null fields from the given config into this one.
     */
    void overrideWith(Config other) {
        if (other.type != null) this.type = other.type;
        if (other.url != null) this.url = other.url;
        if (other.model != null) this.model = other.model;
        if (other.postProcessorClass != null) this.postProcessorClass = other.postProcessorClass;
    }

    public String configHash() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(this);
            return sha256(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize config for hashing", e);
        }
    }

    static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPostProcessorClass() {
        return postProcessorClass;
    }

    public void setPostProcessorClass(String postProcessorClass) {
        this.postProcessorClass = postProcessorClass;
    }
}
