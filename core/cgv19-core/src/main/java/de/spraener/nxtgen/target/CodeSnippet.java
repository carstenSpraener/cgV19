package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.ArrayList;
import java.util.List;

/**
 * <h4>Responsibility</h4>
 * A CodeSnippet is a atomar piece of code that can be referenced by an aspect
 * and an optional ModelElement. It can evaluate itself into a StringBuilder.
 * It always has an "aspect" this snippet is needed for, and it can have a ModelElement
 * for which this Snippet is also needed.
 */
public abstract class CodeSnippet {
    /**
     * The aspect this snippet implements. For example JPA-Entity
     */
    private Object aspect;
    /**
     * The reason why this snippet is there. For Example the MAttribute that needs
     * this snippet. With this information a CodeTarget can be modified by
     * searching the snippet for aspect A and ModelElement E, and then
     * inserting a snippet before that snippet.
     */
    private ModelElement me;

    public CodeSnippet() {
        this(CodeTargetContext.getActiveContext().getAspect(), CodeTargetContext.getActiveContext().getModelElement());
    }
    /**
     * Create a snippet to implement the given aspect. The ModelELement is set to null.
     * @param aspect The aspect this snippet is needed for.
     */
    public CodeSnippet(Object aspect) {
        this(aspect, CodeTargetContext.getActiveContext().getModelElement());
    }

    /**
     * Create a Snippet to implement the given aspect for the given ModelElement.
     * For example the snippet implements the JPA-Aspect for a MAttribute.
     * @param aspect The aspect this snippet is needed for.
     * @param me The ModelElement this snippet is needed for.
     */
    public CodeSnippet(Object aspect, ModelElement me) {
        this.aspect = aspect;
        this.me = me;
    }

    public abstract void evaluate(StringBuilder sb);

    /**
     * Does this snippet match the given aspect and ModelElement? If the snippet
     * has no ModelElement it only matches requests with ModelElement parameter is null.
     * A null-ModelElement on a snippet does not match any ModelElement-Parameter.
     *
     * @param aspect The Aspect to retrieve snippets for
     * @param me the ModelElement to retrieve snippets for or null.
     *
     * @return TRUE if the snippet matches the given parameters
     */
    public boolean matches(Object aspect, ModelElement me) {
        if( this.aspect!=null && this.aspect.equals(aspect) ) {
            if( this.me==null && me==null ) {
                return true;
            } else {
                return this.me!=null && this.me.equals(me);
            }
        }
        return false;
    }

    public Object getAspect() {
        return aspect;
    }
}
