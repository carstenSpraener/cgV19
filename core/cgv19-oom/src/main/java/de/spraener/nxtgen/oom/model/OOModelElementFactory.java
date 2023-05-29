package de.spraener.nxtgen.oom.model;

import de.spraener.nxtgen.ModelElementFactory;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;

public class OOModelElementFactory implements ModelElementFactory {
    @Override
    public ModelElement createModelElement(String modelElmentName) {
        switch( modelElmentName ) {
            case "root":
            case "mElement":
                return new ModelElementImpl();
            case "MPackage":
                return new MPackage();
            case "initNode":
            case "finalNode":
                return new MActivityNode();
            case "mControlFlow":
                return new MActivityControlFlow();
            case "mAction":
                return new MActivityAction();
            case "mDecision":
                return new MActivityDecision();
            case "incoming":
            case "outgoing":
                return new ModelElementImpl();
            default:
                try {
                    Class<? extends ModelElement> clazz = (Class<? extends ModelElement>)Class.forName("de.spraener.nxtgen.oom.model.M" + modelElmentName.substring(1));
                    return clazz.newInstance();
                } catch(Exception e ) {
                    NextGen.LOGGER.severe("Exception in ModelElementCreation: "+e.getMessage());
                    return new ModelElementImpl();
                }
        }
    }

    @Override
    public Model createModel() {
        return new OOModel();
    }
}
