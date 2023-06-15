package de.spraener.nxtgen.target.typescript;

import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SimpleCodeSection;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.UniqueLineSection;

public enum TypeScriptSections {
    HEADER,
    IMPORTS,
    DECLARATION,
    BODY_START,
    ATTRIBUTES,
    METHODS,
    BODY_END;

    public static CodeTarget createCodeTarget(String... preambleLines) {
        CodeTarget codeTarget = new CodeTarget();
        codeTarget.addCodeSection(HEADER, new SimpleCodeSection());
        codeTarget.addCodeSection(IMPORTS, new UniqueLineSection().add(new SingleLineSnippet("typescript", null, "")));
        codeTarget.addCodeSection(DECLARATION, new UniqueLineSection().add(new SingleLineSnippet("typescript", null, "")));
        codeTarget.addCodeSection(BODY_START, new UniqueLineSection().add(new SingleLineSnippet("typescript", null, " {")));
        codeTarget.addCodeSection(ATTRIBUTES, new UniqueLineSection().add(new SingleLineSnippet("typescript", null, "")));
        codeTarget.addCodeSection(METHODS, new UniqueLineSection().add(new SingleLineSnippet("typescript", null, "")));
        codeTarget.addCodeSection(BODY_END, new UniqueLineSection().add(new SingleLineSnippet("typescript", null, "}")));

        if( preambleLines!=null ) {
            for( String preamble : preambleLines ) {
                codeTarget.getSection(HEADER)
                        .add(new SingleLineSnippet("preamble", preamble));
            }
        }
        return codeTarget;
    }
}
