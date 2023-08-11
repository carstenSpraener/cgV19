import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.pojo.ClassFrameTargetCreator
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator
import de.spraener.nxtgen.target.CodeBlockSnippet
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.CodeTargetToCodeConverter
import de.spraener.nxtgen.target.SingleLineSnippet
import de.spraener.nxtgen.target.java.JavaSections

MClass mClass = this.getProperty("modelElement");

CodeTarget pojoTarget = new PoJoCodeTargetCreator(mClass).createPoJoTarget();
pojoTarget.inContext("cartridgeImpl", mClass, t -> {
    t.getSection(JavaSections.METHODS)
    .add(new CodeBlockSnippet(
"""
    @Override
    public String getName() {
        return NAME;
    }

"""
    ));
},
        t -> {
            t.getSection(JavaSections.CLASS_DECLARATION)
            .getFirstSnippetForAspect(ClassFrameTargetCreator.CLAZZ_FRAME)
            .insertBefore("@CGV19Cartridge(\"${mClass.getName()}\")\n")
        },
        t -> {
            t.getSection(JavaSections.CLASS_BLOCK_BEGIN)
            .add(new SingleLineSnippet("    public static final String NAME = \"${mClass.getName()}\";\n"))
        },
        t -> {
            t.getSection(JavaSections.IMPORTS)
            .add(new SingleLineSnippet("import de.spraener.nxtgen.annotations.CGV19Cartridge;"))
        }

);
return new CodeTargetToCodeConverter(pojoTarget).toString();
