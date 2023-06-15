import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator
import de.spraener.nxtgen.target.CodeBlockSnippet
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.CodeTargetToCodeConverter
import de.spraener.nxtgen.target.java.JavaSections

MClass mClass = this.getProperty("modelElement");

CodeTarget pojoTarget = new PoJoCodeTargetCreator(mClass).createPoJoTarget();
pojoTarget.inContext("cartridgeImpl", mClass, t -> {
    t.getSection(JavaSections.METHODS)
    .add(new CodeBlockSnippet(
"""
    @Override
    public String getName() {
        return "${mClass.getName()}";
    }

"""
    ));
});
return new CodeTargetToCodeConverter(pojoTarget).toString();
