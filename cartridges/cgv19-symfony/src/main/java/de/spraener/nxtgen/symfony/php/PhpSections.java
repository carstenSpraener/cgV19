package de.spraener.nxtgen.symfony.php;

import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SimpleCodeSection;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.UniqueLineSection;
import de.spraener.nxtgen.target.java.JavaImplementsCodeSection;

public enum PhpSections {
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

    public static CodeTarget createCodeTarget(String... preambleLines) {
        CodeTarget codeTarget = new CodeTarget();
        codeTarget.addCodeSection(HEADER, new SimpleCodeSection());
        codeTarget.addCodeSection(IMPORTS, new UniqueLineSection().add(new SingleLineSnippet("php", null, "")));
        codeTarget.addCodeSection(CLASS_DECLARATION, new SimpleCodeSection().add(new SingleLineSnippet("php", null, "")));
        codeTarget.addCodeSection(EXTENDS, new SimpleCodeSection());
        codeTarget.addCodeSection(IMPLEMENTS, new PhpImplementsSection());
        codeTarget.addCodeSection(CLASS_BLOCK_BEGIN, new SimpleCodeSection().add(new SingleLineSnippet("php", null, "{")));
        codeTarget.addCodeSection(ATTRIBUTE_DECLARATIONS, new SimpleCodeSection().add(new SingleLineSnippet("php", null, "")));
        codeTarget.addCodeSection(CONSTRUCTORS, new SimpleCodeSection().add(new SingleLineSnippet("php", null, "")));
        codeTarget.addCodeSection(METHODS, new SimpleCodeSection().add(new SingleLineSnippet("php", null, "")));
        codeTarget.addCodeSection(CLASS_BLOCK_ENDS, new SimpleCodeSection().add(new SingleLineSnippet("php", null, "}")));
        if( preambleLines!=null ) {
            for( String preamble : preambleLines ) {
                codeTarget.getSection(HEADER)
                        .add(new SingleLineSnippet("preamble", preamble));
            }
        }
        return codeTarget;
    }
}
