package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.OOModel;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CreateResourceInfrastructureTest {

    private OOModel model;
    private ModelElement testME;
    private CreateResourceInfrastructure uut = new CreateResourceInfrastructure();

    @BeforeEach
    void setup() {
        model = OOModelMother.createDefaultModel();
        testME =OOModelMother.getEventResourceController(model);
    }

    @Test
    void testTransformation() throws Exception {
        // given: TestModel as defined in setup

        // when: Calling Transformation on a matched ModelElement
        uut.doTransformation(this.testME);

        // then: Transformation should create...
        assertThat(model.getClassesByStereotype(LaravelStereotypes.FILAMENTRESOURCE.getName()))
                .hasSize(1)
                .has(new Condition<>(mc ->mc.getName().equals("EventResource"), "Model"), Index.atIndex(0))
        ;

        assertThat(model.getClassesByStereotype(LaravelStereotypes.FILAMENTPAGE.getName()))
                .hasSize(3)
                .has(new Condition<>(mc ->mc.getName().equals("EditEvent"), "Edit Page"), Index.atIndex(0))
                .has(new Condition<>(mc ->mc.getName().equals("CreateEvent"), "Craete Page"), Index.atIndex(1))
                .has(new Condition<>(mc ->mc.getName().equals("ListEvents"), "List Page"), Index.atIndex(2))
        ;

    }
}
