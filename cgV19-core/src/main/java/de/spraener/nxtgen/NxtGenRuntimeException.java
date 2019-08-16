package de.spraener.nxtgen;

public class NxtGenRuntimeException extends RuntimeException {

    public NxtGenRuntimeException() {
        super();
    }

    public NxtGenRuntimeException(Throwable t) {
        super(t);
    }

    public NxtGenRuntimeException(String msg, Throwable t) {
        super(msg, t);
    }

    public NxtGenRuntimeException(String msg) {
        super(msg);
    }
}
