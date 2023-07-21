package de.spraener.nxtgen.blueprint;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesToYamlConverterTest {

    @Test
    public void testPropsToYaml() {
        Properties props = new Properties();
        props.setProperty("p1.p2.p3", "value");
        props.setProperty("p1.p2.p4", "2.0");
        props.setProperty("name", "Name");
        Map<String, Object> yamlMap = new PropertiesToYamlConverter(props).convert();
        assertTrue(yamlMap.get("p1") instanceof Map<?,?>);
        assertTrue(((Map)yamlMap.get("p1")).get("p2") instanceof Map<?,?>);
        assertEquals("value", ((Map) ((Map)yamlMap.get("p1")).get("p2")).get("p3"));
        assertEquals("2.0", ((Map) ((Map)yamlMap.get("p1")).get("p2")).get("p4"));
        assertEquals("Name", yamlMap.get("name"));

        StringWriter writer = new StringWriter();
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        yaml.dump(yamlMap, writer);
        System.out.println(writer.toString());
    }

}
