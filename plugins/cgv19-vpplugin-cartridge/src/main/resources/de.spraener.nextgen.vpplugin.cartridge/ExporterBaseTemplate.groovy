import de.spraener.nxtgen.NextGen
import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.oom.StereotypeHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
def oomType = StereotypeHelper.getStereotype(mClass, "ExporterBase").getTaggedValue("oomType")

NextGen.LOGGER.info("Running ExporterBaseTemplate.groovy on "+mClass.getFQName());

"""//${ProtectionStrategieDefaultImpl.GENERATED_LINE}
package ${mClass.getPackage().getFQName()};

import com.vp.plugin.model.IModelElement;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class ${mClass.getName()} implements de.spraener.nextgen.vpplugin.oom.Exporter {

    @Override
    public void export(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        pw.printf("%s${oomType} {  //Exported by ${mClass.getName()}\\n", indentation);
        exportProperties(exporter, pw,indentation+"  ", element);
        exportBody(exporter, pw,indentation+"  ", element);
        exportChilds(exporter, pw, indentation+"  ", element);
        pw.printf("%s}\\n", indentation);        
    }

    protected void exportChilds(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        for( IModelElement child : listChilds(element) ) {
            exporter.exportElement(pw, indentation, child);
        }
    }

    protected void exportProperties(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
        PropertiesExporter.exportProperties(exporter, pw, indentation, element);
    }
    
    protected void exportBody(OOMExporter exporter, PrintWriter pw, String indentation, IModelElement element) {
    }
    
    protected List<IModelElement> listChilds( IModelElement element) {
        List<IModelElement> result = new ArrayList<>();
        result.addAll(List.of(element.toChildArray()));
        return result;
    }
}
"""
