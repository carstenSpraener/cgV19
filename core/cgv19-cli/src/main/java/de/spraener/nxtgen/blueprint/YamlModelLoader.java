package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.NxtGenRuntimeException;
import de.spraener.nxtgen.incubator.BlueprintCompiler;
import de.spraener.nxtgen.model.Model;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Responsibility:
 *
 * Load a model-yaml and conver it into a Model that can be used for Blueprints.
 */
public class YamlModelLoader extends AbstractBlueprintModelLoader {
    private final BlueprintCompiler bpc;
    private final String modelURI;
    private final String name;
    public YamlModelLoader(String name,BlueprintCompiler bpc, String modelURI) {
        this.bpc = bpc;
        this.modelURI = modelURI;
        this.name = name;
    }

    public Model loadModel(InputStream is) {
        Yaml yaml = new Yaml();
        Map<String,Object> yamlMap = yaml.load(is);
        if( yamlMap == null ) {
            yamlMap = new HashMap<>();
        }
        Properties modelProperties = toPropertiesMap(yamlMap);
        List<String> requiredValues = bpc.getRequiredValues();
        boolean modified = false;
        for (String key : requiredValues) {
            if (!modelProperties.containsKey(key)) {
                modelProperties.setProperty(key, requestValueFromUser(key));
                modified = true;
            }
        }
        if( modified && modelURI != null) {
            propsToYaml(modelURI, modelProperties);
        }

        return toModel(modelProperties, name);
    }

    private void propsToYaml(String modelURI, Properties modelProperties) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(modelURI))){
            Map<String, Object> yamlMap = new PropertiesToYamlConverter(modelProperties).convert();
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(options);
            yaml.dump(yamlMap, pw);
            pw.flush();
        } catch( IOException xc) {
            throw new NxtGenRuntimeException(xc);
        }
    }

    private Properties toPropertiesMap(Map<String, Object> yamlMap) {
        Properties props = new Properties();
        fillPropertyMap(props, "", yamlMap);
        return props;
    }

    private void fillPropertyMap(Properties props, String prefix, Map<String, Object> yamlMap) {
        for( String key: yamlMap.keySet() ) {
            Object value = yamlMap.get(key);
            if( value instanceof Map valueMap) {
                fillPropertyMap(props, prefix+key+".", valueMap);
            } else {
                props.setProperty(prefix+key, ""+value);
            }
        }
    }
}
