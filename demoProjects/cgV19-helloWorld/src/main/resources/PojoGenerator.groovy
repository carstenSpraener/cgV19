import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.oom.model.JavaHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.MPackage;


MClass mClass = (MClass)modelElement;
MPackage mPkg = mClass.getPackage();
def pkgName = mPkg.name;
def cName = mClass.name;

String accessMethods(MClass mc) {
    StringBuilder sb = new StringBuilder();
    mc.attributes.forEach( {
        methodName = JavaHelper.toPorpertyName(it.name);
        sb.append(
"""
  public ${it.type} get${methodName}() {
    return ${it.name};
  }

  public void set${methodName}(${it.type} value) {
    this.${it.name} = value;
  }
"""
        );
    })
    return sb.toString()
}

String attributeDefinitions(MClass mc) {
    StringBuilder sb = new StringBuilder();
    mc.attributes.forEach( {
        sb.append(

"""
  private ${it.type} ${it.name};
"""
        );
    })
    return sb.toString();
}

return
"""// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

public class ${cName} {
  // generate Attribute definitions
  
  ${attributeDefinitions(mClass)}

  public ${cName}() {
  }

  ${accessMethods(mClass)}

}
"""
