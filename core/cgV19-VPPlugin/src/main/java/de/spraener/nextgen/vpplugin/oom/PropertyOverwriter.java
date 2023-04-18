package de.spraener.nextgen.vpplugin.oom;

public class PropertyOverwriter {
    private String key;
    private String value;

    private PropertyOverwriter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static PropertyOverwriter overwrite(String key, String value) {
        return new PropertyOverwriter(key, value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
