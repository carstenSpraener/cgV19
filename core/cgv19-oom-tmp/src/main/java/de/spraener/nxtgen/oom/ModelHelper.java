package de.spraener.nxtgen.oom;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

import java.util.function.Function;
import java.util.stream.Stream;

public class ModelHelper {
    public static void cloneProperties(ModelElement from, ModelElement to) {
        for( String pName : from.getProperties().keySet() ) {
            to.setProperty(pName, from.getProperty(pName));
        }
    }

    public static Stream<ModelElement> streamModelElement(Model m, Function<ModelElement, Boolean> filter) {
        return m.getModelElements().stream()
                .filter(me -> filter.apply(me));
    }

    public static ModelElement findInStream(Stream<ModelElement> meStream, Function<ModelElement, Boolean> matcher ) {
        return meStream.filter(me -> matcher.apply(me)).findFirst().orElse(null);
    }
}
