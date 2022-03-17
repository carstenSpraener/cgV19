package de.spraener.nxtgen;

import de.spraener.nxtgen.model.ModelElement;

/**
 * Creates a CodeBlock for the given ModelElement with the given Template.
 *
 * @see CodeBlock
 */
public interface CodeGenerator {
    /**
     * Apply the requested Template to the model Element.
     *
     * @param element a instance of ModelElement to generate code for
     * @param templateName a Template to apply on the model element.
     *
     * @return a not null instance of CodeBlock the can write the code.
     */
    CodeBlock resolve(ModelElement element, String templateName);
}
