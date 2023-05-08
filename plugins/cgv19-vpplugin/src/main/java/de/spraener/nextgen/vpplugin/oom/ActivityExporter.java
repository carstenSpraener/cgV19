package de.spraener.nextgen.vpplugin.oom;

import com.vp.plugin.model.IModelElement;

import java.util.List;

public class ActivityExporter extends ActivityExporterBase {
    @Override
    protected List<IModelElement> listChilds(IModelElement element) {
        List<IModelElement> childs = super.listChilds(element);
        childs.addAll(OOMExporter.findChildsByMetaType(element,"ControlFLow"));
        return childs;
    }
}
