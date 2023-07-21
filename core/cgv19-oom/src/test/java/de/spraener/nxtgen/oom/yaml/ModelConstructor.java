package de.spraener.nxtgen.oom.yaml;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.logging.Logger;

public class ModelConstructor extends Constructor {
    private static final Logger LOGGER = Logger.getLogger(ModelConstructor.class.getName());

    public ModelConstructor(LoaderOptions loadingConfig) {
        super(loadingConfig);
        this.yamlConstructors.put(new Tag("!model"), new ConstructModel());
        this.yamlConstructors.put(new Tag("!package"), new ConstructPackage());
    }

    private class ConstructModel extends AbstractConstruct {
        public Object construct(Node node) {
            LOGGER.info("Constructing OOModel on node "+node);
            return new OOModel();
        }
    }

    private class ConstructPackage extends AbstractConstruct {
        public Object construct(Node node) {
            LOGGER.info("Constructing OOModel on node "+node);
            return new MPackage();
        }
    }
}
