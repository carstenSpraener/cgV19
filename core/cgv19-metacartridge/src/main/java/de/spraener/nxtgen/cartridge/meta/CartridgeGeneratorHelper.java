package de.spraener.nxtgen.cartridge.meta;

import de.spraener.nxtgen.oom.StereotypeHelper;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;

import java.util.*;
import java.util.stream.Collectors;

public class CartridgeGeneratorHelper {

    public static List<MClass> listTransformations(OOModel m) {
        Set<MClass> set = m.getModelElements().stream()
                .filter( me -> me instanceof MClass)
                .filter( me -> StereotypeHelper.hasStereotype(me, MetaCartridge.STEREOTYPE_TRANSFORMATION))
                .map( me -> (MClass)me)
                .collect(Collectors.toSet());
        List<MClass> result = new ArrayList<>();
        result.addAll(set);
        Collections.sort(result, Comparator.comparingInt(CartridgeGeneratorHelper::readTransformationsPriority));
        return result;
    }

    public static Map<String, List<MClass>> listCodeGeneratorByStereotype(OOModel m) {
        Set<MClass> set = m.getModelElements().stream()
                .filter( me -> me instanceof MClass)
                .filter( me -> StereotypeHelper.hasStereotype(me, MetaCartridge.STEREOTYPE_CODE_GENERATOR))
                .map( me -> (MClass)me)
                .collect(Collectors.toSet());
        Map<String, List<MClass>> result = new HashMap<>();
        for( MClass codeGenerator : set ) {
            String requiredSType = getRequiredStereotype(codeGenerator);
            List<MClass> sTypeList = result.get(requiredSType);
            if( sTypeList==null) {
                sTypeList = new ArrayList<>();
                result.put(requiredSType, sTypeList);
            }
            sTypeList.add(codeGenerator);
        }
        return result;
    }

    private static String getRequiredStereotype(MClass codeGenerator) {
        return codeGenerator.getTaggedValue(MetaCartridge.STEREOTYPE_CODE_GENERATOR, MetaCartridge.TV_REQUIRED_STEREOTYPE);
    }

    public static int readTransformationsPriority(MClass transformationMClass ) {
        String priority = transformationMClass.getTaggedValue(MetaCartridge.STEREOTYPE_TRANSFORMATION, MetaCartridge.TV_PRIORITY);
        return Integer.parseInt(priority);
    }
}
