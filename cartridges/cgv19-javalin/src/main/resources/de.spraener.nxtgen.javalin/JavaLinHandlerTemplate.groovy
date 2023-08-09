import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.javalin.JavaLinStereotypes
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MOperation
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();


String generateHandlerMethod(MClass mClass) {
    MOperation op = mClass.getOperations().stream().filter( {
        op -> StereotypeHelper.hasStereotype(op, JavaLinStereotypes.REQUESTHANDLER.name)
    }).findFirst().orElse(null);
    if( op == null ) {
        return;
    }
    return """
    public void ${op.getName()}() {
        // TODO: Get the Context with getCtx(), read request data from the request and send the result
        getCtx().result("You called ${op.getName()}()."); 
    }
"""
}

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import io.javalin.http.*;

public class ${mClass.getName()} extends ${mClass.getName()}Base {

    public ${mClass.getName()}(Context ctx) {
        super(ctx);
    }
    
${generateHandlerMethod(mClass)}

}
"""
