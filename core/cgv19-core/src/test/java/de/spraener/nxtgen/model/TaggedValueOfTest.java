package de.spraener.nxtgen.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaggedValueOfTest {

    @Test
    void ofWithString() {
        TaggedValue tv = TaggedValue.of("k", "v");

        assertThat(tv.getName()).isEqualTo("k");
        assertThat(tv.getValue()).isEqualTo("v");
    }

    @Test
    void ofWithBoolean() {
        assertThat(TaggedValue.of("k", true).getValue()).isEqualTo("true");
    }

    @Test
    void ofWithInteger() {
        assertThat(TaggedValue.of("k", 42).getValue()).isEqualTo("42");
    }

    @Test
    void ofWithNull() {
        assertThat(TaggedValue.of("k", null).getValue()).isNull();
    }
}
