package de.spraener.nxtgen;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;

public interface ModelElementFactory {
    ModelElement createModelElement(String modelElmentName);
    Model createModel();
}
