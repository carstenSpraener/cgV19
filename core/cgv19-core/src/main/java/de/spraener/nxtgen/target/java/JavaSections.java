package de.spraener.nxtgen.target.java;

import de.spraener.nxtgen.target.*;

public enum JavaSections {
    HEADER,
    IMPORTS,
    CLASS_DECLARATION,
    ATTRIBUTE_DECLARATION,
    METHODS,
    FOOTER;

    public static CodeTarget createJavaCodeTarget(String... preambleLines) {
        CodeTarget codeTarget = new CodeTarget();
        codeTarget.addCodeSection(HEADER, new SimpleCodeSection());
        codeTarget.addCodeSection(IMPORTS, new UniqueLineSection());
        codeTarget.addCodeSection(CLASS_DECLARATION, new SimpleCodeSection());
        codeTarget.addCodeSection(ATTRIBUTE_DECLARATION, new SimpleCodeSection());
        codeTarget.addCodeSection(METHODS, new SimpleCodeSection());
        codeTarget.addCodeSection(FOOTER, new SimpleCodeSection());
        if( preambleLines!=null ) {
            for( String preamble : preambleLines ) {
                codeTarget.getSection(HEADER).get()
                        .add(new SingleLineSnippet("preamble", preamble));
            }
        }
        return codeTarget;
    }
}
