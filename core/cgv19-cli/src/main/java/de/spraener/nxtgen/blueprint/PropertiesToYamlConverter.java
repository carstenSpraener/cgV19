package de.spraener.nxtgen.blueprint;

import java.io.PrintWriter;
import java.util.*;

public class PropertiesToYamlConverter {
    private final Properties properties;

    public PropertiesToYamlConverter(Properties modelProperties) {
        this.properties = modelProperties;
    }

    public Map<String, Object> convert() {
        List<String> keysOrderd = new ArrayList<>();
        keysOrderd.addAll(this.properties.stringPropertyNames());
        Collections.sort(keysOrderd);
        Map<String, Object> yamlMap = new HashMap<>();
        for( String key: keysOrderd ) {
            setToYamlMap(key, properties.get(key), yamlMap);
        }
        return yamlMap;
    }

    private void setToYamlMap(String key, Object value, Map<String, Object> yamlMap) {
        int idx = key.indexOf('.');
        if( idx > -1 ) {
            String parent = key.substring(0, idx);
            Map<String, Object> subMap = (Map) yamlMap.get(parent);
            if( subMap==null ) {
                subMap = new TreeMap<>();
                yamlMap.put(parent, subMap);
            }
            setToYamlMap(key.substring(idx+1), value, subMap);
        } else {
            yamlMap.put(key, value);
        }
    }
}
