package de.csp.fsm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FSMTransition {
    String target();
    String guard();
}
