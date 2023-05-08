package test;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.NxtGenRuntimeException;
import de.spraener.nxtgen.model.Model;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.InputStreamReader;

public class GroovyModelLoader implements ModelLoader {
    @Override
    public boolean canHandle(String modelURI) {
        return modelURI.endsWith(".groovy");
    }

    @Override
    public Model loadModel(String modelURI) {
        try {
            Binding binding = new Binding();
            binding.setVariable("foo", new Integer(2));
            GroovyShell shell = new GroovyShell(binding);

            Object value = shell.evaluate(new InputStreamReader(GroovyModelLoader.class.getResourceAsStream(modelURI)));
            return (Model) value;
        } catch( Exception e ) {
            throw new NxtGenRuntimeException(e);
        }
    }
}
