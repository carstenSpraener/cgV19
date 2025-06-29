package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.model.impl.ModelElementImpl;

import javax.lang.model.element.Element;

public class APModelElementImpl extends ModelElementImpl {
    private Element astElement;

    public APModelElementImpl(Element astElement) {
        super();
        this.astElement = astElement;
    }

    public Element getAstElement() {
        return astElement;
    }

    public void setAstElement(Element astElement) {
        this.astElement = astElement;
    }
}
