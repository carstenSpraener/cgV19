import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.javalin.JavaLinStereotypes
import de.spraener.nxtgen.javalin.MethodToRequestHandlerTransformation
import de.spraener.nxtgen.model.Stereotype
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

def String port = "7070"

String generateHandlerList(MClass mc) {
    StringBuilder sb = new StringBuilder();

    for( MClass handler : MethodToRequestHandlerTransformation.getRequestHandlerList(mc)) {
        generateHandlerCall(handler, sb);
    }
    return sb.toString();
}

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import io.javalin.Javalin;

public class ${mClass.getName()} {
    public static void main(String[] args) {
        var app = Javalin.create()
            .get("/ping", ctx -> ctx.result("pong!"))
${generateHandlerList(mClass)}
            .start(${port});
    }
}
"""

void generateHandlerCall(MClass mClass, StringBuilder sb) {
    Stereotype sType = StereotypeHelper.getStereotype(mClass, JavaLinStereotypes.REQUESTHANDLER.name)
    String method = sType.getTaggedValue("Method");
    if( method == null ) {
        method = "GET";
    }
    String path = sType.getTaggedValue("Path");
    if( path==null ) {
        path = "/";
    }
    String methodName = getMethodName(mClass);
    sb.append("""            .${method.toLowerCase()}("${path}", ctx -> new ${mClass.getName()}(ctx).${methodName}())
""")
}

// A Handler-MClass only has one single method
String getMethodName(MClass mClass) {
    return mClass.getOperations().get(0).getName();
}
