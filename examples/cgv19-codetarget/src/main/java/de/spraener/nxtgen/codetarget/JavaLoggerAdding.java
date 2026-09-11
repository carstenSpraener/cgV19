package de.spraener.nxtgen.codetarget;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeSection;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.StandardSections;
import de.spraener.nxtgen.target.java.JavaSections;

import java.util.ArrayList;
import java.util.List;

public class JavaLoggerAdding {

    public static CodeTarget addJavaLogging(CodeTarget clazzTarget , MClass mc) {
        clazzTarget.forAspect("logging", mc,
                // Add the import statement to the IMPORTS section of the java class
                ct -> ct.append(JavaSections.IMPORTS, "import java.util.logging.Logger;\n"),

                // Add the static logger declaration to the begin of the class block
                ct -> ct.append(JavaSections.CLASS_BLOCK_BEGIN, "    private static final Logger LOGGER = Logger.getLogger(" + mc.getName() + ".class.getName());"),

                // Add a logging message to the end of the default constructor just before it's closed.
                ct->ct.beforeLastSnippetOfAspect(JavaSections.CONSTRUCTORS, "clazz-default-constructor.close",
                        "        LOGGER.trace(\"new Instance of "+mc.getName()+".\");"),

                // Hierarchical sections: a new top-level section with the standard operation
                // scopes (BEFORE/IN/AFTER_OPERATION), inserted before the closing class brace.
                ct -> {
                    List<CodeSection> toc = new ArrayList<>(ct.getSectionsOrdered());
                    int index = 0;
                    for (CodeSection section : toc) {
                        if (JavaSections.CLASS_BLOCK_ENDS.name().equals(section.getId())) break;
                        index++;
                    }
                    ct.addCodeSectionAt("LOGGING_METHODS", StandardSections.operation().get(), index);
                },

                // Address the nested scopes via Unix-style path syntax. The renderer indents
                // scope content automatically, so the snippets carry no manual indentation.
                ct -> ct.append(JavaSections.IMPORTS, "import java.util.logging.Level;\n"),
                ct -> ct.append("LOGGING_METHODS/BEFORE_OPERATION", "// --- debug logging support ---\n"),
                ct -> ct.append("LOGGING_METHODS/IN_OPERATION", "public void logDebug(String message) {\nLOGGER.log(Level.FINE, \"DEBUG: \" + message);\n}\n")
        );
        return clazzTarget;
    }
}
