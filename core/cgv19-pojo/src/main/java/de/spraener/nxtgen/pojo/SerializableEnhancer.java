package de.spraener.nxtgen.pojo;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeBlockSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaAspects;
import de.spraener.nxtgen.target.java.JavaSections;

import java.util.function.Consumer;

public class SerializableEnhancer implements Consumer<CodeTarget> {
    public static final String SERIALIZABLE = "serializable";
    private MClass mClass;

    public SerializableEnhancer(MClass mClass) {
        this.mClass = mClass;
    }

    @Override
    public void accept(CodeTarget target) {
        target.inContext(SERIALIZABLE, this.mClass, t -> {
            // Add the import to the import section
            t.getSection(JavaSections.IMPORTS)
                    .add(new SingleLineSnippet("import java.io.Serializable;"));

            // add an "implements" to the class declaration
            t.getSection(JavaSections.IMPLEMENTS)
                    .add(new CodeBlockSnippet("Serializable"));

            // add a serialVersionUID as a static fields
            t.getSection(JavaSections.CLASS_BLOCK_BEGIN)
                    .add(new CodeBlockSnippet("""
                                 private static final long serialVersionUID=1L;
                                 
                            """));
        });
    }
}
