import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.javalin.JavaLinStereotypes
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.model.impl.StereotypeImpl
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MOperation
import de.spraener.nxtgen.oom.model.OOModel
import de.spraener.nxtgen.pojo.ClassFrameTargetCreator
import de.spraener.nxtgen.pojo.PoJoCodeTargetCreator
import de.spraener.nxtgen.pojo.PoJoGenerator
import de.spraener.nxtgen.target.CodeBlockSnippet
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.CodeTargetToCodeConverter
import de.spraener.nxtgen.target.java.JavaSections

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

def CodeTarget handlerTarget = new PoJoCodeTargetCreator(mClass).createPoJoTarget();
String generateConstructor(MClass mc) {
    return """    
    public ${mc.getName()}( io.javalin.http.Context ctx) {
        super();
        this.ctx = ctx;
    }
"""
}

handlerTarget.inContext("requestHandler", mClass,
        ct -> ct.getSection(JavaSections.CLASS_DECLARATION)
                .getFirstSnippetForAspect(ClassFrameTargetCreator.CLAZZ_FRAME)
                .replace("public abstract class ${mClass.getName()} "),
        ct -> ct.getSection(JavaSections.CONSTRUCTORS).add("HandlerConstructor",generateConstructor(mClass)),
        ct -> ct.getSection(JavaSections.METHODS).add("HandlerMethod",generateHandlerMethod(mClass))
);

String generateHandlerMethod(MClass mClass) {
    MOperation op = GeneratorGapTransformation.getOriginalClass(mClass).getOperations().stream().filter( {
        op -> StereotypeHelper.hasStereotype(op, JavaLinStereotypes.REQUESTHANDLER.name)
    }).findFirst().orElse(null);
    if( op == null ) {
        return;
    }
    return """
    public abstract void ${op.getName()}();
"""
}

return new CodeTargetToCodeConverter(handlerTarget).toString();
