package entity

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes
import de.spraener.nxtgen.ProtectionStrategie


def pkgName = modelElement.package.name;
def cName = modelElement.name;
def keyType = modelElement.getTaggedValue(RESTStereotypes.REPOSITORY.name, 'keyType');
def dataType = modelElement.getTaggedValue(RESTStereotypes.REPOSITORY.name, 'dataType');

return """// ${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface ${cName} extends CrudRepository<${dataType},${keyType}> {
}
"""
