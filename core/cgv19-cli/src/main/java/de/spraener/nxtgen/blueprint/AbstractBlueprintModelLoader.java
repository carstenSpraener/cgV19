package de.spraener.nxtgen.blueprint;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelImpl;
import de.spraener.nxtgen.model.impl.StereotypeImpl;

import java.util.Properties;

public class AbstractBlueprintModelLoader {

    protected String requestValueFromUser(String key) {
        System.out.println("Please give value for '"+key+"': ");
        String line = System.console().readLine();
        return line;
    }

    protected Model toModel(Properties model, String name) {
        Model m = new ModelImpl();
        ModelElement me = m.createModelElement();
        me.setModel(m);
        ((ModelImpl)m).addModelElement(me);
        me.getStereotypes().add(new StereotypeImpl(name));
        for (String key : model.stringPropertyNames()) {
            me.setProperty(key, model.getProperty(key));
        }
        return m;
    }

}
