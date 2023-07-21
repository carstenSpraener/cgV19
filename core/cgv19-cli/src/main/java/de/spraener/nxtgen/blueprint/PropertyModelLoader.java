package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.incubator.BlueprintCompiler;
import de.spraener.nxtgen.model.Model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

public class PropertyModelLoader extends AbstractBlueprintModelLoader {
    private final BlueprintCompiler bpc;
    private final String modelURI;
    private final String name;

    public PropertyModelLoader(String name, BlueprintCompiler bpc, String modelURI) {
        this.bpc = bpc;
        this.modelURI = modelURI;
        this.name = name;
    }

    public Model loadModel(FileInputStream fileInputStream) throws IOException {
        Properties modelProperties = new Properties();
        modelProperties.load(fileInputStream);
        List<String> requiredValues = bpc.getRequiredValues();
        boolean modified = false;
        for (String key : requiredValues) {
            if (!modelProperties.containsKey(key)) {
                modelProperties.setProperty(key, requestValueFromUser(key));
                modified = true;
            }
        }
        if( modified ) {
            try (PrintWriter pw = new PrintWriter("./"+modelURI) ) {
                for( String propName : modelProperties.stringPropertyNames() ) {
                    pw.printf("%s = %s%n", propName, modelProperties.get(propName));
                }
                pw.flush();
            }
        }
        Model m = toModel(modelProperties, name);
        return m;
    }

}
