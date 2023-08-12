package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.ModelElementFactory;
import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.NxtGenRuntimeException;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.*;
import java.net.URL;

public class OOMModelLoader implements ModelLoader {

    private ModelElementFactory elementFactory = new OOModelElementFactory();

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

    @Override
    public ModelElementFactory getModelElementFactory() {
        return this.elementFactory;
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
        return (OOModel)value;
    }

    private InputStreamReader openModel(String modelURI) throws IOException {
        File f = new File(modelURI);
        if( f.exists()) {
            return new InputStreamReader(new FileInputStream(f));
        } else if( getClass().getResourceAsStream(modelURI)!=null ) {
            return new InputStreamReader(getClass().getResourceAsStream(modelURI));
        } else {
            try {
                URL url = new URL(modelURI);
                return new InputStreamReader(url.openStream());
            } catch( IOException xc ) {
                String fileNameFromURI = toFileName(modelURI);
                NextGen.LOGGER.info(String.format("Error loading model via URL '%s'. Try to load file '%s'.", modelURI, fileNameFromURI));
                f = new File(fileNameFromURI);
                if( f.exists() ) {
                    return new InputStreamReader(new FileInputStream(f));
                } else {
                    NextGen.LOGGER.info(String.format("Can not load file '%s'. Giving up...", modelURI, fileNameFromURI));
                    throw new IllegalArgumentException("No such model.");
                }
            }
        }
    }

    private String toFileName(String modelURI) {
        return modelURI.substring(modelURI.lastIndexOf('/')+1)+".oom";
    }
}
