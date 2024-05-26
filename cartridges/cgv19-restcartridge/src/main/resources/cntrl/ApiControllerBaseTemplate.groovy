package cntrl


import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.JavaHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MOperation
import de.spraener.nxtgen.oom.model.MParameter

def toLogicName(me) {
    String name = me.name.replace("Controller", "Logic").replace("Base", "");
    if( !name.contains("Logic")) {
        name += "Logic"
    }
    return name;
}

def toRequestMethod(MOperation op) {
    def type = op.getType();
    def name = op.getName();
    def paramList = "";
    def requestPath = op.getTaggedValue(RESTStereotypes.REQUESTHANDLER.name, "path");
    if( requestPath==null ) {
        requestPath = "/"+op.getName();
    };
    def restMethod = op.getTaggedValue(RESTStereotypes.REQUESTHANDLER.name, "method");
    if( restMethod == null ) {
        restMethod = "Get"
    } else {
        restMethod = "${restMethod.charAt(0)}${restMethod.substring(1).toLowerCase()}";
    }
    def delegateParamList = "";
    for(MParameter p : op.getParameters() ) {
        if( JavaHelper.isBaseType(p.getType()) ) {
            paramList += "@PathVariable() ${p.getType()} ${p.getName()}, ";
            if( !requestPath.endsWith("/") ) {
                requestPath+="/";
            }
            requestPath += "{${p.getName()}}";
            delegateParamList += "${p.getName()}, ";
        }
    }
    if( paramList.length()>2 ) {
        paramList = paramList.substring(0, paramList.length()-2);
        delegateParamList = delegateParamList.substring(0, delegateParamList.length()-2);
    }
    return """
    @${restMethod}Mapping(path = "${requestPath}")
    public ResponseEntity<${type}> ${name}(${paramList}) {
        return ResponseEntity.ok().body(this.logic.${name}(${delegateParamList}));
    }
""";
}

def requestMethodsDelegate(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for( MOperation op : mc.getOperations() ) {
        if( StereotypeHelper.hasStereotype(op, RESTStereotypes.REQUESTHANDLER.getName()) ) {
            sb.append(toRequestMethod(op));
        }
    }
    return sb.toString();

}
def pkgName = ((MClass)modelElement).getPackage().getFQName()
def cName = modelElement.name;
def logicName = toLogicName(modelElement);
def orgClass = GeneratorGapTransformation.getOriginalClass(modelElement);

return """// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import ${pkgName}.logic.${logicName};

public abstract class ${cName} {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(${orgClass.getName()}.class.getName());
    private ${logicName} logic;

    public ${cName}(${logicName} logic) {
        this.logic = logic;
    }
    
    @GetMapping(path = "/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("pong");
    }
${requestMethodsDelegate(orgClass)}
}
"""
