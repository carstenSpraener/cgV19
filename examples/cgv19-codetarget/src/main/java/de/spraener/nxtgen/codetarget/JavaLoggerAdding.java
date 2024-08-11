package de.spraener.nxtgen.codetarget;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.java.JavaSections;

public class JavaLoggerAdding {

    public static CodeTarget addJavaLogging(CodeTarget clazzTarget , MClass mc) {
        clazzTarget.inContext("loggingContext", mc,
                // Add the import statement to the IMPORTS section of the java class
                ct -> ct.getSection(JavaSections.IMPORTS)
                        .add("logging", "import java.util.logging.Logger;\n"),

                // Add the static logger declaration to the begin of the class block
                ct -> ct.getSection(JavaSections.CLASS_BLOCK_BEGIN)
                        .add("logging", "    private static final Logger LOGGER = Logger.getLogger(" + mc.getName() + ".class.getName());\n")
        );
        return clazzTarget;
    }
}
