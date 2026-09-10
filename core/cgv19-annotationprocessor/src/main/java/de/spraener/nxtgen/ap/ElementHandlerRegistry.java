package de.spraener.nxtgen.ap;

import de.spraener.nxtgen.oom.model.OOModelRepository;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public class ElementHandlerRegistry {
    public List<ElementHandler> handlerList = new ArrayList<>();
    private static ElementHandlerRegistry myInstance = null;

    private ElementHandlerRegistry() {
        myInstance = this;
    }

    public static ElementHandlerRegistry instance() {
        return myInstance;
    }

    public ElementHandler getElementHander(Element child) {
        for(ElementHandler eh : handlerList) {
            if( eh.canHandle(child)) {
                return eh;
            }
        }
        return null;
    }

    public ElementHandlerRegistry register(ElementHandler eh) {
        handlerList.add(eh);
        return this;
    }
}
