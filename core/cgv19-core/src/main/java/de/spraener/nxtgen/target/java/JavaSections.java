package de.spraener.nxtgen.target.java;

import de.spraener.nxtgen.target.*;

public enum JavaSections {
    HEADER,
    IMPORTS,
    CLASS_DECLARATION,
    EXTENDS,
    IMPLEMENTS,
    CLASS_BLOCK_BEGIN,
    CONSTRUCTORS,
    ATTRIBUTE_DECLARATIONS,
    METHODS,
    CLASS_BLOCK_ENDS;

    public static CodeTarget createJavaCodeTarget(String... preambleLines) {
        CodeTarget codeTarget = new CodeTarget();
        codeTarget.addCodeSection(HEADER, new SimpleCodeSection());
        codeTarget.addCodeSection(IMPORTS, new UniqueLineSection().add(new SingleLineSnippet("java", null, "")));
        codeTarget.addCodeSection(CLASS_DECLARATION, new SimpleCodeSection().add(new SingleLineSnippet("java", null, "")));
        codeTarget.addCodeSection(EXTENDS, new SimpleCodeSection());
        codeTarget.addCodeSection(IMPLEMENTS, new JavaImplementsCodeSection());
        codeTarget.addCodeSection(CLASS_BLOCK_BEGIN, new SimpleCodeSection().add(new SingleLineSnippet("java", null, "{")));
        codeTarget.addCodeSection(ATTRIBUTE_DECLARATIONS, new SimpleCodeSection().add(new SingleLineSnippet("java", null, "")));
        codeTarget.addCodeSection(CONSTRUCTORS, new SimpleCodeSection().add(new SingleLineSnippet("java", null, "")));
        codeTarget.addCodeSection(METHODS, new SimpleCodeSection().add(new SingleLineSnippet("java", null, "")));
        codeTarget.addCodeSection(CLASS_BLOCK_ENDS, new SimpleCodeSection().add(new SingleLineSnippet("java", null, "}")));
        if( preambleLines!=null ) {
            for( String preamble : preambleLines ) {
                codeTarget.getSection(HEADER)
                        .add(new SingleLineSnippet("preamble", preamble));
            }
        }
        return codeTarget;
    }
}
