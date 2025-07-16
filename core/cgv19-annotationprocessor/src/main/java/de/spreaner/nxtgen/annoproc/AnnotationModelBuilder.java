package de.spreaner.nxtgen.annoproc;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.model.Model;

import javax.lang.model.element.Element;
import java.util.function.Function;

public interface AnnotationModelBuilder {
    /**
     * Adds an optional modelAdapter. It takes the model created by this AnnotationModelBuilder and
     * previously registered ModelAdapters and transforms it into another model instance.
     * This can be used to transform the original
     * standard model created by the builder into an OOModel used by many cartridges.
     * <p>
     * Multiple ModelAdapters can be chained where the next ModelAdapter get the result of the
     * previous registered ModelAdapter to build a transformation chain.
     * <p>
     * The cgv19-oom module contains ModelAdapter implementation to do this task.
     *
     * @param modelAdapter
     * @return a transformed model to use for cartridges.
     */
    AnnotationModelBuilder withModelAdapter(Function<Object, Object> modelAdapter);

    void handleElement(Element e);

    Model getModel();
}
