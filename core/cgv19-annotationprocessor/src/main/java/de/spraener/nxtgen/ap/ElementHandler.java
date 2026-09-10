package de.spraener.nxtgen.ap;

import de.spraener.nxtgen.oom.model.MAbstractModelElement;
import de.spraener.nxtgen.oom.model.OOModel;

import javax.lang.model.element.Element;

public interface ElementHandler {
    boolean canHandle(Element e);
    void handleElement(OOModel oom, MAbstractModelElement parent, Element e);
}
