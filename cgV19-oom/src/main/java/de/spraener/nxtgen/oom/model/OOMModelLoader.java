package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.NxtGenRuntimeException;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class OOMModelLoader implements ModelLoader {

    @Override
    public boolean canHandle(String modelURI) {
        return modelURI.endsWith(".oom") || modelURI.startsWith("http");
    }

    @Override
    public Model loadModel(String modelURI) {
        try {
            Binding binding = new Binding();
            GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
            InputStreamReader modelReader = openModel(modelURI);
            Object value = shell.evaluate(modelReader);
            Model metaModel = (Model) value;
            OOModel oom =  new OOModel(metaModel);
            for( ModelElement e : oom.getModelElements() ) {
                e.setModel(oom);
            }
            return oom;
        } catch( Exception e ) {
            e.printStackTrace();
            throw new NxtGenRuntimeException(e);
        }
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
