package de.csp.nxtgen;

import de.csp.nxtgen.model.ModelElement;

public interface CodeGenerator {
    CodeBlock resolve(ModelElement element, String templateName);
}
