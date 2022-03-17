package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.NxtGenRuntimeException;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.*;
import java.net.URL;

public class OOMModelLoader implements ModelLoader {

    @Override
    public boolean canHandle(String modelURI) {
        return modelURI.endsWith(".oom") || modelURI.startsWith("http");
    }

    @Override
    public Model loadModel(String modelURI) {
        try {
            InputStreamReader modelReader = openModel(modelURI);
            return parseModel(modelReader);
        } catch( Exception e ) {
            e.printStackTrace();
            throw new NxtGenRuntimeException(e);
        }
    }

    public Model loadFromString(String modelScript) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(modelScript.getBytes());
            InputStreamReader modelReader = new InputStreamReader(bais);
            return parseModel(modelReader);
        } catch( Exception e ) {
            e.printStackTrace();
            throw new NxtGenRuntimeException(e);
        }
    }

    private OOModel parseModel(InputStreamReader modelReader) {
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
        Object value = shell.evaluate(modelReader);
        Model metaModel = (Model) value;
        OOModel oom =  new OOModel(metaModel);
        for( ModelElement e : oom.getModelElements() ) {
            e.setModel(oom);
        }
        return oom;
    }

    private InputStreamReader openModel(String modelURI) throws IOException {
        File f = new File(modelURI);
        if( f.exists() ) {
            return new InputStreamReader(new FileInputStream(f));
        } else {
            URL url = new URL(modelURI);
            return new InputStreamReader(url.openStream());
        }
    }
}
