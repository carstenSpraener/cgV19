package de.spraener.nxtgen.model.impl;

import de.spraener.nxtgen.model.Model;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.Stereotype;
import de.spraener.nxtgen.model.TaggedValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ModelElementImplAddStereotypeTest {

    @Test
    void addsNewStereotypeWithValues() {
        ModelElementImpl el = new ModelElementImpl();

        ModelElement result = el.addStereotype("Entity", TaggedValue.of("tableName", "person"));

        assertThat(result).isSameAs(el);
        List<Stereotype> stereotypes = el.getStereotypes();
        assertThat(stereotypes).hasSize(1);
        Stereotype st = stereotypes.get(0);
        assertThat(st.getName()).isEqualTo("Entity");
        assertThat(st.getTaggedValue("tableName")).isEqualTo("person");
    }

    @Test
    void upsertsExistingStereotype() {
        ModelElementImpl el = new ModelElementImpl();

        el.addStereotype("Entity", TaggedValue.of("tableName", "person"));
        el.addStereotype("Entity", TaggedValue.of("tableName", "customer"));

        assertThat(el.getStereotypes()).hasSize(1);
        Stereotype st = el.getStereotypes().get(0);
        assertThat(st.getName()).isEqualTo("Entity");
        assertThat(st.getTaggedValue("tableName")).isEqualTo("customer");
    }

    @Test
    void mergeKeepsOtherKeys() {
        ModelElementImpl el = new ModelElementImpl();

        el.addStereotype("Entity", TaggedValue.of("a", "1"));
        el.addStereotype("Entity", TaggedValue.of("b", "2"));

        assertThat(el.getStereotypes()).hasSize(1);
        Stereotype st = el.getStereotypes().get(0);
        assertThat(st.getTaggedValue("a")).isEqualTo("1");
        assertThat(st.getTaggedValue("b")).isEqualTo("2");
    }

    @Test
    void addsEmptyStereotypeWithoutValues() {
        ModelElementImpl el = new ModelElementImpl();

        ModelElement result = el.addStereotype("Empty");

        assertThat(result).isSameAs(el);
        assertThat(el.getStereotypes()).hasSize(1);
        Stereotype st = el.getStereotypes().get(0);
        assertThat(st.getName()).isEqualTo("Empty");
        assertThat(st.getTaggedValues()).isEmpty();
    }

    @Test
    void isChainable() {
        ModelElementImpl el = new ModelElementImpl();

        assertThat(el.addStereotype("X")).isSameAs(el);
    }

    @Test
    void propertyRoundTrip() {
        ModelElementImpl el = new ModelElementImpl();

        el.setProperty("key", "value");

        assertThat(el.getProperty("key")).isEqualTo("value");
    }

    @Test
    void filterChildsFiltersByPredicate() {
        ModelElementImpl el = new ModelElementImpl();
        ModelElementImpl matching = new ModelElementImpl();
        matching.setName("match");
        ModelElementImpl notMatching = new ModelElementImpl();
        notMatching.setName("other");
        el.addChilds(matching);
        el.addChilds(notMatching);

        List<ModelElement> result = el.filterChilds(c -> "match".equals(c.getName())).toList();

        assertThat(result).containsExactly(matching);
    }

    @Test
    void getTaggedValueReturnsValueForKnownStereotype() {
        ModelElementImpl el = new ModelElementImpl();
        el.addStereotype("Entity", TaggedValue.of("tableName", "person"));

        assertThat(el.getTaggedValue("Entity", "tableName")).isEqualTo("person");
    }

    @Test
    void getTaggedValueReturnsNullForUnknownStereotype() {
        ModelElementImpl el = new ModelElementImpl();

        assertThat(el.getTaggedValue("Unknown", "tableName")).isNull();
    }

    @Test
    void modelRoundTrip() {
        ModelElementImpl el = new ModelElementImpl();
        Model model = mock(Model.class);

        el.setModel(model);

        assertThat(el.getModel()).isSameAs(model);
    }
}
