package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;

import java.util.Stack;

/**
 * <strong>Responsibility</strong>
 * The CodeTargetContext manages a Stack in a ThreadLocal variable to give access to the current
 * Aspect and ModelElement. With the use of the CodeTargetContext a Snippet can retrieve this
 * information automatically from the ThreadLocal. This makes the writing of ClassTarget-Creators
 * much more readable.
 * <p>
 * The CodeTargetContext is a Autocloseable and it is intended to be used in a try()-Header. Nesting
 * is possible. A nested ClassTargetContext will push its information on the stack and pop it
 * on close. The last close will empty the ThreadLocal variable.
 * </p><p>
 * Access to the CodeTargetContext is possible to the static method getActiveContext
 *</p>
 */
public class CodeTargetContext implements AutoCloseable {
    private static ThreadLocal<Stack<CodeTargetContext>> activeContext = new ThreadLocal<>();
    private static CodeTargetContext EMPTY_CONTEXT = new CodeTargetContext(null, null);

    private Object aspect;
    private ModelElement modelElement;

    /**
     * Activate a new ClassTargetContext with the given Information.
     *
     * @param aspect an Aspect describing the purpose.
     * @param modelElement a ModelElement that the CodeTarget creator is currently working on.
     */
    public CodeTargetContext(Object aspect, ModelElement modelElement) {
        this.aspect = aspect;
        this.modelElement = modelElement;
        getActiveContextStack().push(this);
    }

    /**
     * Retrieves the current active ClassTargetContext from the ThreadLocal Stack. It there is
     * no active ClassTargetContext it will return an empty CodeTargetContex.
     *
     * @return the currently active ClassTargetContext or null.
     */
    public static CodeTargetContext getActiveContext() {
        if (activeContext.get() == null || activeContext.get().isEmpty()) {
            return EMPTY_CONTEXT;
        }
        return getActiveContextStack().peek();
    }

    private static Stack<CodeTargetContext> getActiveContextStack() {
        Stack<CodeTargetContext> tlStack = activeContext.get();
        if (tlStack == null) {
            tlStack = new Stack<>();
            activeContext.set(tlStack);
        }
        return tlStack;
    }

    /**
     * Returns the aspect of this (currently active) CodeTargetContext.
     *
     * @return aspect or null
     */
    public Object getAspect() {
        return aspect;
    }

    /**
     * Returns the ModelElement of this (currently active) CodeTargetContext
     *
     * @return ModelElement or null
     */
    public ModelElement getModelElement() {
        return modelElement;
    }

    @Override
    public void close() {
        getActiveContextStack().pop();
        if (activeContext.get().isEmpty()) {
            activeContext.set(null);
        }
    }
}
