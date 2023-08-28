package de.spraener.nxtgen.symfony.entities;

import de.spraener.nxtgen.cartridge.rest.transformations.EnsureEntityDefinitionsTransformation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.symfony.SymfonyModelMother;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PhpEntityGeneratorTest {

    @Test
    public void testEntityGeneratesToMany() {
        OOModel m = SymfonyModelMother.createDefaultModel();
        MClass entity = SymfonyModelMother.getTestEntity(m);
        new EnsureEntityDefinitionsTransformation().doTransformationIntern(entity);
        PhpEntityGenerator uut = new PhpEntityGenerator();
        String code = uut.resolve(entity, "").toCode();
        Assertions.assertThat(code)
                .contains("private $results;")
                .contains("#[ORM\\OneToMany(targetEntity: Result::class, mappedBy: 'null')]")
        ;
    }
}
