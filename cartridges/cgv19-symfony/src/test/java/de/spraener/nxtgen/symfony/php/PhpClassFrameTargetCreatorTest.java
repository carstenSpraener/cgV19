package de.spraener.nxtgen.symfony.php;

import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.symfony.SymfonyModelMother;
import de.spraener.nxtgen.target.CodeTargetCodeBlockAdapter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PhpClassFrameTargetCreatorTest {

    @Test
    void testPhpFrame() {
        OOModel model = SymfonyModelMother.createDefaultModel();
        MClass entity = SymfonyModelMother.getTestEntity(model);
        PhpClassFrameTargetCreator uut = new PhpClassFrameTargetCreator(entity);
        String code = new CodeTargetCodeBlockAdapter(uut.createPhoClassTarget())
                .withMarkers()
                .withCommentPrefix("//")
                .toCode();
        Assertions.assertThat(code)
                .contains("<?php")
                .contains("namespace App\\Entity;")
                .contains("class Test")
                .contains("public function __construct() {")
                ;
    }

    @Test
    void testAttributeGeneration() {
        OOModel model = SymfonyModelMother.createDefaultModel();
        MClass entity = SymfonyModelMother.getTestEntity(model);
        PhpPlainClassCreator uut = new PhpPlainClassCreator(entity);
        String code = new CodeTargetCodeBlockAdapter(uut.getCodeTarget())
                .withMarkers()
                .withCommentPrefix("//")
                .toCode();
        Assertions.assertThat(code)
                .contains("private String $title;")
                .contains("public function getTitle() {")
                .contains("   return $this->title;")
                .contains("public function setTitle( $value ) {")
                .contains("   $this->title = $value;")

                .contains("private Collection $results;")
                .contains("public function addToResults( $value ) {")
                .contains("  $this->results[] = $value;")
                .contains("public function removeFromResults( $value ) {")
                .contains("  unset($this->results[$key]);")
                .contains("  $this->results = array_values($this->results);")
                ;
    }

}
