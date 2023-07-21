package de.spraener.nxtgen.oom.yaml;

import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertNotNull;

public class YamlLoaderExperiment {

    @Test
    public void testYamlModelLoading() {
        LoaderOptions options = new LoaderOptions();
        Yaml yaml = new Yaml(new ModelConstructor(options));
        OOModel oom = (OOModel) yaml.load("""
            !model:
                package:
                    name: de
            """);
        assertNotNull(oom);
    }
}
