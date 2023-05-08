package de.csp.fsm;

public class FSMRunnerException extends RuntimeException {

    public FSMRunnerException(String s, String... args) {
        super(String.format(s, args));
    }
}
