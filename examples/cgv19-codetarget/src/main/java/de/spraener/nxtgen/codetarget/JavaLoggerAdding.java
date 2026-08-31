package de.spraener.nxtgen.codetarget;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

public class JavaLoggerAdding {

    public static CodeTarget addJavaLogging(CodeTarget clazzTarget , MClass mc) {
        clazzTarget.forAspect("logging", mc,
                // Add the import statement to the IMPORTS section of the java class
                ct -> ct.append(JavaSections.IMPORTS, "import java.util.logging.Logger;\n"),

                // Add the static logger declaration to the begin of the class block
                ct -> ct.append(JavaSections.CLASS_BLOCK_BEGIN, "    private static final Logger LOGGER = Logger.getLogger(" + mc.getName() + ".class.getName());"),

                // Add a logging message to the end of the default constructor just before it's closed.
                ct->ct.beforeLastSnippetOfAspect(JavaSections.CONSTRUCTORS, "clazz-default-constructor.close",
                        "        LOGGER.trace(\"new Instance of "+mc.getName()+".\");")
        );
        return clazzTarget;
    }
}
