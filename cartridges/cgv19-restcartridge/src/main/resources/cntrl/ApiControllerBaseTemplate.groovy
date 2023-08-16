package cntrl


import de.spraener.nxtgen.CGV19Config
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MOperation
import de.spraener.nxtgen.oom.model.MParameter

def persistenceAPI = CGV19Config.definitionOf("jakarta.persistence");

def toLogicName(me) {
    return me.name.replace("Controller", "Logic").replace("Base", "");
}

def getDataType(me) {
    def fqName = me.getTaggedValue(RESTStereotypes.RESTCONTROLLER.name, "dataType");
    if( fqName==null ) {
        return null;
    }
    return fqName.substring(fqName.lastIndexOf('.')+1);
}

def toRequestMethod(MOperation op) {
    def type = op.getType();
    def name = op.getName();
    def paramList = "";
    def requestPath = "/"+op.getName();
    def delegateParamList = "";
    for(MParameter p : op.getParameters() ) {
        paramList += "@PathVariable ${p.getType()} ${p.getName()}, ";
        requestPath += "/{${p.getName()}}";
        delegateParamList += "${p.getName()}, ";
    }
    if( paramList.length()>2 ) {
        paramList = paramList.substring(0, paramList.length()-2);
        delegateParamList = delegateParamList.substring(0, delegateParamList.length()-2);
    }
    return """
    @GetMapping(path = "${requestPath}")
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

return """// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

import ${persistenceAPI}.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

public abstract class ${cName}Base {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(${cName}.class.getName());


    public ${cName}Base() {
    }
    
    @GetMapping(path = "/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok().body("pong");
    }
${requestMethodsDelegate(modelElement)}
}
"""
