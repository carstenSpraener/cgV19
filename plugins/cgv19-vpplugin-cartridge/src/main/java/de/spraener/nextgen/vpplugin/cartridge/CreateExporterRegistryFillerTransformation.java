package de.spraener.nextgen.vpplugin.cartridge;

import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.*;
import de.spraener.nxtgen.oom.StereotypeHelper;

import java.util.HashMap;
import java.util.Map;

public class CreateExporterRegistryFillerTransformation extends CreateExporterRegistryFillerTransformationBase {
    public static final String FILLER_CLASS_NAME="";
    private static MClass fillerClass = null;
    private static Map<MClass,Map<String,String>> fillerMapMap = new HashMap<>();

    @Override
    public void doTransformationIntern(MClass me) {
        MClass fillerClass = getFillerClass((OOModel)me.getModel(), me);
        Map<String, String> fillerMap = getFillerMap(fillerClass);
        String vpMetaType = getVPModelType(me);
        fillerMap.put(vpMetaType, me.getFQName());
    }

    public static MClass getFillerClass(OOModel model,MClass me) {
        if( fillerClass==null ) {
            fillerClass = me.getPackage().createMClass("ExporterRegistryFiller");
            StereotypeImpl sType = new StereotypeImpl("ExporterRegistryFiller");
            fillerClass.getStereotypes().add(sType);
            fillerClass.setModel(model);
            fillerMapMap.put(fillerClass,new HashMap<>());
        }
        return fillerClass;
    }

    public static Map<String, String> getFillerMap(MClass fillerClass) {
        return fillerMapMap.get(fillerClass);
    }

    private String getVPModelType(MClass me) {
        return StereotypeHelper.getStereotype(me, "Exporter").getTaggedValue("vpModelType");
    }

}
