package de.spraener.nxtgen.ap;

import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.impl.StereotypeImpl;
import de.spraener.nxtgen.oom.model.MAbstractModelElement;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;
import java.util.Map;

/**
 * Maps Java annotations that are meta-annotated with
 * {@link de.spraener.nxtgen.ap.metameta.Stereotype} to Stereotypes on a model
 * element. The annotation's simple name becomes the stereotype name, its
 * attributes become TaggedValues.
 */
public class StereotypeMapper {

    private static final String STEREOTYPE_ANNOTATION =
            de.spraener.nxtgen.ap.metameta.Stereotype.class.getCanonicalName();

    public static void apply(Element source, MAbstractModelElement target) {
        for (AnnotationMirror am : source.getAnnotationMirrors()) {
            if (!(am.getAnnotationType() instanceof DeclaredType dt)) {
                continue;
            }
            if (!(dt.asElement() instanceof TypeElement annotationType)) {
                continue;
            }
            if (!isStereotypeAnnotation(annotationType)) {
                continue;
            }

            Stereotype st = new StereotypeImpl(annotationType.getSimpleName().toString());
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                st.setTaggedValue(entry.getKey().getSimpleName().toString(), toValueString(entry.getValue()));
            }
            target.addStereotypes(st);
        }
    }

    public static boolean isStereotypeAnnotation(TypeElement annotationType) {
        for (AnnotationMirror meta : annotationType.getAnnotationMirrors()) {
            if (!(meta.getAnnotationType() instanceof DeclaredType dt)) {
                continue;
            }
            if (!(dt.asElement() instanceof TypeElement metaType)) {
                continue;
            }
            if (STEREOTYPE_ANNOTATION.equals(metaType.getQualifiedName().toString())) {
                return true;
            }
        }
        return false;
    }

    private static String toValueString(AnnotationValue value) {
        Object v = value.getValue();
        if (v == null) {
            return "";
        }
        if (v instanceof List<?> values) {
            StringBuilder sb = new StringBuilder();
            for (Object item : values) {
                if (!sb.isEmpty()) {
                    sb.append(", ");
                }
                sb.append(toValueString((AnnotationValue) item));
            }
            return sb.toString();
        }
        // String, Number, Boolean, Character; Class-Literale liefern den FQCN
        return v.toString();
    }
}
