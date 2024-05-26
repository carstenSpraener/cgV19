package de.spraener.nxtgen.target.java;

import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeSnippet;
import de.spraener.nxtgen.target.NonEmptyPrefixedListSection;
import de.spraener.nxtgen.target.UniqueLineSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * This code section evaluates to an empty string if it contains no snippets.
 * If it contains a snippet, it will add the keyword "implements" to the
 * code and a "," between to snippets code. It also removes double implements
 * snippets.
 * </p>
 * <p>
 * It is used to easily create a Java "implements"-List
 * </p>
 */
public class JavaImplementsCodeSection extends NonEmptyPrefixedListSection {
    public JavaImplementsCodeSection() {
        super("java", "implements ", ", ");
    }
}
