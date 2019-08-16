package de.spraener.nxtgen;

import de.spraener.nxtgen.model.ModelElement;

public interface CodeGenerator {
    CodeBlock resolve(ModelElement element, String templateName);
}
