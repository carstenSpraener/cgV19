package de.spraener.nxtgen.visitor;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SimpleCodeSection;

/**
 * Context passed to each {@code @ElementVisitor} method. It is a thin wrapper over the shared
 * {@link CodeTarget} plus the element currently being visited, so visitors can append code to a
 * named section without needing to know about the underlying target. All visitors in one walk
 * share the same {@link CodeTarget}.
 */
public class ElementContext {

    private final CodeTarget codeTarget;
    private final ModelElement element;

    public ElementContext(CodeTarget ct, ModelElement el) {
        this.codeTarget = ct;
        this.element    = el;
    }

    /** Append a snippet to the given section of the shared {@link CodeTarget} (fluent). */
    public ElementContext append(Object section, String code) {
        // CodeTarget.getSection returns null for a not-yet-existing section and
        // CodeTarget.append would NPE on it — so ensure the section exists first.
        if (section instanceof String path) {
            codeTarget.getOrCreate(path, () -> new SimpleCodeSection());
        } else if (codeTarget.getSection(section) == null) {
            codeTarget.addCodeSection(section, new SimpleCodeSection());
        }
        codeTarget.append(section, code);
        return this;
    }

    /** The shared code target for this walk. */
    public CodeTarget getCodeTarget() { return codeTarget; }

    /** The element currently being visited. */
    public ModelElement getElement()   { return element; }
}
