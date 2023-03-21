package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.model.Model;

import javax.lang.model.element.Element;

public interface AnnotationModelBuilder {
    void handleElement(Element e);

    Model getModel();
}
