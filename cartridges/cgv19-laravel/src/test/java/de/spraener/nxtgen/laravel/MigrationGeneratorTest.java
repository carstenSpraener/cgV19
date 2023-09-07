package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import de.spraener.nxtgen.laravel.OOModelMother;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationGeneratorTest {
    private MigrationGenerator uut = new MigrationGenerator();

    private OOModel model;
    private ModelElement testME;
    
    @BeforeEach
    void setup() {
        // Create the required model elements with the OOModelBuilder
        model = OOModelMother.createDefaultModel();
        new CreateModelInfrastructureTransformation().doTransformationIntern(OOModelMother.getEvent(model));
        testME = model.findClassByName("my.test.model.app.models.events.EventMigration");
    }

    @Test
    void testCodeGenerator() throws Exception {
        // Generate the Code
        String code = uut.resolve(testME, "").toCode();

        // Check the generated code to contain...

        assertThat(code)
                .contains("$table->string('name')")
                .contains("$table->text('abstract')")
                .contains("$table->text('description')")
                .contains("$table->datetime('start_ts')")
                .contains("$table->datetime('end_ts')")
        ;
    }

    @Test
    void testToNRelation() throws Exception {
        new CreateModelInfrastructureTransformation().doTransformationIntern(OOModelMother.getParticipation(model));
        MClass participation = model.findClassByName("my.test.model.app.models.events.ParticipationMigration");
        String code = uut.resolve(participation, "").toCode();

        assertThat(code)
                .contains("$table->foreignIdFor(App\\Models\\Events\\Event::class)")
        ;
    }
}
