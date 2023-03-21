import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.model.ModelElement
import de.spraener.nxtgen.model.impl.ModelElementImpl

def me = (ModelElementImpl) modelElement
def pkgName = me.parent.name;
def cName = modelElement.name

String generateWither(ModelElement me) {
    StringBuilder sb = new StringBuilder()
    me.getChilds().stream().filter(c -> c.getMetaType().equals("mAttribute")).forEach( attr -> {
        String wName = attr.name.substring(0,1).toUpperCase()+attr.name.substring(1)
        String type = attr.getProperty("type")
        sb.append("""
    public ${me.name}Builder with${wName}( ${type} value) {
        payload.set${wName}(value);
        return this;
    }
""")
    })
    return sb.toString()
}
return """// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

import de.spraener.nxtgen.hello.HelloPayload;

public class ${cName}Builder {
    private ${cName} payload = new ${cName}();

    public ${cName} build() {
      return payload;
    }
    
${generateWither(me)}
}
"""
