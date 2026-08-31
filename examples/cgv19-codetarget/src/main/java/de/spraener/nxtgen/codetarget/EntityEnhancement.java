package de.spraener.nxtgen.codetarget;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.target.CodeTarget;
import de.spraener.nxtgen.target.SingleLineSnippet;
import de.spraener.nxtgen.target.java.JavaSections;

public class EntityEnhancement {
    public static de.spraener.nxtgen.target.CodeTarget addEntityEnhancement(CodeTarget clazzTarget , MClass mc) {
        String tableName = mc.getName().toLowerCase();
        clazzTarget.forAspect("entities", mc,
                // Add the import statement to the IMPORTS section of the java class
                ct -> ct.getSection(JavaSections.IMPORTS)
                        .add(new SingleLineSnippet("import javax.persistence.*;\n")),

                // Add the static logger declaration to the begin of the class block
                ct ->
                        ct.getSection(JavaSections.CLASS_DECLARATION)
                        .getFirstSnippetForAspect("clazz-frame")
                        .insertBefore(String.format("""
                                        @Entity 
                                        @Table("%s")
                                        """,tableName
                        ))

        );
        return clazzTarget;
    }
}
