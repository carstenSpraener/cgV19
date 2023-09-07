package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.Transformation;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.OOModel;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CreateModelInfrastructureTransformationTest {

    private OOModel model;
    private ModelElement testME;
    private CreateModelInfrastructureTransformation uut = new CreateModelInfrastructureTransformation();

    @BeforeEach
    void setup() {
        model = OOModelMother.createDefaultModel();
        testME = OOModelMother.getEvent(model);
    }

    @Test
    void testTransformation() throws Exception {
        // given: TestModel as defined in setup

        // when: Calling Transformation on a matched ModelElement
        uut.doTransformation(this.testME);

        // then: Transformation should create...
        assertThat(model.getClassesByStereotype(LaravelStereotypes.LARAVELMODEL.getName()))
                .hasSize(1)
                .has(new Condition<>(mc ->mc.getName().equals("Event"), "Model"), Index.atIndex(0))
        ;
        assertThat(model.getClassesByStereotype(LaravelStereotypes.LARAVELFACTORY.getName()))
                .hasSize(1)
                .has(new Condition<>(mc ->mc.getName().equals("EventFactory"), "Factory"), Index.atIndex(0))
        ;
        assertThat(model.getClassesByStereotype(LaravelStereotypes.LARAVELMIGRATION.getName()))
                .hasSize(1)
                .has(new Condition<>(mc ->mc.getName().equals("EventMigration"), "Migration"), Index.atIndex(0))
        ;
        assertThat(model.getClassesByStereotype(LaravelStereotypes.LARAVELSEEDER.getName()))
                .hasSize(1)
                .has(new Condition<>(mc ->mc.getName().equals("EventSeeder"), "Seeder"), Index.atIndex(0))
        ;
    }
}
