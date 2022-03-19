package entity

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MOperation
import de.spraener.nxtgen.oom.model.MParameter


def toRepoName(me) {
    return me.name.replace("Logic", "Repository").replace("Base", "");
}

def toEntityName(me) {
    return me.name.replace("Logic", "").replace("Base", "");
}

def toRequestMethod(MOperation op) {
    def type = op.getType();
    def name = op.getName();
    def paramList = "";
    for(MParameter p : op.getParameters() ) {
        paramList += "${p.getType()} ${p.getName()}, ";
    }
    if( paramList.length()>2 ) {
        paramList = paramList.substring(0, paramList.length()-2);
    }
    return """
    public abstract ${type} ${name}(${paramList});
""";
}

def requestMethods(MClass c) {
    StringBuilder sb = new StringBuilder();
    for( MOperation op : c.getOperations() ) {
        if( StereotypeHelper.hasStereotye(op, RESTStereotypes.REQUEST.getName()) ) {
            sb.append(toRequestMethod(op));
        }
    }
    return sb.toString();
}

def pkgName = ((MClass)modelElement).getPackage().getFQName();
def cName = modelElement.name;
def repoName = toRepoName(modelElement);
def entityName = toEntityName(modelElement);
def basePkgName = pkgName.substring(0, pkgName.lastIndexOf('.'));

return """// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

import com.google.gson.Gson;
import ${basePkgName}.model.*;
import org.springframework.stereotype.Component;

@Component
public abstract class ${cName} {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(${cName}.class.getName());

    protected ${repoName} repository;
    protected Gson gson;
    
    public ${cName}(${repoName} reppository, Gson gson){
        this.repository = reppository;
        this.gson = gson;
    }
       
    public ${entityName} fromJson(String valueJson) {
        return this.gson.fromJson(valueJson, ${entityName}.class);
    } 

    public String toJson(${entityName} value) {
        return this.gson.toJson(value);
    } 

    public ${entityName} save( ${entityName} value ) {
        return this.repository.save(value);
    }
    
    public ${entityName} update(${entityName} value) {
        ${entityName} valueDB = this.repository.findById(value.getId()).orElse(null);
        if( valueDB == null ) {
            throw new RuntimeException("no ${entityName} with id "+value.getId()+" found.");
        }
        return this.repository.save(value);
    }

    public ${entityName} delete(Long id) {
        this.repository.deleteById(id);
        return null;
    }

    public ${entityName} findOrCreate(Long id) {
        return repository.findById(id).orElseGet(() -> new ${entityName}());
    }

    public Iterable<${entityName}> findFirstPage() {
        return repository.findAll();
    }

${requestMethods(modelElement)}
}
"""
