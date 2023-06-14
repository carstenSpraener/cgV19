package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UniqueLineSectionTest {

    @Test
    void testDoublicatesRemoved() {
        UniqueLineSection uut = new UniqueLineSection();
        uut
                .add(new SingleLineSnippet("import", null, "import javax.persistence.*;"))
                .add(new SingleLineSnippet("import", null, "import java.util.List;"))
                .add(new SingleLineSnippet("import", null, "import javax.persistence.*;"))
                ;
        uut.add("collection-adding-aspect", null, "import java.util.Collection;\n");
        uut.add("another-collection-adding-aspect", null, "import java.util.Collection;");

        uut.getLastSnippetForAspect("import")
                .insertAfter("import", null, "import java.util.List;\n")
                .insertBefore("import", null, "import javax.persistence.*;\n");

        uut.withSnippet("import", "import javax.persistence.*;\n");

        CodeTarget target = new CodeTarget();
        target.addCodeSection("test", uut);
        assertEquals("""
                     import javax.persistence.*;
                     import java.util.List;
                     import java.util.Collection;
                     """,
                new CodeTargetToCodeConverter(target).toString());
    }
}
