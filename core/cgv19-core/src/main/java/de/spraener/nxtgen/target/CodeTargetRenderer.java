package de.spraener.nxtgen.target;

/**
 * Interface for rendering a CodeTarget into a string representation.
 */
public interface CodeTargetRenderer {

    /**
     * Render the given CodeTarget into a string.
     *
     * @param codeTarget the CodeTarget to render
     * @return the rendered string representation
     */
    String render(CodeTarget codeTarget);
}
