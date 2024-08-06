import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import org.springframework.web.bind.annotation.*;
import ${mClass.getPackage().getFQName()}.logic.*;

@RestController()
@RequestMapping("/${mClass.getName().toLowerCase()}")
public class ${mClass.getName()} extends ${mClass.getName()}Base {

    public ${mClass.getName()}(${mClass.getName()}Logic logic) {
        super(logic);
    }

    // TODO: Remove Generated marker line an implement methods.
}
"""
